package com.hackanet.repositories.chat;

import com.hackanet.models.user.User;
import com.hackanet.models.chat.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findAllByParticipantsIn(User user);
}
