package com.hackanet.json.forms;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hackanet.application.HttpResponse;
import com.hackanet.application.Patterns;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
public class UserRegistrationForm {
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
    @Pattern(regexp = Patterns.VALID_PHONE_REGEX, message = HttpResponse.BAD_PHONE)
    private String phone;
    @NotNull
    @NotEmpty
    private String name;
    @NotNull
    @NotEmpty
    private String lastname;
    @NotNull
    @NotEmpty
    private String country;
    @NotNull
    @NotEmpty
    private String city;
    private List<Long> skills;
}
