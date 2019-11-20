//package com.hackanet.security.filters;
//
//import com.hackanet.json.mappers.CustomTokenResponseMapper;
//import org.apache.commons.io.output.TeeOutputStream;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mock.web.DelegatingServletOutputStream;
//import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletRequestWrapper;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpServletResponseWrapper;
//import java.io.*;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author Iskander Valiev
// * created by isko
// * on 11/19/19
// */
//@Component
//public class OAuth2AccessTokenFilter implements Filter {
//
//    @Autowired
//    private CustomTokenResponseMapper customTokenResponseMapper;
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//    }
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//        HttpServletResponse response = (HttpServletResponse) servletResponse;
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        final OutputStream ps = new PrintStream(baos);
//
//        Map<String, String> map = new HashMap<>();
//        Map<String, String[]> parameterMap = request.getParameterMap();
//        for (Map.Entry<String, String[]> key : parameterMap.entrySet()) {
//            map.put(key.getKey(), key.getValue()[0]);
//        }
//
////        OAuth2AccessTokenResponse resp = customTokenResponseMapper.convert(map);
////        System.out.println(resp.getAccessToken().getTokenValue());
//
//    }
//
//    @Override
//    public void destroy() {
//
//    }
//}
