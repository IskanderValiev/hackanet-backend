package com.hackanet.services;

import com.hackanet.models.Post;
import com.hackanet.models.PostView;
import com.hackanet.models.User;
import com.hackanet.repositories.PostViewRepository;
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

        postView = postViewRepository.save(postView);
        return postView;
    }

    @Override
    public Long countOfUniqueViews(Long postId) {
        return postViewRepository.countOfUniqueViews(postId);
    }
}
