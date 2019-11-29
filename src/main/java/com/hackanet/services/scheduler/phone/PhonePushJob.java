package com.hackanet.services.scheduler.phone;

import com.hackanet.models.JobOffer;
import com.hackanet.models.Team;
import com.hackanet.models.User;
import com.hackanet.services.JobOfferService;
import com.hackanet.services.TeamService;
import com.hackanet.services.UserService;
import com.hackanet.services.push.RabbitMQPushNotificationService;
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
    @Autowired
    private JobOfferService jobOfferService;

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
                Long teamId = (Long) jobDataMap.get(ENTITY_ID);
                Team team = teamService.get(teamId);
                rabbitMQPushNotificationService.sendHackathonJobReviewRequestNotification(user, team);
                break;
            case JOB_INVITATION:
                Long invitationId = (Long) jobDataMap.get(ENTITY_ID);
                JobOffer invitation = jobOfferService.get(invitationId);

        }

    }
}
