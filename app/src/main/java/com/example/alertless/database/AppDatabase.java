package com.example.alertless.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.alertless.database.dao.ProfileDao;
import com.example.alertless.database.dao.TimeRangeDao;
import com.example.alertless.entities.AppDetailsEntity;
import com.example.alertless.entities.DateRangeEntity;
import com.example.alertless.entities.MultiRangeScheduleEntity;
import com.example.alertless.entities.ProfileDetailsEntity;
import com.example.alertless.entities.ScheduleEntity;
import com.example.alertless.entities.TimeRangeEntity;
import com.example.alertless.entities.WeekScheduleEntity;
import com.example.alertless.entities.relations.ProfileAppRelation;
import com.example.alertless.entities.relations.ProfileScheduleRelation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Database(entities = {
                ProfileDetailsEntity.class,
                AppDetailsEntity.class,
                TimeRangeEntity.class,
                DateRangeEntity.class,
                ScheduleEntity.class,
                WeekScheduleEntity.class,
                MultiRangeScheduleEntity.class,
                ProfileScheduleRelation.class,
                ProfileAppRelation.class
        },
        version = 1,
        exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public static final String ALERTLESS_DB_NAME = "alertless-db";

    public abstract ProfileDao getProfileDao();
    public abstract TimeRangeDao getTimeRangeDao();

    private static volatile AppDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, ALERTLESS_DB_NAME)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
