package com.example.alertless.entities;

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
@EqualsAndHashCode
@Builder
public class DateRangeEntity {
    private String id;
    private long startDateMs;
    private long endDateMs;
    private String timeRangeId;
}
