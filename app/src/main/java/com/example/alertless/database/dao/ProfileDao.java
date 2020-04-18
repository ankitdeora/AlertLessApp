package com.example.alertless.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.alertless.entities.ProfileDetailsEntity;

import java.util.List;

@Dao
public interface ProfileDao {

    @Query("SELECT * FROM profile_details")
    LiveData<List<ProfileDetailsEntity>> getAllProfiles();

    @Query("SELECT * FROM profile_details WHERE profile_name LIKE :profileName")
    ProfileDetailsEntity findByName(String profileName);

    @Query("SELECT * FROM profile_details WHERE active=1")
    LiveData<List<ProfileDetailsEntity>> getActiveProfiles();

    @Insert
    void insert(ProfileDetailsEntity profileDetailsEntity);

    @Delete
    void delete(ProfileDetailsEntity profileDetailsEntity);

    @Update
    void update(ProfileDetailsEntity profileDetailsEntity);

    /*
    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    List<User> loadAllByIds(int[] userIds);

     */
}