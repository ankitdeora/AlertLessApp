package com.example.alertless.models;

import com.example.alertless.entities.TimeRangeEntity;
import com.example.alertless.exceptions.AlertlessIllegalArgumentException;
import com.example.alertless.utils.ValidationUtils;

import java.io.Serializable;

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
@EqualsAndHashCode
public class TimeRangeModel extends BaseModel implements Serializable {
    private int startMin;
    private int endMin;

    @Override
    public TimeRangeEntity getEntity(String id) throws AlertlessIllegalArgumentException {
        ValidationUtils.validateInput(id);

        return TimeRangeEntity.builder()
                    .id(id)
                    .startMin(this.startMin)
                    .endMin(this.endMin)
                .build();
    }
}
