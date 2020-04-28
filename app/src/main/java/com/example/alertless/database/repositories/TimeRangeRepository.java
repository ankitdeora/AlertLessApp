package com.example.alertless.database.repositories;

import android.app.Application;

import com.example.alertless.entities.TimeRangeEntity;
import com.example.alertless.models.TimeRangeModel;

public class TimeRangeRepository extends BaseRepository<TimeRangeEntity, TimeRangeModel> {

    private static volatile TimeRangeRepository INSTANCE;

    private TimeRangeRepository(Application application) {
        super(application);
        this.dao = appDatabase.getTimeRangeDao();
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

}
