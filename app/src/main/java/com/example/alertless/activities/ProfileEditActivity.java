package com.example.alertless.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alertless.R;
import com.example.alertless.commons.ButtonState;
import com.example.alertless.database.repositories.ProfileDetailsRepository;
import com.example.alertless.exceptions.AlertlessDatabaseException;
import com.example.alertless.exceptions.AlertlessException;
import com.example.alertless.models.Profile;
import com.example.alertless.models.ProfileDetailsModel;
import com.example.alertless.models.ScheduleModel;
import com.example.alertless.utils.Constants;
import com.example.alertless.utils.StringUtils;
import com.example.alertless.utils.ToastUtils;


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
            String profileName = currentProfile.getDetails().getName();

            EditText profileEditText = (EditText) findViewById(R.id.profileEditText);
            profileEditText.setText(profileName);

            updateProfileTextView();
        } else {

            if (currentProfile == null) {
                currentProfile = new Profile();
            }

            setButtonsState(ButtonState.DISABLED);
        }

        // Init userRepository
        profileDetailsRepository = ProfileDetailsRepository.getInstance(getApplication());
    }

    private void updateProfileTextView() {
        TextView profileTextView = findViewById(R.id.profileTextView);
        profileTextView.setText("Profile : " + currentProfile.getDetails().getName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SCHEDULER_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){

                ScheduleModel schedule = (ScheduleModel) data.getSerializableExtra(Constants.SCHEDULE_RESULT);

                String toastMsg = "Returned OK, from scheduler : %s";

                toastMsg = String.format(toastMsg, schedule.toString());
                ToastUtils.showToast(getApplicationContext(), toastMsg, Toast.LENGTH_LONG);

            } else if (resultCode == Activity.RESULT_CANCELED) {
                String errResult = "null Data";

                if (data != null) {
                    errResult = data.getStringExtra(Constants.SCHEDULE_ERROR);
                }

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

    public void saveProfileName(View view) {

        final String profileName = checkAndGetProfileNameFromView();

        if (StringUtils.isBlank(profileName)) {
            ToastUtils.showToast(getApplicationContext(),"Profile Name Empty !!!");
            return;
        }

        // insert to DB

        try {
            if (currentProfile.getDetails() == null) {
                ProfileDetailsModel profileDetails = new ProfileDetailsModel(profileName, DEFAULT_PROFILE_SWITCH_STATE);

                profileDetailsRepository.createEntity(profileDetails);

                // Update state
                currentProfile.setDetails(profileDetails);

                // Enable buttons
                setButtonsState(ButtonState.ENABLED);

                ToastUtils.showToast(getApplicationContext(),"Saved Profile : " + profileDetails.toString());
            } else {
                profileDetailsRepository.updateProfileDetails(currentProfile.getDetails().getName(), profileName);

                // Update state
                currentProfile.getDetails().setName(profileName);
                ToastUtils.showToast(getApplicationContext(),"Updated Profile Name to : " + profileName);
            }

            // Update UI
            updateProfileTextView();

        } catch (AlertlessException e) {
            Log.e(TAG, e.getMessage(), e);
            ToastUtils.showToast(getApplicationContext(), e.getMessage());
        }

    }

    private void setButtonsState(final ButtonState state) {
        boolean setEnabled = ButtonState.ENABLED.equals(state);

        Button scheduleBtn = findViewById(R.id.scheduleBtn);
        scheduleBtn.setEnabled(setEnabled);

        Button silentMoreAppsBtn = findViewById(R.id.silentMoreAppsBtn);
        silentMoreAppsBtn.setEnabled(setEnabled);
    }

    public void deleteProfile(View view) {
        final String profileName = checkAndGetProfileNameFromView();

        if (StringUtils.isBlank(profileName)) {
            ToastUtils.showToast(getApplicationContext(),"Profile Name Empty !!!");
            return;
        }

        // TODO: show dialog for user to confirm if he/she really wants to delete?
        try {
            profileDetailsRepository.deleteProfile(profileName);
            ToastUtils.showToast(getApplicationContext(),"Deleted Profile : " + profileName);

            finish();
        } catch (AlertlessException e) {
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
        } catch (AlertlessException e) {
            Log.e(TAG, e.getMessage(), e);
            ToastUtils.showToast(getApplicationContext(), e.getMessage());
        }

    }

    public String checkAndGetProfileNameFromView() {
        EditText profileEditText = (EditText) findViewById(R.id.profileEditText);
        return profileEditText.getText().toString();
    }
}
