package com.example.alertless.models;

import com.example.alertless.enums.ScheduleType;
import com.example.alertless.utils.TimeUtils;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

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

    @Override
    public boolean isActive() {
        Calendar currentDate = Calendar.getInstance();

        return isWeekDaysActive(currentDate, false) &&
                isDateRangeActive(currentDate, true) &&
                isTimeRangeActive(currentDate, true);
    }

    private boolean isDateRangeActive(Calendar currentDate, boolean defaultValue) {

        return Optional.ofNullable(dateRangeModel)
                .map(dateRange -> dateRange.isActive(currentDate))
                .orElse(defaultValue);
    }

    private boolean isWeekDaysActive(Calendar currentDate, boolean defaultValue) {
        MaterialDayPicker.Weekday currentWeekday = weekday(currentDate);

        return Optional.ofNullable(weekdays)
                .map(selectedDays -> selectedDays.contains(currentWeekday))
                .orElse(defaultValue);
    }

    private MaterialDayPicker.Weekday weekday(Calendar calendar) {
        return MaterialDayPicker.Weekday.Companion.get(calendar.get(Calendar.DAY_OF_WEEK) - 1);
    }
}
