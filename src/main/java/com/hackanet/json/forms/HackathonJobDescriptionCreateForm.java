package com.hackanet.json.forms;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/8/19
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class HackathonJobDescriptionCreateForm {
    @NotNull
    private Long userId;
    @NotNull
    private Long hackathonId;
    @NotNull
    private Long teamId;
    private String description;
}
