package com.hackanet.exceptions;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/12/19
 */
@Slf4j
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {
        log.error("Forbidden exception has been thrown");
    }

    public UnauthorizedException(String message) {
        super(message);
        log.error(message);
    }
}
