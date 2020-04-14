package com.hackanet.json.mappers;

import com.hackanet.json.dto.ChatUserDto;
import com.hackanet.models.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/23/20
 */
@Component
public class ChatUserMapper implements Mapper<User, ChatUserDto> {

    @Autowired
    private FileInfoMapper fileInfoMapper;


    @Override
    public ChatUserDto map(User from) {
        return null;
    }
}
