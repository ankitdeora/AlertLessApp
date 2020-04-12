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
public class DateScheduleEntity {
    private String id;
    private String dateScheduleId;
    private String dateRangeId;
}
