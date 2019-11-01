package com.hackanet.repositories;

import com.hackanet.json.forms.JoinToTeamRequestCreateForm;
import com.hackanet.models.JoinToTeamRequest;
import com.hackanet.models.User;
import com.hackanet.models.enums.JoinToTeamRequestStatus;

import java.util.List;

public interface JoinToTeamRequestService {
    JoinToTeamRequest create(User user, JoinToTeamRequestCreateForm form);
    void delete(User user, Long id);
    JoinToTeamRequest get(Long id);
    JoinToTeamRequest updateStatus(User user, Long id, JoinToTeamRequestStatus status);
    List<JoinToTeamRequest> getByTeamId(User user, Long teamId);
}
