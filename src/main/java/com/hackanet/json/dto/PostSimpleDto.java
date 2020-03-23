package com.hackanet.json.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/22/20
 */
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PostSimpleDto {

    private Long id;

    private Boolean important;

    private String title;

    private FileInfoDto picture;

    private Long date;

    private Long viewCount;

    private Long likesCount;

    private UserSimpleDto author;
}
