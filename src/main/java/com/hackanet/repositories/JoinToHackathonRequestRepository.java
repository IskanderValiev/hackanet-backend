package com.hackanet.repositories;

import com.hackanet.models.JoinToHackathonRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/21/19
 */
public interface JoinToHackathonRequestRepository extends JpaRepository<JoinToHackathonRequest, Long> {
    List<JoinToHackathonRequest> findAllByHackathonId(Long hackathonId);
}
