package com.example.alertless.database.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.alertless.database.dao.ProfileAppsDao;
import com.example.alertless.database.dao.ProfileDao;
import com.example.alertless.database.dao.ProfileScheduleDao;
import com.example.alertless.entities.ProfileDetailsEntity;
import com.example.alertless.entities.relations.ProfileAppRelation;
import com.example.alertless.entities.relations.ProfileScheduleRelation;
import com.example.alertless.exceptions.AlertlessDatabaseException;
import com.example.alertless.exceptions.AlertlessException;
import com.example.alertless.exceptions.AlertlessRuntimeException;
import com.example.alertless.models.AppDetailsModel;
import com.example.alertless.models.Profile;
import com.example.alertless.models.ScheduleModel;
import com.example.alertless.utils.DBUtils;
import com.example.alertless.utils.ValidationUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProfileRepository extends ProfileDetailsRepository{
    private static volatile ProfileRepository INSTANCE;
    private final ProfileDao profileDao;
    private final ProfileScheduleDao profileScheduleDao;
    private final ProfileAppsDao profileAppsDao;

    private ProfileRepository(Application application) {
        super(application);
        this.profileDao = (ProfileDao) this.dao;
        this.profileScheduleDao = this.appDatabase.getProfileScheduleDao();
        this.profileAppsDao = this.appDatabase.getProfileAppsDao();
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

        this.activeProfileMap.remove(profileName);
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

    public List<ProfileAppRelation> createOrUpdateProfileApps(Profile profile) throws AlertlessDatabaseException {
        ValidationUtils.validateInput(profile);
        ValidationUtils.validateInput(profile.getDetails());
        ValidationUtils.validateInput(profile.getApps());

        String profileId = checkAndGetProfileId(profile.getDetails().getName());

        String errMsg = String.format("Could not save apps for profile: %s", profile.getDetails().getName());
        return DBUtils.executeTaskAndGet(this.profileAppsDao::createOrUpdateProfileApps,
                profileId, profile.getApps(), errMsg);
    }

    public void removeProfileApps(String profileName, AppDetailsModel... apps) throws AlertlessDatabaseException {
        ValidationUtils.validateInput(profileName);
        ValidationUtils.validateInput(apps);
        removeProfileAppsList(profileName, Arrays.asList(apps));
    }

    public void removeProfileAppsList(String profileName, List<AppDetailsModel> apps) throws AlertlessDatabaseException {
        ValidationUtils.validateInput(profileName);
        ValidationUtils.validateInput(apps);

        String profileId = checkAndGetProfileId(profileName);
        String errMsg = String.format("Could not remove apps for profile: %s", profileName);
        DBUtils.executeTask(this.profileAppsDao::removeProfileApps, profileId, apps, errMsg);
    }

    public List<AppDetailsModel> getProfileApps(String profileName) throws AlertlessDatabaseException {
        String profileId = checkAndGetProfileId(profileName);

        String errMsg = String.format("Could not get Apps for profile: %s", profileName);
        return DBUtils.executeTaskAndGet(this.profileAppsDao::getProfileSilentApps, profileId, errMsg);
    }

    public LiveData<List<ProfileAppRelation>> getLiveProfileApps(String profileName) throws AlertlessDatabaseException {
        String profileId = checkAndGetProfileId(profileName);

        return this.profileAppsDao.findLiveProfileApps(profileId);
    }

    public List<AppDetailsModel> getAppModelsFromRelations(List<ProfileAppRelation> relations) throws AlertlessDatabaseException {
        ValidationUtils.validateInput(relations);

        String errMsg = String.format("Could not get AppModels from relations");
        return DBUtils.executeTaskAndGet(this.profileAppsDao::getAppModelsFromRelations, relations, errMsg);
    }
}
