package com.hackanet.services.user;

import com.hackanet.json.forms.UserNotificationSettingsUpdateForm;
import com.hackanet.models.user.User;
import com.hackanet.models.user.UserNotificationSettings;

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
