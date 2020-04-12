package com.example.alertless.scheduler;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import com.example.alertless.models.DateRangeModel;
import com.example.alertless.models.WeekScheduleModel;
import com.example.alertless.utils.Constants;
import com.example.alertless.utils.ToastUtils;

import java.util.Calendar;

public class WeekScheduleDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    private WeekScheduleModel weekSchedule;

    public WeekScheduleDatePicker(WeekScheduleModel weekSchedule) {
        this.weekSchedule = weekSchedule;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (this.weekSchedule.getDateRangeModel() == null) {
            DateRangeModel dateRangeModel = new DateRangeModel();
            this.weekSchedule.setDateRangeModel(dateRangeModel);
        }

        final Calendar c = Calendar.getInstance();
        DateRangeModel dateRangeModel = this.weekSchedule.getDateRangeModel();

        if (Constants.START_DATE_TAG.equals(this.getTag()) && dateRangeModel != null && dateRangeModel.getStartDateMs() != 0) {
            c.setTimeInMillis(dateRangeModel.getStartDateMs());

        } else if (Constants.END_DATE_TAG.equals(this.getTag()) && dateRangeModel != null && dateRangeModel.getEndDateMs() != 0) {
            c.setTimeInMillis(dateRangeModel.getEndDateMs());

        }

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);

        long minDateInMs = System.currentTimeMillis() - 1000;

        // Disable dates prior to Start date if set
        if (Constants.END_DATE_TAG.equals(this.getTag()) && dateRangeModel != null && dateRangeModel.getStartDateMs() != 0) {
            minDateInMs = dateRangeModel.getStartDateMs();
        }

        datePickerDialog.getDatePicker().setMinDate(minDateInMs);
        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        final Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR, year);
        date.set(Calendar.MONTH, month);
        date.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        final long dateInMillis = date.getTimeInMillis();

        if (!isSelectedDatesValid(dateInMillis)) {
            return;
        }

        String msg = String.format("Date set -> Year : %s, Month : %s, Day: %s, MS : %s", year, month, dayOfMonth, dateInMillis);

        if (Constants.START_DATE_TAG.equals(this.getTag())) {
            this.weekSchedule.getDateRangeModel().setStartDateMs(dateInMillis);
            Log.i("Week Schedule START Date Picker", msg);

        } else if (Constants.END_DATE_TAG.equals(this.getTag())) {
            this.weekSchedule.getDateRangeModel().setEndDateMs(dateInMillis);
            Log.i("Week Schedule END Date Picker", msg);

        } else {
            msg = "Found Invalid Time tag in " + WeekScheduleDatePicker.class.getName();
        }

        ToastUtils.showToast(getContext(), String.format(msg, year, month, dayOfMonth));
    }

    private boolean isSelectedDatesValid(long dateInMs) {

        long existingStartDateMs = this.weekSchedule.getDateRangeModel().getStartDateMs();
        long existingEndDateMs = this.weekSchedule.getDateRangeModel().getEndDateMs();

        if ((Constants.START_DATE_TAG.equals(this.getTag()) && existingEndDateMs != 0 && dateInMs >= existingEndDateMs) ||
                (Constants.END_DATE_TAG.equals(this.getTag()) && existingStartDateMs != 0 && dateInMs <= existingStartDateMs)){

            new AlertDialog.Builder(getContext())
                    .setTitle("Invalid Date Selection")
                    .setMessage("Please ensure Start Date comes before End Date !!!")
                    .setPositiveButton(android.R.string.yes, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return false;
        } else {
            return true;
        }

    }
}
