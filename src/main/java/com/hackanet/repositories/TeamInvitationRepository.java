package com.hackanet.repositories;

import com.hackanet.models.TeamInvitation;
import com.hackanet.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamInvitationRepository extends JpaRepository<TeamInvitation, Long> {
    TeamInvitation getByUserIdAndTeamId(Long userId, Long teamId);
    List<TeamInvitation> getByUser(User user);
}
