package com.hackanet.services;

import com.hackanet.exceptions.BadRequestException;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.models.Post;
import com.hackanet.models.PostLike;
import com.hackanet.models.User;
import com.hackanet.repositories.PostLikeRepository;
import com.hackanet.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/2/19
 */
@Service
public class PostLikeServiceImpl implements PostLikeService {

    @Autowired
    private PostLikeRepository postLikeRepository;
    @Autowired
    private PostService postService;

    private PostLike get(Long id) {
        return postLikeRepository.findById(id).orElseThrow(() -> new NotFoundException("Post like with id=" + id + " not found"));
    }

    @Override
    public PostLike like(Long postId, User user) {
        Boolean exists = postLikeRepository.existsByPostIdAndUserId(postId, user.getId());
        if (!exists) {
            Post post = postService.get(postId);

            PostLike postLike = PostLike.builder()
                    .post(post)
                    .user(user)
                    .build();

            postLike = postLikeRepository.save(postLike);
            return postLike;
        } else throw new BadRequestException("You have already liked this post");
    }

    @Override
    public Long getCountOfPostLikes(Long postId) {
        return postLikeRepository.countAllByPostId(postId);
    }

    @Override
    public void unlike(Long postId, User user) {
        PostLike postLike = get(postId);
        SecurityUtils.checkPostLikeAccess(postLike, user);
        postLikeRepository.delete(postLike);
    }
}
