package com.hackanet.json.dto.activity.log;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/18/20
 */
@Builder
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RequestDto {

    private String fullPath;

    private String contextPath;

    private String requestUrl;

    private String method;

    private String message;

    private String address;

    private String userAgent;

    private List<RequestParam> params;

    private String body;
}
