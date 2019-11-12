package com.hackanet.json.forms;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hackanet.application.HttpResponse;
import com.hackanet.application.Patterns;
import com.hackanet.models.enums.CompanyType;
import lombok.Data;

import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/7/19
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CompanyUpdateForm {
    @Pattern(regexp = Patterns.VALID_EMAIL_REGEX, message = HttpResponse.BAD_EMAIL)
    private String email;
    private String name;
    private String country;
    private String city;
    private List<Long> technologies;
    private CompanyType companyType;
}
