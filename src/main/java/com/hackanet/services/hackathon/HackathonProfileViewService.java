package com.hackanet.services.hackathon;

import com.hackanet.json.dto.HackathonProfileViewFullStatisticDto;
import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.hackathon.HackathonProfileView;
import com.hackanet.models.user.User;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/3/19
 */
public interface HackathonProfileViewService {
    HackathonProfileView addView(User user, Hackathon hackathon);
    Long countOfUniqueViewsInPeriod(User user, Long hackathonId, Long from, Long to);
    Long countOfViewsInPeriod(Long hackathonId, User user, Long from, Long to);
    Long countByHackathonId(Long hackathonId);
    HackathonProfileViewFullStatisticDto getStatisticHourly(Long hackathonId, User user, Long from, Long to);
    HackathonProfileViewFullStatisticDto getStatisticDaily(Long hackathonId, User user, Long from, Long to);
}
