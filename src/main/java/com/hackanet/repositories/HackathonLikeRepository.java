package com.hackanet.repositories;

import com.hackanet.models.hackathon.HackathonLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HackathonLikeRepository extends JpaRepository<HackathonLike, Long> {
    Optional<HackathonLike> findByUserIdAndHackathonId(Long userId, Long hackathonId);
    Long countAllByHackathonId(Long hackathonId);
}
