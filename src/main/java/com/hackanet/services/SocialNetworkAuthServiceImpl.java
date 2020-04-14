package com.hackanet.services;

import com.hackanet.exceptions.BadRequestException;
import com.hackanet.json.dto.TokenDto;
import com.hackanet.models.FileInfo;
import com.hackanet.models.user.User;
import com.hackanet.models.user.UserToken;
import com.hackanet.repositories.user.UserRepository;
import com.hackanet.security.enums.Role;
import com.hackanet.services.user.UserService;
import com.hackanet.services.user.UserTokenService;
import com.hackanet.utils.RandomString;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static com.hackanet.security.utils.ProviderUtils.*;
import static com.hackanet.utils.StringUtils.generateRandomString;

/**
 * @author Iskander Valiev
 * created by isko
 * on 4/14/20
 */
@Service
public class SocialNetworkAuthServiceImpl implements SocialNetworkAuthService {

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileInfoService fileInfoService;

    @Autowired
    private EmailService emailService;

    @Override
    public TokenDto saveFromSocialNetwork(OAuth2AuthenticationToken principal) {
        if (isGoogle(principal)) return saveFromGoogle(principal);
        if (isGithub(principal)) return saveFromGithub(principal);
        if (isFacebook(principal)) return saveFromFacebook(principal);
        if (isLinkedIn(principal)) return saveFromLinkedIn(principal);
        throw new BadRequestException("Provider has not been found");
    }

    @Override
    public TokenDto saveFromGoogle(Authentication authentication) {
        DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal();
        return saveFromGoogle(oidcUser.getAttributes());
    }

    @Transactional
    protected TokenDto saveFromGoogle(Map<String, Object> userDetails) {
        String email = (String) userDetails.get("email");
        boolean exists = userService.exists(email.toLowerCase());
        User user;
        if (exists) {
            user = userService.get(email.toLowerCase());
            UserToken userToken = userTokenService.getOrCreateTokenIfNotExists(user);
            return userTokenService.buildTokenDtoByUser(user, userToken);
        }
        FileInfo fileInfo = FileInfo.builder()
                .name(generateRandomString())
                .previewLink((String) userDetails.get("picture"))
                .build();
        fileInfoService.save(fileInfo);

        user = User.builder()
                .email(email.toLowerCase())
                .name((String) userDetails.get("given_name"))
                .lastname((String) userDetails.get("family_name"))
                .picture(fileInfo)
                .role(Role.USER)
                .lookingForTeam(Boolean.FALSE)
                .refreshTokenParam(new RandomString().nextString())
                .accessTokenParam(new RandomString().nextString())
                .emailConfirmed(true)
                .build();
        user = userRepository.save(user);
        UserToken userToken = userTokenService.getOrCreateTokenIfNotExists(user);
        emailService.sendWelcomeEmail(user);
        return userTokenService.buildTokenDtoByUser(user, userToken);
    }

    @Override
    public TokenDto saveFromGithub(Authentication authentication) {
        DefaultOAuth2User oidcUser = (DefaultOAuth2User) authentication.getPrincipal();
        return saveFromGithub(oidcUser.getAttributes());
    }

    @Transactional
    protected TokenDto saveFromGithub(Map<String, Object> userDetails) {
        User.UserBuilder userBuilder = User.builder();
        String email = (String) userDetails.get("email");
        String login = (String) userDetails.get("login");
        if (!StringUtils.isBlank(email)) {
            boolean exists = userService.exists(email.toLowerCase());
            if (exists) {
                User user = userService.get(email.toLowerCase());
                return userTokenService.getTokenByUser(user);
            }
            userBuilder.email(email);
        } else if (!StringUtils.isBlank(login)) {
            boolean exists = userService.exists(login.toLowerCase());
            if (exists) {
                User user = userService.get(login.toLowerCase());
                return userTokenService.getTokenByUser(user);
            }
            userBuilder.email(login);
        } else {
            throw new BadRequestException("Email and login are null or empty.");
        }
        String name = (String) userDetails.get("name");
        if (!StringUtils.isBlank(name)) {
            userBuilder.name(name);
        } else {
            userBuilder.name(login);
        }
        userBuilder.role(Role.USER)
                .accessTokenParam(new RandomString().nextString())
                .refreshTokenParam(new RandomString().nextString())
                .country((String) userDetails.get("location"))
                .emailConfirmed(true);
        User user = userRepository.save(userBuilder.build());
        UserToken token = userTokenService.getOrCreateTokenIfNotExists(user);
        String avatarUrl = (String) userDetails.get("avatar_url");
        fileInfoService.createAndSave(user, avatarUrl);
        return userTokenService.buildTokenDtoByUser(user, token);

    }

    @Override
    public TokenDto saveFromLinkedIn(Authentication authentication) {
        DefaultOAuth2User oidcUser = (DefaultOAuth2User) authentication.getPrincipal();
        return saveFromLinkedIn(oidcUser.getAttributes());
    }

    @Transactional
    protected TokenDto saveFromLinkedIn(Map<String, Object> userDetails) {
        String name = (String) userDetails.get("localizedFirstName");
        String lastName = (String) userDetails.get("localizedLastName");
        String email = (String) userDetails.get("email");
        // TODO: 11/20/19 get email and save user if he does not exist with received email
        User user = User.builder()
                .name(name)
                .lastname(lastName)
                .email(email)
                .role(Role.USER)
                .refreshTokenParam(new RandomString().nextString())
                .accessTokenParam(new RandomString().nextString())
                .build();
        user = userRepository.save(user);
        return userTokenService.getTokenByUser(user);
    }

    @Override
    public TokenDto saveFromFacebook(Authentication authentication) {
        DefaultOAuth2User oidcUser = (DefaultOAuth2User) authentication.getPrincipal();
        return saveFromFacebook(oidcUser.getAttributes());
    }

    @Transactional
    protected TokenDto saveFromFacebook(Map<String, Object> userDetails) {
        return new TokenDto();
    }
}
