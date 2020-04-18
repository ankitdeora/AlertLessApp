package com.example.alertless.database.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.alertless.commons.ProfileState;
import com.example.alertless.database.dao.ProfileDao;
import com.example.alertless.entities.ProfileDetailsEntity;
import com.example.alertless.exceptions.AlertlessException;
import com.example.alertless.exceptions.AlertlessIllegalArgumentException;
import com.example.alertless.models.ProfileDetailsModel;
import com.example.alertless.exceptions.AlertlessDatabaseException;
import com.example.alertless.utils.DBUtils;
import com.example.alertless.utils.StringUtils;

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

    public void insertProfileDetails(final ProfileDetailsModel model) throws AlertlessException {

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

    public void updateProfileDetails(CharSequence profileName, final CharSequence updatedName) throws AlertlessException {

        validateProfileName(profileName);
        validateProfileName(updatedName);

        ProfileDetailsEntity entity = getProfileDetailsEntityByName(profileName);

        if (entity == null) {
            String errMsg = String.format("Profile with name : %s does not exist !!!", profileName);
            throw new AlertlessDatabaseException(errMsg);
        }

        ProfileDetailsEntity entityWithUpdatedName = getProfileDetailsEntityByName(updatedName);

        if (entityWithUpdatedName != null) {
            String errMsg = String.format("Profile with name : %s already exist !!!", updatedName);
            throw new AlertlessDatabaseException(errMsg);
        }

        // Update entity
        entity.setName(updatedName.toString());

        String errMsg = String.format("Could not rename profile : %s to %s !!!", profileName, updatedName);
        DBUtils.executeTask(profileDao::update, entity, errMsg);
    }

    public void updateProfileDetails(CharSequence profileName, final boolean active) throws AlertlessException {
        ProfileDetailsEntity entity = getProfileDetailsEntityByName(profileName);

        if (entity == null) {
            String errMsg = String.format("Profile with name : %s does not exist !!!", profileName);
            throw new AlertlessDatabaseException(errMsg);
        }

        // Update entity
        entity.setActive(active);

        String errMsg = String.format("Could not update profile : %s !!!", profileName);
        DBUtils.executeTask(profileDao::update, entity, errMsg);
    }

    public void updateProfileDetails(CharSequence profileName, final ProfileState state) throws AlertlessException {
        updateProfileDetails(profileName, ProfileState.ACTIVE.equals(state));
    }

    public void deleteProfileDetails(CharSequence profileName) throws AlertlessException {

        final ProfileDetailsEntity existingEntity = getProfileDetailsEntityByName(profileName);

        if (existingEntity == null) {
            String errMsg = String.format("Profile with name : %s does not exist !!!", profileName);
            throw new AlertlessDatabaseException(errMsg);
        }

        String errMsg = String.format("Could not delete profile : %s !!!", profileName);
        DBUtils.executeTask(profileDao::delete, existingEntity, errMsg);
    }

    private ProfileDetailsEntity getProfileDetailsEntityByName(CharSequence name) throws AlertlessException {
        validateProfileName(name);

        String errMsg = String.format("Could not find profile by name : %s", name);
        return DBUtils.executeTaskAndGet(profileDao::findByName, name.toString(), errMsg);
    }

    private void validateProfileName(CharSequence name) throws AlertlessIllegalArgumentException {
        if (StringUtils.isBlank(name)) {
            throw new AlertlessIllegalArgumentException("Profile name cannot be blank !!!");
        }
    }

    public ProfileDetailsModel getProfileDetailsByName(CharSequence name) throws AlertlessException {

        final ProfileDetailsEntity entity = getProfileDetailsEntityByName(name);
        return ProfileDetailsModel.getModel(entity);
    }

    public LiveData<List<ProfileDetailsEntity>> getAllProfileDetailsEntity() {
        return allProfileDetailsEntities;
    }

}
