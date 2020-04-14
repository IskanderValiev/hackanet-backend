package com.hackanet.services.hackathon;

import com.hackanet.application.AppConstants;
import com.hackanet.components.Profiling;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.json.forms.HackathonCreateForm;
import com.hackanet.json.forms.HackathonSearchForm;
import com.hackanet.json.forms.HackathonUpdateForm;
import com.hackanet.models.FileInfo;
import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.skill.Skill;
import com.hackanet.models.user.User;
import com.hackanet.models.chat.Chat;
import com.hackanet.repositories.hackathon.HackathonRepository;
import com.hackanet.services.FileInfoService;
import com.hackanet.services.skill.SkillService;
import com.hackanet.services.chat.ChatService;
import com.hackanet.utils.validators.HackathonCreateCreateFormValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.*;

import static com.hackanet.security.utils.SecurityUtils.checkHackathonAccess;
import static com.hackanet.utils.DateTimeUtil.epochToLocalDateTime;
import static com.hackanet.utils.DateTimeUtil.getRegistrationLocalDateTimeFromForm;
import static java.lang.System.currentTimeMillis;
import static org.apache.commons.lang.StringUtils.lowerCase;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/19/19
 */
@Profiling(enabled = true)
@Service
public class HackathonServiceImpl implements HackathonService {

    @Autowired
    private HackathonRepository hackathonRepository;

    @Autowired
    private FileInfoService fileInfoService;

    @Autowired
    private SkillService skillService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ChatService chatService;

    @Autowired
    private HackathonCreateCreateFormValidator createFormValidator;

    @Cacheable(value = "hackathons")
    @Override
    @Transactional
    public List<Hackathon> getAll() {
        return hackathonRepository.findAllByDeletedIsFalseAndApprovedIsTrue();
    }

    @CachePut("hackathons")
    @Override
    @Transactional
    public Hackathon save(User user, HackathonCreateForm form) {
        createFormValidator.validateCreateForm(form);
        Hackathon hackathon = buildHackathonFromCreateForm(form, user);
        if (form.getLogoId() != null) {
            FileInfo logo = fileInfoService.get(form.getLogoId());
            hackathon.setLogo(logo);
        }
        hackathon = hackathonRepository.save(hackathon);
        chatService.createForHackathon(hackathon);
        return hackathon;
    }

    @Override
    public Hackathon save(Hackathon hackathon) {
        return hackathonRepository.save(hackathon);
    }

    @Override
    public Hackathon get(Long id) {
        return hackathonRepository.findById(id).orElseThrow(() -> NotFoundException.forHackathon(id));
    }

    /**
     * Updates information about hackathon.
     * If string fields are not empty and are not null, fields will be updated
     *
     * @throws com.hackanet.exceptions.ForbiddenException if user is not an owner of the hackathon
     */
    @CacheEvict(value = "hackathons", allEntries = true)
    @Override
    public Hackathon update(Long id, User user, HackathonUpdateForm form) {
        createFormValidator.validateUpdateForm(form);
        Hackathon hackathon = get(id);
        checkHackathonAccess(hackathon, user);
        setHackathonNewValues(form, hackathon);
        hackathon = hackathonRepository.save(hackathon);
        return hackathon;
    }

    @Override
    public void delete(Long id, User user) {
        Hackathon hackathon = get(id);
        checkHackathonAccess(hackathon, user);
        hackathon.setDeleted(true);
        hackathonRepository.save(hackathon);
    }

    @Override
    public List<Hackathon> hackathonList(HackathonSearchForm form) {
        if (form.getLimit() == null)
            form.setLimit(AppConstants.DEFAULT_LIMIT);
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Hackathon> hackathonListQuery = getHackathonsListQuery(criteriaBuilder, form);
        TypedQuery<Hackathon> query = entityManager.createQuery(hackathonListQuery);
        if (form.getPage() != null) {
            query.setFirstResult((form.getPage() - 1) * form.getLimit());
        } else {
            form.setPage(1);
        }
        query.setMaxResults(form.getLimit());
        return query.getResultList();
    }

    @CacheEvict(value = "hackathons", allEntries = true)
    @Override
    public void updateUsersHackathonList(List<User> users, Hackathon hackathon, boolean add) {
        Set<User> participants = hackathon.getParticipants();
        if (Boolean.TRUE.equals(add))
            participants.addAll(new HashSet<>(users));
        else
            participants.removeAll(new HashSet<>(users));
        hackathon.setParticipants(participants);
        hackathonRepository.save(hackathon);
    }

    @Override
    public List<Hackathon> getFriendsHackathons(User user) {
        String query = "select h.* from hackathons h " +
                "inner join hackathon_participants_table hpt on h.id = hpt.hackathon_id " +
                "where hpt.user_id in " +
                "(select c.connection_id from connections c where c.user_id = :userId) and h.id not in (select hpts.hackathon_id from hackathon_participants_table hpts where hpts.user_id = :userId);";
        Query nativeQuery = entityManager
                .createNativeQuery(query, Hackathon.class)
                .setParameter("userId", user.getId());
        return (List<Hackathon>) nativeQuery.getResultList();
    }

