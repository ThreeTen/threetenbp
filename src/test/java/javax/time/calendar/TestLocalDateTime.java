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

import javax.time.calendar.field.DayOfMonth;
import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.DayOfYear;
import javax.time.calendar.field.Era;
import javax.time.calendar.field.HourOfDay;
import javax.time.calendar.field.HourOfMeridiem;
import javax.time.calendar.field.MeridiemOfDay;
import javax.time.calendar.field.MinuteOfHour;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.NanoOfSecond;
import javax.time.calendar.field.QuarterOfYear;
import javax.time.calendar.field.SecondOfMinute;
import javax.time.calendar.field.WeekOfMonth;
import javax.time.calendar.field.WeekOfWeekyear;
import javax.time.calendar.field.Weekyear;
import javax.time.calendar.field.Year;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test LocalDate.
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
        assertTrue(TEST_2007_07_15_12_30_40_987654321 instanceof Calendrical);
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

            public FlexiDateTime toFlexiDateTime() {
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

            public FlexiDateTime toFlexiDateTime() {
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

            public FlexiDateTime toFlexiDateTime() {
                return null;
            }
        });
    }

    //-----------------------------------------------------------------------
    public void test_getChronology() {
        assertSame(ISOChronology.INSTANCE, TEST_2007_07_15_12_30_40_987654321.getChronology());
    }

    //-----------------------------------------------------------------------
    //TODO: implement this test
    public void test_isSupported() {
//        assertTrue(TEST_2007_07_15_12_30_40_987654321.isSupported(Era.RULE));
//        assertTrue(TEST_2007_07_15_12_30_40_987654321.isSupported(MilleniumOfEra.RULE));
//        assertTrue(TEST_2007_07_15_12_30_40_987654321.isSupported(CenturyOfEra.RULE));
//        assertTrue(TEST_2007_07_15_12_30_40_987654321.isSupported(DecadeOfCentury.RULE));
//        assertTrue(TEST_2007_07_15_12_30_40_987654321.isSupported(Year.rule()));
//        assertTrue(TEST_2007_07_15_12_30_40_987654321.isSupported(YearOfEra.RULE));
//        assertTrue(TEST_2007_07_15_12_30_40_987654321.isSupported(QuarterOfYear.rule()));
//        assertTrue(TEST_2007_07_15_12_30_40_987654321.isSupported(MonthOfYear.rule()));
//        assertTrue(TEST_2007_07_15_12_30_40_987654321.isSupported(MonthOfQuarter.RULE));
//        assertTrue(TEST_2007_07_15_12_30_40_987654321.isSupported(DayOfMonth.rule()));
//        assertTrue(TEST_2007_07_15_12_30_40_987654321.isSupported(DayOfWeek.rule()));
//        assertTrue(TEST_2007_07_15_12_30_40_987654321.isSupported(DayOfYear.rule()));
//        assertTrue(TEST_2007_07_15_12_30_40_987654321.isSupported(WeekOfMonth.rule()));
//        assertTrue(TEST_2007_07_15_12_30_40_987654321.isSupported(WeekOfWeekyear.rule()));
//        assertTrue(TEST_2007_07_15_12_30_40_987654321.isSupported(Weekyear.rule()));
//
//        assertFalse(TEST_2007_07_15_12_30_40_987654321.isSupported(HourOfDay.RULE));
//        assertFalse(TEST_2007_07_15_12_30_40_987654321.isSupported(MinuteOfHour.RULE));
//        assertFalse(TEST_2007_07_15_12_30_40_987654321.isSupported(MinuteOfDay.RULE));
//        assertFalse(TEST_2007_07_15_12_30_40_987654321.isSupported(SecondOfMinute.RULE));
//        assertFalse(TEST_2007_07_15_12_30_40_987654321.isSupported(SecondOfDay.RULE));
//        assertFalse(TEST_2007_07_15_12_30_40_987654321.isSupported(NanoOfSecond.RULE));
//        assertFalse(TEST_2007_07_15_12_30_40_987654321.isSupported(HourOfMeridiem.RULE));
//        assertFalse(TEST_2007_07_15_12_30_40_987654321.isSupported(MeridiemOfDay.RULE));
    }

    // TODO: enable all assertions
    public void test_get() {
        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(Era.RULE), Era.AD.getValue());
//        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(MilleniumOfEra.RULE), TEST_2007_07_15_12_30_40_987654321.getYear().getMilleniumOfEra());
//        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(CenturyOfEra.RULE), TEST_2007_07_15_12_30_40_987654321.getYear().getCenturyOfEra());
//        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(DecadeOfCentury.RULE), TEST_2007_07_15_12_30_40_987654321.getYear().getDecadeOfCentury());
        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(Year.rule()), 2007);
//        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(YearOfEra.RULE), TEST_2007_07_15_12_30_40_987654321.getYear().getYearOfEra());
        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(QuarterOfYear.rule()), 3);
        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(MonthOfYear.rule()), 7);
