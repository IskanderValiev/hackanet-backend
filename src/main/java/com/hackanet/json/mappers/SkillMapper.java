package com.hackanet.json.mappers;

import com.hackanet.json.dto.SkillDto;
import com.hackanet.models.Skill;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/20/19
 */
@Component("skillMapper")
public class SkillMapper implements Mapper<Skill, SkillDto> {

    @Override
    public SkillDto map(Skill from) {
        return SkillDto.builder()
                .id(from.getId())
                .name(from.getName())
                .build();
    }
}
