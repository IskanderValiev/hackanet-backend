package com.hackanet.services.chat;

import com.google.common.collect.Lists;
import com.hackanet.exceptions.BadRequestException;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.json.forms.ChatCreateForm;
import com.hackanet.models.User;
import com.hackanet.models.chat.Chat;
import com.hackanet.repositories.chat.ChatRepository;
import com.hackanet.security.utils.SecurityUtils;
import com.hackanet.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.hackanet.security.utils.SecurityUtils.*;

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

    @Override
    public Chat create(ChatCreateForm form, User currentUser) {
        if (form.getParticipantsIds() == null) {
            form.setParticipantsIds(Lists.newArrayList());
            form.getParticipantsIds().add(currentUser.getId());
        } else if (form.getParticipantsIds().isEmpty())
            form.getParticipantsIds().add(currentUser.getId());

        List<User> participants = userService.getByIds(form.getParticipantsIds());
        Chat chat = Chat.builder()
                .participants(participants)
                .admin(currentUser)
                .type(form.getChatType())
                .build();
        chat = chatRepository.save(chat);
        return chat;
    }

    @Override
    public Chat get(Long id) {
        return chatRepository.findById(id).orElseThrow(() -> new NotFoundException("Chat with id=" + id + " not found"));
    }

    @Override
    public List<Chat> getByUser(User user) {
        return chatRepository.findAllByParticipantsIn(user);
    }

    @Override
    public Chat addOrRemoveUser(Long chatId, Long userId, User currentUser, Boolean add) {
        Chat chat = get(chatId);
        // any participant can add and remove user from chat
        checkChatAccessForOperation(chat, currentUser);
        User user = userService.get(userId);

        List<User> participants = chat.getParticipants();

        if (Boolean.TRUE.equals(add)) {
            if (participants == null)
                participants = new ArrayList<>();
            if (!participants.contains(user))
                participants.add(user);
        } else if (participants.contains(user)) {
            // TODO: 10/27/19 pick up a new admin if user who created the chat has leaved
            participants.remove(user);
        } else {
            throw new BadRequestException("The user is not in the chat");
        }
        chat.setParticipants(participants);
        chat = chatRepository.save(chat);
        return chat;
    }
}
