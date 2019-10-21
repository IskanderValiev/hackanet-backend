package com.hackanet.json.mappers;

import com.hackanet.json.dto.FileInfoDto;
import com.hackanet.json.dto.UserDto;
import com.hackanet.models.FileInfo;
import com.hackanet.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/20/19
 */
@Component
public class UserMapper implements Mapper<User, UserDto> {

    @Autowired
    private Mapper<FileInfo, FileInfoDto> mapper;

    @Override
    public UserDto map(User from) {
        UserDto user = UserDto.builder()
                .id(from.getId())
                .email(from.getEmail())
                .phone(from.getPhone())
                .name(from.getName())
                .lastname(from.getLastname())
                .country(from.getCountry())
                .city(from.getCity())
                .build();
        if (from.getImage() != null)
            user.setImage(mapper.map(from.getImage()));
        return user;
    }
}
