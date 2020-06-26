package com.example.alertless.utils;

import android.app.Activity;
import android.content.Intent;

public class ActivityUtils {

    public static void finishActivityWithErr(String errKey, String errMsg, Activity activity) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(errKey, errMsg);
        activity.setResult(Activity.RESULT_CANCELED, returnIntent);

        ToastUtils.showToast(activity.getApplicationContext(), errMsg);
        activity.finish();
    }
}
