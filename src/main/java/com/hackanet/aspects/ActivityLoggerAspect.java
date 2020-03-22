package com.hackanet.aspects;

import com.hackanet.services.log.ActivityLogService;
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
 * on 3/22/20
 */
@Aspect
@Component
public class ActivityLoggerAspect {

    @Autowired
    private ActivityLogService activityLogService;

    @Pointcut("execution(public * com.hackanet.controllers.*Controller.*(..))")
    public void addActivityLog() {

    }

    @Before("addActivityLog()")
    public void beforeAddActivityLog(JoinPoint jp) {
        activityLogService.saveLog(jp.getArgs());
    }

    @After("addActivityLog()")
    public void afterAddActivityLog(JoinPoint jp) {

    }
}
