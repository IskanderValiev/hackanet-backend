package com.hackanet.exceptions;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Iskander Valiev
 * created by isko
 * on 4/7/20
 */
@Slf4j
public class AlreadyExistException extends RuntimeException {

    public AlreadyExistException() {
        log.error(this.getClass().getName() + " has been thrown.");
    }

    public AlreadyExistException(String message) {
        super(message);
        log.error(this.getClass().getName() + ": " + message);
    }

    public static void throwException(Class<?> aClass, String paramName, String value) {
        throw new AlreadyExistException(aClass.getName() + " with " + paramName + " = " + value + " already exists");
    }
}
