package com.example.alertless.database.dao;

import androidx.room.Dao;
import androidx.room.Query;
import com.example.alertless.entities.PartyEntity;
import com.example.alertless.models.BaseModel;
import java.util.List;

@Dao
public abstract class PartyDao extends BaseDao<PartyEntity, BaseModel> {

    @Override
    @Query("SELECT * FROM party")
    public abstract List<PartyEntity> findAllEntities();

    @Override
    @Query("SELECT * FROM party WHERE id = :id")
    public abstract PartyEntity findEntity(String id);

    @Override
    @Query("DELETE FROM party WHERE id = :id")
    public abstract void delete(String id);
}
