package com.hackanet.services.chat;

import com.hackanet.json.forms.ChatSettingUpdateForm;
import com.hackanet.models.User;
import com.hackanet.models.chat.Chat;
import com.hackanet.models.chat.ChatSettings;
import com.hackanet.repositories.chat.ChatSettingsRepository;
import com.hackanet.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/11/19
 */
@Service
public class ChatSettingsServiceImpl implements ChatSettingsService {

    @Autowired
    private ChatSettingsRepository chatSettingsRepository;
    @Autowired
    private ChatService chatService;

    @Override
    public ChatSettings update(User user, ChatSettingUpdateForm form) {
        ChatSettings chatSettings = getOrCreateForUser(user, form.getChatId());
        chatSettings.setChat(chatService.get(form.getChatId()));
        chatSettings.setMuted(form.getMuted());
        return chatSettingsRepository.save(chatSettings);
    }

    @Override
    public ChatSettings getOrCreateForUser(User user, Long chatId) {
        Chat chat = chatService.get(chatId);
        SecurityUtils.checkChatAccess(chat, user);

        ChatSettings chatSettings = chatSettingsRepository.findByUserAndChat(user, chat).orElse(ChatSettings.builder()
                .chat(chat)
                .muted(false)
                .build());
        return chatSettings.getId() == null
                ? chatSettingsRepository.save(chatSettings) : chatSettings;
    }
}
