package com.hackanet.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/18/19
 */
//The @EnableCaching annotation triggers a post-processor that inspects every Spring bean
//for the presence of caching annotations on public methods.
//If such an annotation is found, a proxy is automatically created to intercept
//the method call and handle the caching behavior accordingly.
@EnableCaching
@ComponentScan("com.hackanet")
@EnableJpaRepositories(basePackages = "com.hackanet.repositories")
@EnableMongoRepositories(basePackages = "com.hackanet.repositories")
@EnableElasticsearchRepositories(basePackages = "com.hackanet.repositories")
@EntityScan(basePackages = "com.hackanet.models")
@EnableScheduling
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableAspectJAutoProxy
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
