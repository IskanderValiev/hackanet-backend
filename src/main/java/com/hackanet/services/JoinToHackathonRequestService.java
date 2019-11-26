package com.hackanet.services;

import com.hackanet.json.forms.JoinToHackathonRequestCreateForm;
import com.hackanet.models.Hackathon;
import com.hackanet.models.JoinToHackathonRequest;
import com.hackanet.models.Team;
import com.hackanet.models.User;
import com.hackanet.models.enums.JoinType;
import com.hackanet.models.enums.RequestStatus;

import java.util.List;

public interface JoinToHackathonRequestService extends CrudService<JoinToHackathonRequest> {
    JoinToHackathonRequest createRequest(JoinToHackathonRequestCreateForm join, User user);
    List<JoinToHackathonRequest> getAllByHackathonId(Long hackathonId, User user);
    JoinToHackathonRequest changeStatus(Long id, User user, RequestStatus status);
    boolean isHackathonAttended(Hackathon hackathon, User user);
    JoinToHackathonRequest save(JoinToHackathonRequest request);
    JoinToHackathonRequest createForHackathonTeam(Team team);
    JoinToHackathonRequest getByHackathonIdAndJoinTypeAndEntityIdAndStatus(Hackathon hackathon, Long entityId, JoinType joinType, RequestStatus status);
}
