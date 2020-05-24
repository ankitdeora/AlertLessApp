package com.example.alertless.enums;

import com.applandeo.materialcalendarview.CalendarView;

public enum DatePickerType {
    CLASSIC (CalendarView.CLASSIC),
    ONE_DAY (CalendarView.ONE_DAY_PICKER),
    MANY_DAYS (CalendarView.MANY_DAYS_PICKER),
    RANGE (CalendarView.RANGE_PICKER);

    private int value;

    DatePickerType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
