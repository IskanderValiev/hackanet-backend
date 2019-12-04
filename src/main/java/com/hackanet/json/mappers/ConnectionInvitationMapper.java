package com.hackanet.json.mappers;

import com.hackanet.json.dto.ConnectionInvitationDto;
import com.hackanet.models.ConnectionInvitation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 12/3/19
 */
@Component
public class ConnectionInvitationMapper implements Mapper<ConnectionInvitation, ConnectionInvitationDto> {

    @Autowired
    private UserSimpleMapper userSimpleMapper;

    @Override
    public ConnectionInvitationDto map(ConnectionInvitation from) {
        return ConnectionInvitationDto.builder()
                .id(from.getId())
                .invitedUser(userSimpleMapper.map(from.getInvitedUser()))
                .user(userSimpleMapper.map(from.getUser()))
                .status(from.getStatus())
                .build();
    }
}
