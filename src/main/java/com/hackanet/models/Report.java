package com.hackanet.models;

import com.hackanet.models.enums.ReportEntityType;
import com.hackanet.models.enums.ReportStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/10/20
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reports")
public class Report extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Enumerated(EnumType.STRING)
    private ReportEntityType type;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;
}
