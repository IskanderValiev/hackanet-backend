package com.hackanet.services.scheduler;

import com.hackanet.models.*;
import com.hackanet.models.chat.Message;
import com.hackanet.models.post.Post;
import com.hackanet.models.team.Team;
import com.hackanet.models.team.TeamInvitation;
import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.user.User;
import com.hackanet.models.user.UserNotificationSettings;
import com.hackanet.services.SubscriptionService;
import com.hackanet.services.scheduler.phone.PhonePushJob;
import com.hackanet.utils.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

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

    @Autowired
    private SubscriptionService subscriptionService;

    public void addHackathonJobReviewRequestJobToTeamLeader(@Nullable UserNotificationSettings settings, User user, Team team) {
        Hackathon hackathon = team.getHackathon();
        Timestamp timestamp = new Timestamp(hackathon.getEndDate().getTime());
        this.addJob(settings, user.getId(), hackathon.getId(), JobType.HACKATHON_JOB_REVIEW_REQUEST, timestamp.getTime());
    }

    public void addJobInvitationJobNotification(@Nullable UserNotificationSettings settings, JobOffer jobOffer) {
        this.addJob(settings, jobOffer.getUser().getId(), jobOffer.getId(), JobType.JOB_INVITATION, null);
    }

    public void addConnectionInvitationNotification(@Nullable UserNotificationSettings settings, ConnectionInvitation connectionInvitation) {
        this.addJob(settings, connectionInvitation.getInvitedUser().getId(), connectionInvitation.getId(), JobType.CONNECTION_INVITATION, null);
    }

    public void addTeamInvitationNotification(@Nullable UserNotificationSettings settings, TeamInvitation teamInvitation) {
        this.addJob(settings, teamInvitation.getUser().getId(), teamInvitation.getId(), JobType.TEAM_INVITATION , null);
    }

    public void addTeamInvitationChangedStatusNotification(@Nullable UserNotificationSettings settings, TeamInvitation teamInvitation) {
        this.addJob(settings, teamInvitation.getTeam().getTeamLeader().getId(), teamInvitation.getId(), JobType.TEAM_INVITATION_CHANGED_STATUS, null);
    }

    public void addNewMessageNotification(@Nullable UserNotificationSettings settings, Message message) {
        this.addJob(null, message.getSenderId(), message.getId(), JobType.NEW_MESSAGE, null);
    }

    public void addNewPostNotification(@Nullable UserNotificationSettings settings, Post post) {
        // TODO: 12/6/19 send notification about new post to users who subscribes the hackathon
        Hackathon hackathon = post.getHackathon();
        List<User> subscriptions = subscriptionService.getAllSubscribersByHackathonId(hackathon.getId());
        subscriptions.forEach(user ->
                this.addJob(null, user.getId(), post.getId(), JobType.NEW_POST, null));
    }

    private void addJob(@Nullable UserNotificationSettings settings, Long userId, Object entityId, JobType jobType, @Nullable Long preferredExecutionTime) {
        if (preferredExecutionTime == null) {
            preferredExecutionTime = System.currentTimeMillis();
        }
        Timestamp executionTime = new Timestamp(preferredExecutionTime);
        if (settings != null) {
            executionTime = DateTimeUtil.getAvailableTime(settings, executionTime);
        }
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(JobDataMapKeys.USER_ID, userId);
        jobDataMap.put(JobDataMapKeys.ENTITY_ID, entityId);
        jobDataMap.put(JobDataMapKeys.JOB_TYPE, jobType);
        JobDetail pushJob = createDurableJob(jobDataMap, PhonePushJob.class);
        Trigger trigger = createTimeTrigger(executionTime, pushJob);
        try {
            scheduler.scheduleJob(pushJob, trigger);
        } catch (SchedulerException e) {
            log.error("Push job with type = " + jobType.toString() + " has failed. Reason: " + e.getMessage());
        }
    }
}
