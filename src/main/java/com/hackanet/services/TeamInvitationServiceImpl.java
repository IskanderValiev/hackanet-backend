package com.hackanet.services;

import com.hackanet.exceptions.NotFoundException;
import com.hackanet.models.Team;
import com.hackanet.models.TeamInvitation;
import com.hackanet.models.User;
import com.hackanet.models.enums.TeamInvitationStatus;
import com.hackanet.repositories.TeamInvitationRepository;
import com.hackanet.services.chat.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.hackanet.security.utils.SecurityUtils.*;
import static com.hackanet.security.utils.SecurityUtils.checkTeamInvitationAccess;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/24/19
 */
@Service
public class TeamInvitationServiceImpl implements TeamInvitationService {

    @Autowired
    private TeamInvitationRepository repository;

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private SkillCombinationService skillCombinationService;

    @Autowired
    private HackathonService hackathonService;

    @Autowired
    private JoinToHackathonRequestService joinToHackathonRequestService;

    @Override
    public TeamInvitation createIfNotExists(User currentUser, Long userId, Long teamId) {
        Team team = teamService.get(teamId);
        checkTeamAccessAsTeamLeader(team, currentUser);
        teamService.throwExceptionIfTeamIsNotActual(team);
        User user = userService.get(userId);
        TeamInvitation invitation = repository.getByUserIdAndTeamId(userId, teamId);
        if (invitation != null)
            return invitation;

        invitation = TeamInvitation.builder()
                .team(team)
                .user(user)
                .time(LocalDateTime.now())
                .status(TeamInvitationStatus.WAITING)
                .build();
        return repository.save(invitation);
    }

    @Override
    public List<TeamInvitation> getAllByUser(User user) {
        return repository.getByUser(user);
    }

    @Override
    public void delete(User user, Long id) {
        TeamInvitation invitation = get(id);
        checkTeamInvitationAccess(invitation, user, true);
        repository.delete(invitation);
    }

    @Override
    @Transactional
    public TeamInvitation changeStatus(User user, Long invitationId, TeamInvitationStatus status) {
        TeamInvitation invitation = get(invitationId);
        checkTeamInvitationAccess(invitation, user, false);
        teamService.throwExceptionIfTeamIsNotActual(invitation.getTeam());
        user = userService.get(user.getId());
        if (status.equals(TeamInvitationStatus.ACCEPTED)) {
            Team team = invitation.getTeam();
            teamService.addUser(team, user);
            chatService.addOrRemoveUser(team.getChat().getId(), user.getId(), null, true);
            skillCombinationService.updateIfUserJoinedToTeam(user, team);
        }
        invitation.setStatus(status);
        return repository.save(invitation);
    }

    @Override
    public TeamInvitation getInfoSecurely(User user, Long id) {
        TeamInvitation invitation = get(id);
        checkTeamInvitationAccess(invitation, user, true);
        return invitation;
    }

    @Override
    public TeamInvitation get(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Team invitation with id=" + id + " not found"));
    }
}
