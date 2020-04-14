package com.hackanet.services.team;

import com.hackanet.json.forms.JoinToTeamRequestCreateForm;
import com.hackanet.models.user.User;
import com.hackanet.models.enums.JoinToTeamRequestStatus;
import com.hackanet.models.team.JoinToTeamRequest;
import com.hackanet.services.RetrieveService;

import java.util.List;

public interface JoinToTeamRequestService extends RetrieveService<JoinToTeamRequest> {
    JoinToTeamRequest create(User user, JoinToTeamRequestCreateForm form);
    void delete(User user, Long id);
    JoinToTeamRequest updateStatus(User user, Long id, JoinToTeamRequestStatus status);
    List<JoinToTeamRequest> getByTeamId(User user, Long teamId);
}
