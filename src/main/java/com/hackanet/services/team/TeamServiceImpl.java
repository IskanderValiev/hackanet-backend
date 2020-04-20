package com.hackanet.services.team;

import com.hackanet.exceptions.BadRequestException;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.json.forms.TeamCreateForm;
import com.hackanet.json.forms.TeamSearchForm;
import com.hackanet.json.forms.TeamUpdateForm;
import com.hackanet.models.chat.Chat;
import com.hackanet.models.enums.TeamType;
import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.skill.Skill;
import com.hackanet.models.team.Team;
import com.hackanet.models.team.TeamMember;
import com.hackanet.models.user.User;
import com.hackanet.models.user.UserNotificationSettings;
import com.hackanet.repositories.TeamRepository;
import com.hackanet.security.utils.SecurityUtils;
import com.hackanet.services.chat.ChatService;
import com.hackanet.services.hackathon.HackathonService;
import com.hackanet.services.scheduler.JobRunner;
import com.hackanet.services.skill.SkillCombinationService;
import com.hackanet.services.skill.SkillService;
import com.hackanet.services.user.UserNotificationSettingsService;
import com.hackanet.services.user.UserService;
import com.hackanet.utils.DateTimeUtil;
import com.hackanet.utils.validators.TeamCreateFormValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/31/19
 */
@Service
@Slf4j
public class TeamServiceImpl implements TeamService {

    private static final Integer DEFAULT_LIMIT = 10;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private HackathonService hackathonService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private SkillService skillService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JobRunner jobRunner;

    @Autowired
    private UserNotificationSettingsService userNotificationSettingsService;

    @Autowired
    private SkillCombinationService skillCombinationService;

    @Autowired
    private TeamInvitationService teamInvitationService;

    @Autowired
    private TeamMemberService teamMemberService;

    @Autowired
    private TeamCreateFormValidator teamFormValidator;

    @Override
    public Team save(Team team) {
        return teamRepository.save(team);
    }

    @Override
    public List<Team> save(List<Team> teams) {
        return teamRepository.saveAll(teams);
    }

    @Override
    @Transactional
    public Team createTeam(User user, TeamCreateForm form) {
        Team team = build(form, user);
        final Team savedTeam = teamRepository.save(team);
        if (TeamType.HACKATHON.equals(team.getTeamType())) {
            UserNotificationSettings settings = userNotificationSettingsService.getOrCreateDefaultsSettingsForUser(user);
            if (Boolean.TRUE.equals(settings.getPushEnabled())) {
                jobRunner.addHackathonJobReviewRequestJobToTeamLeader(settings, user, team);
            }
        }
        Set<User> participants = userService.getByIds(form.getParticipantsIds());
        teamInvitationService.sendInvitations(participants, user, savedTeam);
        teamMemberService.addTeamMember(user, savedTeam, skillService.getByIds(form.getTeamLeaderUsedSkills()));
        skillCombinationService.createByTeam(team);
        return team;
    }

    @Override
    @Transactional
    public Team updateTeam(Long id, User user, TeamUpdateForm form) {
        final Team team = get(id);
        SecurityUtils.checkTeamAccess(team, user);
        team.setName(form.getName().trim());
        team.setLookingForHackers(Boolean.TRUE.equals(form.getLookingForHackers()));
        List<Long> skillsLookingFor = form.getSkillsLookingFor();
        team.setSkillsLookingFor(skillService.getByIds(skillsLookingFor));
        team.setTeamLeader(userService.get(form.getTeamLeader()));
        team.setTeamType(form.getTeamType());
        return teamRepository.save(team);
    }

    @Override
    public Team get(Long id) {
        return teamRepository.findById(id).orElseThrow(() -> new NotFoundException("Team with id=" + id + " not found"));
    }

    @Override
    public List<Team> getByHackathon(Long hackathonId) {
        return teamRepository.findAllByHackathonId(hackathonId);
    }

    @Override
    public List<Team> teamList(TeamSearchForm form) {
        if (form.getLimit() == null) {
            form.setLimit(DEFAULT_LIMIT);
        }
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Team> teamListQuery = getTeamListQuery(criteriaBuilder, form);
        TypedQuery<Team> query = entityManager.createQuery(teamListQuery);
        if (form.getPage() != null) {
            query.setFirstResult((form.getPage() - 1) * form.getLimit());
        } else {
            form.setPage(1);
        }
        query.setMaxResults(form.getLimit());
        return query.getResultList();
    }

    @Override
    public Team getByHackathonIdAndUserId(Long userId, Long hackathonId) {
        return teamRepository.findByHackathonIdAndUserId(userId, hackathonId);
    }

    @Override
    public List<Team> getByHackathonStartTime(LocalDate startTime) {
        return teamRepository.findTeamsByStartDateOfHackathon(startTime);
    }

    @Override
    public void checkRelevance(Team team) {
        if (!Boolean.TRUE.equals(team.getRelevant())) {
            throw new BadRequestException("Team is not relevant anymore");
        }
    }

    @Deprecated
    @Override
    public List<Team> getTeamsSuggestion(User user) {
        user = userService.get(user.getEmail());
        List<Skill> skills = skillCombinationService.mostRelevantSkills(user);
        if (skills.isEmpty()) {
            return teamRepository.findAllByLookingForHackersAndRelevant(true, true);
        }
        List<Long> skillsIds = skills.stream().map(Skill::getId).collect(Collectors.toList());
        return teamRepository.findBySkills(skillsIds);
    }


