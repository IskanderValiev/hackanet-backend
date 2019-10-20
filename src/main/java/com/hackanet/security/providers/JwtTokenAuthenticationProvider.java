package com.hackanet.security.providers;

import com.hackanet.config.JwtConfig;
import com.hackanet.security.authentication.JwtTokenAuthentication;
import com.hackanet.security.details.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


@Component
public class JwtTokenAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private JwtConfig jwtConfig;

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
                e.printStackTrace();
                throw new AuthenticationServiceException("Invalid token");
            }

            UserDetails userDetails = new UserDetailsImpl(
                    Long.parseLong(body.get("sub").toString()),
                    body.get("role").toString(),
                    body.get("email").toString()
            );

            tokenAuthentication.setUserDetails(userDetails);
            tokenAuthentication.setAuthenticated(true);
        }
        return tokenAuthentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtTokenAuthentication.class.equals(authentication);
    }
}
