package com.hackanet.json.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.Currency;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/19/19
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class HackathonDto {
    private Long id;
    private String name;
    private String description;
    private Date start;
    private Date end;
    private List<FileInfoDto> image;
    private UserDto owner;
    private FileInfoDto logo;
    private String country;
    private String city;
    private List<SkillDto> requiredSkills;
    private Integer prizeFund;
    private String currency;
    private List<UserDto> participants;
}
