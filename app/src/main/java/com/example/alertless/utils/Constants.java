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

    // Common across tables
    public static final String ID = "id";

    // ProfileDetails table
    public static final String PROFILE_DETAILS_TABLE = "profile_details";
    public static final String PROFILE_NAME_COL = "name";
    public static final String ACTIVE_COL = "active";
    public static final String PROFILE_ID_FK = "profile_id";

    // TimeRange table
    public static final String TIME_RANGE_TABLE = "time_range";
    public static final String START_MIN_COL = "start_min";
    public static final String END_MIN_COL = "end_min";
    public static final String TIME_RANGE_FK = "time_range_id";

    // DateRange table
    public static final String DATE_RANGE_TABLE = "date_range";
    public static final String START_DATE_MS_COL = "start_date_ms";
    public static final String END_DATE_MS_COL = "end_date_ms";
    public static final String DATE_RANGE_FK ="date_range_id";

    // Schedule Table
    public static final String SCHEDULE_TABLE = "schedule";
    public static final String SCHEDULE_TYPE_COL = "schedule_type";
    public static final String SCHEDULE_ID_FK = "schedule_id";

    // WeekSchedule Table
    public static final String WEEK_SCHEDULE_TABLE = "week_schedule";
    public static final String WEEK_SCHEDULE_ID = "week_schedule_id";
    public static final String WEEKDAYS_COL = "weekdays";

    // MultiRange Table
    public static final String MULTI_RANGE_SCHEDULE_TABLE = "multi_range_schedule";
    public static final String DATE_SCHEDULE_ID = "date_schedule_id";

    // ProfileSchedule Table
    public static final String PROFILE_SCHEDULE_RELATION_TABLE = "profile_schedule";

    // AppDetails Table
    public static final String APP_DETAILS_TABLE = "app_details";
    public static final String APP_NAME_COL = "app_name";
    public static final String PACKAGE_NAME_COL = "package_name";
    public static final String APP_ID_FK = "app_id";

    // ProfileApps Table
    public static final String PROFILE_APPS_RELATION_TABLE = "profile_apps";




}
