package com.hackanet.models.team;

import com.hackanet.models.AbstractEntity;
import com.hackanet.models.Skill;
import com.hackanet.models.User;
import lombok.*;

import javax.persistence.*;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 2/18/20
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "team_members")
public class TeamMember extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "team_members_skills", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private List<Skill> skills;

    @Column(columnDefinition = "bool DEFAULT false")
    private Boolean skillsUpdated;
}
