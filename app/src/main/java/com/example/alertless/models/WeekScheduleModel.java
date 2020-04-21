package com.example.alertless.models;

import com.example.alertless.commons.ScheduleType;

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
@ToString (callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode (callSuper = true)
public class WeekScheduleModel extends ScheduleModel {
    private List<MaterialDayPicker.Weekday> weekdays;
    private DateRangeModel dateRangeModel;

    @Override
    public ScheduleType getType() {
        return ScheduleType.BY_WEEK;
    }
}
