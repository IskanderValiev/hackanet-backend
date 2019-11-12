package com.hackanet.controllers;

import com.hackanet.json.dto.UserNotificationSettingsDto;
import com.hackanet.json.forms.UserNotificationSettingsUpdateForm;
import com.hackanet.json.mappers.UserNotificationSettingsMapper;
import com.hackanet.models.User;
import com.hackanet.models.UserNotificationSettings;
import com.hackanet.services.UserNotificationSettingsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/11/19
 */
@RestController
@Api(tags = "User Notification Settings Controller")
@RequestMapping("/users/notification/settings")
public class UserNotificationSettingsController {

    @Autowired
    private UserNotificationSettingsService userNotificationSettingsService;
    @Autowired
    private UserNotificationSettingsMapper userNotificationSettingsMapper;

    @PutMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Update settings for the user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserNotificationSettingsDto> update(@Valid @RequestBody UserNotificationSettingsUpdateForm form,
                                                              @AuthenticationPrincipal User user) {
        UserNotificationSettings userNotificationSettings = userNotificationSettingsService.updateForUser(user, form);
        return ResponseEntity.ok(userNotificationSettingsMapper.map(userNotificationSettings));
    }

    @GetMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Get settings for the user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserNotificationSettingsDto> get(@AuthenticationPrincipal User user) {
        UserNotificationSettings userNotificationSettings = userNotificationSettingsService.getOrCreateDefaultsSettingsForUser(user);
        return ResponseEntity.ok(userNotificationSettingsMapper.map(userNotificationSettings));
    }
}
