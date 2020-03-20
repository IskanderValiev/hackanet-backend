package com.hackanet.json.mappers.activity.log;

import com.hackanet.json.dto.activity.log.ActivityUser;
import com.hackanet.models.User;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/18/20
 */
@Component
public class ActivityUserMapper {

    public ActivityUser map(User user) {
        ActivityUser activityUser = ActivityUser.builder()
                .id(user.getId())
                .email(user.getEmail())
                .build();
        return activityUser;
    }
}
