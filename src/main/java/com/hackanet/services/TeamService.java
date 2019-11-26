package com.hackanet.services;

import com.hackanet.json.forms.TeamCreateForm;
import com.hackanet.json.forms.TeamSearchForm;
import com.hackanet.json.forms.TeamUpdateForm;
import com.hackanet.models.Hackathon;
import com.hackanet.models.Team;
import com.hackanet.models.User;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/31/19
 */
public interface TeamService extends CrudService<Team> {
    Team save(Team team);
    List<Team> save(List<Team> teams);
    Team createTeam(User user, TeamCreateForm form);
    Team updateTeam(Long id, User user, TeamUpdateForm form);
    List<Team> getByHackathon(Long hackathonId);
    List<Team> teamList(TeamSearchForm form);
    Team getByHackathonIdAndUserId(Long hackathonId, Long userId);
    Team addUser(Team team, User user);
    List<Team> getByHackathonStartTime(LocalDate startTime);
    void throwExceptionIfTeamIsNotActual(Team team);
    List<Team> getTeamsSuggestion(User user);
    List<Team> getTeamsSuggestion(User user, Long hackathonId);
}
