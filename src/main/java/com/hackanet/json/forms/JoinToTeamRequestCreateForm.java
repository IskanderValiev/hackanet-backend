package com.hackanet.json.forms;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/1/19
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class JoinToTeamRequestCreateForm {
    @NotNull
    @ApiModelProperty(required = true)
    private Long teamId;
    @Length(max = 255)
    private String message;
}
