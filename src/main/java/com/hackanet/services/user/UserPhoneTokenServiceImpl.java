package com.hackanet.services.user;

import com.hackanet.exceptions.BadRequestException;
import com.hackanet.json.forms.UserPhoneTokenAddForm;
import com.hackanet.models.user.User;
import com.hackanet.models.user.UserPhoneToken;
import com.hackanet.repositories.user.UserPhoneTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/31/19
 */
@Service
public class UserPhoneTokenServiceImpl implements UserPhoneTokenService {

    @Autowired
    private UserPhoneTokenRepository userPhoneTokenRepository;

    @Autowired
    private UserService userService;

    @Override
    public UserPhoneToken add(UserPhoneTokenAddForm form) {
        UserPhoneToken token = userPhoneTokenRepository.findAllByUserIdAndDeviceIdAndDeviceType(form.getUserId(), form.getDeviceId(), form.getClientType());
        if (token != null)
            throw new BadRequestException("User token with such properties already exists");
        User user = userService.get(form.getUserId());
        UserPhoneToken userPhoneToken = UserPhoneToken.builder()
                .deviceType(form.getClientType())
                .token(form.getToken().trim())
                .deviceId(form.getDeviceId().trim())
                .user(user)
                .build();
        userPhoneToken = userPhoneTokenRepository.save(userPhoneToken);
        return userPhoneToken;
    }
}
