package com.hackanet.json.mappers;

import com.hackanet.json.dto.*;
import com.hackanet.models.*;
import com.hackanet.utils.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hackanet.utils.DateTimeUtil.localDateTimeToLong;

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

    @Autowired
    private PartnerMapper partnerMapper;

    @Override
    public HackathonDto map(Hackathon from) {
        HackathonDto hackathon = HackathonDto.builder()
                .id(from.getId())
                .name(from.getName())
                .description(from.getDescription())
                .start(from.getStartDate().getTime())
                .end(from.getEndDate().getTime())
                .owner(userMapper.map(from.getOwner()))
                .country(from.getCountry())
                .currency(from.getCurrency().toString())
                .prizeFund(from.getPrize())
                .city(from.getCity())
                .deleted(from.getDeleted())
                .longitude(from.getLongitude())
                .latitude(from.getLatitude())
                .registrationStartDate(localDateTimeToLong(from.getRegistrationStartDate()))
                .registrationEndDate(localDateTimeToLong(from.getRegistrationEndDate()))
                .build();
        if (hackathon.getLogo() != null)
            hackathon.setLogo(fileMapper.map(from.getLogo()));
        List<Skill> requiredSkills = from.getRequiredSkills();
        if (requiredSkills != null)
            hackathon.setRequiredSkills(skillMapper.map(requiredSkills));
        Set<User> participants = from.getParticipants();
        if (participants != null) {
            hackathon.setParticipants(userSimpleMapper.map(new ArrayList<>(participants)));
        }
        Set<Partner> partners = from.getPartners();
        if (partners != null) {
            hackathon.setPartners(partnerMapper.map(from.getPartners()));
        }
        return hackathon;
    }
}
