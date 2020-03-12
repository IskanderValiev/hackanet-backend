package com.hackanet.json.forms;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hackanet.models.enums.ReportEntityType;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/10/20
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ReportCreateFrom {

    @NotNull
    private Long entityId;

    @NotNull
    private ReportEntityType type;
}
