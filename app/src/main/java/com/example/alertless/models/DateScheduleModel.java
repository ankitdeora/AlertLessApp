package com.example.alertless.models;

import com.example.alertless.commons.ScheduleType;

import java.io.Serializable;
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
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class DateScheduleModel implements Schedule {
    private List<DateRangeModel> dateRangeModels;

    @Override
    public ScheduleType getType() {
        return ScheduleType.BY_DATE;
    }
}
