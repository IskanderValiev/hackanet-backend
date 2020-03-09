package com.hackanet.models;

import com.hackanet.models.team.Team;
import lombok.*;

import javax.persistence.*;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/7/19
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "hackathon_job_descriptions")
public class HackathonJobDescription extends AbstractEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hackathon_id")
    private Hackathon hackathon;
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;
}