//        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(MonthOfQuarter.RULE), TEST_2007_07_15_12_30_40_987654321.getMonthOfYear().getMonthOfQuarter());
        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(DayOfMonth.rule()), 15);
        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(DayOfWeek.rule()), TEST_2007_07_15_12_30_40_987654321.getDayOfWeek().getValue());
        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(DayOfYear.rule()), TEST_2007_07_15_12_30_40_987654321.getDayOfYear().getValue());
        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(WeekOfMonth.rule()), WeekOfMonth.weekOfMonth(TEST_2007_07_15_12_30_40_987654321).getValue());
        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(WeekOfWeekyear.rule()), WeekOfWeekyear.weekOfWeekyear(TEST_2007_07_15_12_30_40_987654321).getValue());
        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(Weekyear.rule()), Weekyear.weekyear(TEST_2007_07_15_12_30_40_987654321).getValue());

        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(HourOfDay.RULE), 12);
        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(MinuteOfHour.RULE), 30);
        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(SecondOfMinute.RULE), 40);
        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(NanoOfSecond.RULE), 987654321);
        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(HourOfMeridiem.RULE), 0);
        assertEquals(TEST_2007_07_15_12_30_40_987654321.get(MeridiemOfDay.RULE), MeridiemOfDay.PM.getValue());
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
    public void test_with_DateAdjustor() {
        DateAdjustor dateAdjustor = DateAdjustors.lastDayOfMonth();
        LocalDateTime adjusted = TEST_2007_07_15_12_30_40_987654321.with(dateAdjustor);
        assertEquals(adjusted.toLocalDate(), dateAdjustor.adjustDate(TEST_2007_07_15_12_30_40_987654321.toLocalDate()));
        assertSame(adjusted.toLocalTime(), TEST_2007_07_15_12_30_40_987654321.toLocalTime());
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_with_null_adjustDate() {
        TEST_2007_07_15_12_30_40_987654321.with(new DateAdjustor() {
            public LocalDate adjustDate(LocalDate date) {
                return null;
            }
        });
    }

    public void test_with_TimeAdjustor() {
        TimeAdjustor timeAdjustor = MeridiemOfDay.AM;
        LocalDateTime adjusted = TEST_2007_07_15_12_30_40_987654321.with(timeAdjustor);
        assertSame(adjusted.toLocalDate(), TEST_2007_07_15_12_30_40_987654321.toLocalDate());
        assertSame(adjusted.toLocalTime().getHourOfDay().getValue(), 0);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_with_null_adjustTime() {
        TEST_2007_07_15_12_30_40_987654321.with(new TimeAdjustor() {
            public LocalTime adjustTime(LocalTime time) {
                return null;
            }
        });
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

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
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

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
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

//    //-----------------------------------------------------------------------
//    // Since plusDays/minusDays actually depends on MJDays, it cannot be used for testing
//    private LocalDate next(LocalDate date) {
//        int newDayOfMonth = date.getDayOfMonth().getValue() + 1;
//
//        if (newDayOfMonth <= date.getMonthOfYear().lengthInDays(date.getYear())) {
//            return date.withDayOfMonth(newDayOfMonth);
//        }
//
//        date = date.withDayOfMonth(1);
//
//        if (date.getMonthOfYear() == MonthOfYear.DECEMBER) {
//            date = date.with(date.getYear().next());
//        }
//
//        return date.with(date.getMonthOfYear().next());
//    }
//
//    private LocalDate previous(LocalDate date) {
//        int newDayOfMonth = date.getDayOfMonth().getValue() - 1;
//
//        if (newDayOfMonth > 0) {
//            return date.withDayOfMonth(newDayOfMonth);
//        }
//
//        date = date.with(date.getMonthOfYear().previous());
//
//        if (date.getMonthOfYear() == MonthOfYear.DECEMBER) {
//            date = date.with(date.getYear().previous());
//        }
//
//        return date.with(date.getMonthOfYear().getLastDayOfMonth(date.getYear()));
//    }
//
//    //-----------------------------------------------------------------------
//    public void factory_fromMJDays() {
//        LocalDate test = LocalDate.date(0, 1, 1);
//        for (int i = -678941; i < 700000; i++) {
//            assertEquals(LocalDate.fromModifiedJulianDays(i), test);
//            test = next(test);
//        }
//
//        test = LocalDate.date(0, 1, 1);
//        for (int i = -678941; i > -2000000; i--) {
//            assertEquals(LocalDate.fromModifiedJulianDays(i), test);
//            test = previous(test);
//        }
//
//        assertEquals(LocalDate.fromModifiedJulianDays(40587), LocalDate.date(1970, 1, 1));
//        assertEquals(LocalDate.fromModifiedJulianDays(-678942), LocalDate.date(-1, 12, 31));
//    }
//
//    //-----------------------------------------------------------------------
//    // plusYears()
//    //-----------------------------------------------------------------------
//    public void test_plusYears_int_normal() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusYears(1);
//        assertEquals(t, LocalDate.date(2008, 7, 15));
//    }
//
//    public void test_plusYears_int_noChange() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusYears(0);
//        assertEquals(t, LocalDate.date(2007, 7, 15));
//    }
//
//    public void test_plusYears_int_negative() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusYears(-1);
//        assertEquals(t, LocalDate.date(2006, 7, 15));
//    }
//
//    public void test_plusYears_int_adjustDay() {
//        LocalDate t = LocalDate.date(2008, 2, 29).plusYears(1);
//        LocalDate expected = LocalDate.date(2009, 2, 28);
//        assertEquals(t, expected);
//    }
//
//    public void test_plusYears_int_invalidTooLarge() {
//        try {
//            LocalDate.date(Year.MAX_YEAR, 1, 1).plusYears(1);
//            fail();
//        } catch (IllegalCalendarFieldValueException ex) {
//            String actual = Long.toString(((long) Year.MAX_YEAR) + 1);
//            assertEquals(ex.getMessage(), "Illegal value for Year field, value " + actual +
//                " is not in the range " + MIN_YEAR_STR + " to " + MAX_YEAR_STR);
//        }
//    }
//
//    public void test_plusYears_int_invalidTooSmall() {
//        try {
//            LocalDate.date(Year.MIN_YEAR, 1, 1).plusYears(-1);
//            fail();
//        } catch (IllegalCalendarFieldValueException ex) {
//            String actual = Long.toString(((long) Year.MIN_YEAR) - 1);
//            assertEquals(ex.getMessage(), "Illegal value for Year field, value " + actual +
//                " is not in the range " + MIN_YEAR_STR + " to " + MAX_YEAR_STR);
//        }
//    }
//
//    public void test_plusYears_int_DateResolver_normal() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusYears(1, DateResolvers.nextValid());
//        assertEquals(t, LocalDate.date(2008, 7, 15));
//    }
//
//    public void test_plusYears_int_DateResolver_noChange() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusYears(0, DateResolvers.nextValid());
//        assertEquals(t, LocalDate.date(2007, 7, 15));
//    }
//
//    public void test_plusYears_int_DateResolver_negative() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusYears(-1, DateResolvers.nextValid());
//        assertEquals(t, LocalDate.date(2006, 7, 15));
//    }
//
//    public void test_plusYears_int_DateResolver_adjustDay() {
//        LocalDate t = LocalDate.date(2008, 2, 29).plusYears(1, DateResolvers.nextValid());
//        LocalDate expected = LocalDate.date(2009, 3, 1);
//        assertEquals(t, expected);
//    }
//
//    public void test_plusYears_int_DateResolver_invalidTooLarge() {
//        try {
//            LocalDate.date(Year.MAX_YEAR, 1, 1).plusYears(1, DateResolvers.nextValid());
//            fail();
//        } catch (IllegalCalendarFieldValueException ex) {
//            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", ((long) Year.MAX_YEAR) + 1, Year.MIN_YEAR, Year.MAX_YEAR).getMessage());
//        }
//    }
//
//    public void test_plusYears_int_DateResolver_invalidTooSmall() {
//        try {
//            LocalDate.date(Year.MIN_YEAR, 1, 1).plusYears(-1, DateResolvers.nextValid());
//            fail();
//        } catch (IllegalCalendarFieldValueException ex) {
//            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", ((long) Year.MIN_YEAR) - 1, Year.MIN_YEAR, Year.MAX_YEAR).getMessage());
//        }
//    }
//
//    //-----------------------------------------------------------------------
//    // plusMonths()
//    //-----------------------------------------------------------------------
//    public void test_plusMonths_int_normal() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusMonths(1);
//        assertEquals(t, LocalDate.date(2007, 8, 15));
//    }
//
//    public void test_plusMonths_int_noChange() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusMonths(0);
//        assertEquals(t, LocalDate.date(2007, 7, 15));
//    }
//
//    public void test_plusMonths_int_overYears() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusMonths(25);
//        assertEquals(t, LocalDate.date(2009, 8, 15));
//    }
//
//    public void test_plusMonths_int_negative() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusMonths(-1);
//        assertEquals(t, LocalDate.date(2007, 6, 15));
//    }
//
//    public void test_plusMonths_int_negativeAcrossYear() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusMonths(-7);
//        assertEquals(t, LocalDate.date(2006, 12, 15));
//    }
//
//    public void test_plusMonths_int_negativeOverYears() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusMonths(-31);
//        assertEquals(t, LocalDate.date(2004, 12, 15));
//    }
//
//    public void test_plusMonths_int_adjustDayFromLeapYear() {
//        LocalDate t = LocalDate.date(2008, 2, 29).plusMonths(12);
//        LocalDate expected = LocalDate.date(2009, 2, 28);
//        assertEquals(t, expected);
//    }
//
//    public void test_plusMonths_int_adjustDayFromMonthLength() {
//        LocalDate t = LocalDate.date(2007, 3, 31).plusMonths(1);
//        LocalDate expected = LocalDate.date(2007, 4, 30);
//        assertEquals(t, expected);
//    }
//
//    public void test_plusMonths_int_invalidTooLarge() {
//        try {
//            LocalDate.date(Year.MAX_YEAR, 12, 1).plusMonths(1);
//            fail();
//        } catch (IllegalCalendarFieldValueException ex) {
//            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", ((long) Year.MAX_YEAR) + 1, Year.MIN_YEAR, Year.MAX_YEAR).getMessage());
//        }
//    }
//
//    public void test_plusMonths_int_invalidTooSmall() {
//        try {
//            LocalDate.date(Year.MIN_YEAR, 1, 1).plusMonths(-1);
//            fail();
//        } catch (IllegalCalendarFieldValueException ex) {
//            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", ((long) Year.MIN_YEAR) - 1, Year.MIN_YEAR, Year.MAX_YEAR).getMessage());
//        }
//    }
//
//    public void test_plusMonths_int_DateResolver_normal() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusMonths(1, DateResolvers.nextValid());
//        assertEquals(t, LocalDate.date(2007, 8, 15));
//    }
//
//    public void test_plusMonths_int_DateResolver_noChange() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusMonths(0, DateResolvers.nextValid());
//        assertEquals(t, LocalDate.date(2007, 7, 15));
//    }
//
//    public void test_plusMonths_int_DateResolver_overYears() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusMonths(25, DateResolvers.nextValid());
//        assertEquals(t, LocalDate.date(2009, 8, 15));
//    }
//
//    public void test_plusMonths_int_DateResolver_negative() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusMonths(-1, DateResolvers.nextValid());
//        assertEquals(t, LocalDate.date(2007, 6, 15));
//    }
//
//    public void test_plusMonths_int_DateResolver_negativeAcrossYear() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusMonths(-7, DateResolvers.nextValid());
//        assertEquals(t, LocalDate.date(2006, 12, 15));
//    }
//
//    public void test_plusMonths_int_DateResolver_negativeOverYears() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusMonths(-31, DateResolvers.nextValid());
//        assertEquals(t, LocalDate.date(2004, 12, 15));
//    }
//
//    public void test_plusMonths_int_DateResolver_adjustDayFromLeapYear() {
//        LocalDate t = LocalDate.date(2008, 2, 29).plusMonths(12, DateResolvers.nextValid());
//        LocalDate expected = LocalDate.date(2009, 3, 1);
//        assertEquals(t, expected);
//    }
//
//    public void test_plusMonths_int_DateResolver_adjustDayFromMonthLength() {
//        LocalDate t = LocalDate.date(2007, 3, 31).plusMonths(1, DateResolvers.nextValid());
//        LocalDate expected = LocalDate.date(2007, 5, 1);
//        assertEquals(t, expected);
//    }
//
//    public void test_plusMonths_int_DateResolver_invalidTooLarge() {
//        try {
//            LocalDate.date(Year.MAX_YEAR, 12, 1).plusMonths(1, DateResolvers.nextValid());
//            fail();
//        } catch (IllegalCalendarFieldValueException ex) {
//            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", ((long) Year.MAX_YEAR) + 1, Year.MIN_YEAR, Year.MAX_YEAR).getMessage());
//        }
//    }
//
//    public void test_plusMonths_int_DateResolver_invalidTooSmall() {
//        try {
//            LocalDate.date(Year.MIN_YEAR, 1, 1).plusMonths(-1, DateResolvers.nextValid());
//            fail();
//        } catch (IllegalCalendarFieldValueException ex) {
//            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", ((long) Year.MIN_YEAR) - 1, Year.MIN_YEAR, Year.MAX_YEAR).getMessage());
//        }
//    }
//
//    //-----------------------------------------------------------------------
//    // plusWeeks()
//    //-----------------------------------------------------------------------
//    @DataProvider(name="samplePlusWeeksSymmetry")
//    Object[][] provider_samplePlusWeeksSymmetry() {
//        return new Object[][] {
//            {LocalDate.date(-1, 1, 1)},
//            {LocalDate.date(-1, 2, 28)},
//            {LocalDate.date(-1, 3, 1)},
//            {LocalDate.date(-1, 12, 31)},
//            {LocalDate.date(0, 1, 1)},
//            {LocalDate.date(0, 2, 28)},
//            {LocalDate.date(0, 2, 29)},
//            {LocalDate.date(0, 3, 1)},
//            {LocalDate.date(0, 12, 31)},
//            {LocalDate.date(2007, 1, 1)},
//            {LocalDate.date(2007, 2, 28)},
//            {LocalDate.date(2007, 3, 1)},
//            {LocalDate.date(2007, 12, 31)},
//            {LocalDate.date(2008, 1, 1)},
//            {LocalDate.date(2008, 2, 28)},
//            {LocalDate.date(2008, 2, 29)},
//            {LocalDate.date(2008, 3, 1)},
//            {LocalDate.date(2008, 12, 31)},
//            {LocalDate.date(2099, 1, 1)},
//            {LocalDate.date(2099, 2, 28)},
//            {LocalDate.date(2099, 3, 1)},
//            {LocalDate.date(2099, 12, 31)},
//            {LocalDate.date(2100, 1, 1)},
//            {LocalDate.date(2100, 2, 28)},
//            {LocalDate.date(2100, 3, 1)},
//            {LocalDate.date(2100, 12, 31)},
//        };
//    }
//    
//    @Test(dataProvider="samplePlusWeeksSymmetry")
//    private void test_plusWeeks_symmetry(LocalDate reference) {
//        for (int weeks = 0; weeks < 365 * 8; weeks++) {
//            LocalDate t = reference.plusWeeks(weeks).plusWeeks(-weeks);
//            assertEquals(t, reference);
//
//            t = reference.plusWeeks(-weeks).plusWeeks(weeks);
//            assertEquals(t, reference);
//        }
//    }
//
//    public void test_plusWeeks_normal() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusWeeks(1);
//        assertEquals(t, LocalDate.date(2007, 7, 22));
//    }
//
//    public void test_plusWeeks_noChange() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusWeeks(0);
//        assertEquals(t, LocalDate.date(2007, 7, 15));
//    }
//
//    public void test_plusWeeks_overMonths() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusWeeks(9);
//        assertEquals(t, LocalDate.date(2007, 9, 16));
//    }
//
//    public void test_plusWeeks_overYears() {
//        LocalDate t = LocalDate.date(2006, 7, 16).plusWeeks(52);
//        assertEquals(t, TEST_2007_07_15_12_30_40_987654321);
//    }
//
//    public void test_plusWeeks_overLeapYears() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusYears(-1).plusWeeks(104);
//        assertEquals(t, LocalDate.date(2008, 7, 12));
//    }
//
//    public void test_plusWeeks_negative() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusWeeks(-1);
//        assertEquals(t, LocalDate.date(2007, 7, 8));
//    }
//
//    public void test_plusWeeks_negativeAcrossYear() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusWeeks(-28);
//        assertEquals(t, LocalDate.date(2006, 12, 31));
//    }
//
//    public void test_plusWeeks_negativeOverYears() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusWeeks(-104);
//        assertEquals(t, LocalDate.date(2005, 7, 17));
//    }
//
//    public void test_plusWeeks_maximum() {
//        LocalDate t = LocalDate.date(Year.MAX_YEAR, 12, 24).plusWeeks(1);
//        LocalDate expected = LocalDate.date(Year.MAX_YEAR, 12, 31);
//        assertEquals(t, expected);
//    }
//
//    public void test_plusWeeks_minimum() {
//        LocalDate t = LocalDate.date(Year.MIN_YEAR, 1, 8).plusWeeks(-1);
//        LocalDate expected = LocalDate.date(Year.MIN_YEAR, 1, 1);
//        assertEquals(t, expected);
//    }
//
//    public void test_plusWeeks_invalidTooLarge() {
//        try {
//            LocalDate.date(Year.MAX_YEAR, 12, 25).plusWeeks(1);
//            fail();
//        } catch (IllegalCalendarFieldValueException ex) {
//            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", Year.MAX_YEAR + 1L, Year.MIN_YEAR, 
//                    Year.MAX_YEAR).getMessage());
//        }
//    }
//
//    public void test_plusWeeks_invalidTooSmall() {
//        try {
//            LocalDate.date(Year.MIN_YEAR, 1, 7).plusWeeks(-1);
//            fail();
//        } catch (IllegalCalendarFieldValueException ex) {
//            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", Year.MIN_YEAR - 1L, Year.MIN_YEAR, 
//                    Year.MAX_YEAR).getMessage());
//        }
//    }
//
//    //-----------------------------------------------------------------------
//    // plusDays()
//    //-----------------------------------------------------------------------
//    @DataProvider(name="samplePlusDaysSymmetry")
//    Object[][] provider_samplePlusDaysSymmetry() {
//        return new Object[][] {
//            {LocalDate.date(-1, 1, 1)},
//            {LocalDate.date(-1, 2, 28)},
//            {LocalDate.date(-1, 3, 1)},
//            {LocalDate.date(-1, 12, 31)},
//            {LocalDate.date(0, 1, 1)},
//            {LocalDate.date(0, 2, 28)},
//            {LocalDate.date(0, 2, 29)},
//            {LocalDate.date(0, 3, 1)},
//            {LocalDate.date(0, 12, 31)},
//            {LocalDate.date(2007, 1, 1)},
//            {LocalDate.date(2007, 2, 28)},
//            {LocalDate.date(2007, 3, 1)},
//            {LocalDate.date(2007, 12, 31)},
//            {LocalDate.date(2008, 1, 1)},
//            {LocalDate.date(2008, 2, 28)},
//            {LocalDate.date(2008, 2, 29)},
//            {LocalDate.date(2008, 3, 1)},
//            {LocalDate.date(2008, 12, 31)},
//            {LocalDate.date(2099, 1, 1)},
//            {LocalDate.date(2099, 2, 28)},
//            {LocalDate.date(2099, 3, 1)},
//            {LocalDate.date(2099, 12, 31)},
//            {LocalDate.date(2100, 1, 1)},
//            {LocalDate.date(2100, 2, 28)},
//            {LocalDate.date(2100, 3, 1)},
//            {LocalDate.date(2100, 12, 31)},
//        };
//    }
//    
//    @Test(dataProvider="samplePlusDaysSymmetry")
//    private void test_plusDays_symmetry(LocalDate reference) {
//        for (int days = 0; days < 365 * 8; days++) {
//            LocalDate t = reference.plusDays(days).plusDays(-days);
//            assertEquals(t, reference);
//
//            t = reference.plusDays(-days).plusDays(days);
//            assertEquals(t, reference);
//        }
//    }
//
//    public void test_plusDays_normal() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusDays(1);
//        assertEquals(t, LocalDate.date(2007, 7, 16));
//    }
//
//    public void test_plusDays_noChange() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusDays(0);
//        assertEquals(t, LocalDate.date(2007, 7, 15));
//    }
//
//    public void test_plusDays_overMonths() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusDays(62);
//        assertEquals(t, LocalDate.date(2007, 9, 15));
//    }
//
//    public void test_plusDays_overYears() {
//        LocalDate t = LocalDate.date(2006, 7, 14).plusDays(366);
//        assertEquals(t, TEST_2007_07_15_12_30_40_987654321);
//    }
//
//    public void test_plusDays_overLeapYears() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusYears(-1).plusDays(365 + 366);
//        assertEquals(t, LocalDate.date(2008, 7, 15));
//    }
//
//    public void test_plusDays_negative() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusDays(-1);
//        assertEquals(t, LocalDate.date(2007, 7, 14));
//    }
//
//    public void test_plusDays_negativeAcrossYear() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusDays(-196);
//        assertEquals(t, LocalDate.date(2006, 12, 31));
//    }
//
//    public void test_plusDays_negativeOverYears() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusDays(-730);
//        assertEquals(t, LocalDate.date(2005, 7, 15));
//    }
//
//    public void test_plusDays_maximum() {
//        LocalDate t = LocalDate.date(Year.MAX_YEAR, 12, 30).plusDays(1);
//        LocalDate expected = LocalDate.date(Year.MAX_YEAR, 12, 31);
//        assertEquals(t, expected);
//    }
//
//    public void test_plusDays_minimum() {
//        LocalDate t = LocalDate.date(Year.MIN_YEAR, 1, 2).plusDays(-1);
//        LocalDate expected = LocalDate.date(Year.MIN_YEAR, 1, 1);
//        assertEquals(t, expected);
//    }
//
//    public void test_plusDays_invalidTooLarge() {
//        try {
//            LocalDate.date(Year.MAX_YEAR, 12, 31).plusDays(1);
//            fail();
//        } catch (IllegalCalendarFieldValueException ex) {
//            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", Year.MAX_YEAR + 1L, Year.MIN_YEAR, 
//                    Year.MAX_YEAR).getMessage());
//        }
//    }
//
//    public void test_plusDays_invalidTooSmall() {
//        try {
//            LocalDate.date(Year.MIN_YEAR, 1, 1).plusDays(-1);
//            fail();
//        } catch (IllegalCalendarFieldValueException ex) {
//            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", Year.MIN_YEAR - 1L, Year.MIN_YEAR, 
//                    Year.MAX_YEAR).getMessage());
//        }
//    }
//
//    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
//    public void test_plusDays_overflowTooLarge() {
//        LocalDate.date(Year.MAX_YEAR, 12, 31).plusDays(Long.MAX_VALUE);
//    }
//
//    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
//    public void test_plusDays_overflowTooSmall() {
//        LocalDate.date(Year.MIN_YEAR, 1, 1).plusDays(Long.MIN_VALUE);
//    }
//
//    //-----------------------------------------------------------------------
//    // minusYears()
//    //-----------------------------------------------------------------------
//    public void test_minusYears_int_normal() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.minusYears(1);
//        assertEquals(t, LocalDate.date(2006, 7, 15));
//    }
//
//    public void test_minusYears_int_noChange() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.minusYears(0);
//        assertEquals(t, LocalDate.date(2007, 7, 15));
//    }
//
//    public void test_minusYears_int_negative() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.minusYears(-1);
//        assertEquals(t, LocalDate.date(2008, 7, 15));
//    }
//
//    public void test_minusYears_int_adjustDay() {
//        LocalDate t = LocalDate.date(2008, 2, 29).minusYears(1);
//        LocalDate expected = LocalDate.date(2007, 2, 28);
//        assertEquals(t, expected);
//    }
//
//    public void test_minusYears_int_invalidTooLarge() {
//        try {
//            LocalDate.date(Year.MAX_YEAR, 1, 1).minusYears(-1);
//            fail();
//        } catch (IllegalCalendarFieldValueException ex) {
//            String actual = Long.toString(((long) Year.MAX_YEAR) + 1);
//            assertEquals(ex.getMessage(), "Illegal value for Year field, value " + actual +
//                " is not in the range " + MIN_YEAR_STR + " to " + MAX_YEAR_STR);
//        }
//    }
//
//    public void test_minusYears_int_invalidTooSmall() {
//        try {
//            LocalDate.date(Year.MIN_YEAR, 1, 1).minusYears(1);
//            fail();
//        } catch (IllegalCalendarFieldValueException ex) {
//            String actual = Long.toString(((long) Year.MIN_YEAR) - 1);
//            assertEquals(ex.getMessage(), "Illegal value for Year field, value " + actual +
//                " is not in the range " + MIN_YEAR_STR + " to " + MAX_YEAR_STR);
//        }
//    }
//
//    public void test_minusYears_int_DateResolver_normal() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.minusYears(1, DateResolvers.nextValid());
//        assertEquals(t, LocalDate.date(2006, 7, 15));
//    }
//
//    public void test_minusYears_int_DateResolver_noChange() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.minusYears(0, DateResolvers.nextValid());
//        assertEquals(t, LocalDate.date(2007, 7, 15));
//    }
//
//    public void test_minusYears_int_DateResolver_negative() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.minusYears(-1, DateResolvers.nextValid());
//        assertEquals(t, LocalDate.date(2008, 7, 15));
//    }
//
//    public void test_minusYears_int_DateResolver_adjustDay() {
//        LocalDate t = LocalDate.date(2008, 2, 29).minusYears(1, DateResolvers.nextValid());
//        LocalDate expected = LocalDate.date(2007, 3, 1);
//        assertEquals(t, expected);
//    }
//
//    public void test_minusYears_int_DateResolver_invalidTooLarge() {
//        try {
//            LocalDate.date(Year.MAX_YEAR, 1, 1).minusYears(-1, DateResolvers.nextValid());
//            fail();
//        } catch (IllegalCalendarFieldValueException ex) {
//            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", ((long) Year.MAX_YEAR) + 1, Year.MIN_YEAR, Year.MAX_YEAR).getMessage());
//        }
//    }
//
//    public void test_minusYears_int_DateResolver_invalidTooSmall() {
//        try {
//            LocalDate.date(Year.MIN_YEAR, 1, 1).minusYears(1, DateResolvers.nextValid());
//            fail();
//        } catch (IllegalCalendarFieldValueException ex) {
//            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", ((long) Year.MIN_YEAR) - 1, Year.MIN_YEAR, Year.MAX_YEAR).getMessage());
//        }
//    }
//
//    //-----------------------------------------------------------------------
//    // minusMonths()
//    //-----------------------------------------------------------------------
//    public void test_minusMonths_int_normal() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.minusMonths(1);
//        assertEquals(t, LocalDate.date(2007, 6, 15));
//    }
//
//    public void test_minusMonths_int_noChange() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.minusMonths(0);
//        assertEquals(t, LocalDate.date(2007, 7, 15));
//    }
//
//    public void test_minusMonths_int_overYears() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.minusMonths(25);
//        assertEquals(t, LocalDate.date(2005, 6, 15));
//    }
//
//    public void test_minusMonths_int_negative() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.minusMonths(-1);
//        assertEquals(t, LocalDate.date(2007, 8, 15));
//    }
//
//    public void test_minusMonths_int_negativeAcrossYear() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.minusMonths(-7);
//        assertEquals(t, LocalDate.date(2008, 2, 15));
//    }
//
//    public void test_minusMonths_int_negativeOverYears() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.minusMonths(-31);
//        assertEquals(t, LocalDate.date(2010, 2, 15));
//    }
//
//    public void test_minusMonths_int_adjustDayFromLeapYear() {
//        LocalDate t = LocalDate.date(2008, 2, 29).minusMonths(12);
//        LocalDate expected = LocalDate.date(2007, 2, 28);
//        assertEquals(t, expected);
//    }
//
//    public void test_minusMonths_int_adjustDayFromMonthLength() {
//        LocalDate t = LocalDate.date(2007, 3, 31).minusMonths(1);
//        LocalDate expected = LocalDate.date(2007, 2, 28);
//        assertEquals(t, expected);
//    }
//
//    public void test_minusMonths_int_invalidTooLarge() {
//        try {
//            LocalDate.date(Year.MAX_YEAR, 12, 1).minusMonths(-1);
//            fail();
//        } catch (IllegalCalendarFieldValueException ex) {
//            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", ((long) Year.MAX_YEAR) + 1, Year.MIN_YEAR, Year.MAX_YEAR).getMessage());
//        }
//    }
//
//    public void test_minusMonths_int_invalidTooSmall() {
//        try {
//            LocalDate t = LocalDate.date(Year.MIN_YEAR, 1, 1).minusMonths(1);
//            fail(t.toString());
//        } catch (IllegalCalendarFieldValueException ex) {
//            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", ((long) Year.MIN_YEAR) - 1, Year.MIN_YEAR, Year.MAX_YEAR).getMessage());
//        }
//    }
//
//    public void test_minusMonths_int_DateResolver_normal() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.minusMonths(1, DateResolvers.nextValid());
//        assertEquals(t, LocalDate.date(2007, 6, 15));
//    }
//
//    public void test_minusMonths_int_DateResolver_noChange() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.minusMonths(0, DateResolvers.nextValid());
//        assertEquals(t, LocalDate.date(2007, 7, 15));
//    }
//
//    public void test_minusMonths_int_DateResolver_overYears() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.minusMonths(25, DateResolvers.nextValid());
//        assertEquals(t, LocalDate.date(2005, 6, 15));
//    }
//
//    public void test_minusMonths_int_DateResolver_negative() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.minusMonths(-1, DateResolvers.nextValid());
//        assertEquals(t, LocalDate.date(2007, 8, 15));
//    }
//
//    public void test_minusMonths_int_DateResolver_negativeAcrossYear() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.minusMonths(-7, DateResolvers.nextValid());
//        assertEquals(t, LocalDate.date(2008, 2, 15));
//    }
//
//    public void test_minusMonths_int_DateResolver_negativeOverYears() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.minusMonths(-31, DateResolvers.nextValid());
//        assertEquals(t, LocalDate.date(2010, 2, 15));
//    }
//
//    public void test_minusMonths_int_DateResolver_adjustDayFromLeapYear() {
//        LocalDate t = LocalDate.date(2008, 2, 29).minusMonths(12, DateResolvers.nextValid());
//        LocalDate expected = LocalDate.date(2007, 3, 1);
//        assertEquals(t, expected);
//    }
//
//    public void test_minusMonths_int_DateResolver_adjustDayFromMonthLength() {
//        LocalDate t = LocalDate.date(2007, 3, 31).minusMonths(1, DateResolvers.nextValid());
//        LocalDate expected = LocalDate.date(2007, 3, 1);
//        assertEquals(t, expected);
//    }
//
//    public void test_minusMonths_int_DateResolver_invalidTooLarge() {
//        try {
//            LocalDate.date(Year.MAX_YEAR, 12, 1).minusMonths(-1, DateResolvers.nextValid());
//            fail();
//        } catch (IllegalCalendarFieldValueException ex) {
//            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", ((long) Year.MAX_YEAR) + 1, Year.MIN_YEAR, Year.MAX_YEAR).getMessage());
//        }
//    }
//
//    public void test_minusMonths_int_DateResolver_invalidTooSmall() {
//        try {
//            LocalDate.date(Year.MIN_YEAR, 1, 1).minusMonths(1, DateResolvers.nextValid());
//            fail();
//        } catch (IllegalCalendarFieldValueException ex) {
//            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", ((long) Year.MIN_YEAR) - 1, Year.MIN_YEAR, Year.MAX_YEAR).getMessage());
//        }
//    }
//
//    //-----------------------------------------------------------------------
//    // minusWeeks()
//    //-----------------------------------------------------------------------
//    @DataProvider(name="sampleMinusWeeksSymmetry")
//    Object[][] provider_sampleMinusWeeksSymmetry() {
//        return new Object[][] {
//            {LocalDate.date(-1, 1, 1)},
//            {LocalDate.date(-1, 2, 28)},
//            {LocalDate.date(-1, 3, 1)},
//            {LocalDate.date(-1, 12, 31)},
//            {LocalDate.date(0, 1, 1)},
//            {LocalDate.date(0, 2, 28)},
//            {LocalDate.date(0, 2, 29)},
//            {LocalDate.date(0, 3, 1)},
//            {LocalDate.date(0, 12, 31)},
//            {LocalDate.date(2007, 1, 1)},
//            {LocalDate.date(2007, 2, 28)},
//            {LocalDate.date(2007, 3, 1)},
//            {LocalDate.date(2007, 12, 31)},
//            {LocalDate.date(2008, 1, 1)},
//            {LocalDate.date(2008, 2, 28)},
//            {LocalDate.date(2008, 2, 29)},
//            {LocalDate.date(2008, 3, 1)},
//            {LocalDate.date(2008, 12, 31)},
//            {LocalDate.date(2099, 1, 1)},
//            {LocalDate.date(2099, 2, 28)},
//            {LocalDate.date(2099, 3, 1)},
//            {LocalDate.date(2099, 12, 31)},
//            {LocalDate.date(2100, 1, 1)},
//            {LocalDate.date(2100, 2, 28)},
//            {LocalDate.date(2100, 3, 1)},
//            {LocalDate.date(2100, 12, 31)},
//        };
//    }
//    
//    @Test(dataProvider="sampleMinusWeeksSymmetry")
//    private void test_minusWeeks_symmetry(LocalDate reference) {
//        for (int weeks = 0; weeks < 365 * 8; weeks++) {
//            LocalDate t = reference.minusWeeks(weeks).minusWeeks(-weeks);
//            assertEquals(t, reference);
//
//            t = reference.minusWeeks(-weeks).minusWeeks(weeks);
//            assertEquals(t, reference);
//        }
//    }
//
//    public void test_minusWeeks_normal() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.minusWeeks(1);
//        assertEquals(t, LocalDate.date(2007, 7, 8));
//    }
//
//    public void test_minusWeeks_noChange() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.minusWeeks(0);
//        assertEquals(t, LocalDate.date(2007, 7, 15));
//    }
//
//    public void test_minusWeeks_overMonths() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.minusWeeks(9);
//        assertEquals(t, LocalDate.date(2007, 5, 13));
//    }
//
//    public void test_minusWeeks_overYears() {
//        LocalDate t = LocalDate.date(2008, 7, 13).minusWeeks(52);
//        assertEquals(t, TEST_2007_07_15_12_30_40_987654321);
//    }
//
//    public void test_minusWeeks_overLeapYears() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.minusYears(-1).minusWeeks(104);
//        assertEquals(t, LocalDate.date(2006, 7, 18));
//    }
//
//    public void test_minusWeeks_negative() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.minusWeeks(-1);
//        assertEquals(t, LocalDate.date(2007, 7, 22));
//    }
//
//    public void test_minusWeeks_negativeAcrossYear() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.minusWeeks(-28);
//        assertEquals(t, LocalDate.date(2008, 1, 27));
//    }
//
//    public void test_minusWeeks_negativeOverYears() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.minusWeeks(-104);
//        assertEquals(t, LocalDate.date(2009, 7, 12));
//    }
//
//    public void test_minusWeeks_maximum() {
//        LocalDate t = LocalDate.date(Year.MAX_YEAR, 12, 24).minusWeeks(-1);
//        LocalDate expected = LocalDate.date(Year.MAX_YEAR, 12, 31);
//        assertEquals(t, expected);
//    }
//
//    public void test_minusWeeks_minimum() {
//        LocalDate t = LocalDate.date(Year.MIN_YEAR, 1, 8).minusWeeks(1);
//        LocalDate expected = LocalDate.date(Year.MIN_YEAR, 1, 1);
//        assertEquals(t, expected);
//    }
//
//    public void test_minusWeeks_invalidTooLarge() {
//        try {
//            LocalDate.date(Year.MAX_YEAR, 12, 25).minusWeeks(-1);
//            fail();
//        } catch (IllegalCalendarFieldValueException ex) {
//            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", Year.MAX_YEAR + 1L, Year.MIN_YEAR, 
//                    Year.MAX_YEAR).getMessage());
//        }
//    }
//
//    public void test_minusWeeks_invalidTooSmall() {
//        try {
//            LocalDate.date(Year.MIN_YEAR, 1, 7).minusWeeks(1);
//            fail();
//        } catch (IllegalCalendarFieldValueException ex) {
//            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", Year.MIN_YEAR - 1L, Year.MIN_YEAR, 
//                    Year.MAX_YEAR).getMessage());
//        }
//    }
//
//    //-----------------------------------------------------------------------
//    // minusDays()
//    //-----------------------------------------------------------------------
//    @DataProvider(name="sampleMinusDaysSymmetry")
//    Object[][] provider_sampleMinusDaysSymmetry() {
//        return new Object[][] {
//            {LocalDate.date(-1, 1, 1)},
//            {LocalDate.date(-1, 2, 28)},
//            {LocalDate.date(-1, 3, 1)},
//            {LocalDate.date(-1, 12, 31)},
//            {LocalDate.date(0, 1, 1)},
//            {LocalDate.date(0, 2, 28)},
//            {LocalDate.date(0, 2, 29)},
//            {LocalDate.date(0, 3, 1)},
//            {LocalDate.date(0, 12, 31)},
//            {LocalDate.date(2007, 1, 1)},
//            {LocalDate.date(2007, 2, 28)},
//            {LocalDate.date(2007, 3, 1)},
//            {LocalDate.date(2007, 12, 31)},
//            {LocalDate.date(2008, 1, 1)},
//            {LocalDate.date(2008, 2, 28)},
//            {LocalDate.date(2008, 2, 29)},
//            {LocalDate.date(2008, 3, 1)},
//            {LocalDate.date(2008, 12, 31)},
//            {LocalDate.date(2099, 1, 1)},
//            {LocalDate.date(2099, 2, 28)},
//            {LocalDate.date(2099, 3, 1)},
//            {LocalDate.date(2099, 12, 31)},
//            {LocalDate.date(2100, 1, 1)},
//            {LocalDate.date(2100, 2, 28)},
//            {LocalDate.date(2100, 3, 1)},
//            {LocalDate.date(2100, 12, 31)},
//        };
//    }
//    
//    @Test(dataProvider="sampleMinusDaysSymmetry")
//    private void test_minusDays_symmetry(LocalDate reference) {
//        for (int days = 0; days < 365 * 8; days++) {
//            LocalDate t = reference.minusDays(days).minusDays(-days);
//            assertEquals(t, reference);
//
//            t = reference.minusDays(-days).minusDays(days);
//            assertEquals(t, reference);
//        }
//    }
//
//    public void test_minusDays_normal() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.minusDays(1);
//        assertEquals(t, LocalDate.date(2007, 7, 14));
//    }
//
//    public void test_minusDays_noChange() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.minusDays(0);
//        assertEquals(t, LocalDate.date(2007, 7, 15));
//    }
//
//    public void test_minusDays_overMonths() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.minusDays(62);
//        assertEquals(t, LocalDate.date(2007, 5, 14));
//    }
//
//    public void test_minusDays_overYears() {
//        LocalDate t = LocalDate.date(2008, 7, 16).minusDays(367);
//        assertEquals(t, TEST_2007_07_15_12_30_40_987654321);
//    }
//
//    public void test_minusDays_overLeapYears() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.plusYears(2).minusDays(365 + 366);
//        assertEquals(t, TEST_2007_07_15_12_30_40_987654321);
//    }
//
//    public void test_minusDays_negative() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.minusDays(-1);
//        assertEquals(t, LocalDate.date(2007, 7, 16));
//    }
//
//    public void test_minusDays_negativeAcrossYear() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.minusDays(-169);
//        assertEquals(t, LocalDate.date(2007, 12, 31));
//    }
//
//    public void test_minusDays_negativeOverYears() {
//        LocalDate t = TEST_2007_07_15_12_30_40_987654321.minusDays(-731);
//        assertEquals(t, LocalDate.date(2009, 7, 15));
//    }
//
//    public void test_minusDays_maximum() {
//        LocalDate t = LocalDate.date(Year.MAX_YEAR, 12, 30).minusDays(-1);
//        LocalDate expected = LocalDate.date(Year.MAX_YEAR, 12, 31);
//        assertEquals(t, expected);
//    }
//
//    public void test_minusDays_minimum() {
//        LocalDate t = LocalDate.date(Year.MIN_YEAR, 1, 2).minusDays(1);
//        LocalDate expected = LocalDate.date(Year.MIN_YEAR, 1, 1);
//        assertEquals(t, expected);
//    }
//
//    public void test_minusDays_invalidTooLarge() {
//        try {
//            LocalDate.date(Year.MAX_YEAR, 12, 31).minusDays(-1);
//            fail();
//        } catch (IllegalCalendarFieldValueException ex) {
//            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", Year.MAX_YEAR + 1L, Year.MIN_YEAR, 
//                    Year.MAX_YEAR).getMessage());
//        }
//    }
//
//    public void test_minusDays_invalidTooSmall() {
//        try {
//            LocalDate.date(Year.MIN_YEAR, 1, 1).minusDays(1);
//            fail();
//        } catch (IllegalCalendarFieldValueException ex) {
//            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", Year.MIN_YEAR - 1L, Year.MIN_YEAR, 
//                    Year.MAX_YEAR).getMessage());
//        }
//    }
//
//    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
//    public void test_minusDays_overflowTooLarge() {
//        LocalDate.date(Year.MAX_YEAR, 12, 31).minusDays(Long.MIN_VALUE);
//    }
//
//    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
//    public void test_minusDays_overflowTooSmall() {
//        LocalDate.date(Year.MIN_YEAR, 1, 1).minusDays(Long.MAX_VALUE);
//    }
//
//    //-----------------------------------------------------------------------
//    // matches()
//    //-----------------------------------------------------------------------
//    public void test_matches() {
//        assertTrue(TEST_2007_07_15_12_30_40_987654321.matches(Era.AD));
//        assertFalse(TEST_2007_07_15_12_30_40_987654321.matches(Era.BC));
//        assertTrue(TEST_2007_07_15_12_30_40_987654321.matches(Year.isoYear(2007)));
//        assertFalse(TEST_2007_07_15_12_30_40_987654321.matches(Year.isoYear(2006)));
//        assertTrue(TEST_2007_07_15_12_30_40_987654321.matches(QuarterOfYear.Q3));
//        assertFalse(TEST_2007_07_15_12_30_40_987654321.matches(QuarterOfYear.Q2));
//        assertTrue(TEST_2007_07_15_12_30_40_987654321.matches(MonthOfYear.JULY));
//        assertFalse(TEST_2007_07_15_12_30_40_987654321.matches(MonthOfYear.JUNE));
//        assertTrue(TEST_2007_07_15_12_30_40_987654321.matches(DayOfMonth.dayOfMonth(15)));
//        assertFalse(TEST_2007_07_15_12_30_40_987654321.matches(DayOfMonth.dayOfMonth(14)));
//        assertTrue(TEST_2007_07_15_12_30_40_987654321.matches(DayOfWeek.SUNDAY));
//        assertFalse(TEST_2007_07_15_12_30_40_987654321.matches(DayOfWeek.MONDAY));
//    }
//
//    //-----------------------------------------------------------------------
//    // toLocalDate()
//    //-----------------------------------------------------------------------
//    @Test(dataProvider="sampleDates")
//    public void test_toLocalDate(int year, int month, int day) {
//        LocalDate t = LocalDate.date(year, month, day);
//        assertSame(t.toLocalDate(), t);
//    }
//
//    //-----------------------------------------------------------------------
//    // toFlexiDateTime()
//    //-----------------------------------------------------------------------
//    @Test(dataProvider="sampleDates")
//    public void test_toFlexiDateTime(int year, int month, int day) {
//        LocalDate t = LocalDate.date(year, month, day);
//        assertEquals(t.toFlexiDateTime(), new FlexiDateTime(t, null, null, null));
//    }
//
//    //-----------------------------------------------------------------------
//    // toModifiedJulianDays()
//    //-----------------------------------------------------------------------
//    public void test_toMJDays() {
//        LocalDate test = LocalDate.date(0, 1, 1);
//        for (int i = -678941; i < 700000; i++) {
//            assertEquals(test.toModifiedJulianDays(), i);
//            test = next(test);
//        }
//        
//        test = LocalDate.date(0, 1, 1);
//        for (int i = -678941; i > -2000000; i--) {
//            assertEquals(test.toModifiedJulianDays(), i);
//            test = previous(test);
//        }
//
//        assertEquals(LocalDate.date(1970, 1, 1).toModifiedJulianDays(), 40587);
//        assertEquals(LocalDate.date(-1, 12, 31).toModifiedJulianDays(), -678942);
//    }
//
//    public void test_toMJDays_fromMJDays_simmetry() {
//        LocalDate test = LocalDate.date(0, 1, 1);
//        for (int i = -678941; i < 700000; i++) {
//            assertEquals(LocalDate.fromModifiedJulianDays(test.toModifiedJulianDays()), test);
//            test = next(test);
//        }
//
//        test = LocalDate.date(0, 1, 1);
//        for (int i = -678941; i > -2000000; i--) {
//            assertEquals(LocalDate.fromModifiedJulianDays(test.toModifiedJulianDays()), test);
//            test = previous(test);
//        }
//    }
//
//    //-----------------------------------------------------------------------
//    // compareTo()
//    //-----------------------------------------------------------------------
//    public void test_comparisons() {
//        doTest_comparisons_LocalDate(
//            LocalDate.date(Year.MIN_YEAR, 1, 1),
//            LocalDate.date(Year.MIN_YEAR, 12, 31),
//            LocalDate.date(-1, 1, 1),
//            LocalDate.date(-1, 12, 31),
//            LocalDate.date(0, 1, 1),
//            LocalDate.date(0, 12, 31),
//            LocalDate.date(1, 1, 1),
//            LocalDate.date(1, 12, 31),
//            LocalDate.date(2006, 1, 1),
//            LocalDate.date(2006, 12, 31),
//            LocalDate.date(2007, 1, 1),
//            LocalDate.date(2007, 12, 31),
//            LocalDate.date(2008, 1, 1),
//            LocalDate.date(2008, 2, 29),
//            LocalDate.date(2008, 12, 31),
//            LocalDate.date(Year.MAX_YEAR, 1, 1),
//            LocalDate.date(Year.MAX_YEAR, 12, 31)
//        );
//    }
//
//    void doTest_comparisons_LocalDate(LocalDate... LocalDates) {
//        for (int i = 0; i < LocalDates.length; i++) {
//            LocalDate a = LocalDates[i];
//            for (int j = 0; j < LocalDates.length; j++) {
//                LocalDate b = LocalDates[j];
//                if (i < j) {
//                    assertTrue(a.compareTo(b) < 0, a + " <=> " + b);
//                    assertEquals(a.isBefore(b), true, a + " <=> " + b);
//                    assertEquals(a.isAfter(b), false, a + " <=> " + b);
//                    assertEquals(a.equals(b), false, a + " <=> " + b);
//                } else if (i > j) {
//                    assertTrue(a.compareTo(b) > 0, a + " <=> " + b);
//                    assertEquals(a.isBefore(b), false, a + " <=> " + b);
//                    assertEquals(a.isAfter(b), true, a + " <=> " + b);
//                    assertEquals(a.equals(b), false, a + " <=> " + b);
//                } else {
//                    assertEquals(a.compareTo(b), 0, a + " <=> " + b);
//                    assertEquals(a.isBefore(b), false, a + " <=> " + b);
//                    assertEquals(a.isAfter(b), false, a + " <=> " + b);
//                    assertEquals(a.equals(b), true, a + " <=> " + b);
//                }
//            }
//        }
//    }
//
//    @Test(expectedExceptions=NullPointerException.class)
//    public void test_compareTo_ObjectNull() {
//        TEST_2007_07_15_12_30_40_987654321.compareTo(null);
//    }
//
//    @Test(expectedExceptions=NullPointerException.class)
//    public void test_isBefore_ObjectNull() {
//        TEST_2007_07_15_12_30_40_987654321.isBefore(null);
//    }
//
//    @Test(expectedExceptions=NullPointerException.class)
//    public void test_isAfter_ObjectNull() {
//        TEST_2007_07_15_12_30_40_987654321.isAfter(null);
//    }
//
//    @Test(expectedExceptions=ClassCastException.class)
//    @SuppressWarnings("unchecked")
//    public void compareToNonLocalDate() {
//       Comparable c = TEST_2007_07_15_12_30_40_987654321;
//       c.compareTo(new Object());
//    }
//
//    //-----------------------------------------------------------------------
//    // equals()
//    //-----------------------------------------------------------------------
//    @Test(dataProvider="sampleDates")
//    public void test_equals_true(int y, int m, int d) {
//        LocalDate a = LocalDate.date(y, m, d);
//        LocalDate b = LocalDate.date(y, m, d);
//        assertEquals(a.equals(b), true);
//    }
//    @Test(dataProvider="sampleDates")
//    public void test_equals_false_year_differs(int y, int m, int d) {
//        LocalDate a = LocalDate.date(y, m, d);
//        LocalDate b = LocalDate.date(y + 1, m, d);
//        assertEquals(a.equals(b), false);
//    }
//    @Test(dataProvider="sampleDates")
//    public void test_equals_false_month_differs(int y, int m, int d) {
//        LocalDate a = LocalDate.date(y, m, d);
//        LocalDate b = LocalDate.date(y, m + 1, d);
//        assertEquals(a.equals(b), false);
//    }
//    @Test(dataProvider="sampleDates")
//    public void test_equals_false_day_differs(int y, int m, int d) {
//        LocalDate a = LocalDate.date(y, m, d);
//        LocalDate b = LocalDate.date(y, m, d + 1);
//        assertEquals(a.equals(b), false);
//    }
//
//    public void test_equals_itself_true() {
//        assertEquals(TEST_2007_07_15_12_30_40_987654321.equals(TEST_2007_07_15_12_30_40_987654321), true);
//    }
//
//    public void test_equals_string_false() {
//        assertEquals(TEST_2007_07_15_12_30_40_987654321.equals("2007-07-15"), false);
//    }
//
//    public void test_equals_null_false() {
//        assertEquals(TEST_2007_07_15_12_30_40_987654321.equals(null), false);
//    }
//
//    //-----------------------------------------------------------------------
//    // hashCode()
//    //-----------------------------------------------------------------------
//    @Test(dataProvider="sampleDates")
//    public void test_hashCode(int y, int m, int d) {
//        LocalDate a = LocalDate.date(y, m, d);
//        assertEquals(a.hashCode(), a.hashCode());
//        LocalDate b = LocalDate.date(y, m, d);
//        assertEquals(a.hashCode(), b.hashCode());
//    }
//
//    //-----------------------------------------------------------------------
//    // toString()
//    //-----------------------------------------------------------------------
//    @DataProvider(name="sampleToString")
//    Object[][] provider_sampleToString() {
//        return new Object[][] {
//            {2008, 7, 5, "2008-07-05"},
//            {2007, 12, 31, "2007-12-31"},
//            {999, 12, 31, "0999-12-31"},
//            {-1, 1, 2, "-0001-01-02"},
//        };
//    }
//
//    @Test(dataProvider="sampleToString")
//    public void test_toString(int y, int m, int d, String expected) {
//        LocalDate t = LocalDate.date(y, m, d);
//        String str = t.toString();
//        assertEquals(str, expected);
//    }
//    
//    //-----------------------------------------------------------------------
//    // matchesDate()
//    //-----------------------------------------------------------------------
//    @Test(dataProvider="sampleDates")
//    public void test_matchesDate_true(int y, int m, int d) {
//        LocalDate a = LocalDate.date(y, m, d);
//        LocalDate b = LocalDate.date(y, m, d);
//        assertEquals(a.matchesDate(b), true);
//    }
//    @Test(dataProvider="sampleDates")
//    public void test_matchesDate_false_year_differs(int y, int m, int d) {
//        LocalDate a = LocalDate.date(y, m, d);
//        LocalDate b = LocalDate.date(y + 1, m, d);
//        assertEquals(a.matchesDate(b), false);
//    }
//    @Test(dataProvider="sampleDates")
//    public void test_matchesDate_false_month_differs(int y, int m, int d) {
//        LocalDate a = LocalDate.date(y, m, d);
//        LocalDate b = LocalDate.date(y, m + 1, d);
//        assertEquals(a.matchesDate(b), false);
//    }
//    @Test(dataProvider="sampleDates")
//    public void test_matchesDate_false_day_differs(int y, int m, int d) {
//        LocalDate a = LocalDate.date(y, m, d);
//        LocalDate b = LocalDate.date(y, m, d + 1);
//        assertEquals(a.matchesDate(b), false);
//    }
//
//    public void test_matchesDate_itself_true() {
//        assertEquals(TEST_2007_07_15_12_30_40_987654321.matchesDate(TEST_2007_07_15_12_30_40_987654321), true);
//    }
//
//    @Test(expectedExceptions=NullPointerException.class)
//    public void test_matchesDate_null() {
//        TEST_2007_07_15_12_30_40_987654321.matchesDate(null);
//    }
}
