package com.hackanet.services;

import com.hackanet.models.Post;
import com.hackanet.models.PostView;
import com.hackanet.models.User;

public interface PostViewService {
    PostView addView(User user, Post post);
    Long countOfUniqueViews(Long postId);
}
