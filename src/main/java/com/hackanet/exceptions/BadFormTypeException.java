package com.hackanet.exceptions;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Iskander Valiev
 * created by isko
 * on 2/12/20
 */
@Slf4j
public class BadFormTypeException extends RuntimeException {

    public BadFormTypeException() {
        log.error(this.getClass().getName() + " has been thrown");
    }

    public BadFormTypeException(String message) {
        super(message);
        log.error(message);
    }
}
