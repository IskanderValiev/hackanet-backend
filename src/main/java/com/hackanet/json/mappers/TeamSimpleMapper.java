package com.hackanet.json.mappers;

import com.hackanet.json.dto.TeamSimpleDto;
import com.hackanet.models.Team;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/21/19
 */
@Component
public class TeamSimpleMapper implements Mapper<Team, TeamSimpleDto> {
    @Override
    public TeamSimpleDto map(Team from) {
        if (from == null) {
            return null;
        }
        return TeamSimpleDto.builder()
                .id(from.getId())
                .name(from.getName())
                .build();
    }
}
