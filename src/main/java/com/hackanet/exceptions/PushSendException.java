package com.hackanet.exceptions;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/30/19
 */
public class PushSendException extends Exception {
    public PushSendException(String message) {
        super(message);
    }

    public PushSendException(Throwable cause) {
        super(cause);
    }
}
