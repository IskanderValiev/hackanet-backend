package com.hackanet.security.utils;

import com.hackanet.exceptions.BadRequestException;
import com.hackanet.exceptions.UnauthorizedException;
import com.hackanet.models.User;
import com.hackanet.security.authentication.JwtTokenAuthentication;
import com.hackanet.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/26/19
 */
@Component
public class SimpMessageHeaderAccessorUtils {

    @Autowired
    private UserService userService;

    public User getAuthenticatedUser(SimpMessageHeaderAccessor accessor) {
        String token;
        try {
            token = accessor.getNativeHeader("Authorization").get(0);
        } catch (NullPointerException ex) {
            throw new UnauthorizedException("You have to be authorized to get an acсess to this resource");
        }
        User user = userService.getUserByJwtToken(token);
        boolean expired = userService.userTokenExpired(user, false);
        if (expired)
            throw new BadRequestException("User access token has expired");
        return user;
    }
}
