package com.hackanet.services.chat;

import com.hackanet.json.forms.ChatMessageSaveForm;
import com.hackanet.models.chat.ChatMessage;

@Deprecated
public interface ChatMessageService {
    ChatMessage saveMessage(ChatMessageSaveForm form);
    ChatMessage saveMessage(ChatMessage chatMessage);
}
