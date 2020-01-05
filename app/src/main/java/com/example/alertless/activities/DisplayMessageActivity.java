package com.example.alertless.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.example.alertless.R;
import com.example.alertless.activities.MainActivity;
import com.example.alertless.scheduler.ScheduleTimePicker;
import com.example.alertless.utils.ToastUtils;

import java.util.Calendar;
import java.util.List;

public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.textView);
        textView.setText(message);

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);

        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar c = eventDay.getCalendar();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                String msg = String.format("|%s-%s-%s|", year, month, day);
                ToastUtils.showToast(getApplicationContext(), msg);
            }
        });
    }

    public void saveSchedule(View v) {

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);

        List<Calendar> calendars = calendarView.getSelectedDates();

        String msg = "";
        for (Calendar c : calendars) {
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            msg += String.format("|%s-%s-%s|", year, month, day);
        }

        ToastUtils.showToast(getApplicationContext(), msg);
    }
}
