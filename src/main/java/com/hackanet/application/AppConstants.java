package com.hackanet.application;

import java.time.ZoneOffset;

public interface AppConstants {
    Integer CONTENT_MAX_LENGTH = 1024;
    Integer DEFAULT_LIMIT = 10;
    ZoneOffset MOSCOW_ZONE_OFFSET = ZoneOffset.of("+03:00");
    Integer ACCESS_TOKEN_EXPIRING_TIME_IN_HOURS = 4;
    Integer REFRESH_TOKEN_EXPIRING_TIME_IN_DAYS = 180;
    Long DEFAULT_PROFILE_IMAGE_ID = 1L;
    Long DEFAULT_HACKATHON_IMAGE_ID = 2L;
}
