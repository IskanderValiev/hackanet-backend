package com.hackanet.services.hackathon;

import com.hackanet.json.dto.HackathonProfileViewFullStatisticDto;
import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.hackathon.HackathonProfileView;
import com.hackanet.models.user.User;
import com.hackanet.repositories.hackathon.HackathonProfileViewRepository;
import com.hackanet.security.utils.SecurityUtils;
import com.hackanet.utils.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
                .timestamp(LocalDateTime.now())
                .build();
        hackathonProfileView = hackathonProfileViewRepository.save(hackathonProfileView);
        return hackathonProfileView;
    }

    @Override
    public Long countOfUniqueViewsInPeriod(User user, Long hackathonId, Long from, Long to) {
        Hackathon hackathon = hackathonService.get(hackathonId);
        SecurityUtils.checkHackathonAccess(hackathon, user);
        final LocalDateTime fromLDT = DateTimeUtil.epochToLocalDateTime(from);
        final LocalDateTime toLDT = DateTimeUtil.epochToLocalDateTime(to);
        return hackathonProfileViewRepository.countOfUniqueViewsInPeriod(hackathonId, fromLDT, toLDT);
    }

    @Override
    public Long countOfViewsInPeriod(Long hackathonId, User user, Long from, Long to) {
        final Hackathon hackathon = hackathonService.get(hackathonId);
        SecurityUtils.checkHackathonAccess(hackathon, user);
        final LocalDateTime fromLDT = DateTimeUtil.epochToLocalDateTime(from);
        final LocalDateTime toLDT = DateTimeUtil.epochToLocalDateTime(to);
        return hackathonProfileViewRepository.countByHackathonIdAndTimestampBetween(hackathonId, fromLDT, toLDT);
    }

    @Override
    public Long countByHackathonId(Long hackathonId) {
        return hackathonProfileViewRepository.countAllByHackathonId(hackathonId);
    }

    @Override
    public HackathonProfileViewFullStatisticDto getStatisticHourly(Long hackathonId, User user, Long from, Long to) {
        final Hackathon hackathon = hackathonService.get(hackathonId);
        SecurityUtils.checkHackathonAccess(hackathon, user);
        final List<LocalDateTime> hours = DateTimeUtil.splitUpByHours(from, to);
        final LocalDateTime fromLDT = DateTimeUtil.epochToLocalDateTime(from);
        final LocalDateTime toLDT = DateTimeUtil.epochToLocalDateTime(to);
        final List<HackathonProfileView> views =
                hackathonProfileViewRepository.findByHackathonIdAndTimestampBetween(hackathonId, fromLDT, toLDT);
        List<HackathonProfileViewFullStatisticDto.HackathonProfileViewStatisticDto> statistics = new ArrayList<>();
        for (int i = 0; i < hours.size() - 1; i++) {
            final LocalDateTime start = hours.get(i);
            final LocalDateTime end = hours.get(i + 1);
            final long count = views.stream()
                    .filter(view -> DateTimeUtil.isBetween(view.getTimestamp(), start, end))
                    .count();
            statistics.add(new HackathonProfileViewFullStatisticDto.HackathonProfileViewStatisticDto(count, DateTimeUtil.localDateTimeToLong(start)));
        }
        return new HackathonProfileViewFullStatisticDto(statistics);
    }

    @Override
    public HackathonProfileViewFullStatisticDto getStatisticDaily(Long hackathonId, User user, Long from, Long to) {
        final Hackathon hackathon = hackathonService.get(hackathonId);
        SecurityUtils.checkHackathonAccess(hackathon, user);
        final List<LocalDateTime> days = DateTimeUtil.splitByDays(from, to);
        final LocalDateTime fromLDT = DateTimeUtil.epochToLocalDateTime(from);
        final LocalDateTime toLDT = DateTimeUtil.epochToLocalDateTime(to);
        final List<HackathonProfileView> views =
                hackathonProfileViewRepository.findByHackathonIdAndTimestampBetween(hackathonId, fromLDT, toLDT);
        List<HackathonProfileViewFullStatisticDto.HackathonProfileViewStatisticDto> statistics = new ArrayList<>();
        for (int i = 0; i < days.size() - 1; i++) {
            final LocalDateTime start = days.get(i);
            final LocalDateTime end = days.get(i + 1);
            final long count = views.stream()
                    .filter(view -> DateTimeUtil.isBetween(view.getTimestamp(), start, end))
                    .count();
            statistics.add(new HackathonProfileViewFullStatisticDto.HackathonProfileViewStatisticDto(count, DateTimeUtil.localDateTimeToLong(start)));
        }
        return new HackathonProfileViewFullStatisticDto(statistics);
    }
}
