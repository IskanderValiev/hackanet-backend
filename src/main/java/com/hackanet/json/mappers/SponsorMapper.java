package com.hackanet.json.mappers;

import com.hackanet.json.dto.SponsorDto;
import com.hackanet.models.hackathon.Sponsor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/24/20
 */
@Component
public class SponsorMapper implements Mapper<Sponsor, SponsorDto> {

    @Autowired
    private FileInfoMapper fileInfoMapper;

    @Override
    public SponsorDto map(Sponsor from) {
        if (from == null) {
            return null;
        }
        return SponsorDto.builder()
                .id(from.getId())
                .name(from.getName())
                .logo(fileInfoMapper.map(from.getLogo()))
                .link(from.getLink())
                .build();
    }
}
