package com.example.alertless.utils;

import android.content.Context;
import android.text.InputType;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

public class AlertDialogUtils {

    public static AlertDialog getTextDialog(String title, Context context, EditText input) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);

        // Set up the input
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            //Do nothing here because we override this button later to change the close behaviour.
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.setView(input, 60, 20, 120, 20);

        return dialog;
    }
}
