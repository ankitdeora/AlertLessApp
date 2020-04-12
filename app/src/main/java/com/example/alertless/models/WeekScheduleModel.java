package com.example.alertless.models;

import com.example.alertless.commons.ScheduleType;

import java.io.Serializable;
import java.util.List;

import ca.antonious.materialdaypicker.MaterialDayPicker;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class WeekScheduleModel implements Schedule {
    private List<MaterialDayPicker.Weekday> weekdays;
    private DateRangeModel dateRangeModel;

    @Override
    public ScheduleType getType() {
        return ScheduleType.BY_WEEK;
    }
}
