package com.hackanet.repositories.user;

import com.hackanet.models.user.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/12/19
 */
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
    UserToken findByUserId(Long userId);
}
