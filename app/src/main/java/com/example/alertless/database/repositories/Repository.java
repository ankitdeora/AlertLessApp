package com.example.alertless.database.repositories;

import android.app.Application;

import com.example.alertless.database.AppDatabase;

public abstract class Repository {
    protected final AppDatabase appDatabase;

    protected Repository(Application application) {
        appDatabase = AppDatabase.getDatabase(application);
    }
}