package com.hackanet.application;

import java.time.ZoneOffset;

public interface AppConstants {
    final Integer CONTENT_MAX_LENGTH = 1024;
    final Integer DEFAULT_LIMIT = 10;
    ZoneOffset MOSCOW_ZONE_OFFSET = ZoneOffset.of("+03:00");
}
