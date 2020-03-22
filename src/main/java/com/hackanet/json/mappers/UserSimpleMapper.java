package com.hackanet.json.mappers;

import com.hackanet.json.dto.FileInfoDto;
import com.hackanet.json.dto.UserSimpleDto;
import com.hackanet.models.FileInfo;
import com.hackanet.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/21/19
 */
@Component
public class UserSimpleMapper implements Mapper<User, UserSimpleDto> {

    @Autowired
    private Mapper<FileInfo, FileInfoDto> mapper;

    @Override
    public UserSimpleDto map(User from) {
        UserSimpleDto userDto = UserSimpleDto.builder()
                .id(from.getId())
                .name(from.getName())
                .lastname(from.getLastname())
                .build();
        if (from.getPicture() != null)
            userDto.setImage(mapper.map(from.getPicture()));
        return userDto;
    }
}
