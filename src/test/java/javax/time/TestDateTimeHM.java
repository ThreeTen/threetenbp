/*
 * Copyright (c) 2007, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time;

import static org.testng.Assert.*;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.time.calendar.Calendrical;
import javax.time.calendar.DateTimeHM;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.field.MonthOfYear;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test DateTimeHM.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestDateTimeHM {

    private DateTimeHM TEST_2007_07_15;

    @BeforeMethod
    public void setUp() {
        TEST_2007_07_15 = DateTimeHM.dateMidnight(2007, 7, 15);
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(TEST_2007_07_15 instanceof Calendrical);
        assertTrue(TEST_2007_07_15 instanceof Serializable);
        assertTrue(TEST_2007_07_15 instanceof Comparable);
    }

    public void test_immutable() {
        Class<DateTimeHM> cls = DateTimeHM.class;
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            assertTrue(Modifier.isPrivate(field.getModifiers()));
            assertTrue(Modifier.isFinal(field.getModifiers()));
        }
    }

    //-----------------------------------------------------------------------
    public void factory_dateMidnight_ints() {
        assertEquals(TEST_2007_07_15.getYear(), 2007);
        assertEquals(TEST_2007_07_15.getMonthOfYear(), MonthOfYear.JULY);
        assertEquals(TEST_2007_07_15.getDayOfMonth(), 15);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateMidnight_ints_dayTooLow() {
        DateTimeHM.dateMidnight(2007, 1, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateMidnight_ints_dayTooHigh() {
        DateTimeHM.dateMidnight(2007, 1, 32);
    }


    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateMidnight_ints_monthTooLow() {
        DateTimeHM.dateMidnight(2007, 0, 1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateMidnight_ints_monthTooHigh() {
        DateTimeHM.dateMidnight(2007, 13, 1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateMidnight_ints_yearTooLow() {
        DateTimeHM.dateMidnight(Integer.MIN_VALUE, 1, 1);
    }

//    //-----------------------------------------------------------------------
//    public void test_isSupported() {
//        assertEquals(TEST_2007_07_15.isSupported(ISOChronology.INSTANCE.yearRule()), true);
//        assertEquals(TEST_2007_07_15.isSupported(ISOChronology.INSTANCE.monthOfYearRule()), true);
//        assertEquals(TEST_2007_07_15.isSupported(ISOChronology.INSTANCE.dayOfMonthRule()), true);
//        assertEquals(TEST_2007_07_15.isSupported(ISOChronology.INSTANCE.dayOfWeekRule()), true);
//        assertEquals(TEST_2007_07_15.isSupported(ISOChronology.INSTANCE.dayOfYearRule()), true);
//        assertEquals(TEST_2007_07_15.isSupported(HourOfDay.RULE), true);
//        assertEquals(TEST_2007_07_15.isSupported(MinuteOfHour.RULE), true);
//        assertEquals(TEST_2007_07_15.isSupported(SecondOfMinute.RULE), false);
//    }

    //-----------------------------------------------------------------------
    @DataProvider(name="sampleDates")
    Object[][] provider_sampleDates() {
        return new Object[][] {
            {2008, 7, 5, 4, 3},
            {2007, 7, 5, 4, 3},
            {2006, 7, 5, 4, 3},
            {2005, 7, 5, 4, 3},
            {2004, 1, 1, 0, 0},
            {-1, 1, 2, 3, 4},
        };
    }

    //-----------------------------------------------------------------------
    // get*()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_get(int y, int m, int d, int h, int min) {
        DateTimeHM a = DateTimeHM.dateTime(y, m, d, h, min);
        assertEquals(a.getYear(), y);
        assertEquals(a.getMonthOfYear(), MonthOfYear.monthOfYear(m));
        assertEquals(a.getDayOfMonth(), d);
        assertEquals(a.getHourOfDay(), h);
        assertEquals(a.getMinuteOfHour(), min);
    }

    @Test(dataProvider="sampleDates")
    public void test_getDOY(int y, int m, int d, int h, int min) {
        DateTimeHM a = DateTimeHM.dateTime(y, m, d, h, min);
        int total = 0;
        for (int i = 1; i < m; i++) {
            total += MonthOfYear.monthOfYear(i).lengthInDays(y);
        }
        int doy = total + d;
        assertEquals(a.getDayOfYear(), doy);
    }

    //-----------------------------------------------------------------------
    // withYear()
    //-----------------------------------------------------------------------
    public void test_withYear_normal() {
        DateTimeHM t = TEST_2007_07_15.withYear(2008);
        assertEquals(t, DateTimeHM.dateMidnight(2008, 7, 15));
    }

    public void test_withYear_noChange() {
        DateTimeHM t = TEST_2007_07_15.withYear(2007);
        assertEquals(t, DateTimeHM.dateMidnight(2007, 7, 15));
    }

    public void test_withYear_adjustDay() {
        DateTimeHM t = DateTimeHM.dateMidnight(2008, 2, 29).withYear(2007);
        DateTimeHM expected = DateTimeHM.dateMidnight(2007, 2, 28);
        assertEquals(t, expected);
    }

    //-----------------------------------------------------------------------
    // withMonthOfYear()
    //-----------------------------------------------------------------------
    public void test_withMonthOfYear_normal() {
        DateTimeHM t = TEST_2007_07_15.withMonthOfYear(1);
        assertEquals(t, DateTimeHM.dateMidnight(2007, 1, 15));
    }

    public void test_withMonthOfYear_noChange() {
        DateTimeHM t = TEST_2007_07_15.withMonthOfYear(7);
        assertEquals(t, DateTimeHM.dateMidnight(2007, 7, 15));
    }

    public void test_withMonthOfYear_adjustDay() {
        DateTimeHM t = DateTimeHM.dateMidnight(2007, 12, 31).withMonthOfYear(11);
        DateTimeHM expected = DateTimeHM.dateMidnight(2007, 11, 30);
        assertEquals(t, expected);
    }

    //-----------------------------------------------------------------------
    // withDayOfMonth()
    //-----------------------------------------------------------------------
    public void test_withDayOfMonth_normal() {
        DateTimeHM t = TEST_2007_07_15.withDayOfMonth(1);
        assertEquals(t, DateTimeHM.dateMidnight(2007, 7, 1));
    }

    public void test_withDayOfMonth_noChange() {
        DateTimeHM t = TEST_2007_07_15.withDayOfMonth(15);
        assertEquals(t, DateTimeHM.dateMidnight(2007, 7, 15));
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withDayOfMonth_invalid() {
        DateTimeHM.dateMidnight(2007, 11, 30).withDayOfMonth(31);
    }

    //-----------------------------------------------------------------------
    // withLastDayOfMonth()
    //-----------------------------------------------------------------------
    public void test_withLastDayOfMonth_leap() {
        for (MonthOfYear month : MonthOfYear.values()) {
            DateTimeHM t = DateTimeHM.dateMidnight(2008, month.getMonthOfYear(), 1).withLastDayOfMonth();
            assertEquals(t.getDayOfMonth(), month.lengthInDays(2008));
        }
    }

    public void test_withLastDayOfMonth_standard() {
        for (MonthOfYear month : MonthOfYear.values()) {
            DateTimeHM t = DateTimeHM.dateMidnight(2007, month.getMonthOfYear(), 1).withLastDayOfMonth();
            assertEquals(t.getDayOfMonth(), month.lengthInDays(2007));
        }
    }

    public void test_withLastDayOfMonth_noChange() {
        DateTimeHM t = DateTimeHM.dateMidnight(2008, 10, 31).withLastDayOfMonth();
        assertEquals(t, DateTimeHM.dateMidnight(2008, 10, 31));
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_equals_true(int y, int m, int d, int h, int min) {
        DateTimeHM a = DateTimeHM.dateTime(y, m, d, h, min);
        DateTimeHM b = DateTimeHM.dateTime(y, m, d, h, min);
        assertEquals(a.equals(b), true);
    }
    @Test(dataProvider="sampleDates")
    public void test_equals_false_month_differs(int y, int m, int d, int h, int min) {
        DateTimeHM a = DateTimeHM.dateTime(y, m, d, h, min);
        DateTimeHM b = DateTimeHM.dateTime(y, m + 1, d, h, min);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleDates")
    public void test_equals_false_day_differs(int y, int m, int d, int h, int min) {
        DateTimeHM a = DateTimeHM.dateTime(y, m, d, h, min);
        DateTimeHM b = DateTimeHM.dateTime(y, m, d + 1, h, min);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleDates")
    public void test_equals_false_hour_differs(int y, int m, int d, int h, int min) {
        DateTimeHM a = DateTimeHM.dateTime(y, m, d, h, min);
        DateTimeHM b = DateTimeHM.dateTime(y, m, d, h + 1, min);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleDates")
    public void test_equals_false_minute_differs(int y, int m, int d, int h, int min) {
        DateTimeHM a = DateTimeHM.dateTime(y, m, d, h, min);
        DateTimeHM b = DateTimeHM.dateTime(y, m, d, h, min + 1);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleDates")
    public void test_equals_false_year_differs(int y, int m, int d, int h, int min) {
        DateTimeHM a = DateTimeHM.dateTime(y, m, d, h, min);
        DateTimeHM b = DateTimeHM.dateTime(y + 1, m, d, h, min);
        assertEquals(a.equals(b), false);
    }

    public void test_equals_itself_true() {
        assertEquals(TEST_2007_07_15.equals(TEST_2007_07_15), true);
    }

    public void test_equals_string_false() {
        assertEquals(TEST_2007_07_15.equals("2007-07-15"), false);
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleToString")
    Object[][] provider_sampleToString() {
        return new Object[][] {
            {2008, 7, 5, 4, 3, "2008-07-05T04:03"},
            {2007, 12, 31, 23, 59, "2007-12-31T23:59"},
//            {-1, 1, 2, 3, 0, "-0001-01-02T03:00"},
        };
    }

    @Test(dataProvider="sampleToString")
    public void test_toString(int y, int m, int d, int h, int min, String expected) {
        DateTimeHM t = DateTimeHM.dateTime(y, m, d, h, min);
        String str = t.toString();
        assertEquals(str, expected);
    }

}
