package com.hackanet.json.mappers;

import com.hackanet.json.dto.TeamDto;
import com.hackanet.models.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/1/19
 */
@Component
public class TeamMapper implements Mapper<Team, TeamDto> {

    @Autowired
    private TeamParticipantMapper teamParticipantMapper;
    @Autowired
    private SkillMapper skillMapper;

    @Override
    public TeamDto map(Team from) {
        return TeamDto.builder()
                .id(from.getId())
                .name(from.getName())
                .participants(teamParticipantMapper.map(from.getParticipants(), from))
                .skillsLookingFor(skillMapper.map(from.getSkillsLookingFor()))
                .build();
    }
}
