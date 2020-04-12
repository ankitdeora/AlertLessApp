package com.example.alertless.models;

import com.example.alertless.commons.ScheduleType;

import java.io.Serializable;

public interface Schedule extends Serializable {
    ScheduleType getType();
}
