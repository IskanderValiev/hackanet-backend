package com.hackanet.controllers;

import com.hackanet.json.dto.UserPhoneTokenDto;
import com.hackanet.json.forms.UserPhoneTokenAddForm;
import com.hackanet.json.mappers.UserPhoneTokenMapper;
import com.hackanet.models.user.User;
import com.hackanet.models.user.UserPhoneToken;
import com.hackanet.services.user.UserPhoneTokenService;
import com.hackanet.services.push.RabbitMQPushNotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/31/19
 */
@RestController
@RequestMapping("/users/push/tokens")
@Api(tags = {"User Push Token Controller"})
public class UserPhoneTokenController {

    @Autowired
    private UserPhoneTokenService userPhoneTokenService;

    @Autowired
    private UserPhoneTokenMapper userPhoneTokenMapper;

    @Autowired
    private RabbitMQPushNotificationService rabbitMQPushNotificationService;

    @PostMapping
    @ApiOperation("Add push token for current user")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserPhoneTokenDto> add(@Valid @RequestBody UserPhoneTokenAddForm form,
                                                 @AuthenticationPrincipal User user) {
        form.setUserId(user.getId());
        UserPhoneToken token = userPhoneTokenService.add(form);
        return new ResponseEntity<>(userPhoneTokenMapper.map(token), HttpStatus.CREATED);
    }

    @GetMapping("/send")
    @ApiOperation("FOR TESTS")
    public ResponseEntity<String> send() {
        rabbitMQPushNotificationService.sendTestNotification();
        return ResponseEntity.ok("OK");
    }
}
