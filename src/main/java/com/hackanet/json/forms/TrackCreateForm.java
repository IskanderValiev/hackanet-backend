package com.hackanet.json.forms;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/20/20
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TrackCreateForm {

    @NotEmpty
    @NotNull
    @Length(max = 255)
    private String name;

    @NotEmpty
    @NotNull
    @Length(max = 1000)
    private String description;

    @NotNull
    private Long hackathonId;
}
