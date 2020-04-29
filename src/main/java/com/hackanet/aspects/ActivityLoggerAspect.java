package com.hackanet.aspects;

import com.hackanet.models.user.User;
import com.hackanet.services.log.ActivityLogService;
import com.hackanet.services.user.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/22/20
 */
@Aspect
@Component
@Profile(value = "local")
public class ActivityLoggerAspect {

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private UserService userService;

    @Pointcut("execution(public * com.hackanet.controllers.*Controller.*(..))")
    public void addActivityLog() {

    }

    @Before("addActivityLog()")
    public void beforeAddActivityLog(JoinPoint jp) {
        activityLogService.saveLog(jp.getArgs());
    }

    @After("addActivityLog()")
    public void afterAddActivityLog(JoinPoint jp) {
        for (Object arg : jp.getArgs()) {
            if (arg instanceof User) {
                User user  = (User) arg;
                userService.updateLastRequestTime(user);
            }
        }
    }
}
