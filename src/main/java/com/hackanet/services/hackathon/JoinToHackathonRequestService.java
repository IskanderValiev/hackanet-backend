package com.hackanet.services.hackathon;

import com.hackanet.json.forms.JoinToHackathonRequestCreateForm;
import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.JoinToHackathonRequest;
import com.hackanet.models.team.Team;
import com.hackanet.models.user.User;
import com.hackanet.models.enums.JoinType;
import com.hackanet.models.enums.RequestStatus;
import com.hackanet.services.RetrieveService;

import java.util.List;

public interface JoinToHackathonRequestService extends RetrieveService<JoinToHackathonRequest> {
    JoinToHackathonRequest createRequest(JoinToHackathonRequestCreateForm join, User user);
    List<JoinToHackathonRequest> getAllByHackathonId(Long hackathonId, User user);
    JoinToHackathonRequest changeStatus(Long id, User user, RequestStatus status);
    boolean isHackathonAttended(Hackathon hackathon, User user);
    JoinToHackathonRequest save(JoinToHackathonRequest request);
    JoinToHackathonRequest createForHackathonTeam(Team team, Long mainTrackId, Long subTrackId);
    JoinToHackathonRequest getByHackathonIdAndJoinTypeAndEntityIdAndStatus(Hackathon hackathon, Long entityId, JoinType joinType, RequestStatus status);
}
