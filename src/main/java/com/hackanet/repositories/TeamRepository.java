package com.hackanet.repositories;

import com.hackanet.models.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findAllByHackathonId(Long hackathonId);
}
