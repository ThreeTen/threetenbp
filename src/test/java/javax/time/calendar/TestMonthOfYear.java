/*
 * Copyright (c) 2008-2011, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.calendar.ISODateTimeRule.DAY_OF_WEEK;
import static javax.time.calendar.ISODateTimeRule.MONTH_OF_YEAR;
import static javax.time.calendar.ISODateTimeRule.QUARTER_OF_YEAR;
import static javax.time.calendar.MonthOfYear.FEBRUARY;
import static javax.time.calendar.MonthOfYear.JANUARY;
import static javax.time.calendar.MonthOfYear.JUNE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.Serializable;
import java.util.Locale;

import javax.time.CalendricalException;
import javax.time.calendar.format.TextStyle;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test MonthOfYear.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestMonthOfYear {

    private static final int MAX_LENGTH = 12;

    @BeforeMethod
    public void setUp() {
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Enum.class.isAssignableFrom(MonthOfYear.class));
        assertTrue(Serializable.class.isAssignableFrom(MonthOfYear.class));
        assertTrue(Comparable.class.isAssignableFrom(MonthOfYear.class));
        assertTrue(Calendrical.class.isAssignableFrom(MonthOfYear.class));
        assertTrue(CalendricalMatcher.class.isAssignableFrom(MonthOfYear.class));
    }

    //-----------------------------------------------------------------------
    public void test_rule() {
        assertEquals(MonthOfYear.rule().getName(), "MonthOfYear");
        assertEquals(MonthOfYear.rule().getType(), MonthOfYear.class);
    }

    //-----------------------------------------------------------------------
    public void test_factory_int_singleton() {
        for (int i = 1; i <= MAX_LENGTH; i++) {
            MonthOfYear test = MonthOfYear.of(i);
            assertEquals(test.getValue(), i);
            assertSame(MonthOfYear.of(i), test);
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_tooLow() {
        MonthOfYear.of(0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_tooHigh() {
        MonthOfYear.of(13);
    }

    //-----------------------------------------------------------------------
    public void test_factory_Calendricals() {
        assertEquals(MonthOfYear.from(LocalDate.of(2011, 6, 6)), JUNE);
        assertEquals(MonthOfYear.from(MONTH_OF_YEAR.field(1)), JANUARY);
        assertEquals(MonthOfYear.from(LocalDate.of(2011, 6, 6), JUNE.toField()), JUNE);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_factory_Calendricals_invalid_clash() {
        MonthOfYear.from(JANUARY, FEBRUARY.toField());
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_factory_Calendricals_invalid_noDerive() {
        MonthOfYear.from(LocalTime.of(12, 30));
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_factory_Calendricals_invalid_empty() {
        MonthOfYear.from();
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_Calendricals_nullArray() {
        MonthOfYear.from((Calendrical[]) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_Calendricals_null() {
        MonthOfYear.from((Calendrical) null);
    }

    //-----------------------------------------------------------------------
    // get()
    //-----------------------------------------------------------------------
    public void test_get() {
        assertEquals(MonthOfYear.JANUARY.get(MonthOfYear.rule()), MonthOfYear.JANUARY);
        assertEquals(MonthOfYear.OCTOBER.get(MonthOfYear.rule()), MonthOfYear.OCTOBER);
        
        assertEquals(MonthOfYear.JANUARY.get(MONTH_OF_YEAR), MONTH_OF_YEAR.field(1));
        assertEquals(MonthOfYear.APRIL.get(MONTH_OF_YEAR), MONTH_OF_YEAR.field(4));
        
        assertEquals(MonthOfYear.JULY.get(QuarterOfYear.rule()), QuarterOfYear.Q3);
        assertEquals(MonthOfYear.MAY.get(QUARTER_OF_YEAR), QUARTER_OF_YEAR.field(2));
        assertEquals(MonthOfYear.FEBRUARY.get(DAY_OF_WEEK), null);
    }

    //-----------------------------------------------------------------------
    // getText()
    //-----------------------------------------------------------------------
    public void test_getText() {
        assertEquals(MonthOfYear.JANUARY.getText(TextStyle.SHORT, Locale.US), "Jan");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_getText_nullStyle() {
        MonthOfYear.JANUARY.getText(null, Locale.US);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_getText_nullLocale() {
        MonthOfYear.JANUARY.getText(TextStyle.FULL, null);
    }

    //-----------------------------------------------------------------------
    // next()
    //-----------------------------------------------------------------------
    public void test_next() {
        assertEquals(MonthOfYear.JANUARY.next(), MonthOfYear.FEBRUARY);
        assertEquals(MonthOfYear.FEBRUARY.next(), MonthOfYear.MARCH);
        assertEquals(MonthOfYear.MARCH.next(), MonthOfYear.APRIL);
        assertEquals(MonthOfYear.APRIL.next(), MonthOfYear.MAY);
        assertEquals(MonthOfYear.MAY.next(), MonthOfYear.JUNE);
        assertEquals(MonthOfYear.JUNE.next(), MonthOfYear.JULY);
        assertEquals(MonthOfYear.JULY.next(), MonthOfYear.AUGUST);
        assertEquals(MonthOfYear.AUGUST.next(), MonthOfYear.SEPTEMBER);
        assertEquals(MonthOfYear.SEPTEMBER.next(), MonthOfYear.OCTOBER);
        assertEquals(MonthOfYear.OCTOBER.next(), MonthOfYear.NOVEMBER);
        assertEquals(MonthOfYear.NOVEMBER.next(), MonthOfYear.DECEMBER);
        assertEquals(MonthOfYear.DECEMBER.next(), MonthOfYear.JANUARY);
    }

    //-----------------------------------------------------------------------
    // previous()
    //-----------------------------------------------------------------------
    public void test_previous() {
        assertEquals(MonthOfYear.JANUARY.previous(), MonthOfYear.DECEMBER);
        assertEquals(MonthOfYear.FEBRUARY.previous(), MonthOfYear.JANUARY);
        assertEquals(MonthOfYear.MARCH.previous(), MonthOfYear.FEBRUARY);
        assertEquals(MonthOfYear.APRIL.previous(), MonthOfYear.MARCH);
        assertEquals(MonthOfYear.MAY.previous(), MonthOfYear.APRIL);
        assertEquals(MonthOfYear.JUNE.previous(), MonthOfYear.MAY);
        assertEquals(MonthOfYear.JULY.previous(), MonthOfYear.JUNE);
        assertEquals(MonthOfYear.AUGUST.previous(), MonthOfYear.JULY);
        assertEquals(MonthOfYear.SEPTEMBER.previous(), MonthOfYear.AUGUST);
        assertEquals(MonthOfYear.OCTOBER.previous(), MonthOfYear.SEPTEMBER);
        assertEquals(MonthOfYear.NOVEMBER.previous(), MonthOfYear.OCTOBER);
        assertEquals(MonthOfYear.DECEMBER.previous(), MonthOfYear.NOVEMBER);
    }

    //-----------------------------------------------------------------------
    // roll(int)
    //-----------------------------------------------------------------------
    public void test_roll_january() {
        assertEquals(MonthOfYear.JANUARY.roll(-12), MonthOfYear.JANUARY);
        assertEquals(MonthOfYear.JANUARY.roll(-11), MonthOfYear.FEBRUARY);
        assertEquals(MonthOfYear.JANUARY.roll(-10), MonthOfYear.MARCH);
        assertEquals(MonthOfYear.JANUARY.roll(-9), MonthOfYear.APRIL);
        assertEquals(MonthOfYear.JANUARY.roll(-8), MonthOfYear.MAY);
        assertEquals(MonthOfYear.JANUARY.roll(-7), MonthOfYear.JUNE);
        assertEquals(MonthOfYear.JANUARY.roll(-6), MonthOfYear.JULY);
        assertEquals(MonthOfYear.JANUARY.roll(-5), MonthOfYear.AUGUST);
        assertEquals(MonthOfYear.JANUARY.roll(-4), MonthOfYear.SEPTEMBER);
        assertEquals(MonthOfYear.JANUARY.roll(-3), MonthOfYear.OCTOBER);
        assertEquals(MonthOfYear.JANUARY.roll(-2), MonthOfYear.NOVEMBER);
        assertEquals(MonthOfYear.JANUARY.roll(-1), MonthOfYear.DECEMBER);
        assertEquals(MonthOfYear.JANUARY.roll(0), MonthOfYear.JANUARY);
        assertEquals(MonthOfYear.JANUARY.roll(1), MonthOfYear.FEBRUARY);
        assertEquals(MonthOfYear.JANUARY.roll(2), MonthOfYear.MARCH);
        assertEquals(MonthOfYear.JANUARY.roll(3), MonthOfYear.APRIL);
        assertEquals(MonthOfYear.JANUARY.roll(4), MonthOfYear.MAY);
        assertEquals(MonthOfYear.JANUARY.roll(5), MonthOfYear.JUNE);
        assertEquals(MonthOfYear.JANUARY.roll(6), MonthOfYear.JULY);
        assertEquals(MonthOfYear.JANUARY.roll(7), MonthOfYear.AUGUST);
        assertEquals(MonthOfYear.JANUARY.roll(8), MonthOfYear.SEPTEMBER);
        assertEquals(MonthOfYear.JANUARY.roll(9), MonthOfYear.OCTOBER);
        assertEquals(MonthOfYear.JANUARY.roll(10), MonthOfYear.NOVEMBER);
        assertEquals(MonthOfYear.JANUARY.roll(11), MonthOfYear.DECEMBER);
        assertEquals(MonthOfYear.JANUARY.roll(12), MonthOfYear.JANUARY);
    }

    public void test_roll_july() {
        assertEquals(MonthOfYear.JULY.roll(-12), MonthOfYear.JULY);
        assertEquals(MonthOfYear.JULY.roll(-11), MonthOfYear.AUGUST);
        assertEquals(MonthOfYear.JULY.roll(-10), MonthOfYear.SEPTEMBER);
        assertEquals(MonthOfYear.JULY.roll(-9), MonthOfYear.OCTOBER);
        assertEquals(MonthOfYear.JULY.roll(-8), MonthOfYear.NOVEMBER);
        assertEquals(MonthOfYear.JULY.roll(-7), MonthOfYear.DECEMBER);
        assertEquals(MonthOfYear.JULY.roll(-6), MonthOfYear.JANUARY);
        assertEquals(MonthOfYear.JULY.roll(-5), MonthOfYear.FEBRUARY);
        assertEquals(MonthOfYear.JULY.roll(-4), MonthOfYear.MARCH);
        assertEquals(MonthOfYear.JULY.roll(-3), MonthOfYear.APRIL);
        assertEquals(MonthOfYear.JULY.roll(-2), MonthOfYear.MAY);
        assertEquals(MonthOfYear.JULY.roll(-1), MonthOfYear.JUNE);
        assertEquals(MonthOfYear.JULY.roll(0), MonthOfYear.JULY);
        assertEquals(MonthOfYear.JULY.roll(1), MonthOfYear.AUGUST);
        assertEquals(MonthOfYear.JULY.roll(2), MonthOfYear.SEPTEMBER);
        assertEquals(MonthOfYear.JULY.roll(3), MonthOfYear.OCTOBER);
        assertEquals(MonthOfYear.JULY.roll(4), MonthOfYear.NOVEMBER);
        assertEquals(MonthOfYear.JULY.roll(5), MonthOfYear.DECEMBER);
        assertEquals(MonthOfYear.JULY.roll(6), MonthOfYear.JANUARY);
        assertEquals(MonthOfYear.JULY.roll(7), MonthOfYear.FEBRUARY);
        assertEquals(MonthOfYear.JULY.roll(8), MonthOfYear.MARCH);
        assertEquals(MonthOfYear.JULY.roll(9), MonthOfYear.APRIL);
        assertEquals(MonthOfYear.JULY.roll(10), MonthOfYear.MAY);
        assertEquals(MonthOfYear.JULY.roll(11), MonthOfYear.JUNE);
        assertEquals(MonthOfYear.JULY.roll(12), MonthOfYear.JULY);
    }

    //-----------------------------------------------------------------------
    // lengthInDays(boolean)
    //-----------------------------------------------------------------------
    public void test_lengthInDays_boolean_notLeapYear() {
        assertEquals(MonthOfYear.JANUARY.lengthInDays(false), 31);
        assertEquals(MonthOfYear.FEBRUARY.lengthInDays(false), 28);
        assertEquals(MonthOfYear.MARCH.lengthInDays(false), 31);
        assertEquals(MonthOfYear.APRIL.lengthInDays(false), 30);
        assertEquals(MonthOfYear.MAY.lengthInDays(false), 31);
        assertEquals(MonthOfYear.JUNE.lengthInDays(false), 30);
        assertEquals(MonthOfYear.JULY.lengthInDays(false), 31);
        assertEquals(MonthOfYear.AUGUST.lengthInDays(false), 31);
        assertEquals(MonthOfYear.SEPTEMBER.lengthInDays(false), 30);
        assertEquals(MonthOfYear.OCTOBER.lengthInDays(false), 31);
        assertEquals(MonthOfYear.NOVEMBER.lengthInDays(false), 30);
        assertEquals(MonthOfYear.DECEMBER.lengthInDays(false), 31);
    }

    public void test_lengthInDays_boolean_leapYear() {
        assertEquals(MonthOfYear.JANUARY.lengthInDays(true), 31);
        assertEquals(MonthOfYear.FEBRUARY.lengthInDays(true), 29);
        assertEquals(MonthOfYear.MARCH.lengthInDays(true), 31);
        assertEquals(MonthOfYear.APRIL.lengthInDays(true), 30);
        assertEquals(MonthOfYear.MAY.lengthInDays(true), 31);
        assertEquals(MonthOfYear.JUNE.lengthInDays(true), 30);
        assertEquals(MonthOfYear.JULY.lengthInDays(true), 31);
        assertEquals(MonthOfYear.AUGUST.lengthInDays(true), 31);
        assertEquals(MonthOfYear.SEPTEMBER.lengthInDays(true), 30);
        assertEquals(MonthOfYear.OCTOBER.lengthInDays(true), 31);
        assertEquals(MonthOfYear.NOVEMBER.lengthInDays(true), 30);
        assertEquals(MonthOfYear.DECEMBER.lengthInDays(true), 31);
    }

    //-----------------------------------------------------------------------
    // minLengthInDays()
    //-----------------------------------------------------------------------
    public void test_minLengthInDays() {
        assertEquals(MonthOfYear.JANUARY.minLengthInDays(), 31);
        assertEquals(MonthOfYear.FEBRUARY.minLengthInDays(), 28);
        assertEquals(MonthOfYear.MARCH.minLengthInDays(), 31);
        assertEquals(MonthOfYear.APRIL.minLengthInDays(), 30);
        assertEquals(MonthOfYear.MAY.minLengthInDays(), 31);
        assertEquals(MonthOfYear.JUNE.minLengthInDays(), 30);
        assertEquals(MonthOfYear.JULY.minLengthInDays(), 31);
        assertEquals(MonthOfYear.AUGUST.minLengthInDays(), 31);
        assertEquals(MonthOfYear.SEPTEMBER.minLengthInDays(), 30);
        assertEquals(MonthOfYear.OCTOBER.minLengthInDays(), 31);
        assertEquals(MonthOfYear.NOVEMBER.minLengthInDays(), 30);
        assertEquals(MonthOfYear.DECEMBER.minLengthInDays(), 31);
    }

    //-----------------------------------------------------------------------
    // maxLengthInDays()
    //-----------------------------------------------------------------------
    public void test_maxLengthInDays() {
        assertEquals(MonthOfYear.JANUARY.maxLengthInDays(), 31);
        assertEquals(MonthOfYear.FEBRUARY.maxLengthInDays(), 29);
        assertEquals(MonthOfYear.MARCH.maxLengthInDays(), 31);
        assertEquals(MonthOfYear.APRIL.maxLengthInDays(), 30);
        assertEquals(MonthOfYear.MAY.maxLengthInDays(), 31);
        assertEquals(MonthOfYear.JUNE.maxLengthInDays(), 30);
        assertEquals(MonthOfYear.JULY.maxLengthInDays(), 31);
        assertEquals(MonthOfYear.AUGUST.maxLengthInDays(), 31);
        assertEquals(MonthOfYear.SEPTEMBER.maxLengthInDays(), 30);
        assertEquals(MonthOfYear.OCTOBER.maxLengthInDays(), 31);
        assertEquals(MonthOfYear.NOVEMBER.maxLengthInDays(), 30);
        assertEquals(MonthOfYear.DECEMBER.maxLengthInDays(), 31);
    }

    //-----------------------------------------------------------------------
    // getLastDayOfMonth(boolean)
    //-----------------------------------------------------------------------
    public void test_getLastDayOfMonth_notLeapYear() {
        assertEquals(MonthOfYear.JANUARY.getLastDayOfMonth(false), 31);
        assertEquals(MonthOfYear.FEBRUARY.getLastDayOfMonth(false), 28);
        assertEquals(MonthOfYear.MARCH.getLastDayOfMonth(false), 31);
        assertEquals(MonthOfYear.APRIL.getLastDayOfMonth(false), 30);
        assertEquals(MonthOfYear.MAY.getLastDayOfMonth(false), 31);
        assertEquals(MonthOfYear.JUNE.getLastDayOfMonth(false), 30);
        assertEquals(MonthOfYear.JULY.getLastDayOfMonth(false), 31);
        assertEquals(MonthOfYear.AUGUST.getLastDayOfMonth(false), 31);
        assertEquals(MonthOfYear.SEPTEMBER.getLastDayOfMonth(false), 30);
        assertEquals(MonthOfYear.OCTOBER.getLastDayOfMonth(false), 31);
        assertEquals(MonthOfYear.NOVEMBER.getLastDayOfMonth(false), 30);
        assertEquals(MonthOfYear.DECEMBER.getLastDayOfMonth(false), 31);
    }

    public void test_getLastDayOfMonth_leapYear() {
        assertEquals(MonthOfYear.JANUARY.getLastDayOfMonth(true), 31);
        assertEquals(MonthOfYear.FEBRUARY.getLastDayOfMonth(true), 29);
        assertEquals(MonthOfYear.MARCH.getLastDayOfMonth(true), 31);
        assertEquals(MonthOfYear.APRIL.getLastDayOfMonth(true), 30);
        assertEquals(MonthOfYear.MAY.getLastDayOfMonth(true), 31);
        assertEquals(MonthOfYear.JUNE.getLastDayOfMonth(true), 30);
        assertEquals(MonthOfYear.JULY.getLastDayOfMonth(true), 31);
        assertEquals(MonthOfYear.AUGUST.getLastDayOfMonth(true), 31);
        assertEquals(MonthOfYear.SEPTEMBER.getLastDayOfMonth(true), 30);
        assertEquals(MonthOfYear.OCTOBER.getLastDayOfMonth(true), 31);
        assertEquals(MonthOfYear.NOVEMBER.getLastDayOfMonth(true), 30);
        assertEquals(MonthOfYear.DECEMBER.getLastDayOfMonth(true), 31);
    }

    //-----------------------------------------------------------------------
    // getMonthStartDayOfYear(boolean)
    //-----------------------------------------------------------------------
    public void test_getMonthStartDayOfYear_notLeapYear() {
        assertEquals(MonthOfYear.JANUARY.getMonthStartDayOfYear(false), 1);
        assertEquals(MonthOfYear.FEBRUARY.getMonthStartDayOfYear(false), 1 + 31);
        assertEquals(MonthOfYear.MARCH.getMonthStartDayOfYear(false), 1 + 31 + 28);
        assertEquals(MonthOfYear.APRIL.getMonthStartDayOfYear(false), 1 + 31 + 28 + 31);
        assertEquals(MonthOfYear.MAY.getMonthStartDayOfYear(false), 1 + 31 + 28 + 31 + 30);
        assertEquals(MonthOfYear.JUNE.getMonthStartDayOfYear(false), 1 + 31 + 28 + 31 + 30 + 31);
        assertEquals(MonthOfYear.JULY.getMonthStartDayOfYear(false), 1 + 31 + 28 + 31 + 30 + 31 + 30);
        assertEquals(MonthOfYear.AUGUST.getMonthStartDayOfYear(false), 1 + 31 + 28 + 31 + 30 + 31 + 30 + 31);
        assertEquals(MonthOfYear.SEPTEMBER.getMonthStartDayOfYear(false), 1 + 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31);
        assertEquals(MonthOfYear.OCTOBER.getMonthStartDayOfYear(false), 1 + 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30);
        assertEquals(MonthOfYear.NOVEMBER.getMonthStartDayOfYear(false), 1 + 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31);
        assertEquals(MonthOfYear.DECEMBER.getMonthStartDayOfYear(false), 1 + 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31 + 30);
    }

    public void test_getMonthStartDayOfYear_leapYear() {
        assertEquals(MonthOfYear.JANUARY.getMonthStartDayOfYear(true), 1);
        assertEquals(MonthOfYear.FEBRUARY.getMonthStartDayOfYear(true), 1 + 31);
        assertEquals(MonthOfYear.MARCH.getMonthStartDayOfYear(true), 1 + 31 + 29);
        assertEquals(MonthOfYear.APRIL.getMonthStartDayOfYear(true), 1 + 31 + 29 + 31);
        assertEquals(MonthOfYear.MAY.getMonthStartDayOfYear(true), 1 + 31 + 29 + 31 + 30);
        assertEquals(MonthOfYear.JUNE.getMonthStartDayOfYear(true), 1 + 31 + 29 + 31 + 30 + 31);
        assertEquals(MonthOfYear.JULY.getMonthStartDayOfYear(true), 1 + 31 + 29 + 31 + 30 + 31 + 30);
        assertEquals(MonthOfYear.AUGUST.getMonthStartDayOfYear(true), 1 + 31 + 29 + 31 + 30 + 31 + 30 + 31);
        assertEquals(MonthOfYear.SEPTEMBER.getMonthStartDayOfYear(true), 1 + 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31);
        assertEquals(MonthOfYear.OCTOBER.getMonthStartDayOfYear(true), 1 + 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 30);
        assertEquals(MonthOfYear.NOVEMBER.getMonthStartDayOfYear(true), 1 + 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31);
        assertEquals(MonthOfYear.DECEMBER.getMonthStartDayOfYear(true), 1 + 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31 + 30);
    }

    //-----------------------------------------------------------------------
    // getMonthEndDayOfYear(boolean)
    //-----------------------------------------------------------------------
    public void test_getMonthEndDayOfYear_notLeapYear() {
        assertEquals(MonthOfYear.JANUARY.getMonthEndDayOfYear(false), 31);
        assertEquals(MonthOfYear.FEBRUARY.getMonthEndDayOfYear(false), 31 + 28);
        assertEquals(MonthOfYear.MARCH.getMonthEndDayOfYear(false), 31 + 28 + 31);
        assertEquals(MonthOfYear.APRIL.getMonthEndDayOfYear(false), 31 + 28 + 31 + 30);
        assertEquals(MonthOfYear.MAY.getMonthEndDayOfYear(false), 31 + 28 + 31 + 30 + 31);
        assertEquals(MonthOfYear.JUNE.getMonthEndDayOfYear(false), 31 + 28 + 31 + 30 + 31 + 30);
        assertEquals(MonthOfYear.JULY.getMonthEndDayOfYear(false), 31 + 28 + 31 + 30 + 31 + 30 + 31);
        assertEquals(MonthOfYear.AUGUST.getMonthEndDayOfYear(false), 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31);
        assertEquals(MonthOfYear.SEPTEMBER.getMonthEndDayOfYear(false), 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30);
        assertEquals(MonthOfYear.OCTOBER.getMonthEndDayOfYear(false), 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31);
        assertEquals(MonthOfYear.NOVEMBER.getMonthEndDayOfYear(false), 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31 + 30);
        assertEquals(MonthOfYear.DECEMBER.getMonthEndDayOfYear(false), 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31 + 30 + 31);
    }

    public void test_getMonthEndDayOfYear_leapYear() {
        assertEquals(MonthOfYear.JANUARY.getMonthEndDayOfYear(true), 31);
        assertEquals(MonthOfYear.FEBRUARY.getMonthEndDayOfYear(true), 31 + 29);
        assertEquals(MonthOfYear.MARCH.getMonthEndDayOfYear(true), 31 + 29 + 31);
        assertEquals(MonthOfYear.APRIL.getMonthEndDayOfYear(true), 31 + 29 + 31 + 30);
        assertEquals(MonthOfYear.MAY.getMonthEndDayOfYear(true), 31 + 29 + 31 + 30 + 31);
        assertEquals(MonthOfYear.JUNE.getMonthEndDayOfYear(true), 31 + 29 + 31 + 30 + 31 + 30);
        assertEquals(MonthOfYear.JULY.getMonthEndDayOfYear(true), 31 + 29 + 31 + 30 + 31 + 30 + 31);
        assertEquals(MonthOfYear.AUGUST.getMonthEndDayOfYear(true), 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31);
        assertEquals(MonthOfYear.SEPTEMBER.getMonthEndDayOfYear(true), 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 30);
        assertEquals(MonthOfYear.OCTOBER.getMonthEndDayOfYear(true), 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31);
        assertEquals(MonthOfYear.NOVEMBER.getMonthEndDayOfYear(true), 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31 + 30);
        assertEquals(MonthOfYear.DECEMBER.getMonthEndDayOfYear(true), 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31 + 30 + 31);
    }

    //-----------------------------------------------------------------------
    // getQuarterOfYear()
    //-----------------------------------------------------------------------
    public void test_getQuarterOfYear() {
        assertEquals(MonthOfYear.JANUARY.getQuarterOfYear(), QuarterOfYear.Q1);
        assertEquals(MonthOfYear.FEBRUARY.getQuarterOfYear(), QuarterOfYear.Q1);
        assertEquals(MonthOfYear.MARCH.getQuarterOfYear(), QuarterOfYear.Q1);
        assertEquals(MonthOfYear.APRIL.getQuarterOfYear(), QuarterOfYear.Q2);
        assertEquals(MonthOfYear.MAY.getQuarterOfYear(), QuarterOfYear.Q2);
        assertEquals(MonthOfYear.JUNE.getQuarterOfYear(), QuarterOfYear.Q2);
        assertEquals(MonthOfYear.JULY.getQuarterOfYear(), QuarterOfYear.Q3);
        assertEquals(MonthOfYear.AUGUST.getQuarterOfYear(), QuarterOfYear.Q3);
        assertEquals(MonthOfYear.SEPTEMBER.getQuarterOfYear(), QuarterOfYear.Q3);
        assertEquals(MonthOfYear.OCTOBER.getQuarterOfYear(), QuarterOfYear.Q4);
        assertEquals(MonthOfYear.NOVEMBER.getQuarterOfYear(), QuarterOfYear.Q4);
        assertEquals(MonthOfYear.DECEMBER.getQuarterOfYear(), QuarterOfYear.Q4);
    }

    //-----------------------------------------------------------------------
    // getMonthOfQuarter()
    //-----------------------------------------------------------------------
    public void test_getMonthOfQuarter() {
        assertEquals(MonthOfYear.JANUARY.getMonthOfQuarter(), 1);
        assertEquals(MonthOfYear.FEBRUARY.getMonthOfQuarter(), 2);
        assertEquals(MonthOfYear.MARCH.getMonthOfQuarter(), 3);
        assertEquals(MonthOfYear.APRIL.getMonthOfQuarter(), 1);
        assertEquals(MonthOfYear.MAY.getMonthOfQuarter(), 2);
        assertEquals(MonthOfYear.JUNE.getMonthOfQuarter(), 3);
        assertEquals(MonthOfYear.JULY.getMonthOfQuarter(), 1);
        assertEquals(MonthOfYear.AUGUST.getMonthOfQuarter(), 2);
        assertEquals(MonthOfYear.SEPTEMBER.getMonthOfQuarter(), 3);
        assertEquals(MonthOfYear.OCTOBER.getMonthOfQuarter(), 1);
        assertEquals(MonthOfYear.NOVEMBER.getMonthOfQuarter(), 2);
        assertEquals(MonthOfYear.DECEMBER.getMonthOfQuarter(), 3);
    }

    //-----------------------------------------------------------------------
    // matcher
    //-----------------------------------------------------------------------
    public void test_matcher() {
        assertEquals(MonthOfYear.FEBRUARY.matchesCalendrical(MONTH_OF_YEAR.field(2)), true);
        assertEquals(MonthOfYear.FEBRUARY.matchesCalendrical(MonthOfYear.FEBRUARY), true);
        assertEquals(MonthOfYear.FEBRUARY.matchesCalendrical(MonthOfYear.MARCH), false);
        
        assertEquals(MonthOfYear.FEBRUARY.matchesCalendrical(LocalDate.of(1970, 2, 1)), true);
        assertEquals(MonthOfYear.FEBRUARY.matchesCalendrical(LocalDate.of(1970, 5, 1)), false);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_matcher_null() {
        MonthOfYear.FEBRUARY.matchesCalendrical(null);
    }

    //-----------------------------------------------------------------------
    // toField()
    //-----------------------------------------------------------------------
    public void test_toField() {
        assertEquals(MonthOfYear.JANUARY.toField(), MONTH_OF_YEAR.field(1));
        assertEquals(MonthOfYear.APRIL.toField(), MONTH_OF_YEAR.field(4));
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        assertEquals(MonthOfYear.JANUARY.toString(), "JANUARY");
        assertEquals(MonthOfYear.FEBRUARY.toString(), "FEBRUARY");
        assertEquals(MonthOfYear.MARCH.toString(), "MARCH");
        assertEquals(MonthOfYear.APRIL.toString(), "APRIL");
        assertEquals(MonthOfYear.MAY.toString(), "MAY");
        assertEquals(MonthOfYear.JUNE.toString(), "JUNE");
        assertEquals(MonthOfYear.JULY.toString(), "JULY");
        assertEquals(MonthOfYear.AUGUST.toString(), "AUGUST");
        assertEquals(MonthOfYear.SEPTEMBER.toString(), "SEPTEMBER");
        assertEquals(MonthOfYear.OCTOBER.toString(), "OCTOBER");
        assertEquals(MonthOfYear.NOVEMBER.toString(), "NOVEMBER");
        assertEquals(MonthOfYear.DECEMBER.toString(), "DECEMBER");
    }

    //-----------------------------------------------------------------------
    // generated methods
    //-----------------------------------------------------------------------
    public void test_enum() {
        assertEquals(MonthOfYear.valueOf("JANUARY"), MonthOfYear.JANUARY);
        assertEquals(MonthOfYear.values()[0], MonthOfYear.JANUARY);
    }

}
