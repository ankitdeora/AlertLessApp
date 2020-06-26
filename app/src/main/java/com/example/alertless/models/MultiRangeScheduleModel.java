package com.example.alertless.models;

import com.example.alertless.enums.ScheduleType;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;
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
@ToString (callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode (callSuper = true)
public class MultiRangeScheduleModel extends ScheduleModel {
    private List<DateRangeModel> dateRangeModels;

    @Override
    public ScheduleType getType() {
        return ScheduleType.BY_DATE;
    }

    @Override
    public boolean isActive() {
        Calendar currentDate = Calendar.getInstance();
        return isDateRangesActive(currentDate) && isTimeRangeActive(currentDate, true);
    }

    private boolean isDateRangesActive(Calendar currentDate) {

        if (dateRangeModels == null) {
            return false;
        }

        for (DateRangeModel dateRange : dateRangeModels) {
            if (dateRange != null && dateRange.isActive(currentDate)) {
                return true;
            }
        }

        return false;
    }

    public boolean isRangeType() {
        return dateRangeModels != null && dateRangeModels.size() == 1;
    }

    public boolean isManyDaysType() {
        return dateRangeModels != null && dateRangeModels.size() > 1;
    }
}
