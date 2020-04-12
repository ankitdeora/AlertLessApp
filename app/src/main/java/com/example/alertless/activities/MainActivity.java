package com.example.alertless.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alertless.R;
import com.example.alertless.database.repositories.ProfileDetailsRepository;
import com.example.alertless.models.Profile;
import com.example.alertless.models.ProfileDetailsModel;
import com.example.alertless.utils.Constants;
import com.example.alertless.utils.ToastUtils;
import com.example.alertless.view.ProfileListAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName() + Constants.TAG_SUFFIX;
    private List<Profile> profiles;
    private ProfileDetailsRepository profileDetailsRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProfileListAdapter adapter = getProfileListAdapter();

        // Init userRepository
        profileDetailsRepository = ProfileDetailsRepository.getInstance(getApplication());

        List<ProfileDetailsModel> profilesDetails = null;
        try {
            profilesDetails = profileDetailsRepository.getAllProfilesDetails();
        } catch (Exception e) {
            String warnMsg = "Could not find any User Profile !!!";
            Log.e(TAG, e.getMessage(), e);
            ToastUtils.showToast(getApplicationContext(), warnMsg);
        }

        if (profilesDetails != null) {
            adapter.setProfileDetails(profilesDetails);
        }

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
