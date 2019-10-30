package com.hackanet.services.chat;

import com.hackanet.json.forms.ChatMessageSaveForm;
import com.hackanet.models.chat.ChatMessage;

public interface ChatMessageService {
    ChatMessage saveMessage(ChatMessageSaveForm form);
    ChatMessage saveMessage(ChatMessage chatMessage);
}
