package com.hackanet.controllers;

import com.hackanet.json.dto.ChatUserDto;
import com.hackanet.json.dto.MessageDto;
import com.hackanet.json.forms.ChatMessageSaveForm;
import com.hackanet.json.forms.ChatUserTypingStatus;
import com.hackanet.json.mappers.MessageMapper;
import com.hackanet.models.chat.Message;
import com.hackanet.services.chat.ChatMessageServiceElasticsearchImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/24/19
 */
@Controller
public class ChatMessageController {

    private static final String CHAT_DESTINATION = "/chat";

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private ChatMessageServiceElasticsearchImpl messageService;

    /**
     *
     * MessageMapping annotation means that any request with url of /chat/{id} will be routed
     * and handled by this method
     *
     * SendTo annotation means that the object returned by the method will be sent to the mentioned url
     * */
    @MessageMapping("/chat/{id}/send")
    @SendTo("/chat/{id}")
    public MessageDto sendMessage(@Payload ChatMessageSaveForm form,
                                  @DestinationVariable Long id) {
        form.setChatId(id);
        Message message = messageService.saveMessage(form);
        return messageMapper.map(message);
    }

    @MessageMapping("/chat/{id}/connect")
    @SendTo("/chat/{id}")
    public List<MessageDto> getMessages(@DestinationVariable Long id,
                                     @Payload String connect) {
        List<Message> messages = messageService.getByChatId(id);
        return messageMapper.map(messages);
    }

    @MessageMapping("/chat/{id}/typing")
    @SendTo("/chat/{id}")
    public ChatUserDto typingStatus(@DestinationVariable Long id,
                                    @Payload ChatUserTypingStatus status) {
        return ChatUserDto.builder()
                .id(status.getUserId())
                .chatIsTypingIn(id)
                .isTyping(Boolean.TRUE.equals(status.getIsTyping()))
                .build();
    }
}
