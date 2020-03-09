package com.hackanet.repositories;

import com.hackanet.models.team.JoinToTeamRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JoinToTeamRequestRepository extends JpaRepository<JoinToTeamRequest, Long> {
    JoinToTeamRequest findByUserIdAndTeamId(Long userId, Long teamId);
    List<JoinToTeamRequest> findAllByTeamId(Long teamId);
}
