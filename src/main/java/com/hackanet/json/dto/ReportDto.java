package com.hackanet.json.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hackanet.models.enums.ReportEntityType;
import com.hackanet.models.enums.ReportStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/10/20
 */
@Builder
@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ReportDto {

    private Long id;

    private UserSimpleDto user;

    private ReportStatus reportStatus;

    private ReportEntityType type;

    private Long entityId;
}
