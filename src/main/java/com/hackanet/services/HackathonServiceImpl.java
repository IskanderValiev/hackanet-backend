package com.hackanet.services;

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
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.hackanet.security.utils.SecurityUtils.checkHackathonAccess;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/19/19
 */
@Service
public class HackathonServiceImpl implements HackathonService {

    private static final Integer DEFAULT_LIMIT = 10;

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

    @Override
    public List<Hackathon> getAll() {
        return hackathonRepository.findAll();
    }

    @Override
    @Transactional
    public Hackathon save(User user, HackathonCreateForm form) {
        Date start = form.getStart();
        Date end = form.getEnd();
        if (start.after(end))
            throw new BadRequestException("Start date is after end date");

        List<Long> requiredSkills = form.getRequiredSkills();
        if (requiredSkills == null)
            requiredSkills = Collections.emptyList();

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
     * @throws com.hackanet.exceptions.ForbiddenException
     *      if user is not an owner of the hackathon
     *
     * */
    @Override
    public Hackathon update(Long id, User user, HackathonUpdateForm form) {
        Hackathon hackathon = get(id);
        checkHackathonAccess(hackathon, user);

        Date start = form.getStart();
        Date end = form.getEnd();
        if (start != null && end != null) {
            if (start.after(end)) {
                throw new BadRequestException("Start date is after end date");
            }
        }
        if (form.getLogo() != null) {
            FileInfo file = fileInfoService.get(form.getLogo());
            hackathon.setLogo(file);
        }
        if (start != null) {
            if (start.before(new Date(System.currentTimeMillis()))) {
                throw new BadRequestException("Start date is in the past");
            }
            hackathon.setStartDate(start);
        }
        if (end != null)
            hackathon.setEndDate(end);

        String name = form.getName();
        if (!StringUtils.isBlank(name)) {
            hackathon.setName(name.trim());
            hackathon.setNameLc(form.getName().trim().toLowerCase());
        }

        String description = form.getDescription();
        if (!StringUtils.isBlank(description))
            hackathon.setDescription(description.trim());

        String country = StringUtils.capitalize(form.getCountry());
        if (!StringUtils.isBlank(country))
            hackathon.setCountry(country);

        String city = StringUtils.capitalize(form.getCity());
        if (!StringUtils.isBlank(city))
            hackathon.setCity(city);

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
            form.setLimit(DEFAULT_LIMIT);
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
        Date from = form.getFrom();
        if (from == null)
            from = new Date(System.currentTimeMillis());
        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), from));
        Date to = form.getTo();
        if (to != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), to));
        }
        query.distinct(true);
        query.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        return query;
    }

}
