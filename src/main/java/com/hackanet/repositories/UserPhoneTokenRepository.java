package com.hackanet.repositories;

import com.hackanet.models.UserPhoneToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPhoneTokenRepository extends JpaRepository<UserPhoneToken, Long> {
    List<UserPhoneToken> findAllByUserId(Long userId);
}
