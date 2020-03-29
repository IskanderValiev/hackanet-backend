package com.hackanet.json.forms;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hackanet.exceptions.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;


/**
 * @author Iskander Valiev
 * created by isko
 * on 10/20/19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class HackathonUpdateForm {
    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @Start(endField = "endDate")
    private Long startDate;

    @NotNull
    @End
    private Long endDate;

    @NotNull
    @NotFormatted
    private String description;

    private Long logo;

    @NotNull
    private String country;

    @NotNull
    private String city;
    private List<Long> requiredSkills;

    @NotNull
    private Integer prizeFund;

    @NotNull
    private Currency currency;

    @NotNull
    @Max(value = 90, message = "Latitude must be less 90")
    @Min(value = -90, message = "Latitude must be more -90")
    private Double latitude;

    @NotNull
    @Max(value = 180, message = "Latitude must be less 180")
    @Min(value = -180, message = "Latitude must be more -180")
    private Double longitude;

    @NotNull
    @Start(endField = "registrationEndDate")
    private Long registrationStartDate;

    @NotNull
    @End
    private Long registrationEndDate;
}
