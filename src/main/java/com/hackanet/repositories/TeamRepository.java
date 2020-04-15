package com.hackanet.repositories;

import com.hackanet.models.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findAllByHackathonId(Long hackathonId);

    @Query(nativeQuery = true, value = "select * from team t inner join team_participants tp on tp.user_id=:userId where t.hackathon_id=:hackathonId")
    Team findByHackathonIdAndUserId(@Param("userId") Long userId, @Param("hackathonId") Long hackathonId);

    @Query(nativeQuery = true, value = "select * from team t inner join hackathons h on t.hackathon_id = h.id where h.start_date=:startDate")
    List<Team> findTeamsByStartDateOfHackathon(@Param("startDate") LocalDate startDate);

    // TODO: 11/25/19 criteria api
    @Deprecated
    @Query(nativeQuery = true, value = "select distinct t.*" +
            " from team t" +
            " inner join team_participants tp on t.id = tp.team_id" +
            " inner join users u on tp.user_id = u.id" +
            " inner join user_skill_table ust on u.id = ust.user_id" +
            " where ust.skills_id in :skills and t.looking_for_hackers is true and t.relavant is true")
    List<Team> findBySkills(@Param("skills") List<Long> skills);

    List<Team> findAllByLookingForHackersAndRelevant(Boolean lookingForHackers, Boolean relevant);
}
