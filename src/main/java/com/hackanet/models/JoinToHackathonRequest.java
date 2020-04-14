package com.hackanet.models;

import com.hackanet.models.enums.JoinType;
import com.hackanet.models.enums.RequestStatus;
import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.hackathon.Track;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Enumerated(EnumType.STRING)
    private JoinType joinType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hackathon_id")
    private Hackathon hackathon;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @OneToOne(fetch = FetchType.LAZY)
    private Track mainTrack;

    @OneToOne(fetch = FetchType.LAZY)
    private Track subTrack;
}
