package com.hackanet.controllers;

import com.hackanet.json.dto.MessageDto;
import com.hackanet.json.forms.ChatMessageSaveForm;
import com.hackanet.json.mappers.MessageMapper;
import com.hackanet.models.User;
import com.hackanet.models.chat.Chat;
import com.hackanet.models.chat.Message;
import com.hackanet.security.utils.SimpMessageHeaderAccessorUtils;
import com.hackanet.services.chat.ChatMessageServiceElasticsearchImpl;
import com.hackanet.services.chat.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.List;

import static com.hackanet.security.utils.SecurityUtils.checkChatAccess;

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
    @Autowired
    private ChatService chatService;
    @Autowired
    private SimpMessageHeaderAccessorUtils simpMessageHeaderAccessorUtils;

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
                                  @DestinationVariable Long id,
                                  SimpMessageHeaderAccessor accessor) {
        chatService.checkChatAccessByChatId(id, accessor);
        form.setChatId(id);
        Message message = messageService.saveMessage(form);
        return messageMapper.map(message);
    }

    @MessageMapping("/chat/{id}/connect")
    @SendTo("/chat/{id}")
    public List<MessageDto> getMessages(@DestinationVariable Long id,
                                        @Payload String connect,
                                        SimpMessageHeaderAccessor accessor) {
        chatService.checkChatAccessByChatId(id, accessor);
        List<Message> messages = messageService.getByChatId(id);
        return messageMapper.map(messages);
    }
}
