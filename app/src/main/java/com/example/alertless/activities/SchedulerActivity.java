package com.example.alertless.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.example.alertless.R;
import com.example.alertless.commons.ScheduleType;
import com.example.alertless.exceptions.AlertlessFlowException;
import com.example.alertless.models.DateRangeModel;
import com.example.alertless.models.DateScheduleModel;
import com.example.alertless.models.Schedule;
import com.example.alertless.models.TimeRangeModel;
import com.example.alertless.models.WeekScheduleModel;
import com.example.alertless.scheduler.WeekScheduleDatePicker;
import com.example.alertless.scheduler.ScheduleTimePicker;
import com.example.alertless.utils.Constants;
import com.example.alertless.utils.DateRangeUtils;
import com.example.alertless.utils.ToastUtils;
import com.example.alertless.utils.WeekUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ca.antonious.materialdaypicker.MaterialDayPicker;

import static com.applandeo.materialcalendarview.CalendarView.MANY_DAYS_PICKER;
import static com.applandeo.materialcalendarview.CalendarView.RANGE_PICKER;

public class SchedulerActivity extends AppCompatActivity {

    private static final String TAG = SchedulerActivity.class.getName() + Constants.TAG_SUFFIX;
    private int datePickerType = MANY_DAYS_PICKER; // default picker type

