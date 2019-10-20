package com.hackanet.exceptions;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/20/19
 */
@Slf4j
public class BadRequestException extends RuntimeException {

    public BadRequestException() {
        log.error("Bad request exception has been thrown");
    }

    public BadRequestException(String message) {
        super(message);
        log.error(message);
    }

    public BadRequestException(Throwable cause) {
        super(cause);
        log.error(cause.getMessage());
    }

    public static BadRequestException forUploadingFile() {
        return new BadRequestException("Bad request has been occurred during uploading the file");
    }
}
