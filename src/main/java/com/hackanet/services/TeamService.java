package com.hackanet.services;

import com.hackanet.json.forms.TeamCreateForm;
import com.hackanet.json.forms.TeamSearchForm;
import com.hackanet.json.forms.TeamUpdateForm;
import com.hackanet.models.Team;
import com.hackanet.models.User;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/31/19
 */
public interface TeamService extends CrudService<Team> {
    Team save(Team team);
    Team createTeam(TeamCreateForm form);
    Team updateTeam(Long id, User user, TeamUpdateForm form);
    List<Team> getByHackathon(Long hackathonId);
    List<Team> teamList(TeamSearchForm form);
}
