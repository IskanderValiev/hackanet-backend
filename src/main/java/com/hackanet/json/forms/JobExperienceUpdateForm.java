package com.hackanet.json.forms;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/8/19
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class JobExperienceUpdateForm {
    @NotNull
    private Long companyId;
    @NotNull
    private Long from;
    @NotNull
    private Long to;
    private String description;
    private List<Long> technologies;
}