    /**
     * Get team suggestions for user.
     * Works calculating the probability of usage the user's technology with technologies used in teams.
     *
     * @param user        - user to suggest teams for
     * @param hackathonId - hackathonId search teams in
     * @return list of teams which are looking for hackers, actual and are appropriate by user's skill
     */
    @Override
    public List<Team> getTeamsSuggestion(@NotNull User user, Long hackathonId) {
        user = userService.get(user.getId());
        List<Skill> skills = skillCombinationService.mostRelevantSkills(user);
        if (skills.isEmpty()) {
            return teamRepository.findAllByLookingForHackersAndRelevant(true, true);
        }
        List<Long> skillsIds = skills.stream().map(Skill::getId).collect(Collectors.toList());
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Team> suggestions = getTeamSuggestionsListQuery(criteriaBuilder, skillsIds, hackathonId);
        TypedQuery<Team> query = entityManager.createQuery(suggestions);
        return query.getResultList();
    }

    @Override
    public boolean teamContainsUser(Team team, Long userId) {
        return teamMemberService.exists(userId, team.getId());
    }

    @Override
    public Team deleteMember(Long teamId, Long userId, User currentUser) {
        final Team team = get(teamId);
        SecurityUtils.checkTeamAccessAsTeamLeader(team, currentUser);
        checkChangeTeamAvailability(team);
        teamMemberService.deleteTeamMember(teamId, userId);
        chatService.addOrRemoveUser(team.getChat().getId(), userId, currentUser, false);
        return team;
    }

    private CriteriaQuery<Team> getTeamSuggestionsListQuery(CriteriaBuilder criteriaBuilder, List<Long> skillsIds, Long hackathonId) {
        CriteriaQuery<Team> query = criteriaBuilder.createQuery(Team.class);
        Root<Team> root = query.from(Team.class);
        query.select(root);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.isTrue(root.get("actual")));
        predicates.add(criteriaBuilder.isTrue(root.get("lookingForHackers")));
        if (hackathonId != null) {
            Join<Team, Hackathon> join = root.join("hackathon", JoinType.INNER);
            join.on(criteriaBuilder.equal(join.get("id"), hackathonId));
            predicates.add(join.getOn());
        }
        Join<Team, TeamMember> teamParticipantsJoin = root.join("members", JoinType.INNER);
        // FIXME: 4/14/20 join.get("id") fix join parameter
        teamParticipantsJoin.on(teamParticipantsJoin.get("id").in(skillsIds));
        query.distinct(true);
        query.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        return query;
    }

    private CriteriaQuery<Team> getTeamListQuery(CriteriaBuilder criteriaBuilder, TeamSearchForm form) {
        CriteriaQuery<Team> query = criteriaBuilder.createQuery(Team.class);
        Root<Team> root = query.from(Team.class);
        query.select(root);
        List<Predicate> predicates = new ArrayList<>();
        if (form.getHackathonId() != null) {
            Join<Team, Hackathon> join = root.join("hackathon", JoinType.INNER);
            join.on(criteriaBuilder.equal(join.get("id"), form.getHackathonId()));
            predicates.add(join.getOn());
        }
        String name = form.getName();
        if (!StringUtils.isBlank(name)) {
            Expression<String> nameInLc = criteriaBuilder.lower(root.get("name"));
            predicates.add(criteriaBuilder.like(nameInLc, "%" + name.trim().toLowerCase() + "%"));
        }
        List<Long> requiredSkills = form.getRequiredSkills();
        if (requiredSkills != null && !requiredSkills.isEmpty()) {
            Join<Team, Skill> join = root.join("skillsLookingFor", JoinType.INNER);
            join.on(join.get("id").in(requiredSkills));
            predicates.add(join.getOn());
        }
        query.distinct(true);
        query.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        return query;
    }

    private Team build(TeamCreateForm form, User user) {
        teamFormValidator.validateCreateForm(form);
        Chat chat = chatService.createForTeam(user);
        List<Long> skillsLookingForIds = form.getSkillsLookingFor();
        Team team = Team.builder()
                .name(form.getName().trim())
                .chat(chat)
                .teamLeader(user)
                .skillsLookingFor(skillService.getByIds(skillsLookingForIds))
                .teamType(form.getTeamType())
                .relevant(true)
                .build();
        if (TeamType.HACKATHON.equals(form.getTeamType())) {
            if (form.getHackathonId() == null) {
                throw new BadRequestException("hackathonId must not be null if " + TeamType.class.getName() + " is " + TeamType.HACKATHON.toString());
            }
            Hackathon hackathon = hackathonService.get(form.getHackathonId());
            if (hackathon.getStartDate().before(new Date())) {
                throw new BadRequestException("The hackathons has already started");
            }
            team.setHackathon(hackathon);
        }
        return team;
    }

    private void checkChangeTeamAvailability(Team team) {
        if (!TeamType.HACKATHON.equals(team.getTeamType())) {
            return;
        }
        final java.sql.Date startDate = team.getHackathon().getStartDate();
        final LocalDateTime startLocalDateTime = DateTimeUtil.longToLocalDateTime(startDate.getTime());
        final LocalDateTime now = LocalDateTime.now();
        final Long differenceInHours = DateTimeUtil.getDifferenceBetweenLocalDateTimes(startLocalDateTime, now, TimeUnit.HOURS);
        if (differenceInHours < 24) {
            throw new BadRequestException("Team updating is not allowed less than 24 hours before a hackathon starts");
        }
    }
}
