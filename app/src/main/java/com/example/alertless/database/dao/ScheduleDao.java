package com.example.alertless.database.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.alertless.enums.ScheduleType;
import com.example.alertless.database.AppDatabase;
import com.example.alertless.entities.MultiRangeScheduleEntity;
import com.example.alertless.entities.PartyEntity;
import com.example.alertless.entities.ScheduleEntity;
import com.example.alertless.entities.TimeRangeEntity;
import com.example.alertless.entities.WeekScheduleEntity;
import com.example.alertless.exceptions.AlertlessIllegalArgumentException;
import com.example.alertless.exceptions.AlertlessRuntimeException;
import com.example.alertless.models.MultiRangeScheduleModel;
import com.example.alertless.models.ScheduleDTO;
import com.example.alertless.models.ScheduleModel;
import com.example.alertless.models.TimeRangeModel;
import com.example.alertless.models.WeekScheduleModel;
import com.example.alertless.utils.ValidationUtils;

import java.util.List;

@Dao
public abstract class ScheduleDao extends BaseDao<ScheduleEntity, ScheduleDTO> {

    private final TimeRangeDao timeRangeDao;
    private final PartyDao partyDao;
    private final WeekScheduleDao weekScheduleDao;
    private final MultiRangeScheduleDao multiRangeScheduleDao;

    public ScheduleDao(AppDatabase appDatabase) {
        this.timeRangeDao = appDatabase.getTimeRangeDao();
        this.partyDao = appDatabase.getPartyDao();
        this.weekScheduleDao = appDatabase.getWeekScheduleDao();
        this.multiRangeScheduleDao = appDatabase.getMultiRangeScheduleDao();
    }

    @Override
    @Query("SELECT * FROM schedule")
    public abstract List<ScheduleEntity> findAllEntities();

    @Override
    @Query("SELECT * FROM schedule WHERE id = :id")
    public abstract ScheduleEntity findEntity(String id);

    @Query("SELECT * FROM schedule WHERE party_id = :partyId AND time_range_id = :timeRangeId")
    public abstract ScheduleEntity findSchedule(String partyId, String timeRangeId);

    @Query("SELECT * FROM schedule WHERE time_range_id = :timeRangeId")
    public abstract List<ScheduleEntity> findScheduleWithTimeRangeId(String timeRangeId);

    @Query("SELECT * FROM schedule WHERE party_id = :partyId")
    public abstract List<ScheduleEntity> findScheduleWithPartyId(String partyId);

    @Override
    @Query("DELETE FROM schedule WHERE id = :scheduleId")
    public abstract void delete(String scheduleId);

    @Override
    public ScheduleEntity findEntity(ScheduleDTO scheduleDTO) {
        if (scheduleDTO == null) {
            return null;
        }

        return findSchedule(scheduleDTO.getPartyId(), scheduleDTO.getTimeRangeId());
    }

    @Transaction
    public ScheduleEntity findOrCreateSchedule(ScheduleModel scheduleModel) {

        TimeRangeModel timeRangeModel = scheduleModel.getTimeRangeModel();
        TimeRangeEntity timeRangeEntity = this.timeRangeDao.findOrCreateEntity(timeRangeModel);

        String partyId = null;

        if (ScheduleType.BY_WEEK.equals(scheduleModel.getType())) {
            WeekScheduleEntity weekScheduleEntity = this.weekScheduleDao.findOrCreateWeekSchedule(
                                                                    (WeekScheduleModel) scheduleModel);
            partyId = weekScheduleEntity.getWeekScheduleId();

        } else if (ScheduleType.BY_DATE.equals(scheduleModel.getType())){
            MultiRangeScheduleEntity multiRangeScheduleEntity = this.multiRangeScheduleDao
                                                                 .findOrCreateMultiRangeSchedule((MultiRangeScheduleModel) scheduleModel);
            partyId = multiRangeScheduleEntity.getDateScheduleId();
        } else {
            String errMsg = String.format("Schedule type : %s not supported !!!", scheduleModel.getType());
            throw new AlertlessIllegalArgumentException(errMsg);
        }

        ScheduleDTO scheduleDTO = ScheduleDTO.builder()
                                        .partyId(partyId)
                                        .timeRangeId(timeRangeEntity.getId())
                                    .build();

        return findOrCreateEntity(scheduleDTO);
    }

