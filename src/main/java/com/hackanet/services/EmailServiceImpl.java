package com.hackanet.services;

import com.hackanet.models.PasswordChangeRequest;
import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.team.Team;
import com.hackanet.models.user.User;
import com.hackanet.services.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/31/19
 */
@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired
    private TemplateService templateService;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private UserService userService;

    private ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    public void buildMessage(String text, String subject, String email) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            message.setContent(text, "text/html");
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
            messageHelper.setTo(email);
            messageHelper.setSubject(subject);
            messageHelper.setText(text, true);
        } catch (MessagingException e) {
            throw new IllegalArgumentException(e);
        }
        javaMailSender.send(message);
    }

    @Override
    public void send(String text, String subject, String email) {
        executorService.execute(() -> buildMessage(text, subject, email));
    }

    @Override
    public void test(String email) {
        String test = templateService.test();
        log.info("Mail sent to {}", email);
        send(test, "Welcome to Hackanet", email);
    }

    @Override
    public void sendWelcomeEmail(User user) {
        String s = templateService.prepareWelcomeEmail(user);
        send(s, "Welcome to Hackanet", user.getEmail());
    }

    @Override
    public void sendEmailConfirmation(User user) {
        String s = templateService.prepareEmailConfirmation(user);
        send(s, "Email Confirmation", user.getEmail());
    }

    @Override
    public void sendPasswordResetEmail(User user, PasswordChangeRequest request) {
        String s = templateService.prepareResetPasswordEmail(user, request);
        send(s, "Reset password request", user.getEmail());
    }

    @Override
    public void sendTeamWelcomeEmail(User user, Team team) {
        String s = templateService.prepareTeamWelcomeEmail(user, team);
        send(s, "Welcome to the team", user.getEmail());
    }

    @Override
    public void sendTeamRejectEmail(User user, Team team) {
        String s = templateService.prepareTeamRejectEmail(user, team);
        send(s, "Team request is rejected", user.getEmail());
    }

    @Override
    public void sendHackathonWelcomeEmail(User user, Hackathon hackathon) {
        String s = templateService.prepareHackathonWelcomeEmail(user, hackathon);
        send(s, "Welcome to the hackathon", user.getEmail());
    }

    @Override
    public void sendHackathonJobReviewRequestEmail(User user, Team team) {
        String s = templateService.prepareHackathonJobReviewRequestEmail(user, team);
        send(s, "Hackathon job review", user.getEmail());
    }
}
