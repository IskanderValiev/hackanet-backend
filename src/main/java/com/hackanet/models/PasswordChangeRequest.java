package com.hackanet.models;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/31/19
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
@Builder
@Entity
@Table(name = "password_requests")
public class PasswordChangeRequest extends AbstractEntity {
    @Column(unique = true)
    private String code;
    private Long userId;
    private Boolean used;
    private LocalDateTime createdDate;
}
