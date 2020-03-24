package com.hackanet.json.mappers;

import com.hackanet.json.dto.ChatMessageDto;
import com.hackanet.models.chat.ChatMessage;
import com.hackanet.utils.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/24/19
 */
@Component
public class ChatMessageMapper implements Mapper<ChatMessage, ChatMessageDto> {

    @Autowired
    private UserSimpleMapper userSimpleMapper;

    @Override
    public ChatMessageDto map(ChatMessage from) {
        if (from == null) {
            return null;
        }
        return ChatMessageDto.builder()
                .id(from.getId())
                .messageTime(from.getMessageTime().getTime())
                .sender(userSimpleMapper.map(from.getSender()))
                .text(from.getText())
                .build();
    }
}
