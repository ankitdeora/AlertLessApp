package com.example.alertless.models;

import com.example.alertless.entities.BaseEntity;
import com.example.alertless.entities.DateRangeEntity;
import com.example.alertless.exceptions.AlertlessIllegalArgumentException;
import com.example.alertless.utils.ValidationUtils;

import java.io.Serializable;
import java.util.Calendar;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@SuperBuilder
public class DateRangeModel implements BaseModel, Serializable {
    private long startDateMs;
    private long endDateMs;

    @Override
    public DateRangeEntity getEntity(String id) throws AlertlessIllegalArgumentException {
        ValidationUtils.validateInput(id);

        return DateRangeEntity.builder()
                    .id(id)
                    .startDateMs(this.startDateMs)
                    .endDateMs(this.endDateMs)
                .build();
    }

    public boolean isActive(Calendar currentDate) {
        final long currentTimeMs = currentDate.getTimeInMillis();
        return currentTimeMs >= startDateMs && currentTimeMs <= endDateMs;
    }
}