    @Override
    public List<Hackathon> getHackathonsListByUser(User user) {
        return hackathonRepository.findByParticipantsContaining(user);
    }

    public void setChats(List<Chat> chats, Hackathon hackathon) {
        hackathon.setChats(chats);
        save(hackathon);
    }

    @Override
    public Hackathon getByAdmin(Long userId) {
        return Optional.of(hackathonRepository.findByOwnerId(userId))
                .orElseThrow(() -> new NotFoundException("Hackathon with admin id = " + userId + " not found"));
    }

    @Override
    public Hackathon approve(Long id) {
        Hackathon hackathon = get(id);
        hackathon.setApproved(true);
        return hackathonRepository.save(hackathon);
    }

    private CriteriaQuery<Hackathon> getHackathonsListQuery(CriteriaBuilder criteriaBuilder, HackathonSearchForm form) {
        CriteriaQuery<Hackathon> query = criteriaBuilder.createQuery(Hackathon.class);
        Root<Hackathon> root = query.from(Hackathon.class);
        query.select(root);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.isFalse(root.get("deleted")));
        predicates.add(criteriaBuilder.isTrue(root.get("approved")));
        String name = form.getName();
        if (!StringUtils.isBlank(name)) {
            Expression<String> nameInLc = criteriaBuilder.lower(root.get("name"));
            predicates.add(criteriaBuilder.like(nameInLc, "%" + lowerCase(name.trim()) + "%"));
        }
        if (!StringUtils.isBlank(form.getCity())) {
            String city = form.getCity().trim().toLowerCase();
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("city")), "%" + StringUtils.lowerCase(city) + "%"));
        }
        if (!StringUtils.isBlank(form.getCountry())) {
            String country = form.getCountry().trim().toLowerCase();
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("country")), "%" + StringUtils.lowerCase(country) + "%"));
        }
        if (CollectionUtils.isNotEmpty(form.getSkills())) {
            Join<Hackathon, Skill> join = root.join("requiredSkills", JoinType.INNER);
            join.on(join.get("id").in(form.getSkills()));
            predicates.add(join.getOn());
        }

        long fromInMillis = form.getFrom() == null ? currentTimeMillis() : form.getFrom();
        Date from = new Date(fromInMillis);
        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), from));

        Date to = form.getTo() == null ? null : new Date(form.getTo());
        if (to != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), to));
        }
        query.distinct(true);
        query.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        return query;
    }

    private Hackathon buildHackathonFromCreateForm(HackathonCreateForm form, User user) {
        List<Long> requiredSkills = form.getRequiredSkills();
        if (requiredSkills == null)
            requiredSkills = Collections.emptyList();

        LocalDateTime registrationStart = getRegistrationLocalDateTimeFromForm(form, true);
        LocalDateTime registrationEnd = getRegistrationLocalDateTimeFromForm(form, false);

        Date startDate = new Date(form.getStart());
        Date endDate = new Date(form.getEnd());

        return Hackathon.builder()
                .name(form.getName().trim())
                .startDate(startDate)
                .endDate(endDate)
                .owner(user)
                .description(form.getDescription().trim())
                .country(StringUtils.capitalize(form.getCountry()))
                .city(StringUtils.capitalize(form.getCity()))
                .currency(form.getCurrency())
                .prizeFund(form.getPrizeFund())
                .requiredSkills(skillService.getByIds(requiredSkills))
                .deleted(false)
                .longitude(form.getLongitude())
                .latitude(form.getLatitude())
                .registrationStartDate(registrationStart)
                .registrationEndDate(registrationEnd)
                .approved(false)
                .build();
    }

    private void setHackathonNewValues(HackathonUpdateForm form, Hackathon hackathon) {
        Date start = new Date(form.getStartDate());
        Date end = new Date(form.getEndDate());
        hackathon.setStartDate(start);
        hackathon.setEndDate(end);

        if (form.getLogo() != null) {
            FileInfo file = fileInfoService.get(form.getLogo());
            hackathon.setLogo(file);
        }

        String name = form.getName();
        hackathon.setName(name.trim());
        hackathon.setDescription(form.getDescription().trim());

        String country = StringUtils.capitalize(form.getCountry());
        hackathon.setCountry(country);
        String city = StringUtils.capitalize(form.getCity());
        hackathon.setCity(city);
        hackathon.setLatitude(form.getLatitude());
        hackathon.setLongitude(form.getLongitude());

        Long regStartDate = form.getRegistrationStartDate();
        Long regEndDate = form.getRegistrationEndDate();
        hackathon.setRegistrationEndDate(epochToLocalDateTime(regEndDate));
        hackathon.setRegistrationStartDate(epochToLocalDateTime(regStartDate));

        List<Long> requiredSkills = form.getRequiredSkills();
        if (requiredSkills != null && !requiredSkills.isEmpty()) {
            hackathon.setRequiredSkills(skillService.getByIds(requiredSkills));
        }
    }

}
