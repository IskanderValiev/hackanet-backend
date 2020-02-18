package com.hackanet.controllers;

import com.hackanet.json.dto.TokenDto;
import com.hackanet.json.dto.UserDto;
import com.hackanet.json.forms.UserLoginForm;
import com.hackanet.json.forms.UserRegistrationForm;
import com.hackanet.json.forms.UserSearchForm;
import com.hackanet.json.forms.UserUpdateForm;
import com.hackanet.json.mappers.Mapper;
import com.hackanet.models.User;
import com.hackanet.services.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Set;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/20/19
 */
@RestController
@RequestMapping(UserController.ROOT)
@Api(tags = "User Controller")
public class UserController {

    public static final String ROOT = "/users";

    private static final String REGISTER = "/register";
    private static final String LOGIN = "/login";
    private static final String USER_PROFILE = "/{id}";
    private static final String LIST = "/list";
    private static final String RESET_PASSWORD = "/password/reset";
    private static final String RESET_PASSWORD_REQUEST = RESET_PASSWORD + "/request";
    private static final String GET_NEW_ACCESS_TOKEN = "/token/refresh";
    private static final String GOOGLE_LOGIN = LOGIN + "/oauth2/code/google";
    private static final String FACEBOOK_LOGIN = LOGIN + "/oauth2/code/facebook";
    private static final String GITHUB_LOGIN = LOGIN + "/oauth2/code/github";
    public static final String SOCIAL_LOGIN = LOGIN + "/oauth2";
    private static final String ME = "/me";
    private static final String CONNECTIONS = "/connections";
    private static final String USER_CONNECTIONS = USER_PROFILE + CONNECTIONS;
    private static final String ONE_CONNECTION = CONNECTIONS + "/{connectionId}";
    private static final String CONNECTIONS_SUGGESTIONS = CONNECTIONS + "/suggestions";

    @Autowired
    private UserService userService;

    @Autowired
    @Qualifier("userMapper")
    private Mapper<User, UserDto> userMapper;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private PasswordResetRequestService passwordResetRequestService;

    @Autowired
    private SocialNetworkAuthService socialNetworkAuthService;

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

    @GetMapping(GOOGLE_LOGIN)
    public String google(Principal principal) {
        System.out.println(principal.getName());
        return "google";
    }

    @GetMapping(GITHUB_LOGIN)
    public String github() {
        return "github()";
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

    @ApiOperation("Send reset password request")
    @GetMapping(RESET_PASSWORD_REQUEST)
    public ResponseEntity<String> resetPasswordRequest(@RequestParam("email") String email) {
        passwordResetRequestService.passwordResetRequest(email);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @ApiOperation("Change password")
    @PostMapping(RESET_PASSWORD)
    public ResponseEntity<String> resetPassword(@RequestParam("email") String email,
                                                @RequestParam("code") String code,
                                                @RequestParam("newPassword") String newPassword) {
        passwordResetRequestService.changePassword(code, newPassword, email);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @PostMapping(GET_NEW_ACCESS_TOKEN)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("Get access token using REFRESH TOKEN")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TokenDto> getAccessToken(@AuthenticationPrincipal User user) {
        TokenDto tokenDto = userTokenService.updateAccessToken(user);
        return ResponseEntity.ok(tokenDto);
    }

    @GetMapping(SOCIAL_LOGIN)
    @PreAuthorize("isAuthenticated()")
    public TokenDto me(OAuth2AuthenticationToken principal) {
        return socialNetworkAuthService.saveFromSocialNetwork(principal);
    }

    @GetMapping(ME)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> me(@AuthenticationPrincipal User currentUser) {
        User user = userService.get(currentUser);
        return ResponseEntity.ok(userMapper.map(user));
    }

    @GetMapping(USER_CONNECTIONS)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = false, dataType = "string", paramType = "header")
    })
    public ResponseEntity<List<UserDto>> getConnections(@PathVariable Long id) {
        Set<User> connections = connectionService.getConnections(id);
        return ResponseEntity.ok(userMapper.map(connections));
    }

    @DeleteMapping(ONE_CONNECTION)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deleteConnection(@PathVariable("connectionId") Long connectionId,
                                                   @AuthenticationPrincipal User user) {
        connectionService.deleteConnection(user, connectionId);
        return ResponseEntity.ok("OK");
    }

    @GetMapping(CONNECTIONS_SUGGESTIONS)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization header", defaultValue = "Bearer %token%",
                    required = true, dataType = "string", paramType = "header")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserDto>> getConnectionSuggestions(@AuthenticationPrincipal User user) {
        Set<User> connectionsSuggestions = connectionService.getConnectionsSuggestions(user);
        return ResponseEntity.ok(userMapper.map(connectionsSuggestions));
    }
}
