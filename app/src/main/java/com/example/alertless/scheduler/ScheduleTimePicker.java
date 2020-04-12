package com.example.alertless.scheduler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import com.example.alertless.models.TimeRangeModel;
import com.example.alertless.utils.Constants;
import com.example.alertless.utils.TimeUtils;
import com.example.alertless.utils.ToastUtils;

import java.util.Calendar;

public class ScheduleTimePicker extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private TimeRangeModel timeRangeModel;

    public ScheduleTimePicker(TimeRangeModel timeRangeModel) {
        super();
        this.timeRangeModel = timeRangeModel;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        int startTimeMin = this.timeRangeModel.getStartMin();
        int endTimeMin = this.timeRangeModel.getEndMin();

        if (Constants.START_TIME_TAG.equals(this.getTag()) &&
                (startTimeMin != Constants.DEFAULT_START_TIME_DAILY_MIN)) {

             hour = startTimeMin / 60;
             minute = startTimeMin % 60;

        } else if (Constants.END_TIME_TAG.equals(this.getTag()) &&
                     (endTimeMin != Constants.DEFAULT_END_TIME_DAILY_MIN)) {

            hour = endTimeMin / 60;
            minute = endTimeMin % 60;

        }

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        int timeInMin = TimeUtils.getMinutes(hourOfDay, minute);
        String msg = "";

        if (!isTimeSelectedValid(timeInMin)) {
            return;
        }

        if (Constants.START_TIME_TAG.equals(this.getTag())) {
            this.timeRangeModel.setStartMin(timeInMin);
            msg = String.format("Start Minutes of Day : %s", timeInMin);

        } else if (Constants.END_TIME_TAG.equals(this.getTag())) {
            this.timeRangeModel.setEndMin(timeInMin);
            msg = String.format("End Minutes of Day : %s", timeInMin);

        } else {
            msg = "Found Invalid Time tag in " + ScheduleTimePicker.class.getName();
        }

        ToastUtils.showToast(getContext(), msg);
    }

    private boolean isTimeSelectedValid(int timeInMin) {

        if ((Constants.START_TIME_TAG.equals(this.getTag()) && timeInMin >= this.timeRangeModel.getEndMin()) ||
                (Constants.END_TIME_TAG.equals(this.getTag()) && timeInMin <= this.timeRangeModel.getStartMin())) {

            new AlertDialog.Builder(getContext())
                    .setTitle("Invalid Time Selection")
                    .setMessage("Please ensure Start time comes before End time !!!")
                    .setPositiveButton(android.R.string.yes, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return false;
        } else {
            return true;
        }
    }

}