package com.hackanet.services;

/**
 * @author Iskander Valiev
 * created by isko
 * on 1/15/20
 */
public interface PasswordResetRequestService {
    void passwordResetRequest(String email);
    void changePassword(String code, String newPassword, String email);
}
