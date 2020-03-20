package com.hackanet.utils.log;

import com.hackanet.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.logging.LogLevel;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/18/20
 */
@Slf4j
public class LogUtils {

    public static void logMessage(User user, @NotNull LogLevel level) {
        if (LogLevel.INFO.equals(level)) {
            log.info(getLogMessage(user));
        }
    }

    public static String getLogMessage(User user) {
        StringBuilder message = new StringBuilder("\nUser: " + getUserEmail(user) + "\n" +
                         "Date: " + LocalDateTime.now() + "\n" +
                         "Stacktrace:\n");
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stackTrace.length && i < 10; i++) {
            StackTraceElement element = stackTrace[i];
            message.append("    ").append(element).append("\n");
        }
        return message.toString();
    }

    private static String getUserEmail(User user) {
        return user == null ? "anonymous" : user.getEmail();
    }
}
