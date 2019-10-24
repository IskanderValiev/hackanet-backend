package com.hackanet.json.mappers;

import com.hackanet.json.dto.*;
import com.hackanet.models.FileInfo;
import com.hackanet.models.Hackathon;
import com.hackanet.models.Skill;
import com.hackanet.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/19/19
 */
@Component("hackathonMapper")
public class HackathonMapper implements Mapper<Hackathon, HackathonDto> {

    @Autowired
    @Qualifier("fileInfoMapper")
    private Mapper<FileInfo, FileInfoDto> fileMapper;

    @Autowired
    @Qualifier("userMapper")
    private Mapper<User, UserDto> userMapper;

    @Autowired
    @Qualifier("userSimpleMapper")
    private Mapper<User, UserSimpleDto> userSimpleMapper;

    @Autowired
    @Qualifier("skillMapper")
    private Mapper<Skill, SkillDto> skillMapper;

    @Override
    public HackathonDto map(Hackathon from) {
        HackathonDto hackathon = HackathonDto.builder()
                .id(from.getId())
                .name(from.getName())
                .description(from.getDescription())
                .start(from.getStartDate())
                .end(from.getEndDate())
                .owner(userMapper.map(from.getOwner()))
                .country(from.getCountry())
                .currency(from.getCurrency().toString())
                .prizeFund(from.getPrize())
                .city(from.getCity())
                .build();
        if (hackathon.getLogo() != null)
            hackathon.setLogo(fileMapper.map(from.getLogo()));
        List<Skill> requiredSkills = from.getRequiredSkills();
        if (requiredSkills != null)
            hackathon.setRequiredSkills(skillMapper.map(requiredSkills));
        if (from.getParticipants() != null) {
            hackathon.setParticipants(userSimpleMapper.map(from.getParticipants()));
        }
        return hackathon;
    }

    @Override
    public List<HackathonDto> map(List<com.hackanet.models.Hackathon> fromList) {
        return fromList.stream().map(this::map).collect(Collectors.toList());
    }
}
