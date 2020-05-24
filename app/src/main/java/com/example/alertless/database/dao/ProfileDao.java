package com.example.alertless.database.dao;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.alertless.database.AppDatabase;
import com.example.alertless.entities.ProfileDetailsEntity;
import com.example.alertless.entities.TimeRangeEntity;
import com.example.alertless.exceptions.AlertlessRuntimeException;
import com.example.alertless.models.ProfileDetailsModel;
import com.example.alertless.utils.ValidationUtils;

import java.util.List;

@Dao
public abstract class ProfileDao extends BaseDao<ProfileDetailsEntity, ProfileDetailsModel> {

    private final ProfileScheduleDao profileScheduleDao;

    public ProfileDao(AppDatabase appDatabase) {
        this.profileScheduleDao = appDatabase.getProfileScheduleDao();
    }

    @Override
    @Query("SELECT * FROM profile_details")
    public abstract LiveData<List<ProfileDetailsEntity>> findAllLiveEntities();

    @Override
    @Query("SELECT * FROM profile_details")
    public abstract List<ProfileDetailsEntity> findAllEntities();

    @Override
    @Query("SELECT * FROM profile_details WHERE id = :id")
    public abstract ProfileDetailsEntity findEntity(String id);

    @Query("SELECT * FROM profile_details WHERE name LIKE :profileName")
    public abstract ProfileDetailsEntity findProfileDetailsByName(String profileName);

    @Query("SELECT * FROM profile_details WHERE active=1")
    public abstract LiveData<List<ProfileDetailsEntity>> getActiveProfiles();

    @Override
    @Query("DELETE FROM profile_details WHERE id = :id")
    public abstract void delete(String id);

    @Override
    public ProfileDetailsEntity findEntity(ProfileDetailsModel model) {
        if (model == null) {
            return null;
        }
        return findProfileDetailsByName(model.getName());
    }

    @Transaction
    public void cascadeDelete(String profileId) {
        ValidationUtils.validateInput(profileId);

        // delete profile schedule
        this.profileScheduleDao.cascadeDelete(profileId);

        // delete profile apps

        // delete profile
        this.delete(profileId);
    }
}
