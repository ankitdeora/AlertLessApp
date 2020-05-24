package com.example.alertless.database.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.alertless.enums.ProfileState;
import com.example.alertless.entities.ProfileDetailsEntity;
import com.example.alertless.exceptions.AlertlessDatabaseException;
import com.example.alertless.exceptions.AlertlessException;
import com.example.alertless.models.ProfileDetailsModel;
import com.example.alertless.utils.ValidationUtils;

import java.util.List;

public class ProfileDetailsRepository extends BaseRepository<ProfileDetailsEntity, ProfileDetailsModel> {
    private static volatile ProfileDetailsRepository INSTANCE;
    private LiveData<List<ProfileDetailsEntity>> allProfileDetailsEntities;

    private ProfileDetailsRepository(Application application) {
        super(application);
        this.dao = appDatabase.getProfileDao();
        allProfileDetailsEntities = this.dao.findAllLiveEntities();
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

    public void updateProfileDetails(CharSequence profileName, final CharSequence updatedName) throws AlertlessException {

        ValidationUtils.validateInput(updatedName);
        ProfileDetailsEntity entity = checkAndGetProfile(profileName);

        String id = entity.getId();
        ProfileDetailsModel model = entity.getModel();

        // update name
        model.setName(updatedName.toString());
        this.updateEntity(id, model);
    }

    public void updateProfileDetails(CharSequence profileName, final boolean active) throws AlertlessException {
        ProfileDetailsEntity entity = checkAndGetProfile(profileName);

        String id = entity.getId();
        ProfileDetailsModel model = entity.getModel();

        // update active state
        model.setActive(active);
        this.updateEntity(id, model);
    }

    public void updateProfileDetails(CharSequence profileName, final ProfileState state) throws AlertlessException {
        updateProfileDetails(profileName, ProfileState.ACTIVE.equals(state));
    }

    private ProfileDetailsEntity getEntityByName(CharSequence name) throws AlertlessException {
        ValidationUtils.validateInput(name);

        ProfileDetailsModel model = ProfileDetailsModel.builder().name(name.toString()).build();
        return this.getEntity(model);
    }

    public ProfileDetailsModel getProfileDetailsByName(CharSequence name) throws AlertlessException {
        return getEntityByName(name).getModel();
    }

    public LiveData<List<ProfileDetailsEntity>> getAllProfileDetailsEntity() {
        return allProfileDetailsEntities;
    }

    private ProfileDetailsEntity checkAndGetProfile(CharSequence profileName) throws AlertlessException {
        ValidationUtils.validateInput(profileName);

        ProfileDetailsEntity entity = getEntityByName(profileName);

        if (entity == null) {
            String errMsg = String.format("Update Failed as Profile with name : %s does not exist !!!", profileName);
            throw new AlertlessDatabaseException(errMsg);
        }

        return entity;
    }
}
