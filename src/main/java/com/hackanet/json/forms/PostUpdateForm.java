package com.hackanet.json.forms;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hackanet.application.AppConstants;
import com.hackanet.application.HttpResponse;
import io.swagger.annotations.ApiModelProperty;
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
@Builder
@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PostUpdateForm {

    @NotNull(message = HttpResponse.INVALID_PARAM)
    @NotEmpty(message = HttpResponse.INVALID_PARAM)
    @ApiModelProperty(required = true)
    private String title;

    @NotNull(message = HttpResponse.INVALID_PARAM)
    @NotEmpty(message = HttpResponse.INVALID_PARAM)
    @ApiModelProperty(required = true)
    @Length(max = 1024)
    private String content;

    private List<Long> images;

    @NotNull
    private Boolean important;

    @NotNull
    private Long pictureId;
}

