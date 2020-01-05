package com.example.alertless.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.example.alertless.R;
import com.example.alertless.database.entities.User;
import com.example.alertless.database.repositories.UserRepository;
import com.example.alertless.scheduler.ScheduleDatePicker;
import com.example.alertless.scheduler.ScheduleTimePicker;
import com.example.alertless.utils.ToastUtils;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init userRepository
        userRepository = new UserRepository(getApplication());
    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        final String message = editText.getText().toString();

        // insert to DB
        User user = new User(message, "deora");
        userRepository.insertUser(user);
        ToastUtils.showToast(getApplicationContext(),"Saved user : " + user.toString());

        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void schedule(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "Schedule here !!!");
        startActivity(intent);
    }

    public void listAllUsers(View view) throws Exception {
        List<User> usersList = userRepository.getAllUsers();
        ToastUtils.showToast(getApplicationContext(), usersList.toString());
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
