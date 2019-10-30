package com.hackanet.services.push;

import com.hackanet.exceptions.PushSendException;
import com.hackanet.push.ResolvedPush;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/30/19
 */
@Component("androidPushNotificationSender")
public class AndroidPushNotificationSender implements PushNotificationSender {
    @Override
    public void sendPush(PushNotificationMsg pushNotificationMsg, ResolvedPush resolvedPush, String tokens) throws PushSendException {

    }
}
