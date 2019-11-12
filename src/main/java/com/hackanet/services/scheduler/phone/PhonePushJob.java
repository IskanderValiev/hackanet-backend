package com.hackanet.services.scheduler.phone;

import com.hackanet.models.Team;
import com.hackanet.models.User;
import com.hackanet.services.TeamService;
import com.hackanet.services.UserService;
import com.hackanet.services.push.RabbitMQPushNotificationService;
import com.hackanet.services.scheduler.JobDataMapKeys;
import com.hackanet.services.scheduler.JobType;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.hackanet.services.scheduler.JobDataMapKeys.*;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/11/19
 */
@Service
public class PhonePushJob implements Job {

    @Autowired
    private UserService userService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private RabbitMQPushNotificationService rabbitMQPushNotificationService;

    /**
     *
     * the method is invoked once the trigger fires
     *
     * */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        JobType jobType = (JobType) jobDataMap.get(JOB_TYPE);

        Long userId = (Long) jobDataMap.get(USER_ID);
        User user = userService.get(userId);
        switch (jobType) {
            case HACKATHON_JOB_REVIEW_REQUEST:
                Long teamId = (Long) jobDataMap.get(TEAM_ID);
                Team team = teamService.get(teamId);
                rabbitMQPushNotificationService.sendHackathonJobReviewRequestNotification(user, team);
        }

    }
}
