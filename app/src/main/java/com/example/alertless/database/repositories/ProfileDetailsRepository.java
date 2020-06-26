package com.example.alertless.database.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.alertless.database.dao.ProfileDao;
import com.example.alertless.enums.ProfileState;
import com.example.alertless.entities.ProfileDetailsEntity;
import com.example.alertless.exceptions.AlertlessDatabaseException;
import com.example.alertless.exceptions.AlertlessException;
import com.example.alertless.models.Profile;
import com.example.alertless.models.ProfileDetailsModel;
import com.example.alertless.utils.ValidationUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ProfileDetailsRepository extends BaseRepository<ProfileDetailsEntity, ProfileDetailsModel> {
    protected LiveData<List<ProfileDetailsEntity>> allProfileDetailsEntities;
    protected LiveData<List<ProfileDetailsEntity>> activeProfiles;

    // Map of App's package name and list of active profiles
    protected Map<String, Set<Profile>> packageProfilesMap;
    protected Map<String, Profile> activeProfileMap;

    ProfileDetailsRepository(Application application) {
        super(application);
        this.dao = appDatabase.getProfileDao();

        allProfileDetailsEntities = this.dao.findAllLiveEntities();
        activeProfiles = ((ProfileDao)this.dao).getActiveProfiles();

        this.packageProfilesMap = new HashMap<>();
        this.activeProfileMap = new HashMap<>();
    }

    public Map<String, Set<Profile>> getPackageProfilesMap() {
        return packageProfilesMap;
    }

    public Map<String, Profile> getActiveProfilesMap() {
        return activeProfileMap;
    }

    public void createProfile(ProfileDetailsModel profileDetails) throws AlertlessException {
        this.createEntity(profileDetails);

        if (profileDetails.isActive()) {
            this.activeProfileMap.put(profileDetails.getName(), Profile.builder().details(profileDetails).build());
        }
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

    public LiveData<List<ProfileDetailsEntity>> getActiveProfiles() {
        return activeProfiles;
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
