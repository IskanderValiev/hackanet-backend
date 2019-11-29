package com.hackanet.services.chat;

import com.hackanet.json.forms.ChatMessageSaveForm;
import com.hackanet.models.chat.Chat;
import com.hackanet.models.chat.ChatMessage;
import com.hackanet.repositories.chat.ChatMessageRepository;
import com.hackanet.services.UserService;
import com.hackanet.utils.SwearWordsFilter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/24/19
 */
@Deprecated
@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private ChatService chatService;
    @Autowired
    private UserService userService;


    /*
    *
    * The Transactional annotation is required here because
    * before get entities from other tables the transaction is closed
    *
    * */
    @Override
    @Transactional
    public ChatMessage saveMessage(@NotNull ChatMessageSaveForm form) {
        String text = form.getText();
        text = SwearWordsFilter.filterText(text.trim());
        ChatMessage chatMessage = ChatMessage.builder()
                .chat(chatService.get(form.getChatId()))
                .messageTime(new Timestamp(System.currentTimeMillis()))
                .text(text)
                .sender(userService.get(form.getSenderId()))
                .build();
        chatMessage = chatMessageRepository.save(chatMessage);
        return chatMessage;
    }

    @Override
    public ChatMessage saveMessage(ChatMessage chatMessage) {
        return null;
    }
}
