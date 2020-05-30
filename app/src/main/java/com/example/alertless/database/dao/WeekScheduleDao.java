package com.example.alertless.database.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.alertless.entities.BaseEntity;
import com.example.alertless.entities.SchedulableEntity;
import com.example.alertless.enums.ScheduleType;
import com.example.alertless.database.AppDatabase;
import com.example.alertless.entities.DateRangeEntity;
import com.example.alertless.entities.PartyEntity;
import com.example.alertless.entities.WeekScheduleEntity;
import com.example.alertless.models.DateRangeModel;
import com.example.alertless.models.WeekScheduleDTO;
import com.example.alertless.models.WeekScheduleModel;
import com.example.alertless.utils.ValidationUtils;
import com.example.alertless.utils.WeekUtils;

import java.util.List;
import java.util.function.Function;

import ca.antonious.materialdaypicker.MaterialDayPicker;

import static com.example.alertless.utils.StringUtils.*;

@Dao
public abstract class WeekScheduleDao extends BaseDao<WeekScheduleEntity, WeekScheduleDTO> {

    private final DateRangeDao dateRangeDao;
    private final CommonScheduleDao commonScheduleDao;
    private final PartyDao partyDao;

    public WeekScheduleDao(AppDatabase appDatabase) {
        this.dateRangeDao = appDatabase.getDateRangeDao();
        this.partyDao = appDatabase.getPartyDao();
        this.commonScheduleDao = appDatabase.getCommonScheduleDao();
    }

    @Override
    @Query("SELECT * FROM week_schedule")
    public abstract List<WeekScheduleEntity> findAllEntities();

    @Override
    @Query("SELECT * FROM week_schedule WHERE week_schedule_id = :id")
    public abstract WeekScheduleEntity findEntity(String id);

    @Query("SELECT * FROM week_schedule WHERE weekdays = :weekdays AND date_range_id = :dateRangeId")
    public abstract WeekScheduleEntity findWeekSchedule(byte weekdays, String dateRangeId);

    @Override
    @Query("DELETE FROM week_schedule WHERE week_schedule_id = :weekScheduleId")
    public abstract void delete(String weekScheduleId);

    @Override
    public WeekScheduleEntity findEntity(WeekScheduleDTO weekScheduleDTO) {
        if (weekScheduleDTO == null) {
            return null;
        }

        return findWeekSchedule(weekScheduleDTO.getWeekdays(), weekScheduleDTO.getDateRangeId());
    }

    @Override
    @Transaction
    public WeekScheduleEntity findOrCreateEntity(WeekScheduleDTO weekScheduleDTO) {
        WeekScheduleEntity weekScheduleEntity = findEntity(weekScheduleDTO);

        if (weekScheduleEntity != null) {
            return weekScheduleEntity;
        }

        String weekScheduleId = getUniqueId();
        PartyEntity partyEntity = PartyEntity.builder()
                                        .id(weekScheduleId)
                                        .scheduleType(ScheduleType.BY_WEEK.name())
                                    .build();

        this.partyDao.insert(partyEntity);

        weekScheduleEntity = weekScheduleDTO.getEntity(weekScheduleId);
        insert(weekScheduleEntity);

        return weekScheduleEntity;
    }

    @Transaction
    public WeekScheduleEntity findOrCreateWeekSchedule(WeekScheduleModel weekSchedule) {
        ValidationUtils.validateInput(weekSchedule);

        DateRangeModel dateRangeModel = weekSchedule.getDateRangeModel();
        DateRangeEntity dateRangeEntity = this.dateRangeDao.findOrCreateEntity(dateRangeModel);

        List<MaterialDayPicker.Weekday> weekdays = weekSchedule.getWeekdays();
        byte weekByte = WeekUtils.getByte(weekdays);

        WeekScheduleDTO weekScheduleDTO = WeekScheduleDTO.builder()
                                                .weekdays(weekByte)
                                                .dateRangeId(dateRangeEntity.getId())
                                            .build();

        return findOrCreateEntity(weekScheduleDTO);
    }

