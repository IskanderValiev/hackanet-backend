package com.hackanet.security.filters;

import com.hackanet.config.JwtConfig;
import com.hackanet.security.authentication.JwtTokenAuthentication;
import com.hackanet.security.providers.JwtTokenAuthenticationProvider;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class JwtTokenAuthFilter implements Filter {

    @Autowired
    private JwtTokenAuthenticationProvider provider;

    @Autowired
    private JwtConfig jwtConfig;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        String authorizationHeader = request.getHeader("Authorization");
        JwtTokenAuthentication authentication;


        SecurityContext context = SecurityContextHolder.getContext();
        if (authorizationHeader == null) {
            authentication = new JwtTokenAuthentication(null);
            authentication.setAuthenticated(false);
        } else  {
            String token = "";
            if (!StringUtils.isBlank(authorizationHeader)) {
                String prefix = jwtConfig.getPrefix() + " ";
                token = authorizationHeader.substring(prefix.length());
            }
            authentication = new JwtTokenAuthentication(token);
            context.setAuthentication(provider.authenticate(authentication));
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
