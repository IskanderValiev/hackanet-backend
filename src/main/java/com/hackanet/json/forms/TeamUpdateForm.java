package com.hackanet.json.forms;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hackanet.models.enums.TeamType;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/1/19
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TeamUpdateForm {

    @NotEmpty
    private String name;

    @NotNull
    private List<Long> skillsLookingFor;

    @NotNull
    private Long teamLeader;

    @NotNull
    private TeamType teamType;

    @NotNull
    private Boolean lookingForHackers;
}
