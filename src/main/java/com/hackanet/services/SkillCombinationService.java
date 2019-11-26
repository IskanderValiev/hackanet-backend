package com.hackanet.services;

import com.hackanet.models.Skill;
import com.hackanet.models.Team;
import com.hackanet.models.User;

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
}
