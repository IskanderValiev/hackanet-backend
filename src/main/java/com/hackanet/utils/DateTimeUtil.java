package com.hackanet.utils;

import com.hackanet.application.AppConstants;
import com.hackanet.json.forms.HackathonCreateForm;
import com.hackanet.models.UserNotificationSettings;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;

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
        return localDateTimeToLong(localTime.atDate(localDate));
    }

    public static long localDateTimeToLong(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
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
        LocalDateTime availableTime = getAvailableTimeForLocalDateTime(settings, timestamp.toLocalDateTime());
        return Timestamp.valueOf(availableTime);
    }

    public static LocalDateTime getAvailableTimeForLocalDateTime(UserNotificationSettings settings, LocalDateTime localDateTime) {
        LocalTime localTime = localDateTime.toLocalTime();
        if (localTime.isBefore(settings.getDontDisturbTo()) && localTime.isAfter(settings.getDontDisturbFrom()))
            return localDateTime;

        LocalDateTime availableTimeInGMT = localDateTime.plusDays(1).withHour(settings.getDontDisturbTo().getHour()).withMinute(0).withSecond(0);
        LocalDateTime availableTimeInUTC = utcLocalTimeRelativeToZoneOffset(availableTimeInGMT.toLocalTime(), AppConstants.MOSCOW_ZONE_OFFSET).atDate(availableTimeInGMT.toLocalDate());
        return availableTimeInUTC;
    }

    private static LocalTime utcLocalTimeRelativeToZoneOffset(LocalTime localTime, ZoneOffset zoneOffset) {
        return OffsetTime.of(localTime, zoneOffset).withOffsetSameInstant(ZoneOffset.UTC).toLocalTime();
    }

    public static LocalDateTime convertLocalDateTimeToUtc(LocalDateTime localDateTime, TimeZone timeZone) {
        TimeZone utcTimeZone = timeZone;
        Time offset = new Time(utcTimeZone.getRawOffset());
        LocalTime offsetLocalTime = offset.toLocalTime();
        return localDateTime.minusHours(offsetLocalTime.getHour());
    }

    public static long getDifferenceBetweenLocalDateTimes(LocalDateTime from, LocalDateTime to) {
        if (to == null)
            to = LocalDateTime.now();
        LocalDateTime tempDateTime = LocalDateTime.from(from);
        return tempDateTime.until(to, ChronoUnit.MINUTES);
    }

    public static LocalDateTime epochToLocalDateTime(Long epochMillis) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(epochMillis / 1000), ZoneId.systemDefault());
    }

    public static boolean isNowDontDisturbTime(LocalTime from, LocalTime to) {
        if (from == null || to == null)
            return false;

        LocalTime now = LocalTime.now();
        return now.isBefore(from) & now.isAfter(to);
    }

    public static Long getAvailableTimeForNotifications(UserNotificationSettings settings) {
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

    public static Date now() {
        return new Date(System.currentTimeMillis());
    }

    public static LocalDateTime getRegistrationLocalDateTimeFromForm(HackathonCreateForm form, boolean start) {
        if (start) {
            return form.getRegistrationStartDate() == null
                    ? LocalDateTime.now()
                    : epochToLocalDateTime(form.getRegistrationStartDate());
        } else {
            Date end = new Date(form.getEnd());
            return form.getRegistrationEndDate() == null
                    ? end.toLocalDate().minusDays(1).atTime(23, 59)
                    : epochToLocalDateTime(form.getRegistrationEndDate());
        }
    }
}
