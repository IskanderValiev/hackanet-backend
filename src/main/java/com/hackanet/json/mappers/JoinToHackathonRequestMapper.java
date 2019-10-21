package com.hackanet.json.mappers;

import com.hackanet.json.dto.HackathonDto;
import com.hackanet.json.dto.JoinToHackathonRequestDto;
import com.hackanet.json.dto.UserDto;
import com.hackanet.models.Hackathon;
import com.hackanet.models.JoinToHackathonRequest;
import com.hackanet.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/21/19
 */
@Component("requestMapper")
public class JoinToHackathonRequestMapper implements Mapper<JoinToHackathonRequest, JoinToHackathonRequestDto> {

    @Autowired
    @Qualifier("hackathonMapper")
    private Mapper<Hackathon, HackathonDto> mapper;

    @Autowired
    @Qualifier("userMapper")
    private Mapper<User, UserDto> userMapper;

    @Override
    public JoinToHackathonRequestDto map(JoinToHackathonRequest from) {
        return JoinToHackathonRequestDto.builder()
                .id(from.getId())
                .hackathon(mapper.map(from.getHackathon()))
                .user(userMapper.map(from.getUser()))
                .message(from.getMessage())
                .date(from.getDate())
                .build();
    }
}
