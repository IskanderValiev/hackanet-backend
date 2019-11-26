package com.hackanet.json.forms;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/20/19
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserReviewCreateForm {

    @NotNull
    private Long reviewedUserId;

    @Max(5)
    @Positive
    private Integer mark;

    private String reviewMessage;

    @NotNull
    private Long teamId;

    private Boolean anonymously = false;
}
