package com.hackanet.controllers;

import com.hackanet.json.dto.ChatDto;
import com.hackanet.json.forms.ChatCreateForm;
import com.hackanet.json.mappers.Mapper;
import com.hackanet.models.User;
import com.hackanet.models.chat.Chat;
import com.hackanet.services.chat.ChatService;
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

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/24/19
 */
@RestController
@Api(tags = "Chat Controller")
@RequestMapping("/chats")
public class ChatController {

    private static final String CHAT_BY_USER = "/user";
    private static final String MANAGE_USER = "/{chatId}" + CHAT_BY_USER + "/{userId}";

    @Autowired
    private ChatService chatService;

    @Autowired
    private Mapper<Chat, ChatDto> chatMapper;

    @PostMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Create a new chat")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'USER')")
    public ResponseEntity<ChatDto> createChat(@RequestBody ChatCreateForm form,
                                              @AuthenticationPrincipal User user) {
        Chat chat = chatService.create(form, user);
        return new ResponseEntity<>(chatMapper.map(chat), HttpStatus.CREATED);
    }

    @GetMapping(CHAT_BY_USER)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Get chats by user")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'USER')")
    public ResponseEntity<List<ChatDto>> getChatsByUser(@AuthenticationPrincipal User user) {
        List<Chat> chats = chatService.getByUser(user);
        return ResponseEntity.ok(chatMapper.map(chats));
    }

    @PutMapping(MANAGE_USER)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Add user to the chat")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'USER')")
    public ResponseEntity<ChatDto> addUser(@PathVariable Long chatId,
                                                 @PathVariable Long userId,
                                                 @AuthenticationPrincipal User user) {
        Chat chat = chatService.addOrRemoveUser(chatId, userId, user, true);
        return ResponseEntity.ok(chatMapper.map(chat));
    }

    @DeleteMapping(MANAGE_USER)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "Remove user from the chat")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'USER')")
    public ResponseEntity<ChatDto> removeUser(@PathVariable Long chatId,
                                              @PathVariable Long userId,
                                              @AuthenticationPrincipal User user) {
        Chat chat = chatService.addOrRemoveUser(chatId, userId, user, false);
        return ResponseEntity.ok(chatMapper.map(chat));
    }
}
