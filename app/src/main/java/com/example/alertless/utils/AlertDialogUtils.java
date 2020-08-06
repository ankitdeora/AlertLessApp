package com.example.alertless.utils;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

public class AlertDialogUtils {

    public static AlertDialog getTextDialog(String title, Context context, EditText input, String defaultInput) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);

        // Set up the input
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        input.setOnFocusChangeListener((v, hasFocus) -> input.post(() -> {
            InputMethodManager inputMethodManager= (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
        }));

        if (StringUtils.isNotBlank(defaultInput)) {
            input.setText(defaultInput);
            input.setSelectAllOnFocus(true);
        }

        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            //Do nothing here because we override this button later to change the close behaviour.
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.setView(input, 60, 50, 120, 10);

        // show dialog
        dialog.show();

        // change layout size
        int width = (int)(context.getResources().getDisplayMetrics().widthPixels * 0.83);
        int height = (int)(context.getResources().getDisplayMetrics().heightPixels * 0.27);
        dialog.getWindow().setLayout(width, height);

//        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        // request focus in edit text
        input.requestFocus();

        return dialog;
    }

    public static AlertDialog getTextDialog(String title, Context context, EditText input) {
        return getTextDialog(title, context, input, null);
    }
}
