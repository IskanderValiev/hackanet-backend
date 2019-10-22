package com.hackanet.services;

import com.hackanet.exceptions.NotFoundException;
import com.hackanet.json.forms.PostCreateForm;
import com.hackanet.json.forms.PostUpdateForm;
import com.hackanet.models.Hackathon;
import com.hackanet.models.Post;
import com.hackanet.models.User;
import com.hackanet.repositories.PostRepository;
import com.hackanet.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

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

    @Override
    public Post add(PostCreateForm form, User user) {
        Post post = Post.builder()
                .title(form.getTitle())
                .content(form.getContent())
                .owner(user)
                .date(new Date(System.currentTimeMillis()))
                .build();
        if (form.getHackathon() != null) {
            Hackathon hackathon = hackathonService.get(form.getHackathon());
            SecurityUtils.checkHackathonAccess(hackathon, user);
            post.setHackathon(hackathon);
        }
        if (form.getImages() != null && !form.getImages().isEmpty())
            post.setImages(fileInfoService.getByIdsIn(form.getImages()));

        post = postRepository.save(post);
        return post;
    }

    @Override
    public Post update(Long id, User user, PostUpdateForm form) {
        Post post = get(id);
        SecurityUtils.checkPostAccess(post, user);
        post.setTitle(form.getTitle());
        post.setContent(form.getContent());
        if (form.getHackathon() != null) {
            post.setHackathon(hackathonService.get(form.getHackathon()));
        }
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
    public void delete(Long id, User user) {
        Post post = get(id);
        SecurityUtils.checkPostAccess(post, user);
        postRepository.delete(post);
    }
}
