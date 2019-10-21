package com.hackanet.models;

import com.hackanet.security.role.Role;
import com.neovisionaries.i18n.CountryCode;
import lombok.*;

import javax.persistence.*;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/20/19
 */
@EqualsAndHashCode(callSuper = true)
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
    @Column(nullable = false)
    private String lastname;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false, name = "hashed_password")
    private String hashedPassword;
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<FileInfo> files;
    @OneToMany(mappedBy = "owner")
    private List<Hackathon> hackathons;
    @OneToOne(fetch = FetchType.LAZY)
    private FileInfo image;
    @Column(nullable = false)
    private String about;
    @Column(nullable = false)
    private String country;
    @Column(nullable = false)
    private String city;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_skill_table", joinColumns = @JoinColumn(name = "user_id"))
    private List<Skill> skills;
}
