package com.hackanet.utils;

import com.hackanet.models.post.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Iskander Valiev
 * created by isko
 * on 6/5/20
 */
@Component
public class HttpUtils {

    @Autowired
    private RestTemplate restTemplate;

    @SuppressWarnings("unchecked")
    public Set<String> getKeyWords(Post post) {
        try {
            final Set<Object> result = restTemplate.getForObject("http://hackanet-post-recommendation-system/content/handler/keywords?content=" + post.getContent(), Set.class);
            if (result == null) {
                return new HashSet<>();
            }
            return result.stream().map(Object::toString).collect(Collectors.toSet());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new HashSet<>();
    }

    public String get() {
        return restTemplate.getForObject("http://statistic-service/statistics", String.class);
    }
}
