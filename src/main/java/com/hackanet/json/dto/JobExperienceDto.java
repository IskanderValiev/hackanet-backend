package com.hackanet.json.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hackanet.models.Skill;
import lombok.Builder;
import lombok.Data;

import java.sql.Date;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/8/19
 */
@Builder
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class JobExperienceDto {
    private Long id;
    private CompanyDto company;
    private Long from;
    private Long to;
    private String description;
    private List<SkillDto> technologies;
}
