package com.hackanet.services;

import com.hackanet.application.AppConstants;
import com.hackanet.components.Profiling;
import com.hackanet.exceptions.BadFormTypeException;
import com.hackanet.exceptions.BadRequestException;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.json.forms.*;
import com.hackanet.models.FileInfo;
import com.hackanet.models.Hackathon;
import com.hackanet.models.Skill;
import com.hackanet.models.User;
import com.hackanet.repositories.HackathonRepository;
import com.hackanet.services.chat.ChatService;
import com.hackanet.utils.DateTimeUtil;
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
import static com.hackanet.utils.DateTimeUtil.*;
import static java.lang.System.currentTimeMillis;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.capitalize;
import static org.apache.commons.lang.StringUtils.lowerCase;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/19/19
 */
@Profiling(enabled = true)
@Service
public class HackathonServiceImpl extends AbstractManageableService<Hackathon> implements HackathonService {

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

    @Cacheable(value = "hackathons")
    @Override
    @Transactional
    public List<Hackathon> getAll() {
        return hackathonRepository.findAll();
    }

    @CachePut("hackathons")
    @Override
    @Transactional
    public Hackathon save(User user, HackathonCreateForm form) {
        Hackathon hackathon = buildFromForm(user, form);
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
    public Hackathon update(Long id, User user, UpdateForm form) {
        validateUpdateForm(form);
        final Hackathon hackathon = get(id);
        checkHackathonAccess(hackathon, user);

        HackathonUpdateForm updateForm = (HackathonUpdateForm) form;
        fillUpData(updateForm, hackathon);
        Date start = new Date(updateForm.getStartDate());
        Date end = new Date(updateForm.getEndDate());
        validateHackathonDates(start, end);
        hackathon.setStartDate(start);
        hackathon.setEndDate(end);

        Optional.of(updateForm.getLogo()).ifPresent(logo -> {
            FileInfo file = fileInfoService.get(updateForm.getLogo());
            hackathon.setLogo(file);
        });

        Long regStartDate = updateForm.getRegistrationStartDate();
        Long regEndDate = updateForm.getRegistrationEndDate();
        validateRegistrationDates(regStartDate, regEndDate, start, end);
        hackathon.setRegistrationEndDate(epochToLocalDateTime(regEndDate));
        hackathon.setRegistrationStartDate(epochToLocalDateTime(regStartDate));

        List<Long> requiredSkills = updateForm.getRequiredSkills();
        if (isNotEmpty(requiredSkills)) {
            hackathon.setRequiredSkills(skillService.getByIds(requiredSkills));
        }
        return hackathonRepository.save(hackathon);
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
        String query = "select h.* from hackathons h inner join hackathon_participants_table hpt on h.id = hpt.hackathon_id where hpt.user_id in (select c.connection_id from connections c where c.user_id = :userId) and h.id not in (select hpts.hackathon_id from hackathon_participants_table hpts where hpts.user_id = :userId);";
        Query nativeQuery = entityManager
                .createNativeQuery(query, Hackathon.class)
                .setParameter("userId", user.getId());
        List<Hackathon> resultList = nativeQuery.getResultList();
        return resultList;
    }

    private CriteriaQuery<Hackathon> getHackathonsListQuery(CriteriaBuilder criteriaBuilder, HackathonSearchForm form) {
        CriteriaQuery<Hackathon> query = criteriaBuilder.createQuery(Hackathon.class);
        Root<Hackathon> root = query.from(Hackathon.class);
        query.select(root);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.isFalse(root.get("deleted")));
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
        if (isNotEmpty(form.getSkills())) {
            Join<Hackathon, Skill> join = root.join("requiredSkills", JoinType.INNER);
            join.on(join.get("id").in(form.getSkills()));
            predicates.add(join.getOn());
        }
        Date from = form.getFrom();
        if (from == null)
            from = new Date(currentTimeMillis());
        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), from));
        Date to = form.getTo();
        if (to != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), to));
        }
        query.distinct(true);
        query.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        return query;
    }

    private Hackathon buildFromForm(User user, HackathonCreateForm form) {
        Date start = new Date(form.getStart());
        Date end = new Date(form.getEnd());
        if (start.after(end))
            throw new BadRequestException("Start date is after end date");

        List<Long> requiredSkills = form.getRequiredSkills();
        if (requiredSkills == null)
            requiredSkills = Collections.emptyList();

        LocalDateTime registrationStartDate = getRegistrationDate(form.getRegistrationStartDate(), true, end);
        LocalDateTime registrationEndDate = getRegistrationDate(form.getRegistrationEndDate(), false, end);

        final Hackathon hackathon = Hackathon.builder()
                .name(form.getName().trim())
                .startDate(start)
                .endDate(end)
                .owner(user)
                .description(form.getDescription().trim())
                .country(capitalize(form.getCountry()))
                .city(capitalize(form.getCity()))
                .currency(form.getCurrency())
                .prizeFund(form.getPrizeFund())
                .requiredSkills(skillService.getByIds(requiredSkills))
                .deleted(false)
                .longitude(form.getLongitude())
                .latitude(form.getLatitude())
                .registrationStartDate(registrationStartDate)
                .registrationEndDate(registrationEndDate)
                .build();

        if (form.getLogoId() != null) {
            FileInfo logo = fileInfoService.get(form.getLogoId());
            hackathon.setLogo(logo);
        }
        return hackathon;
    }

    @Override
    public void validateCreateForm(CreateForm form) {
        if (!(form instanceof HackathonCreateForm))
            throw new BadFormTypeException(form.getClass().getTypeName() + " is not a hackathon create form.");
    }

    @Override
    public void validateUpdateForm(UpdateForm form) {
        if (!(form instanceof HackathonUpdateForm))
            throw new BadFormTypeException(form.getClass().getTypeName() + " is not a hackathon update form.");
    }
}
