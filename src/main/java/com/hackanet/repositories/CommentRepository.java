package com.hackanet.repositories;

import com.hackanet.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/9/20
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostId(Long postId);
}
