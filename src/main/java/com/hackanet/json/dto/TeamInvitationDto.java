package com.hackanet.json.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hackanet.models.enums.TeamInvitationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/25/19
 */
@Builder
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TeamInvitationDto {
    private Long id;
    private UserSimpleDto user;
    private TeamSimpleDto team;
    private Long datetime;
    private TeamInvitationStatus status;
}
