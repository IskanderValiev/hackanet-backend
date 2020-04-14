package com.hackanet.services.comment;

import com.hackanet.models.comment.CommentLike;
import com.hackanet.models.user.User;
import com.hackanet.models.enums.LikeType;

import java.util.List;

public interface CommentLikeService {
    CommentLike create(User user, Long commentId, LikeType type);
    void delete(User user, Long likeId);
    CommentLike update(User user, Long commentId, LikeType type);
    List<CommentLike> getByCommentId(Long commentId);
}
