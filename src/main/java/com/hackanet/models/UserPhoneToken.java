package com.hackanet.models;

import com.hackanet.push.enums.ClientType;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/30/19
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserPhoneToken extends AbstractEntity {
    @Column(nullable = false)
    private Long userId;
    @Enumerated(EnumType.STRING)
    private ClientType deviceType;
    @Column(nullable = false, length = 500)
    private String token;
    @Column(nullable = false, length = 500)
    private String deviceId;
}
