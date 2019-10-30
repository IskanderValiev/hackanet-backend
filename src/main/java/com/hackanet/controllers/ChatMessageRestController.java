package com.hackanet.controllers;

import com.hackanet.json.dto.MessageDto;
import com.hackanet.json.forms.MessageSearchForm;
import com.hackanet.json.mappers.MessageMapper;
import com.hackanet.models.User;
import com.hackanet.models.chat.Message;
import com.hackanet.services.chat.ChatMessageServiceElasticsearchImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/30/19
 */
@RestController
@RequestMapping("/chats/messages")
@Api(tags = "Message Controller")
public class ChatMessageRestController {

    private static final String SEARCH = "/search";

    @Autowired
    private ChatMessageServiceElasticsearchImpl messageServiceElasticsearch;
    @Autowired
    private MessageMapper messageMapper;

    @PostMapping(SEARCH)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MessageDto>> searchMessages(@RequestBody MessageSearchForm form,
                                                           @AuthenticationPrincipal User user) {
        form.setUserId(user.getId());
        List<Message> messages = messageServiceElasticsearch.searchMessage(form);
        return ResponseEntity.ok(messageMapper.map(messages));
    }
}
