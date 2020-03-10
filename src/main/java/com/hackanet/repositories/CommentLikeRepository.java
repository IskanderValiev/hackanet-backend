package com.hackanet.repositories;

import com.hackanet.models.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/9/20
 */
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    List<CommentLike> findByCommentId(Long commentId);

    CommentLike findByCommentIdAndUserId(Long commentId, Long userId);
}