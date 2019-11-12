package com.hackanet.utils;

import org.quartz.*;

import java.util.Date;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/10/19
 */
public class JobUtils {

    public static Trigger createTimeTrigger(Date executionTime, JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .startAt(executionTime)
                .build();
    }

    public static JobDetail createDurableJob(JobDataMap jobDataMap, Class<? extends Job> jobClass) {
        JobBuilder jobBuilder = JobBuilder.
                newJob(jobClass)
                .storeDurably(true);
        if (jobDataMap != null) {
            jobBuilder.setJobData(jobDataMap);
        }
        return jobBuilder.build();
    }
}
