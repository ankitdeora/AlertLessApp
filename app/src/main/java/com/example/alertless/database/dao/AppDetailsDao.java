package com.example.alertless.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.alertless.entities.AppDetailsEntity;
import com.example.alertless.models.AppDetailsModel;

import java.util.List;

@Dao
public abstract class AppDetailsDao extends BaseDao<AppDetailsEntity, AppDetailsModel> {

    @Override
    @Query("SELECT * FROM app_details")
    public abstract List<AppDetailsEntity> findAllEntities();

    @Override
    @Query("SELECT * FROM app_details")
    public abstract LiveData<List<AppDetailsEntity>> findAllLiveEntities();

    @Override
    @Query("SELECT * FROM app_details WHERE id = :id")
    public abstract AppDetailsEntity findEntity(String id);

    @Query("SELECT * FROM app_details WHERE package_name = :packageName")
    public abstract AppDetailsEntity findAppByPackage(String packageName);

    @Override
    @Query("DELETE FROM app_details WHERE id = :appId")
    public abstract void delete(String appId);

    @Override
    public AppDetailsEntity findEntity(AppDetailsModel model) {
        if (model == null) {
            return null;
        }

        return findAppByPackage(model.getPackageName());
    }
}
