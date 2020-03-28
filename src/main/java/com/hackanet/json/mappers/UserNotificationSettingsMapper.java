package com.hackanet.json.mappers;

import com.hackanet.json.dto.UserNotificationSettingsDto;
import com.hackanet.models.UserNotificationSettings;
import com.hackanet.utils.DateTimeUtil;
import org.springframework.stereotype.Component;

import static com.hackanet.utils.DateTimeUtil.localTimeToLong;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/11/19
 */
@Component
public class UserNotificationSettingsMapper implements  Mapper<UserNotificationSettings, UserNotificationSettingsDto> {
    @Override
    public UserNotificationSettingsDto map(UserNotificationSettings from) {
        if (from == null) {
            return null;
        }
        return UserNotificationSettingsDto.builder()
                .id(from.getId())
                .userId(from.getUser().getId())
                .emailEnabled(from.getEmailEnabled())
                .pushEnabled(from.getPushEnabled())
                .dontDisturbFrom(localTimeToLong(from.getDontDisturbFrom()))
                .dontDisturbTo(localTimeToLong(from.getDontDisturbTo()))
                .build();
    }
}
