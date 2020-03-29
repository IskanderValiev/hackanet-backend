package com.hackanet.models.hackathon;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hackanet.models.*;
import com.hackanet.models.chat.Chat;
import com.hackanet.models.enums.Currency;
import lombok.*;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/18/19
 */
@ToString
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

    @Column(name = "description", nullable = false, length = 1024)
    private String description;

    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Column(name = "end_date", nullable = false)
    private Date endDate;

    //    @Column(nullable = false)
    private Integer prize;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "hackathon")
    private List<FileInfo> fileInfo;

    @OneToOne(fetch = FetchType.LAZY)
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

    @ManyToMany(mappedBy = "attendedHackathons")
    private Set<User> participants;

    @OneToMany(mappedBy = "hackathon")
    private List<Chat> chats;

    @OneToMany(mappedBy = "hackathon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Track> tracks;

    @OneToMany(mappedBy = "hackathon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sponsor> sponsors;

    private Double longitude;
    private Double latitude;
    private LocalDateTime registrationStartDate;
    private LocalDateTime registrationEndDate;

    private Boolean deleted;

    private Boolean approved;

    public List<FileInfo> getFileInfo() {
        return fileInfo == null ? Lists.newArrayList() : fileInfo;
    }

    public List<Chat> getChats() {
        return chats == null ? Lists.newArrayList() : chats;
    }

    public Set<User> getParticipants() {
        return participants == null ? Sets.newHashSet() : participants;
    }

    public Boolean getDeleted() {
        return Boolean.TRUE.equals(this.deleted);
    }

    public List<Track> getTracks() {
        return tracks == null ? Lists.newArrayList() : tracks;
    }
}
