package com.hackanet.services;

import com.hackanet.json.forms.JoinToTeamRequestCreateForm;
import com.hackanet.models.JoinToTeamRequest;
import com.hackanet.models.User;
import com.hackanet.models.enums.JoinToTeamRequestStatus;

import java.util.List;

public interface JoinToTeamRequestService extends CrudService<JoinToTeamRequest> {
    JoinToTeamRequest create(User user, JoinToTeamRequestCreateForm form);
    void delete(User user, Long id);
    JoinToTeamRequest updateStatus(User user, Long id, JoinToTeamRequestStatus status);
    List<JoinToTeamRequest> getByTeamId(User user, Long teamId);
}
