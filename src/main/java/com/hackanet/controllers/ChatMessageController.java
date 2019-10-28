package com.hackanet.controllers;

import com.hackanet.json.dto.ChatMessageDto;
import com.hackanet.json.forms.ChatMessageSaveForm;
import com.hackanet.json.mappers.Mapper;
import com.hackanet.models.chat.ChatMessage;
import com.hackanet.models.chat.Message;
import com.hackanet.services.chat.ChatMessageService;
import com.hackanet.services.chat.ChatMessageServiceElasticsearchImpl;
import com.hackanet.services.chat.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/24/19
 */
@Controller
public class ChatMessageController {

    @Autowired
    private ChatService chatService;
    @Autowired
    private ChatMessageService chatMessageService;
    @Autowired
    private Mapper<ChatMessage, ChatMessageDto> chatMessageMapper;
    @Autowired
    private ChatMessageServiceElasticsearchImpl messageService;

    /**
     *
     * MessageMapping annotation means that any request with url of /chat/{id} will be routed
     * and handled by this method
     *
     * SendTo annotation means that the object returned by the method will be sent to the mentioned url
     * */
    @MessageMapping("/chat/{id}")
    @SendTo("/chat/{id}")
    public ChatMessageDto sendMessage(@Payload ChatMessageSaveForm form,
                               @DestinationVariable Long id) {
        form.setChatId(id);
        ChatMessage chatMessage = chatMessageService.saveMessage(form);
        return chatMessageMapper.map(chatMessage);
//        return messageService.saveMessage(form);
    }
}
