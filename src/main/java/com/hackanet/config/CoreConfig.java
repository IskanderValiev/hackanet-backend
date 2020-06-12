package com.hackanet.config;

import com.hackanet.security.filters.CORSFilter;
import com.hackanet.security.filters.JwtTokenAuthFilter;
import freemarker.template.TemplateException;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/2/19
 */
@Configuration
public class CoreConfig {

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

    //https://stackoverflow.com/questions/29285607/spring-security-custom-filter-called-multiple-times
    @Bean
    public FilterRegistrationBean registrationBean(JwtTokenAuthFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean(filter);
        registration.setEnabled(false);
        registration.setOrder(2);
        return registration;
    }

    @Bean
    public CloseableHttpClient httpClient() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial((chain, authType) -> true).build();

        SSLConnectionSocketFactory sslConnectionSocketFactory =
                new SSLConnectionSocketFactory(sslContext, new String[]
                        {"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2"}, null,
                        NoopHostnameVerifier.INSTANCE);
        return HttpClients.custom()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .build();
    }
}
