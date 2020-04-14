package com.hackanet.repositories;

import com.hackanet.models.BlockedUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlockedUserRepository extends JpaRepository<BlockedUser, Long> {
    Optional<BlockedUser> findByUserId(Long id);
}
