package com.hackanet.repositories.user;

import com.hackanet.models.user.User;
import com.hackanet.models.user.UserNotificationSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserNotificationSettingRepository extends JpaRepository<UserNotificationSettings, Long> {
    Optional<UserNotificationSettings> findByUser(User user);
}
