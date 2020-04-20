package com.hackanet.services.skill;

import com.hackanet.models.team.TeamMember;

public interface SkillCombinationHelperService {
    void recalculate(TeamMember teamMember, boolean userDeleted);
    void reset(TeamMember teamMember);
    void update(TeamMember teamMember);
}
