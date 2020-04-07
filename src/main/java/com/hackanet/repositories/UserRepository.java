package com.hackanet.repositories;

import com.hackanet.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    Set<User> findAllByIdIn(List<Long> ids);
    Optional<User> findByEmailConfirmationCode(String code);
}
