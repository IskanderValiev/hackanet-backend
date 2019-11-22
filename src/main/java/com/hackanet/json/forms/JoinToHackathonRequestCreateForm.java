package com.hackanet.json.forms;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hackanet.models.enums.JoinType;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/21/19
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class JoinToHackathonRequestCreateForm {
    private Long entityId;
    @NotNull
    private JoinType joinType;
    @NotNull
    @ApiModelProperty(hidden = true)
    private Long hackathonId;
    private String message;
}
