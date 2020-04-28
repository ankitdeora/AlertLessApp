package com.example.alertless.database.dao;

import androidx.room.Dao;

import com.example.alertless.entities.AppDetailsEntity;
import com.example.alertless.models.AppDetailsModel;

@Dao
public abstract class AppDetailsDao extends BaseDao<AppDetailsEntity, AppDetailsModel> {
}
