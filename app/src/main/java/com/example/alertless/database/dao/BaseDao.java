package com.example.alertless.database.dao;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.alertless.entities.Identity;
import com.example.alertless.exceptions.AlertlessIllegalArgumentException;
import com.example.alertless.exceptions.AlertlessRuntimeException;
import com.example.alertless.models.BaseModel;
import com.example.alertless.utils.Constants;
import com.example.alertless.utils.StringUtils;
import com.example.alertless.utils.ValidationUtils;

import java.util.List;

public abstract class BaseDao<T extends Identity, M extends BaseModel> {

    private static final String TAG = BaseDao.class.getName() + Constants.TAG_SUFFIX;

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

    public void delete(String id) {
        throw new AlertlessRuntimeException("Cannot call delete(id) method of BaseDao!!!");
    }

    public T findOrCreateEntity(M m) throws AlertlessIllegalArgumentException {
        T entity = findEntity(m);

        if (entity != null) {
            return entity;
        }

        entity = (T)m.getEntity(StringUtils.getUniqueId());
        insert(entity);

        return entity;
    }

    @Transaction
    public T findOrUpdateEntity(String id, M updatedModel, boolean referredByOther) {
        ValidationUtils.validateInput(id);
        ValidationUtils.validateInput(updatedModel);

        if (referredByOther) {
            return findOrCreateEntity(updatedModel);
        }

        T updatedEntity = null;
        T existingEntityWithSameModel = findEntity(updatedModel);

        if (existingEntityWithSameModel == null) {

            updatedEntity = updatedModel.getEntity(id);
            update(updatedEntity);

        } else {
            updatedEntity = existingEntityWithSameModel;
        }

        return updatedEntity;
    }
}
