package com.hackanet.json.mappers;

import com.hackanet.json.dto.*;
import com.hackanet.models.FileInfo;
import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.Skill;
import com.hackanet.models.User;
import com.hackanet.services.HackathonLikeService;
import com.hackanet.services.HackathonProfileViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

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
    private OrganizerMapper organizerMapper;

    @Autowired
    @Qualifier("userSimpleMapper")
    private Mapper<User, UserSimpleDto> userSimpleMapper;

    @Autowired
    @Qualifier("skillMapper")
    private Mapper<Skill, SkillDto> skillMapper;

    @Autowired
    private TrackMapper trackMapper;

    @Autowired
    private SponsorMapper sponsorMapper;

    @Autowired
    private HackathonLikeService hackathonLikeService;

    @Autowired
    private HackathonProfileViewService hackathonProfileViewService;

    @Override
    public HackathonDto map(Hackathon from) {
        if (from == null) {
            return null;
        }
        HackathonDto hackathon = HackathonDto.builder()
                .id(from.getId())
                .name(from.getName())
                .description(from.getDescription())
                .start(from.getStartDate().getTime())
                .end(from.getEndDate().getTime())
                .organizer(organizerMapper.map(from.getOwner()))
                .country(from.getCountry())
                .currency(from.getCurrency().toString())
                .prizeFund(from.getPrizeFund())
                .city(from.getCity())
                .deleted(from.getDeleted())
                .longitude(from.getLongitude())
                .latitude(from.getLatitude())
                .tracks(trackMapper.map(from.getTracks()))
                .approved(Boolean.TRUE.equals(from.getApproved()))
                .sponsors(sponsorMapper.map(from.getSponsors()))
                .picture(fileMapper.map(from.getLogo()))
                .requiredSkills(skillMapper.map(from.getRequiredSkills()))
                .participants(userSimpleMapper.map(from.getParticipants()))
                .likesCount(hackathonLikeService.countByHackathonId(from.getId()))
                .viewsCount(hackathonProfileViewService.countByHackathonId(from.getId()))
                .build();
        if (from.getRegistrationStartDate() != null) {
            hackathon.setRegistrationStartDate(localDateTimeToLong(from.getRegistrationStartDate()));
        }
        if (from.getRegistrationEndDate() != null) {
            hackathon.setRegistrationEndDate(localDateTimeToLong(from.getRegistrationEndDate()));
        }
        return hackathon;
    }
}
