package com.hackanet.json.mappers;

import com.hackanet.json.dto.HackathonDto;
import com.hackanet.json.dto.JoinToHackathonRequestDto;
import com.hackanet.json.dto.UserSimpleDto;
import com.hackanet.models.Hackathon;
import com.hackanet.models.JoinToHackathonRequest;
import com.hackanet.models.Team;
import com.hackanet.models.User;
import com.hackanet.models.enums.JoinType;
import com.hackanet.services.TeamService;
import com.hackanet.services.UserService;
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
    private HackathonMapper mapper;

    @Autowired
    @Qualifier("userSimpleMapper")
    private UserSimpleMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private TeamService teamService;

    @Override
    public JoinToHackathonRequestDto map(JoinToHackathonRequest from) {
        JoinToHackathonRequestDto build = JoinToHackathonRequestDto.builder()
                .id(from.getId())
                .hackathon(mapper.map(from.getHackathon()))
                .joinType(from.getJoinType())
                .message(from.getMessage())
                .date(from.getDate())
                .status(from.getStatus())
                .build();

        if (JoinType.ALONE.equals(from.getJoinType())) {
            User usr = userService.get(from.getEntityId());
            build.setEntity(userMapper.map(usr));
        } else {
            Team team = teamService.get(from.getEntityId());
            build.setEntity(teamMapper.map(team));
        }
        return build;
    }
}
