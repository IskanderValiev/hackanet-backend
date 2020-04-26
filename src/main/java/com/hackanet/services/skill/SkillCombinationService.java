package com.hackanet.services.skill;

import com.hackanet.models.skill.Skill;
import com.hackanet.models.team.Team;
import com.hackanet.models.user.User;
import com.hackanet.models.team.TeamMember;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/24/19
 */
public interface SkillCombinationService {
    void createByTeam(Team team);
    void updateIfUserJoinedToTeam(User user, Team team);
    List<Skill> mostRelevantSkills(User user);
    List<Skill> mostRelevantSkills(Team team);
    void recalculate(TeamMember teamMember, boolean userDeleted);
    void reset(TeamMember teamMember);
}
