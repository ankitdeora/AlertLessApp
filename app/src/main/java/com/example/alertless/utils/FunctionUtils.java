package com.example.alertless.utils;

import java.util.function.Function;
import java.util.function.Supplier;

public class FunctionUtils {

    public static <T, R> Supplier<R> bind(Function<T,R> fn, T val) {
        return () -> fn.apply(val);
    }
}
