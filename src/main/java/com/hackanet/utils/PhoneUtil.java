package com.hackanet.utils;

/**
 * @author Iskander Valiev
 * created by isko
 * on 2/26/19
 */
public class PhoneUtil {

    public static String formatPhone(String phone) {
        if (phone.startsWith("8"))
            return phone.replaceFirst("8", "+7");
        return phone;
    }
}
