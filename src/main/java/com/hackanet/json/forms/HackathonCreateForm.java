package com.hackanet.json.forms;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hackanet.models.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Date;
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
public class HackathonCreateForm {
    @NotNull
    @NotEmpty
    private String name;
    @NotNull
    private Date start;
    @NotNull
    private Date end;
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
}
