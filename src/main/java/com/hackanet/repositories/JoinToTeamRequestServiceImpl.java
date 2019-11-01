package com.hackanet.repositories;

import com.hackanet.exceptions.BadRequestException;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.json.forms.JoinToTeamRequestCreateForm;
import com.hackanet.models.JoinToTeamRequest;
import com.hackanet.models.Team;
import com.hackanet.models.User;
import com.hackanet.models.enums.JoinToTeamRequestStatus;
import com.hackanet.security.utils.SecurityUtils;
import com.hackanet.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/1/19
 */
@Service
public class JoinToTeamRequestServiceImpl implements JoinToTeamRequestService {

    @Autowired
    private JoinToTeamRequestRepository joinToTeamRequestRepository;
    @Autowired
    private TeamService teamService;

    @Override
    public JoinToTeamRequest create(User user, JoinToTeamRequestCreateForm form) {
        JoinToTeamRequest joinToTeamRequest = joinToTeamRequestRepository.findByUserIdAndTeamId(user.getId(), form.getTeamId());
        if (joinToTeamRequest != null)
            return joinToTeamRequest;

        Team team = teamService.get(form.getTeamId());
        if (team.getParticipants().contains(user))
            throw new BadRequestException("You already in the team");

        JoinToTeamRequest request = JoinToTeamRequest.builder()
                .requestStatus(JoinToTeamRequestStatus.WAITING)
                .message(form.getMessage())
                .user(user)
                .team(team)
                .build();

        request = joinToTeamRequestRepository.save(request);
        return request;
    }

    @Override
    public void delete(User user, Long id) {
        SecurityUtils.checkJoinToTeamRequestAccess(user, get(id));
        joinToTeamRequestRepository.deleteById(id);
    }

    @Override
    public JoinToTeamRequest get(Long id) {
        return joinToTeamRequestRepository.findById(id).orElseThrow(() -> new NotFoundException("Join To Team Request with id=" + id + " not found"));
    }

    @Override
    @Transactional
    public JoinToTeamRequest updateStatus(User user, Long id, JoinToTeamRequestStatus status) {
        JoinToTeamRequest request = get(id);
        Team team = request.getTeam();
        SecurityUtils.checkTeamAccessAsTeamLeader(team, user);

        if (JoinToTeamRequestStatus.APPROVED.equals(status)) {
            List<User> participants = team.getParticipants();
            participants.add(request.getUser());
            team.setParticipants(participants);
            teamService.save(team);
            //send email about approving
        } else if (JoinToTeamRequestStatus.REJECTED.equals(status)) {
            //send email about reject
        }

        request.setRequestStatus(status);
        request = joinToTeamRequestRepository.save(request);
        return request;
    }

    @Override
    public List<JoinToTeamRequest> getByTeamId(User user, Long teamId) {
        Team team = teamService.get(teamId);
        SecurityUtils.checkTeamAccess(team, user);
        return joinToTeamRequestRepository.findAllByTeamId(teamId);
    }
}
