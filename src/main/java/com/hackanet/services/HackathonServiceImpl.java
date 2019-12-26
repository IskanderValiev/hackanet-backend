package com.hackanet.services;

import com.hackanet.application.AppConstants;
import com.hackanet.components.Profiling;
import com.hackanet.exceptions.BadRequestException;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.json.forms.HackathonCreateForm;
import com.hackanet.json.forms.HackathonSearchForm;
import com.hackanet.json.forms.HackathonUpdateForm;
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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.hackanet.security.utils.SecurityUtils.checkHackathonAccess;
import static com.hackanet.utils.DateTimeUtil.*;
import static java.lang.System.currentTimeMillis;

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
        Date start = new Date(form.getStart());
        Date end = new Date(form.getEnd());
        if (start.after(end))
            throw new BadRequestException("Start date is after end date");

        List<Long> requiredSkills = form.getRequiredSkills();
        if (requiredSkills == null)
            requiredSkills = Collections.emptyList();

        LocalDateTime registrationStartDate = form.getRegistrationStartDate() == null
                ? LocalDateTime.now()
                : epochToLocalDateTime(form.getRegistrationStartDate());
        LocalDateTime registrationEndDate = form.getRegistrationEndDate() == null
                ? end.toLocalDate().minusDays(1).atTime(23, 59)
                : epochToLocalDateTime(form.getRegistrationEndDate());

        Hackathon hackathon = Hackathon.builder()
                .name(form.getName().trim())
                .nameLc(form.getName().trim().toLowerCase())
                .startDate(start)
                .endDate(end)
                .owner(user)
                .description(form.getDescription().trim())
                .country(StringUtils.capitalize(form.getCountry()))
                .city(StringUtils.capitalize(form.getCity()))
                .currency(form.getCurrency())
                .prize(form.getPrizeFund())
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
        Hackathon hackathon = get(id);
        checkHackathonAccess(hackathon, user);

        Date start = new Date(form.getStart());
        Date end = new Date(form.getEnd());
        if (start.after(end)) {
            throw new BadRequestException("Start date is after end date");
        }
        if (start.before(new Date(currentTimeMillis()))) {
            throw new BadRequestException("Start date is in the past");
        }
        hackathon.setStartDate(start);
        hackathon.setEndDate(end);

        if (form.getLogo() != null) {
            FileInfo file = fileInfoService.get(form.getLogo());
            hackathon.setLogo(file);
        }

        String name = form.getName();
        hackathon.setName(name.trim());
        hackathon.setNameLc(form.getName().trim().toLowerCase());
        hackathon.setDescription(form.getDescription().trim());

        String country = StringUtils.capitalize(form.getCountry());
        hackathon.setCountry(country);
        String city = StringUtils.capitalize(form.getCity());
        hackathon.setCity(city);

        if (form.getLatitude() > 90 || form.getLatitude() < -90)
            throw new BadRequestException("latitude must be >= -90 and <= 90");
        hackathon.setLatitude(form.getLatitude());
        if (form.getLongitude() > 180 || form.getLongitude() < -180)
            throw new BadRequestException("longitude must be >= -180 and <= 180");
        hackathon.setLongitude(form.getLongitude());

        Long regStartDate = form.getRegistrationStartDate();
        Long regEndDate = form.getRegistrationEndDate();
        if (regStartDate > regEndDate)
            throw new BadRequestException("Registration Start Date is after End Date");
        if (regEndDate < System.currentTimeMillis())
            throw new BadRequestException("Registration End Date is in the past");
        if (new Timestamp(regStartDate).after(start))
            throw new BadRequestException("Registration Start Date must be before hackathon start date");
        if (new Timestamp(regEndDate).after(end))
            throw new BadRequestException("Registration End Date must be before hackathon end date");
        hackathon.setRegistrationEndDate(epochToLocalDateTime(regEndDate));
        hackathon.setRegistrationStartDate(epochToLocalDateTime(regStartDate));

        List<Long> requiredSkills = form.getRequiredSkills();
        if (requiredSkills != null && !requiredSkills.isEmpty()) {
            hackathon.setRequiredSkills(skillService.getByIds(requiredSkills));
        }
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
        String query = "select h.* from hackathons h inner join hackathon_participants_table hpt on h.id = hpt.hackathon_id where hpt.user_id in (select c.connection_id from connections c where c.user_id = :userId) and h.id not in (select hpts.hackathon_id from hackathon_participants_table hpts where hpts.user_id = :userId);";
        Query nativeQuery = entityManager
                .createNativeQuery(query, Hackathon.class)
                .setParameter("userId", user.getId());
        List<Hackathon> resultList = nativeQuery.getResultList();
        return resultList;
    }

    @Override
    public List<Hackathon> getHackathonsListByUser(User user) {
        return hackathonRepository.findByParticipantsContaining(user);
    }

    private CriteriaQuery<Hackathon> getHackathonsListQuery(CriteriaBuilder criteriaBuilder, HackathonSearchForm form) {
        CriteriaQuery<Hackathon> query = criteriaBuilder.createQuery(Hackathon.class);
        Root<Hackathon> root = query.from(Hackathon.class);
        query.select(root);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.isFalse(root.get("deleted")));
        String name = form.getName();
        if (!StringUtils.isBlank(name)) {
            name = name.toLowerCase();
            predicates.add(criteriaBuilder.like(root.get("nameLc"), "%" + name + "%"));
        }
        if (!StringUtils.isBlank(form.getCity())) {
            String city = form.getCity().trim().toLowerCase();
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("city")), "%" + StringUtils.lowerCase(city) + "%"));
        }
        if (!StringUtils.isBlank(form.getCountry())) {
            String country = form.getCountry().trim().toLowerCase();
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("country")), "%" + StringUtils.lowerCase(country) + "%"));
        }
        if (form.getSkills() != null && !form.getSkills().isEmpty()) {
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

}
