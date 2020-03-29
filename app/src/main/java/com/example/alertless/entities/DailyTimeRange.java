package com.example.alertless.entities;

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
public class DailyTimeRange {
    private String id;
    private int dailyStartMin;
    private int dailyEndMin;
}
