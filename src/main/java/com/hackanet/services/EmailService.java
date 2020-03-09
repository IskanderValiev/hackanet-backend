package com.hackanet.services;

import com.hackanet.models.Hackathon;
import com.hackanet.models.PasswordChangeRequest;
import com.hackanet.models.team.Team;
import com.hackanet.models.User;

public interface EmailService {
    void buildMessage(String text, String subject, String email);
    void send(String text, String subject, String email);
    void test(String email);
    void sendWelcomeEmail(User user);
    void sendPasswordResetEmail(User user, PasswordChangeRequest request);
    void sendTeamWelcomeEmail(User user, Team team);
    void sendTeamRejectEmail(User user, Team team);
    void sendHackathonWelcomeEmail(User user, Hackathon hackathon);
    void sendHackathonJobReviewRequestEmail(User user, Team team);
}
