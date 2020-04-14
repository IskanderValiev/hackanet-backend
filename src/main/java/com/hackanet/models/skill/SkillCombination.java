package com.hackanet.models.skill;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
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
@Document(collection = "skill_combinations")
public class SkillCombination {
    @Id
    private String id;
    private Long skillId;
    private Long skillUsedWith;
    private Long countOfCombination = 0L;

    public Long getCountOfCombination() {
        return countOfCombination == null ? 0 : countOfCombination;
    }
}
