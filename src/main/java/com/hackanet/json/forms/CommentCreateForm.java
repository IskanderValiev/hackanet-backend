package com.hackanet.json.forms;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/9/20
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CommentCreateForm {

    @NotEmpty
    @NotNull
    private String text;

    @NotNull
    private Long userId;

    @NotNull
    private Long postId;

    private Long replyParentId;
}
