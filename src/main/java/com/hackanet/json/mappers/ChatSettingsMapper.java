package com.hackanet.json.mappers;

import com.hackanet.json.dto.ChatSettingsDto;
import com.hackanet.models.chat.ChatSettings;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/11/19
 */
@Component
public class ChatSettingsMapper implements Mapper<ChatSettings, ChatSettingsDto> {
    @Override
    public ChatSettingsDto map(ChatSettings from) {
        return ChatSettingsDto.builder()
                .id(from.getId())
                .chatId(from.getChat().getId())
                .userId(from.getUser().getId())
                .muted(from.getMuted())
                .build();
    }
}
