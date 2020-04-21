package com.example.alertless.models;

import com.example.alertless.commons.ScheduleType;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@SuperBuilder
public abstract class ScheduleModel implements Serializable {

    private TimeRangeModel timeRangeModel;
    public abstract ScheduleType getType();
}
