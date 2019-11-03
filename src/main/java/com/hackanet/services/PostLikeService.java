package com.hackanet.services;

import com.hackanet.models.PostLike;
import com.hackanet.models.User;

public interface PostLikeService {
    PostLike like(Long postId, User user);
    Long getCountOfPostLikes(Long postId);
    void unlike(Long postId, User user);
}
