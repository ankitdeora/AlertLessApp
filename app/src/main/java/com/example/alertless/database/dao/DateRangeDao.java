package com.example.alertless.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.alertless.entities.DateRangeEntity;
import com.example.alertless.models.DateRangeModel;

import java.util.List;

@Dao
public abstract class DateRangeDao extends BaseDao<DateRangeEntity, DateRangeModel> {

    @Query("SELECT * FROM date_range")
    public abstract List<DateRangeEntity> findAllDateRanges();

    @Query("SELECT * FROM date_range WHERE start_date_ms = :startDateMs AND end_date_ms = :endDateMs")
    public abstract DateRangeEntity findRange(long startDateMs, long endDateMs);
}
