package com.hackanet.models;

import com.hackanet.models.skill.Skill;
import com.hackanet.models.user.Portfolio;
import lombok.*;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/8/19
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "job_experience")
public class JobExperience extends AbstractEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(nullable = false)
    private Date startDate;

    @Column(nullable = false)
    private Date endDate;

    @Column(nullable = false)
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "job_experience_technologies",
            joinColumns = @JoinColumn(name = "job_experience_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id"))
    List<Skill> technologiesUsed;

    @ManyToOne(fetch = FetchType.LAZY)
    private Portfolio portfolio;
}