    @Transaction
    public void cascadeDelete(String weekScheduleId) {
        cascadeDelete(weekScheduleId, true);
    }

    @Transaction
    public void cascadeDelete(String weekScheduleId, boolean deleteParty) {
        ValidationUtils.validateInput(weekScheduleId);

        WeekScheduleEntity weekScheduleEntity = findEntity(weekScheduleId);
        String dateRangeId = weekScheduleEntity.getDateRangeId();
        boolean dateReferredByOther = this.commonScheduleDao.isDateReferredByOther(weekScheduleEntity, dateRangeId);

        // delete week schedule entity
        delete(weekScheduleEntity);

        // delete party
        if (deleteParty) {
            this.partyDao.delete(weekScheduleId);
        }

        // check and delete date range
        if (!dateReferredByOther) {
            this.dateRangeDao.delete(dateRangeId);
        }
    }

    @Transaction
    public WeekScheduleModel findWeekDaySchedule(String weekScheduleId) {
        ValidationUtils.validateInput(weekScheduleId);

        WeekScheduleModel weekScheduleModel = new WeekScheduleModel();

        WeekScheduleEntity weekScheduleEntity = findEntity(weekScheduleId);

        // set week days
        byte weekByte = weekScheduleEntity.getWeekdays();
        List<MaterialDayPicker.Weekday> weekdays = WeekUtils.getWeekdays(weekByte);
        weekScheduleModel.setWeekdays(weekdays);

        // set date range
        String dateRangeId = weekScheduleEntity.getDateRangeId();
        DateRangeEntity dateRangeEntity = this.dateRangeDao.findEntity(dateRangeId);
        DateRangeModel dateRangeModel = dateRangeEntity.getModel();
        weekScheduleModel.setDateRangeModel(dateRangeModel);

        return weekScheduleModel;
    }

    @Transaction
    public PartyEntity findOrUpdateWeekSchedule(String weekScheduleId, WeekScheduleModel requestedWeekSchedule,
                                                                                            boolean weekReferredByOther) {
        ValidationUtils.validateInput(weekScheduleId);
        ValidationUtils.validateInput(requestedWeekSchedule);

        if (weekReferredByOther) {
            WeekScheduleEntity weekScheduleEntity = findOrCreateWeekSchedule(requestedWeekSchedule);
            return this.partyDao.findEntity(weekScheduleEntity.getWeekScheduleId());
        }

        WeekScheduleEntity existingWeekScheduleEntity = findEntity(weekScheduleId);

        // get updated date range id
        String dateRangeId = existingWeekScheduleEntity.getDateRangeId();
        boolean dateReferredByOther = this.commonScheduleDao.isDateReferredByOther(existingWeekScheduleEntity, dateRangeId);

        DateRangeEntity updatedDateRangeEntity = this.dateRangeDao.findOrUpdateEntity(dateRangeId,
                requestedWeekSchedule.getDateRangeModel(), dateReferredByOther);
        String newDateRangeId = updatedDateRangeEntity.getId();

        // get week byte
        List<MaterialDayPicker.Weekday> requestedWeekdays = requestedWeekSchedule.getWeekdays();
        byte requestedWeekByte = WeekUtils.getByte(requestedWeekdays);

        WeekScheduleDTO updatedWeekScheduleDTO = WeekScheduleDTO.builder()
                                                            .weekdays(requestedWeekByte)
                                                            .dateRangeId(newDateRangeId)
                                                        .build();

        WeekScheduleEntity updatedEntity = findOrUpdateEntity(weekScheduleId, updatedWeekScheduleDTO, weekReferredByOther);
        String newWeekScheduleId = updatedEntity.getWeekScheduleId();

        // Schedule is updated actually
        if (newWeekScheduleId.equals(weekScheduleId)) {
            // delete the date range id which is not referred by any schedule
            if (!dateReferredByOther && !newDateRangeId.equals(dateRangeId)) {
                this.dateRangeDao.delete(dateRangeId);
            }
        }

        return this.partyDao.findEntity(newWeekScheduleId);
    }
}
