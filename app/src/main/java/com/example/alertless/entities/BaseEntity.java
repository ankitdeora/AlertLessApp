package com.example.alertless.entities;

import com.example.alertless.models.BaseModel;

public abstract class BaseEntity {
    public abstract String getId();
    public abstract <T extends BaseModel> T getModel();
}
