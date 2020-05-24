package com.example.alertless.models;

import com.example.alertless.entities.MultiRangeScheduleEntity;
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
public class MultiRangeScheduleDTO implements BaseModel {

    private String dateScheduleId;
    private String dateRangeId;

    @Override
    public MultiRangeScheduleEntity getEntity(String id) throws AlertlessIllegalArgumentException {
        ValidationUtils.validateInput(id);

        return MultiRangeScheduleEntity.builder()
                    .id(id)
                    .dateScheduleId(this.dateScheduleId)
                    .dateRangeId(this.dateRangeId)
                .build();
    }
}
