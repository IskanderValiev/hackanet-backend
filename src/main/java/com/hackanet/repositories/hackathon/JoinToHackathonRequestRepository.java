package com.hackanet.repositories.hackathon;

import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.JoinToHackathonRequest;
import com.hackanet.models.enums.JoinType;
import com.hackanet.models.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/21/19
 */
public interface JoinToHackathonRequestRepository extends JpaRepository<JoinToHackathonRequest, Long> {
    List<JoinToHackathonRequest> findAllByHackathonId(Long hackathonId);
    JoinToHackathonRequest findByHackathonAndEntityIdAndJoinTypeAndStatus(Hackathon hackathon, Long entityId, JoinType joinType, RequestStatus status);
    boolean existsByEntityIdAndJoinTypeAndHackathonId(Long entityId, JoinType joinType, Long hackathonId);
}