    @Transaction
    public void cascadeDelete(String scheduleId) {
        ValidationUtils.validateInput(scheduleId);

        ScheduleEntity scheduleEntity = findEntity(scheduleId);
        String timeRangeId = scheduleEntity.getTimeRangeId();
        String partyId = scheduleEntity.getPartyId();

        // delete schedule entity
        delete(scheduleEntity);

        // check and delete time range
        boolean timeReferredByOther = isTimeReferredByOther(scheduleEntity);
        if (!timeReferredByOther) {
            this.timeRangeDao.delete(timeRangeId);
        }

        // check and delete party schedule
        boolean partyReferredByOther = isPartyReferredByOther(scheduleEntity);
        if (!partyReferredByOther) {
            PartyEntity partyEntity = this.partyDao.findEntity(partyId);

            if (ScheduleType.BY_WEEK.name().equalsIgnoreCase(partyEntity.getScheduleType())) {
                this.weekScheduleDao.cascadeDelete(partyId);
            } else if (ScheduleType.BY_DATE.name().equalsIgnoreCase(partyEntity.getScheduleType())) {
                this.multiRangeScheduleDao.cascadeDelete(partyId);
            } else {
                String errMsg = String.format("Schedule type : %s not supported !!!", partyEntity.getScheduleType());
                throw new AlertlessIllegalArgumentException(errMsg);
            }
        }
    }

    @Transaction
    public ScheduleModel findCompleteSchedule(String scheduleId) {
        ValidationUtils.validateInput(scheduleId);

        ScheduleModel scheduleModel = null;
        ScheduleEntity scheduleEntity = findEntity(scheduleId);

        // set time range model
        String timeRangeId = scheduleEntity.getTimeRangeId();
        TimeRangeEntity timeRangeEntity = this.timeRangeDao.findEntity(timeRangeId);
        TimeRangeModel timeRangeModel = timeRangeEntity.getModel();

        // set party schedule
        String partyId = scheduleEntity.getPartyId();
        PartyEntity partyEntity = this.partyDao.findEntity(partyId);
        ScheduleType scheduleType = ScheduleType.valueOf(partyEntity.getScheduleType());

        if (ScheduleType.BY_WEEK.equals(scheduleType)) {
            scheduleModel = this.weekScheduleDao.findWeekDaySchedule(partyId);

        } else if (ScheduleType.BY_DATE.equals(scheduleType)) {
            scheduleModel = this.multiRangeScheduleDao.findMultiRangeDaySchedule(partyId);

        } else {
            String errMsg = String.format("Schedule type : %s not supported !!!", scheduleType);
            throw new AlertlessIllegalArgumentException(errMsg);
        }

        scheduleModel.setTimeRangeModel(timeRangeModel);
        return scheduleModel;
    }

