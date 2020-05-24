package com.example.alertless.database.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.alertless.database.AppDatabase;
import com.example.alertless.entities.ScheduleEntity;
import com.example.alertless.entities.relations.ProfileScheduleRelation;
import com.example.alertless.models.BaseModel;
import com.example.alertless.models.ScheduleModel;
import com.example.alertless.utils.ValidationUtils;

import java.util.List;

@Dao
public abstract class ProfileScheduleDao extends BaseDao<ProfileScheduleRelation, BaseModel> {

    private final ScheduleDao scheduleDao;

    public ProfileScheduleDao(AppDatabase appDatabase) {
        this.scheduleDao = appDatabase.getScheduleDao();
    }

    @Override
    @Query("SELECT * FROM profile_schedule")
    public abstract List<ProfileScheduleRelation> findAllEntities();

    @Override
    @Query("SELECT * FROM profile_schedule WHERE profile_id = :id")
    public abstract ProfileScheduleRelation findEntity(String id);

    @Query("SELECT * FROM profile_schedule WHERE schedule_id = :scheduleId")
    public abstract List<ProfileScheduleRelation> findProfilesWithScheduleId(String scheduleId);

    @Transaction
    public ProfileScheduleRelation createOrUpdateProfileSchedule(String profileId, ScheduleModel requestedSchedule) {
        ValidationUtils.validateInput(profileId);
        ValidationUtils.validateInput(requestedSchedule);

        ProfileScheduleRelation profileScheduleRelation = findEntity(profileId);

        ScheduleEntity scheduleEntity;
        if (profileScheduleRelation == null) {
            scheduleEntity = this.scheduleDao.findOrCreateSchedule(requestedSchedule);
            profileScheduleRelation = ProfileScheduleRelation.builder()
                                            .profileId(profileId)
                                            .scheduleId(scheduleEntity.getId())
                                        .build();
            insert(profileScheduleRelation);
        } else {
            String scheduleId = profileScheduleRelation.getScheduleId();
            boolean referredByOther = isScheduleReferredByOther(profileScheduleRelation);

            scheduleEntity = this.scheduleDao.findOrUpdateSchedule(scheduleId, requestedSchedule, referredByOther);
            String newScheduleId = scheduleEntity.getId();

            if (!scheduleId.equals(newScheduleId)) {
                // update profile schedule
                profileScheduleRelation.setScheduleId(newScheduleId);
                update(profileScheduleRelation);

                if (!referredByOther) {
                    this.scheduleDao.cascadeDelete(scheduleId);
                }
            }
        }

        return profileScheduleRelation;
    }

    @Transaction
    public void cascadeDelete(String profileId) {
        ValidationUtils.validateInput(profileId);
        ProfileScheduleRelation relation = findEntity(profileId);

        if (relation == null) {
            return;
        }

        // delete relation
        delete(relation);

        boolean scheduleReferredByOther = isScheduleReferredByOther(relation);
        if (!scheduleReferredByOther) {
            // cascade delete schedule
            String scheduleId = relation.getScheduleId();
            this.scheduleDao.cascadeDelete(scheduleId);
        }
    }

    @Transaction
    public ScheduleModel findCompleteSchedule(String profileId) {
        ValidationUtils.validateInput(profileId);

        ProfileScheduleRelation relation = findEntity(profileId);

        if (relation == null) {
            return null;
        }

        String scheduleId = relation.getScheduleId();
        return this.scheduleDao.findCompleteSchedule(scheduleId);
    }

    private boolean isScheduleReferredByOther(ProfileScheduleRelation relation) {
        boolean referredByOther = false;
        List<ProfileScheduleRelation> profilesWithSchedule = findProfilesWithScheduleId(relation.getScheduleId());

        if (profilesWithSchedule == null || profilesWithSchedule.isEmpty()) {
            return false;
        }

        if (profilesWithSchedule.size() > 1 ||
                !profilesWithSchedule.get(0).getProfileId().equals(relation.getProfileId())) {
            referredByOther = true;
        }
        return referredByOther;
    }

}
