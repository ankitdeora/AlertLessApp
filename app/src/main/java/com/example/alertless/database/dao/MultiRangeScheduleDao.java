package com.example.alertless.database.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.alertless.entities.MultiRangeScheduleEntity;
import com.example.alertless.exceptions.AlertlessRuntimeException;
import com.example.alertless.models.MultiRangeScheduleDTO;
import com.example.alertless.models.MultiRangeScheduleModel;

import java.util.List;

@Dao
public abstract class MultiRangeScheduleDao extends BaseDao<MultiRangeScheduleEntity, MultiRangeScheduleDTO> {

    @Override
    @Query("SELECT * FROM multi_range_schedule")
    public abstract List<MultiRangeScheduleEntity> findAllEntities();

    @Override
    @Query("SELECT * FROM multi_range_schedule WHERE id = :id")
    public abstract MultiRangeScheduleEntity findEntity(String id);

    @Query("SELECT * FROM multi_range_schedule WHERE date_schedule_id = :dateScheduleId AND date_range_id = :dateRangeId")
    public abstract MultiRangeScheduleEntity findMultiRangeSchedule(String dateScheduleId, String dateRangeId);

    @Query("SELECT * FROM multi_range_schedule WHERE date_schedule_id = :dateScheduleId")
    public abstract List<MultiRangeScheduleEntity> findDateRanges(String dateScheduleId);

    @Query("SELECT * FROM multi_range_schedule WHERE date_range_id = :dateRangeId")
    public abstract List<MultiRangeScheduleEntity> findMultiRangeSchedulesWithDateRange(String dateRangeId);

    @Query("DELETE FROM multi_range_schedule WHERE date_schedule_id = :dateScheduleId")
    public abstract void deleteForScheduleId(String dateScheduleId);

    @Override
    public MultiRangeScheduleEntity findEntity(MultiRangeScheduleDTO multiRangeScheduleDTO) {
        if (multiRangeScheduleDTO == null) {
            return null;
        }

        return findMultiRangeSchedule(multiRangeScheduleDTO.getDateScheduleId(), multiRangeScheduleDTO.getDateRangeId());
    }

    @Transaction
    public MultiRangeScheduleEntity findOrCreateMultiRangeSchedule(MultiRangeScheduleModel multiRangeScheduleModel) {
        // TODO: Provide implementation
        throw new AlertlessRuntimeException("MultiRangeSchedule findOrCreate() not implemented !!!");
    }

    @Transaction
    public void cascadeDelete(String partyId) {
        // TODO: Provide implementation
        throw new AlertlessRuntimeException("MultiRangeSchedule cascadeDelete() not implemented !!!");
    }

    @Transaction
    public MultiRangeScheduleModel findMultiRangeDaySchedule(String multiRangeScheduleId) {
        // TODO: Provide implementation
        throw new AlertlessRuntimeException("MultiRangeSchedule findCompleteMultiRangeSchedule() not implemented !!!");
    }
}
