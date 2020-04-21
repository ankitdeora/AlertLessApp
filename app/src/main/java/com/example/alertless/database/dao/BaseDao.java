package com.example.alertless.database.dao;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

public interface BaseDao<T> {
    @Insert
    void insert(T... t);

    @Delete
    void delete(T t);

    @Update
    void update(T t);
}
