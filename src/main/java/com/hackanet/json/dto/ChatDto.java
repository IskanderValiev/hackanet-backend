package com.hackanet.json.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hackanet.models.enums.ChatType;
import lombok.*;

import java.util.List;
import java.util.Set;

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
    private Set<UserSimpleDto> participants;
    private Set<UserSimpleDto> admin;
    private ChatType chatType;
}
