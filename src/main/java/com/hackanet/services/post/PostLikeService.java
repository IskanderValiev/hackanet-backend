package com.hackanet.services.post;

import com.hackanet.models.post.PostLike;
import com.hackanet.models.user.User;
import com.hackanet.models.enums.LikeType;

public interface PostLikeService {
    PostLike like(Long postId, User user, LikeType type);
    Long getCountOfPostLikes(Long postId, LikeType type);
    void unlike(Long postId, User user);
}
