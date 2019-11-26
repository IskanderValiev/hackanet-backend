package com.hackanet.services;

import com.hackanet.exceptions.BadRequestException;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.json.forms.TeamCreateForm;
import com.hackanet.json.forms.TeamSearchForm;
import com.hackanet.json.forms.TeamUpdateForm;
import com.hackanet.models.*;
import com.hackanet.models.chat.Chat;
import com.hackanet.models.enums.TeamType;
import com.hackanet.repositories.TeamRepository;
import com.hackanet.security.utils.SecurityUtils;
import com.hackanet.services.chat.ChatService;
import com.hackanet.services.scheduler.JobRunner;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.hackanet.utils.StringUtils.throwExceptionIfStringContainsBadWords;

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
    private JoinToHackathonRequestService joinToHackathonRequestService;
    @Autowired
    private SkillCombinationService skillCombinationService;
    @Autowired
    private TeamInvitationService teamInvitationService;

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
        String name = form.getName().trim();
        throwExceptionIfStringContainsBadWords(name, "name");

        Set<User> participants = userService.getByIds(form.getParticipantsIds());
        participants.add(user);
        Chat chat = chatService.createForTeam(user);
        List<Long> skillsLookingForIds = form.getSkillsLookingFor();
        List<Skill> skillsLookingFor = new ArrayList<>();
        if (skillsLookingForIds != null && !skillsLookingForIds.isEmpty())
            skillsLookingFor = skillService.getByIds(skillsLookingForIds);

        Team team = Team.builder()
                .name(name)
                .chat(chat)
                .participants(Collections.singletonList(user))
                .teamLeader(user)
                .skillsLookingFor(skillsLookingFor)
                .teamType(form.getTeamType())
                .actual(true)
                .build();

        if (TeamType.HACKATHON.equals(form.getTeamType())) {
            if (form.getHackathonId() == null)
                throw new BadRequestException("hackathonId must not be null if " + TeamType.class.getName() + " is " + TeamType.HACKATHON.toString());
            Hackathon hackathon = hackathonService.get(form.getHackathonId());
            if (hackathon.getStartDate().before(new Date()))
                throw new BadRequestException("The hackathons has already started");
            team.setHackathon(hackathon);
        }
        final Team savedTeam = teamRepository.save(team);
        joinToHackathonRequestService.createForHackathonTeam(team);

        UserNotificationSettings settings = userNotificationSettingsService.getOrCreateDefaultsSettingsForUser(user);
        if (Boolean.TRUE.equals(settings.getPushEnabled())) {
            jobRunner.addHackathonJobReviewRequestJobToTeamLeader(settings, user, team);
        }
        participants.stream()
                .filter(p -> !p.equals(user))
                .forEach(p -> teamInvitationService.createIfNotExists(user, p.getId(), savedTeam.getId()));
        skillCombinationService.createByTeam(team);
        return team;
    }

    @Override
    @Transactional
    public Team updateTeam(Long id, User user, TeamUpdateForm form) {
        final Team team = get(id);

        SecurityUtils.checkTeamAccess(team, user);

        String name = form.getName();
        if (!StringUtils.isBlank(name)) {
            throwExceptionIfStringContainsBadWords(name.trim(), "name");
            team.setName(name.trim());
        }

        if (form.getLookingForHackers() != null)
            team.setLookingForHackers(Boolean.TRUE.equals(form.getLookingForHackers()));

        /*
         * if user is contained in participants but is not contained in members =>
         * the user will be added in chat and team
         *
         * if user is contained in members but is not contained in participants =>
         * the user will be removed from the chat and the team
         * */
        // TODO: 11/24/19 delete
        List<Long> participants = form.getParticipants();
        if (participants != null && !participants.isEmpty()) {
            List<Long> members = team.getParticipants().stream()
                    .map(User::getId)
                    .collect(Collectors.toList());

            participants.forEach(p -> {
                if (!members.contains(p)) {
                    chatService.addOrRemoveUser(team.getChat().getId(), p, null, true);
                }
            });

            members.forEach(m -> {
                if (!participants.contains(m)) {
                    chatService.addOrRemoveUser(team.getChat().getId(), m, null, false);
                    log.info("Removing member with id = {}", m);
                }
            });
            Set<User> participantSet = userService.getByIds(participants);
            team.setParticipants(new ArrayList<>(participantSet));
        }

        List<Long> skillsLookingFor = form.getSkillsLookingFor();
        if (skillsLookingFor != null && !skillsLookingFor.isEmpty()) {
            team.setSkillsLookingFor(skillService.getByIds(skillsLookingFor));
        }

        if (form.getTeamLeader() != null) {
            team.setTeamLeader(userService.get(form.getTeamLeader()));
        }

        if (form.getTeamType() != null) {
            team.setTeamType(form.getTeamType());
        }
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
        if (form.getLimit() == null) form.setLimit(DEFAULT_LIMIT);
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Team> teamListQuery = getTeamListQuery(criteriaBuilder, form);
        TypedQuery<Team> query = entityManager.createQuery(teamListQuery);
        if (form.getPage() != null)
            query.setFirstResult((form.getPage() - 1) * form.getLimit());
        else form.setPage(1);
        query.setMaxResults(form.getLimit());
        return query.getResultList();
    }

    @Override
    public Team getByHackathonIdAndUserId(Long userId, Long hackathonId) {
        return teamRepository.findByHackathonIdAndUserId(userId, hackathonId);
    }

    @Override
    public Team addUser(Team team, User user) {
        List<User> participants = team.getParticipants();
        if (!participants.contains(user))
            participants.add(user);
        team.setParticipants(participants);
        return teamRepository.save(team);
    }

    @Override
    public List<Team> getByHackathonStartTime(LocalDate startTime) {
        return teamRepository.findTeamsByStartDateOfHackathon(startTime);
    }

    @Override
    public void throwExceptionIfTeamIsNotActual(Team team) {
        if (!Boolean.TRUE.equals(team.getActual()))
            throw new BadRequestException("Team is not actual anymore");
    }

    @Deprecated
    @Override
    public List<Team> getTeamsSuggestion(User user) {
        user = userService.get(user.getEmail());
        List<Skill> skills = skillCombinationService.mostRelevantSkills(user);
        if (skills.isEmpty())
            return teamRepository.findAllByLookingForHackersAndActual(true, true);
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
        if (skills.isEmpty())
            return teamRepository.findAllByLookingForHackersAndActual(true, true);
        List<Long> skillsIds = skills.stream().map(Skill::getId).collect(Collectors.toList());
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Team> suggestions = getTeamSuggestionsListQuery(criteriaBuilder, skillsIds, hackathonId);
        TypedQuery<Team> query = entityManager.createQuery(suggestions);
        return query.getResultList();
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
        Join<Team, User> teamParticipantsJoin = root.join("participants", JoinType.INNER);
        Join<User, Skill> userSkillJoin = teamParticipantsJoin.join("skills", JoinType.INNER);
        userSkillJoin.on(userSkillJoin.get("id").in(skillsIds));
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
        String name = form.getName().trim();
        if (!StringUtils.isBlank(name)) {
            Expression<String> nameInLc = criteriaBuilder.lower(root.get("name"));
            predicates.add(criteriaBuilder.like(nameInLc, "%" + name + "%"));
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
}
