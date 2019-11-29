package com.hackanet.models;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/28/19
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "job_offers")
public class JobOffer extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;

    private LocalDateTime time;

    private boolean deletedForUser;

    public boolean isDeletedForUser() {
        return Boolean.TRUE.equals(this.deletedForUser);
    }
}
