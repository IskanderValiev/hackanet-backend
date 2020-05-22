package com.hackanet.exceptions;

/**
 * @author Iskander Valiev
 * created by isko
 * on 5/21/20
 */
public class CensorViolationException extends RuntimeException {
    public CensorViolationException(String s) {
        super(s);
    }
}
