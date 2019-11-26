package com.hackanet.scheduler;

import com.hackanet.models.Team;
import com.hackanet.services.HackathonService;
import com.hackanet.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/25/19
 */
@Component
public class TeamActualStatusScheduler {

    @Autowired
    private TeamService teamService;

    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Moscow")
    public void setTeamActualStatusToFalse() {
        List<Team> teams = teamService.getByHackathonStartTime(LocalDate.now());
        teams.forEach(team -> {
            team.setActual(false);
        });
        teamService.save(teams);
    }
}
