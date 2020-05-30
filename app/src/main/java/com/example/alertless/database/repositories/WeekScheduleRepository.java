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
import com.example.alertless.entities.WeekScheduleEntity;
import com.example.alertless.entities.relations.ProfileScheduleRelation;
import com.example.alertless.exceptions.AlertlessDatabaseException;
import com.example.alertless.models.WeekScheduleDTO;
import com.example.alertless.utils.DBUtils;

import java.util.List;


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

}
