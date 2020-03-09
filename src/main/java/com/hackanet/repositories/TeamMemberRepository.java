package com.hackanet.repositories;

import com.hackanet.models.team.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    List<TeamMember> findAllByUserId(Long userId);
    List<TeamMember> findAllByTeamId(Long teamId);
    TeamMember findByUserIdAndTeamId(Long userId, Long teamId);
    Boolean existsByUserIdAndTeamId(Long userId, Long teamId);
}
