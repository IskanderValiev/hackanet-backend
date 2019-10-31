package com.hackanet.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/31/19
 */
@Configuration
@ConfigurationProperties(prefix = "fcm")
@Data
public class FcmConfig {
    private String host;
    private String endPoint;
    private String key;
}
