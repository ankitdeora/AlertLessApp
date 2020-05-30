package com.example.alertless.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.example.alertless.R;
import com.example.alertless.enums.DatePickerType;
import com.example.alertless.enums.ScheduleType;
import com.example.alertless.database.repositories.ProfileRepository;
import com.example.alertless.database.repositories.WeekScheduleRepository;
import com.example.alertless.entities.relations.ProfileScheduleRelation;
import com.example.alertless.exceptions.AlertlessDatabaseException;
import com.example.alertless.models.DateRangeModel;
import com.example.alertless.models.MultiRangeScheduleModel;
import com.example.alertless.models.Profile;
import com.example.alertless.models.ScheduleModel;
import com.example.alertless.models.TimeRangeModel;
import com.example.alertless.models.WeekScheduleModel;
import com.example.alertless.scheduler.WeekScheduleDatePicker;
import com.example.alertless.scheduler.ScheduleTimePicker;
import com.example.alertless.utils.Constants;
import com.example.alertless.utils.DateRangeUtils;
import com.example.alertless.utils.ToastUtils;
import com.example.alertless.utils.WeekUtils;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ca.antonious.materialdaypicker.MaterialDayPicker;

import static com.example.alertless.enums.DatePickerType.MANY_DAYS;
import static com.example.alertless.enums.DatePickerType.RANGE;

public class SchedulerActivity extends AppCompatActivity {

    private static final String TAG = SchedulerActivity.class.getName() + Constants.TAG_SUFFIX;

    // Repository
    private WeekScheduleRepository weekScheduleRepository = WeekScheduleRepository.getInstance(getApplication());
    private ProfileRepository profileRepository = ProfileRepository.getInstance(getApplication());

