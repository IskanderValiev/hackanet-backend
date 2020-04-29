package com.hackanet.exceptions;

/**
 * @author Iskander Valiev
 * created by isko
 * on 4/29/20
 */
public class BlockedUserException extends RuntimeException {

    public BlockedUserException() {
        super("The user has been blocked");
    }

    public BlockedUserException(String message) {
        super(message);
    }
}
