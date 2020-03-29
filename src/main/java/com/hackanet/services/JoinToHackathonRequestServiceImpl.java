package com.hackanet.services;

import com.google.common.collect.Lists;
import com.hackanet.exceptions.BadRequestException;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.json.forms.JoinToHackathonRequestCreateForm;
import com.hackanet.models.*;
import com.hackanet.models.chat.Chat;
import com.hackanet.models.enums.JoinType;
import com.hackanet.models.enums.RequestStatus;
import com.hackanet.models.enums.TeamType;
import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.team.Team;
import com.hackanet.repositories.JoinToHackathonRequestRepository;
import com.hackanet.services.chat.ChatService;
import com.hackanet.services.team.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.hackanet.security.utils.SecurityUtils.checkHackathonAccess;
import static com.hackanet.security.utils.SecurityUtils.checkTeamAccessAsTeamLeader;
import static com.hackanet.utils.DateTimeUtil.now;
import static com.hackanet.utils.validators.HackathonUtils.registrationIsAvailable;

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

    @Autowired
    private ChatService chatService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private TrackService trackService;

    @Override
    public JoinToHackathonRequest createRequest(JoinToHackathonRequestCreateForm form, User user) {
        Hackathon hackathon = hackathonService.get(form.getHackathonId());
        registrationIsAvailable(hackathon);

        if (JoinType.ALONE.equals(form.getJoinType())) {
            if (hackathon.getParticipants().contains(user))
                throw new BadRequestException("You are already a participant of this hackathon");
            throwExceptionIfRequestExistsByUserId(user.getId(), hackathon.getId());
            form.setEntityId(user.getId());
        } else if (JoinType.TEAM.equals(form.getJoinType())) {
            if (form.getEntityId() == null)
                throw new BadRequestException("entity_id must not be null if " + JoinType.class.getName() + " is " + JoinType.TEAM.toString());
            Team team = teamService.get(form.getEntityId());
            throwExceptionIfRequestExistsByTeam(team);
            checkTeamAccessAsTeamLeader(team, user);
            return createForHackathonTeam(team, form.getMainTrackId(), form.getSubTrackId());
        }

        JoinToHackathonRequest request = buildRequestFromCreateForm(form, hackathon);
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
        checkHackathonAccess(hackathon, user);
        return requestRepository.findAllByHackathonId(hackathonId);
    }

    @Override
    @Transactional
    public JoinToHackathonRequest changeStatus(Long id, User user, RequestStatus status) {
        JoinToHackathonRequest request = get(id);
        Hackathon hackathon = request.getHackathon();
        checkHackathonAccess(hackathon, user);
        request.setStatus(status);

        JoinType joinType = request.getJoinType();
        boolean alone = JoinType.ALONE.equals(joinType);
        switch (status) {
            case APPROVED:

                List<Chat> chats = hackathon.getChats();
                if (chats.isEmpty()) {
                    chats = chatService.createForHackathon(hackathon);
                    hackathonService.setChats(chats, hackathon);
                }
                if (alone) {
                    final User participant = userService.get(request.getEntityId());
                    chats.forEach(chat -> {
                        chatService.addOrRemoveUser(chat.getId(), participant.getId(), null, true);
                    });
                    emailService.sendHackathonWelcomeEmail(participant, hackathon);
                    userService.updateUsersHackathonList(participant, hackathon, true);
                } else {
                    Team team = teamService.get(request.getEntityId());
                    chats.forEach(chat -> {
                        chatService.addOrRemoveListOfUsers(chat.getId(), team.getParticipants(), null, true);
                    });
                    team.getParticipants().stream()
                            .filter(tp -> !hackathon.getParticipants().contains(tp))
                            .forEach(tp -> {
                                userService.updateUsersHackathonList(tp, hackathon, true);
                                emailService.sendHackathonWelcomeEmail(tp, hackathon);
                    });
                }
                break;

            case ATTENDED:
                Date startDate = hackathon.getStartDate();
                Date date = now();
                if (date.before(startDate))
                    throw new BadRequestException("Registration for hackathons has not been started yet");
                if (alone) {
                    final User participant = userService.get(request.getEntityId());
                    portfolioService.addHackathonJob(participant.getId(), hackathon);
                } else {
                    Team team = teamService.get(request.getEntityId());
                    team.getParticipants().forEach(tp -> portfolioService.addHackathonJob(tp.getId(), hackathon));
                }
                break;

            case REJECTED:
            case WAITING:
                break;
        }
        request = requestRepository.save(request);
        return request;
    }

    @Override
    public boolean isHackathonAttended(Hackathon hackathon, User user) {
        JoinToHackathonRequest attended = requestRepository.findByHackathonAndEntityIdAndJoinTypeAndStatus(hackathon, user.getId(), JoinType.ALONE, RequestStatus.ATTENDED);
        Team team = teamService.getByHackathonIdAndUserId(user.getId(), hackathon.getId());
        JoinToHackathonRequest teamAttended = requestRepository.findByHackathonAndEntityIdAndJoinTypeAndStatus(hackathon, team.getId(), JoinType.TEAM, RequestStatus.ATTENDED);
        return attended != null || teamAttended != null;
    }

    @Override
    public JoinToHackathonRequest save(JoinToHackathonRequest request) {
        return requestRepository.save(request);
    }

    @Override
    public JoinToHackathonRequest createForHackathonTeam(Team team, Long mainTrackId, Long subTrackId) {
        validate(team);
        List<Track> tracks = trackService.getMainAndSubTracks(mainTrackId, subTrackId);
        JoinToHackathonRequest request = JoinToHackathonRequest.builder()
                .joinType(JoinType.TEAM)
                .entityId(team.getId())
                .status(RequestStatus.WAITING)
                .date(LocalDateTime.now())
                .hackathon(team.getHackathon())
                .mainTrack(tracks.get(0))
                .subTrack(tracks.get(1))
                .build();
        return requestRepository.save(request);
    }

    @Override
    public JoinToHackathonRequest getByHackathonIdAndJoinTypeAndEntityIdAndStatus(Hackathon hackathon, Long entityId, JoinType joinType, RequestStatus status) {
        return requestRepository.findByHackathonAndEntityIdAndJoinTypeAndStatus(hackathon, entityId, joinType, status);
    }

    private void throwExceptionIfRequestExistsByTeam(Team team) {
        boolean exists = requestRepository.existsByEntityIdAndJoinTypeAndHackathonId(team.getId(), JoinType.TEAM, team.getHackathon().getId());
        if (exists)
            throw new BadRequestException("Request already exists");
    }

    private void throwExceptionIfRequestExistsByUserId(Long userId, Long hackathonId) {
        boolean exists = requestRepository.existsByEntityIdAndJoinTypeAndHackathonId(userId, JoinType.ALONE, hackathonId);
        if (exists)
            throw new BadRequestException("Request already exists");
    }

    private JoinToHackathonRequest buildRequestFromCreateForm(JoinToHackathonRequestCreateForm form, Hackathon hackathon) {
        ArrayList<Track> tracks
                = Lists.newArrayList(trackService.getMainAndSubTracks(form.getMainTrackId(), form.getSubTrackId()));
        return JoinToHackathonRequest.builder()
                .hackathon(hackathon)
                .date(LocalDateTime.now())
                .entityId(form.getEntityId())
                .joinType(form.getJoinType())
                .status(RequestStatus.WAITING)
                .mainTrack(tracks.get(0))
                .subTrack(tracks.get(1))
                .build();
    }

    private void validate(Team team) {
        throwExceptionIfRequestExistsByTeam(team);
        if (!TeamType.HACKATHON.equals(team.getTeamType()))
            throw new RuntimeException("TeamType is not " + TeamType.HACKATHON.toString());
    }

}
