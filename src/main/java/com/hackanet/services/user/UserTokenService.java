package com.hackanet.services.user;

import com.hackanet.json.dto.CompanyOwnerTokenDto;
import com.hackanet.json.dto.TokenDto;
import com.hackanet.models.user.User;
import com.hackanet.models.user.UserToken;
import com.hackanet.security.enums.TokenType;

public interface UserTokenService {
    UserToken getOrCreateTokenIfNotExists(User user);
    TokenDto buildTokenDtoByUser(User user, UserToken userToken);
    String getTokenValue(User user, TokenType type);
    TokenDto updateAccessToken(User user);
    boolean userTokenExpired(UserToken token, boolean isRefreshToken);
    UserToken getByUserId(Long userId);
    TokenDto getTokenByUser(User user);
    CompanyOwnerTokenDto convert(TokenDto tokenDto, Long companyId);
}
