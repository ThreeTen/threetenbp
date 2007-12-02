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
package javax.time.calendar;

import static org.testng.Assert.*;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.time.calendar.field.DayOfMonth;
import javax.time.calendar.field.DayOfYear;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.Year;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test OffsetDate.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestOffsetDate {

    private static final String MIN_YEAR_STR = Integer.toString(Year.MIN_YEAR);
    private static final String MAX_YEAR_STR = Integer.toString(Year.MAX_YEAR);
    private static final ZoneOffset OFFSET_PONE = ZoneOffset.zoneOffset(1);
    private static final ZoneOffset OFFSET_PTWO = ZoneOffset.zoneOffset(2);
    private OffsetDate TEST_2007_07_15;

    @BeforeMethod
    public void setUp() {
        TEST_2007_07_15 = OffsetDate.date(2007, 7, 15, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(TEST_2007_07_15 instanceof Calendrical);
        assertTrue(TEST_2007_07_15 instanceof Serializable);
        assertTrue(TEST_2007_07_15 instanceof Comparable);
    }

    public void test_immutable() {
        Class<OffsetDate> cls = OffsetDate.class;
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            assertTrue(Modifier.isPrivate(field.getModifiers()));
            assertTrue(Modifier.isFinal(field.getModifiers()));
        }
    }

    //-----------------------------------------------------------------------
    public void factory_date_ints() {
        assertEquals(TEST_2007_07_15.getYear(), Year.isoYear(2007));
        assertEquals(TEST_2007_07_15.getMonthOfYear(), MonthOfYear.JULY);
        assertEquals(TEST_2007_07_15.getDayOfMonth(), DayOfMonth.dayOfMonth(15));
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_date_ints_dayTooLow() {
        OffsetDate.date(2007, 1, 0, OFFSET_PONE);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_date_ints_dayTooHigh() {
        OffsetDate.date(2007, 1, 32, OFFSET_PONE);
    }


    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_date_ints_monthTooLow() {
        OffsetDate.date(2007, 0, 1, OFFSET_PONE);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_date_ints_monthTooHigh() {
        OffsetDate.date(2007, 13, 1, OFFSET_PONE);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_date_ints_yearTooLow() {
        OffsetDate.date(Integer.MIN_VALUE, 1, 1, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_date_ints_nullOffset() {
        OffsetDate.date(2007, 1, 1, (ZoneOffset) null);
    }

    //-----------------------------------------------------------------------
    public void test_isSupported() {
        assertEquals(TEST_2007_07_15.isSupported(ISOChronology.INSTANCE.yearRule()), true);
        assertEquals(TEST_2007_07_15.isSupported(ISOChronology.INSTANCE.monthOfYearRule()), true);
        assertEquals(TEST_2007_07_15.isSupported(ISOChronology.INSTANCE.dayOfMonthRule()), true);
        assertEquals(TEST_2007_07_15.isSupported(ISOChronology.INSTANCE.dayOfWeekRule()), true);
        assertEquals(TEST_2007_07_15.isSupported(ISOChronology.INSTANCE.dayOfYearRule()), true);
//        assertEquals(TEST_2007_07_15.isSupported(HourOfDay.RULE), false);
//        assertEquals(TEST_2007_07_15.isSupported(MinuteOfHour.RULE), false);
//        assertEquals(TEST_2007_07_15.isSupported(SecondOfMinute.RULE), false);
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

    //-----------------------------------------------------------------------
    // get*()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_get(int y, int m, int d) {
        OffsetDate a = OffsetDate.date(y, m, d, OFFSET_PONE);
        assertEquals(a.getYear(), Year.isoYear(y));
        assertEquals(a.getMonthOfYear(), MonthOfYear.monthOfYear(m));
        assertEquals(a.getDayOfMonth(), DayOfMonth.dayOfMonth(d));
    }

    @Test(dataProvider="sampleDates")
    public void test_getDOY(int y, int m, int d) {
        Year year = Year.isoYear(y);
        OffsetDate a = OffsetDate.date(y, m, d, OFFSET_PONE);
        int total = 0;
        for (int i = 1; i < m; i++) {
            total += MonthOfYear.monthOfYear(i).lengthInDays(year);
        }
        int doy = total + d;
        assertEquals(a.getDayOfYear(), DayOfYear.dayOfYear(doy));
    }

    //-----------------------------------------------------------------------
    // withYear()
    //-----------------------------------------------------------------------
    public void test_withYear_normal() {
        OffsetDate t = TEST_2007_07_15.withYear(2008);
        assertEquals(t, OffsetDate.date(2008, 7, 15, OFFSET_PONE));
    }

    public void test_withYear_noChange() {
        OffsetDate t = TEST_2007_07_15.withYear(2007);
        assertEquals(t, OffsetDate.date(2007, 7, 15, OFFSET_PONE));
    }

    public void test_withYear_adjustDay() {
        OffsetDate t = OffsetDate.date(2008, 2, 29, OFFSET_PONE).withYear(2007);
        OffsetDate expected = OffsetDate.date(2007, 2, 28, OFFSET_PONE);
        assertEquals(t, expected);
    }

    //-----------------------------------------------------------------------
    // withMonthOfYear()
    //-----------------------------------------------------------------------
    public void test_withMonthOfYear_normal() {
        OffsetDate t = TEST_2007_07_15.withMonthOfYear(1);
        assertEquals(t, OffsetDate.date(2007, 1, 15, OFFSET_PONE));
    }

    public void test_withMonthOfYear_noChange() {
        OffsetDate t = TEST_2007_07_15.withMonthOfYear(7);
        assertEquals(t, OffsetDate.date(2007, 7, 15, OFFSET_PONE));
    }

    public void test_withMonthOfYear_adjustDay() {
        OffsetDate t = OffsetDate.date(2007, 12, 31, OFFSET_PONE).withMonthOfYear(11);
        OffsetDate expected = OffsetDate.date(2007, 11, 30, OFFSET_PONE);
        assertEquals(t, expected);
    }

    //-----------------------------------------------------------------------
    // withDayOfMonth()
    //-----------------------------------------------------------------------
    public void test_withDayOfMonth_normal() {
        OffsetDate t = TEST_2007_07_15.withDayOfMonth(1);
        assertEquals(t, OffsetDate.date(2007, 7, 1, OFFSET_PONE));
    }

    public void test_withDayOfMonth_noChange() {
        OffsetDate t = TEST_2007_07_15.withDayOfMonth(15);
        assertEquals(t, OffsetDate.date(2007, 7, 15, OFFSET_PONE));
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withDayOfMonth_invalid() {
        OffsetDate.date(2007, 11, 30, OFFSET_PONE).withDayOfMonth(31);
    }

    //-----------------------------------------------------------------------
    // withLastDayOfMonth()
    //-----------------------------------------------------------------------
    public void test_withLastDayOfMonth_leap() {
        Year year = Year.isoYear(2008);
        for (MonthOfYear month : MonthOfYear.values()) {
            OffsetDate t = OffsetDate.date(2008, month, 1, OFFSET_PONE).withLastDayOfMonth();
            assertEquals(t.getDayOfMonth(), month.getLastDayOfMonth(year));
        }
    }

    public void test_withLastDayOfMonth_standard() {
        Year year = Year.isoYear(2007);
        for (MonthOfYear month : MonthOfYear.values()) {
            OffsetDate t = OffsetDate.date(2007, month, 1, OFFSET_PONE).withLastDayOfMonth();
            assertEquals(t.getDayOfMonth(), month.getLastDayOfMonth(year));
        }
    }

    public void test_withLastDayOfMonth_noChange() {
        OffsetDate t = OffsetDate.date(2008, 10, 31, OFFSET_PONE).withLastDayOfMonth();
        assertEquals(t, OffsetDate.date(2008, 10, 31, OFFSET_PONE));
    }

    //-----------------------------------------------------------------------
    // plusYears()
    //-----------------------------------------------------------------------
    public void test_plusYears_normal() {
        OffsetDate t = TEST_2007_07_15.plusYears(1);
        assertEquals(t, OffsetDate.date(2008, 7, 15, OFFSET_PONE));
    }

    public void test_plusYears_noChange() {
        OffsetDate t = TEST_2007_07_15.plusYears(0);
        assertEquals(t, OffsetDate.date(2007, 7, 15, OFFSET_PONE));
    }

    public void test_plusYears_negative() {
        OffsetDate t = TEST_2007_07_15.plusYears(-1);
        assertEquals(t, OffsetDate.date(2006, 7, 15, OFFSET_PONE));
    }

    public void test_plusYears_adjustDay() {
        OffsetDate t = OffsetDate.date(2008, 2, 29, OFFSET_PONE).plusYears(1);
        OffsetDate expected = OffsetDate.date(2009, 2, 28, OFFSET_PONE);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_plusYears_invalidTooLarge() {
        try {
            OffsetDate.date(Year.MAX_YEAR, 1, 1, OFFSET_PONE).plusYears(1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            String actual = Long.toString(((long) Year.MAX_YEAR) + 1);
            assertEquals(ex.getMessage(), "Illegal value for Year field, value " + actual +
                " is not in the range " + MIN_YEAR_STR + " to " + MAX_YEAR_STR);
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_plusYears_invalidTooSmall() {
        try {
            OffsetDate.date(Year.MIN_YEAR, 1, 1, OFFSET_PONE).plusYears(-1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            String actual = Long.toString(((long) Year.MIN_YEAR) - 1);
            assertEquals(ex.getMessage(), "Illegal value for Year field, value " + actual +
                " is not in the range " + MIN_YEAR_STR + " to " + MAX_YEAR_STR);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // plusMonths()
    //-----------------------------------------------------------------------
    public void test_plusMonths_normal() {
        OffsetDate t = TEST_2007_07_15.plusMonths(1);
        assertEquals(t, OffsetDate.date(2007, 8, 15, OFFSET_PONE));
    }

    public void test_plusMonths_noChange() {
        OffsetDate t = TEST_2007_07_15.plusMonths(0);
        assertEquals(t, OffsetDate.date(2007, 7, 15, OFFSET_PONE));
    }

    public void test_plusMonths_negative() {
        OffsetDate t = TEST_2007_07_15.plusMonths(-1);
        assertEquals(t, OffsetDate.date(2007, 6, 15, OFFSET_PONE));
    }

    public void test_plusMonths_negativeAcrossYear() {
        OffsetDate t = TEST_2007_07_15.plusMonths(-7);
        assertEquals(t, OffsetDate.date(2006, 12, 15, OFFSET_PONE));
    }

    public void test_plusMonths_adjustDayFromLeapYear() {
        OffsetDate t = OffsetDate.date(2008, 2, 29, OFFSET_PONE).plusMonths(12);
        OffsetDate expected = OffsetDate.date(2009, 2, 28, OFFSET_PONE);
        assertEquals(t, expected);
    }

    public void test_plusMonths_adjustDayFromMonthLength() {
        OffsetDate t = OffsetDate.date(2007, 3, 31, OFFSET_PONE).plusMonths(1);
        OffsetDate expected = OffsetDate.date(2007, 4, 30, OFFSET_PONE);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_plusMonths_invalidTooLarge() {
        try {
            OffsetDate.date(Year.MAX_YEAR, 12, 1, OFFSET_PONE).plusMonths(1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            String actual = Long.toString(((long) Year.MAX_YEAR) + 1);
            assertEquals(ex.getMessage(), "Illegal value for Year field, value " + actual +
                " is not in the range " + MIN_YEAR_STR + " to " + MAX_YEAR_STR);
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_plusMonths_invalidTooSmall() {
        try {
            OffsetDate.date(Year.MIN_YEAR, 1, 1, OFFSET_PONE).plusMonths(-1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            String actual = Long.toString(((long) Year.MIN_YEAR) - 1);
            assertEquals(ex.getMessage(), "Illegal value for Year field, value " + actual +
                " is not in the range " + MIN_YEAR_STR + " to " + MAX_YEAR_STR);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_equals_true(int y, int m, int d) {
        OffsetDate a = OffsetDate.date(y, m, d, OFFSET_PONE);
        OffsetDate b = OffsetDate.date(y, m, d, OFFSET_PONE);
        assertEquals(a.equals(b), true);
    }
    @Test(dataProvider="sampleDates")
    public void test_equals_false_year_differs(int y, int m, int d) {
        OffsetDate a = OffsetDate.date(y, m, d, OFFSET_PONE);
        OffsetDate b = OffsetDate.date(y + 1, m, d, OFFSET_PONE);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleDates")
    public void test_equals_false_month_differs(int y, int m, int d) {
        OffsetDate a = OffsetDate.date(y, m, d, OFFSET_PONE);
        OffsetDate b = OffsetDate.date(y, m + 1, d, OFFSET_PONE);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleDates")
    public void test_equals_false_day_differs(int y, int m, int d) {
        OffsetDate a = OffsetDate.date(y, m, d, OFFSET_PONE);
        OffsetDate b = OffsetDate.date(y, m, d + 1, OFFSET_PONE);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleDates")
    public void test_equals_false_offset_differs(int y, int m, int d) {
        OffsetDate a = OffsetDate.date(y, m, d, OFFSET_PONE);
        OffsetDate b = OffsetDate.date(y, m, d, OFFSET_PTWO);
        assertEquals(a.equals(b), false);
    }

    public void test_equals_itself_true() {
        assertEquals(TEST_2007_07_15.equals(TEST_2007_07_15), true);
    }

    public void test_equals_string_false() {
        assertEquals(TEST_2007_07_15.equals("2007-07-15"), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleToString")
    Object[][] provider_sampleToString() {
        return new Object[][] {
            {2008, 7, 5, "Z", "2008-07-05Z"},
            {2008, 7, 5, "+00", "2008-07-05Z"},
            {2008, 7, 5, "+0000", "2008-07-05Z"},
            {2008, 7, 5, "+00:00", "2008-07-05Z"},
            {2008, 7, 5, "+000000", "2008-07-05Z"},
            {2008, 7, 5, "+00:00:00", "2008-07-05Z"},
            {2008, 7, 5, "-00", "2008-07-05Z"},
            {2008, 7, 5, "-0000", "2008-07-05Z"},
            {2008, 7, 5, "-00:00", "2008-07-05Z"},
            {2008, 7, 5, "-000000", "2008-07-05Z"},
            {2008, 7, 5, "-00:00:00", "2008-07-05Z"},
            {2008, 7, 5, "+01", "2008-07-05+01:00"},
            {2008, 7, 5, "+0100", "2008-07-05+01:00"},
            {2008, 7, 5, "+01:00", "2008-07-05+01:00"},
            {2008, 7, 5, "+010000", "2008-07-05+01:00"},
            {2008, 7, 5, "+01:00:00", "2008-07-05+01:00"},
            {2008, 7, 5, "+0130", "2008-07-05+01:30"},
            {2008, 7, 5, "+01:30", "2008-07-05+01:30"},
            {2008, 7, 5, "+013000", "2008-07-05+01:30"},
            {2008, 7, 5, "+01:30:00", "2008-07-05+01:30"},
            {2008, 7, 5, "+013040", "2008-07-05+01:30:40"},
            {2008, 7, 5, "+01:30:40", "2008-07-05+01:30:40"},
        };
    }

    @Test(dataProvider="sampleToString")
    public void test_toString(int y, int m, int d, String offsetId, String expected) {
        OffsetDate t = OffsetDate.date(y, m, d, ZoneOffset.zoneOffset(offsetId));
        String str = t.toString();
        assertEquals(str, expected);
    }

}
