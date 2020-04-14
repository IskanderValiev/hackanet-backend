package com.hackanet.services.hackathon;

import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.hackathon.HackathonProfileView;
import com.hackanet.models.user.User;
import com.hackanet.repositories.hackathon.HackathonProfileViewRepository;
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
    public Long countOfUniqueViewsInPeriod(User user, Long hackathonId, Date from, Date to) {
        Hackathon hackathon = hackathonService.get(hackathonId);
        checkHackathonAccess(hackathon, user);
        return hackathonProfileViewRepository.countOfUniqueViewsInPeriod(hackathonId, from, to);
    }

    @Override
    public Long countByHackathonId(Long hackathonId) {
        return hackathonProfileViewRepository.countAllByHackathonId(hackathonId);
    }
}
