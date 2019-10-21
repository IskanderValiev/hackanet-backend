package com.hackanet.services;

import com.hackanet.exceptions.BadRequestException;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.json.forms.JoinToHackathonRequestCreateForm;
import com.hackanet.models.Hackathon;
import com.hackanet.models.JoinToHackathonRequest;
import com.hackanet.models.User;
import com.hackanet.models.enums.RequestStatus;
import com.hackanet.repositories.JoinToHackathonRequestRepository;
import com.hackanet.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/21/19
 */
@Service
public class JoinToHackathonRequestServiceImpl implements JoinToHackathonRequestService {

    @Autowired
    private JoinToHackathonRequestRepository requestRepository;
    @Autowired
    private HackathonService hackathonService;
    @Autowired
    private UserService userService;

    @Override
    public JoinToHackathonRequest createRequest(JoinToHackathonRequestCreateForm form, User user) {
        Hackathon hackathon = hackathonService.get(form.getHackathonId());
        Date now = new Date(System.currentTimeMillis());

        if (now.after(hackathon.getStartDate()))
            throw new BadRequestException("Hackathon has already started or passed");

        JoinToHackathonRequest request = JoinToHackathonRequest.builder()
                .hackathon(hackathon)
                .message(form.getMessage())
                .date(now)
                .user(user)
                .status(RequestStatus.WAITING)
                .build();
        request = requestRepository.save(request);
        return request;
    }

    @Override
    public JoinToHackathonRequest get(Long id) {
        return requestRepository.findById(id).orElseThrow(() -> new NotFoundException("Request with id=" + id + " not found"));
    }

    @Override
    public List<JoinToHackathonRequest> getAllByHackathonId(Long hackathonId, User user) {
        Hackathon hackathon = hackathonService.get(hackathonId);
        SecurityUtils.checkHackathonAccess(hackathon, user);
        return requestRepository.findAllByHackathonId(hackathonId);
    }

    @Override
    @Transactional
    public JoinToHackathonRequest changeStatus(Long id, User user, RequestStatus status) {
        JoinToHackathonRequest request = get(id);
        Hackathon hackathon = request.getHackathon();
        SecurityUtils.checkHackathonAccess(hackathon, user);
        request.setStatus(status);
        switch (status) {
            case APPROVED:
                User participant = request.getUser();
                userService.updateUsersHackathonList(participant, hackathon, true);
                break;
            case REJECTED:
            case WAITING:
                break;
        }
        request = requestRepository.save(request);
        return request;
    }
}
