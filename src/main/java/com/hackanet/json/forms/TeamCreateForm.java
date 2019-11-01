package com.hackanet.json.forms;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/31/19
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TeamCreateForm {
    @NotNull
    @NotEmpty
    @ApiModelProperty(required = true)
    private String name;
    @NotNull
    @ApiModelProperty(required = true)
    private List<Long> participantsIds;
    @NotNull
    @ApiModelProperty(required = true)
    // TODO: 11/1/19 ??
    private Long hackathonId;
    @NotNull
    @ApiModelProperty(required = true)
    private Long teamLeader;
    private List<Long> skillsLookingFor;
}
