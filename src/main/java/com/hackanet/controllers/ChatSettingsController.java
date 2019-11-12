package com.hackanet.controllers;

import com.hackanet.json.dto.ChatSettingsDto;
import com.hackanet.json.forms.ChatSettingUpdateForm;
import com.hackanet.json.mappers.ChatSettingsMapper;
import com.hackanet.models.User;
import com.hackanet.models.chat.ChatSettings;
import com.hackanet.services.chat.ChatSettingsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/11/19
 */
@RestController
@RequestMapping("/chats/settings")
@Api(tags = "Chat Settings Controller")
public class ChatSettingsController {

    @Autowired
    private ChatSettingsService chatSettingsService;
    @Autowired
    private ChatSettingsMapper chatSettingsMapper;

    @PutMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Update chat settings for the user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatSettingsDto> update(@Valid @RequestBody ChatSettingUpdateForm form,
                                                  @AuthenticationPrincipal User user) {
        ChatSettings chatSettings = chatSettingsService.update(user, form);
        return ResponseEntity.ok(chatSettingsMapper.map(chatSettings));
    }

}
