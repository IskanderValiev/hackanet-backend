package com.hackanet.models;

import com.hackanet.models.chat.Chat;
import com.hackanet.models.enums.TeamType;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/31/19
 */
@ToString
@EqualsAndHashCode(callSuper = true)
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Team extends AbstractEntity {

    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "team_participants",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> participants;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hackathon_id")
    private Hackathon hackathon;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    private User teamLeader;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "teams_skills_looking_for")
    private List<Skill> skillsLookingFor;

    @Enumerated(EnumType.STRING)
    private TeamType teamType;
}
