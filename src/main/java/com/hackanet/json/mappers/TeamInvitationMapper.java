package com.hackanet.json.mappers;

import com.hackanet.json.dto.TeamInvitationDto;
import com.hackanet.models.team.TeamInvitation;
import com.hackanet.utils.DateTimeUtil;
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
        if (from == null) {
            return null;
        }
        return TeamInvitationDto.builder()
                .id(from.getId())
                .datetime(DateTimeUtil.localDateTimeToLong(from.getTime()))
                .team(teamSimpleMapper.map(from.getTeam()))
                .user(userSimpleMapper.map(from.getUser()))
                .status(from.getStatus())
                .build();
    }
}
