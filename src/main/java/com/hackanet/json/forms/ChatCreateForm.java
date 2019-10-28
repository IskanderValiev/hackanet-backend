package com.hackanet.json.forms;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

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
public class ChatCreateForm {
    private List<Long> participantsIds;
}
