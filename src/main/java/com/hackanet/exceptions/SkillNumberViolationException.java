package com.hackanet.exceptions;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Iskander Valiev
 * created by isko
 * on 2/18/20
 */
@Slf4j
public class SkillNumberViolationException extends RuntimeException {

    public SkillNumberViolationException() {
        super("More than 3 or null skills have been mentioned");
        log.error(SkillNumberViolationException.class.getName() + " has been thrown. Reason: more than 3 skills have been chosen.");
    }

    public SkillNumberViolationException(String message) {
        super(message);
        log.error(message, new SkillNumberViolationException());
    }
}
