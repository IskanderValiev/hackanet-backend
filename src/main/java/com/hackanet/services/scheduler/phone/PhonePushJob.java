package com.hackanet.services.scheduler.phone;

import com.hackanet.models.*;
import com.hackanet.models.chat.Message;
import com.hackanet.services.*;
import com.hackanet.services.chat.ChatMessageServiceElasticsearchImpl;
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

    @Autowired
    private ChatMessageServiceElasticsearchImpl chatMessageServiceElasticsearch;

    @Autowired
    private ConnectionInvitationService connectionInvitationService;

    @Autowired
    private TeamInvitationService teamInvitationService;

    @Autowired
    private JoinToTeamRequestService joinToTeamRequestService;

    @Autowired
    private PostService postService;

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
                JobOffer jobOffer = jobOfferService.get(invitationId);
                rabbitMQPushNotificationService.sendJobInvitationNotification(user, jobOffer);
                break;
            case NEW_MESSAGE:
                String messageId = (String) jobDataMap.get(ENTITY_ID);
                Message message = chatMessageServiceElasticsearch.getById(messageId);
                rabbitMQPushNotificationService.sendNewMessageNotification(message);
                break;
            case CONNECTION_INVITATION:
                Long connectionInvitationId = (Long) jobDataMap.get(ENTITY_ID);
                ConnectionInvitation connectionInvitation = connectionInvitationService.get(connectionInvitationId);
                rabbitMQPushNotificationService.sendConnectionInvitationNotification(user, connectionInvitation);
                break;
            case TEAM_INVITATION:
                Long teamInvitationId = (Long) jobDataMap.get(ENTITY_ID);
                TeamInvitation teamInvitation = teamInvitationService.get(teamInvitationId);
                rabbitMQPushNotificationService.sendTeamInvitationNotification(user, teamInvitation);
                break;
            case TEAM_INVITATION_CHANGED_STATUS:
                teamInvitationId = (Long) jobDataMap.get(ENTITY_ID);
                teamInvitation = teamInvitationService.get(teamInvitationId);
                rabbitMQPushNotificationService.sendTeamInvitationUpdatedStatus(user, teamInvitation);
                break;
            case JOIN_TO_TEAM_REQUEST_STATUS:
                Long joinToTeamRequestId = (Long) jobDataMap.get(ENTITY_ID);
                JoinToTeamRequest request = joinToTeamRequestService.get(joinToTeamRequestId);
                rabbitMQPushNotificationService.sendJoinToTeamRequestUpdatedStatusNotification(user, request);
                break;
            case NEW_POST:
                Long postId = (Long) jobDataMap.get(ENTITY_ID);
                Post post = postService.get(postId);
                rabbitMQPushNotificationService.sendNewPostNotification(user, post);
                break;
        }

    }
}
