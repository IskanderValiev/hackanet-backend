package com.hackanet.services.push;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hackanet.config.FcmConfig;
import com.hackanet.exceptions.PushSendException;
import com.hackanet.push.ResolvedPush;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/30/19
 */
@Component("iosPushNotificationSender")
@Slf4j
public class IosPushNotificationSender implements PushNotificationSender {

    private static final Integer TIMEOUT = 5000;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CloseableHttpClient httpClient;

    @Autowired
    private FcmConfig fcmConfig;

    @Override
    public void sendPush(PushNotificationMsg msg, ResolvedPush resolvedPush, String token) throws PushSendException {
        log.info("Send push to IOS using FCM/IOS component for {} of type {}", msg.getToUserId(), msg.getType());
        //TODO merge with android component version
        // https://firebase.google.com/docs/cloud-messaging/http-server-ref
        // http://stackoverflow.com/questions/37426542/firebase-cloud-messaging-notification-from-java-instead-of-firebase-console
        String host = "https://fcm.googleapis.com:443";
        String requestURI = "/fcm/send";
        ObjectNode body = new ObjectNode(objectMapper.getNodeFactory());

        body.put("to", token);
        body.put("priority", "high");

        ObjectNode data = new ObjectNode(objectMapper.getNodeFactory());
        data.put("type", msg.getType().getValue());
        data.put("key", resolvedPush.getKey());
        if (msg.getPayloadEntity() != null) {
            data.putPOJO("payload", msg.getPayloadEntity());
        }
        for (Map.Entry<String, Object> customData : resolvedPush.getAdditionalData().entrySet()) {
            data.putPOJO(customData.getKey(), customData.getValue());
        }

        body.set("data", data);

        ObjectNode notification = new ObjectNode(objectMapper.getNodeFactory());
        notification.put("click_action", resolvedPush.getKey());
        notification.put("body_loc_key", resolvedPush.getKey());
        notification.put("title_loc_key", resolvedPush.getKey() + "_title");
        notification.putPOJO("body_loc_args", resolvedPush.getBodyArgs());
        body.set("notification", notification);

        RequestConfig requestConfig = RequestConfig
                .custom()
                .setConnectTimeout(TIMEOUT)
                .setSocketTimeout(TIMEOUT)
                .setConnectionRequestTimeout(TIMEOUT)
                .build();
        HttpPost httpPost = new HttpPost(host + requestURI);
        httpPost.setConfig(requestConfig);
        httpPost.setHeader("Authorization", "key=" + fcmConfig.getKey());
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        try {
            String str = objectMapper.writeValueAsString(body);
            StringEntity stringEntity = new StringEntity(str, "UTF-8");
            log.info("Sending push to iOS {}", str);
            httpPost.setEntity(stringEntity);
            try (CloseableHttpResponse execute = httpClient.execute(httpPost)) {
                if (execute.getStatusLine().getStatusCode() != 200) {
                    log.error("Push notification send error. Reason: " + execute.getStatusLine().getReasonPhrase());
                } else {
                    log.info("Push notification sent successfully");
                }
            }
        } catch (Exception e) {
            throw new PushSendException(e);
        }
    }
}
