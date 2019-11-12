package com.hackanet.services;

import com.hackanet.json.forms.UserNotificationSettingsUpdateForm;
import com.hackanet.models.User;
import com.hackanet.models.UserNotificationSettings;
import com.hackanet.repositories.UserNotificationSettingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/11/19
 */
@Service
@Slf4j
public class UserNotificationSettingsServiceImpl implements UserNotificationSettingsService {

    @Autowired
    private UserNotificationSettingRepository userNotificationSettingRepository;

    @Override
    public UserNotificationSettings getOrCreateDefaultsSettingsForUser(User user) {
        UserNotificationSettings userNotificationSettings = userNotificationSettingRepository.findByUser(user)
                .orElse(UserNotificationSettings.builder()
                        .user(user)
                        .emailEnabled(Boolean.TRUE)
                        .pushEnabled(Boolean.TRUE)
                        .build());

        return userNotificationSettings.getId() == null
                ? userNotificationSettingRepository.save(userNotificationSettings)
                : userNotificationSettings;
    }

    @Override
    @Transactional
    public UserNotificationSettings updateForUser(User user, UserNotificationSettingsUpdateForm form) {
        UserNotificationSettings settings = getOrCreateDefaultsSettingsForUser(user);
        settings.setEmailEnabled(form.getEmailEnabled());
        settings.setPushEnabled(form.getPushEnabled());

        if (form.getDontDisturbFrom() != null)
            settings.setDontDisturbFrom(LocalTime.ofSecondOfDay(form.getDontDisturbFrom() / 1000));
        else settings.setDontDisturbFrom(null);

        if (form.getDontDisturbTo() != null)
            settings.setDontDisturbTo(LocalTime.ofSecondOfDay(form.getDontDisturbTo() / 1000));
        else settings.setDontDisturbTo(null);

        return userNotificationSettingRepository.save(settings);
    }

    @Override
    public boolean pushEnabled(User user) {
        UserNotificationSettings settings = getOrCreateDefaultsSettingsForUser(user);
        return Boolean.TRUE.equals(settings.getPushEnabled());
    }

    @Override
    public boolean emailEnabled(User user) {
        UserNotificationSettings settings = getOrCreateDefaultsSettingsForUser(user);
        return Boolean.TRUE.equals(settings.getEmailEnabled());
    }
}
