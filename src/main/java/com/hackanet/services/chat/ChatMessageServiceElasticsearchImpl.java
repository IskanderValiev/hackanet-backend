package com.hackanet.services.chat;

import com.hackanet.exceptions.BadRequestException;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.json.forms.ChatMessageSaveForm;
import com.hackanet.json.forms.MessageSearchForm;
import com.hackanet.models.FileInfo;
import com.hackanet.models.user.User;
import com.hackanet.models.chat.Chat;
import com.hackanet.models.chat.Message;
import com.hackanet.models.enums.LocalMessageText;
import com.hackanet.repositories.chat.MessageRepository;
import com.hackanet.security.utils.SecurityUtils;
import com.hackanet.services.FileInfoService;
import com.hackanet.services.user.UserService;
import com.hackanet.services.scheduler.JobRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.SimpleField;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.hackanet.utils.StringUtils.generateRandomString;
import static com.hackanet.utils.StringUtils.isPhrase;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/28/19
 */
@Component("chatMessageServiceElasticsearchImpl")
public class ChatMessageServiceElasticsearchImpl {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private FileInfoService fileInfoService;

    @Autowired
    private JobRunner jobRunner;

    @Transactional
    public Message saveMessage(ChatMessageSaveForm form) {
        List<Long> attachments = null;
        if (form.getAttachments() != null) {
            if (form.getAttachments().size() > 10) {
                throw new BadRequestException("The number of attachments can't be more than 10");
            }
            attachments = fileInfoService.getByIdsIn(form.getAttachments())
                    .stream()
                    .map(FileInfo::getId)
                    .collect(Collectors.toList());
        }
        chatService.get(form.getChatId());
        userService.get(form.getSenderId());
        if (form.getReplyTo() != null) {
            getById(form.getReplyTo());
        }
        Message chatMessage = Message.builder()
                .id(generateRandomString())
                .chatId(form.getChatId())
                .datetime(System.currentTimeMillis())
                .text(form.getText().trim())
                .attachments(attachments)
                .senderId(form.getSenderId())
                .replyTo(form.getReplyTo())
                .build();
        chatMessage = messageRepository.save(chatMessage);
        jobRunner.addNewMessageNotification(null, chatMessage);
        return chatMessage;
    }

    public List<Message> getAll() {
        List<Message> messages = new ArrayList<>();
        messageRepository.findAll().forEach(messages::add);
        return messages;
    }

    public List<Message> getByChatId(Long chatId) {
        return messageRepository.findAllByChatId(chatId);
    }

    public List<Message> searchMessage(MessageSearchForm form) {
        String query = form.getQuery();
        boolean isPhrase = isPhrase(query);
        Criteria textCriteria;
        if (!isPhrase) {
            textCriteria = Criteria.where("text").contains(query);
        } else {
            textCriteria = Criteria.where("text").expression(query);
        }
        Criteria chatCriteria;
        User user = userService.get(form.getUserId());
        if (form.getChatId() != null) {
            Chat chat = chatService.get(form.getChatId());
            SecurityUtils.checkChatAccess(chat, user);
            chatCriteria = Criteria.where(new SimpleField("chatId")).is(form.getChatId());
        } else {
            List<Chat> chats = chatService.getByUser(user);
            List<Long> ids = chats.stream().map(Chat::getId).collect(Collectors.toList());
            chatCriteria = Criteria.where("chatId").in(ids);
        }
        Criteria searchCriteria = new Criteria();
        searchCriteria.and(textCriteria, chatCriteria);
        CriteriaQuery criteriaQuery = new CriteriaQuery(searchCriteria);
        return elasticsearchTemplate.queryForList(criteriaQuery, Message.class).stream()
                .sorted(Comparator.comparing(Message::getDatetime).reversed())
                .collect(Collectors.toList());
    }

    void createAcceptJobInvitationMessage(Chat chat) {
        Message message = Message.builder()
                .chatId(chat.getId())
                .datetime(System.currentTimeMillis())
                .text(LocalMessageText.EN.getAcceptedJobOffer())
                .build();
        message = messageRepository.save(message);
        jobRunner.addNewMessageNotification(null, message);
    }

    public Message getById(String id) {
        return messageRepository.findById(id).orElseThrow(() -> new NotFoundException("Message with id = " + id + " not found"));
    }

    public List<Message> getReplies(String id) {
        return messageRepository.findAllByReplyTo(id);
    }

    public Message getLastMessageByChatId(Long chatId) {
        return null;
    }
}
