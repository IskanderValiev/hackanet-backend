package com.hackanet.security.utils;

import com.hackanet.exceptions.ForbiddenException;
import com.hackanet.models.FileInfo;
import com.hackanet.models.Hackathon;
import com.hackanet.models.User;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/20/19
 */
public class SecurityUtils {

    public static void checkFileAccess(FileInfo fileInfo, User user) {
        if (!user.equals(fileInfo.getUser()))
            throw new ForbiddenException("You have no access this file");
    }

    public static void checkHackathonAccess(Hackathon hackathon, User user) {
        if (!user.getId().equals(hackathon.getOwner().getId()))
            throw new ForbiddenException("You have no access this hackathon");
    }

}
