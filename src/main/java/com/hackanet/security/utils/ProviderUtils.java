package com.hackanet.security.utils;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/19/19
 */
public class ProviderUtils {

    public static boolean isGoogle(OAuth2AuthenticationToken token) {
        return token.getAuthorizedClientRegistrationId().equals("google");
    }

    public static boolean isGithub(OAuth2AuthenticationToken token) {
        return token.getAuthorizedClientRegistrationId().equals("github");
    }

    public static boolean isFacebook(OAuth2AuthenticationToken token) {
        return token.getAuthorizedClientRegistrationId().equals("facebook");
    }
}
