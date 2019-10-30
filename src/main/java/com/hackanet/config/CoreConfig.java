package com.hackanet.config;

import com.hackanet.security.filters.CORSFilter;
import freemarker.template.TemplateException;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/2/19
 */
@Configuration
public class CoreConfig {

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(name = "mailFreemarkerConfigurer")
    public FreeMarkerConfigurationFactory freemarkerConfig() throws IOException, TemplateException {
        FreeMarkerConfigurationFactory configurationFactory = new FreeMarkerConfigurationFactory();
        configurationFactory.setTemplateLoaderPath("classpath:/templates/email/");
        configurationFactory.setFreemarkerSettings(new Properties() {{
            this.put("default_encoding", "UTF-8");
        }});
        configurationFactory.setDefaultEncoding("UTF-8");
        return configurationFactory;
    }

    @Bean(name = "mailFreemarkerConfig")
    public freemarker.template.Configuration freeMarkerConfiguration() throws IOException, TemplateException {
        return freemarkerConfig().createConfiguration();
    }

    /*
    *
    * The bean registers custom filter
    *
    * */
    @Bean
    public FilterRegistrationBean<CORSFilter> someFilterRegistration() {

        FilterRegistrationBean<CORSFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CORSFilter());
        registration.addUrlPatterns("/hackanet/**");
//        registration.addInitParameter("paramName", "paramValue");
        registration.setName("corsFilter");
        registration.setOrder(1);
        return registration;
    }
}
