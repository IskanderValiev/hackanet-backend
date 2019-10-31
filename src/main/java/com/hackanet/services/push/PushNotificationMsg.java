package com.hackanet.services.push;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hackanet.push.enums.PushType;
import lombok.Builder;
import lombok.Data;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/30/19
 */
@Data
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PushNotificationMsg {
    private PushType type;

    @JsonProperty("payload_entity")
    private Object payloadEntity;

    @JsonProperty("user_id")
    private Long toUserId;

    private Long fromUserId;

    private Integer priority = 1;

    private Integer retries = 0;
}
