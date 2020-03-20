package com.hackanet.aspects;

import com.hackanet.services.log.ActivityLogService;
import lombok.extern.slf4j.Slf4j;
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
 * on 3/18/20
 */
@Aspect
@Component
@Slf4j
public class HackathonActivityLoggerAspect {

    @Autowired
    private ActivityLogService activityLogService;

    @Pointcut("execution(public * com.hackanet.controllers.HackathonController.*(..))")
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
