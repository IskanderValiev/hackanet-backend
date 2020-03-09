package com.hackanet.json.mappers;

import com.hackanet.json.dto.JoinToTeamRequestDto;
import com.hackanet.models.team.JoinToTeamRequest;
import com.hackanet.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/1/19
 */
@Component
public class JoinToTeamRequestMapper implements Mapper<JoinToTeamRequest, JoinToTeamRequestDto> {

    @Autowired
    private UserService userService;
    @Autowired
    private UserSimpleMapper userSimpleMapper;

    @Override
    public JoinToTeamRequestDto map(JoinToTeamRequest from) {
        return JoinToTeamRequestDto.builder()
                .id(from.getId())
                .status(from.getRequestStatus())
                .teamId(from.getTeam().getId())
                .user(userSimpleMapper.map(from.getUser()))
                .message(from.getMessage())
                .build();
    }
}
