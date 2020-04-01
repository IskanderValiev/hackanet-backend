package com.hackanet.services;

import com.hackanet.json.dto.TokenDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

public interface SocialNetworkAuthService {
    TokenDto saveFromGoogle(Authentication principal);
    TokenDto saveFromGithub(Authentication principal);
    TokenDto saveFromFacebook(Authentication principal);
    TokenDto saveFromLinkedIn(Authentication principal);
    TokenDto saveFromSocialNetwork(OAuth2AuthenticationToken principal);
}
