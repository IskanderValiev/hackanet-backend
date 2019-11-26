package com.hackanet.json.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hackanet.models.enums.TeamType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/1/19
 */
@Data
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TeamDto {
    private Long id;
    private List<TeamParticipantDto> participants;
    private List<SkillDto> skillsLookingFor;
    private String name;
    private TeamType teamType;
    private Boolean lookingForHackers;
    private Boolean actual;
}
