package com.hackanet.services;

import com.hackanet.models.Hackathon;
import com.hackanet.models.HackathonProfileView;
import com.hackanet.models.User;
import com.hackanet.repositories.HackathonProfileViewRepository;
import com.hackanet.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;

import static com.hackanet.security.utils.SecurityUtils.*;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/3/19
 */
@Service
public class HackathonProfileViewServiceImpl implements HackathonProfileViewService {

    @Autowired
    private HackathonProfileViewRepository hackathonProfileViewRepository;
    @Autowired
    private HackathonService hackathonService;

    @Override
    public HackathonProfileView addView(User user, Hackathon hackathon) {
        HackathonProfileView hackathonProfileView = HackathonProfileView.builder()
                .hackathon(hackathon)
                .user(user)
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .build();

        hackathonProfileView = hackathonProfileViewRepository.save(hackathonProfileView);
        return hackathonProfileView;
    }

    @Override
    public Long countsOfUniqueViewInPeriod(User user, Long hackathonId, Date from, Date to) {
        Hackathon hackathon = hackathonService.get(hackathonId);
        checkHackathonAccess(hackathon, user);
        return hackathonProfileViewRepository.countOfUniqueViewsInPeriod(hackathonId, from, to);
    }
}
