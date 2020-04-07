package com.hackanet.json.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hackanet.models.FileInfo;
import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/22/19
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private UserSimpleDto author;
    private HackathonSimpleDto hackathon;
    private FileInfoDto picture;
    private Long date;
    private Long likesCount;
    private Long dislikesCount;
    private Long views;
    private Integer commentsCount;
}
