package com.hackanet.json.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/23/20
 */
@Builder
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ChatUserDto {

    private Long id;

    private String name;

    private String lastname;

    private FileInfoDto picture;

    private Boolean isTyping;

    private Long chatIsTypingIn;
}
