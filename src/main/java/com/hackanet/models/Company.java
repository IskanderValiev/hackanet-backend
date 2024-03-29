package com.hackanet.models;

import com.hackanet.models.enums.CompanyType;
import com.hackanet.models.skill.Skill;
import com.hackanet.models.user.User;
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
@Table(name = "companies")
public class Company extends AbstractEntity {

    @Column(nullable = false)
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    private FileInfo logo;

    @Column(name = "description", nullable = false, length = 1024)
    private String description;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @OneToOne(fetch = FetchType.LAZY)
    private User admin;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "companies_technologies",
            joinColumns = @JoinColumn(name = "company_id"),
            inverseJoinColumns = @JoinColumn(name = "technology_id"))
    private List<Skill> technologies;

    @Enumerated(EnumType.STRING)
    private CompanyType type = CompanyType.SOFTWARE_ENGINEERING;

    @Column(name = "approved")
    private Boolean approved = Boolean.FALSE;
}
