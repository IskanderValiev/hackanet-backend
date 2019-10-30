package com.hackanet.push;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/30/19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResolvedPush {
    private String body;
    private String title;
    private String key;
    private String titleKey;
    private Map<String, Object> additionalData = new HashMap<>();
    private List<String> bodyArgs;
    private List<String> titleArgs;
}
