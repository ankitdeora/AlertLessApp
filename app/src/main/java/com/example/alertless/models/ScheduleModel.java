package com.example.alertless.models;

import com.example.alertless.enums.ScheduleType;

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

    public boolean isWeekType() {
        return ScheduleType.BY_WEEK.equals(getType());
    }

    public boolean isDateType() {
        return ScheduleType.BY_DATE.equals(getType());
    }
}
