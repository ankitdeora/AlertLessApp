package com.example.alertless.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.view.View;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.example.alertless.R;
import com.example.alertless.scheduler.ScheduleDatePicker;
import com.example.alertless.scheduler.ScheduleTimePicker;
import com.example.alertless.utils.ToastUtils;

import java.util.Calendar;
import java.util.List;

import ca.antonious.materialdaypicker.MaterialDayPicker;

public class SchedulerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduler);
    }

    public void showWeekDays(View v) {

        MaterialDayPicker dayPicker = findViewById(R.id.day_picker);
        List<MaterialDayPicker.Weekday> weekdays = dayPicker.getSelectedDays();

        String msg = "";
        for (MaterialDayPicker.Weekday day : weekdays) {
            msg += String.format("-%s-", day.name());
        }

        ToastUtils.showToast(getApplicationContext(), msg);
    }

    public void showTimePickerDialog(View v) {
        DialogFragment timeFragment = new ScheduleTimePicker();
        timeFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment dateFragment = new ScheduleDatePicker();
        dateFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showMultipleDatePicker(View v) {
        DatePickerBuilder builder = new DatePickerBuilder(this, getSelectDateListener())
                .setPickerType(CalendarView.MANY_DAYS_PICKER)
                .setHeaderColor(R.color.colorAccent)
                .setSelectionColor(R.color.colorAccent)
                .setTodayColor(R.color.colorAccent);

        DatePicker datePicker = builder.build();
        datePicker.show();
    }

    private OnSelectDateListener getSelectDateListener() {
        return new OnSelectDateListener() {
            @Override
            public void onSelect(List<Calendar> calendars) {
                String msg = "";
                for (Calendar c : calendars) {
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);

                    msg += String.format("|%s-%s-%s|", year, month, day);
                }

                ToastUtils.showToast(getApplicationContext(), msg);
            }
        };
    }
}
