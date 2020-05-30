package com.example.alertless.database.dao;

import android.util.Log;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Transaction;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.example.alertless.database.AppDatabase;
import com.example.alertless.entities.DateRangeEntity;
import com.example.alertless.entities.MultiRangeScheduleEntity;
import com.example.alertless.entities.PartyEntity;
import com.example.alertless.enums.ScheduleType;
import com.example.alertless.models.DateRangeModel;
import com.example.alertless.models.MultiRangeScheduleDTO;
import com.example.alertless.models.MultiRangeScheduleModel;
import com.example.alertless.utils.StringUtils;
import com.example.alertless.utils.ValidationUtils;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.alertless.utils.StringUtils.getUniqueId;

@Dao
public abstract class MultiRangeScheduleDao extends BaseDao<MultiRangeScheduleEntity, MultiRangeScheduleDTO> {

    private final DateRangeDao dateRangeDao;
    private final PartyDao partyDao;
    private final CommonScheduleDao commonScheduleDao;

    public MultiRangeScheduleDao(AppDatabase appDatabase) {
        this.dateRangeDao = appDatabase.getDateRangeDao();
        this.partyDao = appDatabase.getPartyDao();
        this.commonScheduleDao = appDatabase.getCommonScheduleDao();
    }

    @Override
    @Query("SELECT * FROM multi_range_schedule")
    public abstract List<MultiRangeScheduleEntity> findAllEntities();

    @Override
    @Query("SELECT * FROM multi_range_schedule WHERE id = :id")
    public abstract MultiRangeScheduleEntity findEntity(String id);

    @Query("SELECT * FROM multi_range_schedule WHERE date_schedule_id = :dateScheduleId AND date_range_id = :dateRangeId")
    public abstract MultiRangeScheduleEntity findMultiRangeSchedule(String dateScheduleId, String dateRangeId);

    @Query("SELECT * FROM multi_range_schedule WHERE date_schedule_id = :dateScheduleId")
    public abstract List<MultiRangeScheduleEntity> findMultiRangeSchedules(String dateScheduleId);

    @Query("SELECT date_range_id FROM multi_range_schedule WHERE date_schedule_id = :dateScheduleId")
    public abstract List<String> findDateRangeIds(String dateScheduleId);

    @Query("DELETE FROM multi_range_schedule WHERE date_schedule_id = :dateScheduleId")
    public abstract void deleteForScheduleId(String dateScheduleId);

    @RawQuery
    protected abstract String findScheduleForDateRangesRawQuery(SupportSQLiteQuery query);

    @Transaction
    public String findCommonScheduleIdForDateRanges(List<String> dateRangeIds) {
        ValidationUtils.validateInput(dateRangeIds);

        int len = dateRangeIds.size();
        String query = "select date_schedule_id " +
                "from multi_range_schedule " +
                "where date_schedule_id in " +
                "(select date_schedule_id " +
                "from multi_range_schedule where date_range_id in (%s) " +
                "group by date_schedule_id " +
                "having count(distinct date_range_id) = %s) group by date_schedule_id having count (distinct date_range_id) = %s";

        String dateRangesWithComma = StringUtils.addQuotesToList(dateRangeIds);
        query = String.format(query, dateRangesWithComma, len, len);
        SimpleSQLiteQuery simpleSQLiteQuery = new SimpleSQLiteQuery(query);
        return findScheduleForDateRangesRawQuery(simpleSQLiteQuery);
    }



    @RawQuery
    protected abstract String executeRawQueryDao(SupportSQLiteQuery query);

    public Object executeRawQuery(String query) {
        SimpleSQLiteQuery simpleSQLiteQuery = new SimpleSQLiteQuery(query);
        return executeRawQueryDao(simpleSQLiteQuery);
    }

    @Override
    public MultiRangeScheduleEntity findEntity(MultiRangeScheduleDTO multiRangeScheduleDTO) {
        if (multiRangeScheduleDTO == null) {
            return null;
        }

        return findMultiRangeSchedule(multiRangeScheduleDTO.getDateScheduleId(), multiRangeScheduleDTO.getDateRangeId());
    }

