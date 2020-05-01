package com.hackanet.repositories.hackathon;

import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HackathonRepository extends JpaRepository<Hackathon, Long> {
    List<Hackathon> findByParticipantsContaining(User user);
    List<Hackathon> findByOwnerId(Long userId);
    List<Hackathon> findAllByDeletedIsFalseAndApprovedIsTrue();
}
