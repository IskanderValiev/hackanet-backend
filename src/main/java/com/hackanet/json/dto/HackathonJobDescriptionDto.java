package com.hackanet.json.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/8/19
 */
@Data
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class HackathonJobDescriptionDto {
    private Long id;
    private TeamDto team;
    private HackathonDto hackathon;
    private String description;
}
