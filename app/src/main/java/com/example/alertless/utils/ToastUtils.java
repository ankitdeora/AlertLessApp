package com.example.alertless.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    private static final int SHORT_DURATION = Toast.LENGTH_SHORT;

    public static void showToast(Context context, String msg) {
        showToast(context, msg, SHORT_DURATION);
    }

    public static void showToast(Context context, String msg, int duration) {
        Toast.makeText(context, msg, duration).show();
    }

}
