package com.example.alertless.entities;

import com.example.alertless.models.BaseModel;

public interface BaseEntity extends Identity {
    <T extends BaseModel> T getModel();
}
