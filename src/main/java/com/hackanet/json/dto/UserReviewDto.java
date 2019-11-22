package com.hackanet.json.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/21/19
 */
@Data
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserReviewDto {
    private Long id;
    private UserSimpleDto user;
    private TeamDto teamDto;
    private String reviewMessage;
    private Boolean anonymously;
    private Integer mark;
}
