package com.example.alertless.entities;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class WeekScheduleEntity {
    private String id;
    private byte weekdays;
    private String dateRangeId;

}
