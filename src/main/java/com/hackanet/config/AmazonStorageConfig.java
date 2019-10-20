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
 * on 5/9/19
 */
@Configuration
@ConfigurationProperties(prefix = "amazon")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmazonStorageConfig {
    private String endpointUrl;
    private String accessKey;
    private String secretKey;
    private String bucketName;
}
