package com.hackanet.services.user;

import com.hackanet.application.AppConstants;
import com.hackanet.config.JwtConfig;
import com.hackanet.exceptions.ForbiddenException;
import com.hackanet.json.dto.CompanyOwnerTokenDto;
import com.hackanet.json.dto.TokenDto;
import com.hackanet.models.user.User;
import com.hackanet.models.user.UserToken;
import com.hackanet.repositories.user.UserRepository;
import com.hackanet.repositories.user.UserTokenRepository;
import com.hackanet.security.enums.TokenType;
import com.hackanet.utils.DateTimeUtil;
import com.hackanet.utils.RandomString;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @author Iskander Valiev
 * created by isko
 * on 1/15/20
 */
@Service
public class UserTokenServiceImpl implements UserTokenService {

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private UserTokenRepository userTokenRepository;

    @Autowired
    private UserRepository userRepository;

    // @Lazy is used because of circular dependency (FIX THIS URGENTLY)
    @Autowired
    @Lazy
    private UserService userService;

    @Override
    public UserToken getOrCreateTokenIfNotExists(User user) {
        UserToken userToken = userTokenRepository.findByUserId(user.getId());
        if (userToken != null) return userToken;

        userToken = UserToken.builder()
                .user(user)
                .accessTokenExpiresAt(LocalDateTime.now().plusHours(AppConstants.ACCESS_TOKEN_EXPIRING_TIME_IN_HOURS))
                .refreshTokenExpiresAt(LocalDateTime.now().plusDays(AppConstants.REFRESH_TOKEN_EXPIRING_TIME_IN_DAYS))
                .refreshToken(getTokenValue(user, TokenType.REFRESH))
                .build();
        return userTokenRepository.save(userToken);
    }

    @Override
    public TokenDto buildTokenDtoByUser(User user, UserToken userToken) {
        return TokenDto.builder()
                .userId(user.getId())
                .refreshToken(userToken.getRefreshToken())
                .accessToken(getTokenValue(user, TokenType.ACCESS))
                .refreshTokenExpiresAt(DateTimeUtil.localDateTimeToLong(userToken.getRefreshTokenExpiresAt()))
                .accessTokenExpiresAt(DateTimeUtil.localDateTimeToLong(userToken.getAccessTokenExpiresAt()))
                .role(user.getRole().toString())
                .build();
    }

    /**
     * builds token value
     *
     * @param user - user get token value for
     * @param type - type of token
     */
    @Override
    public String getTokenValue(User user, TokenType type) {
        return Jwts.builder()
                .claim("role", user.getRole().toString())
                .claim("email", user.getEmail())
                .claim("tokenParam", TokenType.REFRESH.equals(type) ? user.getRefreshTokenParam() : user.getAccessTokenParam())
                .claim("token-type", type.toString())
                .setSubject(user.getId().toString())
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret()).compact();
    }

    /**
     * updates access token
     *
     * @param user - user whose access token to update
     * @return tokenDto - dto data with tokens
     */
    @Override
    @Transactional
    public TokenDto updateAccessToken(User user) {
        UserToken userToken = getByUserId(user.getId());

        if (userTokenExpired(userToken, true))
            throw new ForbiddenException("Refresh token has expired");

        userToken.setAccessTokenExpiresAt(LocalDateTime.now().plusHours(AppConstants.ACCESS_TOKEN_EXPIRING_TIME_IN_HOURS));
        userTokenRepository.save(userToken);

        User updateAccessTokenUser = userService.get(user.getId());
        updateAccessTokenUser.setAccessTokenParam(new RandomString().nextString());
        updateAccessTokenUser = userRepository.save(updateAccessTokenUser);

        return buildTokenDtoByUser(updateAccessTokenUser, userToken);
    }

    /**
     * checks if the token has expired
     *
     * @param token          to check
     * @param isRefreshToken type of token
     * @return true if token has expired
     */
    @Override
    public boolean userTokenExpired(@NotNull UserToken token, boolean isRefreshToken) {
        if (isRefreshToken)
            return LocalDateTime.now().isAfter(token.getRefreshTokenExpiresAt());
        else
            return LocalDateTime.now().isAfter(token.getAccessTokenExpiresAt());
    }

    @Override
    public UserToken getByUserId(Long userId) {
        return userTokenRepository.findByUserId(userId);
    }

    @Override
    public TokenDto getTokenByUser(User user) {
        UserToken token = getOrCreateTokenIfNotExists(user);
        return buildTokenDtoByUser(user, token);
    }

    @Override
    public CompanyOwnerTokenDto convert(TokenDto tokenDto, Long companyId) {
        CompanyOwnerTokenDto companyOwnerTokenDto = new CompanyOwnerTokenDto();
        companyOwnerTokenDto.setCompanyId(companyId);
        companyOwnerTokenDto.setAccessToken(tokenDto.getAccessToken());
        companyOwnerTokenDto.setAccessTokenExpiresAt(tokenDto.getAccessTokenExpiresAt());
        companyOwnerTokenDto.setRefreshToken(tokenDto.getRefreshToken());
        companyOwnerTokenDto.setRefreshTokenExpiresAt(tokenDto.getRefreshTokenExpiresAt());
        companyOwnerTokenDto.setUserId(tokenDto.getUserId());
        companyOwnerTokenDto.setRole(tokenDto.getRole());
        return companyOwnerTokenDto;
    }
}
