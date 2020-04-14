package com.hackanet.security.providers;

import com.hackanet.config.JwtConfig;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.models.user.UserToken;
import com.hackanet.security.authentication.JwtTokenAuthentication;
import com.hackanet.security.details.UserDetailsImpl;
import com.hackanet.services.user.UserTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtTokenAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private UserTokenService userTokenService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtTokenAuthentication tokenAuthentication = (JwtTokenAuthentication) authentication;
        Claims body;
        if (!StringUtils.isBlank(tokenAuthentication.getName())) {
            try {
                body = Jwts.parser()
                        .setSigningKey(jwtConfig.getSecret())
                        .parseClaimsJws(tokenAuthentication.getName())
                        .getBody();
            } catch (MalformedJwtException | SignatureException e) {
                log.error(e.getMessage());
                throw new AuthenticationServiceException("Invalid token");
            }
            long userId = Long.parseLong(body.get("sub").toString());
            UserToken token = userTokenService.getByUserId(userId);
            if (token == null) {
                throw new NotFoundException("Token not found");
            }
            if (userTokenService.userTokenExpired(token, true)) {
                tokenAuthentication.setAuthenticated(false);
                return tokenAuthentication;
            }
            UserDetails userDetails = new UserDetailsImpl(
                    userId,
                    body.get("role").toString(),
                    body.get("email").toString(),
                    token
            );
            if (userDetails.isAccountNonExpired() || tokenAuthentication.isRefresh()) {
                tokenAuthentication.setUserDetails(userDetails);
                tokenAuthentication.setAuthenticated(true);
            } else {
                tokenAuthentication.setAuthenticated(false);
            }
        }
        return tokenAuthentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtTokenAuthentication.class.isAssignableFrom(authentication);
    }
}
