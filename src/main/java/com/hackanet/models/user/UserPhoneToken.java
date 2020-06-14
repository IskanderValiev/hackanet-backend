package com.hackanet.models.user;

import com.hackanet.models.AbstractEntity;
import com.hackanet.push.enums.ClientType;
import lombok.*;

import javax.persistence.*;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/30/19
 */
@EqualsAndHashCode
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(indexes = {
        @Index(
                columnList = "user_id" + ", "
                        + "deviceType" + ", "
                        + "deviceId", unique = true)})
public class UserPhoneToken extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private ClientType deviceType;

    @Column(nullable = false, length = 500)
    private String token;

    @Column(nullable = false, length = 500)
    private String deviceId;
}
