package com.hackanet.utils;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

/**
 * @author Iskander Valiev
 * created by isko
 * on 5/21/20
 */
// name - name of the service we want to send a request to
@FeignClient(name = "hackanet-post-recommendation-system")
public interface PostKeyWordsFeignClient {
    @GetMapping(value = "/content/handler/keywords")
    Set<String> getKeyWords(@RequestParam String content);
}
