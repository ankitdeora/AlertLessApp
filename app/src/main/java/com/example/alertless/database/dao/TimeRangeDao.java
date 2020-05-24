package com.example.alertless.database.dao;

import android.util.Log;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.alertless.entities.TimeRangeEntity;
import com.example.alertless.models.TimeRangeModel;
import com.example.alertless.utils.Constants;
import com.example.alertless.utils.ValidationUtils;

import java.util.List;

@Dao
public abstract class TimeRangeDao extends BaseDao<TimeRangeEntity, TimeRangeModel> {

    private static final String TAG = TimeRangeDao.class.getName() + Constants.TAG_SUFFIX;

    @Override
    @Query("SELECT * FROM time_range")
    public abstract  List<TimeRangeEntity> findAllEntities();

    @Override
    @Query("SELECT * FROM time_range WHERE id = :id")
    public abstract TimeRangeEntity findEntity(String id);

    @Query("SELECT * FROM time_range WHERE start_min = :startMin AND end_min = :endMin")
    public abstract TimeRangeEntity findTimeRange(int startMin, int endMin);

    @Override
    @Query("DELETE FROM time_range WHERE id = :timeRangeId")
    public abstract void delete(String timeRangeId);

    @Override
    public TimeRangeEntity findEntity(TimeRangeModel model) {
        if (model == null) {
            return null;
        }

        return findTimeRange(model.getStartMin(), model.getEndMin());
    }

}
