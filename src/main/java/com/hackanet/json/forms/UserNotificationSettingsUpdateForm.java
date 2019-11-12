package com.hackanet.json.forms;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/11/19
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserNotificationSettingsUpdateForm {
    @NotNull
    private Boolean emailEnabled;
    @NotNull
    private Boolean pushEnabled;
    private Long dontDisturbFrom;
    private Long dontDisturbTo;

}
