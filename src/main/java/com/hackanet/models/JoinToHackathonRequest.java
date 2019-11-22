package com.hackanet.models;

import com.hackanet.models.enums.JoinType;
import com.hackanet.models.enums.RequestStatus;
import lombok.*;

import javax.persistence.*;
import java.sql.Date;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/21/19
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "join_to_hackathon_requests")
public class JoinToHackathonRequest extends AbstractEntity {
    @Column(nullable = false)
    private Long entityId;
    @Enumerated(EnumType.STRING)
    private JoinType joinType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hackathon_id")
    private Hackathon hackathon;
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
    @Column(nullable = false)
    private Date date;
    private String message;
}
