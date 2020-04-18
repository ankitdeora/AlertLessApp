package com.example.alertless.database.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.alertless.database.dao.ProfileDao;
import com.example.alertless.entities.ProfileDetailsEntity;
import com.example.alertless.models.ProfileDetailsModel;
import com.example.alertless.exceptions.AlertlessDatabaseException;
import com.example.alertless.utils.DBUtils;

import java.util.Date;
import java.util.List;

public class ProfileDetailsRepository extends Repository {
    private static volatile ProfileDetailsRepository INSTANCE;
    private final ProfileDao profileDao;
    private LiveData<List<ProfileDetailsEntity>> allProfileDetailsEntities;

    private ProfileDetailsRepository(Application application) {
        super(application);
        profileDao = appDatabase.getProfileDao();
        allProfileDetailsEntities = profileDao.getAllProfiles();
    }

    public static ProfileDetailsRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ProfileDetailsRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ProfileDetailsRepository(application);
                }
            }
        }

        return INSTANCE;
    }

    public void insertProfileDetails(final ProfileDetailsModel model) throws AlertlessDatabaseException {

        String profileName = model.getName();
        final ProfileDetailsEntity existingEntity = getProfileDetailsEntityByName(profileName);

        if (existingEntity != null) {
            String errMsg = String.format("Profile with name : %s already exist !!!", profileName);
            throw new AlertlessDatabaseException(errMsg);
        }

        String uniqueId = String.valueOf(new Date().getTime());
        final ProfileDetailsEntity entity = ProfileDetailsEntity.getEntity(model, uniqueId);

        String errMsg = String.format("Could not insert profile : %s !!!", profileName);
        DBUtils.executeTask(profileDao::insert, entity, errMsg);
    }

    public void updateProfileDetails(final ProfileDetailsModel updatedModel) throws AlertlessDatabaseException {
        String profileName = updatedModel.getName();
        final ProfileDetailsEntity existingEntity = getProfileDetailsEntityByName(profileName);

        if (existingEntity == null) {
            String errMsg = String.format("Profile with name : %s does not exist !!!", profileName);
            throw new AlertlessDatabaseException(errMsg);
        }

        final ProfileDetailsEntity updatedEntity = ProfileDetailsEntity.getEntity(updatedModel, existingEntity.getId());
        String errMsg = String.format("Could not update profile : %s !!!", profileName);
        DBUtils.executeTask(profileDao::update, updatedEntity, errMsg);
    }

    public void deleteProfileDetails(CharSequence profileName) throws AlertlessDatabaseException {

        final ProfileDetailsEntity existingEntity = getProfileDetailsEntityByName(profileName);

        if (existingEntity == null) {
            String errMsg = String.format("Profile with name : %s does not exist !!!", profileName);
            throw new AlertlessDatabaseException(errMsg);
        }

        String errMsg = String.format("Could not delete profile : %s !!!", profileName);
        DBUtils.executeTask(profileDao::delete, existingEntity, errMsg);
    }

    private ProfileDetailsEntity getProfileDetailsEntityByName(CharSequence name) throws AlertlessDatabaseException {

        String errMsg = String.format("Could not find profile by name : %s", name);
        return DBUtils.executeTaskAndGet(profileDao::findByName, name.toString(), errMsg);
    }

    public ProfileDetailsModel getProfileDetailsByName(CharSequence name) throws AlertlessDatabaseException {

        final ProfileDetailsEntity entity = getProfileDetailsEntityByName(name);
        return ProfileDetailsModel.getModel(entity);
    }

    public LiveData<List<ProfileDetailsEntity>> getAllProfileDetailsEntity() {
        return allProfileDetailsEntities;
    }

}
