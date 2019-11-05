package com.hackanet.security.interceptors;

import com.hackanet.config.JwtConfig;
import com.hackanet.security.authentication.JwtTokenAuthentication;
import com.hackanet.security.providers.JwtTokenAuthenticationProvider;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/5/19
 */
@Component
public class MessageInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtTokenAuthenticationProvider provider;

    @Autowired
    private JwtConfig jwtConfig;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        MessageHeaders headers = message.getHeaders();
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        MultiValueMap<String, String> multiValueMap = headers.get(StompHeaderAccessor.NATIVE_HEADERS, MultiValueMap.class);
//        for(Map.Entry<String, List<String>> head : multiValueMap.entrySet()){
//            System.out.println(head.getKey() + "#" + head.getValue());
//        }
            if (multiValueMap != null) {
                List<String> authList = multiValueMap.get("Authorization");
                String authHeader = authList != null && !authList.isEmpty() ? authList.get(0) : "";
                JwtTokenAuthentication authentication;

                SecurityContext securityContext = SecurityContextHolder.getContext();
                if (StringUtils.isBlank(authHeader)) {
                    authentication = new JwtTokenAuthentication(null);
                    authentication.setAuthenticated(false);
                } else {
                    String token = "";
                    String prefix = jwtConfig.getPrefix() + " ";
                    token = authHeader.substring(prefix.length());
                    authentication = new JwtTokenAuthentication(token);
                    securityContext.setAuthentication(provider.authenticate(authentication));
                }
            }
        return message;
    }


}
