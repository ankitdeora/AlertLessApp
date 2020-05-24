package com.example.alertless.database.repositories;

import com.example.alertless.exceptions.AlertlessDatabaseException;
import com.example.alertless.exceptions.AlertlessException;

import java.util.List;

public interface IRepository<T, M> {
    List<T> getAllEntities() throws AlertlessException;
    T getEntity(String id) throws AlertlessException;
    T getEntity(M model) throws AlertlessException;
    T createEntity(M model) throws AlertlessException;
    void updateEntity(String id, M model) throws AlertlessException;
    void deleteEntity(String id) throws AlertlessException;
    void deleteEntity(M model) throws AlertlessException;
}
