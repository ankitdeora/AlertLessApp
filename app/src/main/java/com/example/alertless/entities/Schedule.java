package com.example.alertless.entities;

import java.util.Calendar;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Schedule {
    private String id;
    private long startDateMs;
    private long endDateMs;
    private String dailyTimeRangeId;
}
