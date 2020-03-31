package com.example.alertless.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alertless.R;
import com.example.alertless.database.repositories.ProfileRepository;
import com.example.alertless.entities.ProfileDetails;
import com.example.alertless.models.Profile;
import com.example.alertless.utils.ToastUtils;
import com.example.alertless.view.ProfileListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.alertless.activities.MainActivity.MESSAGE";
    public static final String TAG = MainActivity.class.getName() + ".tag";
    private List<Profile> profiles;
    private ProfileRepository profileRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProfileListAdapter adapter = getProfileListAdapter();

        // Init userRepository
        profileRepository = ProfileRepository.getInstance(getApplication());

        List<ProfileDetails> profilesDetails = null;
        try {
            profilesDetails = profileRepository.getAllProfiles();
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
        final ProfileListAdapter adapter = new ProfileListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        return adapter;
    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        final String message = editText.getText().toString();

        // insert to DB
        ProfileDetails profileDetails = new ProfileDetails(String.valueOf(message.hashCode()), message, message.length() % 2 == 0);
        profileRepository.insertProfile(profileDetails);
        ToastUtils.showToast(getApplicationContext(),"Saved Profile : " + profileDetails.toString());

        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void editProfile(View view) throws Exception {
        Intent intent = new Intent(this, ProfileEditActivity.class);
        startActivity(intent);
    }

}
