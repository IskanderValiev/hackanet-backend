package com.hackanet.models.skill;

import com.hackanet.models.AbstractEntity;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Set;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/24/19
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "skills_combinations")
public class SkillCombination extends AbstractEntity {

    @Column(name = "skill_id", nullable = false)
    private Long skillId;

    @Column(name = "skill_used_with", nullable = false)
    private Long skillUsedWith;

    @Column(name = "count_of_combinations", nullable = false)
    private Long countOfCombination = 0L;

    public Long getCountOfCombination() {
        return countOfCombination == null ? 0 : countOfCombination;
    }
}
