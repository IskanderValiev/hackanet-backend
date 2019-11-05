package com.hackanet.services.chat;

import com.hackanet.json.forms.ChatCreateForm;
import com.hackanet.models.Hackathon;
import com.hackanet.models.User;
import com.hackanet.models.chat.Chat;

import java.util.List;

public interface ChatService {
    Chat create(ChatCreateForm form, User currentUser);
    Chat get(Long id);
    List<Chat> getByUser(User user);
    Chat addOrRemoveUser(Long chatId, Long user, User currentUser, Boolean add);
    Chat createForTeam(List<User> participants);
    List<Chat> createForHackathon(Hackathon hackathon);
}
