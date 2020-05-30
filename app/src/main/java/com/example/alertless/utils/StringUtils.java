package com.example.alertless.utils;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.alertless.utils.Constants.UNIQUE_ID_LENGTH;

public class StringUtils {

    private static Function<String, String> addQuotes = s -> "\"" + s + "\"";

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

    public static String addQuotesToList(List<String> strs) {
        return strs.stream()
                .map(addQuotes)
                .collect(Collectors.joining(","));
    }
}
