package com.hackanet.repositories;

import com.hackanet.models.PasswordChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordChangeRequestRepository extends JpaRepository<PasswordChangeRequest, Long> {
    PasswordChangeRequest findAllByUserIdAndUsed(Long userId, Boolean used);
    Optional<PasswordChangeRequest> findByCode(String code);
}
