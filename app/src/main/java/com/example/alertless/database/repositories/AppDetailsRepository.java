package com.example.alertless.database.repositories;

import android.app.Application;

import com.example.alertless.entities.AppDetailsEntity;
import com.example.alertless.models.AppDetailsModel;

public class AppDetailsRepository extends BaseRepository<AppDetailsEntity, AppDetailsModel> {
    private static volatile AppDetailsRepository INSTANCE;

    private AppDetailsRepository(Application application) {
        super(application);
        this.dao = appDatabase.getAppDetailsDao();
    }

    public static AppDetailsRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (AppDetailsRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppDetailsRepository(application);
                }
            }
        }

        return INSTANCE;
    }
}
