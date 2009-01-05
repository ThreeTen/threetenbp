/*
 * Copyright (c) 2008, Stephen Colebourne & Michael Nascimento Santos
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

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;

import javax.time.CalendricalException;
import javax.time.calendar.field.AmPmOfDay;
import javax.time.calendar.field.DayOfMonth;
import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.DayOfYear;
import javax.time.calendar.field.HourOfDay;
import javax.time.calendar.field.MinuteOfHour;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.NanoOfSecond;
import javax.time.calendar.field.QuarterOfYear;
import javax.time.calendar.field.SecondOfMinute;
import javax.time.calendar.field.WeekOfWeekyear;
import javax.time.calendar.field.Weekyear;
import javax.time.calendar.field.Year;
import javax.time.period.Period;
import javax.time.period.PeriodProvider;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test LocalDateTime.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestLocalDateTime {
    private LocalDateTime TEST_2007_07_15_12_30_40_987654321;

    @BeforeMethod
    public void setUp() {
        TEST_2007_07_15_12_30_40_987654321 = LocalDateTime.dateTime(2007, 7, 15, 12, 30, 40, 987654321);
    }

    //-----------------------------------------------------------------------
    private void check(LocalDateTime dateTime, int y, int m, int d, int h, int mi, int s, int n) {
        assertEquals(dateTime.getYear().getValue(), y);
        assertEquals(dateTime.getMonthOfYear().getValue(), m);
        assertEquals(dateTime.getDayOfMonth().getValue(), d);
        assertEquals(dateTime.getHourOfDay().getValue(), h);
        assertEquals(dateTime.getMinuteOfHour().getValue(), mi);
        assertEquals(dateTime.getSecondOfMinute().getValue(), s);
        assertEquals(dateTime.getNanoOfSecond().getValue(), n);
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(TEST_2007_07_15_12_30_40_987654321 instanceof CalendricalProvider);
        assertTrue(TEST_2007_07_15_12_30_40_987654321 instanceof Serializable);
        assertTrue(TEST_2007_07_15_12_30_40_987654321 instanceof Comparable);
        assertTrue(TEST_2007_07_15_12_30_40_987654321 instanceof DateTimeProvider);
        assertTrue(TEST_2007_07_15_12_30_40_987654321 instanceof DateMatcher);
        assertTrue(TEST_2007_07_15_12_30_40_987654321 instanceof TimeMatcher);
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(TEST_2007_07_15_12_30_40_987654321);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), TEST_2007_07_15_12_30_40_987654321);
    }

    public void test_immutable() {
        Class<LocalDateTime> cls = LocalDateTime.class;
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            assertTrue(Modifier.isPrivate(field.getModifiers()));
            assertTrue(Modifier.isFinal(field.getModifiers()));
        }
    }

    //-----------------------------------------------------------------------
    public void factory_dateMidnight_objects() {
        LocalDateTime dateTime = LocalDateTime.dateMidnight(Year.isoYear(2008), MonthOfYear.FEBRUARY, DayOfMonth.dayOfMonth(29));
        check(dateTime, 2008, 2, 29, 0, 0, 0, 0);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateMidnight_objects_nullYear() {
        LocalDateTime.dateMidnight(null, MonthOfYear.FEBRUARY, DayOfMonth.dayOfMonth(29));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateMidnight_objects_nullMonth() {
        LocalDateTime.dateMidnight(Year.isoYear(2008), null, DayOfMonth.dayOfMonth(29));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateMidnight_objects_nullDay() {
        LocalDateTime.dateMidnight(Year.isoYear(2008), MonthOfYear.FEBRUARY, null);
    }

    //-----------------------------------------------------------------------
    public void factory_dateMidnight_intsMonth() {
        LocalDateTime dateTime = LocalDateTime.dateMidnight(2008, MonthOfYear.FEBRUARY, 29);
        check(dateTime, 2008, 2, 29, 0, 0, 0, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateMidnight_intsMonth_yearTooLow() {
        LocalDateTime.dateMidnight(Integer.MIN_VALUE, MonthOfYear.FEBRUARY, 29);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateMidnight_intsMonth_nullMonth() {
        LocalDateTime.dateMidnight(2008, null, 29);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateMidnight_intsMonth_dayTooLow() {
        LocalDateTime.dateMidnight(2008, MonthOfYear.FEBRUARY, -1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateMidnight_intsMonth_dayTooHigh() {
        LocalDateTime.dateMidnight(2008, MonthOfYear.MARCH, 32);
    }

    //-----------------------------------------------------------------------
    public void factory_dateMidnight_ints() {
        LocalDateTime dateTime = LocalDateTime.dateMidnight(2008, 2, 29);
        check(dateTime, 2008, 2, 29, 0, 0, 0, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateMidnight_ints_yearTooLow() {
        LocalDateTime.dateMidnight(Integer.MIN_VALUE, 2, 29);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateMidnight_ints_monthTooLow() {
        LocalDateTime.dateMidnight(2008, 0, 29);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateMidnight_ints_monthTooHigh() {
        LocalDateTime.dateMidnight(2008, 13, 29);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateMidnight_ints_dayTooLow() {
        LocalDateTime.dateMidnight(2008, 2, -1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateMidnight_ints_dayTooHigh() {
        LocalDateTime.dateMidnight(2008, 3, 32);
    }

    //-----------------------------------------------------------------------
    public void factory_dateMidnight_DateProvider() {
        LocalDateTime dateTime = LocalDateTime.dateMidnight(LocalDate.date(2008, 2, 29));
        check(dateTime, 2008, 2, 29, 0, 0, 0, 0);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateMidnight_DateProvider_null() {
        LocalDateTime.dateMidnight(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateMidnight_DateProvider_null_toLocalDate() {
        LocalDateTime.dateMidnight(new DateProvider() {
            public LocalDate toLocalDate() {
                return null;
            }

            public Calendrical toCalendrical() {
                return null;
            }
        });
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_5objects() {
        LocalDateTime dateTime = LocalDateTime.dateTime(Year.isoYear(2008), MonthOfYear.FEBRUARY, DayOfMonth.dayOfMonth(29), HourOfDay.
                hourOfDay(12), MinuteOfHour.minuteOfHour(30));
        check(dateTime, 2008, 2, 29, 12, 30, 0, 0);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateTime_5objects_nullYear() {
        LocalDateTime.dateTime(null, MonthOfYear.FEBRUARY, DayOfMonth.dayOfMonth(29), HourOfDay.hourOfDay(12),
                MinuteOfHour.minuteOfHour(30));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateTime_5objects_nullMonth() {
        LocalDateTime.dateTime(Year.isoYear(2008), null, DayOfMonth.dayOfMonth(29), HourOfDay.hourOfDay(12), 
                MinuteOfHour.minuteOfHour(30));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateTime_5objects_nullDay() {
        LocalDateTime.dateTime(Year.isoYear(2008), MonthOfYear.FEBRUARY, null, HourOfDay.hourOfDay(12), MinuteOfHour.minuteOfHour(30));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateTime_5objects_nullHour() {
        LocalDateTime.dateTime(Year.isoYear(2008), MonthOfYear.FEBRUARY, DayOfMonth.dayOfMonth(29), null, MinuteOfHour.minuteOfHour(30));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateTime_5objects_nullMinute() {
        LocalDateTime.dateTime(Year.isoYear(2008), MonthOfYear.FEBRUARY, DayOfMonth.dayOfMonth(29), HourOfDay.hourOfDay(12), null);
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_6objects() {
        LocalDateTime dateTime = LocalDateTime.dateTime(Year.isoYear(2008), MonthOfYear.FEBRUARY, DayOfMonth.dayOfMonth(29), HourOfDay.
                hourOfDay(12), MinuteOfHour.minuteOfHour(30), SecondOfMinute.secondOfMinute(40));
        check(dateTime, 2008, 2, 29, 12, 30, 40, 0);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateTime_6objects_nullYear() {
        LocalDateTime.dateTime(null, MonthOfYear.FEBRUARY, DayOfMonth.dayOfMonth(29), HourOfDay.hourOfDay(12),
                MinuteOfHour.minuteOfHour(30), SecondOfMinute.secondOfMinute(40));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateTime_6objects_nullMonth() {
        LocalDateTime.dateTime(Year.isoYear(2008), null, DayOfMonth.dayOfMonth(29), HourOfDay.hourOfDay(12), 
                MinuteOfHour.minuteOfHour(30), SecondOfMinute.secondOfMinute(40));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateTime_6objects_nullDay() {
        LocalDateTime.dateTime(Year.isoYear(2008), MonthOfYear.FEBRUARY, null, HourOfDay.hourOfDay(12), MinuteOfHour.minuteOfHour(30),
                SecondOfMinute.secondOfMinute(40));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateTime_6objects_nullHour() {
        LocalDateTime.dateTime(Year.isoYear(2008), MonthOfYear.FEBRUARY, DayOfMonth.dayOfMonth(29), null, MinuteOfHour.minuteOfHour(30),
                SecondOfMinute.secondOfMinute(40));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateTime_6objects_nullMinute() {
        LocalDateTime.dateTime(Year.isoYear(2008), MonthOfYear.FEBRUARY, DayOfMonth.dayOfMonth(29), HourOfDay.hourOfDay(12), null,
                SecondOfMinute.secondOfMinute(40));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateTime_6objects_nullSecond() {
        LocalDateTime.dateTime(Year.isoYear(2008), MonthOfYear.FEBRUARY, DayOfMonth.dayOfMonth(29), HourOfDay.hourOfDay(12), 
                MinuteOfHour.minuteOfHour(30), null);
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_7objects() {
        LocalDateTime dateTime = LocalDateTime.dateTime(Year.isoYear(2008), MonthOfYear.FEBRUARY, DayOfMonth.dayOfMonth(29), HourOfDay.
                hourOfDay(12), MinuteOfHour.minuteOfHour(30), SecondOfMinute.secondOfMinute(40), NanoOfSecond.nanoOfSecond(987654321));
        check(dateTime, 2008, 2, 29, 12, 30, 40, 987654321);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateTime_7objects_nullYear() {
        LocalDateTime.dateTime(null, MonthOfYear.FEBRUARY, DayOfMonth.dayOfMonth(29), HourOfDay.hourOfDay(12),
                MinuteOfHour.minuteOfHour(30), SecondOfMinute.secondOfMinute(40), NanoOfSecond.nanoOfSecond(987654321));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateTime_7objects_nullMonth() {
        LocalDateTime.dateTime(Year.isoYear(2008), null, DayOfMonth.dayOfMonth(29), HourOfDay.hourOfDay(12), 
                MinuteOfHour.minuteOfHour(30), SecondOfMinute.secondOfMinute(40), NanoOfSecond.nanoOfSecond(987654321));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateTime_7objects_nullDay() {
        LocalDateTime.dateTime(Year.isoYear(2008), MonthOfYear.FEBRUARY, null, HourOfDay.hourOfDay(12), MinuteOfHour.minuteOfHour(30),
                SecondOfMinute.secondOfMinute(40), NanoOfSecond.nanoOfSecond(987654321));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateTime_7objects_nullHour() {
        LocalDateTime.dateTime(Year.isoYear(2008), MonthOfYear.FEBRUARY, DayOfMonth.dayOfMonth(29), null, MinuteOfHour.minuteOfHour(30),
                SecondOfMinute.secondOfMinute(40), NanoOfSecond.nanoOfSecond(987654321));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateTime_7objects_nullMinute() {
        LocalDateTime.dateTime(Year.isoYear(2008), MonthOfYear.FEBRUARY, DayOfMonth.dayOfMonth(29), HourOfDay.hourOfDay(12), null,
                SecondOfMinute.secondOfMinute(40), NanoOfSecond.nanoOfSecond(987654321));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateTime_7objects_nullSecond() {
        LocalDateTime.dateTime(Year.isoYear(2008), MonthOfYear.FEBRUARY, DayOfMonth.dayOfMonth(29), HourOfDay.hourOfDay(12), 
                MinuteOfHour.minuteOfHour(30), null, NanoOfSecond.nanoOfSecond(987654321));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateTime_7objects_nullNano() {
        LocalDateTime.dateTime(Year.isoYear(2008), MonthOfYear.FEBRUARY, DayOfMonth.dayOfMonth(29), HourOfDay.hourOfDay(12), 
                MinuteOfHour.minuteOfHour(30), SecondOfMinute.secondOfMinute(40), null);
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_4intsMonth() {
        LocalDateTime dateTime = LocalDateTime.dateTime(2007, MonthOfYear.JULY, 15, 12, 30);
        check(dateTime, 2007, 7, 15, 12, 30, 0, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_4intsMonth_yearTooLow() {
        LocalDateTime.dateTime(Integer.MIN_VALUE, MonthOfYear.JULY, 15, 12, 30);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateTime_4intsMonth_nullMonth() {
        LocalDateTime.dateTime(2007, null, 15, 12, 30);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_4intsMonth_dayTooLow() {
        LocalDateTime.dateTime(2007, MonthOfYear.JULY, -1, 12, 30);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_4intsMonth_dayTooHigh() {
        LocalDateTime.dateTime(2007, MonthOfYear.JULY, 32, 12, 30);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_4intsMonth_hourTooLow() {
        LocalDateTime.dateTime(2007, MonthOfYear.JULY, 15, -1, 30);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_4intsMonth_hourTooHigh() {
        LocalDateTime.dateTime(2007, MonthOfYear.JULY, 15, 24, 30);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_4intsMonth_minuteTooLow() {
        LocalDateTime.dateTime(2007, MonthOfYear.JULY, 15, 12, -1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_4intsMonth_minuteTooHigh() {
        LocalDateTime.dateTime(2007, MonthOfYear.JULY, 15, 12, 60);
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_5intsMonth() {
        LocalDateTime dateTime = LocalDateTime.dateTime(2007, MonthOfYear.JULY, 15, 12, 30, 40);
        check(dateTime, 2007, 7, 15, 12, 30, 40, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_5intsMonth_yearTooLow() {
        LocalDateTime.dateTime(Integer.MIN_VALUE, MonthOfYear.JULY, 15, 12, 30, 40);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateTime_5intsMonth_nullMonth() {
        LocalDateTime.dateTime(2007, null, 15, 12, 30, 40);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_5intsMonth_dayTooLow() {
        LocalDateTime.dateTime(2007, MonthOfYear.JULY, -1, 12, 30, 40);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_5intsMonth_dayTooHigh() {
        LocalDateTime.dateTime(2007, MonthOfYear.JULY, 32, 12, 30, 40);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_5intsMonth_hourTooLow() {
        LocalDateTime.dateTime(2007, MonthOfYear.JULY, 15, -1, 30, 40);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_5intsMonth_hourTooHigh() {
        LocalDateTime.dateTime(2007, MonthOfYear.JULY, 15, 24, 30, 40);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_5intsMonth_minuteTooLow() {
        LocalDateTime.dateTime(2007, MonthOfYear.JULY, 15, 12, -1, 40);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_5intsMonth_minuteTooHigh() {
        LocalDateTime.dateTime(2007, MonthOfYear.JULY, 15, 12, 60, 40);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_5intsMonth_secondTooLow() {
        LocalDateTime.dateTime(2007, MonthOfYear.JULY, 15, 12, 30, -1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_5intsMonth_secondTooHigh() {
        LocalDateTime.dateTime(2007, MonthOfYear.JULY, 15, 12, 30, 60);
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_6intsMonth() {
        LocalDateTime dateTime = LocalDateTime.dateTime(2007, MonthOfYear.JULY, 15, 12, 30, 40, 987654321);
        check(dateTime, 2007, 7, 15, 12, 30, 40, 987654321);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_6intsMonth_yearTooLow() {
        LocalDateTime.dateTime(Integer.MIN_VALUE, MonthOfYear.JULY, 15, 12, 30, 40, 987654321);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateTime_6intsMonth_nullMonth() {
        LocalDateTime.dateTime(2007, null, 15, 12, 30, 40, 987654321);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_6intsMonth_dayTooLow() {
        LocalDateTime.dateTime(2007, MonthOfYear.JULY, -1, 12, 30, 40, 987654321);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_6intsMonth_dayTooHigh() {
        LocalDateTime.dateTime(2007, MonthOfYear.JULY, 32, 12, 30, 40, 987654321);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_6intsMonth_hourTooLow() {
        LocalDateTime.dateTime(2007, MonthOfYear.JULY, 15, -1, 30, 40, 987654321);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_6intsMonth_hourTooHigh() {
        LocalDateTime.dateTime(2007, MonthOfYear.JULY, 15, 24, 30, 40, 987654321);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_6intsMonth_minuteTooLow() {
        LocalDateTime.dateTime(2007, MonthOfYear.JULY, 15, 12, -1, 40, 987654321);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_6intsMonth_minuteTooHigh() {
        LocalDateTime.dateTime(2007, MonthOfYear.JULY, 15, 12, 60, 40, 987654321);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_6intsMonth_secondTooLow() {
        LocalDateTime.dateTime(2007, MonthOfYear.JULY, 15, 12, 30, -1, 987654321);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_6intsMonth_secondTooHigh() {
        LocalDateTime.dateTime(2007, MonthOfYear.JULY, 15, 12, 30, 60, 987654321);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_6intsMonth_nanoTooLow() {
        LocalDateTime.dateTime(2007, MonthOfYear.JULY, 15, 12, 30, 40, -1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_6intsMonth_nanoTooHigh() {
        LocalDateTime.dateTime(2007, MonthOfYear.JULY, 15, 12, 30, 40, 1000000000);
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_5ints() {
        LocalDateTime dateTime = LocalDateTime.dateTime(2007, 7, 15, 12, 30);
        check(dateTime, 2007, 7, 15, 12, 30, 0, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_5ints_yearTooLow() {
        LocalDateTime.dateTime(Integer.MIN_VALUE, 7, 15, 12, 30);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_5ints_monthTooLow() {
        LocalDateTime.dateTime(2007, 0, 15, 12, 30);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_5ints_monthTooHigh() {
        LocalDateTime.dateTime(2007, 13, 15, 12, 30);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_5ints_dayTooLow() {
        LocalDateTime.dateTime(2007, 7, -1, 12, 30);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_5ints_dayTooHigh() {
        LocalDateTime.dateTime(2007, 7, 32, 12, 30);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_5ints_hourTooLow() {
        LocalDateTime.dateTime(2007, 7, 15, -1, 30);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_5ints_hourTooHigh() {
        LocalDateTime.dateTime(2007, 7, 15, 24, 30);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_5ints_minuteTooLow() {
        LocalDateTime.dateTime(2007, 7, 15, 12, -1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_5ints_minuteTooHigh() {
        LocalDateTime.dateTime(2007, 7, 15, 12, 60);
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_6ints() {
        LocalDateTime dateTime = LocalDateTime.dateTime(2007, 7, 15, 12, 30, 40);
        check(dateTime, 2007, 7, 15, 12, 30, 40, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_6ints_yearTooLow() {
        LocalDateTime.dateTime(Integer.MIN_VALUE, 7, 15, 12, 30, 40);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_6ints_monthTooLow() {
        LocalDateTime.dateTime(2007, 0, 15, 12, 30, 40);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_6ints_monthTooHigh() {
        LocalDateTime.dateTime(2007, 13, 15, 12, 30, 40);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_6ints_dayTooLow() {
        LocalDateTime.dateTime(2007, 7, -1, 12, 30, 40);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_6ints_dayTooHigh() {
        LocalDateTime.dateTime(2007, 7, 32, 12, 30, 40);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_6ints_hourTooLow() {
        LocalDateTime.dateTime(2007, 7, 15, -1, 30, 40);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_6ints_hourTooHigh() {
        LocalDateTime.dateTime(2007, 7, 15, 24, 30, 40);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_6ints_minuteTooLow() {
        LocalDateTime.dateTime(2007, 7, 15, 12, -1, 40);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_6ints_minuteTooHigh() {
        LocalDateTime.dateTime(2007, 7, 15, 12, 60, 40);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_6ints_secondTooLow() {
        LocalDateTime.dateTime(2007, 7, 15, 12, 30, -1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_6ints_secondTooHigh() {
        LocalDateTime.dateTime(2007, 7, 15, 12, 30, 60);
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_7ints() {
        LocalDateTime dateTime = LocalDateTime.dateTime(2007, 7, 15, 12, 30, 40, 987654321);
        check(dateTime, 2007, 7, 15, 12, 30, 40, 987654321);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_7ints_yearTooLow() {
        LocalDateTime.dateTime(Integer.MIN_VALUE, 7, 15, 12, 30, 40, 987654321);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_7ints_monthTooLow() {
        LocalDateTime.dateTime(2007, 0, 15, 12, 30, 40, 987654321);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_7ints_monthTooHigh() {
        LocalDateTime.dateTime(2007, 13, 15, 12, 30, 40, 987654321);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_7ints_dayTooLow() {
        LocalDateTime.dateTime(2007, 7, -1, 12, 30, 40, 987654321);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_7ints_dayTooHigh() {
        LocalDateTime.dateTime(2007, 7, 32, 12, 30, 40, 987654321);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_7ints_hourTooLow() {
        LocalDateTime.dateTime(2007, 7, 15, -1, 30, 40, 987654321);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_7ints_hourTooHigh() {
        LocalDateTime.dateTime(2007, 7, 15, 24, 30, 40, 987654321);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_7ints_minuteTooLow() {
        LocalDateTime.dateTime(2007, 7, 15, 12, -1, 40, 987654321);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_7ints_minuteTooHigh() {
        LocalDateTime.dateTime(2007, 7, 15, 12, 60, 40, 987654321);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_7ints_secondTooLow() {
        LocalDateTime.dateTime(2007, 7, 15, 12, 30, -1, 987654321);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_7ints_secondTooHigh() {
        LocalDateTime.dateTime(2007, 7, 15, 12, 30, 60, 987654321);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_7ints_nanoTooLow() {
        LocalDateTime.dateTime(2007, 7, 15, 12, 30, 40, -1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateTime_7ints_nanoTooHigh() {
        LocalDateTime.dateTime(2007, 7, 15, 12, 30, 40, 1000000000);
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_DateProvider_TimeProvider() {
        LocalDateTime dateTime = LocalDateTime.dateTime(LocalDate.date(2007, 7, 15), LocalTime.time(12, 30, 40, 987654321));
        check(dateTime, 2007, 7, 15, 12, 30, 40, 987654321);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateTime_DateProvider_TimeProvider_nullDateProvider() {
        LocalDateTime.dateTime(null, LocalTime.time(12, 30, 40, 987654321));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateTime_DateProvider_TimeProvider_null_toLocalDate() {
        LocalDateTime.dateTime(new DateProvider() {
            public LocalDate toLocalDate() {
                return null;
            }

            public Calendrical toCalendrical() {
                return null;
            }
        }, LocalTime.time(12, 30, 40, 987654321));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateTime_DateProvider_TimeProvider_nullTimeProvider() {
        LocalDateTime.dateTime(LocalDate.date(2007, 7, 15), null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateTime_DateProvider_TimeProvider_null_toLocalTime() {
        LocalDateTime.dateTime(LocalDate.date(2007, 7, 15), new TimeProvider() {
            public LocalTime toLocalTime() {
                return null;
            }

            public Calendrical toCalendrical() {
                return null;
            }
        });
    }

    //-----------------------------------------------------------------------
    public void test_getChronology() {
        assertSame(ISOChronology.INSTANCE, TEST_2007_07_15_12_30_40_987654321.getChronology());
    }

    //-----------------------------------------------------------------------
    public void test_isSupported() {
        assertTrue(TEST_2007_07_15_12_30_40_987654321.isSupported(ISOChronology.yearRule()));
        assertTrue(TEST_2007_07_15_12_30_40_987654321.isSupported(ISOChronology.quarterOfYearRule()));
        assertTrue(TEST_2007_07_15_12_30_40_987654321.isSupported(ISOChronology.monthOfYearRule()));
        assertTrue(TEST_2007_07_15_12_30_40_987654321.isSupported(ISOChronology.monthOfQuarterRule()));
        assertTrue(TEST_2007_07_15_12_30_40_987654321.isSupported(ISOChronology.dayOfMonthRule()));
        assertTrue(TEST_2007_07_15_12_30_40_987654321.isSupported(ISOChronology.dayOfWeekRule()));
        assertTrue(TEST_2007_07_15_12_30_40_987654321.isSupported(ISOChronology.dayOfYearRule()));
        assertTrue(TEST_2007_07_15_12_30_40_987654321.isSupported(ISOChronology.weekOfMonthRule()));
        assertTrue(TEST_2007_07_15_12_30_40_987654321.isSupported(ISOChronology.weekOfWeekyearRule()));
        assertTrue(TEST_2007_07_15_12_30_40_987654321.isSupported(ISOChronology.weekyearRule()));
        
        assertTrue(TEST_2007_07_15_12_30_40_987654321.isSupported(ISOChronology.hourOfDayRule()));
        assertTrue(TEST_2007_07_15_12_30_40_987654321.isSupported(ISOChronology.minuteOfHourRule()));
        assertTrue(TEST_2007_07_15_12_30_40_987654321.isSupported(ISOChronology.secondOfMinuteRule()));
        assertTrue(TEST_2007_07_15_12_30_40_987654321.isSupported(ISOChronology.nanoOfSecondRule()));
        assertTrue(TEST_2007_07_15_12_30_40_987654321.isSupported(ISOChronology.hourOfAmPmRule()));
        assertTrue(TEST_2007_07_15_12_30_40_987654321.isSupported(ISOChronology.amPmOfDayRule()));
        
        assertFalse(TEST_2007_07_15_12_30_40_987654321.isSupported(null));
    }

    public void test_get() {
        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(ISOChronology.yearRule()), 2007);
        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(ISOChronology.quarterOfYearRule()), 3);
        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(ISOChronology.monthOfYearRule()), 7);
        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(ISOChronology.monthOfQuarterRule()), TEST_2007_07_15_12_30_40_987654321.getMonthOfYear().getMonthOfQuarter());
        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(ISOChronology.dayOfMonthRule()), 15);
        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(ISOChronology.dayOfWeekRule()), TEST_2007_07_15_12_30_40_987654321.getDayOfWeek().getValue());
        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(ISOChronology.dayOfYearRule()), TEST_2007_07_15_12_30_40_987654321.getDayOfYear().getValue());
        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(ISOChronology.weekOfWeekyearRule()), WeekOfWeekyear.weekOfWeekyear(TEST_2007_07_15_12_30_40_987654321).getValue());
        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(ISOChronology.weekyearRule()), Weekyear.weekyear(TEST_2007_07_15_12_30_40_987654321).getValue());

        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(ISOChronology.hourOfDayRule()), 12);
        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(ISOChronology.minuteOfHourRule()), 30);
        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(ISOChronology.secondOfMinuteRule()), 40);
        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(ISOChronology.nanoOfSecondRule()), 987654321);
        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(ISOChronology.hourOfAmPmRule()), 0);
        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(ISOChronology.amPmOfDayRule()), AmPmOfDay.PM.getValue());
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="sampleDates")
    Object[][] provider_sampleDates() {
        return new Object[][] {
            {2008, 7, 5},
            {2007, 7, 5},
            {2006, 7, 5},
            {2005, 7, 5},
            {2004, 1, 1},
            {-1, 1, 2},
        };
    }

    @DataProvider(name="sampleTimes")
    Object[][] provider_sampleTimes() {
        return new Object[][] {
            {0, 0, 0, 0},
            {0, 0, 0, 1},
            {0, 0, 1, 0},
            {0, 0, 1, 1},
            {0, 1, 0, 0},
            {0, 1, 0, 1},
            {0, 1, 1, 0},
            {0, 1, 1, 1},
            {1, 0, 0, 0},
            {1, 0, 0, 1},
            {1, 0, 1, 0},
            {1, 0, 1, 1},
            {1, 1, 0, 0},
            {1, 1, 0, 1},
            {1, 1, 1, 0},
            {1, 1, 1, 1},
        };
    }

    //-----------------------------------------------------------------------
    // get*()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_getYearMonth(int y, int m, int d) {
        assertEquals(LocalDateTime.dateTime(y, m, d, 12, 30).getYearMonth(), YearMonth.yearMonth(y, m));
    }

    @Test(dataProvider="sampleDates")
    public void test_getMonthDay(int y, int m, int d) {
        assertEquals(LocalDateTime.dateTime(y, m, d, 12, 30).getMonthDay(), MonthDay.monthDay(m, d));
    }

    @Test(dataProvider="sampleDates")
    public void test_get(int y, int m, int d) {
        LocalDateTime a = LocalDateTime.dateTime(y, m, d, 12, 30);
        assertEquals(a.getYear(), Year.isoYear(y));
        assertEquals(a.getMonthOfYear(), MonthOfYear.monthOfYear(m));
        assertEquals(a.getDayOfMonth(), DayOfMonth.dayOfMonth(d));
    }

    @Test(dataProvider="sampleDates")
    public void test_getDOY(int y, int m, int d) {
        Year year = Year.isoYear(y);
        LocalDateTime a = LocalDateTime.dateTime(y, m, d, 12 ,30);
        int total = 0;
        for (int i = 1; i < m; i++) {
            total += MonthOfYear.monthOfYear(i).lengthInDays(year);
        }
        int doy = total + d;
        assertEquals(a.getDayOfYear(), DayOfYear.dayOfYear(doy));
    }

    @Test(dataProvider="sampleTimes")
    public void test_get(int h, int m, int s, int ns) {
        LocalDateTime a = LocalDateTime.dateTime(TEST_2007_07_15_12_30_40_987654321, LocalTime.time(h, m, s, ns));
        assertEquals(a.getHourOfDay(), HourOfDay.hourOfDay(h));
        assertEquals(a.getMinuteOfHour(), MinuteOfHour.minuteOfHour(m));
        assertEquals(a.getSecondOfMinute(), SecondOfMinute.secondOfMinute(s));
        assertEquals(a.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(ns));
    }

    //-----------------------------------------------------------------------
    // getDate()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_getDate(int year, int month, int day) {
        LocalDate d = LocalDate.date(year, month, day);
        LocalDateTime dt = LocalDateTime.dateTime(year, month, day, 12, 30);
        assertEquals(dt.getDate(), d);
    }

    //-----------------------------------------------------------------------
    // getTime()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes")
    public void test_getTime(int h, int m, int s, int ns) {
        LocalTime t = LocalTime.time(h, m, s, ns);
        LocalDateTime dt = LocalDateTime.dateTime(2007, 7, 15, h, m, s, ns);
        assertEquals(dt.getTime(), t);
    }

    //-----------------------------------------------------------------------
    // getDayOfWeek()
    //-----------------------------------------------------------------------
    public void test_getDayOfWeek() {
        DayOfWeek dow = DayOfWeek.MONDAY;
        Year year = Year.isoYear(2007);

        for (MonthOfYear month : MonthOfYear.values()) {
            int length = month.lengthInDays(year);
            for (int i = 1; i <= length; i++) {
                LocalDateTime d = LocalDateTime.dateTime(LocalDate.date(year, month, DayOfMonth.dayOfMonth(i)),
                        TEST_2007_07_15_12_30_40_987654321);
                assertSame(d.getDayOfWeek(), dow);
                dow = dow.next();
            }
        }
    }

    //-----------------------------------------------------------------------
    // with()
    //-----------------------------------------------------------------------
    public void test_with_DateAdjuster() {
        DateAdjuster dateAdjuster = DateAdjusters.lastDayOfMonth();
        LocalDateTime adjusted = TEST_2007_07_15_12_30_40_987654321.with(dateAdjuster);
        assertEquals(adjusted.toLocalDate(), dateAdjuster.adjustDate(TEST_2007_07_15_12_30_40_987654321.toLocalDate()));
        assertSame(adjusted.toLocalTime(), TEST_2007_07_15_12_30_40_987654321.toLocalTime());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_with_null_adjustDate() {
        TEST_2007_07_15_12_30_40_987654321.with(new MockDateAdjusterReturnsNull());
    }

    public void test_with_TimeAdjuster() {
        TimeAdjuster timeAdjuster = AmPmOfDay.AM;
        LocalDateTime adjusted = TEST_2007_07_15_12_30_40_987654321.with(timeAdjuster);
        assertSame(adjusted.toLocalDate(), TEST_2007_07_15_12_30_40_987654321.toLocalDate());
        assertSame(adjusted.toLocalTime().getHourOfDay().getValue(), 0);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_with_null_adjustTime() {
        TEST_2007_07_15_12_30_40_987654321.with(new MockTimeAdjusterReturnsNull());
    }

    //-----------------------------------------------------------------------
    // withYear()
    //-----------------------------------------------------------------------
    public void test_withYear_int_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withYear(2008);
        check(t, 2008, 7, 15, 12, 30, 40, 987654321);
    }

    public void test_withYear_int_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withYear(2007);
        assertSame(t.toLocalDate(), TEST_2007_07_15_12_30_40_987654321.toLocalDate());
        assertSame(t.toLocalTime(), TEST_2007_07_15_12_30_40_987654321.toLocalTime());
    }
    
    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withYear_int_invalid() {
        TEST_2007_07_15_12_30_40_987654321.withYear(Year.MIN_YEAR - 1);
    }

    public void test_withYear_int_adjustDay() {
        LocalDateTime t = LocalDateTime.dateTime(2008, 2, 29, 12, 30).withYear(2007);
        LocalDateTime expected = LocalDateTime.dateTime(2007, 2, 28, 12, 30);
        assertEquals(t, expected);
    }

    public void test_withYear_int_DateResolver_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withYear(2008, DateResolvers.strict());
        check(t, 2008, 7, 15, 12, 30, 40, 987654321);
    }

    public void test_withYear_int_DateResolver_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withYear(2007, DateResolvers.strict());
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }
    
    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withYear_int_DateResolver_invalid() {
        TEST_2007_07_15_12_30_40_987654321.withYear(Year.MIN_YEAR - 1, DateResolvers.nextValid());
    }

    public void test_withYear_int_DateResolver_adjustDay() {
        LocalDateTime t = LocalDateTime.dateTime(2008, 2, 29, 12, 30).withYear(2007, DateResolvers.nextValid());
        LocalDateTime expected = LocalDateTime.dateTime(2007, 3, 1, 12, 30);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_withYear_int_DateResolver_null_adjustDay() {
        TEST_2007_07_15_12_30_40_987654321.withYear(2008, new MockDateResolverReturnsNull());
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_withYear_int_DateResolver_adjustDay_invalid() {
        LocalDateTime.dateTime(2008, 2, 29, 12, 30).withYear(2007, DateResolvers.strict());
    }

    //-----------------------------------------------------------------------
    // withMonthOfYear()
    //-----------------------------------------------------------------------
    public void test_withMonthOfYear_int_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withMonthOfYear(1);
        check(t, 2007, 1, 15, 12, 30, 40, 987654321);
    }

    public void test_withMonthOfYear_int_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withMonthOfYear(7);
        assertSame(t.toLocalDate(), TEST_2007_07_15_12_30_40_987654321.toLocalDate());
        assertSame(t.toLocalTime(), TEST_2007_07_15_12_30_40_987654321.toLocalTime());
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withMonthOfYear_int_invalid() {
        TEST_2007_07_15_12_30_40_987654321.withMonthOfYear(13);
    }

    public void test_withMonthOfYear_int_adjustDay() {
        LocalDateTime t = LocalDateTime.dateTime(2007, 12, 31, 12, 30).withMonthOfYear(11);
        LocalDateTime expected = LocalDateTime.dateTime(2007, 11, 30, 12, 30);
        assertEquals(t, expected);
    }

    public void test_withMonthOfYear_int_DateResolver_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withMonthOfYear(1, DateResolvers.strict());
        check(t, 2007, 1, 15, 12, 30, 40, 987654321);
    }

    public void test_withMonthOfYear_int_DateResolver_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withMonthOfYear(7, DateResolvers.strict());
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withMonthOfYear_int_DateResolver_invalid() {
        TEST_2007_07_15_12_30_40_987654321.withMonthOfYear(13, DateResolvers.nextValid());
    }

    public void test_withMonthOfYear_int_DateResolver_adjustDay() {
        LocalDateTime t = LocalDateTime.dateTime(2007, 12, 31, 12, 30).withMonthOfYear(11, DateResolvers.nextValid());
        LocalDateTime expected = LocalDateTime.dateTime(2007, 12, 1, 12, 30);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_withMonthOfYear_int_DateResolver_null_adjustDay() {
        TEST_2007_07_15_12_30_40_987654321.withMonthOfYear(1, new MockDateResolverReturnsNull());
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_withMonthOfYear_int_DateResolver_adjustDay_invalid() {
        LocalDateTime.dateTime(2007, 12, 31, 12, 30).withMonthOfYear(11, DateResolvers.strict());
    }

    //-----------------------------------------------------------------------
    // withDayOfMonth()
    //-----------------------------------------------------------------------
    public void test_withDayOfMonth_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withDayOfMonth(1);
        check(t, 2007, 7, 1, 12, 30, 40, 987654321);
    }

    public void test_withDayOfMonth_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withDayOfMonth(15);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withDayOfMonth_invalid() {
        LocalDateTime.dateTime(2007, 11, 30, 12, 30).withDayOfMonth(32);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_withDayOfMonth_invalidCombination() {
        LocalDateTime.dateTime(2007, 11, 30, 12, 30).withDayOfMonth(31);
    }

    //-----------------------------------------------------------------------
    // withDate()
    //-----------------------------------------------------------------------
    public void test_withDate() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withDate(2008, 2, 29);
        check(t, 2008, 2, 29, 12, 30, 40, 987654321);
    }

    public void test_withDate_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withDate(2007, 7, 15);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    public void test_withDate_sameYear() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withDate(2007, 6, 14);
        check(t, 2007, 6, 14, 12, 30, 40, 987654321);
    }

    public void test_withDate_sameMonth() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withDate(2006, 7, 14);
        check(t, 2006, 7, 14, 12, 30, 40, 987654321);
    }

    public void test_withDate_sameDay() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withDate(2006, 6, 15);
        check(t, 2006, 6, 15, 12, 30, 40, 987654321);
    }

    public void test_withDate_dayChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withDate(2007, 7, 16);
        check(t, 2007, 7, 16, 12, 30, 40, 987654321);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withDate_yearTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withDate(Integer.MIN_VALUE, 2, 29);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withDate_monthTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withDate(2008, 0, 29);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withDate_monthTooHigh() {
        TEST_2007_07_15_12_30_40_987654321.withDate(2008, 13, 29);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withDate_dayTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withDate(2008, 2, -1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withDate_dayTooHigh() {
        TEST_2007_07_15_12_30_40_987654321.withDate(2008, 3, 32);
    }

    //-----------------------------------------------------------------------
    // withHourOfDay()
    //-----------------------------------------------------------------------
    public void test_withHourOfDay_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321;
        for (int i = 0; i < 24; i++) {
            t = t.withHourOfDay(i);
            assertEquals(t.getHourOfDay().getValue(), i);
        }
    }

    public void test_withHourOfDay_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withHourOfDay(12);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    public void test_withHourOfDay_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.time(1, 0)).withHourOfDay(0);
        assertSame(t.toLocalTime(), LocalTime.MIDNIGHT);
    }

    public void test_withHourOfDay_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.time(1, 0)).withHourOfDay(12);
        assertSame(t.toLocalTime(), LocalTime.MIDDAY);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withHourOfDay_hourTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withHourOfDay(-1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withHourOfDay_hourTooHigh() {
        TEST_2007_07_15_12_30_40_987654321.withHourOfDay(24);
    }

    //-----------------------------------------------------------------------
    // withMinuteOfHour()
    //-----------------------------------------------------------------------
    public void test_withMinuteOfHour_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321;
        for (int i = 0; i < 60; i++) {
            t = t.withMinuteOfHour(i);
            assertEquals(t.getMinuteOfHour().getValue(), i);
        }
    }

    public void test_withMinuteOfHour_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withMinuteOfHour(30);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    public void test_withMinuteOfHour_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.time(0, 1)).withMinuteOfHour(0);
        assertSame(t.toLocalTime(), LocalTime.MIDNIGHT);
    }

    public void test_withMinuteOfHour_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.time(12, 1)).withMinuteOfHour(0);
        assertSame(t.toLocalTime(), LocalTime.MIDDAY);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withMinuteOfHour_minuteTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withMinuteOfHour(-1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withMinuteOfHour_minuteTooHigh() {
        TEST_2007_07_15_12_30_40_987654321.withMinuteOfHour(60);
    }

    //-----------------------------------------------------------------------
    // withSecondOfMinute()
    //-----------------------------------------------------------------------
    public void test_withSecondOfMinute_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321;
        for (int i = 0; i < 60; i++) {
            t = t.withSecondOfMinute(i);
            assertEquals(t.getSecondOfMinute().getValue(), i);
        }
    }

    public void test_withSecondOfMinute_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withSecondOfMinute(40);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    public void test_withSecondOfMinute_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.time(0, 0, 1)).withSecondOfMinute(0);
        assertSame(t.toLocalTime(), LocalTime.MIDNIGHT);
    }

    public void test_withSecondOfMinute_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.time(12, 0, 1)).withSecondOfMinute(0);
        assertSame(t.toLocalTime(), LocalTime.MIDDAY);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withSecondOfMinute_secondTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withSecondOfMinute(-1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withSecondOfMinute_secondTooHigh() {
        TEST_2007_07_15_12_30_40_987654321.withSecondOfMinute(60);
    }

    //-----------------------------------------------------------------------
    // withNanoOfSecond()
    //-----------------------------------------------------------------------
    public void test_withNanoOfSecond_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321;
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
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withNanoOfSecond(987654321);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    public void test_withNanoOfSecond_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.time(0, 0, 0, 1)).withNanoOfSecond(0);
        assertSame(t.toLocalTime(), LocalTime.MIDNIGHT);
    }

    public void test_withNanoOfSecond_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.time(12, 0, 0, 1)).withNanoOfSecond(0);
        assertSame(t.toLocalTime(), LocalTime.MIDDAY);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withNanoOfSecond_nanoTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withNanoOfSecond(-1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withNanoOfSecond_nanoTooHigh() {
        TEST_2007_07_15_12_30_40_987654321.withNanoOfSecond(1000000000);
    }

    //-----------------------------------------------------------------------
    // withTime()
    //-----------------------------------------------------------------------
    public void test_withTime_2ints() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(13, 40);
        check(t, 2007, 7, 15, 13, 40, 0, 0);
    }

    public void test_withTime_2ints_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.time(12, 30));
        LocalDateTime wt = t.withTime(12, 30);
        assertSame(t, wt);
    }

    public void test_withTime_2ints_sameHour() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.time(12, 30));
        t = TEST_2007_07_15_12_30_40_987654321.withTime(12, 20);
        check(t, 2007, 7, 15, 12, 20, 0, 0);
    }

    public void test_withTime_2ints_sameMinute() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.time(12, 30));
        t = TEST_2007_07_15_12_30_40_987654321.withTime(11, 30);
        check(t, 2007, 7, 15, 11, 30, 0, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withTime_2ints_hourTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withTime(-1, 30);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withTime_2ints_hourTooHigh() {
        TEST_2007_07_15_12_30_40_987654321.withTime(24, 30);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withTime_2ints_minuteTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withTime(12, -1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withTime_2ints_minuteTooHigh() {
        TEST_2007_07_15_12_30_40_987654321.withTime(12, 60);
    }

    //-----------------------------------------------------------------------
    public void test_withTime_3ints() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(13, 40, 50);
        check(t, 2007, 7, 15, 13, 40, 50, 0);
    }

    public void test_withTime_3ints_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.time(12, 30, 40));
        LocalDateTime wt = t.withTime(12, 30, 40);
        assertSame(t, wt);
    }

    public void test_withTime_3ints_sameHour() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.time(12, 30, 40));
        t = TEST_2007_07_15_12_30_40_987654321.withTime(12, 20, 30);
        check(t, 2007, 7, 15, 12, 20, 30, 0);
    }

    public void test_withTime_3ints_sameMinute() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.time(12, 30, 40));
        t = TEST_2007_07_15_12_30_40_987654321.withTime(11, 30, 30);
        check(t, 2007, 7, 15, 11, 30, 30, 0);
    }

    public void test_withTime_3ints_sameSecond() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.time(12, 30, 40));
        t = TEST_2007_07_15_12_30_40_987654321.withTime(11, 20, 40);
        check(t, 2007, 7, 15, 11, 20, 40, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withTime_3ints_hourTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withTime(-1, 30, 40);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withTime_3ints_hourTooHigh() {
        TEST_2007_07_15_12_30_40_987654321.withTime(24, 30, 40);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withTime_3ints_minuteTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withTime(12, -1, 40);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withTime_3ints_minuteTooHigh() {
        TEST_2007_07_15_12_30_40_987654321.withTime(12, 60, 40);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withTime_3ints_secondTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withTime(12, 30, -1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withTime_3ints_secondTooHigh() {
        TEST_2007_07_15_12_30_40_987654321.withTime(12, 30, 60);
    }

    //-----------------------------------------------------------------------
    public void test_withTime_4ints() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(13, 40, 50, 987654322);
        check(t, 2007, 7, 15, 13, 40, 50, 987654322);
    }

    public void test_withTime_4ints_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.time(12, 30, 40, 987654321));
        LocalDateTime wt = t.withTime(12, 30, 40, 987654321);
        assertSame(t, wt);
    }

    public void test_withTime_4ints_sameHour() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.time(12, 30, 40, 987654321));
        t = TEST_2007_07_15_12_30_40_987654321.withTime(12, 20, 30, 987654320);
        check(t, 2007, 7, 15, 12, 20, 30, 987654320);
    }

    public void test_withTime_4ints_sameMinute() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.time(12, 30, 40, 987654321));
        t = TEST_2007_07_15_12_30_40_987654321.withTime(11, 30, 30, 987654320);
        check(t, 2007, 7, 15, 11, 30, 30, 987654320);
    }

    public void test_withTime_4ints_sameSecond() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.time(12, 30, 40, 987654321));
        t = TEST_2007_07_15_12_30_40_987654321.withTime(11, 20, 40, 987654320);
        check(t, 2007, 7, 15, 11, 20, 40, 987654320);
    }

    public void test_withTime_4ints_sameNano() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.time(12, 30, 40, 987654321));
        t = TEST_2007_07_15_12_30_40_987654321.withTime(11, 20, 30, 987654321);
        check(t, 2007, 7, 15, 11, 20, 30, 987654321);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withTime_4ints_hourTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withTime(-1, 30, 40, 987654321);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withTime_4ints_hourTooHigh() {
        TEST_2007_07_15_12_30_40_987654321.withTime(24, 30, 40, 987654321);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withTime_4ints_minuteTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withTime(12, -1, 40, 987654321);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withTime_4ints_minuteTooHigh() {
        TEST_2007_07_15_12_30_40_987654321.withTime(12, 60, 40, 987654321);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withTime_4ints_secondTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withTime(12, 30, -1, 987654321);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withTime_4ints_secondTooHigh() {
        TEST_2007_07_15_12_30_40_987654321.withTime(12, 30, 60, 987654321);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withTime_4ints_nanoTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withTime(12, 30, 40, -1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withTime_4ints_nanoTooHigh() {
        TEST_2007_07_15_12_30_40_987654321.withTime(12, 30, 40, 1000000000);
    }

    //-----------------------------------------------------------------------
    // plus(PeriodProvider)
    //-----------------------------------------------------------------------
    public void test_plus_PeriodProvider() {
        PeriodProvider provider = Period.period(1, 2, 3, 4, 5, 6, 7);
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plus(provider);
        assertEquals(t, LocalDateTime.dateTime(2008, 9, 18, 16, 35, 46, 987654328));
    }

    public void test_plus_PeriodProvider_daysOverflow() {
        PeriodProvider provider = Period.hours(1);
        LocalDateTime t = LocalDateTime.dateTime(2008, 6, 30, 23, 30).plus(provider);
        assertEquals(t, LocalDateTime.dateTime(2008, 7, 1, 0, 30));
    }

    public void test_plus_PeriodProvider_zero() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plus(Period.ZERO);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    //-----------------------------------------------------------------------
    // plusYears()
    //-----------------------------------------------------------------------
    public void test_plusYears_int_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusYears(1);
        check(t, 2008, 7, 15, 12, 30, 40, 987654321);
    }

    public void test_plusYears_int_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusYears(0);
        assertSame(TEST_2007_07_15_12_30_40_987654321, t);
    }

    public void test_plusYears_int_negative() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusYears(-1);
        check(t, 2006, 7, 15, 12, 30, 40, 987654321);
    }

    public void test_plusYears_int_adjustDay() {
        LocalDateTime t = LocalDateTime.dateMidnight(2008, 2, 29).plusYears(1);
        check(t, 2009, 2, 28, 0, 0, 0, 0);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusYears_int_invalidTooLarge() {
        LocalDateTime.dateMidnight(Year.MAX_YEAR, 1, 1).plusYears(1);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusYears_int_invalidTooSmall() {
        LocalDate.date(Year.MIN_YEAR, 1, 1).plusYears(-1);
    }

    public void test_plusYears_int_DateResolver_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusYears(1, DateResolvers.nextValid());
        check(t, 2008, 7, 15, 12, 30, 40, 987654321);
    }

    public void test_plusYears_int_DateResolver_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusYears(0, DateResolvers.nextValid());
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    public void test_plusYears_int_DateResolver_negative() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusYears(-1, DateResolvers.nextValid());
        check(t, 2006, 7, 15, 12, 30, 40, 987654321);
    }

    public void test_plusYears_int_DateResolver_adjustDay() {
        LocalDateTime t = LocalDateTime.dateMidnight(2008, 2, 29).plusYears(1, DateResolvers.nextValid());
        check(t, 2009, 3, 1, 0, 0, 0, 0);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_plusYears_int_DateResolver_null_adjustDay() {
        TEST_2007_07_15_12_30_40_987654321.plusYears(1, new MockDateResolverReturnsNull());
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusYears_int_DateResolver_invalidTooLarge() {
        LocalDateTime.dateMidnight(Year.MAX_YEAR, 1, 1).plusYears(1, DateResolvers.nextValid());
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusYears_int_DateResolver_invalidTooSmall() {
        LocalDateTime.dateMidnight(Year.MIN_YEAR, 1, 1).plusYears(-1, DateResolvers.nextValid());
    }

    //-----------------------------------------------------------------------
    // plusMonths()
    //-----------------------------------------------------------------------
    public void test_plusMonths_int_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusMonths(1);
        check(t, 2007, 8, 15, 12, 30, 40, 987654321);
    }

    public void test_plusMonths_int_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusMonths(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    public void test_plusMonths_int_overYears() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusMonths(25);
        check(t, 2009, 8, 15, 12, 30, 40, 987654321);
    }

    public void test_plusMonths_int_negative() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusMonths(-1);
        check(t, 2007, 6, 15, 12, 30, 40, 987654321);
    }

    public void test_plusMonths_int_negativeAcrossYear() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusMonths(-7);
        check(t, 2006, 12, 15, 12, 30, 40, 987654321);
    }

    public void test_plusMonths_int_negativeOverYears() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusMonths(-31);
        check(t, 2004, 12, 15, 12, 30, 40, 987654321);
    }

    public void test_plusMonths_int_adjustDayFromLeapYear() {
        LocalDateTime t = LocalDateTime.dateMidnight(2008, 2, 29).plusMonths(12);
        check(t, 2009, 2, 28, 0, 0, 0, 0);
    }

    public void test_plusMonths_int_adjustDayFromMonthLength() {
        LocalDateTime t = LocalDateTime.dateMidnight(2007, 3, 31).plusMonths(1);
        check(t, 2007, 4, 30, 0, 0, 0, 0);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusMonths_int_invalidTooLarge() {
        LocalDateTime.dateMidnight(Year.MAX_YEAR, 12, 1).plusMonths(1);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusMonths_int_invalidTooSmall() {
        LocalDateTime.dateMidnight(Year.MIN_YEAR, 1, 1).plusMonths(-1);
    }

    public void test_plusMonths_int_DateResolver_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusMonths(1, DateResolvers.nextValid());
        check(t, 2007, 8, 15, 12, 30, 40, 987654321);
    }

    public void test_plusMonths_int_DateResolver_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusMonths(0, DateResolvers.nextValid());
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    public void test_plusMonths_int_DateResolver_overYears() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusMonths(25, DateResolvers.nextValid());
        check(t, 2009, 8, 15, 12, 30, 40, 987654321);
    }

    public void test_plusMonths_int_DateResolver_negative() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusMonths(-1, DateResolvers.nextValid());
        check(t, 2007, 6, 15, 12, 30, 40, 987654321);
    }

    public void test_plusMonths_int_DateResolver_negativeAcrossYear() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusMonths(-7, DateResolvers.nextValid());
        check(t, 2006, 12, 15, 12, 30, 40, 987654321);
    }

    public void test_plusMonths_int_DateResolver_negativeOverYears() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusMonths(-31, DateResolvers.nextValid());
        check(t, 2004, 12, 15, 12, 30, 40, 987654321);
    }

    public void test_plusMonths_int_DateResolver_adjustDayFromLeapYear() {
        LocalDateTime t = LocalDateTime.dateMidnight(2008, 2, 29).plusMonths(12, DateResolvers.nextValid());
        check(t, 2009, 3, 1, 0, 0, 0, 0);
    }

    public void test_plusMonths_int_DateResolver_adjustDayFromMonthLength() {
        LocalDateTime t = LocalDateTime.dateMidnight(2007, 3, 31).plusMonths(1, DateResolvers.nextValid());
        check(t, 2007, 5, 1, 0, 0, 0, 0);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_plusMonths_int_DateResolver_null_adjustDay() {
        TEST_2007_07_15_12_30_40_987654321.plusMonths(1, new MockDateResolverReturnsNull());
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusMonths_int_DateResolver_invalidTooLarge() {
        LocalDateTime.dateMidnight(Year.MAX_YEAR, 12, 1).plusMonths(1, DateResolvers.nextValid());
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusMonths_int_DateResolver_invalidTooSmall() {
        LocalDateTime.dateMidnight(Year.MIN_YEAR, 1, 1).plusMonths(-1, DateResolvers.nextValid());
    }

    //-----------------------------------------------------------------------
    // plusWeeks()
    //-----------------------------------------------------------------------
    @DataProvider(name="samplePlusWeeksSymmetry")
    Object[][] provider_samplePlusWeeksSymmetry() {
        return new Object[][] {
            {LocalDateTime.dateMidnight(-1, 1, 1)},
            {LocalDateTime.dateMidnight(-1, 2, 28)},
            {LocalDateTime.dateMidnight(-1, 3, 1)},
            {LocalDateTime.dateMidnight(-1, 12, 31)},
            {LocalDateTime.dateMidnight(0, 1, 1)},
            {LocalDateTime.dateMidnight(0, 2, 28)},
            {LocalDateTime.dateMidnight(0, 2, 29)},
            {LocalDateTime.dateMidnight(0, 3, 1)},
            {LocalDateTime.dateMidnight(0, 12, 31)},
            {LocalDateTime.dateMidnight(2007, 1, 1)},
            {LocalDateTime.dateMidnight(2007, 2, 28)},
            {LocalDateTime.dateMidnight(2007, 3, 1)},
            {LocalDateTime.dateMidnight(2007, 12, 31)},
            {LocalDateTime.dateMidnight(2008, 1, 1)},
            {LocalDateTime.dateMidnight(2008, 2, 28)},
            {LocalDateTime.dateMidnight(2008, 2, 29)},
            {LocalDateTime.dateMidnight(2008, 3, 1)},
            {LocalDateTime.dateMidnight(2008, 12, 31)},
            {LocalDateTime.dateMidnight(2099, 1, 1)},
            {LocalDateTime.dateMidnight(2099, 2, 28)},
            {LocalDateTime.dateMidnight(2099, 3, 1)},
            {LocalDateTime.dateMidnight(2099, 12, 31)},
            {LocalDateTime.dateMidnight(2100, 1, 1)},
            {LocalDateTime.dateMidnight(2100, 2, 28)},
            {LocalDateTime.dateMidnight(2100, 3, 1)},
            {LocalDateTime.dateMidnight(2100, 12, 31)},
        };
    }
    
    @Test(dataProvider="samplePlusWeeksSymmetry")
    public void test_plusWeeks_symmetry(LocalDateTime reference) {
        for (int weeks = 0; weeks < 365 * 8; weeks++) {
            LocalDateTime t = reference.plusWeeks(weeks).plusWeeks(-weeks);
            assertEquals(t, reference);

            t = reference.plusWeeks(-weeks).plusWeeks(weeks);
            assertEquals(t, reference);
        }
    }

    public void test_plusWeeks_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusWeeks(1);
        check(t, 2007, 7, 22, 12, 30, 40, 987654321);
    }

    public void test_plusWeeks_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusWeeks(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    public void test_plusWeeks_overMonths() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusWeeks(9);
        check(t, 2007, 9, 16, 12, 30, 40, 987654321);
    }

    public void test_plusWeeks_overYears() {
        LocalDateTime t = LocalDateTime.dateTime(2006, 7, 16, 12, 30, 40, 987654321).plusWeeks(52);
        assertEquals(t, TEST_2007_07_15_12_30_40_987654321);
    }

    public void test_plusWeeks_overLeapYears() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusYears(-1).plusWeeks(104);
        check(t, 2008, 7, 12, 12, 30, 40, 987654321);
    }

    public void test_plusWeeks_negative() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusWeeks(-1);
        check(t, 2007, 7, 8, 12, 30, 40, 987654321);
    }

    public void test_plusWeeks_negativeAcrossYear() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusWeeks(-28);
        check(t, 2006, 12, 31, 12, 30, 40, 987654321);
    }

    public void test_plusWeeks_negativeOverYears() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusWeeks(-104);
        check(t, 2005, 7, 17, 12, 30, 40, 987654321);
    }

    public void test_plusWeeks_maximum() {
        LocalDateTime t = LocalDateTime.dateMidnight(Year.MAX_YEAR, 12, 24).plusWeeks(1);
        check(t, Year.MAX_YEAR, 12, 31, 0, 0, 0, 0);
    }

    public void test_plusWeeks_minimum() {
        LocalDateTime t = LocalDateTime.dateMidnight(Year.MIN_YEAR, 1, 8).plusWeeks(-1);
        check(t, Year.MIN_YEAR, 1, 1, 0, 0, 0, 0);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusWeeks_invalidTooLarge() {
        LocalDateTime.dateMidnight(Year.MAX_YEAR, 12, 25).plusWeeks(1);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusWeeks_invalidTooSmall() {
        LocalDateTime.dateMidnight(Year.MIN_YEAR, 1, 7).plusWeeks(-1);
    }

    //-----------------------------------------------------------------------
    // plusDays()
    //-----------------------------------------------------------------------
    @DataProvider(name="samplePlusDaysSymmetry")
    Object[][] provider_samplePlusDaysSymmetry() {
        return new Object[][] {
            {LocalDateTime.dateMidnight(-1, 1, 1)},
            {LocalDateTime.dateMidnight(-1, 2, 28)},
            {LocalDateTime.dateMidnight(-1, 3, 1)},
            {LocalDateTime.dateMidnight(-1, 12, 31)},
            {LocalDateTime.dateMidnight(0, 1, 1)},
            {LocalDateTime.dateMidnight(0, 2, 28)},
            {LocalDateTime.dateMidnight(0, 2, 29)},
            {LocalDateTime.dateMidnight(0, 3, 1)},
            {LocalDateTime.dateMidnight(0, 12, 31)},
            {LocalDateTime.dateMidnight(2007, 1, 1)},
            {LocalDateTime.dateMidnight(2007, 2, 28)},
            {LocalDateTime.dateMidnight(2007, 3, 1)},
            {LocalDateTime.dateMidnight(2007, 12, 31)},
            {LocalDateTime.dateMidnight(2008, 1, 1)},
            {LocalDateTime.dateMidnight(2008, 2, 28)},
            {LocalDateTime.dateMidnight(2008, 2, 29)},
            {LocalDateTime.dateMidnight(2008, 3, 1)},
            {LocalDateTime.dateMidnight(2008, 12, 31)},
            {LocalDateTime.dateMidnight(2099, 1, 1)},
            {LocalDateTime.dateMidnight(2099, 2, 28)},
            {LocalDateTime.dateMidnight(2099, 3, 1)},
            {LocalDateTime.dateMidnight(2099, 12, 31)},
            {LocalDateTime.dateMidnight(2100, 1, 1)},
            {LocalDateTime.dateMidnight(2100, 2, 28)},
            {LocalDateTime.dateMidnight(2100, 3, 1)},
            {LocalDateTime.dateMidnight(2100, 12, 31)},
        };
    }
    
    @Test(dataProvider="samplePlusDaysSymmetry")
    public void test_plusDays_symmetry(LocalDateTime reference) {
        for (int days = 0; days < 365 * 8; days++) {
            LocalDateTime t = reference.plusDays(days).plusDays(-days);
            assertEquals(t, reference);

            t = reference.plusDays(-days).plusDays(days);
            assertEquals(t, reference);
        }
    }

    public void test_plusDays_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusDays(1);
        check(t, 2007, 7, 16, 12, 30, 40, 987654321);
    }

    public void test_plusDays_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusDays(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    public void test_plusDays_overMonths() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusDays(62);
        check(t, 2007, 9, 15, 12, 30, 40, 987654321);
    }

    public void test_plusDays_overYears() {
        LocalDateTime t = LocalDateTime.dateTime(2006, 7, 14, 12, 30, 40, 987654321).plusDays(366);
        assertEquals(t, TEST_2007_07_15_12_30_40_987654321);
    }

    public void test_plusDays_overLeapYears() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusYears(-1).plusDays(365 + 366);
        check(t, 2008, 7, 15, 12, 30, 40, 987654321);
    }

    public void test_plusDays_negative() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusDays(-1);
        check(t, 2007, 7, 14, 12, 30, 40, 987654321);
    }

    public void test_plusDays_negativeAcrossYear() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusDays(-196);
        check(t, 2006, 12, 31, 12, 30, 40, 987654321);
    }

    public void test_plusDays_negativeOverYears() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusDays(-730);
        check(t, 2005, 7, 15, 12, 30, 40, 987654321);
    }

    public void test_plusDays_maximum() {
        LocalDateTime t = LocalDateTime.dateMidnight(Year.MAX_YEAR, 12, 30).plusDays(1);
        check(t, Year.MAX_YEAR, 12, 31, 0, 0, 0, 0);
    }

    public void test_plusDays_minimum() {
        LocalDateTime t = LocalDateTime.dateMidnight(Year.MIN_YEAR, 1, 2).plusDays(-1);
        check(t, Year.MIN_YEAR, 1, 1, 0, 0, 0, 0);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusDays_invalidTooLarge() {
        LocalDateTime.dateMidnight(Year.MAX_YEAR, 12, 31).plusDays(1);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusDays_invalidTooSmall() {
        LocalDateTime.dateMidnight(Year.MIN_YEAR, 1, 1).plusDays(-1);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusDays_overflowTooLarge() {
        LocalDateTime.dateMidnight(Year.MAX_YEAR, 12, 31).plusDays(Long.MAX_VALUE);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusDays_overflowTooSmall() {
        LocalDateTime.dateMidnight(Year.MIN_YEAR, 1, 1).plusDays(Long.MIN_VALUE);
    }

    //-----------------------------------------------------------------------
    // plusHours()
    //-----------------------------------------------------------------------
    public void test_plusHours_one() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.MIDNIGHT);
        LocalDate d = t.getDate();

        for (int i = 0; i < 50; i++) {
            t = t.plusHours(1);

            if ((i + 1) % 24 == 0) {
                d = d.plusDays(1);
            }

            assertEquals(t.getDate(), d);
            assertEquals(t.getHourOfDay().getValue(), (i + 1) % 24);
        }
    }

    public void test_plusHours_fromZero() {
        LocalDateTime base = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.MIDNIGHT);
        LocalDate d = base.getDate().minusDays(3);
        LocalTime t = LocalTime.time(21, 0);
        
        for (int i = -50; i < 50; i++) {
            LocalDateTime dt = base.plusHours(i);
            t = t.plusHours(1);

            if (t.getHourOfDay().getValue() == 0) {
                d = d.plusDays(1);
            }

            assertEquals(dt.getDate(), d);
            assertEquals(dt.getTime(), t);
        }
    }

    public void test_plusHours_fromOne() {
        LocalDateTime base = TEST_2007_07_15_12_30_40_987654321.withTime(1, 0);
        LocalDate d = base.getDate().minusDays(3);
        LocalTime t = LocalTime.time(22, 0);

        for (int i = -50; i < 50; i++) {
            LocalDateTime dt = base.plusHours(i);

            t = t.plusHours(1);

            if (t.getHourOfDay().getValue() == 0) {
                d = d.plusDays(1);
            }

            assertEquals(dt.getDate(), d);
            assertEquals(dt.getTime(), t);
        }
    }

    public void test_plusHours_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusHours(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    public void test_plusHours_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(23, 0).plusHours(1);
        assertSame(t.toLocalTime(), LocalTime.MIDNIGHT);
    }

    public void test_plusHours_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(11, 0).plusHours(1);
        assertSame(t.toLocalTime(), LocalTime.MIDDAY);
    }

    //-----------------------------------------------------------------------
    // plusMinutes()
    //-----------------------------------------------------------------------
    public void test_plusMinutes_one() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.MIDNIGHT);
        LocalDate d = t.getDate();

        int hour = 0;
        int min = 0;

        for (int i = 0; i < 70; i++) {
            t = t.plusMinutes(1);
            min++;
            if (min == 60) {
                hour++;
                min = 0;
            }

            assertEquals(t.getDate(), d);
            assertEquals(t.getHourOfDay().getValue(), hour);
            assertEquals(t.getMinuteOfHour().getValue(), min);
        }
    }

    public void test_plusMinutes_fromZero() {
        LocalDateTime base = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.MIDNIGHT);
        LocalDate d = base.getDate().minusDays(1);
        LocalTime t = LocalTime.time(22, 49);

        for (int i = -70; i < 70; i++) {
            LocalDateTime dt = base.plusMinutes(i);
            t = t.plusMinutes(1);

            if (t == LocalTime.MIDNIGHT) {
                d = d.plusDays(1);
            }

            assertEquals(dt.getDate(), d, String.valueOf(i));
            assertEquals(dt.getTime(), t, String.valueOf(i));
        }
    }

    public void test_plusMinutes_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusMinutes(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    public void test_plusMinutes_noChange_oneDay() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusMinutes(24 * 60);
        assertEquals(t.getDate(), TEST_2007_07_15_12_30_40_987654321.getDate().plusDays(1));
        assertSame(t.getTime(), TEST_2007_07_15_12_30_40_987654321.getTime());
    }

    public void test_plusMinutes_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(23, 59).plusMinutes(1);
        assertSame(t.getTime(), LocalTime.MIDNIGHT);
    }

    public void test_plusMinutes_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(11, 59).plusMinutes(1);
        assertSame(t.getTime(), LocalTime.MIDDAY);
    }

    //-----------------------------------------------------------------------
    // plusSeconds()
    //-----------------------------------------------------------------------
    public void test_plusSeconds_one() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.MIDNIGHT);
        LocalDate d = t.getDate();

        int hour = 0;
        int min = 0;
        int sec = 0;

        for (int i = 0; i < 3700; i++) {
            t = t.plusSeconds(1);
            sec++;
            if (sec == 60) {
                min++;
                sec = 0;
            }
            if (min == 60) {
                hour++;
                min = 0;
            }

            assertEquals(t.getDate(), d);
            assertEquals(t.getHourOfDay().getValue(), hour);
            assertEquals(t.getMinuteOfHour().getValue(), min);
            assertEquals(t.getSecondOfMinute().getValue(), sec);
        }
    }

    @DataProvider(name="plusSeconds_fromZero")
    Iterator<Object[]> plusSeconds_fromZero() {
        return new Iterator<Object[]>() {
            int delta = 30;

            int i = -3660;
            LocalDate date = TEST_2007_07_15_12_30_40_987654321.getDate().minusDays(1);
            int hour = 22;
            int min = 59;
            int sec = 0;

            public boolean hasNext() {
                return i <= 3660;
            }

            public Object[] next() {
                final Object[] ret = new Object[] {i, date, hour, min, sec};
                i += delta;
                sec += delta;

                if (sec >= 60) {
                    min++;
                    sec -= 60;

                    if (min == 60) {
                        hour++;
                        min = 0;
                        
                        if (hour == 24) {
                            hour = 0;
                        }
                    }
                }
                
                if (i == 0) {
                    date = date.plusDays(1);
                }

                return ret;
            }

            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @Test(dataProvider="plusSeconds_fromZero")
    public void test_plusSeconds_fromZero(int seconds, LocalDate date, int hour, int min, int sec) {
        LocalDateTime base = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.MIDNIGHT);
        LocalDateTime t = base.plusSeconds(seconds);

        assertEquals(date, t.getDate());
        assertEquals(hour, t.getHourOfDay().getValue());
        assertEquals(min, t.getMinuteOfHour().getValue());
        assertEquals(sec, t.getSecondOfMinute().getValue());
    }

    public void test_plusSeconds_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusSeconds(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    public void test_plusSeconds_noChange_oneDay() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusSeconds(24 * 60 * 60);
        assertEquals(t.getDate(), TEST_2007_07_15_12_30_40_987654321.getDate().plusDays(1));
        assertSame(t.getTime(), TEST_2007_07_15_12_30_40_987654321.getTime());
    }

    public void test_plusSeconds_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(23, 59, 59).plusSeconds(1);
        assertSame(t.toLocalTime(), LocalTime.MIDNIGHT);
    }

    public void test_plusSeconds_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(11, 59, 59).plusSeconds(1);
        assertSame(t.toLocalTime(), LocalTime.MIDDAY);
    }

    //-----------------------------------------------------------------------
    // plusNanos()
    //-----------------------------------------------------------------------
    public void test_plusNanos_halfABillion() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.MIDNIGHT);
        LocalDate d = t.getDate();

        int hour = 0;
        int min = 0;
        int sec = 0;
        int nanos = 0;

        for (long i = 0; i < 3700 * 1000000000L; i+= 500000000) {
            t = t.plusNanos(500000000);
            nanos += 500000000;
            if (nanos == 1000000000) {
                sec++;
                nanos = 0;
            }
            if (sec == 60) {
                min++;
                sec = 0;
            }
            if (min == 60) {
                hour++;
                min = 0;
            }

            assertEquals(t.getDate(), d, String.valueOf(i));
            assertEquals(t.getHourOfDay().getValue(), hour);
            assertEquals(t.getMinuteOfHour().getValue(), min);
            assertEquals(t.getSecondOfMinute().getValue(), sec);
            assertEquals(t.getNanoOfSecond().getValue(), nanos);
        }
    }

    @DataProvider(name="plusNanos_fromZero")
    Iterator<Object[]> plusNanos_fromZero() {
        return new Iterator<Object[]>() {
            long delta = 7500000000L;

            long i = -3660 * 1000000000L;
            LocalDate date = TEST_2007_07_15_12_30_40_987654321.getDate().minusDays(1);
            int hour = 22;
            int min = 59;
            int sec = 0;
            long nanos = 0;

            public boolean hasNext() {
                return i <= 3660 * 1000000000L;
            }

            public Object[] next() {
                final Object[] ret = new Object[] {i, date, hour, min, sec, (int)nanos};
                i += delta;
                nanos += delta;

                if (nanos >= 1000000000L) {
                    sec += nanos / 1000000000L;
                    nanos %= 1000000000L;

                    if (sec >= 60) {
                        min++;
                        sec %= 60;

                        if (min == 60) {
                            hour++;
                            min = 0;

                            if (hour == 24) {
                                hour = 0;
                                date = date.plusDays(1);
                            }
                        }
                    }
                }

                return ret;
            }

            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @Test(dataProvider="plusNanos_fromZero")
    public void test_plusNanos_fromZero(long nanoseconds, LocalDate date, int hour, int min, int sec, int nanos) {
        LocalDateTime base = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.MIDNIGHT);
        LocalDateTime t = base.plusNanos(nanoseconds);

        assertEquals(date, t.getDate());
        assertEquals(hour, t.getHourOfDay().getValue());
        assertEquals(min, t.getMinuteOfHour().getValue());
        assertEquals(sec, t.getSecondOfMinute().getValue());
        assertEquals(nanos, t.getNanoOfSecond().getValue());
    }

    public void test_plusNanos_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusNanos(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    public void test_plusNanos_noChange_oneDay() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusNanos(24 * 60 * 60 * 1000000000L);
        assertEquals(t.getDate(), TEST_2007_07_15_12_30_40_987654321.getDate().plusDays(1));
        assertSame(t.getTime(), TEST_2007_07_15_12_30_40_987654321.getTime());
    }

    public void test_plusNanos_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(23, 59, 59, 999999999).plusNanos(1);
        assertSame(t.getTime(), LocalTime.MIDNIGHT);
    }

    public void test_plusNanos_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(11, 59, 59, 999999999).plusNanos(1);
        assertSame(t.getTime(), LocalTime.MIDDAY);
    }

    //-----------------------------------------------------------------------
    // minus(PeriodProvider)
    //-----------------------------------------------------------------------
    public void test_minus_PeriodProvider() {
        PeriodProvider provider = Period.period(1, 2, 3, 4, 5, 6, 7);
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minus(provider);
        assertEquals(t, LocalDateTime.dateTime(2006, 5, 12, 8, 25, 34, 987654314));
    }

    public void test_minus_PeriodProvider_daysOverflow() {
        PeriodProvider provider = Period.hours(1);
        LocalDateTime t = LocalDateTime.dateTime(2008, 6, 1, 0, 30).minus(provider);
        assertEquals(t, LocalDateTime.dateTime(2008, 5, 31, 23, 30));
    }

    public void test_minus_PeriodProvider_zero() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minus(Period.ZERO);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    //-----------------------------------------------------------------------
    // minusYears()
    //-----------------------------------------------------------------------
    public void test_minusYears_int_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusYears(1);
        check(t, 2006, 7, 15, 12, 30, 40, 987654321);
    }

    public void test_minusYears_int_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusYears(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    public void test_minusYears_int_negative() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusYears(-1);
        check(t, 2008, 7, 15, 12, 30, 40, 987654321);
    }

    public void test_minusYears_int_adjustDay() {
        LocalDateTime t = LocalDateTime.dateMidnight(2008, 2, 29).minusYears(1);
        check(t, 2007, 2, 28, 0, 0, 0, 0);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusYears_int_invalidTooLarge() {
        LocalDateTime.dateMidnight(Year.MAX_YEAR, 1, 1).minusYears(-1);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusYears_int_invalidTooSmall() {
        LocalDateTime.dateMidnight(Year.MIN_YEAR, 1, 1).minusYears(1);
    }

    public void test_minusYears_int_DateResolver_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusYears(1, DateResolvers.nextValid());
        check(t, 2006, 7, 15, 12, 30, 40, 987654321);
    }

    public void test_minusYears_int_DateResolver_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusYears(0, DateResolvers.nextValid());
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    public void test_minusYears_int_DateResolver_negative() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusYears(-1, DateResolvers.nextValid());
        check(t, 2008, 7, 15, 12, 30, 40, 987654321);
    }

    public void test_minusYears_int_DateResolver_adjustDay() {
        LocalDateTime t = LocalDateTime.dateMidnight(2008, 2, 29).minusYears(1, DateResolvers.nextValid());
        check(t, 2007, 3, 1, 0, 0, 0, 0);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_minusYears_int_DateResolver_null_adjustDay() {
        TEST_2007_07_15_12_30_40_987654321.minusYears(1, new MockDateResolverReturnsNull());
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusYears_int_DateResolver_invalidTooLarge() {
        LocalDateTime.dateMidnight(Year.MAX_YEAR, 1, 1).minusYears(-1, DateResolvers.nextValid());
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusYears_int_DateResolver_invalidTooSmall() {
        LocalDateTime.dateMidnight(Year.MIN_YEAR, 1, 1).minusYears(1, DateResolvers.nextValid());
    }

    //-----------------------------------------------------------------------
    // minusMonths()
    //-----------------------------------------------------------------------
    public void test_minusMonths_int_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusMonths(1);
        check(t, 2007, 6, 15, 12, 30, 40, 987654321);
    }

    public void test_minusMonths_int_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusMonths(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    public void test_minusMonths_int_overYears() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusMonths(25);
        check(t, 2005, 6, 15, 12, 30, 40, 987654321);
    }

    public void test_minusMonths_int_negative() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusMonths(-1);
        check(t, 2007, 8, 15, 12, 30, 40, 987654321);
    }

    public void test_minusMonths_int_negativeAcrossYear() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusMonths(-7);
        check(t, 2008, 2, 15, 12, 30, 40, 987654321);
    }

    public void test_minusMonths_int_negativeOverYears() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusMonths(-31);
        check(t, 2010, 2, 15, 12, 30, 40, 987654321);
    }

    public void test_minusMonths_int_adjustDayFromLeapYear() {
        LocalDateTime t = LocalDateTime.dateMidnight(2008, 2, 29).minusMonths(12);
        check(t, 2007, 2, 28, 0, 0, 0, 0);
    }

    public void test_minusMonths_int_adjustDayFromMonthLength() {
        LocalDateTime t = LocalDateTime.dateMidnight(2007, 3, 31).minusMonths(1);
        check(t, 2007, 2, 28, 0, 0, 0, 0);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusMonths_int_invalidTooLarge() {
        LocalDateTime.dateMidnight(Year.MAX_YEAR, 12, 1).minusMonths(-1);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusMonths_int_invalidTooSmall() {
        LocalDateTime t = LocalDateTime.dateMidnight(Year.MIN_YEAR, 1, 1).minusMonths(1);
    }

    public void test_minusMonths_int_DateResolver_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusMonths(1, DateResolvers.nextValid());
        check(t, 2007, 6, 15, 12, 30, 40, 987654321);
    }

    public void test_minusMonths_int_DateResolver_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusMonths(0, DateResolvers.nextValid());
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    public void test_minusMonths_int_DateResolver_overYears() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusMonths(25, DateResolvers.nextValid());
        check(t, 2005, 6, 15, 12, 30, 40, 987654321);
    }

    public void test_minusMonths_int_DateResolver_negative() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusMonths(-1, DateResolvers.nextValid());
        check(t, 2007, 8, 15, 12, 30, 40, 987654321);
    }

    public void test_minusMonths_int_DateResolver_negativeAcrossYear() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusMonths(-7, DateResolvers.nextValid());
        check(t, 2008, 2, 15, 12, 30, 40, 987654321);
    }

    public void test_minusMonths_int_DateResolver_negativeOverYears() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusMonths(-31, DateResolvers.nextValid());
        check(t, 2010, 2, 15, 12, 30, 40, 987654321);
    }

    public void test_minusMonths_int_DateResolver_adjustDayFromLeapYear() {
        LocalDateTime t = LocalDateTime.dateMidnight(2008, 2, 29).minusMonths(12, DateResolvers.nextValid());
        check(t, 2007, 3, 1, 0, 0, 0, 0);
    }

    public void test_minusMonths_int_DateResolver_adjustDayFromMonthLength() {
        LocalDateTime t = LocalDateTime.dateMidnight(2007, 3, 31).minusMonths(1, DateResolvers.nextValid());
        check(t, 2007, 3, 1, 0, 0, 0, 0);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_minusMonths_int_DateResolver_null_adjustDay() {
        TEST_2007_07_15_12_30_40_987654321.minusMonths(1, new MockDateResolverReturnsNull());
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusMonths_int_DateResolver_invalidTooLarge() {
        LocalDateTime.dateMidnight(Year.MAX_YEAR, 12, 1).minusMonths(-1, DateResolvers.nextValid());
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusMonths_int_DateResolver_invalidTooSmall() {
        LocalDateTime.dateMidnight(Year.MIN_YEAR, 1, 1).minusMonths(1, DateResolvers.nextValid());
    }

    //-----------------------------------------------------------------------
    // minusWeeks()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleMinusWeeksSymmetry")
    Object[][] provider_sampleMinusWeeksSymmetry() {
        return new Object[][] {
            {LocalDateTime.dateMidnight(-1, 1, 1)},
            {LocalDateTime.dateMidnight(-1, 2, 28)},
            {LocalDateTime.dateMidnight(-1, 3, 1)},
            {LocalDateTime.dateMidnight(-1, 12, 31)},
            {LocalDateTime.dateMidnight(0, 1, 1)},
            {LocalDateTime.dateMidnight(0, 2, 28)},
            {LocalDateTime.dateMidnight(0, 2, 29)},
            {LocalDateTime.dateMidnight(0, 3, 1)},
            {LocalDateTime.dateMidnight(0, 12, 31)},
            {LocalDateTime.dateMidnight(2007, 1, 1)},
            {LocalDateTime.dateMidnight(2007, 2, 28)},
            {LocalDateTime.dateMidnight(2007, 3, 1)},
            {LocalDateTime.dateMidnight(2007, 12, 31)},
            {LocalDateTime.dateMidnight(2008, 1, 1)},
            {LocalDateTime.dateMidnight(2008, 2, 28)},
            {LocalDateTime.dateMidnight(2008, 2, 29)},
            {LocalDateTime.dateMidnight(2008, 3, 1)},
            {LocalDateTime.dateMidnight(2008, 12, 31)},
            {LocalDateTime.dateMidnight(2099, 1, 1)},
            {LocalDateTime.dateMidnight(2099, 2, 28)},
            {LocalDateTime.dateMidnight(2099, 3, 1)},
            {LocalDateTime.dateMidnight(2099, 12, 31)},
            {LocalDateTime.dateMidnight(2100, 1, 1)},
            {LocalDateTime.dateMidnight(2100, 2, 28)},
            {LocalDateTime.dateMidnight(2100, 3, 1)},
            {LocalDateTime.dateMidnight(2100, 12, 31)},
        };
    }
    
    @Test(dataProvider="sampleMinusWeeksSymmetry")
    public void test_minusWeeks_symmetry(LocalDateTime reference) {
        for (int weeks = 0; weeks < 365 * 8; weeks++) {
            LocalDateTime t = reference.minusWeeks(weeks).minusWeeks(-weeks);
            assertEquals(t, reference);

            t = reference.minusWeeks(-weeks).minusWeeks(weeks);
            assertEquals(t, reference);
        }
    }

    public void test_minusWeeks_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusWeeks(1);
        check(t, 2007, 7, 8, 12, 30, 40, 987654321);
    }

    public void test_minusWeeks_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusWeeks(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    public void test_minusWeeks_overMonths() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusWeeks(9);
        check(t, 2007, 5, 13, 12, 30, 40, 987654321);
    }

    public void test_minusWeeks_overYears() {
        LocalDateTime t = LocalDateTime.dateTime(2008, 7, 13, 12, 30, 40, 987654321).minusWeeks(52);
        assertEquals(t, TEST_2007_07_15_12_30_40_987654321);
    }

    public void test_minusWeeks_overLeapYears() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusYears(-1).minusWeeks(104);
        check(t, 2006, 7, 18, 12, 30, 40, 987654321);
    }

    public void test_minusWeeks_negative() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusWeeks(-1);
        check(t, 2007, 7, 22, 12, 30, 40, 987654321);
    }

    public void test_minusWeeks_negativeAcrossYear() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusWeeks(-28);
        check(t, 2008, 1, 27, 12, 30, 40, 987654321);
    }

    public void test_minusWeeks_negativeOverYears() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusWeeks(-104);
        check(t, 2009, 7, 12, 12, 30, 40, 987654321);
    }

    public void test_minusWeeks_maximum() {
        LocalDateTime t = LocalDateTime.dateMidnight(Year.MAX_YEAR, 12, 24).minusWeeks(-1);
        check(t, Year.MAX_YEAR, 12, 31, 0, 0, 0, 0);
    }

    public void test_minusWeeks_minimum() {
        LocalDateTime t = LocalDateTime.dateMidnight(Year.MIN_YEAR, 1, 8).minusWeeks(1);
        check(t, Year.MIN_YEAR, 1, 1, 0, 0, 0, 0);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusWeeks_invalidTooLarge() {
        LocalDateTime.dateMidnight(Year.MAX_YEAR, 12, 25).minusWeeks(-1);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusWeeks_invalidTooSmall() {
        LocalDateTime.dateMidnight(Year.MIN_YEAR, 1, 7).minusWeeks(1);
    }

    //-----------------------------------------------------------------------
    // minusDays()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleMinusDaysSymmetry")
    Object[][] provider_sampleMinusDaysSymmetry() {
        return new Object[][] {
            {LocalDateTime.dateMidnight(-1, 1, 1)},
            {LocalDateTime.dateMidnight(-1, 2, 28)},
            {LocalDateTime.dateMidnight(-1, 3, 1)},
            {LocalDateTime.dateMidnight(-1, 12, 31)},
            {LocalDateTime.dateMidnight(0, 1, 1)},
            {LocalDateTime.dateMidnight(0, 2, 28)},
            {LocalDateTime.dateMidnight(0, 2, 29)},
            {LocalDateTime.dateMidnight(0, 3, 1)},
            {LocalDateTime.dateMidnight(0, 12, 31)},
            {LocalDateTime.dateMidnight(2007, 1, 1)},
            {LocalDateTime.dateMidnight(2007, 2, 28)},
            {LocalDateTime.dateMidnight(2007, 3, 1)},
            {LocalDateTime.dateMidnight(2007, 12, 31)},
            {LocalDateTime.dateMidnight(2008, 1, 1)},
            {LocalDateTime.dateMidnight(2008, 2, 28)},
            {LocalDateTime.dateMidnight(2008, 2, 29)},
            {LocalDateTime.dateMidnight(2008, 3, 1)},
            {LocalDateTime.dateMidnight(2008, 12, 31)},
            {LocalDateTime.dateMidnight(2099, 1, 1)},
            {LocalDateTime.dateMidnight(2099, 2, 28)},
            {LocalDateTime.dateMidnight(2099, 3, 1)},
            {LocalDateTime.dateMidnight(2099, 12, 31)},
            {LocalDateTime.dateMidnight(2100, 1, 1)},
            {LocalDateTime.dateMidnight(2100, 2, 28)},
            {LocalDateTime.dateMidnight(2100, 3, 1)},
            {LocalDateTime.dateMidnight(2100, 12, 31)},
        };
    }
    
    @Test(dataProvider="sampleMinusDaysSymmetry")
    public void test_minusDays_symmetry(LocalDateTime reference) {
        for (int days = 0; days < 365 * 8; days++) {
            LocalDateTime t = reference.minusDays(days).minusDays(-days);
            assertEquals(t, reference);

            t = reference.minusDays(-days).minusDays(days);
            assertEquals(t, reference);
        }
    }

    public void test_minusDays_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusDays(1);
        check(t, 2007, 7, 14, 12, 30, 40, 987654321);
    }

    public void test_minusDays_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusDays(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    public void test_minusDays_overMonths() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusDays(62);
        check(t, 2007, 5, 14, 12, 30, 40, 987654321);
    }

    public void test_minusDays_overYears() {
        LocalDateTime t = LocalDateTime.dateTime(2008, 7, 16, 12, 30, 40, 987654321).minusDays(367);
        assertEquals(t, TEST_2007_07_15_12_30_40_987654321);
    }

    public void test_minusDays_overLeapYears() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusYears(2).minusDays(365 + 366);
        assertEquals(t, TEST_2007_07_15_12_30_40_987654321);
    }

    public void test_minusDays_negative() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusDays(-1);
        check(t, 2007, 7, 16, 12, 30, 40, 987654321);
    }

    public void test_minusDays_negativeAcrossYear() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusDays(-169);
        check(t, 2007, 12, 31, 12, 30, 40, 987654321);
    }

    public void test_minusDays_negativeOverYears() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusDays(-731);
        check(t, 2009, 7, 15, 12, 30, 40, 987654321);
    }

    public void test_minusDays_maximum() {
        LocalDateTime t = LocalDateTime.dateMidnight(Year.MAX_YEAR, 12, 30).minusDays(-1);
        check(t, Year.MAX_YEAR, 12, 31, 0, 0, 0, 0);
    }

    public void test_minusDays_minimum() {
        LocalDateTime t = LocalDateTime.dateMidnight(Year.MIN_YEAR, 1, 2).minusDays(1);
        check(t, Year.MIN_YEAR, 1, 1, 0, 0, 0, 0);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusDays_invalidTooLarge() {
        LocalDateTime.dateMidnight(Year.MAX_YEAR, 12, 31).minusDays(-1);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusDays_invalidTooSmall() {
        LocalDateTime.dateMidnight(Year.MIN_YEAR, 1, 1).minusDays(1);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusDays_overflowTooLarge() {
        LocalDateTime.dateMidnight(Year.MAX_YEAR, 12, 31).minusDays(Long.MIN_VALUE);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusDays_overflowTooSmall() {
        LocalDateTime.dateMidnight(Year.MIN_YEAR, 1, 1).minusDays(Long.MAX_VALUE);
    }

    //-----------------------------------------------------------------------
    // minusHours()
    //-----------------------------------------------------------------------
    public void test_minusHours_one() {
        LocalDateTime t =TEST_2007_07_15_12_30_40_987654321.with(LocalTime.MIDNIGHT);
        LocalDate d = t.getDate();

        for (int i = 0; i < 50; i++) {
            t = t.minusHours(1);

            if (i % 24 == 0) {
                d = d.minusDays(1);
            }

            assertEquals(t.getDate(), d);
            assertEquals(t.getHourOfDay().getValue(), (((-i + 23) % 24) + 24) % 24);
        }
    }

    public void test_minusHours_fromZero() {
        LocalDateTime base = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.MIDNIGHT);
        LocalDate d = base.getDate().plusDays(2);
        LocalTime t = LocalTime.time(3, 0);

        for (int i = -50; i < 50; i++) {
            LocalDateTime dt = base.minusHours(i);
            t = t.minusHours(1);

            if (t.getHourOfDay().getValue() == 23) {
                d = d.minusDays(1);
            }

            assertEquals(dt.getDate(), d, String.valueOf(i));
            assertEquals(dt.getTime(), t);
        }
    }

    public void test_minusHours_fromOne() {
        LocalDateTime base = TEST_2007_07_15_12_30_40_987654321.withTime(1, 0);
        LocalDate d = base.getDate().plusDays(2);
        LocalTime t = LocalTime.time(4, 0);

        for (int i = -50; i < 50; i++) {
            LocalDateTime dt = base.minusHours(i);

            t = t.minusHours(1);

            if (t.getHourOfDay().getValue() == 23) {
                d = d.minusDays(1);
            }

            assertEquals(dt.getDate(), d, String.valueOf(i));
            assertEquals(dt.getTime(), t);
        }
    }

    public void test_minusHours_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusHours(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    public void test_minusHours_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(1, 0).minusHours(1);
        assertSame(t.toLocalTime(), LocalTime.MIDNIGHT);
    }

    public void test_minusHours_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(13, 0).minusHours(1);
        assertSame(t.toLocalTime(), LocalTime.MIDDAY);
    }

    //-----------------------------------------------------------------------
    // minusMinutes()
    //-----------------------------------------------------------------------
    public void test_minusMinutes_one() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.MIDNIGHT);
        LocalDate d = t.getDate().minusDays(1);

        int hour = 0;
        int min = 0;

        for (int i = 0; i < 70; i++) {
            t = t.minusMinutes(1);
            min--;
            if (min == -1) {
                hour--;
                min = 59;
                
                if (hour == -1) {
                    hour = 23;
                }
            }
            assertEquals(t.getDate(), d);
            assertEquals(t.getHourOfDay().getValue(), hour);
            assertEquals(t.getMinuteOfHour().getValue(), min);
        }
    }

    public void test_minusMinutes_fromZero() {
        LocalDateTime base = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.MIDNIGHT);
        LocalDate d = base.getDate().minusDays(1);
        LocalTime t = LocalTime.time(22, 49);

        for (int i = 70; i > -70; i--) {
            LocalDateTime dt = base.minusMinutes(i);
            t = t.plusMinutes(1);
            
            if (t == LocalTime.MIDNIGHT) {
                d = d.plusDays(1);
            }

            assertEquals(dt.getDate(), d);
            assertEquals(dt.getTime(), t);
        }
    }

    public void test_minusMinutes_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusMinutes(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    public void test_minusMinutes_noChange_oneDay() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusMinutes(24 * 60);
        assertEquals(t.getDate(), TEST_2007_07_15_12_30_40_987654321.getDate().minusDays(1));
        assertSame(t.getTime(), TEST_2007_07_15_12_30_40_987654321.getTime());
    }

    public void test_minusMinutes_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(0, 1).minusMinutes(1);
        assertSame(t.getTime(), LocalTime.MIDNIGHT);
    }

    public void test_minusMinutes_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(12, 1).minusMinutes(1);
        assertSame(t.getTime(), LocalTime.MIDDAY);
    }

    //-----------------------------------------------------------------------
    // minusSeconds()
    //-----------------------------------------------------------------------
    public void test_minusSeconds_one() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.MIDNIGHT);
        LocalDate d = t.getDate().minusDays(1);

        int hour = 0;
        int min = 0;
        int sec = 0;

        for (int i = 0; i < 3700; i++) {
            t = t.minusSeconds(1);
            sec--;
            if (sec == -1) {
                min--;
                sec = 59;

                if (min == -1) {
                    hour--;
                    min = 59;
                    
                    if (hour == -1) {
                        hour = 23;
                    }
                }
            }

            assertEquals(t.getDate(), d);
            assertEquals(t.getHourOfDay().getValue(), hour);
            assertEquals(t.getMinuteOfHour().getValue(), min);
            assertEquals(t.getSecondOfMinute().getValue(), sec);
        }
    }

    @DataProvider(name="minusSeconds_fromZero")
    Iterator<Object[]> minusSeconds_fromZero() {
        return new Iterator<Object[]>() {
            int delta = 30;

            int i = 3660;
            LocalDate date = TEST_2007_07_15_12_30_40_987654321.getDate().minusDays(1);
            int hour = 22;
            int min = 59;
            int sec = 0;

            public boolean hasNext() {
                return i >= -3660;
            }

            public Object[] next() {
                final Object[] ret = new Object[] {i, date, hour, min, sec};
                i -= delta;
                sec += delta;

                if (sec >= 60) {
                    min++;
                    sec -= 60;

                    if (min == 60) {
                        hour++;
                        min = 0;
                        
                        if (hour == 24) {
                            hour = 0;
                        }
                    }
                }

                if (i == 0) {
                    date = date.plusDays(1);
                }

                return ret;
            }

            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @Test(dataProvider="minusSeconds_fromZero")
    public void test_minusSeconds_fromZero(int seconds, LocalDate date, int hour, int min, int sec) {
        LocalDateTime base = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.MIDNIGHT);
        LocalDateTime t = base.minusSeconds(seconds);

        assertEquals(date, t.getDate());
        assertEquals(hour, t.getHourOfDay().getValue());
        assertEquals(min, t.getMinuteOfHour().getValue());
        assertEquals(sec, t.getSecondOfMinute().getValue());
    }

    public void test_minusSeconds_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusSeconds(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    public void test_minusSeconds_noChange_oneDay() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusSeconds(24 * 60 * 60);
        assertEquals(t.getDate(), TEST_2007_07_15_12_30_40_987654321.getDate().minusDays(1));
        assertSame(t.getTime(), TEST_2007_07_15_12_30_40_987654321.getTime());
    }

    public void test_minusSeconds_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(0, 0, 1).minusSeconds(1);
        assertSame(t.toLocalTime(), LocalTime.MIDNIGHT);
    }

    public void test_minusSeconds_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(12, 0, 1).minusSeconds(1);
        assertSame(t.toLocalTime(), LocalTime.MIDDAY);
    }

    //-----------------------------------------------------------------------
    // minusNanos()
    //-----------------------------------------------------------------------
    public void test_minusNanos_halfABillion() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.MIDNIGHT);
        LocalDate d = t.getDate().minusDays(1);

        int hour = 0;
        int min = 0;
        int sec = 0;
        int nanos = 0;

        for (long i = 0; i < 3700 * 1000000000L; i+= 500000000) {
            t = t.minusNanos(500000000);
            nanos -= 500000000;

            if (nanos < 0) {
                sec--;
                nanos += 1000000000;

                if (sec == -1) {
                    min--;
                    sec += 60;
                    
                    if (min == -1) {
                        hour--;
                        min += 60;

                        if (hour == -1) {
                            hour += 24;
                        }
                    }
                }
            }

            assertEquals(t.getDate(), d);
            assertEquals(t.getHourOfDay().getValue(), hour);
            assertEquals(t.getMinuteOfHour().getValue(), min);
            assertEquals(t.getSecondOfMinute().getValue(), sec);
            assertEquals(t.getNanoOfSecond().getValue(), nanos);
        }
    }

    @DataProvider(name="minusNanos_fromZero")
    Iterator<Object[]> minusNanos_fromZero() {
        return new Iterator<Object[]>() {
            long delta = 7500000000L;

            long i = 3660 * 1000000000L;
            LocalDate date = TEST_2007_07_15_12_30_40_987654321.getDate().minusDays(1);
            int hour = 22;
            int min = 59;
            int sec = 0;
            long nanos = 0;

            public boolean hasNext() {
                return i >= -3660 * 1000000000L;
            }

            public Object[] next() {
                final Object[] ret = new Object[] {i, date, hour, min, sec, (int)nanos};
                i -= delta;
                nanos += delta;

                if (nanos >= 1000000000L) {
                    sec += nanos / 1000000000L;
                    nanos %= 1000000000L;

                    if (sec >= 60) {
                        min++;
                        sec %= 60;

                        if (min == 60) {
                            hour++;
                            min = 0;

                            if (hour == 24) {
                                hour = 0;
                                date = date.plusDays(1);
                            }
                        }
                    }
                }

                return ret;
            }

            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @Test(dataProvider="minusNanos_fromZero")
    public void test_minusNanos_fromZero(long nanoseconds, LocalDate date, int hour, int min, int sec, int nanos) {
        LocalDateTime base = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.MIDNIGHT);
        LocalDateTime t = base.minusNanos(nanoseconds);

        assertEquals(date, t.getDate());
        assertEquals(hour, t.getHourOfDay().getValue());
        assertEquals(min, t.getMinuteOfHour().getValue());
        assertEquals(sec, t.getSecondOfMinute().getValue());
        assertEquals(nanos, t.getNanoOfSecond().getValue());
    }

    public void test_minusNanos_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusNanos(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    public void test_minusNanos_noChange_oneDay() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusNanos(24 * 60 * 60 * 1000000000L);
        assertEquals(t.getDate(), TEST_2007_07_15_12_30_40_987654321.getDate().minusDays(1));
        assertSame(t.getTime(), TEST_2007_07_15_12_30_40_987654321.getTime());
    }

    public void test_minusNanos_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(0, 0, 0, 1).minusNanos(1);
        assertSame(t.getTime(), LocalTime.MIDNIGHT);
    }

    public void test_minusNanos_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(12, 0, 0, 1).minusNanos(1);
        assertSame(t.getTime(), LocalTime.MIDDAY);
    }

    //-----------------------------------------------------------------------
    // matches()
    //-----------------------------------------------------------------------
    public void test_matches_DateMatcher() {
        assertTrue(TEST_2007_07_15_12_30_40_987654321.matches(Year.isoYear(2007)));
        assertFalse(TEST_2007_07_15_12_30_40_987654321.matches(Year.isoYear(2006)));
        assertTrue(TEST_2007_07_15_12_30_40_987654321.matches(QuarterOfYear.Q3));
        assertFalse(TEST_2007_07_15_12_30_40_987654321.matches(QuarterOfYear.Q2));
        assertTrue(TEST_2007_07_15_12_30_40_987654321.matches(MonthOfYear.JULY));
        assertFalse(TEST_2007_07_15_12_30_40_987654321.matches(MonthOfYear.JUNE));
        assertTrue(TEST_2007_07_15_12_30_40_987654321.matches(DayOfMonth.dayOfMonth(15)));
        assertFalse(TEST_2007_07_15_12_30_40_987654321.matches(DayOfMonth.dayOfMonth(14)));
        assertTrue(TEST_2007_07_15_12_30_40_987654321.matches(DayOfWeek.SUNDAY));
        assertFalse(TEST_2007_07_15_12_30_40_987654321.matches(DayOfWeek.MONDAY));
    }

    public void test_matches_TimeMatcher() {
        assertTrue(TEST_2007_07_15_12_30_40_987654321.matches(HourOfDay.hourOfDay(12)));
        assertFalse(TEST_2007_07_15_12_30_40_987654321.matches(HourOfDay.hourOfDay(0)));
        assertTrue(TEST_2007_07_15_12_30_40_987654321.matches(MinuteOfHour.minuteOfHour(30)));
        assertFalse(TEST_2007_07_15_12_30_40_987654321.matches(MinuteOfHour.minuteOfHour(0)));
        assertTrue(TEST_2007_07_15_12_30_40_987654321.matches(SecondOfMinute.secondOfMinute(40)));
        assertFalse(TEST_2007_07_15_12_30_40_987654321.matches(SecondOfMinute.secondOfMinute(50)));
        assertTrue(TEST_2007_07_15_12_30_40_987654321.matches(NanoOfSecond.nanoOfSecond(987654321)));
        assertFalse(TEST_2007_07_15_12_30_40_987654321.matches(NanoOfSecond.nanoOfSecond(0)));
//        assertTrue(TEST_2007_07_15_12_30_40_987654321.matches(HourOfMeridiem.hourOfMeridiem(0)));
//        assertFalse(TEST_2007_07_15_12_30_40_987654321.matches(HourOfMeridiem.hourOfMeridiem(11)));
        assertTrue(TEST_2007_07_15_12_30_40_987654321.matches(AmPmOfDay.PM));
        assertFalse(TEST_2007_07_15_12_30_40_987654321.matches(AmPmOfDay.AM));
    }

    //-----------------------------------------------------------------------
    // toLocalDate()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_toLocalDate(int year, int month, int day) {
        LocalDate d = LocalDate.date(year, month, day);
        LocalDateTime dt = LocalDateTime.dateMidnight(d);
        assertSame(dt.toLocalDate(), d);
    }

    //-----------------------------------------------------------------------
    // toLocalTime()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes")
    public void test_toLocalTime(int h, int m, int s, int ns) {
        LocalTime t = LocalTime.time(h, m, s, ns);
        LocalDateTime dt = LocalDateTime.dateTime(TEST_2007_07_15_12_30_40_987654321, t);
        assertSame(dt.toLocalTime(), t);
    }

    //-----------------------------------------------------------------------
    // toCalendrical()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_toCalendrical(int year, int month, int day) {
        LocalDate d = LocalDate.date(year, month, day);
        LocalDateTime dt = LocalDateTime.dateMidnight(d);
        assertEquals(dt.toCalendrical(), new Calendrical(d, LocalTime.MIDNIGHT, null, null));
    }

    @Test(dataProvider="sampleTimes")
    public void test_toCalendrical(int h, int m, int s, int ns) {
        LocalTime t = LocalTime.time(h, m, s, ns);
        LocalDateTime dt = LocalDateTime.dateTime(TEST_2007_07_15_12_30_40_987654321, t);
        assertEquals(dt.toCalendrical(), new Calendrical(TEST_2007_07_15_12_30_40_987654321.toLocalDate(), t, null, null));
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_comparisons() {
        test_comparisons_LocalDateTime(
            LocalDate.date(Year.MIN_YEAR, 1, 1),
            LocalDate.date(Year.MIN_YEAR, 12, 31),
            LocalDate.date(-1, 1, 1),
            LocalDate.date(-1, 12, 31),
            LocalDate.date(0, 1, 1),
            LocalDate.date(0, 12, 31),
            LocalDate.date(1, 1, 1),
            LocalDate.date(1, 12, 31),
            LocalDate.date(2008, 1, 1),
            LocalDate.date(2008, 2, 29),
            LocalDate.date(2008, 12, 31),
            LocalDate.date(Year.MAX_YEAR, 1, 1),
            LocalDate.date(Year.MAX_YEAR, 12, 31)
        );
    }

    void test_comparisons_LocalDateTime(LocalDate... localDates) {
        test_comparisons_LocalDateTime(
            localDates,
            LocalTime.MIDNIGHT,
            LocalTime.time(0, 0, 0, 999999999),
            LocalTime.time(0, 0, 59, 0),
            LocalTime.time(0, 0, 59, 999999999),
            LocalTime.time(0, 59, 0, 0),
            LocalTime.time(0, 59, 59, 999999999),
            LocalTime.MIDDAY,
            LocalTime.time(12, 0, 0, 999999999),
            LocalTime.time(12, 0, 59, 0),
            LocalTime.time(12, 0, 59, 999999999),
            LocalTime.time(12, 59, 0, 0),
            LocalTime.time(12, 59, 59, 999999999),
            LocalTime.time(23, 0, 0, 0),
            LocalTime.time(23, 0, 0, 999999999),
            LocalTime.time(23, 0, 59, 0),
            LocalTime.time(23, 0, 59, 999999999),
            LocalTime.time(23, 59, 0, 0),
            LocalTime.time(23, 59, 59, 999999999)
        );
    }

    void test_comparisons_LocalDateTime(LocalDate[] localDates, LocalTime... localTimes) {
        LocalDateTime[] localDateTimes = new LocalDateTime[localDates.length * localTimes.length];
        int i = 0;

        for (LocalDate localDate : localDates) {
            for (LocalTime localTime : localTimes) {
                localDateTimes[i++] = LocalDateTime.dateTime(localDate, localTime);
            }
        }

        doTest_comparisons_LocalDateTime(localDateTimes);
    }

    void doTest_comparisons_LocalDateTime(LocalDateTime[] localDateTimes) {
        for (int i = 0; i < localDateTimes.length; i++) {
            LocalDateTime a = localDateTimes[i];
            for (int j = 0; j < localDateTimes.length; j++) {
                LocalDateTime b = localDateTimes[j];
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
        TEST_2007_07_15_12_30_40_987654321.compareTo(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isBefore_ObjectNull() {
        TEST_2007_07_15_12_30_40_987654321.isBefore(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isAfter_ObjectNull() {
        TEST_2007_07_15_12_30_40_987654321.isAfter(null);
    }

    @Test(expectedExceptions=ClassCastException.class)
    @SuppressWarnings("unchecked")
    public void compareToNonLocalDateTime() {
       Comparable c = TEST_2007_07_15_12_30_40_987654321;
       c.compareTo(new Object());
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleDateTimes") 
    Iterator<Object[]> provider_sampleDateTimes() {
        return new Iterator<Object[]>() {
            Object[][] sampleDates = provider_sampleDates();
            Object[][] sampleTimes = provider_sampleTimes();
            int datesIndex = 0;
            int timesIndex = 0;

            public boolean hasNext() {
                return datesIndex < sampleDates.length;
            }

            public Object[] next() {
                Object[] sampleDate = sampleDates[datesIndex];
                Object[] sampleTime = sampleTimes[timesIndex];

                Object[] ret = new Object[sampleDate.length + sampleTime.length];

                System.arraycopy(sampleDate, 0, ret, 0, sampleDate.length);
                System.arraycopy(sampleTime, 0, ret, sampleDate.length, sampleTime.length);

                if (++timesIndex == sampleTimes.length) {
                    datesIndex++;
                    timesIndex = 0;
                }
                
                return ret;
            }

            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @Test(dataProvider="sampleDateTimes")
    public void test_equals_true(int y, int m, int d, int h, int mi, int s, int n) {
        LocalDateTime a = LocalDateTime.dateTime(y, m, d, h, mi, s, n);
        LocalDateTime b = LocalDateTime.dateTime(y, m, d, h, mi, s, n);
        assertTrue(a.equals(b));
    }

    @Test(dataProvider="sampleDateTimes")
    public void test_equals_false_year_differs(int y, int m, int d, int h, int mi, int s, int n) {
        LocalDateTime a = LocalDateTime.dateTime(y, m, d, h, mi, s, n);
        LocalDateTime b = LocalDateTime.dateTime(y + 1, m, d, h, mi, s, n);
        assertFalse(a.equals(b));
    }

    @Test(dataProvider="sampleDateTimes")
    public void test_equals_false_month_differs(int y, int m, int d, int h, int mi, int s, int n) {
        LocalDateTime a = LocalDateTime.dateTime(y, m, d, h, mi, s, n);
        LocalDateTime b = LocalDateTime.dateTime(y, m + 1, d, h, mi, s, n);
        assertFalse(a.equals(b));
    }

    @Test(dataProvider="sampleDateTimes")
    public void test_equals_false_day_differs(int y, int m, int d, int h, int mi, int s, int n) {
        LocalDateTime a = LocalDateTime.dateTime(y, m, d, h, mi, s, n);
        LocalDateTime b = LocalDateTime.dateTime(y, m, d + 1, h, mi, s, n);
        assertFalse(a.equals(b));
    }

    @Test(dataProvider="sampleDateTimes")
    public void test_equals_false_hour_differs(int y, int m, int d, int h, int mi, int s, int n) {
        LocalDateTime a = LocalDateTime.dateTime(y, m, d, h, mi, s, n);
        LocalDateTime b = LocalDateTime.dateTime(y, m, d, h + 1, mi, s, n);
        assertFalse(a.equals(b));
    }

    @Test(dataProvider="sampleDateTimes")
    public void test_equals_false_minute_differs(int y, int m, int d, int h, int mi, int s, int n) {
        LocalDateTime a = LocalDateTime.dateTime(y, m, d, h, mi, s, n);
        LocalDateTime b = LocalDateTime.dateTime(y, m, d, h, mi + 1, s, n);
        assertFalse(a.equals(b));
    }

    @Test(dataProvider="sampleDateTimes")
    public void test_equals_false_second_differs(int y, int m, int d, int h, int mi, int s, int n) {
        LocalDateTime a = LocalDateTime.dateTime(y, m, d, h, mi, s, n);
        LocalDateTime b = LocalDateTime.dateTime(y, m, d, h, mi, s + 1, n);
        assertFalse(a.equals(b));
    }

    @Test(dataProvider="sampleDateTimes")
    public void test_equals_false_nano_differs(int y, int m, int d, int h, int mi, int s, int n) {
        LocalDateTime a = LocalDateTime.dateTime(y, m, d, h, mi, s, n);
        LocalDateTime b = LocalDateTime.dateTime(y, m, d, h, mi, s, n + 1);
        assertFalse(a.equals(b));
    }
    
    public void test_equals_itself_true() {
        assertEquals(TEST_2007_07_15_12_30_40_987654321.equals(TEST_2007_07_15_12_30_40_987654321), true);
    }

    public void test_equals_string_false() {
        assertEquals(TEST_2007_07_15_12_30_40_987654321.equals("2007-07-15T12:30:40.987654321"), false);
    }

    public void test_equals_null_false() {
        assertEquals(TEST_2007_07_15_12_30_40_987654321.equals(null), false);
    }

    //-----------------------------------------------------------------------
    // hashCode()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDateTimes")
    public void test_hashCode(int y, int m, int d, int h, int mi, int s, int n) {
        LocalDateTime a = LocalDateTime.dateTime(y, m, d, h, mi, s, n);
        assertEquals(a.hashCode(), a.hashCode());
        LocalDateTime b = LocalDateTime.dateTime(y, m, d, h, mi, s, n);
        assertEquals(a.hashCode(), b.hashCode());
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleToString")
    Object[][] provider_sampleToString() {
        return new Object[][] {
            {2008, 7, 5, 2, 1, 0, 0, "2008-07-05T02:01"},
            {2007, 12, 31, 23, 59, 1, 0, "2007-12-31T23:59:01"},
            {999, 12, 31, 23, 59, 59, 990000000, "0999-12-31T23:59:59.990"},
            {-1, 1, 2, 23, 59, 59, 999990000, "-0001-01-02T23:59:59.999990"},
            {-2008, 1, 2, 23, 59, 59, 999999990, "-2008-01-02T23:59:59.999999990"},
        };
    }

    @Test(dataProvider="sampleToString")
    public void test_toString(int y, int m, int d, int h, int mi, int s, int n, String expected) {
        LocalDateTime t = LocalDateTime.dateTime(y, m, d, h, mi, s, n);
        String str = t.toString();
        assertEquals(str, expected);
    }
    
    //-----------------------------------------------------------------------
    // matchesDate()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_matchesDate_true(int y, int m, int d) {
        LocalDateTime a = TEST_2007_07_15_12_30_40_987654321.withDate(y, m, d);
        LocalDate b = LocalDate.date(y, m, d);
        assertEquals(a.matchesDate(b), true);
    }
    @Test(dataProvider="sampleDates")
    public void test_matchesDate_false_year_differs(int y, int m, int d) {
        LocalDateTime a = TEST_2007_07_15_12_30_40_987654321.withDate(y, m, d);
        LocalDate b = LocalDate.date(y + 1, m, d);
        assertEquals(a.matchesDate(b), false);
    }
    @Test(dataProvider="sampleDates")
    public void test_matchesDate_false_month_differs(int y, int m, int d) {
        LocalDateTime a = TEST_2007_07_15_12_30_40_987654321.withDate(y, m, d);
        LocalDate b = LocalDate.date(y, m + 1, d);
        assertEquals(a.matchesDate(b), false);
    }
    @Test(dataProvider="sampleDates")
    public void test_matchesDate_false_day_differs(int y, int m, int d) {
        LocalDateTime a = TEST_2007_07_15_12_30_40_987654321.withDate(y, m, d);
        LocalDate b = LocalDate.date(y, m, d + 1);
        assertEquals(a.matchesDate(b), false);
    }

    public void test_matchesDate_itself_true() {
        assertTrue(TEST_2007_07_15_12_30_40_987654321.matchesDate(TEST_2007_07_15_12_30_40_987654321.toLocalDate()));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_matchesDate_null() {
        TEST_2007_07_15_12_30_40_987654321.matchesDate(null);
    }
    
    //-----------------------------------------------------------------------
    // matchesTime()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes")
    public void test_matchesTime_true(int h, int m, int s, int n) {
        LocalDateTime a = TEST_2007_07_15_12_30_40_987654321.withTime(h, m, s, n);
        LocalTime b = LocalTime.time(h, m, s, n);
        assertEquals(a.matchesTime(b), true);
    }
    @Test(dataProvider="sampleTimes")
    public void test_matchesTime_false_hour_differs(int h, int m, int s, int n) {
        LocalDateTime a = TEST_2007_07_15_12_30_40_987654321.withTime(h, m, s, n);
        LocalTime b = LocalTime.time(h + 1, m, s, n);
        assertEquals(a.matchesTime(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_matchesTime_false_minute_differs(int h, int m, int s, int n) {
        LocalDateTime a = TEST_2007_07_15_12_30_40_987654321.withTime(h, m, s, n);
        LocalTime b = LocalTime.time(h, m + 1, s, n);
        assertEquals(a.matchesTime(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_matchesTime_false_second_differs(int h, int m, int s, int n) {
        LocalDateTime a = TEST_2007_07_15_12_30_40_987654321.withTime(h, m, s, n);
        LocalTime b = LocalTime.time(h, m, s + 1, n);
        assertEquals(a.matchesTime(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_matchesTime_false_nano_differs(int h, int m, int s, int n) {
        LocalDateTime a = TEST_2007_07_15_12_30_40_987654321.withTime(h, m, s, n);
        LocalTime b = LocalTime.time(h, m, s, n + 1);
        assertEquals(a.matchesTime(b), false);
    }

    public void test_matchesTime_itself_true() {
        assertEquals(TEST_2007_07_15_12_30_40_987654321.matchesTime(TEST_2007_07_15_12_30_40_987654321.toLocalTime()), true);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_matchesTime_null() {
        TEST_2007_07_15_12_30_40_987654321.matchesTime(null);
    }
    
    //-----------------------------------------------------------------------
    // adjustDate()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_adjustDate(int y, int m, int d) {
        LocalDateTime a = TEST_2007_07_15_12_30_40_987654321.withDate(y, m, d);
        assertSame(a.adjustDate(TEST_2007_07_15_12_30_40_987654321.toLocalDate()), a.toLocalDate());
        assertSame(TEST_2007_07_15_12_30_40_987654321.adjustDate(a.toLocalDate()), TEST_2007_07_15_12_30_40_987654321.toLocalDate());
    }

    public void test_adjustDate_same() {
        assertSame(LocalDateTime.dateMidnight(2007, 7, 15).adjustDate(TEST_2007_07_15_12_30_40_987654321.toLocalDate()), 
                TEST_2007_07_15_12_30_40_987654321.toLocalDate());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustDate_null() {
        TEST_2007_07_15_12_30_40_987654321.adjustDate(null);
    }
    
    //-----------------------------------------------------------------------
    // adjustTime()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes")
    public void test_adjustTime(int h, int m, int s, int n) {
        LocalDateTime a = TEST_2007_07_15_12_30_40_987654321.withTime(h, m, s, n);
        assertSame(a.adjustTime(TEST_2007_07_15_12_30_40_987654321.toLocalTime()), a.toLocalTime());
        assertSame(TEST_2007_07_15_12_30_40_987654321.adjustTime(a.toLocalTime()), TEST_2007_07_15_12_30_40_987654321.toLocalTime());
    }

    public void test_adjustTime_same() {
        assertSame(LocalDateTime.dateTime(1, 1, 1, 12, 30, 40, 987654321).adjustTime(TEST_2007_07_15_12_30_40_987654321.toLocalTime()), 
                TEST_2007_07_15_12_30_40_987654321.toLocalTime());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustTime_null() {
        TEST_2007_07_15_12_30_40_987654321.adjustTime(null);
    }
}
