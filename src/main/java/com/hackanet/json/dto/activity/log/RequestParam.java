package com.hackanet.json.dto.activity.log;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/18/20
 */
@Builder
@Data
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RequestParam {

    private String name;

    private String[] values;

    public RequestParam(Map.Entry<String, String[]> map) {
        this.name = map.getKey();
        this.values = map.getValue();
    }
}
