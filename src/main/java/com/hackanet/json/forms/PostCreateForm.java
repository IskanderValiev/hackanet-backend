package com.hackanet.json.forms;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hackanet.application.AppConstants;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/22/19
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PostCreateForm extends CreateForm {
    @NotNull
    @NotEmpty
    private String title;

    @NotNull
    @NotEmpty
    @Length(max = 1024)
    private String content;

    private List<Long> images;
    private Long hackathon;
    private Boolean sendImportanceRequest;
}
