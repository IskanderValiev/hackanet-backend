package com.hackanet.services;

import com.hackanet.application.Patterns;
import com.hackanet.exceptions.BadRequestException;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.models.PasswordChangeRequest;
import com.hackanet.models.User;
import com.hackanet.repositories.PasswordChangeRequestRepository;
import com.hackanet.repositories.UserRepository;
import com.hackanet.security.utils.PasswordUtil;
import com.hackanet.utils.DateTimeUtil;
import com.hackanet.utils.RandomString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author Iskander Valiev
 * created by isko
 * on 1/15/20
 */
@Service
@Slf4j
public class PasswordResetRequestServiceImpl implements PasswordResetRequestService {

    private static final Integer PASSWORD_REQUEST_EXPIRED_TIME = 15;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordChangeRequestRepository passwordChangeRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordUtil passwordUtil;


    @Override
    public void passwordResetRequest(String email) {
        if (StringUtils.isBlank(email)) throw new BadRequestException("Email is empty or null");

        RandomString randomString = new RandomString(21);
        String code = randomString.nextString();
        Optional<PasswordChangeRequest> optional = passwordChangeRequestRepository.findByCode(code);
        if (optional.isPresent()) {
            do {
                log.warn("Password change request with such code already exists. Generating new code.");
                code = randomString.nextString();
                optional = passwordChangeRequestRepository.findByCode(code);
            } while (optional.isPresent());
        }

        email = email.trim().toLowerCase();
        User user = userService.get(email);

        PasswordChangeRequest request = passwordChangeRequestRepository.findAllByUserIdAndUsed(user.getId(), false);
        if (request == null) {
            request = PasswordChangeRequest.builder()
                    .code(code)
                    .createdDate(LocalDateTime.now())
                    .used(Boolean.FALSE)
                    .userId(user.getId())
                    .build();
            passwordChangeRequestRepository.save(request);
        } else {
            LocalDateTime now = LocalDateTime.now();
            long minutes = DateTimeUtil.getDifferenceBetweenLocalDateTimes(request.getCreatedDate(), now);
            if (minutes > PASSWORD_REQUEST_EXPIRED_TIME) {
                request.setCode(code);
                request.setUsed(Boolean.FALSE);
                request.setCreatedDate(now);
                passwordChangeRequestRepository.save(request);
            }
        }
        emailService.sendPasswordResetEmail(user, request);
    }

    @Override
    @Transactional
    public void changePassword(String code, String newPassword, String email) {
        if (StringUtils.isBlank(code))
            throw new BadRequestException("Code is empty or null");

        if (StringUtils.isBlank(email))
            throw new BadRequestException("Email is empty");

        boolean matches = Pattern.matches(Patterns.VALID_PASSWORD_REGEX, newPassword.trim());
        if (!matches)
            throw new BadRequestException("Password is invalid");

        PasswordChangeRequest passwordRequest = passwordChangeRequestRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Password change request not found"));

        if (Boolean.TRUE.equals(passwordRequest.getUsed()))
            throw new BadRequestException("The password change request has been already used");

        LocalDateTime now = LocalDateTime.now();
        long minutes = DateTimeUtil.getDifferenceBetweenLocalDateTimes(passwordRequest.getCreatedDate(), now);
        if (minutes > PASSWORD_REQUEST_EXPIRED_TIME) {
            throw new BadRequestException("Password reset request has expired");
        }

        User user = userService.get(passwordRequest.getUserId());
        if (passwordUtil.matches(newPassword, user.getHashedPassword()))
            throw new BadRequestException("You can't use old password as a new one");
        if (!user.getEmail().equals(email)) {
            throw new BadRequestException("Emails are not the same");
        }

        user.setHashedPassword(passwordUtil.hash(newPassword));
        userRepository.save(user);

        passwordRequest.setUsed(Boolean.TRUE);
        passwordChangeRequestRepository.save(passwordRequest);
    }
}