    // State Variables Should be initialized in OnCreate()
    private WeekScheduleModel weekSchedule;
    private Map<Integer, DateScheduleModel> dateScheduleMap;
    private Schedule currentSchedule;
    private TimeRangeModel timeRangeModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduler);

        initCurrentStates();

        // TODO: Set DateRangeModel obtained from ProfileEditActivity
    }

    private void initCurrentStates() {
        // TODO: Read from current profile and set current states
        this.timeRangeModel = TimeRangeModel.builder()
                .startMin(Constants.DEFAULT_START_TIME_DAILY_MIN)
                .endMin(Constants.DEFAULT_END_TIME_DAILY_MIN)
                .build();

        this.weekSchedule = new WeekScheduleModel();

        MaterialDayPicker weekdayPicker = findViewById(R.id.week_day_picker);
        weekdayPicker.setDaySelectionChangedListener(selectedWeekDays -> {
            this.weekSchedule.setWeekdays(selectedWeekDays);
        });

        this.dateScheduleMap = new HashMap<>();
        this.dateScheduleMap.put(MANY_DAYS_PICKER, new DateScheduleModel());
        this.dateScheduleMap.put(RANGE_PICKER, new DateScheduleModel());

        RadioGroup scheduleRadioGroup = (RadioGroup) findViewById(R.id.scheduleRadioGroup);
        int selectedScheduleRadioId = scheduleRadioGroup.getCheckedRadioButtonId();

        this.currentSchedule = this.weekSchedule; // default is set to week

        if (R.id.radio_date == selectedScheduleRadioId) {
            this.datePickerType = MANY_DAYS_PICKER; // default value

            RadioGroup dateRadioGroup = (RadioGroup) findViewById(R.id.date_selector_radios);
            int selectedDateRadioId = dateRadioGroup.getCheckedRadioButtonId();

            if (R.id.range_selector_radio == selectedDateRadioId) {
                this.datePickerType = RANGE_PICKER;
            }

            this.currentSchedule = this.dateScheduleMap.get(this.datePickerType);
        }

    }

    private void populateWeekDaySchedule() {

        WeekScheduleModel currentWeekSchedule = (WeekScheduleModel) this.currentSchedule;

        // Set Date Range if not present
        WeekUtils.checkAndSetDateRangeInWeekSchedule(currentWeekSchedule);

        // Set Time Range
        currentWeekSchedule.getDateRangeModel().setTimeRangeModel(this.timeRangeModel);
    }

    private void populateSelectedSchedule() throws AlertlessFlowException {

        if (ScheduleType.BY_WEEK.equals(this.currentSchedule.getType())) {
            populateWeekDaySchedule();

        } else if (ScheduleType.BY_DATE.equals(this.currentSchedule.getType())) {
            // Date schedule is already populated on selecting dates
        } else {
            ToastUtils.showToast(getApplicationContext(), "No Schedule Type Selected !!!");
            return;
        }
    }

    public void saveSchedule(View v) {
        if (!isValidScheduleData()) {
            return;
        }

        // TODO: save schedule data here for the current profile
        Intent returnIntent = new Intent();

        try {
            populateSelectedSchedule();
            Log.i(TAG, this.currentSchedule.toString());

            returnIntent.putExtra(Constants.SCHEDULE_RESULT, this.currentSchedule);
            setResult(Activity.RESULT_OK, returnIntent);

        } catch (AlertlessFlowException e) {
            returnIntent.putExtra(Constants.SCHEDULE_ERROR, e.getMessage());
            setResult(Activity.RESULT_CANCELED, returnIntent);
        }

        finish();
    }

    private boolean isValidScheduleData() {
        if (ScheduleType.BY_WEEK.equals(this.currentSchedule.getType())) {

            MaterialDayPicker weekDayPicker = findViewById(R.id.week_day_picker);
            List<MaterialDayPicker.Weekday> selectedWeekDays = weekDayPicker.getSelectedDays();

            if (selectedWeekDays == null || selectedWeekDays.isEmpty()) {

                showAlertDialog("No Schedule Selection", "Please select at least one weekday !!!");
                return false;
            }
        } else if (ScheduleType.BY_DATE.equals(this.currentSchedule.getType())) {

            DateScheduleModel dateScheduleModel = (DateScheduleModel) this.currentSchedule;
            if (dateScheduleModel.getDateRangeModels() == null) {
                showAlertDialog("No Schedule Selection", "Please select at least one date !!!");
                return false;
            }
        }

        return true;
    }

    private void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void showStartTimePickerDialog(View v) {
        DialogFragment timeFragment = new ScheduleTimePicker(this.timeRangeModel);
        timeFragment.show(getSupportFragmentManager(), Constants.START_TIME_TAG);
    }

    public void showEndTimePickerDialog(View v) {
        DialogFragment timeFragment = new ScheduleTimePicker(this.timeRangeModel);
        timeFragment.show(getSupportFragmentManager(), Constants.END_TIME_TAG);
    }

    public void showWeekScheduleStartDatePickerDialog(View v) {
        DialogFragment dateFragment = new WeekScheduleDatePicker((WeekScheduleModel) this.currentSchedule);
        dateFragment.show(getSupportFragmentManager(), Constants.START_DATE_TAG);
    }

    public void showWeekScheduleEndDatePickerDialog(View v) {
        DialogFragment dateFragment = new WeekScheduleDatePicker((WeekScheduleModel) this.currentSchedule);
        dateFragment.show(getSupportFragmentManager(), Constants.END_DATE_TAG);
    }

    public void onIndividualDatePickerRadioSelection(View v) {
        this.datePickerType = MANY_DAYS_PICKER;
        this.currentSchedule = this.dateScheduleMap.get(MANY_DAYS_PICKER);
    }

    public void onRangeDatePickerRadioSelection(View v) {
        this.datePickerType = RANGE_PICKER;
        this.currentSchedule = this.dateScheduleMap.get(RANGE_PICKER);
    }

    public void showMultipleDatePicker(View v) {

        DateScheduleModel dateScheduleModel = this.dateScheduleMap.get(this.datePickerType);
        List<DateRangeModel> dateRangeModels = dateScheduleModel.getDateRangeModels();

        List<Calendar> existingDates = DateRangeUtils.getCalendarDates(dateRangeModels);
        List days = Optional.ofNullable(existingDates).orElse(Collections.EMPTY_LIST);

        DatePickerBuilder builder = new DatePickerBuilder(this, getSelectDateListener())
                .setPickerType(this.datePickerType)
                .setHeaderColor(R.color.colorAccent)
                .setSelectionColor(R.color.colorAccent)
                .setTodayColor(R.color.colorAccent)
                .setSelectedDays(days);

        DatePicker datePicker = builder.build();
        datePicker.show();
    }

    public Calendar getCalendar(int year, int month, int day) {
        Calendar date = Calendar.getInstance();

        date.set(Calendar.YEAR, year);
        date.set(Calendar.MONTH, month);
        date.set(Calendar.DAY_OF_MONTH, day);

        return date;
    }

    private OnSelectDateListener getSelectDateListener() {
        return new OnSelectDateListener() {
            @Override
            public void onSelect(List<Calendar> calendars) {

                List<DateRangeModel> dateRangeModels = DateRangeUtils.getDateSchedule(calendars, timeRangeModel);

                DateScheduleModel dateScheduleModel = dateScheduleMap.get(datePickerType);
                dateScheduleModel.setDateRangeModels(dateRangeModels);
            }
        };
    }

    public void onWeekRadioSelection(View v) {

        LinearLayout switchLayout = findViewById(R.id.scheduleSwitchLayout);
        switchLayout.removeAllViews();

        // switch view to week selector
        getLayoutInflater().inflate(R.layout.week_selector, switchLayout);

        this.currentSchedule = this.weekSchedule;

        MaterialDayPicker weekdayPicker = findViewById(R.id.week_day_picker);
        weekdayPicker.setDaySelectionChangedListener(selectedWeekDays -> {
            this.weekSchedule.setWeekdays(selectedWeekDays);
        });

        if (this.weekSchedule.getWeekdays() != null) {
            weekdayPicker.setSelectedDays(this.weekSchedule.getWeekdays());
        }
    }

    public void onDateRadioSelection(View v) {

        LinearLayout switchLayout = findViewById(R.id.scheduleSwitchLayout);
        switchLayout.removeAllViews();

        // switch view to date selector
        getLayoutInflater().inflate(R.layout.date_selector, switchLayout);

        RadioGroup dateSelectorRadioGroup = (RadioGroup) findViewById(R.id.date_selector_radios);

        int datePickerRadioBtnId = this.datePickerType == RANGE_PICKER ? R.id.range_selector_radio : R.id.individual_selector_radio ;
        dateSelectorRadioGroup.check(datePickerRadioBtnId);

        this.currentSchedule = this.dateScheduleMap.get(this.datePickerType);
    }
}
