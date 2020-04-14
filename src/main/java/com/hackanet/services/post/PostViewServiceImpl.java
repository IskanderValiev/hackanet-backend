package com.hackanet.services.post;

import com.hackanet.models.post.Post;
import com.hackanet.models.post.PostView;
import com.hackanet.models.user.User;
import com.hackanet.repositories.post.PostViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/3/19
 */
@Service
public class PostViewServiceImpl implements PostViewService {

    @Autowired
    private PostViewRepository postViewRepository;

    @Override
    public PostView addView(User user, Post post) {
        PostView postView = PostView.builder()
                .post(post)
                .user(user)
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .build();
        return postViewRepository.save(postView);
    }

    @Override
    public Long countOfUniqueViews(Long postId) {
        return postViewRepository.countOfUniqueViews(postId);
    }
}
