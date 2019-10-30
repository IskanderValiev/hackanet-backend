package com.hackanet.services.push;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
//import com.hackanet.config.AMPQConfig;
import com.hackanet.models.UserPhoneToken;
import com.hackanet.push.ResolvedPush;
import com.hackanet.push.enums.ClientType;
import com.hackanet.services.UserService;
import com.hackanet.utils.PushAvailabilityChecker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.hackanet.utils.PushAvailabilityChecker.isAvailableForAndroid;
import static com.hackanet.utils.PushAvailabilityChecker.isAvailableForiOS;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/30/19
 */
@Service
@Slf4j
public class RabbitMQPushNotificationService implements MessageListener {
    
    public static final int MAX_PUSH_TEXT_LENGTH = 50;
    public static final int MAX_RETRIES_FOR_PUSH = 3;

    @Autowired
//    @Qualifier("rabbitObjectMapper")
    private ObjectMapper objectMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    @Qualifier("androidPushNotificationSender")
    private PushNotificationSender androidPushNotificationSender;

    @Autowired
    @Qualifier("iosPushNotificationSender")
    private PushNotificationSender fcmIosPushNotificationSender;

    @Autowired
    private PushNotificationResolverService pushResolver;

    @Autowired
    private UserService userService;

    private Message buildMessage(PushNotificationMsg msgEntity) {
        MessageProperties messageProperties = new MessageProperties();
        Integer priority;
        if (msgEntity.getPriority() == null) {
            priority = getMessagePriority(msgEntity);
        } else {
            priority = msgEntity.getPriority();
        }
        messageProperties.setPriority(priority);
        // delete this if not necessary
        msgEntity.setPriority(priority);
        try {
            return new Message(objectMapper.writeValueAsBytes(msgEntity), messageProperties);
        } catch (JsonProcessingException e) {
            log.debug("Error on push message building", e);
            return new Message("null".getBytes(), messageProperties);
        }
    }

    private Integer getMessagePriority(PushNotificationMsg msgEntity) {
        return new Double(Math.random() * 9 + 1).intValue();
    }

    @Override
    public void onMessage(Message message) {
        PushNotificationMsg msg = null;
        try {
            msg = objectMapper.readValue(message.getBody(), PushNotificationMsg.class);
            Multimap<ClientType, UserPhoneToken> tokensForUser = userService.getTokensForUser(msg.getToUserId());
            List<UserPhoneToken> androidTokens = Lists.newArrayList(tokensForUser.get(ClientType.ANDROID));
            List<UserPhoneToken> iosTokens = Lists.newArrayList(tokensForUser.get(ClientType.IOS));
            ResolvedPush resolvedPush = pushResolver.resolve(msg);
            for (UserPhoneToken iosToken : iosTokens) {
                fcmIosPushNotificationSender.sendPush(msg, resolvedPush, iosToken.getToken());
                log.debug("Send IOS push notification of {} to {}", msg.getType(), iosToken.getToken());
            }
            for (UserPhoneToken androidToken : androidTokens) {
                androidPushNotificationSender.sendPush(msg, resolvedPush, androidToken.getToken());
                log.debug("Send android push notification of {} to {}", msg.getType(), androidToken.getToken());
            }
            log.debug("Done sending push for all of {} to user [{}]", msg.getType(), msg.getToUserId());
        } catch (Exception e) {
            if (msg != null && msg.getRetries() < MAX_RETRIES_FOR_PUSH) {
                Integer retries = msg.getRetries();
                if (msg.getPriority() > 0) {
                    msg.setPriority(msg.getPriority() - 1);
                }
                log.debug("Send push notification with type {} and retry {}", msg.getType(), msg.getRetries());
                msg.setRetries(++retries);
//                rabbitTemplate.convertAndSend(AMPQConfig.QUEUE_NAME, buildMessage(msg));
            } else {
                log.debug("Push notification msg reached its max retries and will be removed");
            }
            log.error("Error on push notification message receive or send", e);
        }
    }
}
