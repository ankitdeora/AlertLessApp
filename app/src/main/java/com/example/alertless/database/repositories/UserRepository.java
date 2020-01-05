package com.example.alertless.database.repositories;

import android.app.Application;
import android.util.Log;

import com.example.alertless.database.AppDatabase;
import com.example.alertless.database.dao.UserDao;
import com.example.alertless.database.entities.User;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class UserRepository {
    private final AppDatabase appDatabase;
    private final UserDao userDao;

    public UserRepository(Application application) {
        appDatabase = AppDatabase.getDatabase(application);
        userDao = appDatabase.getUserDao();
    }

    public void insertUser(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.insertAll(user);
        });
    }

    public List<User> getAllUsers() throws Exception{
        Callable<List<User>> getUsersTask = new Callable<List<User>>() {
            @Override
            public List<User> call() {
                return userDao.getAll();
            }
        };

        Future<List<User>> futureUsers = AppDatabase.databaseWriteExecutor.submit(getUsersTask);

        List<User> users = null;
        try {
            users = futureUsers.get();
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e("DB_ERROR_GET_USERS", e.getMessage());
            throw e;
        }

        return users;
    }


}
