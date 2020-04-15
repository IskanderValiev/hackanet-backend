package com.hackanet.models;

import com.hackanet.models.enums.BlockInitiator;
import com.hackanet.models.enums.BlockReason;
import com.hackanet.models.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author Iskander Valiev
 * created by isko
 * on 4/13/20
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "blocked_users")
public class BlockedUser extends AbstractEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "block_reason", nullable = false)
    @Enumerated(EnumType.STRING)
    private BlockReason blockReason;

    @Column(name = "block_time", nullable = false)
    private LocalDateTime blockTime = LocalDateTime.now();

    @Column(name = "block_initiator", nullable = false)
    private BlockInitiator blockInitiator = BlockInitiator.SYSTEM;

    @Column(name = "message")
    private String message;

    @Column(name = "canceled", nullable = false, columnDefinition = " BOOLEAN default false")
    private Boolean canceled;
}
