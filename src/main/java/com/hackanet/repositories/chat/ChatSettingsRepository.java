package com.hackanet.repositories.chat;

import com.hackanet.models.User;
import com.hackanet.models.chat.Chat;
import com.hackanet.models.chat.ChatSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatSettingsRepository extends JpaRepository<ChatSettings, Long> {
    Optional<ChatSettings> findByUserAndChat(User user, Chat chat);
}
