package com.hackanet.json.mappers;

import com.hackanet.json.dto.UserNotificationSettingsDto;
import com.hackanet.models.UserNotificationSettings;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/11/19
 */
@Component
public class UserNotificationSettingsMapper implements  Mapper<UserNotificationSettings, UserNotificationSettingsDto> {
    @Override
    public UserNotificationSettingsDto map(UserNotificationSettings from) {
        return UserNotificationSettingsDto.builder()
                .id(from.getId())
                .userId(from.getUser().getId())
                .emailEnabled(from.getEmailEnabled())
                .pushEnabled(from.getPushEnabled())
                .dontDisturbFrom(from.getDontDisturbFrom())
                .dontDisturbTo(from.getDontDisturbTo())
                .build();
    }
}
