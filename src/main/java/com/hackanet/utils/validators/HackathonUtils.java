package com.hackanet.utils.validators;

import com.hackanet.exceptions.BadRequestException;
import com.hackanet.models.Hackathon;

import java.sql.Date;
import java.time.LocalDateTime;

/**
 * @author Iskander Valiev
 * created by isko
 * on 12/26/19
 */
public class HackathonUtils {

    public static void registrationIsAvailable(Hackathon hackathon) {
        if (hackathon.getRegistrationEndDate().isBefore(LocalDateTime.now()))
            throw new BadRequestException("Registration for the hackathon has already finished");
        if (hackathon.getRegistrationStartDate().isAfter(LocalDateTime.now()))
            throw new BadRequestException("Registration for the hackathon has not started yet");
        Date now = new Date(System.currentTimeMillis());
        if (now.after(hackathon.getStartDate()))
            throw new BadRequestException("Hackathon has already started or passed");
    }
}
