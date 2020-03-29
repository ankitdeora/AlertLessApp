package com.example.alertless.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alertless.R;
import com.example.alertless.database.repositories.ProfileRepository;
import com.example.alertless.entities.ProfileDetails;
import com.example.alertless.models.Profile;
import com.example.alertless.utils.ToastUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.alertless.activities.MainActivity.MESSAGE";
    private List<Profile> profiles;
    private ProfileRepository profileRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init userRepository
        profileRepository = ProfileRepository.getInstance(getApplication());
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

    public void listAllUsers(View view) throws Exception {

        List<ProfileDetails> profiles = profileRepository.getAllProfiles();
        ToastUtils.showToast(getApplicationContext(), profiles.toString());
    }

    public void editProfile(View view) throws Exception {
        Intent intent = new Intent(this, ProfileEditActivity.class);
        startActivity(intent);
    }

}
