package com.hackanet.services;

import com.hackanet.exceptions.NotFoundException;
import com.hackanet.models.ConnectionInvitation;
import com.hackanet.models.enums.ConnectionInvitationStatus;
import com.hackanet.models.user.User;
import com.hackanet.repositories.ConnectionInvitationRepository;
import com.hackanet.services.scheduler.JobRunner;
import com.hackanet.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.hackanet.security.utils.SecurityUtils.checkConnectionInvitationAccess;
import static com.hackanet.security.utils.SecurityUtils.containedInBlackList;

/**
 * @author Iskander Valiev
 * created by isko
 * on 12/2/19
 */
@Service
public class ConnectionInvitationServiceImpl implements ConnectionInvitationService {

    @Autowired
    private ConnectionInvitationRepository connectionInvitationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private JobRunner jobRunner;

    @Override
    public ConnectionInvitation sendInvitation(User user, Long invitedUserId) {
        User invitedUser = userService.get(invitedUserId);
        containedInBlackList(invitedUser, user);
        ConnectionInvitation invitation = connectionInvitationRepository.findByUserIdAndInvitedUserId(user.getId(), invitedUserId);
        if (invitation != null) {
            return invitation;
        }
        invitation = ConnectionInvitation.builder()
                .user(user)
                .invitedUser(invitedUser)
                .status(ConnectionInvitationStatus.NEW)
                .build();
        return connectionInvitationRepository.save(invitation);
    }

    @Override
    @Transactional
    public ConnectionInvitation changeStatus(User user, Long invitationId, ConnectionInvitationStatus status) {
        ConnectionInvitation invitation = get(invitationId);
        checkConnectionInvitationAccess(invitation, user, false);

        if (ConnectionInvitationStatus.ACCEPTED.equals(status)) {
            connectionService.addConnection(invitation.getUser(), invitation.getInvitedUser());
            jobRunner.addConnectionInvitationNotification(null, invitation);
        } else if (ConnectionInvitationStatus.REJECTED.equals(status)) {
            connectionService.deleteConnection(invitation.getUser(), invitation.getInvitedUser());
        }
        invitation.setStatus(status);
        return connectionInvitationRepository.save(invitation);
    }

    @Override
    public Set<ConnectionInvitation> getByInvitedUser(Long invitedUser) {
        return connectionInvitationRepository.findByInvitedUserId(invitedUser);
    }

    @Override
    public void delete(Long userId, Long connectionId) {
        ConnectionInvitation invitation = connectionInvitationRepository.findByUserIdAndInvitedUserId(userId, connectionId);
        if (invitation == null) {
            invitation = connectionInvitationRepository.findByUserIdAndInvitedUserId(connectionId, userId);
        }
        connectionInvitationRepository.delete(invitation);
    }

    @Override
    public ConnectionInvitation get(Long id) {
        return connectionInvitationRepository.findById(id).orElseThrow(() -> new NotFoundException("Connection Invitation with id = " + id + " not found."));
    }
}
