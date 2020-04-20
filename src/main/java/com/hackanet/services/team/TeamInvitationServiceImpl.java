package com.hackanet.services.team;

import com.hackanet.exceptions.NotFoundException;
import com.hackanet.models.skill.Skill;
import com.hackanet.models.team.Team;
import com.hackanet.models.team.TeamInvitation;
import com.hackanet.models.user.User;
import com.hackanet.models.enums.TeamInvitationStatus;
import com.hackanet.repositories.TeamInvitationRepository;
import com.hackanet.services.skill.SkillCombinationService;
import com.hackanet.services.skill.SkillService;
import com.hackanet.services.user.UserService;
import com.hackanet.services.chat.ChatService;
import com.hackanet.services.scheduler.JobRunner;
import com.hackanet.utils.validators.TeamInvitationSkillsValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.hackanet.security.utils.SecurityUtils.checkTeamAccessAsTeamLeader;
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
    private JobRunner jobRunner;

    @Autowired
    private TeamMemberService teamMemberService;

    @Autowired
    private SkillService skillService;

    @Autowired
    private TeamInvitationSkillsValidator teamInvitationSkillsValidator;

    @Override
    public TeamInvitation createIfNotExists(User currentUser, Long userId, Long teamId) {
        Team team = teamService.get(teamId);
        checkTeamAccessAsTeamLeader(team, currentUser);
        teamService.checkRelevance(team);
        User user = userService.get(userId);
        TeamInvitation invitation = repository.getByUserIdAndTeamId(userId, teamId);
        if (invitation != null) {
            return invitation;
        }
        invitation = build(user, team);
        invitation = repository.save(invitation);
        jobRunner.addTeamInvitationNotification(null, invitation);
        return invitation;
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
    public TeamInvitation changeStatus(User user, Long invitationId, TeamInvitationStatus status, List<Long> skillsIds) {
        teamInvitationSkillsValidator.validate(status, skillsIds);
        TeamInvitation invitation = get(invitationId);
        checkTeamInvitationAccess(invitation, user, false);
        teamService.checkRelevance(invitation.getTeam());
        user = userService.get(user.getId());
        if (status.equals(TeamInvitationStatus.ACCEPTED)) {
            acceptInvitation(user, invitation, skillService.getByIds(skillsIds));
        }
        invitation.setStatus(status);
        invitation = repository.save(invitation);
        jobRunner.addTeamInvitationChangedStatusNotification(null, invitation);
        return invitation;
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

    @Override
    public void sendInvitations(Set<User> participants, User user, Team team) {
        participants
                .forEach(p -> createIfNotExists(user, p.getId(), team.getId()));
    }

    private TeamInvitation build(User user, Team team) {
        return TeamInvitation.builder()
                .team(team)
                .user(user)
                .time(LocalDateTime.now())
                .status(TeamInvitationStatus.WAITING)
                .build();
    }

    private void acceptInvitation(User user, TeamInvitation invitation, List<Skill> skills) {
        Team team = invitation.getTeam();
        teamMemberService.addTeamMember(user, team, skills);
        chatService.addOrRemoveUser(team.getChat().getId(), user.getId(), null, true);
        skillCombinationService.updateIfUserJoinedToTeam(user, team);
    }
}
