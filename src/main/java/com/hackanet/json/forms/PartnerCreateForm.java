package com.hackanet.json.forms;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hackanet.application.HttpResponse;
import com.hackanet.application.Patterns;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/27/19
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PartnerCreateForm {
    @NotNull
    @NotEmpty
    private String name;
    @NotNull
    private Long logoId;
    @NotEmpty
    @NotNull
    @Pattern(regexp = Patterns.VALID_LINK_REGEX, message = HttpResponse.INVALID_PARAM)
    private String link;
}
