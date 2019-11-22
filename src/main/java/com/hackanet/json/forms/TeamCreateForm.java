package com.hackanet.json.forms;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hackanet.models.enums.TeamType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
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
    private Long hackathonId;
    private Long teamLeader;
    private List<Long> skillsLookingFor;
    private TeamType teamType;

    public List<Long> getParticipantsIds() {
        return participantsIds == null ? new ArrayList<>() : participantsIds;
    }

    public TeamType getTeamType() {
        return teamType == null ? TeamType.CONSTANT : teamType;
    }
}
