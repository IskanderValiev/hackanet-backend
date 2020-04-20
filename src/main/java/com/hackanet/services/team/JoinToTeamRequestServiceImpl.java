package com.hackanet.services.team;

import com.hackanet.exceptions.AlreadyExistException;
import com.hackanet.exceptions.BadRequestException;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.json.forms.JoinToTeamRequestCreateForm;
import com.hackanet.models.enums.JoinToTeamRequestStatus;
import com.hackanet.models.team.JoinToTeamRequest;
import com.hackanet.models.team.Team;
import com.hackanet.models.team.TeamMember;
import com.hackanet.models.user.User;
import com.hackanet.repositories.JoinToTeamRequestRepository;
import com.hackanet.security.utils.SecurityUtils;
import com.hackanet.services.EmailService;
import com.hackanet.services.chat.ChatService;
import com.hackanet.services.push.RabbitMQPushNotificationService;
import com.hackanet.services.skill.SkillCombinationService;
import com.hackanet.services.user.UserNotificationSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlIjoiQURNSU4iLCJlbWFpbCI6Imlza2FuZC52YWxpZXZAeWFuZGV4LnJ1Iiwic3ViIjoiMSJ9.XPbjyLPS_AHCtjbk9xEteRI_ruOtWSiCedR6O9HSKoKY1ZuXXdyfBDA2ere6diN4ice27ZG0w4WgX_1SmhQikg
 * on 11/1/19
 */
@Service
public class JoinToTeamRequestServiceImpl implements JoinToTeamRequestService {

    @Autowired
    private JoinToTeamRequestRepository joinToTeamRequestRepository;

    @Autowired
    private TeamService teamService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserNotificationSettingsService userNotificationSettingsService;

    @Autowired
    private RabbitMQPushNotificationService pushNotificationService;

    @Autowired
    private SkillCombinationService skillCombinationService;

    @Autowired
    private TeamMemberService teamMemberService;

    @Override
    public JoinToTeamRequest create(User user, JoinToTeamRequestCreateForm form) {
        JoinToTeamRequest joinToTeamRequest = joinToTeamRequestRepository.findByUserIdAndTeamId(user.getId(), form.getTeamId());
        if (joinToTeamRequest != null) {
            return joinToTeamRequest;
        }
        Team team = teamService.get(form.getTeamId());
        teamService.checkRelevance(team);
        if (teamService.teamContainsUser(team, user.getId())) {
            throw new BadRequestException("The user is already in the team");
        }
        JoinToTeamRequest request = JoinToTeamRequest.builder()
                .requestStatus(JoinToTeamRequestStatus.WAITING)
                .message(form.getMessage())
                .user(user)
                .team(team)
                .build();
        return joinToTeamRequestRepository.save(request);
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
        teamService.checkRelevance(team);
        SecurityUtils.checkTeamAccessAsTeamLeader(team, user);
        User userFromRequest = request.getUser();

        if (JoinToTeamRequestStatus.APPROVED.equals(status)) {
            final boolean containsUser = teamService.teamContainsUser(team, userFromRequest.getId());
            if (containsUser) {
                throw new AlreadyExistException("The user is already in team");
            }
            teamMemberService.addTeamMember(userFromRequest, team);
            chatService.addOrRemoveUser(team.getChat().getId(), userFromRequest.getId(), null, true);
            skillCombinationService.updateIfUserJoinedToTeam(userFromRequest, team);
            if (userNotificationSettingsService.emailEnabled(userFromRequest)) {
                emailService.sendTeamWelcomeEmail(userFromRequest, team);
            }
        } else if (JoinToTeamRequestStatus.REJECTED.equals(status)) {
            final TeamMember teamMember = teamMemberService.getMemberByUserIdAndTeamId(userFromRequest.getId(), team.getId());
            if (teamMember != null) {
                chatService.addOrRemoveUser(team.getChat().getId(), userFromRequest.getId(), null, false);
                skillCombinationService.recalculate(teamMember, true);
                teamMemberService.deleteTeamMember(teamMember);
            }
            emailService.sendTeamRejectEmail(request.getUser(), team);
        }

        if (userNotificationSettingsService.pushEnabled(userFromRequest)) {
            pushNotificationService.sendJoinToTeamRequestUpdatedStatusNotification(userFromRequest, request);
        }
        request.setRequestStatus(status);
        return joinToTeamRequestRepository.save(request);
    }

    @Override
    public List<JoinToTeamRequest> getByTeamId(User user, Long teamId) {
        Team team = teamService.get(teamId);
        SecurityUtils.checkTeamAccess(team, user);
        return joinToTeamRequestRepository.findAllByTeamId(teamId);
    }
}
