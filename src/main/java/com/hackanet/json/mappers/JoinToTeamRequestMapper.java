package com.hackanet.json.mappers;

import com.hackanet.json.dto.JoinToTeamRequestDto;
import com.hackanet.models.JoinToTeamRequest;
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
    private UserSimpleMapper userSimpleMapper;

    @Override
    public JoinToTeamRequestDto map(JoinToTeamRequest from) {
        if (from == null) {
            return null;
        }
        return JoinToTeamRequestDto.builder()
                .id(from.getId())
                .status(from.getRequestStatus())
                .teamId(from.getTeam().getId())
                .user(userSimpleMapper.map(from.getUser()))
                .message(from.getMessage())
                .build();
    }
}
