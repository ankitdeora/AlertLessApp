package com.example.alertless.models;

import com.example.alertless.entities.WeekScheduleEntity;
import com.example.alertless.exceptions.AlertlessIllegalArgumentException;
import com.example.alertless.utils.ValidationUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
@EqualsAndHashCode(callSuper = false)
public class WeekScheduleDTO implements BaseModel{

    private byte weekdays;
    private String dateRangeId;

    @Override
    public WeekScheduleEntity getEntity(String id) throws AlertlessIllegalArgumentException {
        ValidationUtils.validateInput(id);

        return WeekScheduleEntity.builder()
                    .weekScheduleId(id)
                    .weekdays(this.weekdays)
                    .dateRangeId(this.dateRangeId)
                .build();
    }
}
