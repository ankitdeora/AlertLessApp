package com.example.alertless.database.repositories;

import android.app.Application;
import android.util.Log;

import com.example.alertless.database.dao.MultiRangeScheduleDao;
import com.example.alertless.database.dao.PartyDao;
import com.example.alertless.database.dao.ProfileScheduleDao;
import com.example.alertless.database.dao.ScheduleDao;
import com.example.alertless.database.dao.WeekScheduleDao;
import com.example.alertless.entities.MultiRangeScheduleEntity;
import com.example.alertless.entities.PartyEntity;
import com.example.alertless.entities.ScheduleEntity;
import com.example.alertless.entities.WeekScheduleEntity;
import com.example.alertless.entities.relations.ProfileScheduleRelation;
import com.example.alertless.exceptions.AlertlessDatabaseException;
import com.example.alertless.exceptions.AlertlessException;
import com.example.alertless.models.ScheduleModel;
import com.example.alertless.models.WeekScheduleDTO;
import com.example.alertless.models.WeekScheduleModel;
import com.example.alertless.utils.DBUtils;
import com.example.alertless.utils.ValidationUtils;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;


public class WeekScheduleRepository extends BaseRepository<WeekScheduleEntity, WeekScheduleDTO> {
    //TODO: Null checks are not in place in this class, NPEs might happen

    private static volatile WeekScheduleRepository INSTANCE;

    private final PartyDao partyDao;
    private final ScheduleDao scheduleDao;
    private final ProfileScheduleDao profileScheduleDao;
    private final WeekScheduleDao weekScheduleDao;
    private final MultiRangeScheduleDao multiRangeScheduleDao;
    private final ScheduleRepository scheduleRepository;
    private final TimeRangeRepository timeRangeRepository;
    private final DateRangeRepository dateRangeRepository;
    private final ProfileDetailsRepository profileDetailsRepository;

    private WeekScheduleRepository(Application application) {
        super(application);

        this.dao = appDatabase.getWeekScheduleDao();
        this.scheduleDao = appDatabase.getScheduleDao();
        this.profileScheduleDao = appDatabase.getProfileScheduleDao();
        this.weekScheduleDao = (WeekScheduleDao) this.dao;
        this.multiRangeScheduleDao = appDatabase.getMultiRangeScheduleDao();
        this.partyDao = appDatabase.getPartyDao();

        this.scheduleRepository = ScheduleRepository.getInstance(application);
        this.timeRangeRepository = TimeRangeRepository.getInstance(application);
        this.dateRangeRepository = DateRangeRepository.getInstance(application);
        this.profileDetailsRepository = ProfileDetailsRepository.getInstance(application);
    }

