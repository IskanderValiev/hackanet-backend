package com.hackanet.models.team;

import com.google.common.collect.Lists;
import com.hackanet.models.AbstractEntity;
import com.hackanet.models.skill.Skill;
import com.hackanet.models.user.User;
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

    @OneToMany(mappedBy = "team", orphanRemoval = true)
    private List<TeamMember> members;

    @Column(name = "relevant", nullable = false)
    private Boolean relevant;

    @Column(name = "looking_for_hackers")
    private Boolean lookingForHackers;

    public List<TeamMember> getMembers() {
        return members == null ? Lists.newArrayList() : members;
    }

    public Boolean getRelevant() {
        return Boolean.TRUE.equals(this.relevant);
    }

    public Boolean getLookingForHackers() {
        return Boolean.TRUE.equals(this.lookingForHackers);
    }
}
