package com.example.alertless.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class WeekSchedule extends Schedule {
    private byte weekdays;

    public WeekSchedule(String id, long startDateMs, long endDateMs, String dailyTimeRangeId, byte weekdays) {
        super(id, startDateMs, endDateMs, dailyTimeRangeId);
        this.weekdays = weekdays;
    }
}
