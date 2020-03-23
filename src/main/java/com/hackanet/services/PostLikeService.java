package com.hackanet.services;

import com.hackanet.models.PostLike;
import com.hackanet.models.User;
import com.hackanet.models.enums.LikeType;

public interface PostLikeService {
    PostLike like(Long postId, User user, LikeType type);
    Long getCountOfPostLikes(Long postId, LikeType type);
    void unlike(Long postId, User user);
}
