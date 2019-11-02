package com.hackanet.services;

import com.hackanet.json.forms.JoinToHackathonRequestCreateForm;
import com.hackanet.models.JoinToHackathonRequest;
import com.hackanet.models.User;
import com.hackanet.models.enums.RequestStatus;

import java.util.List;

public interface JoinToHackathonRequestService {
    JoinToHackathonRequest createRequest(JoinToHackathonRequestCreateForm join, User user);
    JoinToHackathonRequest get(Long id);
    List<JoinToHackathonRequest> getAllByHackathonId(Long hackathonId, User user);
    JoinToHackathonRequest changeStatus(Long id, User user, RequestStatus status);
}