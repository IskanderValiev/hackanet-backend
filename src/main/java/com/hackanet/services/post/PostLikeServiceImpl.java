package com.hackanet.services.post;

import com.hackanet.exceptions.BadRequestException;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.models.post.Post;
import com.hackanet.models.post.PostLike;
import com.hackanet.models.user.User;
import com.hackanet.models.enums.LikeType;
import com.hackanet.repositories.post.PostLikeRepository;
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
    public PostLike like(Long postId, User user, LikeType type) {
        Boolean exists = postLikeRepository.existsByPostIdAndUserId(postId, user.getId());
        if (!exists) {
            Post post = postService.get(postId);

            PostLike postLike = PostLike.builder()
                    .post(post)
                    .user(user)
                    .likeType(type)
                    .build();

            return postLikeRepository.save(postLike);
        } else throw new BadRequestException("You have already liked this post");
    }

    @Override
    public Long getCountOfPostLikes(Long postId, LikeType type) {
        return postLikeRepository.countAllByPostIdAndLikeType(postId, type);
    }

    @Override
    public void unlike(Long postId, User user) {
        PostLike postLike = get(postId);
        SecurityUtils.checkPostLikeAccess(postLike, user);
        postLikeRepository.delete(postLike);
    }
}
