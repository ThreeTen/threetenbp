/*
 * Copyright (c) 2007,2008, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package javax.time.calendar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.time.calendar.format.FlexiDateTime;
import static org.testng.Assert.*;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.time.calendar.field.HourOfDay;
import javax.time.calendar.field.MinuteOfHour;
import javax.time.calendar.field.NanoOfSecond;
import javax.time.calendar.field.SecondOfMinute;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test LocalTime.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test(timeOut=5000)
public class TestLocalTime {
    private LocalTime TEST_12_30_40_987654321;

    @BeforeMethod
    public void setUp() {
        TEST_12_30_40_987654321 = LocalTime.time(12, 30, 40, 987654321);
    }

    //-----------------------------------------------------------------------
    private void check(LocalTime time, int h, int m, int s, int n) {
        assertEquals(time.getHourOfDay(), HourOfDay.hourOfDay(h));
        assertEquals(time.getMinuteOfHour(), MinuteOfHour.minuteOfHour(m));
        assertEquals(time.getSecondOfMinute(), SecondOfMinute.secondOfMinute(s));
        assertEquals(time.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(n));
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(TEST_12_30_40_987654321 instanceof Calendrical);
        assertTrue(TEST_12_30_40_987654321 instanceof Serializable);
        assertTrue(TEST_12_30_40_987654321 instanceof Comparable);
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(TEST_12_30_40_987654321);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), TEST_12_30_40_987654321);
    }

    public void test_immutable() {
        Class<LocalTime> cls = LocalTime.class;
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                assertTrue(Modifier.isFinal(field.getModifiers()), "Field:" + field.getName());
            } else {
                assertTrue(Modifier.isPrivate(field.getModifiers()), "Field:" + field.getName());
                assertTrue(Modifier.isFinal(field.getModifiers()), "Field:" + field.getName());
            }
        }
    }

    //-----------------------------------------------------------------------
    public void constant_MIDNIGHT() {
        check(LocalTime.MIDNIGHT, 0, 0, 0, 0);
    }

    public void constant_MIDDAY() {
        check(LocalTime.MIDDAY, 12, 0, 0, 0);
    }

    //-----------------------------------------------------------------------
    public void factory_time_2objects() {
        LocalTime test = LocalTime.time(HourOfDay.hourOfDay(12), MinuteOfHour.minuteOfHour(30));
        check(test, 12, 30, 0, 0);
    }

    public void factory_time_2objects_midnightSingleton() {
        LocalTime test = LocalTime.time(HourOfDay.hourOfDay(0), MinuteOfHour.minuteOfHour(0));
        assertSame(test, LocalTime.MIDNIGHT);
    }

    public void factory_time_2objects_middaySingleton() {
        LocalTime test = LocalTime.time(HourOfDay.hourOfDay(12), MinuteOfHour.minuteOfHour(0));
        assertSame(test, LocalTime.MIDDAY);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_time_2objects_nullHour() {
       LocalTime.time(null, MinuteOfHour.minuteOfHour(0));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_time_2objects_nullMinute() {
       LocalTime.time(HourOfDay.hourOfDay(0), null);
    }

    //-----------------------------------------------------------------------
    public void factory_time_3objects() {
        LocalTime test = LocalTime.time(HourOfDay.hourOfDay(12), MinuteOfHour.minuteOfHour(30), SecondOfMinute.secondOfMinute(59));
        check(test, 12, 30, 59, 0);
    }

    public void factory_time_3objects_midnightSingleton() {
        LocalTime test = LocalTime.time(HourOfDay.hourOfDay(0), MinuteOfHour.minuteOfHour(0), SecondOfMinute.secondOfMinute(0));
        assertSame(test, LocalTime.MIDNIGHT);
    }

    public void factory_time_3objects_middaySingleton() {
        LocalTime test = LocalTime.time(HourOfDay.hourOfDay(12), MinuteOfHour.minuteOfHour(0), SecondOfMinute.secondOfMinute(0));
        assertSame(test, LocalTime.MIDDAY);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_time_3objects_nullHour() {
       LocalTime.time(null, MinuteOfHour.minuteOfHour(0), SecondOfMinute.secondOfMinute(0));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_time_3objects_nullMinute() {
       LocalTime.time(HourOfDay.hourOfDay(0), null, SecondOfMinute.secondOfMinute(0));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_time_3objects_nullSecond() {
       LocalTime.time(HourOfDay.hourOfDay(0), MinuteOfHour.minuteOfHour(0), null);
    }

    //-----------------------------------------------------------------------
    public void factory_time_4objects() {
        LocalTime test = LocalTime.time(HourOfDay.hourOfDay(12), MinuteOfHour.minuteOfHour(30), SecondOfMinute.secondOfMinute(59), NanoOfSecond.nanoOfSecond(300));
        check(test, 12, 30, 59, 300);
    }

    public void factory_time_4objects_nonSingletons() {
        LocalTime test = LocalTime.time(HourOfDay.hourOfDay(0), MinuteOfHour.minuteOfHour(0), SecondOfMinute.secondOfMinute(1), NanoOfSecond.nanoOfSecond(0));
        check(test, 0, 0, 1, 0);
        test = LocalTime.time(HourOfDay.hourOfDay(0), MinuteOfHour.minuteOfHour(0), SecondOfMinute.secondOfMinute(0), NanoOfSecond.nanoOfSecond(1));
        check(test, 0, 0, 0, 1);
        test = LocalTime.time(HourOfDay.hourOfDay(12), MinuteOfHour.minuteOfHour(0), SecondOfMinute.secondOfMinute(0), NanoOfSecond.nanoOfSecond(1));
        check(test, 12, 0, 0, 1);
    }

    public void factory_time_4objects_midnightSingleton() {
        LocalTime test = LocalTime.time(HourOfDay.hourOfDay(0), MinuteOfHour.minuteOfHour(0), SecondOfMinute.secondOfMinute(0), NanoOfSecond.nanoOfSecond(0));
        assertSame(test, LocalTime.MIDNIGHT);
    }

    public void factory_time_4objects_middaySingleton() {
        LocalTime test = LocalTime.time(HourOfDay.hourOfDay(12), MinuteOfHour.minuteOfHour(0), SecondOfMinute.secondOfMinute(0), NanoOfSecond.nanoOfSecond(0));
        assertSame(test, LocalTime.MIDDAY);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_time_4objects_nullHour() {
       LocalTime.time(null, MinuteOfHour.minuteOfHour(0), SecondOfMinute.secondOfMinute(0), NanoOfSecond.nanoOfSecond(0));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_time_4objects_nullMinute() {
       LocalTime.time(HourOfDay.hourOfDay(0), null, SecondOfMinute.secondOfMinute(0), NanoOfSecond.nanoOfSecond(0));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_time_4objects_nullSecond() {
       LocalTime.time(HourOfDay.hourOfDay(0), MinuteOfHour.minuteOfHour(0), null, NanoOfSecond.nanoOfSecond(0));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_time_4objects_nullNano() {
       LocalTime.time(HourOfDay.hourOfDay(0), MinuteOfHour.minuteOfHour(0), SecondOfMinute.secondOfMinute(0), null);
    }

    //-----------------------------------------------------------------------
    public void factory_time_2ints() {
        LocalTime test = LocalTime.time(12, 30);
        check(test, 12, 30, 0, 0);
    }

    public void factory_time_2ints_midnightSingleton() {
        LocalTime test = LocalTime.time(0, 0);
        assertSame(test, LocalTime.MIDNIGHT);
    }

    public void factory_time_2ints_middaySingleton() {
        LocalTime test = LocalTime.time(12, 0);
        assertSame(test, LocalTime.MIDDAY);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_time_2ints_hourTooLow() {
        LocalTime.time(-1, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_time_2ints_hourTooHigh() {
        LocalTime.time(24, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_time_2ints_minuteTooLow() {
        LocalTime.time(0, -1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_time_2ints_minuteTooHigh() {
        LocalTime.time(0, 60);
    }

    //-----------------------------------------------------------------------
    public void factory_time_3ints() {
        LocalTime test = LocalTime.time(12, 30, 40);
        check(test, 12, 30, 40, 0);
    }

    public void factory_time_3ints_midnightSingleton() {
        LocalTime test = LocalTime.time(0, 0, 0);
        assertSame(test, LocalTime.MIDNIGHT);
    }

    public void factory_time_3ints_middaySingleton() {
        LocalTime test = LocalTime.time(12, 0, 0);
        assertSame(test, LocalTime.MIDDAY);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_time_3ints_hourTooLow() {
        LocalTime.time(-1, 0, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_time_3ints_hourTooHigh() {
        LocalTime.time(24, 0, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_time_3ints_minuteTooLow() {
        LocalTime.time(0, -1, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_time_3ints_minuteTooHigh() {
        LocalTime.time(0, 60, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_time_3ints_secondTooLow() {
        LocalTime.time(0, 0, -1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_time_3ints_secondTooHigh() {
        LocalTime.time(0, 0, 60);
    }

    //-----------------------------------------------------------------------
    public void factory_time_4ints() {
        LocalTime test = LocalTime.time(12, 30, 40, 987654321);
        check(test, 12, 30, 40, 987654321);
        test = LocalTime.time(12, 0, 40, 987654321);
        check(test, 12, 0, 40, 987654321);
    }

    public void factory_time_4ints_midnightSingleton() {
        LocalTime test = LocalTime.time(0, 0, 0, 0);
        assertSame(test, LocalTime.MIDNIGHT);
    }

    public void factory_time_4ints_middaySingleton() {
        LocalTime test = LocalTime.time(12, 0, 0, 0);
        assertSame(test, LocalTime.MIDDAY);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_time_4ints_hourTooLow() {
        LocalTime.time(-1, 0, 0, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_time_4ints_hourTooHigh() {
        LocalTime.time(24, 0, 0, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_time_4ints_minuteTooLow() {
        LocalTime.time(0, -1, 0, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_time_4ints_minuteTooHigh() {
        LocalTime.time(0, 60, 0, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_time_4ints_secondTooLow() {
        LocalTime.time(0, 0, -1, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_time_4ints_secondTooHigh() {
        LocalTime.time(0, 0, 60, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_time_4ints_nanoTooLow() {
        LocalTime.time(0, 0, 0, -1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_time_4ints_nanoTooHigh() {
        LocalTime.time(0, 0, 0, 1000000000);
    }

    //-----------------------------------------------------------------------
    public void factory_time_ReadableTime() {
        LocalTime localTime = LocalTime.time(TEST_12_30_40_987654321);
        check(localTime, 12, 30, 40, 987654321);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_time_ReadableTime_null() {
        LocalTime.time(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_time_ReadableTime_null_toLocalTime() {
        LocalTime.time(new ReadableTime() {
            public LocalTime toLocalTime() {
                return null;
            }

            public FlexiDateTime toFlexiDateTime() {
                return null;
            }
        });
    }

    //-----------------------------------------------------------------------
    public void test_isSupported() {
//        assertEquals(TEST_12_30_40_987654321.isSupported(ISOChronology.INSTANCE.yearRule()), true);
//        assertEquals(TEST_12_30_40_987654321.isSupported(ISOChronology.INSTANCE.monthOfYearRule()), true);
//        assertEquals(TEST_12_30_40_987654321.isSupported(ISOChronology.INSTANCE.dayOfMonthRule()), true);
//        assertEquals(TEST_12_30_40_987654321.isSupported(ISOChronology.INSTANCE.dayOfWeekRule()), true);
//        assertEquals(TEST_12_30_40_987654321.isSupported(ISOChronology.INSTANCE.dayOfYearRule()), true);
//        assertEquals(TEST_2007_07_15.isSupported(HourOfDay.RULE), false);
//        assertEquals(TEST_2007_07_15.isSupported(MinuteOfHour.RULE), false);
//        assertEquals(TEST_2007_07_15.isSupported(SecondOfMinute.RULE), false);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="sampleTimes")
    Object[][] provider_sampleTimes() {
        return new Object[][] {
            {0, 0, 0, 0},
            {0, 0, 0, 1},
            {0, 0, 1, 0},
            {0, 1, 0, 0},
            {1, 0, 0, 0},
            {1, 0, 1, 0},
            {0, 1, 0, 1},
            {1, 1, 1, 1},
        };
    }

//    //-----------------------------------------------------------------------
//    // get*()
//    //-----------------------------------------------------------------------
//    @Test(dataProvider="sampleDates")
//    public void test_get(int y, int m, int d) {
//        LocalTime a = LocalTime.time(y, m, d);
//        assertEquals(a.getYear(), Year.isoYear(y));
//        assertEquals(a.getMonthOfYear(), MonthOfYear.monthOfYear(m));
//        assertEquals(a.getDayOfMonth(), DayOfMonth.dayOfMonth(d));
//    }
//
//    @Test(dataProvider="sampleDates")
//    public void test_getDOY(int y, int m, int d) {
//        Year year = Year.isoYear(y);
//        LocalTime a = LocalTime.time(y, m, d);
//        int total = 0;
//        for (int i = 1; i < m; i++) {
//            total += MonthOfYear.monthOfYear(i).lengthInDays(year);
//        }
//        int doy = total + d;
//        assertEquals(a.getDayOfYear(), DayOfYear.dayOfYear(doy));
//    }

    //-----------------------------------------------------------------------
    // withHourOfDay()
    //-----------------------------------------------------------------------
    public void test_withHourOfDay_normal() {
        LocalTime t = TEST_12_30_40_987654321;
        for (int i = 0; i < 24; i++) {
            t = t.withHourOfDay(i);
            assertEquals(t.getHourOfDay().getValue(), i);
        }
    }

    public void test_withHourOfDay_noChange() {
        LocalTime t = TEST_12_30_40_987654321.withHourOfDay(12);
        assertSame(t, TEST_12_30_40_987654321);
    }

    public void test_withHourOfDay_toMidnight() {
        LocalTime t = LocalTime.time(1, 0).withHourOfDay(0);
        assertSame(t, LocalTime.MIDNIGHT);
    }

    public void test_withHourOfDay_toMidday() {
        LocalTime t = LocalTime.time(1, 0).withHourOfDay(12);
        assertSame(t, LocalTime.MIDDAY);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withHourOfDay_hourTooLow() {
        TEST_12_30_40_987654321.withHourOfDay(-1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withHourOfDay_hourTooHigh() {
        TEST_12_30_40_987654321.withHourOfDay(24);
    }

    //-----------------------------------------------------------------------
    // withMinuteOfHour()
    //-----------------------------------------------------------------------
    public void test_withMinuteOfHour_normal() {
        LocalTime t = TEST_12_30_40_987654321;
        for (int i = 0; i < 60; i++) {
            t = t.withMinuteOfHour(i);
            assertEquals(t.getMinuteOfHour().getValue(), i);
        }
    }

    public void test_withMinuteOfHour_noChange() {
        LocalTime t = TEST_12_30_40_987654321.withMinuteOfHour(30);
        assertSame(t, TEST_12_30_40_987654321);
    }

    public void test_withMinuteOfHour_toMidnight() {
        LocalTime t = LocalTime.time(0, 1).withMinuteOfHour(0);
        assertSame(t, LocalTime.MIDNIGHT);
    }

    public void test_withMinuteOfHour_toMidday() {
        LocalTime t = LocalTime.time(12, 1).withMinuteOfHour(0);
        assertSame(t, LocalTime.MIDDAY);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withMinuteOfHour_hourTooLow() {
        TEST_12_30_40_987654321.withMinuteOfHour(-1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withMinuteOfHour_hourTooHigh() {
        TEST_12_30_40_987654321.withMinuteOfHour(60);
    }

    //-----------------------------------------------------------------------
    // withSecondOfMinute()
    //-----------------------------------------------------------------------
    public void test_withSecondOfMinute_normal() {
        LocalTime t = TEST_12_30_40_987654321;
        for (int i = 0; i < 60; i++) {
            t = t.withSecondOfMinute(i);
            assertEquals(t.getSecondOfMinute().getValue(), i);
        }
    }

    public void test_withSecondOfMinute_noChange() {
        LocalTime t = TEST_12_30_40_987654321.withSecondOfMinute(40);
        assertSame(t, TEST_12_30_40_987654321);
    }

    public void test_withSecondOfMinute_toMidnight() {
        LocalTime t = LocalTime.time(0, 0, 1).withSecondOfMinute(0);
        assertSame(t, LocalTime.MIDNIGHT);
    }

    public void test_withSecondOfMinute_toMidday() {
        LocalTime t = LocalTime.time(12, 0, 1).withSecondOfMinute(0);
        assertSame(t, LocalTime.MIDDAY);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withSecondOfMinute_hourTooLow() {
        TEST_12_30_40_987654321.withSecondOfMinute(-1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withSecondOfMinute_hourTooHigh() {
        TEST_12_30_40_987654321.withSecondOfMinute(60);
    }

    //-----------------------------------------------------------------------
    // withNanoOfSecond()
    //-----------------------------------------------------------------------
    public void test_withNanoOfSecond_normal() {
        LocalTime t = TEST_12_30_40_987654321;
        t = t.withNanoOfSecond(1);
        assertEquals(t.getNanoOfSecond().getValue(), 1);
        t = t.withNanoOfSecond(10);
        assertEquals(t.getNanoOfSecond().getValue(), 10);
        t = t.withNanoOfSecond(100);
        assertEquals(t.getNanoOfSecond().getValue(), 100);
        t = t.withNanoOfSecond(999999999);
        assertEquals(t.getNanoOfSecond().getValue(), 999999999);
    }

    public void test_withNanoOfSecond_noChange() {
        LocalTime t = TEST_12_30_40_987654321.withNanoOfSecond(987654321);
        assertSame(t, TEST_12_30_40_987654321);
    }

    public void test_withNanoOfSecond_toMidnight() {
        LocalTime t = LocalTime.time(0, 0, 0, 1).withNanoOfSecond(0);
        assertSame(t, LocalTime.MIDNIGHT);
    }

    public void test_withNanoOfSecond_toMidday() {
        LocalTime t = LocalTime.time(12, 0, 0, 1).withNanoOfSecond(0);
        assertSame(t, LocalTime.MIDDAY);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withNanoOfSecond_hourTooLow() {
        TEST_12_30_40_987654321.withNanoOfSecond(-1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withNanoOfSecond_hourTooHigh() {
        TEST_12_30_40_987654321.withNanoOfSecond(1000000000);
    }

    //-----------------------------------------------------------------------
    // plusHours()
    //-----------------------------------------------------------------------
    public void test_plusHours_one() {
        LocalTime t = LocalTime.MIDNIGHT;
        for (int i = 0; i < 50; i++) {
            t = t.plusHours(1);
            assertEquals(t.getHourOfDay().getValue(), (i + 1) % 24);
        }
    }

    public void test_plusHours_fromZero() {
        LocalTime base = LocalTime.MIDNIGHT;
        for (int i = -50; i < 50; i++) {
            LocalTime t = base.plusHours(i);
            assertEquals(t.getHourOfDay().getValue(), (i + 72) % 24);
        }
    }

    public void test_plusHours_fromOne() {
        LocalTime base = LocalTime.time(1, 0);
        for (int i = -50; i < 50; i++) {
            LocalTime t = base.plusHours(i);
            assertEquals(t.getHourOfDay().getValue(), (1 + i + 72) % 24);
        }
    }

    public void test_plusHours_noChange() {
        LocalTime t = TEST_12_30_40_987654321.plusHours(0);
        assertSame(t, TEST_12_30_40_987654321);
    }

    public void test_plusHours_toMidnight() {
        LocalTime t = LocalTime.time(23, 0).plusHours(1);
        assertSame(t, LocalTime.MIDNIGHT);
    }

    public void test_plusHours_toMidday() {
        LocalTime t = LocalTime.time(11, 0).plusHours(1);
        assertSame(t, LocalTime.MIDDAY);
    }

    //-----------------------------------------------------------------------
    // plusMinutes()
    //-----------------------------------------------------------------------
    public void test_plusMinutes_one() {
        LocalTime t = LocalTime.MIDNIGHT;
        int hour = 0;
        int min = 0;
        for (int i = 0; i < 70; i++) {
            t = t.plusMinutes(1);
            min++;
            if (min == 60) {
                hour++;
                min = 0;
            }
            assertEquals(t.getHourOfDay().getValue(), hour);
            assertEquals(t.getMinuteOfHour().getValue(), min);
        }
    }

    public void test_plusMinutes_fromZero() {
        LocalTime base = LocalTime.MIDNIGHT;
        int hour = 0;
        int min = 0;
        for (int i = -70; i < 70; i++) {
            LocalTime t = base.plusMinutes(i);
            if (i < -60) {
                hour = 22;
                min = i + 120;
            } else if (i < 0) {
                hour = 23;
                min = i + 60;
            } else if (i >= 60) {
                hour = 1;
                min = i - 60;
            } else {
                hour = 0;
                min = i;
            }
            assertEquals(t.getHourOfDay().getValue(), hour);
            assertEquals(t.getMinuteOfHour().getValue(), min);
        }
    }

    public void test_plusMinutes_noChange() {
        LocalTime t = TEST_12_30_40_987654321.plusMinutes(0);
        assertSame(t, TEST_12_30_40_987654321);
    }

    public void test_plusMinutes_noChange_oneDay() {
        LocalTime t = TEST_12_30_40_987654321.plusMinutes(24 * 60);
        assertSame(t, TEST_12_30_40_987654321);
    }

    public void test_plusMinutes_toMidnight() {
        LocalTime t = LocalTime.time(23, 59).plusMinutes(1);
        assertSame(t, LocalTime.MIDNIGHT);
    }

    public void test_plusMinutes_toMidday() {
        LocalTime t = LocalTime.time(11, 59).plusMinutes(1);
        assertSame(t, LocalTime.MIDDAY);
    }

    //-----------------------------------------------------------------------
    // toLocalTime()
    //-----------------------------------------------------------------------
    public void test_toLocalTime() {
        assertSame(TEST_12_30_40_987654321.toLocalTime(), TEST_12_30_40_987654321);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_comparisons() {
        doTest_comparisons_LocalTime(
            LocalTime.MIDNIGHT,
            LocalTime.time(0, 0, 0, 999999999),
            LocalTime.time(0, 0, 59, 0),
            LocalTime.time(0, 0, 59, 999999999),
            LocalTime.time(0, 59, 0, 0),
            LocalTime.time(0, 59, 0, 999999999),
            LocalTime.time(0, 59, 59, 0),
            LocalTime.time(0, 59, 59, 999999999),
            LocalTime.MIDDAY,
            LocalTime.time(12, 0, 0, 999999999),
            LocalTime.time(12, 0, 59, 0),
            LocalTime.time(12, 0, 59, 999999999),
            LocalTime.time(12, 59, 0, 0),
            LocalTime.time(12, 59, 0, 999999999),
            LocalTime.time(12, 59, 59, 0),
            LocalTime.time(12, 59, 59, 999999999),
            LocalTime.time(23, 0, 0, 0),
            LocalTime.time(23, 0, 0, 999999999),
            LocalTime.time(23, 0, 59, 0),
            LocalTime.time(23, 0, 59, 999999999),
            LocalTime.time(23, 59, 0, 0),
            LocalTime.time(23, 59, 0, 999999999),
            LocalTime.time(23, 59, 59, 0),
            LocalTime.time(23, 59, 59, 999999999)
        );
    }

    void doTest_comparisons_LocalTime(LocalTime... localTimes) {
        for (int i = 0; i < localTimes.length; i++) {
            LocalTime a = localTimes[i];
            for (int j = 0; j < localTimes.length; j++) {
                LocalTime b = localTimes[j];
                if (i < j) {
                    assertTrue(a.compareTo(b) < 0, a + " <=> " + b);
                    assertEquals(a.isBefore(b), true, a + " <=> " + b);
                    assertEquals(a.isAfter(b), false, a + " <=> " + b);
                    assertEquals(a.equals(b), false, a + " <=> " + b);
                } else if (i > j) {
                    assertTrue(a.compareTo(b) > 0, a + " <=> " + b);
                    assertEquals(a.isBefore(b), false, a + " <=> " + b);
                    assertEquals(a.isAfter(b), true, a + " <=> " + b);
                    assertEquals(a.equals(b), false, a + " <=> " + b);
                } else {
                    assertEquals(a.compareTo(b), 0, a + " <=> " + b);
                    assertEquals(a.isBefore(b), false, a + " <=> " + b);
                    assertEquals(a.isAfter(b), false, a + " <=> " + b);
                    assertEquals(a.equals(b), true, a + " <=> " + b);
                }
            }
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_compareTo_ObjectNull() {
        TEST_12_30_40_987654321.compareTo(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isBefore_ObjectNull() {
        TEST_12_30_40_987654321.isBefore(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isAfter_ObjectNull() {
        TEST_12_30_40_987654321.isAfter(null);
    }

    @Test(expectedExceptions=ClassCastException.class)
    @SuppressWarnings("unchecked")
    public void compareToNonLocalTime() {
       Comparable c = TEST_12_30_40_987654321;
       c.compareTo(new Object());
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes")
    public void test_equals_true(int h, int m, int s, int n) {
        LocalTime a = LocalTime.time(h, m, s, n);
        LocalTime b = LocalTime.time(h, m, s, n);
        assertEquals(a.equals(b), true);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_hour_differs(int h, int m, int s, int n) {
        LocalTime a = LocalTime.time(h, m, s, n);
        LocalTime b = LocalTime.time(h + 1, m, s, n);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_minute_differs(int h, int m, int s, int n) {
        LocalTime a = LocalTime.time(h, m, s, n);
        LocalTime b = LocalTime.time(h, m + 1, s, n);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_second_differs(int h, int m, int s, int n) {
        LocalTime a = LocalTime.time(h, m, s, n);
        LocalTime b = LocalTime.time(h, m, s + 1, n);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_nano_differs(int h, int m, int s, int n) {
        LocalTime a = LocalTime.time(h, m, s, n);
        LocalTime b = LocalTime.time(h, m, s, n + 1);
        assertEquals(a.equals(b), false);
    }

    public void test_equals_itself_true() {
        assertEquals(TEST_12_30_40_987654321.equals(TEST_12_30_40_987654321), true);
    }

    public void test_equals_string_false() {
        assertEquals(TEST_12_30_40_987654321.equals("2007-07-15"), false);
    }

    public void test_equals_null_false() {
        assertEquals(TEST_12_30_40_987654321.equals(null), false);
    }

    //-----------------------------------------------------------------------
    // hashCode()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes")
    public void test_hashCode_same(int h, int m, int s, int n) {
        LocalTime a = LocalTime.time(h, m, s, n);
        LocalTime b = LocalTime.time(h, m, s, n);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test(dataProvider="sampleTimes")
    public void test_hashCode_hour_differs(int h, int m, int s, int n) {
        LocalTime a = LocalTime.time(h, m, s, n);
        LocalTime b = LocalTime.time(h + 1, m, s, n);
        assertEquals(a.hashCode() == b.hashCode(), false);
    }

    @Test(dataProvider="sampleTimes")
    public void test_hashCode_minute_differs(int h, int m, int s, int n) {
        LocalTime a = LocalTime.time(h, m, s, n);
        LocalTime b = LocalTime.time(h, m + 1, s, n);
        assertEquals(a.hashCode() == b.hashCode(), false);
    }

    @Test(dataProvider="sampleTimes")
    public void test_hashCode_second_differs(int h, int m, int s, int n) {
        LocalTime a = LocalTime.time(h, m, s, n);
        LocalTime b = LocalTime.time(h, m, s + 1, n);
        assertEquals(a.hashCode() == b.hashCode(), false);
    }

    @Test(dataProvider="sampleTimes")
    public void test_hashCode_nano_differs(int h, int m, int s, int n) {
        LocalTime a = LocalTime.time(h, m, s, n);
        LocalTime b = LocalTime.time(h, m, s, n + 1);
        assertEquals(a.hashCode() == b.hashCode(), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleToString")
    Object[][] provider_sampleToString() {
        return new Object[][] {
            {0, 0, 0, 0, "00:00"},
            {1, 0, 0, 0, "01:00"},
            {23, 0, 0, 0, "23:00"},
            {0, 1, 0, 0, "00:01"},
            {12, 30, 0, 0, "12:30"},
            {23, 59, 0, 0, "23:59"},
            {0, 0, 1, 0, "00:00:01"},
            {0, 0, 59, 0, "00:00:59"},
            {0, 0, 0, 100000000, "00:00:00.100"},
            {0, 0, 0, 10000000, "00:00:00.010"},
            {0, 0, 0, 1000000, "00:00:00.001"},
            {0, 0, 0, 100000, "00:00:00.000100"},
            {0, 0, 0, 10000, "00:00:00.000010"},
            {0, 0, 0, 1000, "00:00:00.000001"},
            {0, 0, 0, 100, "00:00:00.000000100"},
            {0, 0, 0, 10, "00:00:00.000000010"},
            {0, 0, 0, 1, "00:00:00.000000001"},
            {0, 0, 0, 999999999, "00:00:00.999999999"},
            {0, 0, 0, 99999999, "00:00:00.099999999"},
            {0, 0, 0, 9999999, "00:00:00.009999999"},
            {0, 0, 0, 999999, "00:00:00.000999999"},
            {0, 0, 0, 99999, "00:00:00.000099999"},
            {0, 0, 0, 9999, "00:00:00.000009999"},
            {0, 0, 0, 999, "00:00:00.000000999"},
            {0, 0, 0, 99, "00:00:00.000000099"},
            {0, 0, 0, 9, "00:00:00.000000009"},
        };
    }

    @Test(dataProvider="sampleToString")
    public void test_toString(int h, int m, int s, int n, String expected) {
        LocalTime t = LocalTime.time(h, m, s, n);
        String str = t.toString();
        assertEquals(str, expected);
    }

}
