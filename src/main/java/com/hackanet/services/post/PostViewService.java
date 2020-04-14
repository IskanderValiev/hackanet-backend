package com.hackanet.services.post;

import com.hackanet.models.post.Post;
import com.hackanet.models.post.PostView;
import com.hackanet.models.user.User;

public interface PostViewService {
    PostView addView(User user, Post post);
    Long countOfUniqueViews(Long postId);
}