    @Transaction
    public ScheduleEntity findOrUpdateSchedule(String scheduleId, ScheduleModel requestedSchedule, boolean scheduleReferredByOther) {
        ValidationUtils.validateInput(scheduleId);
        ValidationUtils.validateInput(requestedSchedule);

        if (scheduleReferredByOther) {
            return findOrCreateSchedule(requestedSchedule);
        }

        ScheduleEntity scheduleEntity = findEntity(scheduleId);

        // update time range
        String timeRangeId = scheduleEntity.getTimeRangeId();
        TimeRangeModel requestedTimeRangeModel = requestedSchedule.getTimeRangeModel();
        boolean timeReferredByOther = isTimeReferredByOther(scheduleEntity);

        TimeRangeEntity updatedTimeRangeEntity = this.timeRangeDao.findOrUpdateEntity(timeRangeId,
                                                                     requestedTimeRangeModel, timeReferredByOther);
        String newTimeRangeId = updatedTimeRangeEntity.getId();

        // update party schedule
        String partyId = scheduleEntity.getPartyId();
        boolean partyReferredByOther = isPartyReferredByOther(scheduleEntity);

        PartyEntity partyEntity = this.partyDao.findEntity(partyId);
        ScheduleType scheduleType = ScheduleType.valueOf(partyEntity.getScheduleType());

        PartyEntity updatedPartyEntity;
        if (ScheduleType.BY_WEEK.equals(scheduleType)) {

            WeekScheduleModel requestedWeekDaySchedule = (WeekScheduleModel) requestedSchedule;
            updatedPartyEntity = this.weekScheduleDao.findOrUpdateWeekSchedule(partyId, requestedWeekDaySchedule, partyReferredByOther);

        } else if (ScheduleType.BY_DATE.equals(scheduleType)) {
            // TODO: Provide implementation
            throw new AlertlessRuntimeException("MultiRangeSchedule findOrUpdateMultiRangeSchedule() not implemented !!!");
        } else {
            String errMsg = String.format("Schedule type : %s not supported !!!", scheduleType);
            throw new AlertlessIllegalArgumentException(errMsg);
        }

        String newPartyId = updatedPartyEntity.getId();

        ScheduleDTO updateScheduleDTO = ScheduleDTO.builder()
                                                .partyId(newPartyId)
                                                .timeRangeId(newTimeRangeId)
                                            .build();

        ScheduleEntity updatedEntity = findOrUpdateEntity(scheduleId, updateScheduleDTO, scheduleReferredByOther);
        String newScheduleId = updatedEntity.getId();

        // Schedule is updated actually
        if (newScheduleId.equals(scheduleId)) {
            // delete the time range id which is not referred by any schedule
            if (!timeReferredByOther && !newTimeRangeId.equals(timeRangeId)) {
                this.timeRangeDao.delete(timeRangeId);
            }

            // delete the party id which is not referred by any schedule
            if (!partyReferredByOther && !newPartyId.equals(partyId)) {

                if (ScheduleType.BY_WEEK.equals(scheduleType)) {
                    this.weekScheduleDao.cascadeDelete(partyId);
                } else if (ScheduleType.BY_DATE.equals(scheduleType)) {
                    this.multiRangeScheduleDao.cascadeDelete(partyId);
                } else {
                    String errMsg = String.format("Schedule type : %s not supported !!!", scheduleType);
                    throw new AlertlessIllegalArgumentException(errMsg);
                }
            }
        }

        return updatedEntity;
    }

    private boolean isTimeReferredByOther(ScheduleEntity entity) {
        boolean timeReferredByOther = false;

        String timeRangeId = entity.getTimeRangeId();
        List<ScheduleEntity> scheduleWithSameTimeRange = findScheduleWithTimeRangeId(timeRangeId);

        if (scheduleWithSameTimeRange == null || scheduleWithSameTimeRange.isEmpty()) {
            return false;
        }

        if (scheduleWithSameTimeRange.size() > 1 ||
                !scheduleWithSameTimeRange.get(0).getId().equals(entity.getId())) {
            timeReferredByOther = true;
        }

        return timeReferredByOther;
    }

    private boolean isPartyReferredByOther(ScheduleEntity entity) {
        boolean partyReferredByOther = false;

        String partyId = entity.getPartyId();
        List<ScheduleEntity> scheduleWithSamePartyId = findScheduleWithPartyId(partyId);

        if (scheduleWithSamePartyId == null || scheduleWithSamePartyId.isEmpty()) {
            return false;
        }

        if (scheduleWithSamePartyId.size() > 1 ||
                !scheduleWithSamePartyId.get(0).getId().equals(entity.getId())) {
            partyReferredByOther = true;
        }

        return partyReferredByOther;
    }
}
