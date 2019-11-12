package com.hackanet.json.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/7/19
 */
@Builder
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CompanyOwnerTokenDto {
    private TokenDto tokenDto;
    private Long companyId;
}
