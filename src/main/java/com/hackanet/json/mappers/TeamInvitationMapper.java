package com.hackanet.json.mappers;

import com.hackanet.json.dto.TeamInvitationDto;
import com.hackanet.json.dto.TeamSimpleDto;
import com.hackanet.models.TeamInvitation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/25/19
 */
@Component
public class TeamInvitationMapper implements Mapper<TeamInvitation, TeamInvitationDto> {

    @Autowired
    private UserSimpleMapper userSimpleMapper;

    @Autowired
    private TeamSimpleMapper teamSimpleMapper;

    @Override
    public TeamInvitationDto map(TeamInvitation from) {
        return TeamInvitationDto.builder()
                .id(from.getId())
                .localDateTime(from.getTime())
                .teamSimpleDto(teamSimpleMapper.map(from.getTeam()))
                .userSimpleDto(userSimpleMapper.map(from.getUser()))
                .status(from.getStatus())
                .build();
    }
}
