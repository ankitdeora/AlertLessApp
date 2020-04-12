package com.example.alertless.utils;

import com.example.alertless.models.DateRangeModel;
import com.example.alertless.models.WeekScheduleModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ca.antonious.materialdaypicker.MaterialDayPicker;

/*
This class is util class for converting weekdays in to a byte representation and vice-versa.
It is assumed that in a byte weekdays are starting from left with sunday.
 */
public class WeekUtils {

    public static byte addWeekdays(byte week, MaterialDayPicker.Weekday... weekdays) {
        for (MaterialDayPicker.Weekday weekday : weekdays) {
            week |= 1 << weekday.ordinal();
        }
        return week;
    }

    public static byte addWeekdays(MaterialDayPicker.Weekday... weekdays) {
        byte week = 0 << 0;
        return addWeekdays(week, weekdays);
    }

    public static byte addWeekdays(List<MaterialDayPicker.Weekday> weekdays) {
        return addWeekdays(weekdays.toArray(new MaterialDayPicker.Weekday[0]));
    }

    public static List<MaterialDayPicker.Weekday> getWeekdays(byte week) {
        List<MaterialDayPicker.Weekday> weekdays = new ArrayList<>();

        for (MaterialDayPicker.Weekday weekday : MaterialDayPicker.Weekday.values()) {
            if (isBitSet(week, weekday.ordinal())) {
                weekdays.add(weekday);
            }
        }

        return weekdays;
    }

    private static boolean isBitSet(byte week, int k) {
        return (week & (1 << k)) != 0;
    }

    public static void checkAndSetDateRangeInWeekSchedule(WeekScheduleModel weekSchedule) {
        DateRangeModel dateRangeModel = weekSchedule.getDateRangeModel();

        if (dateRangeModel == null) {
            dateRangeModel = new DateRangeModel();
            weekSchedule.setDateRangeModel(dateRangeModel);
        }

        Calendar date = Calendar.getInstance();

        if (dateRangeModel.getStartDateMs() == 0) {
            long startDateMs = date.getTimeInMillis();
            dateRangeModel.setStartDateMs(startDateMs);
        }

        if (dateRangeModel.getEndDateMs() == 0) {
            int currentYear = date.get(Calendar.YEAR);
            date.set(Calendar.YEAR, currentYear + Constants.DEFAULT_WEEK_SCHEDULE_END_DURATION_YEARS);

            long endDateMs = date.getTimeInMillis();
            dateRangeModel.setEndDateMs(endDateMs);
        }

    }
}
