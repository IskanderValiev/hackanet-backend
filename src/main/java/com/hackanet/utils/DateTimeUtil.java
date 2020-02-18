package com.hackanet.utils;

import com.hackanet.application.AppConstants;
import com.hackanet.exceptions.BadRequestException;
import com.hackanet.models.UserNotificationSettings;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;

import static java.lang.System.currentTimeMillis;

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

    public static LocalDateTime getRegistrationDate(Long date, boolean start, Date end) {
        LocalDateTime retVal;
        if (start) {
            retVal = date == null
                    ? LocalDateTime.now()
                    : epochToLocalDateTime(date);
            return retVal;
        }
        retVal = date == null
                ? end.toLocalDate().minusDays(1).atTime(23, 59)
                : epochToLocalDateTime(date);
        return retVal;
    }

    public static void validateRegistrationDates(Long regStartDate, Long regEndDate, Date start, Date end) {
        if (regStartDate > regEndDate)
            throw new BadRequestException("Registration Start Date is after End Date");
        if (regEndDate < System.currentTimeMillis())
            throw new BadRequestException("Registration End Date is in the past");
        if (new Timestamp(regStartDate).after(start))
            throw new BadRequestException("Registration Start Date must be before hackathon start date");
        if (new Timestamp(regEndDate).after(end))
            throw new BadRequestException("Registration End Date must be before hackathon end date");
    }

    public static void validateHackathonDates(Date start, Date end) {
        if (start.after(end)) {
            throw new BadRequestException("Start date is after end date");
        }
        if (start.before(new Date(currentTimeMillis()))) {
            throw new BadRequestException("Start date is in the past");
        }
    }
}
