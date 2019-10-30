package com.hackanet.utils;

import java.util.UUID;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/30/19
 */
public class StringUtils {

    public static String generateRandomString() {
        return UUID.randomUUID().toString();
    }

    public static boolean isPhrase(String input) {
        String[] arr = input.trim().split(" ");
        return arr.length > 1;
    }
}
