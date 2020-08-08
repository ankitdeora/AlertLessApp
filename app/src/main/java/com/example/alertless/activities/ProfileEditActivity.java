package com.example.alertless.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alertless.R;
import com.example.alertless.database.repositories.ProfileRepository;
import com.example.alertless.exceptions.AlertlessDatabaseException;
import com.example.alertless.exceptions.AlertlessException;
import com.example.alertless.models.AppDetailsModel;
import com.example.alertless.models.Profile;
import com.example.alertless.models.ScheduleModel;
import com.example.alertless.utils.ActivityUtils;
import com.example.alertless.utils.AlertDialogUtils;
import com.example.alertless.utils.Constants;
import com.example.alertless.utils.StringUtils;
import com.example.alertless.utils.ToastUtils;
import com.example.alertless.view.adapters.SilentAppListAdapter;

import java.util.List;
import java.util.function.Supplier;

public class ProfileEditActivity extends AppCompatActivity {
    private static final String TAG = ProfileEditActivity.class.getName() + Constants.TAG_SUFFIX;

    public static final int LAUNCH_SCHEDULER_ACTIVITY = 1;
    public static final int LAUNCH_APP_SELECTOR_ACTIVITY = 2;

    public static final String SCHEDULER_SUCCESS_TOAST = "Scheduler Result : %s";
    public static final String SCHEDULER_CANCELLED_TOAST = "Scheduler Activity Cancelled due to error : %s";

    public static final String APP_SELECTOR_SUCCESS_TOAST = "AppSelector Result: %s";
    public static final String APP_SELECTOR_CANCELLED_TOAST = "App Selector Activity Cancelled due to error : %s";
    public static final String NULL_DATA_ERROR = "null Data";


    // current state
    private Profile currentProfile;
    private ProfileRepository profileRepository = ProfileRepository.getInstance(getApplication());
    private RecyclerView recyclerView;
    private ImageView appsArrowIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_edit_layout);

        try {
            initStates(savedInstanceState);
        } catch (AlertlessDatabaseException e) {
            ActivityUtils.finishActivityWithErr(Constants.APP_SELECTOR_ERROR, e.getMessage(), this);
        }

        this.recyclerView.setVisibility(View.GONE);
        this.appsArrowIcon = findViewById(R.id.apps_arrow_icon);
        this.appsArrowIcon.setImageResource(R.drawable.ic_arrow_right_black_24dp);
    }

    private void initStates(Bundle savedInstanceState) throws AlertlessDatabaseException {
        currentProfile = (Profile) getIntent().getSerializableExtra(Constants.CURRENT_PROFILE);

        if ((currentProfile == null || currentProfile.getDetails() == null) && savedInstanceState != null) {
            currentProfile = (Profile) savedInstanceState.getSerializable(Constants.CURRENT_PROFILE);
        }

        if (currentProfile != null && currentProfile.getDetails() != null) {
            String profileName = currentProfile.getDetails().getName();

            TextView profileTextView = findViewById(R.id.profileTextView);
            profileTextView.setText(profileName);

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

            setTitle("Create Profile");
        }
    }

    private SilentAppListAdapter getSilentAppListAdapter(String profileName) {

        recyclerView = findViewById(R.id.silentOnlyAppRecyclerview);
        final SilentAppListAdapter adapter = new SilentAppListAdapter(this, getApplication(), profileName);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        return adapter;
    }

    private void updateProfileTextView() {
        setTitle("Update Profile");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // save state
        outState.putSerializable(Constants.CURRENT_PROFILE, currentProfile);
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

    public void editProfileName(View view) {
        final EditText input = new EditText(this);
        AlertDialog dialog = AlertDialogUtils.getTextDialog("Update Profile Name !!!", this, input, currentProfile.getDetails().getName());

        //Overriding the handler immediately after show for text validations and existing profile validations
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {

            String profileNameInDialog = input.getText().toString();

            if (StringUtils.isBlank(profileNameInDialog)) {
                ToastUtils.showToast(getApplicationContext(),"Profile Name Empty !!!", Toast.LENGTH_LONG);
            } else {
                try {
                    if (profileRepository.getProfileDetailsByName(profileNameInDialog) != null) {
                        throw new AlertlessException(String.format("Profile : %s already exists", profileNameInDialog));
                    }

                    profileRepository.updateProfileDetails(currentProfile.getDetails().getName(), profileNameInDialog);

                    // Update states
                    currentProfile.getDetails().setName(profileNameInDialog);
                    TextView profileTextView = findViewById(R.id.profileTextView);
                    profileTextView.setText(profileNameInDialog);

                    // dismiss dialog
                    dialog.dismiss();

                    ToastUtils.showToast(getApplicationContext(),"Updated Profile Name to : " + profileNameInDialog);

                } catch (AlertlessException e) {
                    Log.i(TAG, e.getMessage());
                    ToastUtils.showToast(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                }
            }
        });
    }

    public void toggleApps(View view) {

        if (this.recyclerView.isShown()) {
            this.appsArrowIcon.setImageResource(R.drawable.ic_arrow_right_black_24dp);
            this.recyclerView.setVisibility(View.GONE);
        } else {
            this.appsArrowIcon.setImageResource(R.drawable.ic_arrow_down_black_24dp);
            this.recyclerView.setVisibility(View.VISIBLE);
        }

    }
}
