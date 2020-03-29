package com.hackanet.json.forms;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hackanet.application.Patterns;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/23/20
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SponsorUpdateForm {

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    private Long logoId;

    @Pattern(regexp = Patterns.VALID_URL, message = "bad-url")
    private String link;
}
