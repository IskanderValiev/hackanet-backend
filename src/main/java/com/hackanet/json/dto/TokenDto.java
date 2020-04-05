package com.hackanet.json.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/20/19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TokenDto implements Serializable {

    private static final long serialVersionUID = -5534236268551559040L;

    private Long userId;
    private String role;
    private String accessToken;
    private Long accessTokenExpiresAt;
    private String refreshToken;
    private Long refreshTokenExpiresAt;
}
