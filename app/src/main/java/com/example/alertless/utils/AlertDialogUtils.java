package com.example.alertless.utils;

import android.app.Activity;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.example.alertless.R;
import com.example.alertless.exceptions.AlertlessRuntimeException;

public class AlertDialogUtils {

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
