package com.hackanet.repositories;

import com.hackanet.models.User;
import com.hackanet.models.UserNotificationSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserNotificationSettingRepository extends JpaRepository<UserNotificationSettings, Long> {
    Optional<UserNotificationSettings> findByUser(User user);
}
