package com.hackanet.services;

import com.hackanet.json.forms.UserNotificationSettingsUpdateForm;
import com.hackanet.models.User;
import com.hackanet.models.UserNotificationSettings;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/11/19
 */
public interface UserNotificationSettingsService {
    UserNotificationSettings getOrCreateDefaultsSettingsForUser(User user);
    UserNotificationSettings updateForUser(User user, UserNotificationSettingsUpdateForm form);
    boolean pushEnabled(User user);
    boolean emailEnabled(User user);
}
