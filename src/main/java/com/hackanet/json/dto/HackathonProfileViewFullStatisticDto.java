package com.hackanet.json.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 5/3/20
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@AllArgsConstructor
@NoArgsConstructor
public class HackathonProfileViewFullStatisticDto {

    private List<HackathonProfileViewStatisticDto> statistics;

    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HackathonProfileViewStatisticDto {
        private long count;

        private Long time;
    }
}
