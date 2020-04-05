package com.hackanet.services;

import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.hackathon.HackathonProfileView;
import com.hackanet.models.User;

import java.sql.Date;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/3/19
 */
public interface HackathonProfileViewService {
    HackathonProfileView addView(User user, Hackathon hackathon);
    Long countOfUniqueViewsInPeriod(User user, Long hackathonId, Date from, Date to);
    Long countByHackathonId(Long hackathonId);
}
