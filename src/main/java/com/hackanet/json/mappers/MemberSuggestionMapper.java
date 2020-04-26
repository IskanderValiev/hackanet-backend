package com.hackanet.json.mappers;

import com.hackanet.json.dto.MemberSuggestionDto;
import com.hackanet.models.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 4/26/20
 */
@Component
public class MemberSuggestionMapper implements Mapper<User, MemberSuggestionDto> {

    @Autowired
    private PositionMapper positionMapper;

    @Autowired
    private SkillMapper skillMapper;

    @Override
    public MemberSuggestionDto map(User from) {
        if (from == null) {
            return null;
        }
        MemberSuggestionDto dto = MemberSuggestionDto.builder()
                .id(from.getId())
                .name(from.getName())
                .lastname(from.getLastname())
                .position(positionMapper.map(from.getPosition()))
                .skills(skillMapper.map(from.getSkills()))
                .build();
        return dto;
    }
}
