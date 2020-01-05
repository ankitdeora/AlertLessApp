package com.example.alertless.scheduler;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import com.example.alertless.utils.ToastUtils;

import java.util.Calendar;

public class ScheduleDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);

        DatePicker datePicker = datePickerDialog.getDatePicker();
        /*datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String msg = "Date changed -> Year : %s, Month : %s, Day: %s";
                ToastUtils.showToast(getContext(), String.format(msg, year, monthOfYear, dayOfMonth));
            }
        });*/
        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String msg = "Date set -> Year : %s, Month : %s, Day: %s";
        ToastUtils.showToast(getContext(), String.format(msg, year, month, dayOfMonth));
    }
}
