package com.example.alertless.models;

import com.example.alertless.entities.ScheduleEntity;
import com.example.alertless.exceptions.AlertlessIllegalArgumentException;
import com.example.alertless.utils.ValidationUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
@EqualsAndHashCode(callSuper = false)
public class ScheduleDTO implements BaseModel {

    private String partyId;
    private String timeRangeId;

    @Override
    public ScheduleEntity getEntity(String id) throws AlertlessIllegalArgumentException {
        ValidationUtils.validateInput(id);

        return ScheduleEntity.builder()
                    .id(id)
                    .partyId(this.partyId)
                    .timeRangeId(this.timeRangeId)
                .build();
    }
}
