package com.hackanet.json.forms;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/1/19
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TeamSearchForm {
    private Long hackathonId;
    private List<Long> requiredSkills;
    private String name;
    private Integer limit;
    private Integer page;
}
