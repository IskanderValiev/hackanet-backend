package com.hackanet.models;

import lombok.*;

import javax.persistence.*;
import java.sql.Time;
import java.time.LocalTime;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/11/19
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user_notification_settings")
public class UserNotificationSettings extends AbstractEntity {

    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    private Boolean emailEnabled = true;

    private Boolean pushEnabled = true;

    private LocalTime dontDisturbFrom;

    private LocalTime dontDisturbTo;
}
