package com.hackanet.json.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hackanet.models.enums.ConnectionInvitationStatus;
import lombok.Builder;
import lombok.Data;

/**
 * @author Iskander Valiev
 * created by isko
 * on 12/3/19
 */
@Data
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ConnectionInvitationDto {
    private Long id;
    private UserSimpleDto user;
    private UserSimpleDto invitedUser;
    private ConnectionInvitationStatus status;
}
