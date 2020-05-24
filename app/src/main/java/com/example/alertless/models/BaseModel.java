package com.example.alertless.models;

import com.example.alertless.entities.BaseEntity;
import com.example.alertless.exceptions.AlertlessIllegalArgumentException;

public interface BaseModel {
    <T extends BaseEntity> T getEntity(String id) throws AlertlessIllegalArgumentException;
}
