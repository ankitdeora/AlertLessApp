package com.example.alertless.utils;

public class StringUtils {

    public static boolean isBlank(String str) {

        if (str == null || str.isEmpty() || str.matches("[\\s\\t]+")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
}
