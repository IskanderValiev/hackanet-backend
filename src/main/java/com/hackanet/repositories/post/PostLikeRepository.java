package com.hackanet.repositories.post;

import com.hackanet.models.post.PostLike;
import com.hackanet.models.enums.LikeType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Long countAllByPostIdAndLikeType(Long postId, LikeType type);
    Boolean existsByPostIdAndUserId(Long postId, Long userId);
}
