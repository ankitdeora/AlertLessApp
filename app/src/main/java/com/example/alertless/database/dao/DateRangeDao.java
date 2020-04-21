package com.example.alertless.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.alertless.entities.DateRangeEntity;

import java.util.List;

@Dao
public interface DateRangeDao extends BaseDao<DateRangeEntity> {

    @Query("SELECT * FROM date_range")
    List<DateRangeEntity> findAllDateRanges();

    @Query("SELECT * FROM date_range WHERE start_date_ms = :startDateMs AND end_date_ms = :endDateMs")
    DateRangeEntity findRange(long startDateMs, long endDateMs);
}
