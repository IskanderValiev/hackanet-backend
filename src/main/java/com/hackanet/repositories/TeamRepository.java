package com.hackanet.repositories;

import com.hackanet.models.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findAllByHackathonId(Long hackathonId);
    @Query(nativeQuery = true, value = "select * from team t inner join team_participants tp on tp.user_id=:userId where t.hackathon_id=:hackathonId")
    Team findByHackathonIdAndUserId(@Param("userId") Long userId, @Param("hackathonId") Long hackathonId);
}
