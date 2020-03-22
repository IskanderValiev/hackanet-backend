package com.hackanet.repositories;

import com.hackanet.models.Hackathon;
import com.hackanet.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HackathonRepository extends JpaRepository<Hackathon, Long> {
    List<Hackathon> findByParticipantsContaining(User user);
    Hackathon findByOwnerId(Long userId);
    List<Hackathon> findAllByDeletedIsFalseAndApprovedIsTrue();
}
