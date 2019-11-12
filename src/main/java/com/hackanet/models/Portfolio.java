package com.hackanet.models;

import lombok.*;

import javax.persistence.*;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/5/19
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "portfolios")
public class Portfolio extends AbstractEntity {
    @OneToOne(fetch = FetchType.LAZY)
    private User user;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "portfolio")
    private List<JobExperience> jobExperience;
    @OneToMany(mappedBy = "portfolio")
    private List<HackathonJobDescription> hackathonJobDescriptions;
    private Boolean visibleForCompanies = Boolean.TRUE;
}
