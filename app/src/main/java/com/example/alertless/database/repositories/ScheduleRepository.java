package com.example.alertless.database.repositories;

import android.app.Application;

import com.example.alertless.entities.ScheduleEntity;
import com.example.alertless.models.ScheduleDTO;


public class ScheduleRepository extends BaseRepository<ScheduleEntity, ScheduleDTO> {
    private static volatile ScheduleRepository INSTANCE;

    private ScheduleRepository(Application application) {
        super(application);
        this.dao = appDatabase.getScheduleDao();
    }

    public static ScheduleRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ScheduleRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ScheduleRepository(application);
                }
            }
        }

        return INSTANCE;
    }
}
