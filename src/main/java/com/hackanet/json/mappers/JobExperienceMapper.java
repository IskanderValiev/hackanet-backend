package com.hackanet.json.mappers;

import com.hackanet.json.dto.JobExperienceDto;
import com.hackanet.models.JobExperience;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/8/19
 */
@Component
public class JobExperienceMapper implements Mapper<JobExperience, JobExperienceDto> {

    @Autowired
    private CompanyMapper companyMapper;
    @Autowired
    private SkillMapper skillMapper;

    @Override
    public JobExperienceDto map(JobExperience from) {
        if (from == null) {
            return null;
        }
        JobExperienceDto dto = JobExperienceDto.builder()
                .id(from.getId())
                .company(companyMapper.map(from.getCompany()))
                .from(from.getStartDate().getTime())
                .to(from.getEndDate().getTime())
                .description(from.getDescription())
                .technologies(skillMapper.map(from.getTechnologiesUsed()))
                .build();
        return dto;
    }
}
