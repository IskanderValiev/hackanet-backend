package com.hackanet.services;

public interface EmailService {
    void buildMessage(String text, String subject, String email);

    void send(String text, String subject, String email);

    void test(String email);
}
