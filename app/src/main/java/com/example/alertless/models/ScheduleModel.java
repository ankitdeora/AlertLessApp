package com.example.alertless.models;

import com.example.alertless.enums.ScheduleType;
import com.example.alertless.utils.TimeUtils;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Optional;

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
    public abstract boolean isActive();

    public boolean isWeekType() {
        return ScheduleType.BY_WEEK.equals(getType());
    }

    public boolean isDateType() {
        return ScheduleType.BY_DATE.equals(getType());
    }

    protected boolean isTimeRangeActive(Calendar currentDate, boolean defaultValue) {

        return Optional.ofNullable(this.getTimeRangeModel())
                         .map(timeRange -> timeRange.isActive(currentDate))
                         .orElse(defaultValue);
    }

}
