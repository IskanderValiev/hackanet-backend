package com.hackanet.services.log;

import com.google.common.collect.Lists;
import com.hackanet.json.dto.activity.log.UserLocationInfo;
import com.hackanet.json.mappers.activity.log.ActivityUserMapper;
import com.hackanet.json.mappers.activity.log.RequestMapper;
import com.hackanet.json.mappers.activity.log.UserLocationInfoMapper;
import com.hackanet.models.User;
import com.hackanet.models.log.ActivityLog;
import com.hackanet.repositories.log.ActivityLogRepository;
import com.hackanet.utils.StringUtils;
import com.maxmind.geoip2.model.CityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/18/20
 */
@Service
public class ActivityLogServiceImpl implements ActivityLogService {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private ActivityUserMapper userMapper;

    @Autowired
    private RequestMapper requestMapper;

    @Autowired
    private UserLocationInfoMapper userLocationInfoMapper;

    @Autowired
    private UserLocationService userLocationService;

    @Override
    public void saveLog(Object... args) {
        ActivityLog.ActivityLogBuilder activityLogBuilder = ActivityLog.builder()
                .id(StringUtils.generateRandomString())
                .date(new Timestamp(System.currentTimeMillis()));

        for (Object arg : args) {
            if (arg instanceof User) {
                User user = (User) arg;
                activityLogBuilder.user(userMapper.map(user));
            }
            if (arg instanceof HttpServletRequest) {
                HttpServletRequest request = (HttpServletRequest) arg;
                activityLogBuilder.request(requestMapper.map(request));

                CityResponse cityResponse = userLocationService.getCountry(request.getRemoteAddr());
                UserLocationInfo userLocationInfo = userLocationInfoMapper.map(cityResponse);
                activityLogBuilder.userLocationInfo(userLocationInfo);
            }
        }
        activityLogRepository.save(activityLogBuilder.build());
    }

    @Override
    public List<ActivityLog> findAll() {
        Iterable<ActivityLog> iterableAll = activityLogRepository.findAll();
        ArrayList<ActivityLog> logs = Lists.newArrayList(iterableAll);
        return logs.stream()
                .sorted(Comparator.comparing(ActivityLog::getDate))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAll() {
        activityLogRepository.deleteAll();
    }
}
