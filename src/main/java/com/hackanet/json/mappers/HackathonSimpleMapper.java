package com.hackanet.json.mappers;

import com.hackanet.json.dto.HackathonSimpleDto;
import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.services.HackathonLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/21/20
 */
@Component
public class HackathonSimpleMapper implements Mapper<Hackathon, HackathonSimpleDto> {

    @Autowired
    private FileInfoMapper fileInfoMapper;

    @Autowired
    private HackathonLikeService hackathonLikeService;

    @Override
    public HackathonSimpleDto map(Hackathon from) {
        if (from == null) {
            return null;
        }
        return HackathonSimpleDto.builder()
                .id(from.getId())
                .start(from.getStartDate().getTime())
                .end(from.getEndDate().getTime())
                .picture(fileInfoMapper.map(from.getLogo()))
                .name(from.getName())
                .likesCount(hackathonLikeService.countByHackathonId(from.getId()))
                .build();
    }
}
