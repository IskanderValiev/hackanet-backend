package com.hackanet.services.chat;

import com.google.common.collect.Lists;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.json.forms.ChatCreateForm;
import com.hackanet.models.JobOffer;
import com.hackanet.models.chat.Chat;
import com.hackanet.models.enums.ChatType;
import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.user.User;
import com.hackanet.repositories.chat.ChatRepository;
import com.hackanet.security.utils.SecurityUtils;
import com.hackanet.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/24/19
 */
@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ChatMessageServiceElasticsearchImpl chatMessageServiceElasticsearch;

    @Override
    public Chat create(ChatCreateForm form, User currentUser) {
        if (form.getParticipantsIds() == null) {
            form.setParticipantsIds(Collections.singletonList(currentUser.getId()));
        } else if (form.getParticipantsIds().isEmpty()) {
            form.getParticipantsIds().add(currentUser.getId());
        }
        Set<User> participants = userService.getByIds(form.getParticipantsIds());
        Chat chat = Chat.builder()
                .participants(participants)
                .admins(Collections.singleton(currentUser))
                .type(form.getChatType())
                .build();
        return chatRepository.save(chat);
    }

    @Override
    public Chat get(Long id) {
        return chatRepository.findById(id).orElseThrow(() -> new NotFoundException("Chat with id=" + id + " not found"));
    }

    @Override
    public List<Chat> getByUser(User user) {
        return chatRepository.findAllByParticipantsIn(Collections.singleton(user));
    }

    @Override
    public Chat addOrRemoveUser(Long chatId, Long userId, User currentUser, Boolean add) {
        Chat chat = get(chatId);
        if (currentUser != null) {
            SecurityUtils.checkChatAccessForOperation(chat, currentUser);
        }
        User user = userService.get(userId);
        Set<User> participants = chat.getParticipants();
        if (Boolean.TRUE.equals(add)) {
            participants.add(user);
        } else {
            participants.remove(user);
        }
        chat.setParticipants(participants);
        chat = chatRepository.save(chat);
        return chat;
    }

    @Override
    public Chat addOrRemoveListOfUsers(Long chatId, List<User> users, User currentUser, Boolean add) {
        Chat chat = get(chatId);
        if (currentUser != null) {
            SecurityUtils.checkChatAccessForOperation(chat, currentUser);
        }

        Set<User> participants = chat.getParticipants();
        if (Boolean.TRUE.equals(add)) {
            participants.addAll(users);
        } else {
            participants.removeAll(users);
        }
        chat.setParticipants(participants);
        return chatRepository.save(chat);
    }

    @Override
    public Chat createForTeam(User teamCreator) {
        Chat chat = Chat.builder()
                .type(ChatType.TEAM_CHAT)
                .participants(Collections.singleton(teamCreator))
                .admins(Collections.singleton(teamCreator))
                .build();
        chat = chatRepository.save(chat);
        return chat;
    }

    @Override
    @Transactional
    public List<Chat> createForHackathon(Hackathon hackathon) {
        Set<User> participants = hackathon.getParticipants();
        participants.add(hackathon.getOwner());

        Chat newsChat = Chat.builder()
                .admins(Collections.singleton(hackathon.getOwner()))
                .type(ChatType.NEWS)
                .participants(participants)
                .hackathon(hackathon)
                .build();

        Chat participantsChat = Chat.builder()
                .type(ChatType.PARTICIPANTS_CONFERENCE)
                .participants(participants)
                .hackathon(hackathon)
                .build();

        List<Chat> chats = new ArrayList<>();
        Collections.addAll(chats, newsChat, participantsChat);
        return chatRepository.saveAll(chats);
    }

    @Override
    public Chat createForAcceptedJobInvitation(JobOffer jobOffer) {
        Set<User> participants = new HashSet<>();
        participants.add(jobOffer.getUser());
        participants.add(jobOffer.getCompany().getAdmin());
        Chat chat = Chat.builder()
                .participants(participants)
                .type(ChatType.PRIVATE)
                .build();
        chat = chatRepository.save(chat);
        chatMessageServiceElasticsearch.createAcceptJobInvitationMessage(chat);
        return chat;
    }
}
