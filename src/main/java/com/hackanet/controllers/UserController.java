package com.hackanet.controllers;

import com.hackanet.json.dto.TokenDto;
import com.hackanet.json.dto.UserDto;
import com.hackanet.json.forms.UserLoginForm;
import com.hackanet.json.forms.UserRegistrationForm;
import com.hackanet.json.forms.UserSearchForm;
import com.hackanet.json.forms.UserUpdateForm;
import com.hackanet.json.mappers.Mapper;
import com.hackanet.models.User;
import com.hackanet.services.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/20/19
 */
@RestController
@RequestMapping("/users")
@Api(tags = "User Controller")
public class UserController {

    private static final String REGISTER = "/register";
    private static final String LOGIN = "/login";
    private static final String USER_PROFILE = "/{id}";
    private static final String LIST = "/list";

    @Autowired
    private UserService userService;
    @Autowired
    @Qualifier("userMapper")
    private Mapper<User, UserDto> userMapper;

    @PostMapping(REGISTER)
    @ApiOperation("Register a new user")
    public ResponseEntity<TokenDto> register(@RequestBody @Valid UserRegistrationForm form) {
        TokenDto tokenDto = userService.register(form);
        return ResponseEntity.ok(tokenDto);
    }

    @PostMapping(LOGIN)
    @ApiOperation("Log in")
    public ResponseEntity<TokenDto> login(@RequestBody @Valid UserLoginForm form) {
        TokenDto tokenDto = userService.login(form);
        return ResponseEntity.ok(tokenDto);
    }

    @GetMapping(USER_PROFILE)
    @ApiOperation("Get information about user")
    public ResponseEntity<UserDto> getInfo(@PathVariable("id") Long id) {
        User user = userService.get(id);
        return ResponseEntity.ok(userMapper.map(user));
    }

    @PostMapping(LIST)
    @ApiOperation("Search users")
    public ResponseEntity<List<UserDto>> search(@RequestBody UserSearchForm form) {
        List<User> users = userService.userList(form);
        return ResponseEntity.ok(userMapper.map(users));
    }

    @PutMapping(USER_PROFILE)
    @ApiOperation("Update user profile")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> update(@RequestBody UserUpdateForm form,
                                          @AuthenticationPrincipal User currentUser,
                                          @PathVariable Long id) {
        User user = userService.update(id, currentUser, form);
        return ResponseEntity.ok(userMapper.map(user));
    }
}
