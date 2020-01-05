package com.example.alertless.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    private static final int DURATION = Toast.LENGTH_SHORT;

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, DURATION).show();
    }

}
