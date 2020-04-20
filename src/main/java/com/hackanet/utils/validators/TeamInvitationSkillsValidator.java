package com.hackanet.utils.validators;

import com.google.common.base.Preconditions;
import com.hackanet.models.enums.TeamInvitationStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 4/20/20
 */
@Service
public class TeamInvitationSkillsValidator {

    public void validate(TeamInvitationStatus status, List<Long> skillsIds) {
        if (TeamInvitationStatus.ACCEPTED.equals(status)) {
            Preconditions.checkArgument(skillsIds != null && !skillsIds.isEmpty(), "Skills must not be null if status is ACCEPTED");
        }
    }
}
