package com.hackanet.services.scheduler.email;

import com.hackanet.models.Team;
import com.hackanet.models.User;
import com.hackanet.services.EmailService;
import com.hackanet.services.TeamService;
import com.hackanet.services.UserService;
import com.hackanet.services.scheduler.JobType;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import static com.hackanet.services.scheduler.JobDataMapKeys.*;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/10/19
 */
public class EmailJob implements Job {

    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private TeamService teamService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        JobType jobType = (JobType) jobDataMap.get(JOB_TYPE);

        Long userId = (Long) jobDataMap.get(USER_ID);
        User user = userService.get(userId);
        switch (jobType) {
            case HACKATHON_JOB_REVIEW_REQUEST:
                Long teamId = (Long) jobDataMap.get(TEAM_ID);
                Team team = teamService.get(teamId);
                emailService.sendHackathonJobReviewRequestEmail(user, team);
                break;
        }
    }
}
