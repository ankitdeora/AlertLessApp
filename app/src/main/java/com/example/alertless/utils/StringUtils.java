package com.example.alertless.utils;

import org.apache.commons.lang3.RandomStringUtils;

import static com.example.alertless.utils.Constants.UNIQUE_ID_LENGTH;

public class StringUtils {

    public static boolean isBlank(CharSequence str) {

        if (str == null || str.toString().isEmpty() || str.toString().matches("[\\s\\t]+")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isNotBlank(CharSequence str) {
        return !isBlank(str);
    }

    public static String getUniqueId() {
        return RandomStringUtils.randomAlphanumeric(UNIQUE_ID_LENGTH);
    }
}
