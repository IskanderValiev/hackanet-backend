package com.hackanet.json.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.sql.Date;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/21/19
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class JoinToHackathonRequestDto {
    private Long id;
    private String message;
    private UserDto user;
    private HackathonDto hackathon;
    private Date date;
}
