package com.hackanet.services.skill;

import com.hackanet.models.team.TeamMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Iskander Valiev
 * created by isko
 * on 4/20/20
 */
@Service
public class SkillCombinationHelperServiceImpl implements SkillCombinationHelperService {

    @Autowired
    private SkillCombinationService skillCombinationService;

    @Override
    public void recalculate(TeamMember teamMember, boolean userDeleted) {
        skillCombinationService.recalculate(teamMember, userDeleted);
    }

    @Override
    public void reset(TeamMember teamMember) {
        skillCombinationService.reset(teamMember);
    }

    @Override
    public void update(TeamMember teamMember) {
        skillCombinationService.updateIfUserJoinedToTeam(teamMember.getUser(), teamMember.getTeam());
    }
}
