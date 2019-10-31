package com.hackanet.exceptions;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/31/19
 */
public class TemplateException extends RuntimeException {

    public TemplateException() {
    }

    public TemplateException(String message) {
        super(message);
    }

    public TemplateException(Throwable cause) {
        super(cause);
    }
}
