package com.hackanet.services.log;

import com.hackanet.models.log.ActivityLog;

import java.util.List;

public interface ActivityLogService {
    void saveLog(Object... args);
    List<ActivityLog> findAll();
    void deleteAll();
}
