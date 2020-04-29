package com.hackanet.aspects;

import com.hackanet.exceptions.BlockedUserException;
import com.hackanet.models.user.User;
import com.hackanet.services.BlockedUserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 4/29/20
 */
@Aspect
@Component
public class BlockedUserCheckAspect {

    @Autowired
    private BlockedUserService blockedUserService;

    @Pointcut("execution(public * com.hackanet.controllers.*Controller.*(..))")
    public void check() {}

    @Before("check()")
    public void beforeCheck(JoinPoint jp) {
        final Object[] args = jp.getArgs();
        for (Object arg : args) {
            if (arg instanceof User) {
                User user = (User) arg;
                final boolean blocked = blockedUserService.isBlocked(user);
                if (blocked) {
                    throw new BlockedUserException();
                }
            }
        }
    }

    @After("check()")
    public void afterCheck(JoinPoint jp) {

    }
}
