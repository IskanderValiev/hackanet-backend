package com.hackanet.services.chat;

import com.hackanet.json.forms.ChatSettingUpdateForm;
import com.hackanet.models.User;
import com.hackanet.models.chat.ChatSettings;

public interface ChatSettingsService {
    ChatSettings update(User user, ChatSettingUpdateForm form);
    ChatSettings getOrCreateForUser(User user, Long chatId);
}
