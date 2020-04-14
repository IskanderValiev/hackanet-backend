package com.hackanet.services;

import com.hackanet.exceptions.BadRequestException;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.json.forms.JobExperienceCreateForm;
import com.hackanet.json.forms.JobExperienceUpdateForm;
import com.hackanet.models.Company;
import com.hackanet.models.JobExperience;
import com.hackanet.models.user.Portfolio;
import com.hackanet.models.user.User;
import com.hackanet.repositories.JobExperienceRepository;
import com.hackanet.services.skill.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

import static com.hackanet.security.utils.SecurityUtils.*;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/8/19
 */
@Service
public class JobExperienceServiceImpl implements JobExperienceService {

    @Autowired
    private JobExperienceRepository jobExperienceRepository;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private SkillService skillService;
    @Autowired
    private PortfolioService portfolioService;

    @Override
    public JobExperience addForPortfolio(User user, JobExperienceCreateForm form) {
        Date from = new Date(form.getFrom());
        Date to = new Date(form.getTo());
        if (from.after(to)) {
            throw new BadRequestException("From date is after to date");
        }

        Company company = companyService.get(form.getCompanyId());
        Portfolio portfolio = portfolioService.getByUserId(user.getId());
        JobExperience jobExperience = JobExperience.builder()
                .company(company)
                .description(form.getDescription())
                .startDate(from)
                .endDate(to)
                .portfolio(portfolio)
                .build();

        List<Long> technologies = form.getTechnologies();
        if (isNotEmpty(technologies)) {
            jobExperience.setTechnologiesUsed(skillService.getByIds(technologies));
        }

        return jobExperienceRepository.save(jobExperience);
    }

    @Override
    public void delete(User user, Long id) {
        JobExperience jobExperience = get(id);
        Portfolio portfolio = jobExperience.getPortfolio();
        checkPortfolioAccess(portfolio, user);
        jobExperienceRepository.delete(jobExperience);
    }

    @Override
    public JobExperience get(Long id) {
        return jobExperienceRepository.findById(id).orElseThrow(() -> new NotFoundException("Job experience with id = " + id + " not found"));
    }

    @Override
    public JobExperience update(Long id, User user, JobExperienceUpdateForm form) {
        JobExperience jobExperience = get(id);
        Portfolio portfolio = jobExperience.getPortfolio();
        checkPortfolioAccess(portfolio, user);

        jobExperience = JobExperience.builder()
                .company(companyService.get(form.getCompanyId()))
                .startDate(new Date(form.getFrom()))
                .endDate(new Date(form.getTo()))
                .technologiesUsed(skillService.getByIds(form.getTechnologies()))
                .description(form.getDescription())
                .build();

        return jobExperienceRepository.save(jobExperience);
    }

    @Override
    public List<JobExperience> getByIdsIn(List<Long> jobExperience) {
        return jobExperienceRepository.findAllByIdIn(jobExperience);
    }
}
