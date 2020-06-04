package com.hackanet.utils.validators;

import com.hackanet.models.hackathon.Hackathon;

import java.sql.Date;
import java.time.LocalDateTime;

import static com.google.common.base.Preconditions.checkArgument;
/**
 * @author Iskander Valiev
 * created by isko
 * on 12/26/19
 */
public class HackathonRegistrationDateValidator {

    public static void registrationIsAvailable(Hackathon hackathon) {
        checkArgument(hackathon.getRegistrationEndDate().isAfter(LocalDateTime.now()), "Registration for the hackathon has already finished");
        checkArgument(hackathon.getRegistrationStartDate().isBefore(LocalDateTime.now()), "Registration for the hackathon has not started yet");

        Date now = new Date(System.currentTimeMillis());
        checkArgument(now.before(hackathon.getStartDate()), "Hackathon has already started or passed");
    }
}
