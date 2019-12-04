package com.hackanet.repositories;

import com.hackanet.models.ConnectionInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ConnectionInvitationRepository extends JpaRepository<ConnectionInvitation, Long> {
    Set<ConnectionInvitation> findByInvitedUserId(Long id);
    ConnectionInvitation findByUserIdAndInvitedUserId(Long userId, Long invitationId);
}
