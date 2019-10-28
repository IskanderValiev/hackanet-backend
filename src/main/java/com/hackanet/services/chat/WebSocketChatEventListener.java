package com.hackanet.services.chat;

import com.hackanet.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/24/19
 */
@Component
@Slf4j
public class WebSocketChatEventListener {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private ChatMessageService chatMessageService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {}

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {}
}