    // State Variables to store states (user preferences)
    private TimeRangeModel timeRangeModel;
    private WeekScheduleModel weekSchedule;
    private Map<DatePickerType, MultiRangeScheduleModel> multiRangeScheduleMap;
    private DatePickerType datePickerType;
    private Profile currentProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduler);

        initCurrentStates();
    }

    private void initCurrentStates() {
        this.currentProfile = (Profile) getIntent().getSerializableExtra(Constants.CURRENT_PROFILE);

        if (currentProfile == null) {
            finishActivityWithErr("Scheduler Activity cancelled as no Profile configured !!!");
        }

        String profileName = this.currentProfile.getDetails().getName();
        try {
            this.setCurrentSchedule(this.profileRepository.getSchedule(profileName));
        } catch (AlertlessDatabaseException e) {
            finishActivityWithErr(e.getMessage());
        }

        initScheduleStates();
    }

    private void loadWeekView() {
        RadioGroup scheduleRadioGroup = findViewById(R.id.scheduleRadioGroup);
        scheduleRadioGroup.check(R.id.radio_week);
        onWeekRadioSelection(null);
    }

    private void loadDateView() {
        RadioGroup scheduleRadioGroup = findViewById(R.id.scheduleRadioGroup);
        scheduleRadioGroup.check(R.id.radio_date);
        onDateRadioSelection(null);
    }

    private void loadManyDateView() {
        loadDateView();
        RadioGroup dateRadioGroup = findViewById(R.id.date_selector_radios);
        dateRadioGroup.check(R.id.individual_selector_radio);
        onIndividualDatePickerRadioSelection(null);
    }

    private void loadRangeDateView() {
        loadDateView();
        RadioGroup dateRadioGroup = findViewById(R.id.date_selector_radios);
        dateRadioGroup.check(R.id.range_selector_radio);
        onRangeDatePickerRadioSelection(null);
    }

    private void initScheduleStates() {

        Optional<ScheduleModel> optionalCurrentSchedule = Optional.ofNullable(this.getCurrentSchedule());

        // init time range model
        this.timeRangeModel = optionalCurrentSchedule.map(ScheduleModel::getTimeRangeModel)
                                .orElseGet(() -> TimeRangeModel.builder()
                                                    .startMin(Constants.DEFAULT_START_TIME_DAILY_MIN)
                                                    .endMin(Constants.DEFAULT_END_TIME_DAILY_MIN)
                                                    .build());

        // check and init week schedule
        this.weekSchedule = optionalCurrentSchedule.filter(ScheduleModel::isWeekType)
                                                .map(scheduleModel -> (WeekScheduleModel) scheduleModel)
                                                .orElseGet(WeekScheduleModel::new);

        // check and init many days schedule
        MultiRangeScheduleModel manyDaysSchedule = optionalCurrentSchedule.filter(ScheduleModel::isDateType)
                                                    .map(scheduleModel -> (MultiRangeScheduleModel)scheduleModel)
                                                    .filter(MultiRangeScheduleModel::isManyDaysType)
                                                    .orElseGet(MultiRangeScheduleModel::new);

        // check and init range schedule
        MultiRangeScheduleModel rangeDaysSchedule = optionalCurrentSchedule.filter(ScheduleModel::isDateType)
                                                        .map(scheduleModel -> (MultiRangeScheduleModel)scheduleModel)
                                                        .filter(MultiRangeScheduleModel::isRangeType)
                                                        .orElseGet(MultiRangeScheduleModel::new);

        this.multiRangeScheduleMap = new HashMap<>();
        this.multiRangeScheduleMap.put(MANY_DAYS, manyDaysSchedule);
        this.multiRangeScheduleMap.put(RANGE, rangeDaysSchedule);

        // check and init date schedule type
        this.datePickerType = optionalCurrentSchedule.filter(ScheduleModel::isDateType)
                .map(scheduleModel -> (MultiRangeScheduleModel)scheduleModel)
                .filter(MultiRangeScheduleModel::isRangeType).isPresent() ? RANGE : MANY_DAYS;

        // if current schedule not set
        this.setCurrentSchedule(optionalCurrentSchedule.orElse(this.weekSchedule));

        if (ScheduleType.BY_WEEK.equals(this.getCurrentSchedule().getType())) {
            loadWeekView();
        } else if (ScheduleType.BY_DATE.equals(this.getCurrentSchedule().getType())) {
            if (MANY_DAYS.equals(this.datePickerType)) {
                loadManyDateView();
            } else if (RANGE.equals(this.datePickerType)) {
                loadRangeDateView();
            } else {
                String errMsg = String.format("Date picker type %s NOT supported, only %s are allowed",
                                                        this.datePickerType.name(), DatePickerType.values());
                finishActivityWithErr(errMsg);
            }
        } else {
            String errMsg = String.format("Schedule type %s NOT supported, only %s are allowed",
                    this.getCurrentSchedule().getType().name(), ScheduleType.values());
            finishActivityWithErr(errMsg);
        }
    }

    private void finishActivityWithErr(String errMsg) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Constants.SCHEDULE_ERROR, errMsg);
        setResult(Activity.RESULT_CANCELED, returnIntent);

        ToastUtils.showToast(getApplicationContext(), errMsg);

        finish();
    }

    private void populateWeekDaySchedule() {

        WeekScheduleModel currentWeekSchedule = (WeekScheduleModel) this.getCurrentSchedule();

        // Set Date Range if not present
        WeekUtils.checkAndSetDateRangeInWeekSchedule(currentWeekSchedule);
    }

    private void populateSelectedSchedule() {
        this.getCurrentSchedule().setTimeRangeModel(this.timeRangeModel);

        if (ScheduleType.BY_WEEK.equals(this.getCurrentSchedule().getType())) {
            populateWeekDaySchedule();

        } else if (ScheduleType.BY_DATE.equals(this.getCurrentSchedule().getType())) {
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
            Log.i(TAG, this.getCurrentSchedule().toString());

            // TODO: remove save time range
            saveProfileSchedule();

            returnIntent.putExtra(Constants.SCHEDULE_RESULT, this.getCurrentSchedule());
            setResult(Activity.RESULT_OK, returnIntent);

        } catch (Exception e) {
            returnIntent.putExtra(Constants.SCHEDULE_ERROR, e.getMessage());
            setResult(Activity.RESULT_CANCELED, returnIntent);
            ToastUtils.showToast(getApplicationContext(), e.getMessage());
        }

        finish();
    }

    public void showTimeRange(View v) throws AlertlessDatabaseException {

        this.weekScheduleRepository.listAllEntities();
        ToastUtils.showToast(getApplicationContext(), "Check data in logs");
    }

    private void saveProfileSchedule() {
        String msg = "Schedule Saved !!!";

        try {

            ProfileScheduleRelation profileScheduleRelation = this.profileRepository.createOrUpdateSchedule(this.currentProfile);
            Log.i(TAG, profileScheduleRelation.toString());

        } catch (Exception e) {
            msg = e.getMessage();
            Log.i(TAG, msg, e);
        }
        ToastUtils.showToast(getApplicationContext(), msg);
    }

    private boolean isValidScheduleData() {
        if (ScheduleType.BY_WEEK.equals(this.getCurrentSchedule().getType())) {

            MaterialDayPicker weekDayPicker = findViewById(R.id.week_day_picker);
            List<MaterialDayPicker.Weekday> selectedWeekDays = weekDayPicker.getSelectedDays();

            if (selectedWeekDays.isEmpty()) {

                showAlertDialog("No Schedule Selection", "Please select at least one weekday !!!");
                return false;
            }
        } else if (ScheduleType.BY_DATE.equals(this.getCurrentSchedule().getType())) {

            MultiRangeScheduleModel dateScheduleModel = (MultiRangeScheduleModel) this.getCurrentSchedule();
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
        DialogFragment dateFragment = new WeekScheduleDatePicker((WeekScheduleModel) this.getCurrentSchedule());
        dateFragment.show(getSupportFragmentManager(), Constants.START_DATE_TAG);
    }

    public void showWeekScheduleEndDatePickerDialog(View v) {
        DialogFragment dateFragment = new WeekScheduleDatePicker((WeekScheduleModel) this.getCurrentSchedule());
        dateFragment.show(getSupportFragmentManager(), Constants.END_DATE_TAG);
    }

    public void onIndividualDatePickerRadioSelection(View v) {
        this.datePickerType = MANY_DAYS;
        this.setCurrentSchedule(this.multiRangeScheduleMap.get(MANY_DAYS));
    }

    public void onRangeDatePickerRadioSelection(View v) {
        this.datePickerType = RANGE;
        this.setCurrentSchedule(this.multiRangeScheduleMap.get(RANGE));
    }

    public void showMultipleDatePicker(View v) {

        MultiRangeScheduleModel dateScheduleModel = this.multiRangeScheduleMap.get(this.datePickerType);
        List<DateRangeModel> dateRangeModels = dateScheduleModel.getDateRangeModels();

        List<Calendar> existingDates = DateRangeUtils.getCalendarDates(dateRangeModels);
        List days = Optional.ofNullable(existingDates).orElse(Collections.EMPTY_LIST);

        DatePickerBuilder builder = new DatePickerBuilder(this, getSelectDateListener())
                .setPickerType(this.datePickerType.value())
                .setHeaderColor(R.color.colorAccent)
                .setSelectionColor(R.color.colorAccent)
                .setTodayColor(R.color.colorAccent)
                .setSelectedDays(days);

        DatePicker datePicker = builder.build();
        datePicker.show();
    }

    private OnSelectDateListener getSelectDateListener() {
        return new OnSelectDateListener() {
            @Override
            public void onSelect(List<Calendar> calendars) {

                List<DateRangeModel> dateRangeModels = DateRangeUtils.getDateSchedule(calendars);

                MultiRangeScheduleModel dateScheduleModel = multiRangeScheduleMap.get(datePickerType);
                dateScheduleModel.setDateRangeModels(dateRangeModels);
                dateScheduleModel.setTimeRangeModel(timeRangeModel);
            }
        };
    }

    public void onWeekRadioSelection(View v) {

        LinearLayout switchLayout = findViewById(R.id.scheduleSwitchLayout);
        switchLayout.removeAllViews();

        // switch view to week selector
        getLayoutInflater().inflate(R.layout.week_selector, switchLayout);

        this.setCurrentSchedule(this.weekSchedule);

        MaterialDayPicker weekdayPicker = findViewById(R.id.week_day_picker);
        weekdayPicker.setDaySelectionChangedListener(this.weekSchedule::setWeekdays);

        if (this.weekSchedule.getWeekdays() != null) {
            weekdayPicker.setSelectedDays(this.weekSchedule.getWeekdays());
        }
    }

    public void onDateRadioSelection(View v) {

        LinearLayout switchLayout = findViewById(R.id.scheduleSwitchLayout);
        switchLayout.removeAllViews();

        // switch view to date selector
        getLayoutInflater().inflate(R.layout.date_selector, switchLayout);

        RadioGroup dateSelectorRadioGroup = findViewById(R.id.date_selector_radios);

        int datePickerRadioBtnId = RANGE.equals(this.datePickerType) ? R.id.range_selector_radio : R.id.individual_selector_radio ;
        dateSelectorRadioGroup.check(datePickerRadioBtnId);

        this.setCurrentSchedule(this.multiRangeScheduleMap.get(this.datePickerType));
    }

    private ScheduleModel getCurrentSchedule() {
        return this.currentProfile.getSchedule();
    }

    private void setCurrentSchedule(ScheduleModel scheduleModel) {
        this.currentProfile.setSchedule(scheduleModel);
    }
}
