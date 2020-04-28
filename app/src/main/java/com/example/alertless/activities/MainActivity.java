package com.example.alertless.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alertless.R;
import com.example.alertless.entities.ProfileDetailsEntity;
import com.example.alertless.models.ProfileDetailsModel;
import com.example.alertless.utils.Constants;
import com.example.alertless.view.adapters.ProfileListAdapter;
import com.example.alertless.view.models.ProfileViewModel;

import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName() + Constants.TAG_SUFFIX;
    private ProfileViewModel profileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProfileListAdapter profileListAdapter = getProfileListAdapter();

        // Init Profile view model
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        profileViewModel.getAllProfileDetailsEntities().observe(this, new Observer<List<ProfileDetailsEntity>>() {
            @Override
            public void onChanged(List<ProfileDetailsEntity> profileDetailsEntities) {
                List<ProfileDetailsModel> profileDetailsModels = profileDetailsEntities.stream()
                        .map(ProfileDetailsEntity::getModel)
                        .collect(Collectors.toList());

                profileListAdapter.setProfileDetails(profileDetailsModels);
            }
        });
    }


    private ProfileListAdapter getProfileListAdapter() {
        RecyclerView recyclerView = findViewById(R.id.profileRecyclerview);
        final ProfileListAdapter adapter = new ProfileListAdapter(this, getApplication());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        return adapter;
    }

    public void editProfile(View view) throws Exception {
        Intent intent = new Intent(this, ProfileEditActivity.class);
        startActivity(intent);

    }

}
