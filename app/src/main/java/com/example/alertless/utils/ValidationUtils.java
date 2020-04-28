package com.example.alertless.utils;

import com.example.alertless.exceptions.AlertlessIllegalArgumentException;

public class ValidationUtils {
    public static <T> void validateInput(T input) throws AlertlessIllegalArgumentException {

        if (input == null) {
            throw new AlertlessIllegalArgumentException("Input cannot be null !!!");
        }

        if (input instanceof CharSequence && StringUtils.isBlank((CharSequence) input)) {
            throw new AlertlessIllegalArgumentException("Input cannot be blank !!!");
        }
    }
}
