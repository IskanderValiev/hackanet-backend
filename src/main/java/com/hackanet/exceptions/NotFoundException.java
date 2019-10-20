package com.hackanet.exceptions;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/19/19
 */
@Slf4j
public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super();
        log.error("Not found exception has been thrown");
    }

    public NotFoundException(String message) {
        super(message);
        log.error(message);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
        log.error(cause.getMessage());
    }

    public static NotFoundException forHackathon(Long id) {
        return new NotFoundException("Hackathon with id=" + id + " not found");
    }

    public static NotFoundException forFileInfo(Long id) {
        return new NotFoundException("Information about file with id=" + id + " not found");
    }

    public static NotFoundException forFileInfo(String name) {
        return new NotFoundException("Information about file with name=" + name + " not found");
    }

    public static NotFoundException forUser(String email) {
        return new NotFoundException("User with email=" + email + " not found");
    }
}
