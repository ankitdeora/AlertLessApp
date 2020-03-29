package com.example.alertless.services;

import android.content.Context;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.example.alertless.utils.ToastUtils;

public class AlertNotifyService extends NotificationListenerService {
    Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    /*
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        String pack = sbn.getPackageName();
        int id = sbn.getId();
        String key = sbn.getKey();

        CharSequence text = "default";
        String title = "default";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            Bundle extras = extras = sbn.getNotification().extras;
            text = extras.getCharSequence("android.text");
            title = extras.getString("android.title");
        }

        String msg = String.format("Pack: %s Text : %s Title : %s id : %s key: %s", pack, text, title, id, key);
        ToastUtils.showToast(getApplicationContext(), msg);
        Log.i("Notify Service", "##### New POST ########" + msg);

        String hangoutPack = "com.google.android.talk";
        if (hangoutPack.equalsIgnoreCase(pack)) {
//            this.cancelNotification(key);
//            ToastUtils.showToast(getApplicationContext(), "Caneclling Hangout Notification");
        } else {
//            ToastUtils.showToast(getApplicationContext(), "not cancelling anything");
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

        String pack = sbn.getPackageName();
        int id = sbn.getId();
        String key = sbn.getKey();

        CharSequence text = "default";
        String title = "default";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            Bundle extras = extras = sbn.getNotification().extras;
            text = extras.getCharSequence("android.text");
            title = extras.getString("android.title");
        }

        String msg = String.format("Removed Pack: %s Text : %s Title : %s id : %s key : %s", pack, text, title, id, key);
        ToastUtils.showToast(getApplicationContext(), msg);

        Log.i("Notify Service", "##### New REMOVE #########" + msg);
    }
    */
}
