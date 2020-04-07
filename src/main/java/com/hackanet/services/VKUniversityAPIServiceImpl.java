package com.hackanet.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackanet.exceptions.BadRequestException;
import com.hackanet.json.dto.VkUniversityResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author Iskander Valiev
 * created by isko
 * on 4/7/20
 */
@Service
public class VKUniversityAPIServiceImpl implements UniversityService {

    @Value("${vk.serviceToken}")
    private String serviceToken;

    @Autowired
    private CloseableHttpClient httpClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public VkUniversityResponse.VkUniversityListDto getUniversity(String query) {
        return sendRequest(query).getResponse();
    }

    private VkUniversityResponse sendRequest(String query) {
        HttpGet httpGet =
                new HttpGet("https://api.vk.com/method/database.getUniversities?access_token=" + serviceToken + "&q=" + query + "&v=5.103");
        try (CloseableHttpResponse execute = httpClient.execute(httpGet)) {
            if (execute.getStatusLine().getStatusCode() == 200) {
                return objectMapper.readValue(execute.getEntity().getContent(), VkUniversityResponse.class);
            }
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }
        return null;
    }
}
