package com.example.alertless.database.repositories;

import android.app.Application;
import android.util.Log;

import com.example.alertless.database.AppDatabase;
import com.example.alertless.database.dao.ProfileDao;
import com.example.alertless.entities.ProfileDetails;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class ProfileRepository extends Repository {
    private static volatile ProfileRepository INSTANCE;
    private final ProfileDao profileDao;

    private ProfileRepository(Application application) {
        super(application);
        profileDao = appDatabase.getProfileDao();
    }

    public static ProfileRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ProfileRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ProfileRepository(application);
                }
            }
        }

        return INSTANCE;
    }

    public void insertProfile(ProfileDetails profileDetails) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            profileDao.insertAll(profileDetails);
        });
    }

    public List<ProfileDetails> getAllProfiles() throws Exception{
        Callable<List<ProfileDetails>> getProfilesTask = new Callable<List<ProfileDetails>>() {
            @Override
            public List<ProfileDetails> call() {
                return profileDao.getProfiles();
            }
        };

        Future<List<ProfileDetails>> futureProfiles = AppDatabase.databaseWriteExecutor.submit(getProfilesTask);

        List<ProfileDetails> profileDetails = null;
        try {
            profileDetails = futureProfiles.get();
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e("DB_ERROR_GET_PROFILES", e.getMessage());
            throw e;
        }

        return profileDetails;
    }

}
