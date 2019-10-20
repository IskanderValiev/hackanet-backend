package com.hackanet.security.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/20/19
 */
@Component
public class PasswordUtil {

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String hash(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean matches(String password, String hashedPassword) {
        return passwordEncoder.matches(password, hashedPassword);
    }
}
