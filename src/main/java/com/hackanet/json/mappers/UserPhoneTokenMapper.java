package com.hackanet.json.mappers;

import com.hackanet.json.dto.UserPhoneTokenDto;
import com.hackanet.models.UserPhoneToken;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/31/19
 */
@Component
public class UserPhoneTokenMapper implements Mapper<UserPhoneToken, UserPhoneTokenDto> {
    @Override
    public UserPhoneTokenDto map(UserPhoneToken from) {
        return UserPhoneTokenDto.builder()
                .id(from.getId())
                .deviceId(from.getDeviceId())
                .token(from.getToken())
                .userId(from.getUser().getId())
                .clientType(from.getDeviceType())
                .build();
    }
}
