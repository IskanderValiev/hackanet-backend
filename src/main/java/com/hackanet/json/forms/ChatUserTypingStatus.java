package com.hackanet.json.forms;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/23/20
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ChatUserTypingStatus {

    private Long userId;

    private Boolean isTyping;
}
