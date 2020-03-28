package com.hackanet.repositories;

import com.hackanet.models.hackathon.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Subscription findByUserIdAndHackathonId(Long userId, Long hackathonId);
    List<Subscription> findByUserId(Long userId);
    List<Subscription> findByHackathonId(Long hackathonId);
}
