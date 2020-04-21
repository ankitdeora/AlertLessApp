package com.example.alertless.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.alertless.entities.TimeRangeEntity;
import com.example.alertless.models.TimeRangeModel;

import java.util.List;

@Dao
public abstract class TimeRangeDao implements BaseDao<TimeRangeEntity> {

    @Query("SELECT * FROM time_range")
    public abstract  List<TimeRangeEntity> findAllTimeRanges();

    @Query("SELECT * FROM time_range WHERE start_min = :startMin AND end_min = :endMin")
    protected abstract TimeRangeEntity findRange(int startMin, int endMin);

    public TimeRangeEntity findRange(TimeRangeModel model) {
        return findRange(model.getStartMin(), model.getEndMin());
    }

}
