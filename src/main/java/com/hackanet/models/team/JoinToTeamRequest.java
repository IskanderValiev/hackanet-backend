package com.hackanet.models.team;

import com.hackanet.models.AbstractEntity;
import com.hackanet.models.User;
import com.hackanet.models.enums.JoinToTeamRequestStatus;
import com.hackanet.models.team.Team;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/1/19
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class JoinToTeamRequest extends AbstractEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Team team;
    @Enumerated(EnumType.STRING)
    private JoinToTeamRequestStatus requestStatus;
    private Timestamp timestamp;
    private String message;
}