    @Transaction
    public MultiRangeScheduleEntity findOrCreateMultiRangeSchedule(MultiRangeScheduleModel multiRangeScheduleModel) {
        ValidationUtils.validateInput(multiRangeScheduleModel);

        List<String> dateRangeIds = findOrCreateDateRangeIdsForMultiRangeSchedule(multiRangeScheduleModel);
        String existingDateScheduleId = findCommonScheduleIdForDateRanges(dateRangeIds);

        if (existingDateScheduleId != null) {
            return findMultiRangeSchedule(existingDateScheduleId, dateRangeIds.get(0));

        } else {

            String partyId = getUniqueId();
            PartyEntity partyEntity = PartyEntity.builder()
                    .id(partyId)
                    .scheduleType(ScheduleType.BY_DATE.name())
                    .build();

            this.partyDao.insert(partyEntity);

            dateRangeIds.forEach(dateRangeId -> {

                MultiRangeScheduleEntity entity = MultiRangeScheduleEntity.builder()
                        .id(getUniqueId())
                        .dateScheduleId(partyId)
                        .dateRangeId(dateRangeId)
                        .build();

                insert(entity);
            });

            return findMultiRangeSchedule(partyId, dateRangeIds.get(0));
        }
    }

    @Transaction
    public PartyEntity findOrUpdateMultiRangeSchedule(String dateScheduleId,
                                                                        MultiRangeScheduleModel requestedMultiRangeSchedule,
                                                                        boolean multiScheduleReferredByOther) {
        ValidationUtils.validateInput(dateScheduleId);
        ValidationUtils.validateInput(requestedMultiRangeSchedule);

        if (multiScheduleReferredByOther) {
            MultiRangeScheduleEntity multiRangeScheduleEntity = findOrCreateMultiRangeSchedule(requestedMultiRangeSchedule);
            return this.partyDao.findEntity(multiRangeScheduleEntity.getDateScheduleId());
        }

        List<String> newDateRangeIds = findOrCreateDateRangeIdsForMultiRangeSchedule(requestedMultiRangeSchedule);
        String existingDateScheduleId = findCommonScheduleIdForDateRanges(newDateRangeIds);

        // if schedule already exist return
        if (existingDateScheduleId != null) {
            return this.partyDao.findEntity(existingDateScheduleId);
        }

        // delete existing multi range entities
        cascadeDelete(dateScheduleId, false);

        // add new multi range entities
        newDateRangeIds.forEach(newDateRangeId -> {
            MultiRangeScheduleEntity entity = MultiRangeScheduleEntity.builder()
                                                    .id(getUniqueId())
                                                    .dateScheduleId(dateScheduleId)
                                                    .dateRangeId(newDateRangeId)
                                                .build();
            insert(entity);
        });

        return this.partyDao.findEntity(dateScheduleId);
    }

    public static <T> boolean listEqualsIgnoreOrder(List<T> listA, List<T> listB) {
        return new HashSet<>(listA).equals(new HashSet<>(listB));
    }

    @Transaction
    public List<String> findOrCreateDateRangeIdsForMultiRangeSchedule(MultiRangeScheduleModel multiRangeSchedule) {

        return multiRangeSchedule.getDateRangeModels().stream()
                .map(this.dateRangeDao::findOrCreateEntity)
                .map(DateRangeEntity::getId)
                .collect(Collectors.toList());
    }

    @Transaction
    public void cascadeDelete(String dateScheduleId) {
        cascadeDelete(dateScheduleId, true);
    }

    @Transaction
    protected void cascadeDelete(String dateScheduleId, boolean deleteParty) {
        ValidationUtils.validateInput(dateScheduleId);

        List<MultiRangeScheduleEntity> existingScheduleEntities = findMultiRangeSchedules(dateScheduleId);

        // delete existing multi range entities
        existingScheduleEntities.forEach(entity -> {
            String dateRangeId = entity.getDateRangeId();
            boolean dateReferredByOther = this.commonScheduleDao.isDateReferredByOther(entity, dateRangeId);

            delete(entity);

            // delete date range if not referred
            if (!dateReferredByOther) {
                this.dateRangeDao.delete(dateRangeId);
            }
        });

        if (deleteParty) {
            // delete party
            this.partyDao.delete(dateScheduleId);
        }

    }

    @Transaction
    public MultiRangeScheduleModel findMultiRangeDaySchedule(String dateScheduleId) {

        List<String> dateRangeIds = findDateRangeIds(dateScheduleId);

        List<DateRangeModel> dateRangeModels = dateRangeIds.stream()
                .map(this.dateRangeDao::findEntity)
                .map(DateRangeEntity::getModel)
                .collect(Collectors.toList());

        return MultiRangeScheduleModel.builder()
                    .dateRangeModels(dateRangeModels)
                .build();
    }
}
