package com.hackanet.services;

import com.hackanet.models.user.User;

public interface BlockedUserService {
    boolean isBlocked(User user);
}
