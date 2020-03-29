package com.hackanet.services;

import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.PasswordChangeRequest;
import com.hackanet.models.team.Team;
import com.hackanet.models.User;

public interface TemplateService {
    String prepareEmail(User user);
    String prepareWelcomeEmail(User user);
    String test();
    String prepareResetPasswordEmail(User user, PasswordChangeRequest request);
    String prepareTeamWelcomeEmail(User user, Team team);
    String prepareTeamRejectEmail(User user, Team team);
    String prepareHackathonWelcomeEmail(User user, Hackathon hackathon);
    String prepareHackathonJobReviewRequestEmail(User user, Team team);
}
