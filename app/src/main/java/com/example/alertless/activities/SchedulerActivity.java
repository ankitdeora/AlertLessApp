package com.example.alertless.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.example.alertless.R;
import com.example.alertless.entities.Schedule;
import com.example.alertless.scheduler.ScheduleDatePicker;
import com.example.alertless.scheduler.ScheduleTimePicker;
import com.example.alertless.utils.ToastUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ca.antonious.materialdaypicker.MaterialDayPicker;

public class SchedulerActivity extends AppCompatActivity {

    private List<Schedule> schedules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduler);

        MaterialDayPicker dayPicker = findViewById(R.id.day_picker);
        MaterialDayPicker.Weekday wednesday = MaterialDayPicker.Weekday.WEDNESDAY;
        MaterialDayPicker.Weekday friday = MaterialDayPicker.Weekday.FRIDAY;
        dayPicker.setSelectedDays(wednesday, friday);

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
        Calendar dayOne = getCalendar(2020, 2, 27);
        Calendar dayTwo = getCalendar(2020, 2, 29);

        DatePickerBuilder builder = new DatePickerBuilder(this, getSelectDateListener())
                .setPickerType(CalendarView.MANY_DAYS_PICKER)
                .setHeaderColor(R.color.colorAccent)
                .setSelectionColor(R.color.colorAccent)
                .setTodayColor(R.color.colorAccent)
                .setSelectedDays(Arrays.asList(dayOne, dayTwo));

        DatePicker datePicker = builder.build();
        datePicker.show();
    }

    public Calendar getCalendar(int year, int month, int day) {
        Calendar date = Calendar.getInstance();

        date.set(Calendar.YEAR, year);
        date.set(Calendar.MONTH, month);
        date.set(Calendar.DAY_OF_MONTH, day);

        return date;
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

                    Date date = c.getTime();

                    msg += String.format("|%s-%s-%s|", year, month, day);
                }

                ToastUtils.showToast(getApplicationContext(), msg);
            }
        };
    }
}
