package com.hackanet.json.mappers;

import com.hackanet.json.dto.FileInfoDto;
import com.hackanet.json.dto.UserSimpleDto;
import com.hackanet.models.FileInfo;
import com.hackanet.models.user.User;
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
        if (from == null) {
            return null;
        }
        UserSimpleDto userDto = UserSimpleDto.builder()
                .id(from.getId())
                .name(from.getName())
                .lastname(from.getLastname())
                .nickname(from.getNickname())
                .picture(mapper.map(from.getPicture()))
                .build();
        return userDto;
    }
}
