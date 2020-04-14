package com.hackanet.services;

import com.hackanet.exceptions.NotFoundException;
import com.hackanet.models.Company;
import com.hackanet.models.JobOffer;
import com.hackanet.models.user.User;
import com.hackanet.models.user.UserNotificationSettings;
import com.hackanet.repositories.JobOfferRepository;
import com.hackanet.services.chat.ChatService;
import com.hackanet.services.scheduler.JobRunner;
import com.hackanet.services.user.UserNotificationSettingsService;
import com.hackanet.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.hackanet.security.utils.SecurityUtils.checkJobInvitationAccess;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/28/19
 */
@Service
public class JobOfferServiceImpl implements JobOfferService {

    @Autowired
    private JobOfferRepository jobOfferRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserNotificationSettingsService userNotificationSettingsService;

    @Autowired
    private JobRunner jobRunner;

    @Autowired
    private ChatService chatService;

    @Override
    public JobOffer create(User admin, Long userId) {
        User user = userService.get(userId);
        Company company = companyService.getByAdmin(admin);

        JobOffer invitation = JobOffer.builder()
                .company(company)
                .user(user)
                .time(LocalDateTime.now())
                .deletedForUser(Boolean.FALSE)
                .build();
        invitation = jobOfferRepository.save(invitation);

        UserNotificationSettings settings = userNotificationSettingsService.getOrCreateDefaultsSettingsForUser(user);
        jobRunner.addJobInvitationJobNotification(settings, invitation);
        return invitation;
    }

    @Override
    public List<JobOffer> getByUserId(Long userId) {
        List<JobOffer> invitations = jobOfferRepository.getByUserId(userId);
        return invitations.stream().filter(jobInvitation -> !Boolean.TRUE.equals(jobInvitation.isDeletedForUser())).collect(Collectors.toList());
    }

    @Override
    public JobOffer deleteForUser(User user, Long id) {
        JobOffer invitation = get(id);
        checkJobInvitationAccess(invitation, user, false);
        invitation.setDeletedForUser(true);
        return jobOfferRepository.save(invitation);
    }

    @Override
    public void delete(User user, Long id) {
        JobOffer invitation = get(id);
        checkJobInvitationAccess(invitation, user, true);
        jobOfferRepository.delete(invitation);
    }

    @Override
    @Transactional
    public void accept(User user, Long id) {
        JobOffer jobOffer = get(id);
        checkJobInvitationAccess(jobOffer, user, false);
        chatService.createForAcceptedJobInvitation(jobOffer);
    }

    @Override
    public JobOffer get(Long id) {
        return jobOfferRepository.findById(id).orElseThrow(() -> new NotFoundException("Job invitation with id=" + id + " not found"));
    }
}
