package com.hackanet.exceptions;

/**
 * @author Iskander Valiev
 * created by isko
 * on 12/18/19
 */
public class BlackListException extends ForbiddenException {
    public BlackListException(String message) {
        super(message);
    }
}
