package com.hackanet.services.post;

import com.hackanet.application.AppConstants;
import com.hackanet.exceptions.CensorViolationException;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.json.forms.PostCreateForm;
import com.hackanet.json.forms.PostSearchForm;
import com.hackanet.json.forms.PostUpdateForm;
import com.hackanet.models.enums.PostImportance;
import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.post.Post;
import com.hackanet.models.post.PostLike;
import com.hackanet.models.user.User;
import com.hackanet.repositories.post.PostRepository;
import com.hackanet.security.utils.SecurityUtils;
import com.hackanet.services.FileInfoService;
import com.hackanet.services.hackathon.HackathonService;
import com.hackanet.services.scheduler.JobRunner;
import com.hackanet.services.user.UserService;
import com.hackanet.utils.PostKeyWordsFeignClient;
import com.hackanet.utils.SwearWordsFilter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/22/19
 */
@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private HackathonService hackathonService;

    @Autowired
    private FileInfoService fileInfoService;

    @Autowired
    private UserService userService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JobRunner jobRunner;

    @Autowired
    private PostKeyWordsFeignClient postKeyWordsFeignClient;

    @Override
    public Post add(PostCreateForm form, User user) {
        final Boolean containsBadWords = SwearWordsFilter.containsBadWords(form.getContent());
        if (containsBadWords) {
            throw new CensorViolationException("The post's content has bad words.");
        }
        Post post = Post.builder()
                .title(form.getTitle())
                .content(form.getContent())
                .owner(user)
                .date(LocalDateTime.now())
                .build();
        if (form.getHackathon() != null) {
            Hackathon hackathon = hackathonService.get(form.getHackathon());
            SecurityUtils.checkHackathonAccess(hackathon, user);
            post.setHackathon(hackathon);
        }
        if (form.getImages() != null && !form.getImages().isEmpty()) {
            post.setImages(fileInfoService.getByIdsIn(form.getImages()));
        }
        if (Boolean.TRUE.equals(form.getSendImportanceRequest())) {
            post.setImportance(PostImportance.WAITING);
        } else {
            post.setImportance(PostImportance.NOT_IMPORTANT);
        }
        final String content = form.getContent().replaceAll("\"", "\\\"");
        final Set<String> keyWords = postKeyWordsFeignClient.getKeyWords(content);
        post.setKeywords(keyWords);
        post = postRepository.save(post);
        jobRunner.addNewPostNotification(null, post);
        return post;
    }

    @Override
    public Post update(Long id, User user, PostUpdateForm form) {
        Post post = get(id);
        SecurityUtils.checkPostAccess(post, user);
        post.setTitle(form.getTitle());
        post.setContent(form.getContent());
        post.setPicture(fileInfoService.get(form.getPictureId()));
        if (form.getImages() != null && !form.getImages().isEmpty()) {
            post.setImages(fileInfoService.getByIdsIn(form.getImages()));
        }
        post = postRepository.save(post);
        return post;
    }

    @Override
    public List<Post> getByHackathon(Long hackathonId) {
        Hackathon hackathon = hackathonService.get(hackathonId);
        return postRepository.findAllByHackathonOrderByDate(hackathon);
    }

    @Override
    public List<Post> getByUser(Long userId) {
        User user = userService.get(userId);
        return postRepository.findAllByOwnerOrderByDate(user);
    }

    @Override
    public Post get(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new NotFoundException("Post with id=" + id + " not found"));
    }

    @Override
    public List<Post> getAll() {
        return postRepository.findAll();
    }

    @Override
    public void delete(Long id, User user) {
        Post post = get(id);
        SecurityUtils.checkPostAccess(post, user);
        postRepository.delete(post);
    }

    @Override
    public Post changePostImportance(Long id, PostImportance importance) {
        Post post = get(id);
        post.setImportance(importance);
        post = postRepository.save(post);
        return post;
    }

    @Override
    public List<Post> getByImportance(PostImportance importance) {
        return postRepository.findAllByImportance(importance);
    }

    @Override
    public List<Post> postList(PostSearchForm form) {
        if (form.getLimit() == null) {
            form.setLimit(AppConstants.DEFAULT_LIMIT);
        }
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> postsListQuery = getPostsListQuery(criteriaBuilder, form);
        TypedQuery<Post> query = entityManager.createQuery(postsListQuery);
        if (form.getPage() != null) {
            query.setFirstResult((form.getLimit() - 1) * form.getLimit());
        } else {
            form.setPage(1);
        }
        query.setMaxResults(form.getLimit());
        return query.getResultList();
    }

    @Override
    public List<Post> getLikedPostsForUser(Long userId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> query = criteriaBuilder.createQuery(Post.class);
        Root<Post> root = query.from(Post.class);
        query.select(root);
        List<Predicate> predicates = new ArrayList<>();
        Join<Post, PostLike> join = root.join("likes");
        join.on(criteriaBuilder.equal(root.get("id"), userId));
        predicates.add(join.getOn());
        query.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        TypedQuery<Post> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }

    private CriteriaQuery<Post> getPostsListQuery(CriteriaBuilder criteriaBuilder, PostSearchForm form) {
        CriteriaQuery<Post> query = criteriaBuilder.createQuery(Post.class);
        Root<Post> root = query.from(Post.class);
        query.select(root);
        List<Predicate> predicates = new ArrayList<>();
        String title = form.getTitle();
        if (!StringUtils.isBlank(title)) {
            title = title.toLowerCase();
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title + "%"));
        }
        if (form.getHackathonId() == null) {
            Join<Post, Hackathon> join = root.join("hackathon", JoinType.INNER);
            join.on(criteriaBuilder.equal(join.get("id"), form.getHackathonId()));
            predicates.add(join.getOn());
        }
        query.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        return query;
    }

}
