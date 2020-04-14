package com.hackanet.repositories.user;

import com.hackanet.models.user.UserPhoneToken;
import com.hackanet.push.enums.ClientType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPhoneTokenRepository extends JpaRepository<UserPhoneToken, Long> {
    List<UserPhoneToken> findAllByUserId(Long userId);
    UserPhoneToken findAllByUserIdAndDeviceIdAndDeviceType(Long userId, String deviceId, ClientType deviceType);
}
