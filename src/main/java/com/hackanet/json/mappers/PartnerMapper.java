package com.hackanet.json.mappers;

import com.hackanet.json.dto.PartnerDto;
import com.hackanet.models.Partner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/27/19
 */
@Component
public class PartnerMapper implements Mapper<Partner, PartnerDto> {

    @Autowired
    private FileInfoMapper fileInfoMapper;

    @Override
    public PartnerDto map(Partner from) {
        return PartnerDto.builder()
                .id(from.getId())
                .link(from.getLink())
                .logo(fileInfoMapper.map(from.getLogo()))
                .build();
    }
}
