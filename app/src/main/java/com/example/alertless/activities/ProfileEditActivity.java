package com.example.alertless.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alertless.R;
import com.example.alertless.enums.ButtonState;
import com.example.alertless.database.repositories.ProfileRepository;
import com.example.alertless.exceptions.AlertlessDatabaseException;
import com.example.alertless.exceptions.AlertlessException;
import com.example.alertless.models.AppDetailsModel;
import com.example.alertless.models.Profile;
import com.example.alertless.models.ProfileDetailsModel;
import com.example.alertless.models.ScheduleModel;
import com.example.alertless.utils.ActivityUtils;
import com.example.alertless.utils.Constants;
import com.example.alertless.utils.StringUtils;
import com.example.alertless.utils.ToastUtils;
import com.example.alertless.view.adapters.SilentAppListAdapter;


import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ProfileEditActivity extends AppCompatActivity {
    private static final String TAG = ProfileEditActivity.class.getName() + Constants.TAG_SUFFIX;
    public static final boolean DEFAULT_PROFILE_SWITCH_STATE = true;
    public static final int LAUNCH_SCHEDULER_ACTIVITY = 1;
    public static final int LAUNCH_APP_SELECTOR_ACTIVITY = 2;

    public static final String SCHEDULER_SUCCESS_TOAST = "Scheduler Result : %s";
    public static final String SCHEDULER_CANCELLED_TOAST = "Scheduler Activity Cancelled due to error : %s";

    public static final String APP_SELECTOR_SUCCESS_TOAST = "AppSelector Result: %s";
    public static final String APP_SELECTOR_CANCELLED_TOAST = "App Selector Activity Cancelled due to error : %s";
    public static final String NULL_DATA_ERROR = "null Data";


    private Profile currentProfile;
    private ProfileRepository profileRepository = ProfileRepository.getInstance(getApplication());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        try {
            initStates();
        } catch (AlertlessDatabaseException e) {
            ActivityUtils.finishActivityWithErr(Constants.APP_SELECTOR_ERROR, e.getMessage(), this);
        }
    }

    private void initStates() throws AlertlessDatabaseException {
        currentProfile = (Profile) getIntent().getSerializableExtra(Constants.CURRENT_PROFILE);

        if (currentProfile != null && currentProfile.getDetails() != null) {
            String profileName = currentProfile.getDetails().getName();

            EditText profileEditText = findViewById(R.id.profileEditText);
            profileEditText.setText(profileName);

            updateProfileTextView();

            SilentAppListAdapter silentAppListAdapter = getSilentAppListAdapter(profileName);

            profileRepository.getLiveProfileApps(profileName).observe(this, profileAppRelations -> {
                try {
                    List<AppDetailsModel> appModels = profileRepository.getAppModelsFromRelations(profileAppRelations);
                    silentAppListAdapter.setSilentApps(appModels);
                } catch (AlertlessDatabaseException e) {
                    Log.i(TAG, e.getMessage());
                    ActivityUtils.finishActivityWithErr(Constants.APP_SELECTOR_ERROR, e.getMessage(), this);
                }

            });

        } else {

            if (currentProfile == null) {
                currentProfile = new Profile();
            }

            setTitle("Unknown Profile");
            setButtonsState(ButtonState.DISABLED);
        }
    }

    private SilentAppListAdapter getSilentAppListAdapter(String profileName) {

        RecyclerView recyclerView = findViewById(R.id.silentOnlyAppRecyclerview);
        final SilentAppListAdapter adapter = new SilentAppListAdapter(this, getApplication(), profileName);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        return adapter;
    }

    private void updateProfileTextView() {
        setTitle(currentProfile.getDetails().getName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.done_app_btn) {
            doneWithProfileEdit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SCHEDULER_ACTIVITY) {
            Supplier<ScheduleModel> scheduleSupplier = () -> (ScheduleModel) data.getSerializableExtra(Constants.SCHEDULE_RESULT);
            handleActivityResult(resultCode, data, scheduleSupplier, SCHEDULER_SUCCESS_TOAST, Constants.SCHEDULE_ERROR, SCHEDULER_CANCELLED_TOAST);

        } else if (requestCode == LAUNCH_APP_SELECTOR_ACTIVITY) {

            Supplier<List<AppDetailsModel>> appsSupplier = () -> (List) data.getSerializableExtra(Constants.APP_SELECTOR_RESULT);
            handleActivityResult(resultCode, data, appsSupplier, APP_SELECTOR_SUCCESS_TOAST, Constants.APP_SELECTOR_ERROR, APP_SELECTOR_CANCELLED_TOAST);
        }
    }

    private <T> void handleActivityResult(int resultCode, Intent data, Supplier<T> dataSupplier, String successToastKey,
                                                                                   String errorDataKey, String errorToastKey) {
        if (resultCode == Activity.RESULT_OK) {

            T result = dataSupplier.get();
            String resultMsg = String.format(successToastKey, result);
            Log.i(TAG, resultMsg);

        } else if (resultCode == Activity.RESULT_CANCELED) {

            String errResult = data != null ? data.getStringExtra(errorDataKey) : NULL_DATA_ERROR;
            String errMsg = String.format(errorToastKey, errResult);
            Log.i(TAG, errMsg);
        }
    }

    public void schedule(View view) {
        Intent intent = new Intent(this, SchedulerActivity.class);
        intent.putExtra(Constants.CURRENT_PROFILE, currentProfile);
        startActivityForResult(intent, LAUNCH_SCHEDULER_ACTIVITY);
    }

    public void silentMoreApps(View view) {
        Intent intent = new Intent(this, AppSelectorActivity.class);
        intent.putExtra(Constants.CURRENT_PROFILE, currentProfile);
        startActivityForResult(intent, LAUNCH_APP_SELECTOR_ACTIVITY);
    }

    public void doneWithProfileEdit() {
        finish();
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

                profileRepository.createProfile(profileDetails);

                // Update state
                currentProfile.setDetails(profileDetails);

                // Enable buttons
                setButtonsState(ButtonState.ENABLED);

                ToastUtils.showToast(getApplicationContext(),"Saved Profile : " + profileDetails.toString());
            } else {
                profileRepository.updateProfileDetails(currentProfile.getDetails().getName(), profileName);

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

    public void getProfile(View view) {

        // TODO : this is duplicate code from saveProfile() above, try to remove this duplicacy
        final String profileName = checkAndGetProfileNameFromView();

        if (StringUtils.isBlank(profileName)) {
            ToastUtils.showToast(getApplicationContext(),"Profile Name Empty !!!");
            return;
        }

        ProfileDetailsModel profileDetails = null;
        try {
            profileDetails = profileRepository.getProfileDetailsByName(profileName);

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
