package com.hackanet.models;

import com.hackanet.models.chat.Chat;
import lombok.*;

import javax.persistence.*;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/31/19
 */
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
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id"))
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
}
