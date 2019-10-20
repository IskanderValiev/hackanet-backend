package com.hackanet.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/31/19
 */
@Configuration
@ConfigurationProperties(prefix = "easy.reserve")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppConfig {
    private String mainUrl;
    private String confirmLink;
    private String passwordResetLink;
}
