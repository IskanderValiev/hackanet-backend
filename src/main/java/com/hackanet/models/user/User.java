package com.hackanet.models.user;

import com.hackanet.models.AbstractEntity;
import com.hackanet.models.BlockedUser;
import com.hackanet.models.FileInfo;
import com.hackanet.models.chat.Chat;
import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.skill.Skill;
import com.hackanet.models.team.TeamMember;
import com.hackanet.security.enums.Role;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/20/19
 */
@EqualsAndHashCode(of = "email", callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "users")
public class User extends AbstractEntity {

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "lastname", length = 100)
    private String lastname;

    @Column(name = "nickname", length = 100, unique = true)
    private String nickname;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "hashed_password", nullable = false)
    private String hashedPassword;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<FileInfo> files;

    @OneToMany(mappedBy = "owner")
    private List<Hackathon> hackathons;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "picture_id", columnDefinition = "bigint DEFAULT 1")
    private FileInfo picture;

    @Column(name = "about", length = 1024)
    private String about;

    @Column(name = "country", length = 50)
    private String country;

    @Column(name = "city", length = 50)
    private String city;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_skill_table", joinColumns = @JoinColumn(name = "user_id"))
    private List<Skill> skills;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "hackathon_participants_table",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "hackathon_id"))
    private List<Hackathon> attendedHackathons;

    @ManyToMany(mappedBy = "admins")
    private List<Chat> chatsOwner;

    private Boolean lookingForTeam;

    @Column(name = "access_token_param", nullable = false, unique = true)
    private String accessTokenParam;

    @Column(name = "refresh_token_param", nullable = false, unique = true)
    private String refreshTokenParam;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "connections",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "connection_id"))
    private Set<User> connections;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "users_blacklist",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "blocked_user_id"))
    private List<User> blockedUsers;

    @Column(name = "last_request_time", nullable = false)
    private LocalDateTime lastRequestTime;

    @Column(name = "email_confirmed", nullable = false)
    private Boolean emailConfirmed;

    @Column(name = "email_confirmation_code", nullable = false, unique = true)
    private String emailConfirmationCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private Position position;

    @Column(name = "university", length = 100)
    private String university;

    @OneToOne(mappedBy = "user")
    private BlockedUser blockedUser;

    @OneToMany(mappedBy = "user")
    private List<TeamMember> members;

    public List<FileInfo> getFiles() {
        return files == null ? new ArrayList<>() : files;
    }

    public List<Hackathon> getHackathons() {
        return hackathons == null ? new ArrayList<>() : hackathons;
    }

    public List<Skill> getSkills() {
        return skills == null ? new ArrayList<>() : skills;
    }

    public List<Hackathon> getAttendedHackathons() {
        return attendedHackathons == null ? new ArrayList<>() : attendedHackathons;
    }

    public List<Chat> getChatsOwner() {
        return chatsOwner == null ? new ArrayList<>() : chatsOwner;
    }

    public Set<User> getConnections() {
        return connections == null ? new HashSet<>() : connections;
    }

    public List<User> getBlockedUsers() {
        return blockedUsers == null ? new ArrayList<>() : blockedUsers;
    }
}
