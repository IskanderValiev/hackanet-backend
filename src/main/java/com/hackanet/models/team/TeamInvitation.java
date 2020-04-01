package com.hackanet.models.team;

import com.hackanet.models.AbstractEntity;
import com.hackanet.models.User;
import com.hackanet.models.enums.TeamInvitationStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/24/19
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "team_invitations")
public class TeamInvitation extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    @Enumerated(EnumType.STRING)
    private TeamInvitationStatus status;

    private LocalDateTime time;
}
