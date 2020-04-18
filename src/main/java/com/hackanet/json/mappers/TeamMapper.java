package com.hackanet.json.mappers;

import com.hackanet.json.dto.TeamDto;
import com.hackanet.models.team.Team;
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
    private TeamMemberMapper teamMemberMapper;
    @Autowired
    private SkillMapper skillMapper;

    @Override
    public TeamDto map(Team from) {
        if (from == null) {
            return null;
        }
        return TeamDto.builder()
                .id(from.getId())
                .name(from.getName())
                .members(teamMemberMapper.map(from.getParticipants(), from))
                .skillsLookingFor(skillMapper.map(from.getSkillsLookingFor()))
                .teamType(from.getTeamType())
                .lookingForHackers(Boolean.TRUE.equals(from.getLookingForHackers()))
                .actual(Boolean.TRUE.equals(from.getRelevant()))
                .build();
    }
}
