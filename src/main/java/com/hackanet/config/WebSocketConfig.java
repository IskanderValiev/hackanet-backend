package com.hackanet.config;

import com.hackanet.security.interceptors.MessageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/26/19
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private MessageInterceptor messageInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/hackanet/ws").setAllowedOrigins("http://localhost:63342").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes();
        registry.enableSimpleBroker("");   // Enables a simple in-memory broker


        //   Use this for enabling a Full featured broker like RabbitMQ


//        registry.enableStompBrokerRelay("/hackanet-chat")
//                .setRelayHost("localhost")
//                // once stomp is enabled, the stomp adapter works on 61613 port by default
//                .setRelayPort(61613)
//                .setClientLogin("guest")
//                .setClientPasscode("guest");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(messageInterceptor);
    }
}
