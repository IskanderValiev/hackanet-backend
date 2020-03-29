package com.hackanet.models.team;

import com.hackanet.models.AbstractEntity;
import com.hackanet.models.Hackathon;
import com.hackanet.models.Skill;
import com.hackanet.models.User;
import com.hackanet.models.chat.Chat;
import com.hackanet.models.enums.TeamType;
import com.hackanet.models.hackathon.Hackathon;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    private Boolean actual;

    private Boolean lookingForHackers;

    public List<User> getParticipants() {
        return participants == null ? new ArrayList<>() : participants;
    }

    public Boolean getActual() {
        return Boolean.TRUE.equals(this.actual);
    }

    public Boolean getLookingForHackers() {
        return Boolean.TRUE.equals(this.lookingForHackers);
    }
}
