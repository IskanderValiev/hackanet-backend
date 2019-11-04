package com.hackanet.json.forms;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/24/19
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ChatMessageSaveForm {
    @NotNull
    @ApiModelProperty(required = true)
    private Long senderId;
    @NotNull
    @ApiModelProperty(required = true)
    private Long chatId;
    @NotNull
    @NotEmpty
    @Length(max = 1000)
    @ApiModelProperty(required = true)
    private String text;
    private List<Long> attachments;
}
