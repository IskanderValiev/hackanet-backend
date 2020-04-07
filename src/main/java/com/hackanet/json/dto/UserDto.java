package com.hackanet.json.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/20/19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserDto {
    private Long id;
    private String email;
    private String phone;
    private String about;
    private String university;
    private String name;
    private String lastname;
    private String country;
    private String city;
    private FileInfoDto picture;
    private List<SkillDto> skills;
    private Long reviewCount;
    private Double rating;
    private Long lastRequestTime;
    private PositionDto position;
}
