package com.hackanet.json.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hackanet.models.enums.CompanyType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/7/19
 */
@Data
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CompanyDto {
    private Long id;
    private String name;
    private String description;
    private String country;
    private String city;
    private List<SkillDto> technologies;
    private CompanyType companyType;
    private FileInfoDto logo;
    private Boolean approved;
}
