package com.hackanet.json.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/24/19
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ChatDto {
    private Long id;
    private List<UserSimpleDto> participants;
    private List<ChatMessageDto> messages;
    private UserSimpleDto admin;
}
