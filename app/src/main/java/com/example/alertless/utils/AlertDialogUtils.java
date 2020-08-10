package com.example.alertless.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.provider.Settings;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.core.app.NotificationManagerCompat;

import com.example.alertless.R;
import com.example.alertless.exceptions.AlertlessRuntimeException;

public class AlertDialogUtils {

    public static void requestNotificationPermissionsDialog(Context context) {

        if (!NotificationManagerCompat.getEnabledListenerPackages(context).contains(context.getPackageName())) {
            showNotificationDialog(context);
        }
    }

    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    private static void showNotificationDialog(Context context) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Grant Notification Permission !!!");
        alertBuilder.setMessage(String.format("Notification access permission is necessary for %s Application to work.", getApplicationName(context)));
        alertBuilder.setPositiveButton("ALLOW", (dialog, which) -> {
            context.startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            dialog.dismiss();
        });
        alertBuilder.setNegativeButton("DENY", (dialog, which) -> {
            ((Activity)context).finish();
            dialog.cancel();
        });

        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    public static AlertDialog getProfileNameDialog(String title, Context context) {
        return getProfileNameDialog(title, context, null);
    }

    public static AlertDialog getProfileNameDialog(String title, Context context, String defaultInput) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();

        builder.setTitle(title)
                .setView(layoutInflater.inflate(R.layout.dialog_profile_name, null))
                .setPositiveButton("OK", (dialog, which) -> {
                    //Do nothing here because we override this button later to change the close behaviour.
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();

        final EditText input = dialog.findViewById(R.id.profile_name_edit_text);
        if (input == null) {
            throw new AlertlessRuntimeException("Could not find EditText input for ProfileName edit dialog !!!");
        }

        if (StringUtils.isNotBlank(defaultInput)) {
            input.setText(defaultInput);
            input.setSelectAllOnFocus(true);
        }

        setUpInput(input, context);

        return dialog;
    }

    private static void setUpInput(EditText input, Context context) {
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        input.setOnFocusChangeListener((v, hasFocus) -> input.post(() -> {
            if (hasFocus) {
                showKeyboard(context);
            } else {
                closeKeyboard(context);
            }
        }));

        input.requestFocus();
    }

    public static void showKeyboard(Context context){
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void closeKeyboard(Context context){
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
}
