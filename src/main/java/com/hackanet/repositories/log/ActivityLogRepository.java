package com.hackanet.repositories.log;

import com.hackanet.models.log.ActivityLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ActivityLogRepository extends ElasticsearchRepository<ActivityLog, String> {
}
