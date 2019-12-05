package com.hackanet.models;

import com.hackanet.models.chat.Chat;
import com.hackanet.security.enums.Role;
import lombok.*;

import javax.persistence.*;
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

    @Column(nullable = false)
    private String name;

    private String lastname;

    @Column(nullable = false)
    private String email;

    private String phone;

    @Column(name = "hashed_password")
    private String hashedPassword;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<FileInfo> files;

    @OneToMany(mappedBy = "owner")
    private List<Hackathon> hackathons;

    @OneToOne(fetch = FetchType.LAZY)
    private FileInfo image;

    @Column(length = 1024)
    private String about;
    private String country;
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

    @Column(unique = true)
    private String accessTokenParam;

    @Column(unique = true)
    private String refreshTokenParam;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "connections",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "connection_id"))
    private Set<User> connections;

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
}
