package com.hackanet.services;

import com.hackanet.models.ConnectionInvitation;
import com.hackanet.models.user.User;
import com.hackanet.models.enums.ConnectionInvitationStatus;

import java.util.Set;

/**
 * @author Iskander Valiev
 * created by isko
 * on 12/2/19
 */
public interface ConnectionInvitationService extends RetrieveService<ConnectionInvitation> {
    ConnectionInvitation sendInvitation(User user, Long invitedUser);
    ConnectionInvitation changeStatus(User user, Long invitationId, ConnectionInvitationStatus status);
    Set<ConnectionInvitation> getByInvitedUser(Long invitedUser);
    void delete(Long userId, Long connectionId);
}
