package com.hackanet.services;

import com.hackanet.config.AppConfig;
import com.hackanet.exceptions.TemplateException;
import com.hackanet.models.User;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/31/19
 */
@Slf4j
@Service
public class TemplateServiceImpl implements TemplateService {

    private static final String WELCOME_EMAIL_TEMPLATE = "welcome_email.ftl";
    private static final String TEST = "test.ftl";

    @Autowired
    private AppConfig appConfig;

    @Autowired
    @Qualifier("mailFreemarkerConfig")
    private Configuration templateConfiguration;

    private String resolveTemplate(Map<String, Object> dataModel, String templateFile, Locale locale, int attempt) {
        if (locale == null) {
            locale = new Locale("us", "US");
        }
        if (dataModel == null) {
            dataModel = new HashMap<>();
        }
        try (StringWriter writer = new StringWriter()) {
            Template inviteTemplate = templateConfiguration.getTemplate(templateFile, locale, null, null, true, false);
            inviteTemplate.process(dataModel, writer);
            writer.flush();
            return writer.toString();
        } catch (Exception e) {
            if (e instanceof TemplateNotFoundException) {
                if (attempt < 1) {
                    try {
                        return resolveTemplate(dataModel, templateFile, null, 1);
                    } catch (Exception e1) {
                        throw new TemplateException(e1);
                    }
                }
            }
            log.warn(String.format("Failed to prepare email template %s", templateFile), e);
            throw new TemplateException(e);
        }
    }

    private String resolveTemplate(Map<String, Object> dataModel, String templateFile, Locale locale) {
        return resolveTemplate(dataModel, templateFile, locale, 0);
    }

    @Override
    public String prepareEmail(User user) {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("name", user.getName());
        log.info("Building email");
        return resolveTemplate(dataModel, WELCOME_EMAIL_TEMPLATE, null);
    }

    @Override
    public String prepareWelcomeEmail(User user) {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("name", user.getName());
        log.info("Preparing welcome email");
        return resolveTemplate(dataModel, WELCOME_EMAIL_TEMPLATE, null);
    }

    @Override
    public String test() {
        return resolveTemplate(new HashMap<>(), TEST, null);
    }

}
