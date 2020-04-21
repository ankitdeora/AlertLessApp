package com.example.alertless.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.alertless.entities.ProfileDetailsEntity;

import java.util.List;

@Dao
public interface ProfileDao extends BaseDao<ProfileDetailsEntity> {

    @Query("SELECT * FROM profile_details")
    LiveData<List<ProfileDetailsEntity>> getAllProfiles();

    @Query("SELECT * FROM profile_details WHERE name LIKE :profileName")
    ProfileDetailsEntity findByName(String profileName);

    @Query("SELECT * FROM profile_details WHERE active=1")
    LiveData<List<ProfileDetailsEntity>> getActiveProfiles();

}
