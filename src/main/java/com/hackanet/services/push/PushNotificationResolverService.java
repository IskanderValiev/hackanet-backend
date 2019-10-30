package com.hackanet.services.push;

import com.google.common.collect.Lists;
import com.hackanet.push.ResolvedPush;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/30/19
 */
@Service
public class PushNotificationResolverService {

    public ResolvedPush resolve(PushNotificationMsg msg) {
        String title = "";
        String body = "";
        String key = msg.getType().getValue();
        List<String> bodyArgs = Lists.newArrayList();
        Map<String, Object> customData = new HashMap<>();
        switch (msg.getType()) {
//            case BONUS_ACCRUAL:
//                OrderPayload orderPayload = (OrderPayload) msg.getPayloadEntity();
//                bodyArgs.add(orderPayload.getBonuses().toString());
//                break;
//            case GIVE_FEEDBACK:
//                bodyArgs.add(AppConstants.BONUS_POINTS_COUNT_FOR_REVIEW.toString());
//                break;
//            case ORDER_STATUS:
//                orderPayload = (OrderPayload) msg.getPayloadEntity();
//                bodyArgs.add(orderPayload.getOrderStatus().getRusTitle());
//                break;
        }
        ResolvedPush resolvedPush = ResolvedPush.builder()
                .body(body)
                .title(title)
                .bodyArgs(bodyArgs)
                .additionalData(customData)
                .key(key)
                .build();
        return resolvedPush;
    }
}
