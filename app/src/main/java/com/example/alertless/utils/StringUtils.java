package com.example.alertless.utils;

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
}
