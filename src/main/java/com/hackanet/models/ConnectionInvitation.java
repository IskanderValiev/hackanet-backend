package com.hackanet.models;

import com.hackanet.models.enums.ConnectionInvitationStatus;
import lombok.*;

import javax.persistence.*;

/**
 * @author Iskander Valiev
 * created by isko
 * on 12/2/19
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "connection_invitations", indexes = {
        @Index(name = "ci_index", columnList = "user_id, invited_user_id")
})
public class ConnectionInvitation extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private User invitedUser;

    @Enumerated(EnumType.STRING)
    private ConnectionInvitationStatus status;
}
