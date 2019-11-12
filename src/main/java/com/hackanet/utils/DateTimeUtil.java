package com.hackanet.utils;

import com.hackanet.application.AppConstants;
import com.hackanet.models.UserNotificationSettings;

import java.sql.Time;
import java.sql.Timestamp;
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

    public static LocalDateTime timestampToLocalDateTime(Timestamp timestamp) {
        return timestamp.toLocalDateTime();
    }

    public static Timestamp addDaysAndHoursToTimestamp(Timestamp timestamp, Integer days, Integer hours) {
        LocalDateTime localDateTime = timestamp.toLocalDateTime().plusDays(days).plusHours(hours);
        timestamp = new Timestamp(localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli());
        return timestamp;
    }

    public static Timestamp getAvailableTime(UserNotificationSettings settings, Timestamp timestamp) {
        if (settings.getDontDisturbTo() == null || settings.getDontDisturbFrom() == null)
            return timestamp;
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        LocalTime localTime = localDateTime.toLocalTime();
        if (localTime.isBefore(settings.getDontDisturbTo()) && localTime.isAfter(settings.getDontDisturbFrom()))
            return Timestamp.valueOf(localDateTime);

        LocalDateTime availableTimeInGMT = localDateTime.plusDays(1).withHour(settings.getDontDisturbTo().getHour()).withMinute(0).withSecond(0);
        LocalDateTime availableTimeInUTC = utcLocalTimeRelativeToZoneOffset(availableTimeInGMT.toLocalTime(), AppConstants.MOSCOW_ZONE_OFFSET).atDate(availableTimeInGMT.toLocalDate());
        return Timestamp.valueOf(availableTimeInUTC);
    }

    private static LocalTime utcLocalTimeRelativeToZoneOffset(LocalTime localTime, ZoneOffset zoneOffset) {
        return OffsetTime.of(localTime, zoneOffset).withOffsetSameInstant(ZoneOffset.UTC).toLocalTime();
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

    public static boolean isNowDontDisturbTime(LocalTime from, LocalTime to) {
        if (from == null || to == null)
            return false;

        LocalTime now = LocalTime.now();
        return now.isBefore(from) & now.isAfter(to);
    }

    public static Long getAvailableTimeFotNotifications(UserNotificationSettings settings) {
        LocalTime now = LocalTime.now();
        long nowInMills = (now.getSecond() * 1000);
        if (settings.getDontDisturbFrom() == null || settings.getDontDisturbTo() == null) {
            return nowInMills;
        }

        LocalTime fromLT = settings.getDontDisturbFrom();
        LocalTime toLT = settings.getDontDisturbTo();
        if (now.isAfter(fromLT) && now.isBefore(toLT))
            return nowInMills;
        return (long) (fromLT.toSecondOfDay() * 1000);
    }
}
