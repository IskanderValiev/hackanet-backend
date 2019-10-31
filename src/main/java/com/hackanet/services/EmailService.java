package com.hackanet.services;

import com.hackanet.models.User;

public interface EmailService {
    void buildMessage(String text, String subject, String email);

    void send(String text, String subject, String email);

    void test(String email);
    void sendWelcomeEmail(User user);
}
