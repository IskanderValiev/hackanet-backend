package com.hackanet.security.filters;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

// the filter is needed because server does not allow to access to server from external resources
@Component
public class CORSFilter implements Filter {

    // This is to be replaced with a list of domains allowed to access the server
    //You can include more than one origin here
    // TODO: 10/27/19 change this
    private final List<String> allowedOrigins = Lists.newArrayList("http://localhost:63342", "http://localhost:8081");

    public void destroy() {}

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        // Lets make sure that we are working with HTTP (that is, against HttpServletRequest and HttpServletResponse objects)
        if (req instanceof HttpServletRequest && res instanceof HttpServletResponse) {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;

            // Access-Control-Allow-Origin
            String origin = request.getHeader("Origin");
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Vary", "Origin");

            // Access-Control-Max-Age
            response.setHeader("Access-Control-Max-Age", "3600");

            // Access-Control-Allow-Credentials
            response.setHeader("Access-Control-Allow-Credentials", "true");

            // Access-Control-Allow-Methods
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");

            // Access-Control-Allow-Headers
            response.setHeader("Access-Control-Allow-Headers",
                "Origin, X-Requested-With, Content-Type, Accept, Authorization," + "X-CSRF-TOKEN");
        }

        chain.doFilter(req, res);
    }

    public void init(FilterConfig filterConfig) {
    }
}
