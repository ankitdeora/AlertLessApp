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
    public static final String EXTRA_MESSAGE = "com.example.alertless.activities.MainActivity.MESSAGE";
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

    public void listAllUsers(View view) throws Exception {
        List<User> usersList = userRepository.getAllUsers();
        ToastUtils.showToast(getApplicationContext(), usersList.toString());
    }

    public void editProfile(View view) throws Exception {
        Intent intent = new Intent(this, ProfileEditActivity.class);
        startActivity(intent);
    }

}
