package com.hackanet.services;

import com.hackanet.application.AppConstants;
import com.hackanet.exceptions.BadRequestException;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.json.dto.CompanyOwnerTokenDto;
import com.hackanet.json.forms.*;
import com.hackanet.models.Company;
import com.hackanet.models.Hackathon;
import com.hackanet.models.Skill;
import com.hackanet.models.User;
import com.hackanet.models.enums.CompanyType;
import com.hackanet.repositories.CompanyRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static com.hackanet.security.utils.SecurityUtils.checkCompanyAccess;
import static org.apache.commons.lang.StringUtils.lowerCase;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/7/19
 */
@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private SkillService skillService;
    @Autowired
    private FileInfoService fileInfoService;
    @Autowired
    private EntityManager entityManager;

    @Override
    public Company get(@NotNull Long id) {
        return companyRepository.findById(id).orElseThrow(() -> new NotFoundException("Company with id = " + id + " not found"));
    }

    @Transactional
    @Override
    public CompanyOwnerTokenDto registerCompany(@NotNull CompanyCreateForm form) {
        User user = userService.createForCompany(form);

        Company company = Company.builder()
                .admin(user)
                .country(form.getCountry())
                .name(form.getName())
                .description(form.getDescription())
                .city(form.getCity())
                .type(form.getCompanyType())
                .build();

        List<Long> technologies = form.getTechnologies();
        if (technologies != null && !technologies.isEmpty()) {
            company.setTechnologies(skillService.getByIds(technologies));
        }

        Long imageId = form.getLogoId();
        if (imageId != null) {
            company.setLogo(fileInfoService.get(imageId));
        }

        company = companyRepository.save(company);

        return CompanyOwnerTokenDto.builder()
                .companyId(company.getId())
                .tokenDto(userService.login(UserLoginForm.builder()
                        .email(user.getEmail())
                        .password(form.getPassword()).build()))
                .build();
    }

    @Override
    public Company update(Long id, User user, CompanyUpdateForm form) {
        Company company = get(id);
        checkCompanyAccess(company, user);

        String name = form.getName();
        if (StringUtils.isBlank(name)) {
            name = name.trim();
            company.setName(name);
        }

        String city = form.getCity();
        if (StringUtils.isBlank(city)) {
            city = city.trim();
            company.setCity(StringUtils.capitalize(city));
        }

        String country = form.getCountry();
        if (StringUtils.isBlank(country)) {
            country = country.trim();
            company.setCountry(StringUtils.capitalize(country));
        }

        CompanyType companyType = form.getCompanyType();
        if (companyType != null) {
            company.setType(companyType);
        }

        List<Long> technologies = form.getTechnologies();
        if (technologies != null && !technologies.isEmpty()) {
            company.setTechnologies(skillService.getByIds(technologies));
        }
        return companyRepository.save(company);
    }

    @Override
    public Company updateApprovedStatus(@NotNull Long companyId, Boolean approved) {
        Company company = get(companyId);
        company.setApproved(Boolean.TRUE.equals(approved));
        return companyRepository.save(company);
    }

    @Override
    public List<Company> getCompaniesList(CompanySearchForm form) {
        if (form.getLimit() == null)
            form.setLimit(AppConstants.DEFAULT_LIMIT);
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Company> companiesListQuery = getCompaniesListQuery(criteriaBuilder, form);
        TypedQuery<Company> query = entityManager.createQuery(companiesListQuery);
        if (form.getPage() != null) {
            query.setFirstResult((form.getPage() - 1) * form.getLimit());
        } else {
            form.setPage(1);
        }
        query.setMaxResults(form.getLimit());
        return query.getResultList();
    }

    @Override
    public Company createCompanyByName(String name, String country, String city) {
        Company company = Company.builder()
                .name(name)
                .country(country)
                .city(city)
                .type(CompanyType.ADDED_BY_NAME)
                .approved(false)
                .build();
        return companyRepository.save(company);
    }

    @Override
    public Company getByAdmin(User admin) {
        return companyRepository.findByAdmin(admin).orElseThrow(() -> new BadRequestException("The user is not a company owner."));
    }

    private CriteriaQuery<Company> getCompaniesListQuery(CriteriaBuilder criteriaBuilder, CompanySearchForm form) {
        CriteriaQuery<Company> query = criteriaBuilder.createQuery(Company.class);
        Root<Company> root = query.from(Company.class);
        query.select(root);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.isTrue(root.get("approved")));
        String name = form.getName();
        if (!StringUtils.isBlank(name)) {
            Expression<String> nameInLc = criteriaBuilder.lower(root.get("name"));
            predicates.add(criteriaBuilder.like(nameInLc, "%" + lowerCase(name.trim()) + "%"));
        }
        if (!StringUtils.isBlank(form.getCity())) {
            String city = form.getCity().trim().toLowerCase();
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("city")), "%" + lowerCase(city) + "%"));
        }
        if (!StringUtils.isBlank(form.getCountry())) {
            String country = form.getCountry().trim().toLowerCase();
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("country")), "%" + lowerCase(country) + "%"));
        }
        List<Long> technologies = form.getTechnologies();
        if (technologies != null && !form.getTechnologies().isEmpty()) {
            Join<Hackathon, Skill> join = root.join("technologies", JoinType.INNER);
            join.on(join.get("id").in(technologies));
            predicates.add(join.getOn());
        }
        query.distinct(true);
        query.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        return query;
    }
}
