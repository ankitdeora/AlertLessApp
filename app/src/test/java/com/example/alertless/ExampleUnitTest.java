package com.example.alertless;

import androidx.sqlite.db.SimpleSQLiteQuery;

import com.example.alertless.entities.AppDetailsEntity;
import com.example.alertless.entities.Identity;
import com.example.alertless.models.AppDetailsModel;
import com.example.alertless.models.DateRangeModel;
import com.example.alertless.utils.Constants;
import com.example.alertless.utils.DateRangeUtils;
import com.example.alertless.utils.DiffUtils;
import com.example.alertless.utils.StringUtils;
import com.example.alertless.utils.WeekUtils;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import ca.antonious.materialdaypicker.MaterialDayPicker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void CalendarTest() {
        long ms = Calendar.getInstance().getTimeInMillis();
        System.out.println(ms);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 29);

        System.out.println("Day of month : " + c.get(Calendar.DAY_OF_MONTH));
        System.out.println("Hours : " + c.get(Calendar.HOUR_OF_DAY));
        System.out.println("Min : " + c.get(Calendar.MINUTE));
        System.out.println("Day of week : " + c.get(Calendar.DAY_OF_WEEK));
    }

    @Test
    public void addHundredYears() {
        Calendar date = Calendar.getInstance();
        long startDateMs = date.getTimeInMillis();
        System.out.println(startDateMs);

        int currentYear = date.get(Calendar.YEAR);
        date.set(Calendar.YEAR, currentYear + Constants.DEFAULT_WEEK_SCHEDULE_END_DURATION_YEARS);

        long endDateMs = date.getTimeInMillis();
        System.out.println(endDateMs);

        long diff = endDateMs - startDateMs;
        System.out.println(diff);
        long divisor = (long)1000 * 60 * 60 * 24 * 365;
        System.out.println(divisor);

        long years = diff / divisor;
        System.out.println(years);
        Assert.assertEquals(Constants.DEFAULT_WEEK_SCHEDULE_END_DURATION_YEARS, years);
    }

    @Test
    public void testStringUtils() {
        String strA = null;
        String strB = "";
        String strC = "            ";
        String strD = "  abc  ";

        Assert.assertTrue(StringUtils.isBlank(strA));
        Assert.assertTrue(StringUtils.isBlank(strB));
        Assert.assertTrue(StringUtils.isBlank(strC));
        Assert.assertTrue(StringUtils.isNotBlank(strD));
    }

    @Test
    public void testBitSet() {
        byte test = 1 << 0; // set SUNDAY by default
        test = WeekUtils.addWeekdaysAndGetByte(test, MaterialDayPicker.Weekday.SATURDAY);
        Assert.assertEquals(65, test);
        System.out.println(WeekUtils.getWeekdays(test));

        test = WeekUtils.addWeekdaysAndGetByte(test, MaterialDayPicker.Weekday.MONDAY);
        Assert.assertEquals(67, test);
        System.out.println(WeekUtils.getWeekdays(test));

        test = WeekUtils.getByte(MaterialDayPicker.Weekday.SUNDAY, MaterialDayPicker.Weekday.MONDAY, MaterialDayPicker.Weekday.TUESDAY);
        Assert.assertEquals(7, test);
        System.out.println(WeekUtils.getWeekdays(test));
    }

    @Test
    public void testIsConsecutive() {
        Calendar calA = Calendar.getInstance();
        System.out.println(calA.getTime());

        Calendar calB = (Calendar) calA.clone();
        calB.add(Calendar.DATE, 1);
        System.out.println(calB.getTime());

        assertEquals(calB.get(Calendar.DATE) - calA.get(Calendar.DATE), 1);

        Calendar calC = Calendar.getInstance();
        calC.set(Calendar.YEAR, 2020);
        calC.set(Calendar.MONTH, 3);
        calC.set(Calendar.DAY_OF_MONTH, 30);
        System.out.println(calC.getTime());

        Calendar calNextMonth = Calendar.getInstance();
        calNextMonth.set(Calendar.YEAR, 2020);
        calNextMonth.set(Calendar.MONTH, 4);
        calNextMonth.set(Calendar.DAY_OF_MONTH, 1);

        Calendar calD = (Calendar) calC.clone();
        calD.add(Calendar.DATE, 1);
        System.out.println(calD.getTime());

        assertTrue(calNextMonth.get(Calendar.YEAR) == calD.get(Calendar.YEAR));
        assertTrue(calNextMonth.get(Calendar.MONTH) == calD.get(Calendar.MONTH));
        assertTrue(calNextMonth.get(Calendar.DAY_OF_MONTH) == calD.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void testCreateDateRanges() {
        Calendar calA = Calendar.getInstance();
        Calendar calB = Calendar.getInstance();
        Calendar calC = Calendar.getInstance();

        int dayOfMonth = calA.get(Calendar.DAY_OF_MONTH);

        // All Consecutive
        calB.set(Calendar.DAY_OF_MONTH, dayOfMonth+1);
        calC.set(Calendar.DAY_OF_MONTH, dayOfMonth+2);

        List<Calendar> dates = Arrays.asList(calA, calB, calC);

        List<DateRangeModel> dateRangeModels = DateRangeUtils.getDateSchedule(dates);
        System.out.println(dateRangeModels);
        assertEquals(1, dateRangeModels.size());

        // Last Two dates consecutive
        calB.set(Calendar.DAY_OF_MONTH, dayOfMonth+2);
        calC.set(Calendar.DAY_OF_MONTH, dayOfMonth+3);

        dateRangeModels = DateRangeUtils.getDateSchedule(dates);
        System.out.println(dateRangeModels);
        assertEquals(2, dateRangeModels.size());

        // First Two dates consecutive
        calB.set(Calendar.DAY_OF_MONTH, dayOfMonth+1);
        calC.set(Calendar.DAY_OF_MONTH, dayOfMonth+3);

        dateRangeModels = DateRangeUtils.getDateSchedule(dates);
        System.out.println(dateRangeModels);
        assertEquals(2, dateRangeModels.size());

        // None Consecutive
        calB.set(Calendar.DAY_OF_MONTH, dayOfMonth+2);
        calC.set(Calendar.DAY_OF_MONTH, dayOfMonth+4);

        dateRangeModels = DateRangeUtils.getDateSchedule(dates);
        System.out.println(dateRangeModels);
        assertEquals(3, dateRangeModels.size());

        // Only one date
        dateRangeModels = DateRangeUtils.getDateSchedule(Arrays.asList(calC));
        System.out.println(dateRangeModels);
        assertEquals(1, dateRangeModels.size());

    }

    @Test
    public void getDaysBetween() {
        long firstDateMs = 1586889000000L;
        long secondDateMs = 1587580200000L;

        List<Calendar> daysBetween = DateRangeUtils.getDaysBetween(firstDateMs, secondDateMs);
        daysBetween.forEach(cal -> System.out.println(cal.getTime()));

        assertEquals(9, daysBetween.size());
    }

    @Test
    public void testUniqueId() {
        System.out.println(UUID.randomUUID());
    }

    @Test
    public void testJoin() {
        String[] dateRanges = new String[] {"4", "5", "6"};

        String query = "select date_schedule_id " +
                "from multi_range_schedule " +
                "where date_schedule_id in " +
                "(select date_schedule_id " +
                "from multi_range_schedule where date_range_id in (?) " +
                "group by date_schedule_id " +
                "having count(distinct date_range_id) = ?) group by date_schedule_id having count (distinct date_range_id) = ?";
        String dateRangesWithComma = org.apache.commons.lang3.StringUtils.join(dateRanges, ",");
        SimpleSQLiteQuery simpleSQLiteQuery = new SimpleSQLiteQuery(query, new Object[]{dateRangesWithComma, 3, 3});
        System.out.println(simpleSQLiteQuery.getSql());
    }

    @Test
    public void testDiffUtils() {
        List<AppDetailsEntity> oldList = new ArrayList<>();
        oldList.add(AppDetailsEntity.builder().id("1").appName("ankit").packageName("package-ankit").build());
        oldList.add(AppDetailsEntity.builder().id("2").appName("nitina").packageName("package-nitina").build());
        oldList.add(AppDetailsEntity.builder().id("3").appName("papa").packageName("package-papa").build());

        List<AppDetailsEntity> newList = new ArrayList<>();
        newList.add(AppDetailsEntity.builder().id("4").appName("pranav").packageName("package-pranav").build());
        newList.add(AppDetailsEntity.builder().id("2").appName("jiaji").packageName("package-nitina").build());
        newList.add(AppDetailsEntity.builder().id("5").appName("sanaya").packageName("package-sanaya").build());

        DiffUtils<AppDetailsEntity> diffUtils = new DiffUtils<>(oldList, newList);
        diffUtils.findDiff();

        System.out.println(diffUtils.added());
        System.out.println(diffUtils.updated());
        System.out.println(diffUtils.removed());
    }

}
