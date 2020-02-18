package com.hackanet.utils;

import com.hackanet.exceptions.BadRequestException;
import com.hackanet.json.dto.TokenDto;
import com.hackanet.security.enums.Role;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/30/19
 */
public class StringUtils {

    public static String formatTitle(String s) {
        s = org.apache.commons.lang.StringUtils.trimToEmpty(s);
        return org.apache.commons.lang.StringUtils.capitalize(s);
    }

    public static String generateRandomString() {
        return UUID.randomUUID().toString();
    }

    public static boolean isPhrase(String input) {
        String[] arr = input.trim().split(" ");
        return arr.length > 1;
    }

    public static void badWordFilter(String input, String fieldName) {
        if (SwearWordsFilter.containsBadWords(input))
            throw new BadRequestException(fieldName + " contains bad words");
    }

    public static String getJsonOfTokenDtoFromPrincipalName(String principalName) {
        Class<TokenDto> aClass = TokenDto.class;

        principalName = principalName.substring(9, principalName.length() - 1);
        principalName = "{" + principalName;
        Field[] fields = aClass.getDeclaredFields();
        int i = 0;
        for (Field field : fields) {
            if (principalName.contains(field.getName())) {
                principalName = principalName.replaceFirst(field.getName(), "\"" + field.getName() + "\"");
                String fieldTypeName = field.getType().getName();
                if (fieldTypeName.equals(String.class.getName()) || fieldTypeName.equals(Role.class.getName()) || fieldTypeName.equals(LocalDateTime.class.getName())) {
                    String[] split = principalName.split(",");
                    String[] split1 = split[i].replaceFirst("=", ":").split("\":");
                    if (fieldTypeName.equals(LocalDateTime.class.getName())) {
                        String first = split1[1];
                        split1[1] = split1[1].substring(0, split1[1].length() - 4);
                        principalName = principalName.replace(first, "\"" + split1[1] + "\"");
                    } else {
                        principalName = principalName.replace(split1[1], "\"" + split1[1] + "\"");
                    }
                    principalName = principalName.replaceAll("=", ":");
                }
                i++;
            }
        }
        principalName = principalName + "}";
        return principalName;
    }
}
