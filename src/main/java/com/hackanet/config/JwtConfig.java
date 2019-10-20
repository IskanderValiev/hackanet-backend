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
 * on 2/23/19
 */
@Configuration
@ConfigurationProperties(prefix = "jwt")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtConfig {
    private String secret;
    private String header;
    private String prefix;
}
