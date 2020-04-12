package com.example.alertless.utils;

import com.example.alertless.models.TimeRangeModel;

public class Constants {
    public static final String TAG_SUFFIX = ".tag";
    public static final String CURRENT_PROFILE = "CURRENT_PROFILE";
    public static final String SCHEDULE_RESULT = "SCHEDULE_RESULT";
    public static final String SCHEDULE_ERROR = "SCHEDULE_ERROR";

    // Time tags
    public static final String START_TIME_TAG = "START_TIME_TAG";
    public static final String END_TIME_TAG = "END_TIME_TAG";

    // Date tags
    public static final String START_DATE_TAG = "START_DATE_TAG";
    public static final String END_DATE_TAG = "END_DATE_TAG";

    // Default Date and Time values
    public static final int DEFAULT_START_TIME_DAILY_MIN = 0;
    public static final int DEFAULT_END_TIME_DAILY_MIN = 1439; // (24 * 60) - 1;
    public static final int DEFAULT_WEEK_SCHEDULE_END_DURATION_YEARS = 100;

}
