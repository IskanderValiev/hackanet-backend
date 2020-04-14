package com.hackanet.json.mappers;

import com.hackanet.json.dto.OrganizerDto;
import com.hackanet.models.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/21/20
 */
@Component
public class OrganizerMapper implements Mapper<User, OrganizerDto> {

    @Autowired
    private FileInfoMapper fileInfoMapper;

    @Override
    public OrganizerDto map(User from) {
        if (from == null) {
            return null;
        }
        return OrganizerDto.builder()
                .name(from.getName())
                .about(from.getAbout())
                .picture(fileInfoMapper.map(from.getPicture()))
                .build();
    }
}
