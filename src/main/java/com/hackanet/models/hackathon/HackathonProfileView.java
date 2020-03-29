package com.hackanet.models.hackathon;

import com.hackanet.models.AbstractEntity;
import com.hackanet.models.User;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/3/19
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class HackathonProfileView extends AbstractEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    private Hackathon hackathon;
    private Timestamp timestamp;
}
