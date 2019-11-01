package com.hackanet.json.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hackanet.models.enums.JoinToTeamRequestStatus;
import lombok.Builder;
import lombok.Data;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/1/19
 */
@Builder
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class JoinToTeamRequestDto {
    private Long id;
    private UserSimpleDto user;
    private JoinToTeamRequestStatus status;
    private Long teamId;
    private String message;
}
