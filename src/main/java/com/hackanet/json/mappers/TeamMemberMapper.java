package com.hackanet.json.mappers;

import com.hackanet.json.dto.TeamMemberDto;
import com.hackanet.models.FileInfo;
import com.hackanet.models.skill.Skill;
import com.hackanet.models.team.Team;
import com.hackanet.models.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/1/19
 */
@Component
public class TeamMemberMapper {

    @Autowired
    private SkillMapper skillMapper;

    @Autowired
    private FileInfoMapper fileInfoMapper;

    public TeamMemberDto map(User from, Team team) {
        FileInfo image = from.getPicture();
        List<Skill> skills = from.getSkills();
        TeamMemberDto build = TeamMemberDto.builder()
                .id(from.getId())
                .name(from.getName())
                .lastName(from.getLastname())
                .isTeamLeader(team.getTeamLeader().getId().equals(from.getId()))
                .skills(skillMapper.map(skills))
                .image(fileInfoMapper.map(image))
                .build();
        return build;
    }

    public List<TeamMemberDto> map(Collection<User> participants, Team team) {
        return participants.stream()
                .map(participant -> map(participant, team))
                .collect(Collectors.toList());
    }
}
