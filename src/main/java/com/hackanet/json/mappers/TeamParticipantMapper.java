package com.hackanet.json.mappers;

import com.hackanet.json.dto.TeamParticipantDto;
import com.hackanet.models.FileInfo;
import com.hackanet.models.Skill;
import com.hackanet.models.team.Team;
import com.hackanet.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/1/19
 */
@Component
public class TeamParticipantMapper {

    @Autowired
    private SkillMapper skillMapper;
    @Autowired
    private FileInfoMapper fileInfoMapper;

    public TeamParticipantDto map(User from, Team team) {
        FileInfo image = from.getImage();
        List<Skill> skills = from.getSkills();
        TeamParticipantDto build = TeamParticipantDto.builder()
                .id(from.getId())
                .name(from.getName())
                .lastName(from.getLastname())
                .isTeamLeader(team.getTeamLeader().getId().equals(from.getId()))
                .build();

        if (image != null) {
            build.setImage(fileInfoMapper.map(image));
        }
        if (skills != null) {
            build.setSkills(skillMapper.map(skills));
        }
        return build;
    }

    public List<TeamParticipantDto> map(Collection<User> participants, Team team) {
        return participants.stream()
                .map(participant -> map(participant, team))
                .collect(Collectors.toList());
    }
}
