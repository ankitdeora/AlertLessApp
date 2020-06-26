package com.example.alertless.view.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.alertless.database.repositories.ProfileRepository;
import com.example.alertless.entities.ProfileDetailsEntity;

import java.util.List;

public class ProfileViewModel extends AndroidViewModel {
    private ProfileRepository profileRepository;
    private LiveData<List<ProfileDetailsEntity>> allProfileEntities;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        this.profileRepository = ProfileRepository.getInstance(getApplication());
        this.allProfileEntities = this.profileRepository.getAllProfileDetailsEntity();
    }

    public LiveData<List<ProfileDetailsEntity>> getAllProfileDetailsEntities() {
        return this.allProfileEntities;
    }
}
