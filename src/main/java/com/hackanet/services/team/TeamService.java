package com.hackanet.services.team;

import com.hackanet.json.forms.TeamCreateForm;
import com.hackanet.json.forms.TeamSearchForm;
import com.hackanet.json.forms.TeamUpdateForm;
import com.hackanet.models.team.Team;
import com.hackanet.models.user.User;
import com.hackanet.services.RetrieveService;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/31/19
 */
public interface TeamService extends RetrieveService<Team> {
    Team save(Team team);
    List<Team> save(List<Team> teams);
    Team createTeam(User user, TeamCreateForm form);
    Team updateTeam(Long id, User user, TeamUpdateForm form);
    List<Team> getByHackathon(Long hackathonId);
    List<Team> teamList(TeamSearchForm form);
    Team getByHackathonIdAndUserId(Long hackathonId, Long userId);
    List<Team> getByHackathonStartTime(LocalDate startTime);
    void checkRelevance(Team team);
    List<Team> getTeamsSuggestion(User user);
    List<Team> getTeamsSuggestion(User user, Long hackathonId);
    List<Team> getTeamsSuggestionUsingJDBC(User user, Long hackathonId);
    boolean teamContainsUser(Team team, Long userId);
    Team deleteMember(Long id, Long userId, User currentUser);
    List<Team> getTeamsByUser(long userId, Boolean relevance);
}
