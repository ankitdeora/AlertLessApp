package com.example.alertless.utils;

import com.example.alertless.models.DateRangeModel;
import com.example.alertless.models.TimeRangeModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DateRangeUtils {

    public static List<DateRangeModel> getDateSchedule(List<Calendar> dates) {
        return getDateSchedule(dates, null);
    }

    public static List<DateRangeModel> getDateSchedule(List<Calendar> dates, TimeRangeModel timeRangeModel) {
        if (dates == null) {
            return null;
        }

        Collections.sort(dates);
        List<DateRangeModel> dateRangeModels = new ArrayList<>();

        int count = dates.size();
        for (int i = 0; i < count; i++) {

            Calendar firstDate = dates.get(i);
            DateRangeModel dateRangeModel = DateRangeModel.builder()
                                                .startDateMs(firstDate.getTimeInMillis())
                                                .timeRangeModel(timeRangeModel)
                                            .build();

            if (i == (count-1)) {
                dateRangeModel.setEndDateMs(firstDate.getTimeInMillis());
                dateRangeModels.add(dateRangeModel);
                break;
            }

            Calendar secondDate = dates.get(i+1);
            while (isConsecutive(firstDate, secondDate)) {
                i++;
                if (i == (count-1)) {
                    dateRangeModel.setEndDateMs(secondDate.getTimeInMillis());
                    dateRangeModels.add(dateRangeModel);
                    break;
                }
                firstDate = secondDate;
                secondDate = dates.get(i+1);
            }

            if (i == (count-1)) {
                return dateRangeModels;
            }

            dateRangeModel.setEndDateMs(firstDate.getTimeInMillis());
            dateRangeModels.add(dateRangeModel);
        }

        return dateRangeModels;
    }

    private static boolean isConsecutive (final Calendar firstDate, final Calendar secondDate) {

        Calendar firstDateClone = (Calendar) firstDate.clone();
        firstDateClone.add(Calendar.DATE, 1);

        if ((secondDate.get(Calendar.YEAR) == firstDateClone.get(Calendar.YEAR)) &&
                (secondDate.get(Calendar.MONTH) == firstDateClone.get(Calendar.MONTH)) &&
                     (secondDate.get(Calendar.DAY_OF_MONTH) == firstDateClone.get(Calendar.DAY_OF_MONTH))) {
            return true;
        } else {
            return false;
        }
    }

    public static List<Calendar> getCalendarDates(List<DateRangeModel> dateRangeModels) {

        if (dateRangeModels == null) {
            return null;
        }

        List<Calendar> calendarDates = new ArrayList<>();

        for (DateRangeModel dateRangeModel : dateRangeModels) {

            long startDateMs = dateRangeModel.getStartDateMs();
            long endDateMs = dateRangeModel.getEndDateMs();

            List<Calendar> daysBetween = getDaysBetween(startDateMs, endDateMs);

            calendarDates.addAll(daysBetween);
        }

        return calendarDates;
    }

    private static List<Calendar> getDaysBetween(long startDateMs, long endDateMs) {
        return getDaysBetween(new Date(startDateMs), new Date(endDateMs));
    }

    private static List<Calendar> getDaysBetween(Date startDate, Date endDate) {

        List<Calendar> calendars = new ArrayList<>();

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);

        while (calendar.getTime().before(endDate))
        {
            calendars.add((Calendar) calendar.clone());
            calendar.add(Calendar.DATE, 1);
        }

        calendar.setTime(endDate);
        calendars.add(calendar);

        return calendars;
    }
}
