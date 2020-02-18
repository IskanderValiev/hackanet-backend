package com.hackanet.json.forms;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hackanet.annotations.NotFormatted;
import com.hackanet.application.HttpResponse;
import com.hackanet.application.Patterns;
import com.hackanet.models.enums.CompanyType;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/7/19
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CompanyUpdateForm extends UpdateForm {
    @NotNull
    @NotEmpty
    @Pattern(regexp = Patterns.VALID_EMAIL_REGEX, message = HttpResponse.BAD_EMAIL)
    private String email;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @NotEmpty
    private String country;

    @NotNull
    @NotEmpty
    private String city;

    @NotNull
    @NotEmpty
    @NotFormatted
    private String description;

    private List<Long> technologies;
    private CompanyType type;
}
