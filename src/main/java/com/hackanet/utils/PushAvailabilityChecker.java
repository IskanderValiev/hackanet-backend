package com.hackanet.utils;

import com.hackanet.push.enums.PushType;

import java.util.Optional;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/30/19
 */
public class PushAvailabilityChecker {
    public static boolean isAvailableForiOS(PushType pushType) {
        return pushType == null;
    }

    public static boolean isAvailableForAndroid(PushType pushType, Integer appBuild) {
        return pushType == null;
    }
}
