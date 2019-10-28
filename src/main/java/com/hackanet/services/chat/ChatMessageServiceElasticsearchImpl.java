package com.hackanet.services.chat;

import com.hackanet.json.forms.ChatMessageSaveForm;
import com.hackanet.models.User;
import com.hackanet.models.chat.Chat;
import com.hackanet.models.chat.ChatMessage;
import com.hackanet.models.chat.Message;
import com.hackanet.repositories.chat.MessageRepository;
import com.hackanet.services.UserService;
import com.hackanet.utils.SwearWordsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/28/19
 */
@Component("chatMessageServiceElasticsearchImpl")
public class ChatMessageServiceElasticsearchImpl {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ChatService chatService;
    @Autowired
    private UserService userService;

    public Message saveMessage(ChatMessageSaveForm form) {
        chatService.get(form.getChatId());
        userService.get(form.getSenderId());
        String text = form.getText();
        text = SwearWordsFilter.filterText(text.trim());
        Message chatMessage = Message.builder()
                .chatId(form.getChatId())
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .text(text)
                .senderId(form.getChatId())
                .build();
        chatMessage = messageRepository.save(chatMessage);
        return chatMessage;
    }
}
