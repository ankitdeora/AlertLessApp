package com.example.alertless.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alertless.R;
import com.example.alertless.database.repositories.ProfileRepository;
import com.example.alertless.entities.ProfileDetailsEntity;
import com.example.alertless.exceptions.AlertlessException;
import com.example.alertless.models.Profile;
import com.example.alertless.models.ProfileDetailsModel;
import com.example.alertless.utils.AlertDialogUtils;
import com.example.alertless.utils.Constants;
import com.example.alertless.utils.StringUtils;
import com.example.alertless.utils.ToastUtils;
import com.example.alertless.view.adapters.ProfileListAdapter;
import com.example.alertless.view.models.ProfileViewModel;

import java.util.Collections;
import java.util.Comparator;

import static com.example.alertless.utils.Constants.DEFAULT_PROFILE_SWITCH_STATE;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName() + Constants.TAG_SUFFIX;
    private ProfileViewModel profileViewModel;
    private RecyclerView recyclerView;
    private ProfileListAdapter profileListAdapter;
    private ProfileRepository profileRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profileRepository = ProfileRepository.getInstance(getApplication());
        setProfileListAdapter();

        // Init Profile view model
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        profileViewModel.getAllProfileDetailsEntities().observe(this, profileDetailsEntities -> {

            Collections.sort(profileDetailsEntities, Comparator.comparing(ProfileDetailsEntity::getName));
            profileListAdapter.setProfileDetails(profileDetailsEntities);
        });
    }


    private void setProfileListAdapter() {
        recyclerView = findViewById(R.id.profileRecyclerview);
        profileListAdapter = new ProfileListAdapter(this, getApplication());
        recyclerView.setAdapter(profileListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        registerForContextMenu(recyclerView);
    }

    public void createProfile(View view) {
        final EditText input = new EditText(this);
        AlertDialog dialog = AlertDialogUtils.getTextDialog("Add Profile Name !!!",this, input);

        // show dialog
        dialog.show();

        //Overriding the handler immediately after show for text validations and existing profile validations
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {

            String profileNameInDialog = input.getText().toString();

            if (StringUtils.isBlank(profileNameInDialog)) {
                ToastUtils.showToast(getApplicationContext(),"Profile Name Empty !!!", Toast.LENGTH_LONG);
            } else {
                ProfileDetailsModel detailsModel = new ProfileDetailsModel(profileNameInDialog, DEFAULT_PROFILE_SWITCH_STATE);

                try {

                    if (profileRepository.getProfileDetailsByName(profileNameInDialog) != null) {
                        throw new AlertlessException(String.format("Profile : %s already exists", profileNameInDialog));
                    }

                    profileRepository.createProfile(detailsModel);

                    Profile profile = Profile.builder()
                            .details(detailsModel)
                            .build();

                    Intent intent = new Intent(this, ProfileEditActivity.class);
                    intent.putExtra(Constants.CURRENT_PROFILE, profile);

                    // dismiss dialog
                    dialog.dismiss();

                    // start profile edit activity
                    startActivity(intent);

                } catch (AlertlessException e) {
                    Log.i(TAG, e.getMessage());
                    ToastUtils.showToast(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                }
            }
        });
    }

}
