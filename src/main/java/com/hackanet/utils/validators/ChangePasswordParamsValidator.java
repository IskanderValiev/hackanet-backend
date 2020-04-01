package com.hackanet.utils.validators;

import com.hackanet.application.Patterns;
import com.hackanet.models.PasswordChangeRequest;
import com.hackanet.services.PasswordResetRequestServiceImpl;
import com.hackanet.utils.DateTimeUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/31/20
 */
@Service
public class ChangePasswordParamsValidator {

    public void validate(String code, String newPassword, String email) {
        checkArgument(StringUtils.isBlank(code), "Code is empty or null");
        checkArgument(StringUtils.isBlank(email), "Email is empty");

        boolean matches = Pattern.matches(Patterns.VALID_PASSWORD_REGEX, newPassword.trim());
        checkArgument(matches, "Password is invalid");
    }

    public void validate(PasswordChangeRequest passwordRequest) {
        checkArgument(Boolean.TRUE.equals(passwordRequest.getUsed()), "The password change request has been already used");

        long minutes = DateTimeUtil.getDifferenceBetweenLocalDateTimes(passwordRequest.getCreatedDate(), LocalDateTime.now());
        checkArgument(minutes > PasswordResetRequestServiceImpl.PASSWORD_REQUEST_EXPIRED_TIME, "Password reset request has expired");
    }
}
