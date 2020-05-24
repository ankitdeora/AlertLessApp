package com.example.alertless.database.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.alertless.entities.DateRangeEntity;
import com.example.alertless.models.DateRangeModel;
import com.example.alertless.utils.ValidationUtils;

import java.util.List;

@Dao
public abstract class DateRangeDao extends BaseDao<DateRangeEntity, DateRangeModel> {

    @Override
    @Query("SELECT * FROM date_range")
    public abstract List<DateRangeEntity> findAllEntities();

    @Override
    @Query("SELECT * FROM date_range WHERE id = :id")
    public abstract DateRangeEntity findEntity(String id);

    @Query("SELECT * FROM date_range WHERE start_date_ms = :startDateMs AND end_date_ms = :endDateMs")
    public abstract DateRangeEntity findDateRange(long startDateMs, long endDateMs);

    @Override
    @Query("DELETE FROM date_range WHERE id = :dateRangeId")
    public abstract void delete(String dateRangeId);

    @Override
    public DateRangeEntity findEntity(DateRangeModel model) {
        if (model == null) {
            return null;
        }

        return findDateRange(model.getStartDateMs(), model.getEndDateMs());
    }

}
