package com.hackanet.services;

import com.hackanet.exceptions.BadRequestException;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.models.PasswordChangeRequest;
import com.hackanet.models.user.User;
import com.hackanet.repositories.PasswordChangeRequestRepository;
import com.hackanet.repositories.user.UserRepository;
import com.hackanet.security.utils.PasswordUtil;
import com.hackanet.services.user.UserService;
import com.hackanet.utils.DateTimeUtil;
import com.hackanet.utils.RandomString;
import com.hackanet.utils.validators.ChangePasswordParamsValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author Iskander Valiev
 * created by isko
 * on 1/15/20
 */
@Service
@Slf4j
public class PasswordResetRequestServiceImpl implements PasswordResetRequestService {

    public static final Integer PASSWORD_REQUEST_EXPIRED_TIME = 15;

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

    @Autowired
    private ChangePasswordParamsValidator validator;

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
        validator.validate(code, newPassword, email);
        PasswordChangeRequest passwordRequest = getByCode(code);
        validator.validate(passwordRequest);

        User user = userService.get(passwordRequest.getUserId());
        if (passwordUtil.matches(newPassword, user.getHashedPassword())) {
            throw new BadRequestException("You can't use old password as a new one");
        }
        if (!user.getEmail().equals(email)) {
            throw new BadRequestException("Emails are not the same");
        }

        user.setHashedPassword(passwordUtil.hash(newPassword));
        userRepository.save(user);

        passwordRequest.setUsed(Boolean.TRUE);
        passwordChangeRequestRepository.save(passwordRequest);
    }

    private PasswordChangeRequest getByCode(String code) {
        return passwordChangeRequestRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Password change request not found"));
    }
}
