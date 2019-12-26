package com.hackanet.json.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.sql.Timestamp;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/24/19
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ChatMessageDto {
    private Long id;
    private UserSimpleDto sender;
    private Long messageTime;
    private String text;
}
