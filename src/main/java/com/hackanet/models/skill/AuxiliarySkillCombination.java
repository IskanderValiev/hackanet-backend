package com.hackanet.models.skill;

import lombok.Builder;
import lombok.Data;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/25/19
 */
@Data
@Builder
public class AuxiliarySkillCombination {
    private Long skillId;
    private Long skillUsedWithId;
    private Double probability;
}
