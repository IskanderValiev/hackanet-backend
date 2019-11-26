package com.hackanet.services;

import com.hackanet.models.TeamInvitation;
import com.hackanet.models.User;
import com.hackanet.models.enums.TeamInvitationStatus;

import java.util.List;

public interface TeamInvitationService extends CrudService<TeamInvitation> {
    TeamInvitation createIfNotExists(User currentUser, Long userId, Long teamId);
    List<TeamInvitation> getAllByUser(User user);
    void delete(User user, Long id);
    TeamInvitation changeStatus(User user, Long invitationId, TeamInvitationStatus status);
    TeamInvitation getInfoSecurely(User user, Long id);
}
