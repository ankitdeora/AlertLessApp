package com.example.alertless.database.repositories;

import android.app.Application;

import com.example.alertless.entities.DateRangeEntity;
import com.example.alertless.models.DateRangeModel;

public class DateRangeRepository extends BaseRepository<DateRangeEntity, DateRangeModel> {

    private static volatile DateRangeRepository INSTANCE;

    private DateRangeRepository(Application application) {
        super(application);
        this.dao = appDatabase.getDateRangeDao();
    }

    public static DateRangeRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (DateRangeRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DateRangeRepository(application);
                }
            }
        }

        return INSTANCE;
    }
}
