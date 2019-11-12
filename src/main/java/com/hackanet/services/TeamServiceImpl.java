package com.hackanet.services;

import com.hackanet.exceptions.BadRequestException;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.json.forms.TeamCreateForm;
import com.hackanet.json.forms.TeamSearchForm;
import com.hackanet.json.forms.TeamUpdateForm;
import com.hackanet.models.*;
import com.hackanet.models.chat.Chat;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    @Override
    public Team save(Team team) {
        return teamRepository.save(team);
    }

    @Override
    @Transactional
    public Team createTeam(TeamCreateForm form) {
        if (form.getParticipantsIds().size() < 2)
            throw new BadRequestException("Team should be contain at least 2 participants");
        if (!form.getParticipantsIds().contains(form.getTeamLeader()))
            throw new BadRequestException("Team leader is not in the team");

        String name = form.getName().trim();
        throwExceptionIfStringContainsBadWords(name, "name");

        List<User> participants = userService.getByIds(form.getParticipantsIds());
        User teamLeader = userService.get(form.getTeamLeader());
        Hackathon hackathon = hackathonService.get(form.getHackathonId());
        Chat chat = chatService.createForTeam(participants);
        List<Long> skillsLookingForIds = form.getSkillsLookingFor();
        List<Skill> skillsLookingFor = new ArrayList<>();
        if (skillsLookingForIds != null && !skillsLookingForIds.isEmpty())
            skillsLookingFor = skillService.getByIds(skillsLookingForIds);

        Team team = Team.builder()
                .name(name)
                .participants(participants)
                .hackathon(hackathon)
                .chat(chat)
                .teamLeader(teamLeader)
                .skillsLookingFor(skillsLookingFor)
                .build();

        team = teamRepository.save(team);

        UserNotificationSettings settings = userNotificationSettingsService.getOrCreateDefaultsSettingsForUser(teamLeader);
        if (Boolean.TRUE.equals(settings.getPushEnabled())) {
            jobRunner.addHackathonJobReviewRequestJobToTeamLeader(settings, teamLeader, team);
        }
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

        /*
        * if user is contained in participants but is not contained in members =>
        * the user will be added in chat and team
        *
        * if user is contained in members but is not contained in participants =>
        * the user will be removed from the chat and the team
        * */
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
            team.setParticipants(userService.getByIds(participants));
        }

        List<Long> skillsLookingFor = form.getSkillsLookingFor();
        if (skillsLookingFor != null && !skillsLookingFor.isEmpty()) {
            team.setSkillsLookingFor(skillService.getByIds(skillsLookingFor));
        }

        if (form.getTeamLeader() != null) {
            team.setTeamLeader(userService.get(form.getTeamLeader()));
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
