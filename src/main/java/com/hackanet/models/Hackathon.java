package com.hackanet.models;

import com.neovisionaries.i18n.CountryCode;
import lombok.*;

import javax.persistence.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/18/19
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "hackathons")
public class Hackathon extends AbstractEntity {
    @Column(nullable = false)
    private String name;
    @Column(name = "name_lc")
    private String nameLc;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "start_date", nullable = false)
    private Date startDate;
    @Column(name = "end_date", nullable = false)
    private Date endDate;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "hackathon")
    private List<FileInfo> fileInfo;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "logo_id")
    private FileInfo logo;
    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;
    @Column(nullable = false)
    private String country;
    @Column(nullable = false)
    private String city;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "hackathon_skills_table", joinColumns = @JoinColumn(name = "hackathon_id"))
    private List<Skill> requiredSkills;

    public List<FileInfo> getFileInfo() {
        if (fileInfo == null)
            fileInfo = new ArrayList<>();
        return fileInfo;
    }
}