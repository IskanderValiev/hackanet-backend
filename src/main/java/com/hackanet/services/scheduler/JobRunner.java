package com.hackanet.services.scheduler;

import com.hackanet.models.*;
import com.hackanet.services.scheduler.email.EmailJob;
import com.hackanet.utils.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

import static com.hackanet.utils.JobUtils.createDurableJob;
import static com.hackanet.utils.JobUtils.createTimeTrigger;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/10/19
 */
@Component
@Slf4j
public class JobRunner {

    @Autowired
    private Scheduler scheduler;

    public void addHackathonJobReviewRequestJobToTeamLeader(UserNotificationSettings settings, User user, Team team) {
        Hackathon hackathon = team.getHackathon();
        Timestamp timestamp = new Timestamp(hackathon.getEndDate().getTime());
        timestamp = DateTimeUtil.addDaysAndHoursToTimestamp(timestamp, 1, 0);
        Timestamp executionTime = DateTimeUtil.getAvailableTime(settings, timestamp);
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(JobDataMapKeys.USER_ID, user.getId());
        jobDataMap.put(JobDataMapKeys.TEAM_ID, team.getId());
        jobDataMap.put(JobDataMapKeys.JOB_TYPE, JobType.HACKATHON_JOB_REVIEW_REQUEST);
        JobDetail emailJobDetail = createDurableJob(jobDataMap, EmailJob.class);
        Trigger trigger = createTimeTrigger(executionTime, emailJobDetail);
        log.info("Trigger's execution time is {}", executionTime.toString());
        try {
            scheduler.scheduleJob(emailJobDetail, trigger);
        } catch (SchedulerException e) {
            log.debug("User email job scheduling failed", e);
        }
    }
}
