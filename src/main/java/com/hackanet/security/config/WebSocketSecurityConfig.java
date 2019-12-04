package com.hackanet.security.config;

import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/26/19
 */
@Component
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages.simpTypeMatchers(
                SimpMessageType.CONNECT,
                SimpMessageType.DISCONNECT).permitAll()
                .simpTypeMatchers(
                SimpMessageType.SUBSCRIBE,
                SimpMessageType.MESSAGE).permitAll();
    }
}
