package com.example.alertless.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.alertless.entities.ProfileDetails;

import java.util.List;

@Dao
public interface ProfileDao {

    @Query("SELECT * FROM profile_details")
    List<ProfileDetails> getProfiles();

    @Query("SELECT * FROM profile_details WHERE active=1")
    List<ProfileDetails> getActiveProfiles();

    @Insert
    void insertAll(ProfileDetails... profileDetails);

    @Delete
    void delete(ProfileDetails profileDetails);

    /*
    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    List<User> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    User findByName(String first, String last);
     */
}
