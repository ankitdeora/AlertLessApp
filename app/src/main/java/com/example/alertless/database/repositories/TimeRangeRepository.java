package com.example.alertless.database.repositories;

import android.app.Application;

import com.example.alertless.database.dao.TimeRangeDao;
import com.example.alertless.entities.TimeRangeEntity;
import com.example.alertless.exceptions.AlertlessDatabaseException;
import com.example.alertless.models.TimeRangeModel;
import com.example.alertless.utils.DBUtils;

import java.util.Date;
import java.util.List;

public class TimeRangeRepository extends Repository {

    private static volatile TimeRangeRepository INSTANCE;
    private final TimeRangeDao timeRangeDao;

    private TimeRangeRepository(Application application) {
        super(application);
        timeRangeDao = appDatabase.getTimeRangeDao();
    }

    public static TimeRangeRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (TimeRangeRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TimeRangeRepository(application);
                }
            }
        }

        return INSTANCE;
    }

    public List<TimeRangeEntity> getAllTimeRangeEntities() throws AlertlessDatabaseException {
        return DBUtils.executeTaskAndGet(timeRangeDao::findAllTimeRanges, "Could not get all TimeRanges !!!");
    }

    public TimeRangeEntity getTimeRangeEntity(TimeRangeModel model) throws AlertlessDatabaseException {
        return DBUtils.executeTaskAndGet(timeRangeDao::findRange, model,"Could not get TimeRange model !!!");
    }

    public void insertTimeRange(TimeRangeModel model) throws AlertlessDatabaseException {
        TimeRangeEntity entity = TimeRangeEntity.getEntity(model, String.valueOf(new Date().getTime()));
        DBUtils.executeTask(timeRangeDao::insert, entity, "Error caught while inserting Time Range!!!");
    }
}
