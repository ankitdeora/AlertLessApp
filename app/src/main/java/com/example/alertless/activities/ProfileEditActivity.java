package com.example.alertless.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alertless.R;
import com.example.alertless.commons.ScheduleType;
import com.example.alertless.database.repositories.ProfileDetailsRepository;
import com.example.alertless.exceptions.AlertlessDatabaseException;
import com.example.alertless.models.Profile;
import com.example.alertless.models.ProfileDetailsModel;
import com.example.alertless.models.Schedule;
import com.example.alertless.models.WeekScheduleModel;
import com.example.alertless.utils.Constants;
import com.example.alertless.utils.StringUtils;
import com.example.alertless.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Optional;

public class ProfileEditActivity extends AppCompatActivity {
    private static final String TAG = ProfileEditActivity.class.getName() + Constants.TAG_SUFFIX;
    public static final boolean DEFAULT_PROFILE_SWITCH_STATE = true;
    public static final int LAUNCH_SCHEDULER_ACTIVITY = 1;

    private Profile currentProfile;
    private ProfileDetailsRepository profileDetailsRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        currentProfile = (Profile) getIntent().getSerializableExtra(Constants.CURRENT_PROFILE);

        if (currentProfile != null && currentProfile.getDetails() != null) {
            EditText profileEditText = (EditText) findViewById(R.id.profileEditText);
            profileEditText.setText(currentProfile.getDetails().getName());
        }

        // Init userRepository
        profileDetailsRepository = ProfileDetailsRepository.getInstance(getApplication());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SCHEDULER_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){

                Schedule schedule = (Schedule) data.getSerializableExtra(Constants.SCHEDULE_RESULT);

                String toastMsg = "Returned OK, from scheduler : %s";

                toastMsg = String.format(toastMsg, schedule.toString());
                ToastUtils.showToast(getApplicationContext(), toastMsg, Toast.LENGTH_LONG);

            } else if (resultCode == Activity.RESULT_CANCELED) {

                String errResult = data.getStringExtra(Constants.SCHEDULE_ERROR);
                String errMsg = String.format("Scheduler Activity Cancelled due to error : %s", errResult);
                ToastUtils.showToast(getApplicationContext(), errMsg);
            }
        }
    }

    public void schedule(View view) {
        Intent intent = new Intent(this, SchedulerActivity.class);
        intent.putExtra(Constants.CURRENT_PROFILE, currentProfile);
        startActivityForResult(intent, LAUNCH_SCHEDULER_ACTIVITY);
    }

    public void silentMoreApps(View view) {

        Intent intent = new Intent(this, AppSelectorActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the Send button */
    public void saveProfile(View view) {

        final String profileName = checkAndGetProfileNameFromView();

        if (StringUtils.isBlank(profileName)) {
            ToastUtils.showToast(getApplicationContext(),"Profile Name Empty !!!");
            return;
        }

        // insert to DB
        ProfileDetailsModel profileDetails = new ProfileDetailsModel(profileName, DEFAULT_PROFILE_SWITCH_STATE);

        try {
            profileDetailsRepository.insertProfileDetails(profileDetails);
            ToastUtils.showToast(getApplicationContext(),"Saved Profile : " + profileDetails.toString());
        } catch (AlertlessDatabaseException e) {
            Log.e(TAG, e.getMessage(), e);
            ToastUtils.showToast(getApplicationContext(), e.getMessage());
        }

        // TODO : Go back to previous activity by removing current activity from activity stack
    }

    public void deleteProfile(View view) {
        final String profileName = checkAndGetProfileNameFromView();

        if (StringUtils.isBlank(profileName)) {
            ToastUtils.showToast(getApplicationContext(),"Profile Name Empty !!!");
            return;
        }

        // TODO: show dialog for user to confirm if he/she really wants to delete?
        try {
            profileDetailsRepository.deleteProfileDetails(profileName);
            ToastUtils.showToast(getApplicationContext(),"Deleted Profile : " + profileName);
        } catch (AlertlessDatabaseException e) {
            Log.e(TAG, e.getMessage(), e);
            ToastUtils.showToast(getApplicationContext(), e.getMessage());
        }

    }

    public void getProfile(View view) {

        // TODO : this is duplicate code from saveProfile() above, try to remove this duplicacy
        final String profileName = checkAndGetProfileNameFromView();

        if (StringUtils.isBlank(profileName)) {
            ToastUtils.showToast(getApplicationContext(),"Profile Name Empty !!!");
            return;
        }

        ProfileDetailsModel profileDetails = null;
        try {
            profileDetails = profileDetailsRepository.getProfileDetailsByName(profileName);

            Optional.ofNullable(profileDetails).orElseThrow(() -> {
                String notFoundMsg = String.format("Profile : %s does not exist !!!", profileName);
                return new AlertlessDatabaseException(notFoundMsg);
            });

            ToastUtils.showToast(getApplicationContext(),"Got Profile : " + profileDetails.toString());
        } catch (AlertlessDatabaseException e) {
            Log.e(TAG, e.getMessage(), e);
            ToastUtils.showToast(getApplicationContext(), e.getMessage());
        }

    }

    public String checkAndGetProfileNameFromView() {
        EditText profileEditText = (EditText) findViewById(R.id.profileEditText);
        return profileEditText.getText().toString();
    }
}
