package com.hackanet.services;

import com.hackanet.models.BlockedUser;
import com.hackanet.models.user.User;
import com.hackanet.repositories.BlockedUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Iskander Valiev
 * created by isko
 * on 4/29/20
 */
@Service
public class BlockedUserServiceImpl implements BlockedUserService {

    @Autowired
    private BlockedUserRepository blockedUserRepository;

    @Override
    public boolean isBlocked(User user) {
        final Optional<BlockedUser> blockedUser = blockedUserRepository.findByUserId(user.getId());
        if (!blockedUser.isPresent()) {
            return false;
        }
        return blockedUser
                .filter(value -> Boolean.FALSE.equals(value.getCanceled()))
                .isPresent();
    }
}
