package com.hackanet.json.forms;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
public class CompanyCreateForm {
    @NotNull
    @NotEmpty
    @Pattern(regexp = Patterns.VALID_EMAIL_REGEX, message = HttpResponse.BAD_EMAIL)
    private String email;
    @NotNull
    @NotEmpty
    @Pattern(regexp = Patterns.VALID_PASSWORD_REGEX, message = HttpResponse.BAD_PASSWORD)
    private String password;
    @NotNull
    @NotEmpty
    private String name;
    @NotNull
    @NotEmpty
    // TODO: 11/7/19 add maxlength
    private String description;
    @NotNull
    @NotEmpty
    private String country;
    @NotNull
    @NotEmpty
    private String city;
    private List<Long> technologies;
    private Long logoId;
    private CompanyType companyType = CompanyType.SOFTWARE_ENGINEERING;
}
