package com.hackanet.models;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/12/19
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user_tokens")
public class UserToken extends AbstractEntity {
    @OneToOne(fetch = FetchType.LAZY)
    private User user;
    @Column(nullable = false)
    private LocalDateTime accessTokenExpiresAt;
    @Column(nullable = false)
    private LocalDateTime refreshTokenExpiresAt;
    @Column(nullable = false, unique = true, length = 500)
    private String refreshToken;
}
