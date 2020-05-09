package com.hackanet.json.mappers;

import com.hackanet.json.dto.ChatDto;
import com.hackanet.json.dto.UserSimpleDto;
import com.hackanet.models.chat.Chat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/24/19
 */
@Component
public class ChatMapper implements Mapper<Chat, ChatDto> {

    @Autowired
    private UserSimpleMapper userSimpleMapper;

    @Override
    public ChatDto map(Chat from) {
        if (from == null) {
            return null;
        }
        List<UserSimpleDto> userSimpleDtos = userSimpleMapper.map(from.getParticipants());
        List<UserSimpleDto> adminDtos = userSimpleMapper.map(from.getAdmins());
        return ChatDto.builder()
                .id(from.getId())
                .participants(new HashSet<>(userSimpleDtos))
                .admin(new HashSet<>(adminDtos))
                .chatType(from.getType())
                .build();
    }
}
