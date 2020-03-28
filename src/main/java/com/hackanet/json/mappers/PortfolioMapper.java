package com.hackanet.json.mappers;

import com.hackanet.json.dto.HackathonJobDescriptionDto;
import com.hackanet.json.dto.PortfolioDto;
import com.hackanet.models.JobExperience;
import com.hackanet.models.Portfolio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/8/19
 */
@Component
public class PortfolioMapper implements Mapper<Portfolio, PortfolioDto> {

    @Autowired
    private JobExperienceMapper jobExperienceMapper;
    @Autowired
    private HackathonJobDescriptionMapper hackathonJobDescriptionMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public PortfolioDto map(Portfolio from) {
        if (from == null) {
            return null;
        }
        List<HackathonJobDescriptionDto> jobDescription = hackathonJobDescriptionMapper.map(from.getHackathonJobDescriptions());
        List<JobExperience> jobExperience = from.getJobExperience();
        PortfolioDto dto = PortfolioDto.builder()
                .id(from.getId())
                .user(userMapper.map(from.getUser()))
                .hackathonJobDescription(jobDescription == null ? Collections.emptyList() : jobDescription)
                .jobExperience(jobExperienceMapper.map(jobExperience == null ? Collections.emptyList() : jobExperience))
                .build();
        return dto;
    }
}
