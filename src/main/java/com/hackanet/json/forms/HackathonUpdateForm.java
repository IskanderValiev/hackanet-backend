package com.hackanet.json.forms;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Set;


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
    private Long start;
    @NotNull
    private Long end;
    @NotNull
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
    private Double latitude;
    @NotNull
    private Double longitude;
    private Long registrationStartDate;
    private Long registrationEndDate;
    private Set<Long> partners;
}
