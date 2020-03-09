package com.hackanet.services.team;

import com.hackanet.models.User;
import com.hackanet.models.enums.TeamInvitationStatus;
import com.hackanet.models.team.Team;
import com.hackanet.models.team.TeamInvitation;
import com.hackanet.services.CrudService;

import java.util.List;
import java.util.Set;

public interface TeamInvitationService extends CrudService<TeamInvitation> {
    TeamInvitation createIfNotExists(User currentUser, Long userId, Long teamId);
    List<TeamInvitation> getAllByUser(User user);
    void delete(User user, Long id);
    TeamInvitation changeStatus(User user, Long invitationId, TeamInvitationStatus status);
    TeamInvitation getInfoSecurely(User user, Long id);
    void sendInvitations(Set<User> participants, User user, Team team);
}
