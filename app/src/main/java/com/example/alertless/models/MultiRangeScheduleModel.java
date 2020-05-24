package com.example.alertless.models;

import com.example.alertless.enums.ScheduleType;

import java.util.List;

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

    public boolean isRangeType() {
        return dateRangeModels != null && dateRangeModels.size() == 1;
    }

    public boolean isManyDaysType() {
        return dateRangeModels != null && dateRangeModels.size() > 1;
    }
}