    public static WeekScheduleRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (WeekScheduleRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WeekScheduleRepository(application);
                }
            }
        }

        return INSTANCE;
    }

    public Object executeRawQuery(String query) throws AlertlessDatabaseException {
        String errMsg = String.format("error executing raw query : %s", query);
        return DBUtils.executeTaskAndGet(this.multiRangeScheduleDao::executeRawQuery, query, errMsg);
    }

    public void listAllEntities() throws AlertlessDatabaseException {
        Log.i("PRINTING-ALL-DATA", "*************** PRINTING-ALL-DATA ***************");

        Log.i("PRINTING-PROFILE-DATA", String.format("Size : %s | %s",this.profileDetailsRepository.getAllEntities().size(),
                this.profileDetailsRepository.getAllEntities().toString()));

        List<ProfileScheduleRelation> profileScheduleRelations = DBUtils.executeTaskAndGet(this.profileScheduleDao::findAllEntities, "Could not get all profileSchedules !!!");

        Log.i("PRINTING-PROFILE-SCHEDULE-DATA", String.format("Size : %s | %s",profileScheduleRelations.size(),
                profileScheduleRelations.toString()));

        Log.i("PRINTING-SCHEDULE-DATA", String.format("Size : %s | %s",this.scheduleRepository.getAllEntities().size(),
                this.scheduleRepository.getAllEntities().toString()));

        Log.i("PRINTING-TIME-DATA", String.format("Size : %s | %s",this.timeRangeRepository.getAllEntities().size(),
                                                                        this.timeRangeRepository.getAllEntities().toString()));

        List<PartyEntity> partyEntities = DBUtils.executeTaskAndGet(this.partyDao::findAllEntities, "Could not get all parties !!!");

        Log.i("PRINTING-PARTY-DATA", String.format("Size : %s | %s",partyEntities.size(),
                partyEntities.toString()));

        Log.i("PRINTING-WEEK-DATA", String.format("Size : %s | %s",this.getAllEntities().size(),
                this.getAllEntities().toString()));

        Log.i("PRINTING-DATE-DATA", String.format("Size : %s | %s",this.dateRangeRepository.getAllEntities().size(),
                                                                        this.dateRangeRepository.getAllEntities().toString()));

        List<MultiRangeScheduleEntity> multiScheduleEntities = DBUtils.executeTaskAndGet(this.multiRangeScheduleDao::findAllEntities,
                                            "Could not get all multiRangeSchedules !!!");

        Log.i("PRINTING-Multi-SCHEDULE-DATA", String.format("Size : %s | %s",multiScheduleEntities.size(),
                multiScheduleEntities.toString()));


    }

    public ScheduleEntity getOrCreateSchedule(WeekScheduleModel weekScheduleModel) throws AlertlessException, ExecutionException, InterruptedException {
        ValidationUtils.validateInput(weekScheduleModel);

        Callable<ScheduleEntity> callable = new Callable<ScheduleEntity>() {
            @Override
            public ScheduleEntity call() throws Exception {
                /*
                TimeRangeModel timeRangeModel = weekScheduleModel.getTimeRangeModel();
                TimeRangeEntity timeRangeEntity = timeRangeRepository.getOrCreateEntity(timeRangeModel);
                String timeRangeId = timeRangeEntity.getId();

                DateRangeModel dateRangeModel = weekScheduleModel.getDateRangeModel();
                DateRangeEntity dateRangeEntity = dateRangeRepository.getOrCreateEntity(dateRangeModel);
                String dateRangeId = dateRangeEntity.getId();

                List<MaterialDayPicker.Weekday> weekdays = weekScheduleModel.getWeekdays();
                byte weekByte = WeekUtils.getByte(weekdays);

                WeekScheduleDTO weekScheduleDTO = WeekScheduleDTO.builder()
                        .weekdays(weekByte)
                        .dateRangeId(dateRangeId)
                        .build();

                WeekScheduleEntity weekScheduleEntity = getEntity(weekScheduleDTO);
                String weekScheduleId = null;

                if (weekScheduleEntity != null) {
                    weekScheduleId = weekScheduleEntity.getWeekScheduleId();
                } else {
                    weekScheduleId = getUniqueId();

                    PartyEntity partyEntity = PartyEntity.builder()
                            .id(weekScheduleId)
                            .scheduleType(ScheduleType.BY_WEEK.name())
                            .build();
                    String errMsg = String.format("Could not insert Party : %s", partyEntity);
                    DBUtils.executeTask(partyDao::insert, partyEntity, errMsg);

                    weekScheduleEntity = weekScheduleDTO.getEntity(weekScheduleId);
                    DBUtils.executeTask(weekScheduleDao::insert, weekScheduleEntity, "Could not insert week schedule");
                }

                ScheduleDTO scheduleDTO = ScheduleDTO.builder()
                        .partyId(weekScheduleId)
                        .timeRangeId(timeRangeId)
                        .build();
                return scheduleRepository.getOrCreateEntity(scheduleDTO);

                 */

                /*
                List<MaterialDayPicker.Weekday> weekdays = weekScheduleModel.getWeekdays();
                byte weekByte = WeekUtils.getByte(weekdays);

                String dateRangeId = getUniqueId();
                DateRangeEntity dateRangeEntity = DateRangeEntity.builder()
                                                    .id(dateRangeId)
                                                    .startDateMs(new Date().getTime())
                                                    .endDateMs(new Date().getTime())
                                                .build();
                appDatabase.getDateRangeDao().insert(dateRangeEntity);

                WeekScheduleDTO weekScheduleDTO = WeekScheduleDTO.builder()
                        .weekdays(weekByte)
                        .dateRangeId(dateRangeId)
                        .build();

                String weekScheduleId = getUniqueId();

                PartyEntity partyEntity = PartyEntity.builder()
                        .id(weekScheduleId)
                        .scheduleType(ScheduleType.BY_WEEK.name())
                        .build();

                partyDao.insert(partyEntity);

                WeekScheduleEntity weekScheduleEntity = weekScheduleDTO.getEntity(weekScheduleId);
                weekScheduleDao.insert(weekScheduleEntity);

                return ScheduleEntity.builder()
                        .id(weekScheduleId)
                        .timeRangeId(weekScheduleId)
                        .partyId(weekScheduleId)
                        .build();

                 */


                ScheduleEntity scheduleEntity = null;
                try {
                    scheduleEntity = scheduleDao.findEntity("VU8adfKJyXrNh8D");
                    ScheduleModel scheduleModel = scheduleDao.findCompleteSchedule("VU8adfKJyXrNh8D");
                    Log.i("-------COMPLETE-SCHEDULE--------", scheduleModel.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return scheduleEntity;
            }
        };

        return DBUtils.executeTaskAndGet(appDatabase::runInTransaction, callable, "could not execute transaction");
    }

}
