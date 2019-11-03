package com.hackanet.repositories;

import com.hackanet.models.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Long countAllByPostId(Long postId);
    Boolean existsByPostIdAndUserId(Long postId, Long userId);
}
