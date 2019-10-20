package com.hackanet.exceptions;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/20/19
 */
@Slf4j
public class ForbiddenException extends RuntimeException {

    public ForbiddenException() {
        log.error("Forbidden exception has been thrown");
    }

    public ForbiddenException(String message) {
        super(message);
        log.error(message);
    }

    public ForbiddenException(Throwable cause) {
        super(cause);
        log.error(cause.getMessage());
    }

    public static ForbiddenException forFileInfo(Long id) {
        return new ForbiddenException("Forbidden to access file with id=" + id);
    }
}
