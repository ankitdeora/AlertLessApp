package com.example.alertless.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.alertless.entities.TimeRangeEntity;
import com.example.alertless.models.TimeRangeModel;

import java.util.List;

@Dao
public abstract class TimeRangeDao extends BaseDao<TimeRangeEntity, TimeRangeModel> {

    private final String entityTable = "time_range";

    @Query("SELECT * FROM " + entityTable)
    protected abstract  List<TimeRangeEntity> findAllTimeRanges();

    @Query("SELECT * FROM time_range WHERE id = :id")
    protected abstract TimeRangeEntity findTimeRange(String id);

    @Query("SELECT * FROM time_range WHERE start_min = :startMin AND end_min = :endMin")
    public abstract TimeRangeEntity findTimeRange(int startMin, int endMin);

    public TimeRangeEntity findTimeRange(TimeRangeModel model) {
        if (model == null) {
            return null;
        }

        return findTimeRange(model.getStartMin(), model.getEndMin());
    }

    @Override
    public List<TimeRangeEntity> findAllEntities() {
        return findAllTimeRanges();
    }

    @Override
    public TimeRangeEntity findEntity(String id) {
        return findTimeRange(id);
    }

    @Override
    public TimeRangeEntity findEntity(TimeRangeModel model) {
        return findTimeRange(model);
    }

}
