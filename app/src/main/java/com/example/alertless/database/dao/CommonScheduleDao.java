package com.example.alertless.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.alertless.entities.MultiRangeScheduleEntity;
import com.example.alertless.entities.SchedulableEntity;
import com.example.alertless.entities.WeekScheduleEntity;

import java.util.List;
import java.util.function.Function;

@Dao
public abstract class CommonScheduleDao {

    @Query("SELECT * FROM week_schedule WHERE date_range_id = :dateRangeId")
    abstract List<WeekScheduleEntity> findWeekSchedulesWithDateRange(String dateRangeId);

    @Query("SELECT * FROM multi_range_schedule WHERE date_range_id = :dateRangeId")
    abstract List<MultiRangeScheduleEntity> findMultiRangeSchedulesWithDateRange(String dateRangeId);

    boolean isDateReferredByOther(SchedulableEntity entity, String originalDateRangeId) {
        boolean dateReferredByOther = false;

        List<WeekScheduleEntity> weekSchedulesWithSameDateRange = findWeekSchedulesWithDateRange(originalDateRangeId);
        List<MultiRangeScheduleEntity> multiRangeSchedulesWithSameDateRange = findMultiRangeSchedulesWithDateRange(originalDateRangeId);

        if (isDateReferredInOtherEntities(entity, weekSchedulesWithSameDateRange) ||
                isDateReferredInOtherEntities(entity, multiRangeSchedulesWithSameDateRange)) {
            dateReferredByOther = true;
        }

        return dateReferredByOther;
    }

    private boolean isDateReferredInOtherEntities(SchedulableEntity entity, List<? extends SchedulableEntity> entities) {

        return entities != null && !entities.isEmpty() &&
                (entities.size() > 1 || !entities.get(0).getScheduleId().equals(entity.getScheduleId()));
    }
}
