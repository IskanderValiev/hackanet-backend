package com.hackanet.json.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/11/19
 */
@Data
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserNotificationSettingsDto {
    private Long id;
    private Long userId;
    private Boolean emailEnabled;
    private Boolean pushEnabled;
    private Long dontDisturbFrom;
    private Long dontDisturbTo;
}
