package com.hackanet.json.mappers;

import com.hackanet.json.dto.ChatDto;
import com.hackanet.models.chat.Chat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/24/19
 */
@Component
public class ChatMapper implements Mapper<Chat, ChatDto> {

    @Autowired
    private UserSimpleMapper userSimpleMapper;
    @Autowired
    private ChatMessageMapper messageMapper;

    @Override
    public ChatDto map(Chat from) {

        if (from.getMessages()==null) {
            from.setMessages(Collections.emptyList());
        }

        return ChatDto.builder()
                .id(from.getId())
                .participants(userSimpleMapper.map(from.getParticipants()))
                .messages(messageMapper.map(from.getMessages()))
                .admin(userSimpleMapper.map(from.getAdmin()))
                .chatType(from.getType())
                .build();
    }
}
