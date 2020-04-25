package com.hackanet.models.mappers;

import com.hackanet.models.enums.TeamType;
import com.hackanet.models.team.Team;
import com.hackanet.services.chat.ChatService;
import com.hackanet.services.hackathon.HackathonService;
import com.hackanet.services.team.TeamMemberService;
import com.hackanet.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Iskander Valiev
 * created by isko
 * on 4/25/20
 */
@Component
public class TeamRowMapper implements RowMapper<Team> {

    @Autowired
    private UserService userService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private HackathonService hackathonService;

    @Autowired
    private TeamMemberService teamMemberService;

    @Override
    public Team mapRow(ResultSet resultSet, int i) throws SQLException {
        final Team team = Team.builder()
                .lookingForHackers(resultSet.getBoolean("looking_for_hackers"))
                .name(resultSet.getString("name"))
                .relevant(resultSet.getBoolean("relevant"))
                .chat(chatService.get(resultSet.getLong("chat_id")))
                .teamType(TeamType.valueOf(resultSet.getString("team_type")))
                .teamLeader(userService.get(resultSet.getLong("team_leader_id")))
                .build();
        if (resultSet.getLong("hackathon_id") != 0) {
            team.setHackathon(hackathonService.get(resultSet.getLong("hackathon_id")));
        }
        team.setId(resultSet.getLong("id"));
        team.setMembers(teamMemberService.getMembers(team.getId()));
        return team;
    }
}
