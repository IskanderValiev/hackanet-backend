package com.hackanet.services;

import com.hackanet.models.BlockedUser;
import com.hackanet.models.enums.BlockInitiator;
import com.hackanet.models.enums.BlockReason;

public interface UserBlockingService {
    BlockedUser block(Long userId, BlockInitiator initiator, BlockReason reason);
    void unblock(Long userId);
}
