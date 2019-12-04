package com.hackanet.services;

import com.hackanet.exceptions.NotFoundException;
import com.hackanet.models.ConnectionInvitation;
import com.hackanet.models.User;
import com.hackanet.models.enums.ConnectionInvitationStatus;
import com.hackanet.repositories.ConnectionInvitationRepository;
import com.hackanet.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.hackanet.security.utils.SecurityUtils.*;

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

    @Override
    public ConnectionInvitation sendInvitation(User user, Long invitedUser) {
        ConnectionInvitation invitation = ConnectionInvitation.builder()
                .user(user)
                .invitedUser(userService.get(invitedUser))
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
            userService.addConnection(invitation.getUser(), invitation.getInvitedUser());
        } else if (ConnectionInvitationStatus.REJECTED.equals(status)) {
            userService.deleteConnection(invitation.getUser(), invitation.getInvitedUser());
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
