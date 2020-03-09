package com.hackanet.services.push;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.hackanet.config.AMPQConfig;
import com.hackanet.json.mappers.MessageMapper;
import com.hackanet.models.*;
import com.hackanet.models.chat.Chat;
import com.hackanet.models.chat.ChatSettings;
import com.hackanet.models.team.JoinToTeamRequest;
import com.hackanet.models.team.Team;
import com.hackanet.models.team.TeamInvitation;
import com.hackanet.push.ResolvedPush;
import com.hackanet.push.enums.ClientType;
import com.hackanet.push.enums.PushType;
import com.hackanet.push.payload.PostPushPayload;
import com.hackanet.services.ConnectionInvitationService;
import com.hackanet.services.UserService;
import com.hackanet.services.chat.ChatService;
import com.hackanet.services.chat.ChatSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.stream.Collectors;

//import com.hackanet.config.AMPQConfig;

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

    @Autowired
    private ChatService chatService;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private ChatSettingsService chatSettingsService;

    @Autowired
    private ConnectionInvitationService connectionInvitationService;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    public void sendNewMessageNotification(@NotNull com.hackanet.models.chat.Message message) {
        Chat chat = chatService.get(message.getChatId());
        List<User> participants = chat.getParticipants().stream()
                .filter(participant -> !participant.getId().equals(message.getSenderId()))
                .collect(Collectors.toList());

        participants.forEach(participant -> {
            // do not send notification if muted has been set false
            ChatSettings chatSettings = chatSettingsService.getOrCreateForUser(participant, chat.getId());
            if (!chatSettings.getMuted()) {
                this.buildPushNotificationMsgAndSendToQueue(participant, message, PushType.NEW_MESSAGE);
            }
        });
    }

    public void sendTestNotification() {
        PushNotificationMsg msg = PushNotificationMsg.builder()
                .toUserId(1L)
                .type(PushType.NEW_MESSAGE)
                .payloadEntity("NEW_MESSAGE")
                .build();
        rabbitTemplate.convertAndSend(AMPQConfig.QUEUE_NAME, buildMessage(msg));
    }

    public void sendHackathonJobReviewRequestNotification(User user, Team team) {
        this.buildPushNotificationMsgAndSendToQueue(user, team, PushType.HACKATHON_JOB_REVIEW_REQUEST);
    }

    public void sendJoinToTeamRequestUpdatedStatusNotification(User user , JoinToTeamRequest request) {
        this.buildPushNotificationMsgAndSendToQueue(user, request, PushType.JOIN_TO_TEAM_REQUEST_STATUS);
    }

    public void sendJobInvitationNotification(User user, JobOffer jobOffer) {
        this.buildPushNotificationMsgAndSendToQueue(user, jobOffer, PushType.JOB_INVITATION);
    }

    public void sendConnectionInvitationNotification(User user, ConnectionInvitation connectionInvitation) {
        this.buildPushNotificationMsgAndSendToQueue(user, connectionInvitation, PushType.CONNECTION_INVITATION);
    }

    public void sendTeamInvitationNotification(User user, TeamInvitation teamInvitation) {
        this.buildPushNotificationMsgAndSendToQueue(user, teamInvitation, PushType.TEAM_INVITATION);
    }

    public void sendTeamInvitationUpdatedStatus(User user, TeamInvitation teamInvitation) {
        this.buildPushNotificationMsgAndSendToQueue(user, teamInvitation, PushType.TEAM_INVITATION_CHANGED_STATUS);
    }

    public void sendNewPostNotification(User user, Post post) {
        log.info("Sending a new post notification to user with id={}", user.getId());
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        Session session = sessionFactory.openSession();
        NativeQuery<Hackathon> nativeQuery = session.createNativeQuery("select h.* from hackanet_db.public.hackathons h inner join hackanet_db.public.posts p on h.id = p.hackathon_id where p.id = :postId", Hackathon.class);
        nativeQuery.setParameter("postId", post.getId());

        PostPushPayload payload = PostPushPayload.builder()
                .hackathonName(nativeQuery.getSingleResult().getName())
                .build();
        payload.setId(post.getId());

        this.buildPushNotificationMsgAndSendToQueue(user, payload, PushType.NEW_POST);
        session.clear();
        session.close();
    }

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
                rabbitTemplate.convertAndSend(AMPQConfig.QUEUE_NAME, buildMessage(msg));
            } else {
                log.debug("Push notification msg reached its max retries and will be removed");
            }
            log.error("Error on push notification message receive or send", e);
        }
    }

    private /*<T extends PushPayloadEntity>*/ void buildPushNotificationMsgAndSendToQueue(User user, Object entity, PushType pushType) {
        PushNotificationMsg msg = PushNotificationMsg.builder()
                .toUserId(user.getId())
                .type(pushType)
                .payloadEntity(entity)
                .build();
        rabbitTemplate.convertAndSend(AMPQConfig.QUEUE_NAME, buildMessage(msg));
    }
}
