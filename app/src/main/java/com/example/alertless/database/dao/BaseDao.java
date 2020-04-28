package com.example.alertless.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

import com.example.alertless.exceptions.AlertlessRuntimeException;

import java.util.List;

public abstract class BaseDao<T, M> {

    @Insert
    public abstract void insert(T t);

    @Delete
    public abstract void delete(T t);

    @Update
    public abstract void update(T t);

    public List<T> findAllEntities() {
        throw new AlertlessRuntimeException("Cannot call findAllEntities() method of BaseDao!!!");
    };

    public LiveData<List<T>> findAllLiveEntities() {
        throw new AlertlessRuntimeException("Cannot call findAllLiveEntities() method of BaseDao!!!");
    };

    public T findEntity(String id) {
        throw new AlertlessRuntimeException("Cannot call findEntity(id) method of BaseDao!!!");
    }

    public T findEntity(M m) {
        throw new AlertlessRuntimeException("Cannot call findEntity(model) method of BaseDao!!!");
    }
}
