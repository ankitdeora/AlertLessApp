package com.example.alertless.models;

import com.example.alertless.entities.TimeRangeEntity;
import com.example.alertless.exceptions.AlertlessIllegalArgumentException;
import com.example.alertless.utils.TimeUtils;
import com.example.alertless.utils.ValidationUtils;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Optional;

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
@EqualsAndHashCode (callSuper = false)
public class TimeRangeModel implements BaseModel, Serializable {
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

    public boolean isActive(Calendar currentDate) {

        int hour = currentDate.get(Calendar.HOUR_OF_DAY);
        int minute = currentDate.get(Calendar.MINUTE);
        int minutesElapsed = TimeUtils.getMinutes(hour, minute);

        return minutesElapsed >= startMin && minutesElapsed <= endMin;
    }
}
