package com.hackanet.json.forms;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.common.collect.Lists;
import com.hackanet.application.AppConstants;
import com.hackanet.models.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/19/19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class HackathonCreateForm extends CreateForm {

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    private Long start;

    @NotNull
    private Long end;
    private Long logoId;

    @NotNull
    private String description;

    @NotNull
    private String country;

    @NotNull
    private String city;
    private List<Long> requiredSkills;

    @NotNull
    private Integer prizeFund;

    @NotNull
    private Currency currency;

    @Max(90)
    @Min(-90)
    @NotNull
    private Double latitude;

    @Max(180)
    @Min(-180)
    @NotNull
    private Double longitude;

    private Long registrationStartDate;
    private Long registrationEndDate;

    public List<Long> getRequiredSkills() {
        return requiredSkills == null ? Lists.newArrayList() : requiredSkills;
    }

    public Long getLogoId() {
        return logoId == null ? AppConstants.DEFAULT_HACKATHON_IMAGE_ID : logoId;
    }
}
