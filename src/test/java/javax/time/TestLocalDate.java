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
import javax.time.calendar.ISOChronology;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.LocalDate;
import javax.time.calendar.field.MonthOfYear;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test LocalDate.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestLocalDate {

    private LocalDate TEST_2007_07_15;

    @BeforeMethod
    public void setUp() {
        TEST_2007_07_15 = LocalDate.date(2007, 7, 15);
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(TEST_2007_07_15 instanceof Calendrical);
        assertTrue(TEST_2007_07_15 instanceof Serializable);
        assertTrue(TEST_2007_07_15 instanceof Comparable);
    }

    public void test_immutable() {
        Class<LocalDate> cls = LocalDate.class;
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
        assertEquals(TEST_2007_07_15.getYear(), 2007);
        assertEquals(TEST_2007_07_15.getMonthOfYear(), MonthOfYear.JULY);
        assertEquals(TEST_2007_07_15.getDayOfMonth(), 15);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_date_ints_dayTooLow() {
        LocalDate.date(2007, 1, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_date_ints_dayTooHigh() {
        LocalDate.date(2007, 1, 32);
    }


    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_date_ints_monthTooLow() {
        LocalDate.date(2007, 0, 1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_date_ints_monthTooHigh() {
        LocalDate.date(2007, 13, 1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_date_ints_yearTooLow() {
        LocalDate.date(Integer.MIN_VALUE, 1, 1);
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
        LocalDate a = LocalDate.date(y, m, d);
        assertEquals(a.getYear(), y);
        assertEquals(a.getMonthOfYear(), MonthOfYear.monthOfYear(m));
        assertEquals(a.getDayOfMonth(), d);
    }

    @Test(dataProvider="sampleDates")
    public void test_getDOY(int y, int m, int d) {
        LocalDate a = LocalDate.date(y, m, d);
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
        LocalDate t = TEST_2007_07_15.withYear(2008);
        assertEquals(t, LocalDate.date(2008, 7, 15));
    }

    public void test_withYear_noChange() {
        LocalDate t = TEST_2007_07_15.withYear(2007);
        assertEquals(t, LocalDate.date(2007, 7, 15));
    }

    public void test_withYear_adjustDay() {
        LocalDate t = LocalDate.date(2008, 2, 29).withYear(2007);
        LocalDate expected = LocalDate.date(2007, 2, 28);
        assertEquals(t, expected);
    }

    //-----------------------------------------------------------------------
    // withMonthOfYear()
    //-----------------------------------------------------------------------
    public void test_withMonthOfYear_normal() {
        LocalDate t = TEST_2007_07_15.withMonthOfYear(1);
        assertEquals(t, LocalDate.date(2007, 1, 15));
    }

    public void test_withMonthOfYear_noChange() {
        LocalDate t = TEST_2007_07_15.withMonthOfYear(7);
        assertEquals(t, LocalDate.date(2007, 7, 15));
    }

    public void test_withMonthOfYear_adjustDay() {
        LocalDate t = LocalDate.date(2007, 12, 31).withMonthOfYear(11);
        LocalDate expected = LocalDate.date(2007, 11, 30);
        assertEquals(t, expected);
    }

    //-----------------------------------------------------------------------
    // withDayOfMonth()
    //-----------------------------------------------------------------------
    public void test_withDayOfMonth_normal() {
        LocalDate t = TEST_2007_07_15.withDayOfMonth(1);
        assertEquals(t, LocalDate.date(2007, 7, 1));
    }

    public void test_withDayOfMonth_noChange() {
        LocalDate t = TEST_2007_07_15.withDayOfMonth(15);
        assertEquals(t, LocalDate.date(2007, 7, 15));
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withDayOfMonth_invalid() {
        LocalDate.date(2007, 11, 30).withDayOfMonth(31);
    }

    //-----------------------------------------------------------------------
    // withLastDayOfMonth()
    //-----------------------------------------------------------------------
    public void test_withLastDayOfMonth_leap() {
        for (MonthOfYear month : MonthOfYear.values()) {
            LocalDate t = LocalDate.date(2008, month.getMonthOfYear(), 1).withLastDayOfMonth();
            assertEquals(t.getDayOfMonth(), month.lengthInDays(2008));
        }
    }

    public void test_withLastDayOfMonth_standard() {
        for (MonthOfYear month : MonthOfYear.values()) {
            LocalDate t = LocalDate.date(2007, month.getMonthOfYear(), 1).withLastDayOfMonth();
            assertEquals(t.getDayOfMonth(), month.lengthInDays(2007));
        }
    }

    public void test_withLastDayOfMonth_noChange() {
        LocalDate t = LocalDate.date(2008, 10, 31).withLastDayOfMonth();
        assertEquals(t, LocalDate.date(2008, 10, 31));
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_equals_true(int y, int m, int d) {
        LocalDate a = LocalDate.date(y, m, d);
        LocalDate b = LocalDate.date(y, m, d);
        assertEquals(a.equals(b), true);
    }
    @Test(dataProvider="sampleDates")
    public void test_equals_false_year_differs(int y, int m, int d) {
        LocalDate a = LocalDate.date(y, m, d);
        LocalDate b = LocalDate.date(y + 1, m, d);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleDates")
    public void test_equals_false_month_differs(int y, int m, int d) {
        LocalDate a = LocalDate.date(y, m, d);
        LocalDate b = LocalDate.date(y, m + 1, d);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleDates")
    public void test_equals_false_day_differs(int y, int m, int d) {
        LocalDate a = LocalDate.date(y, m, d);
        LocalDate b = LocalDate.date(y, m, d + 1);
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
            {2008, 7, 5, "2008-07-05"},
            {2007, 12, 31, "2007-12-31"},
//            {-1, 1, 2, "-0001-01-02"},
        };
    }

    @Test(dataProvider="sampleToString")
    public void test_toString(int y, int m, int d, String expected) {
        LocalDate t = LocalDate.date(y, m, d);
        String str = t.toString();
        assertEquals(str, expected);
    }

}
