package com.hackanet.services.push;

import com.hackanet.exceptions.PushSendException;
import com.hackanet.push.ResolvedPush;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/30/19
 */
public interface PushNotificationSender {
    void sendPush(PushNotificationMsg pushNotificationMsg, ResolvedPush resolvedPush, String tokens) throws PushSendException;
}
