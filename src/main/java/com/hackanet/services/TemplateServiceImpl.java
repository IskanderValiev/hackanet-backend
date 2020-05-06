package com.hackanet.services;

import com.hackanet.config.AppConfig;
import com.hackanet.exceptions.TemplateException;
import com.hackanet.models.PasswordChangeRequest;
import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.team.Team;
import com.hackanet.models.user.User;
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

    private static final String WELCOME_EMAIL_TEMPLATE = "welcome_email.ftlh";
    private static final String TEST = "test.ftlh";
    private static final String RESET_PASSWORD_TEMPLATE = "reset_password.ftlh";
    private static final String TEAM_WELCOME_TEMPLATE = "team_welcome.ftlh";
    private static final String TEAM_REJECT_TEMPLATE = "team_reject.ftlh";
    private static final String HACKATHON_WELCOME_TEMPLATE = "hackathon_welcome.ftlh";
    private static final String HACKATHON_JOB_REVIEW_REQUEST_TEMPLATE = "hackathon_job_review_request.ftlh";
    private static final String EMAIL_CONFIRMATION_TEMPLATE = "email_confirmation_email.ftlh";

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
        log.info("Preparing welcome email");
        return resolveTemplate(dataModel, WELCOME_EMAIL_TEMPLATE, null);
    }

    @Override
    public String test() {
        return resolveTemplate(new HashMap<>(), TEST, null);
    }

    @Override
    public String prepareResetPasswordEmail(User user, PasswordChangeRequest request) {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("link", String.format(appConfig.getMainUrl() + appConfig.getPasswordResetLink(), request.getCode(), user.getEmail()));
        log.info("Preparing reset password email");
        return resolveTemplate(dataModel, RESET_PASSWORD_TEMPLATE, null);
    }

    @Override
    public String prepareTeamWelcomeEmail(User user, Team team) {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("teamName", team.getName());
        dataModel.put("members", team.getMembers());
        log.info("Preparing team welcome email");
        return resolveTemplate(dataModel, TEAM_WELCOME_TEMPLATE, null);
    }

    @Override
    public String prepareTeamRejectEmail(User user, Team team) {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("teamName", team.getName());
        log.info("Preparing team reject email");
        return resolveTemplate(dataModel, TEAM_REJECT_TEMPLATE, null);
    }

    @Override
    public String prepareHackathonWelcomeEmail(User user, Hackathon hackathon) {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("hackathonName", hackathon.getName());
        log.info("Preparing hackathon welcome email");
        return resolveTemplate(dataModel, HACKATHON_WELCOME_TEMPLATE, null);
    }

    @Override
    public String prepareHackathonJobReviewRequestEmail(User user, Team team) {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("team", team);
        log.info("Preparing hackathon job review request email");
        return resolveTemplate(dataModel, HACKATHON_JOB_REVIEW_REQUEST_TEMPLATE, null);
    }

    @Override
    public String prepareEmailConfirmation(User user) {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("link", String.format(appConfig.getMainUrl() + appConfig.getConfirmLink(), user.getEmailConfirmationCode()));
        log.info("Preparing email confirmation");
        return resolveTemplate(dataModel, EMAIL_CONFIRMATION_TEMPLATE, null);
    }

}
