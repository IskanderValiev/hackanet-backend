package com.hackanet.push.payload;

import lombok.*;

/**
 * @author Iskander Valiev
 * created by isko
 * on 12/7/19
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostPushPayload extends PushPayloadEntity {
    private String hackathonName;
}
