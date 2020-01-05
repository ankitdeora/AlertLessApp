package com.example.alertless.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.alertless.R;

public class ProfileEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
    }

    public void schedule(View view) {
        Intent intent = new Intent(this, SchedulerActivity.class);
        startActivity(intent);
    }
}
