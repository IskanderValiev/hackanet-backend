package com.hackanet.services;

import com.hackanet.exceptions.NotFoundException;
import com.hackanet.models.BlockedUser;
import com.hackanet.models.enums.BlockInitiator;
import com.hackanet.models.enums.BlockReason;
import com.hackanet.repositories.BlockedUserRepository;
import com.hackanet.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author Iskander Valiev
 * created by isko
 * on 4/14/20
 */
@Service
public class UserBlockingBlockingServiceImpl implements UserBlockingService {

    @Autowired
    private BlockedUserRepository blockedUserRepository;

    @Autowired
    private UserService userService;

    @Override
    public BlockedUser block(@NotNull Long userId, BlockInitiator initiator, @NotNull BlockReason reason) {
        BlockedUser blockedUser = BlockedUser.builder()
                .blockInitiator(initiator == null ? BlockInitiator.SYSTEM : initiator)
                .blockReason(reason)
                .user(userService.get(userId))
                .blockTime(LocalDateTime.now())
                .canceled(false)
                .build();
        return blockedUserRepository.save(blockedUser);
    }

    @Override
    public void unblock(Long userId) {
        final BlockedUser blockedUser = getByUserId(userId);
        blockedUser.setCanceled(true);
        blockedUserRepository.save(blockedUser);
    }

    private BlockedUser getByUserId(Long userId) {
        return blockedUserRepository.findByUserId(userId)
                .orElseThrow(() -> NotFoundException.throwNFE(BlockedUser.class, "userId", userId));
    }
}
