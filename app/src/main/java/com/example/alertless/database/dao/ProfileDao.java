package com.example.alertless.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.alertless.entities.ProfileDetailsEntity;
import com.example.alertless.models.ProfileDetailsModel;

import java.util.List;

@Dao
public abstract class ProfileDao extends BaseDao<ProfileDetailsEntity, ProfileDetailsModel> {

    @Query("SELECT * FROM profile_details")
    protected abstract LiveData<List<ProfileDetailsEntity>> getAllLiveProfiles();

    @Query("SELECT * FROM profile_details WHERE name LIKE :profileName")
    protected abstract ProfileDetailsEntity findProfileDetailsByName(String profileName);

    @Query("SELECT * FROM profile_details WHERE id = :id")
    protected abstract ProfileDetailsEntity findProfileDetailsById(String id);

    @Query("SELECT * FROM profile_details WHERE active=1")
    public abstract LiveData<List<ProfileDetailsEntity>> getActiveProfiles();

    protected ProfileDetailsEntity findProfileDetails(ProfileDetailsModel model) {
        if (model == null) {
            return null;
        }
        return findProfileDetailsByName(model.getName());
    }

    @Override
    public LiveData<List<ProfileDetailsEntity>> findAllLiveEntities() {
        return getAllLiveProfiles();
    }

    @Override
    public ProfileDetailsEntity findEntity(ProfileDetailsModel model) {
        return findProfileDetails(model);
    }

    @Override
    public ProfileDetailsEntity findEntity(String id) {
        return findProfileDetailsById(id);
    }
}
