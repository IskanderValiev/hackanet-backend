package com.hackanet.utils;

import java.time.*;
import java.time.temporal.ChronoUnit;

/**
 * @author Iskander Valiev
 * created by isko
 * on 2/26/19
 */
public class DateTimeUtil {

    public static LocalTime longToLocalTime(Long mills) {
        LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochMilli(mills), ZoneId.systemDefault());
        return time.toLocalTime();
    }

    public static long localTimeToLong(LocalTime localTime) {
        LocalDate localDate = LocalDate.of(1970, Month.JANUARY, 1);
        LocalDateTime localDateTime = localTime.atDate(localDate);
        long mills = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return mills;
    }

    public static long getDifferenceBetweenLocalDateTimes(LocalDateTime from, LocalDateTime to) {
        if (to == null)
            to = LocalDateTime.now();
        LocalDateTime tempDateTime = LocalDateTime.from(from);
        return tempDateTime.until(to, ChronoUnit.MINUTES);
    }

    public static LocalDateTime epochToLocalDateTime(Long epochSeconds) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneId.systemDefault());
    }
}
