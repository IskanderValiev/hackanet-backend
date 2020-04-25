package com.hackanet.json.mappers;

import com.hackanet.json.dto.TeamDto;
import com.hackanet.models.team.Team;
import com.hackanet.models.team.TeamMember;
import com.hackanet.models.user.User;
import com.hackanet.services.team.TeamMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private TeamMemberService teamMemberService;

    @Override
    public TeamDto map(Team from) {
        if (from == null) {
            return null;
        }
        final List<User> members = teamMemberService.getMembers(from.getId()).stream()
                .map(TeamMember::getUser)
                .collect(Collectors.toList());
        return TeamDto.builder()
                .id(from.getId())
                .name(from.getName())
                .members(teamMemberMapper.map(members, from))
                .skillsLookingFor(skillMapper.map(from.getSkillsLookingFor()))
                .teamType(from.getTeamType())
                .lookingForHackers(Boolean.TRUE.equals(from.getLookingForHackers()))
                .relevant(Boolean.TRUE.equals(from.getRelevant()))
                .build();
    }
}
