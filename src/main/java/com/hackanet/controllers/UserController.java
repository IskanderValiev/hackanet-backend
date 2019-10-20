package com.hackanet.controllers;

import com.hackanet.json.dto.TokenDto;
import com.hackanet.json.dto.UserDto;
import com.hackanet.json.forms.UserLoginForm;
import com.hackanet.json.forms.UserRegistrationForm;
import com.hackanet.services.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    @Autowired
    private UserService userService;

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

//    @GetMapping(USER_PROFILE)
//    @ApiOperation("Get information about user")
//    public ResponseEntity<UserDto> getInfo(@PathVariable("id") Long id) {
//
//    }
}
