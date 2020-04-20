package com.hackanet.services.team;

import com.hackanet.models.skill.Skill;
import com.hackanet.models.user.User;
import com.hackanet.models.team.Team;
import com.hackanet.models.team.TeamMember;

import java.util.List;

public interface TeamMemberService {
    void addTeamMember(User user, Team team, List<Skill> skills);
    void addTeamMember(User user, Team team);
    void deleteTeamMember(TeamMember teamMember);
    void deleteTeamMember(Long teamId, Long userId);
    TeamMember updateSkills(Long id, List<Long> skillsIds, User user);
    TeamMember get(Long id);
    List<Team> getTeams(Long userId);
    List<TeamMember> getMembers(Long teamId);
    TeamMember getMemberByUserIdAndTeamId(Long userId, Long teamId);
    boolean exists(Long userId, Long teamId);
}
