package com.example.alertless.database.repositories;

import android.app.Application;

import com.example.alertless.database.AppDatabase;
import com.example.alertless.database.dao.ProfileDao;
import com.example.alertless.database.dao.ProfileScheduleDao;
import com.example.alertless.entities.ProfileDetailsEntity;
import com.example.alertless.entities.relations.ProfileScheduleRelation;
import com.example.alertless.exceptions.AlertlessDatabaseException;
import com.example.alertless.exceptions.AlertlessException;
import com.example.alertless.exceptions.AlertlessRuntimeException;
import com.example.alertless.models.Profile;
import com.example.alertless.models.ScheduleModel;
import com.example.alertless.utils.DBUtils;
import com.example.alertless.utils.ValidationUtils;

public class ProfileRepository {
    private static volatile ProfileRepository INSTANCE;
    private final AppDatabase appDatabase;
    private final ProfileDao profileDao;
    private final ProfileScheduleDao profileScheduleDao;

    protected ProfileRepository(Application application) {
        this.appDatabase = AppDatabase.getDatabase(application);
        this.profileDao = this.appDatabase.getProfileDao();
        this.profileScheduleDao = this.appDatabase.getProfileScheduleDao();
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

    public ProfileScheduleRelation createOrUpdateSchedule(Profile profile) throws AlertlessDatabaseException {
        ValidationUtils.validateInput(profile);
        ValidationUtils.validateInput(profile.getDetails());
        ValidationUtils.validateInput(profile.getSchedule());

        String profileId = checkAndGetProfileId(profile.getDetails().getName());

        String errMsg = String.format("Could not save schedule for profile: %s", profile.getDetails().getName());
        return DBUtils.executeTaskAndGet(this.profileScheduleDao::createOrUpdateProfileSchedule,
                            profileId, profile.getSchedule(), errMsg);
    }

    public ScheduleModel getSchedule(String profileName) throws AlertlessDatabaseException {
        String profileId = checkAndGetProfileId(profileName);

        String errMsg = String.format("Could not get schedule for profile: %s", profileName);
        return DBUtils.executeTaskAndGet(this.profileScheduleDao::findCompleteSchedule, profileId, errMsg);
    }

    public void deleteProfile(String profileName) throws AlertlessException {
        String profileId = checkAndGetProfileId(profileName);

        String errMsg = String.format("Could not delete profile : %s", profileName);
        DBUtils.executeTask(this.profileDao::cascadeDelete, profileId, errMsg);
    }

    private String checkAndGetProfileId(CharSequence profileName) throws AlertlessDatabaseException {
        ValidationUtils.validateInput(profileName);
        String daoErrMsg = String.format("Could not get profile : %s", profileName);
        ProfileDetailsEntity profileDetailsEntity = DBUtils.executeTaskAndGet(this.profileDao::findProfileDetailsByName,
                                                        profileName.toString(), daoErrMsg);

        if (profileDetailsEntity == null) {
            String errMsg = String.format("Could not find a profile with name : %s", profileName);
            throw new AlertlessRuntimeException(errMsg);
        }

        return profileDetailsEntity.getId();
    }
}
