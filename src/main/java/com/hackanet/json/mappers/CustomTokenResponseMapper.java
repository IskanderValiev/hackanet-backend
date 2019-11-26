package com.hackanet.json.mappers;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/19/19
 */
@Component
public class CustomTokenResponseMapper implements Converter<Map<String, String>, OAuth2AccessTokenResponse> {

    @Override
    public OAuth2AccessTokenResponse convert(Map<String, String> tokenResponseParameters) {
        String accessToken = tokenResponseParameters.get("access_token");
        String expiresInParam = tokenResponseParameters.get("expires_in");
        if (StringUtils.isBlank(expiresInParam)) {
            expiresInParam = "3600";
        }
        long expiresIn = Long.parseLong(expiresInParam);


        OAuth2AccessToken.TokenType accessTokenType = OAuth2AccessToken.TokenType.BEARER;

        Map<String, Object> additionalParameters = new HashMap<>();

        tokenResponseParameters.forEach(additionalParameters::put);

        return OAuth2AccessTokenResponse.withToken(accessToken)
                .tokenType(accessTokenType)
                .expiresIn(expiresIn)
                .additionalParameters(additionalParameters)
                .build();
    }
}
