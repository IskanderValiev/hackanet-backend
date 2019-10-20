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

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/20/19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserLoginForm {
    @NotNull
    @NotEmpty
    @Pattern(regexp = Patterns.VALID_EMAIL_REGEX, message = HttpResponse.BAD_EMAIL)
    private String email;
    @NotNull
    @NotEmpty
    private String password;
}
