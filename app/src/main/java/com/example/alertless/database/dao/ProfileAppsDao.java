package com.example.alertless.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.alertless.entities.relations.ProfileAppRelation;
import com.example.alertless.models.BaseModel;

import java.util.List;

@Dao
public abstract class ProfileAppsDao extends BaseDao<ProfileAppRelation, BaseModel> {

    @Override
    @Query("SELECT * FROM profile_apps")
    public abstract List<ProfileAppRelation> findAllEntities();

    @Override
    @Query("SELECT * FROM profile_apps WHERE id = :id")
    public abstract ProfileAppRelation findEntity(String id);

    @Query("SELECT * FROM profile_apps WHERE profile_id = :profileId AND app_id = :appId")
    public abstract ProfileAppRelation findProfileAppRelation(String profileId, String appId);

    @Query("SELECT * FROM profile_apps WHERE profile_id = :profileId")
    public abstract List<ProfileAppRelation> findProfileApps(String profileId);
}
