package com.hackanet.json.mappers.activity.log;

import com.hackanet.json.dto.activity.log.RequestDto;
import com.hackanet.json.dto.activity.log.RequestParam;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/18/20
 */
@Component("httpRequestMapper")
public class RequestMapper {

    public RequestDto map(HttpServletRequest request) {
        RequestDto dto = RequestDto.builder()
                .fullPath(request.getRequestURL().toString())
                .contextPath(request.getContextPath())
                .requestUrl(request.getRequestURI())
                .method(request.getMethod())
                .address(request.getRemoteAddr())
                .userAgent(request.getHeader("user-agent"))
                .params(request.getParameterMap().entrySet()
                        .stream()
                        .map(RequestParam::new)
                        .collect(Collectors.toList()))
                .build();
        return dto;
    }

//    @SneakyThrows
//    private String getBody(HttpServletRequest request) {
//        RequestWrapper requestWrapper = new RequestWrapper(request);
//        return requestWrapper.getBody();
//    }
}
