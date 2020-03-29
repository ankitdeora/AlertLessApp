package com.example.alertless;

import com.example.alertless.database.entities.User;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

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
}