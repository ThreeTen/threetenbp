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

import static org.testng.Assert.*;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import javax.time.CalendricalException;
import javax.time.calendar.field.DayOfMonth;
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
        assertTrue(TEST_2007_07_15 instanceof CalendricalProvider);
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
    // factories
    //-----------------------------------------------------------------------
    public void factory_date_YMD() {
        OffsetDate test = OffsetDate.date(Year.isoYear(2007), MonthOfYear.JULY, DayOfMonth.dayOfMonth(15), OFFSET_PONE);
        assertEquals(test.getYear(), Year.isoYear(2007));
        assertEquals(test.getMonthOfYear(), MonthOfYear.JULY);
        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(15));
    }

    //-----------------------------------------------------------------------
    public void factory_date_intMonthInt() {
        OffsetDate test = OffsetDate.date(2007, MonthOfYear.JULY, 15, OFFSET_PONE);
        assertEquals(test.getYear(), Year.isoYear(2007));
        assertEquals(test.getMonthOfYear(), MonthOfYear.JULY);
        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(15));
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
    public void factory_DateProvider() {
        DateProvider localDate = LocalDate.date(2008, 6, 30);
        OffsetDate test = OffsetDate.date(localDate, OFFSET_PONE);
        assertEquals(test.getYear(), Year.isoYear(2008));
        assertEquals(test.getMonthOfYear(), MonthOfYear.monthOfYear(6));
        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class)
    public void constructor_nullDate() throws Throwable  {
        Constructor<OffsetDate> con = OffsetDate.class.getDeclaredConstructor(LocalDate.class, ZoneOffset.class);
        con.setAccessible(true);
        try {
            con.newInstance(null, OFFSET_PONE);
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void constructor_nullOffset() throws Throwable  {
        Constructor<OffsetDate> con = OffsetDate.class.getDeclaredConstructor(LocalDate.class, ZoneOffset.class);
        con.setAccessible(true);
        try {
            con.newInstance(LocalDate.date(2008, 6, 30), null);
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }

    //-----------------------------------------------------------------------
    // basics
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleDates")
    Object[][] provider_sampleDates() {
        return new Object[][] {
            {2008, 7, 5, OFFSET_PONE},
            {2007, 7, 5, OFFSET_PONE},
            {2006, 7, 5, OFFSET_PONE},
            {2005, 7, 5, OFFSET_PONE},
            {2004, 1, 1, OFFSET_PONE},
            {-1, 1, 2, OFFSET_PONE},
        };
    }

    @Test(dataProvider="sampleDates")
    public void test_get(int y, int m, int d, ZoneOffset offset) {
        LocalDate localDate = LocalDate.date(y, m, d);
        OffsetDate a = OffsetDate.date(localDate, offset);
        assertSame(a.getDate(), localDate);
        assertSame(a.getOffset(), offset);
        assertEquals(a.getChronology(), ISOChronology.INSTANCE);
        
        assertEquals(a.getYear(), localDate.getYear());
        assertEquals(a.getMonthOfYear(), localDate.getMonthOfYear());
        assertEquals(a.getDayOfMonth(), localDate.getDayOfMonth());
        assertEquals(a.getDayOfYear(), localDate.getDayOfYear());
        assertEquals(a.getDayOfWeek(), localDate.getDayOfWeek());
        
        assertSame(a.toLocalDate(), localDate);
        assertEquals(a.toCalendrical(), Calendrical.calendrical(localDate, null, offset, null));
        assertEquals(a.toString(), localDate.toString() + offset.toString());
    }

    //-----------------------------------------------------------------------
    // isSupported(DateTimeFieldRule)
    //-----------------------------------------------------------------------
    public void test_isSupported() {
        assertEquals(TEST_2007_07_15.isSupported(ISOChronology.INSTANCE.year()), true);
        assertEquals(TEST_2007_07_15.isSupported(ISOChronology.INSTANCE.monthOfYear()), true);
        assertEquals(TEST_2007_07_15.isSupported(ISOChronology.INSTANCE.dayOfMonth()), true);
        assertEquals(TEST_2007_07_15.isSupported(ISOChronology.INSTANCE.dayOfWeek()), true);
        assertEquals(TEST_2007_07_15.isSupported(ISOChronology.INSTANCE.dayOfYear()), true);
        
        assertEquals(TEST_2007_07_15.isSupported(ISOChronology.INSTANCE.hourOfDay()), false);
        assertEquals(TEST_2007_07_15.isSupported(ISOChronology.INSTANCE.minuteOfHour()), false);
        assertEquals(TEST_2007_07_15.isSupported(ISOChronology.INSTANCE.secondOfMinute()), false);
    }

    //-----------------------------------------------------------------------
    // get(DateTimeFieldRule)
    //-----------------------------------------------------------------------
    public void test_get_DateTimeFieldRule() {
        OffsetDate test = OffsetDate.date(2008, 6, 30, OFFSET_PONE);
        assertEquals(test.get(ISOChronology.INSTANCE.year()), 2008);
        assertEquals(test.get(ISOChronology.INSTANCE.monthOfYear()), 6);
        assertEquals(test.get(ISOChronology.INSTANCE.dayOfMonth()), 30);
        assertEquals(test.get(ISOChronology.INSTANCE.dayOfWeek()), 1);
        assertEquals(test.get(ISOChronology.INSTANCE.dayOfYear()), 182);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_get_DateTimeFieldRule_null() {
        OffsetDate test = OffsetDate.date(2008, 6, 30, OFFSET_PONE);
        test.get((DateTimeFieldRule) null);
    }

    @Test(expectedExceptions=UnsupportedCalendarFieldException.class )
    public void test_get_DateTimeFieldRule_unsupported() {
        OffsetDate test = OffsetDate.date(2008, 6, 30, OFFSET_PONE);
        try {
            test.get(MockRuleNoValue.INSTANCE);
        } catch (UnsupportedCalendarFieldException ex) {
            assertEquals(ex.getFieldRule(), MockRuleNoValue.INSTANCE);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // withDate()
    //-----------------------------------------------------------------------
    public void test_withDate() {
        OffsetDate base = OffsetDate.date(2008, 6, 30, OFFSET_PONE);
        LocalDate date = LocalDate.date(2008, 7, 1);
        OffsetDate test = base.withDate(date);
        assertSame(test.toLocalDate(), date);
        assertSame(test.getOffset(), base.getOffset());
    }

    public void test_withDate_noChange() {
        OffsetDate base = OffsetDate.date(2008, 6, 30, OFFSET_PONE);
        LocalDate date = LocalDate.date(2008, 6, 30);
        OffsetDate test = base.withDate(date);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_withDate_null() {
        OffsetDate base = OffsetDate.date(2008, 6, 30, OFFSET_PONE);
        base.withDate(null);
    }

    //-----------------------------------------------------------------------
    // withOffset()
    //-----------------------------------------------------------------------
    public void test_withOffset() {
        OffsetDate base = OffsetDate.date(2008, 6, 30, OFFSET_PONE);
        OffsetDate test = base.withOffset(OFFSET_PTWO);
        assertSame(test.toLocalDate(), base.toLocalDate());
        assertSame(test.getOffset(), OFFSET_PTWO);
    }

    public void test_withOffset_noChange() {
        OffsetDate base = OffsetDate.date(2008, 6, 30, OFFSET_PONE);
        OffsetDate test = base.withOffset(OFFSET_PONE);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_withOffset_null() {
        OffsetDate base = OffsetDate.date(2008, 6, 30, OFFSET_PONE);
        base.withOffset(null);
    }

    //-----------------------------------------------------------------------
    // with()
    //-----------------------------------------------------------------------
    public void test_with() {
        OffsetDate base = OffsetDate.date(2008, 6, 30, OFFSET_PONE);
        OffsetDate test = base.with(DayOfMonth.dayOfMonth(1));
        assertEquals(test.toLocalDate(), LocalDate.date(2008, 6, 1));
        assertSame(test.getOffset(), base.getOffset());
    }

    public void test_with_noChange() {
        LocalDate date = LocalDate.date(2008, 6, 30);
        OffsetDate base = OffsetDate.date(date, OFFSET_PONE);
        OffsetDate test = base.with(date);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_with_null() {
        OffsetDate base = OffsetDate.date(2008, 6, 30, OFFSET_PONE);
        base.with(null);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_with_badAdjuster() {
        OffsetDate base = OffsetDate.date(2008, 6, 30, OFFSET_PONE);
        base.with(new MockDateAdjusterReturnsNull());
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

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_withDayOfMonth_invalidForMonth() {
        try {
            OffsetDate.date(2007, 11, 30, OFFSET_PONE).withDayOfMonth(31);
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getFieldRule(), ISOChronology.INSTANCE.dayOfMonth());
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withDayOfMonth_invalidAlways() {
        try {
            OffsetDate.date(2007, 11, 30, OFFSET_PONE).withDayOfMonth(32);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getFieldRule(), ISOChronology.INSTANCE.dayOfMonth());
            throw ex;
        }
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
        assertSame(t, TEST_2007_07_15);
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

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusYears_invalidTooLarge() {
        OffsetDate.date(Year.MAX_YEAR, 1, 1, OFFSET_PONE).plusYears(1);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusYears_invalidTooSmall() {
        OffsetDate.date(Year.MIN_YEAR, 1, 1, OFFSET_PONE).plusYears(-1);
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
        assertSame(t, TEST_2007_07_15);
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

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusMonths_invalidTooLarge() {
        OffsetDate.date(Year.MAX_YEAR, 12, 1, OFFSET_PONE).plusMonths(1);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusMonths_invalidTooSmall() {
        OffsetDate.date(Year.MIN_YEAR, 1, 1, OFFSET_PONE).plusMonths(-1);
    }

    //-----------------------------------------------------------------------
    // plusWeeks()
    //-----------------------------------------------------------------------
    public void test_plusWeeks() {
        OffsetDate base = OffsetDate.date(2008, 6, 30, OFFSET_PONE);
        OffsetDate test = base.plusWeeks(1);
        assertEquals(test, OffsetDate.date(2008, 7, 7, OFFSET_PONE));
    }

    public void test_plusWeeks_zero() {
        OffsetDate base = OffsetDate.date(2008, 6, 30, OFFSET_PONE);
        OffsetDate test = base.plusWeeks(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusDays()
    //-----------------------------------------------------------------------
    public void test_plusDays() {
        OffsetDate base = OffsetDate.date(2008, 6, 30, OFFSET_PONE);
        OffsetDate test = base.plusDays(1);
        assertEquals(test, OffsetDate.date(2008, 7, 1, OFFSET_PONE));
    }

    public void test_plusDays_zero() {
        OffsetDate base = OffsetDate.date(2008, 6, 30, OFFSET_PONE);
        OffsetDate test = base.plusDays(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // matches()
    //-----------------------------------------------------------------------
    public void test_matches() {
        OffsetDate test = OffsetDate.date(2008, 6, 30, OFFSET_PONE);
        assertEquals(test.matches(Year.isoYear(2008)), true);
        assertEquals(test.matches(Year.isoYear(2007)), false);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_matches_null() {
        OffsetDate base = OffsetDate.date(2008, 6, 30, OFFSET_PONE);
        base.matches(null);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo_date() {
        OffsetDate a = OffsetDate.date(2008, 6, 29, OFFSET_PONE);
        OffsetDate b = OffsetDate.date(2008, 6, 30, OFFSET_PONE);  // a is before b due to date
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    public void test_compareTo_offset() {
        OffsetDate a = OffsetDate.date(2008, 6, 30, OFFSET_PTWO);
        OffsetDate b = OffsetDate.date(2008, 6, 30, OFFSET_PONE);  // a is before b due to offset
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    public void test_compareTo_both() {
        OffsetDate a = OffsetDate.date(2008, 6, 29, OFFSET_PTWO);
        OffsetDate b = OffsetDate.date(2008, 6, 30, OFFSET_PONE);  // a is before b on instant scale
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    public void test_compareTo_24hourDifference() {
        OffsetDate a = OffsetDate.date(2008, 6, 29, ZoneOffset.zoneOffset(-12));
        OffsetDate b = OffsetDate.date(2008, 6, 30, ZoneOffset.zoneOffset(12));  // a is before b despite being same time-line time
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_compareTo_null() {
        OffsetDate a = OffsetDate.date(2008, 6, 30, OFFSET_PONE);
        a.compareTo(null);
    }

    //-----------------------------------------------------------------------
    // isAfter() / isBefore()
    //-----------------------------------------------------------------------
    public void test_isBeforeIsAfter() {
        OffsetDate a = OffsetDate.date(2008, 6, 29, OFFSET_PONE);
        OffsetDate b = OffsetDate.date(2008, 6, 30, OFFSET_PONE);  // a is before b due to date
        assertEquals(a.isBefore(b), true);
        assertEquals(a.isAfter(b), false);
        assertEquals(b.isBefore(a), false);
        assertEquals(b.isAfter(a), true);
        assertEquals(a.isBefore(a), false);
        assertEquals(b.isBefore(b), false);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isBefore_null() {
        OffsetDate a = OffsetDate.date(2008, 6, 30, OFFSET_PONE);
        a.isBefore(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isAfter_null() {
        OffsetDate a = OffsetDate.date(2008, 6, 30, OFFSET_PONE);
        a.isAfter(null);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_equals_true(int y, int m, int d, ZoneOffset ignored) {
        OffsetDate a = OffsetDate.date(y, m, d, OFFSET_PONE);
        OffsetDate b = OffsetDate.date(y, m, d, OFFSET_PONE);
        assertEquals(a.equals(b), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
    }
    @Test(dataProvider="sampleDates")
    public void test_equals_false_year_differs(int y, int m, int d, ZoneOffset ignored) {
        OffsetDate a = OffsetDate.date(y, m, d, OFFSET_PONE);
        OffsetDate b = OffsetDate.date(y + 1, m, d, OFFSET_PONE);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleDates")
    public void test_equals_false_month_differs(int y, int m, int d, ZoneOffset ignored) {
        OffsetDate a = OffsetDate.date(y, m, d, OFFSET_PONE);
        OffsetDate b = OffsetDate.date(y, m + 1, d, OFFSET_PONE);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleDates")
    public void test_equals_false_day_differs(int y, int m, int d, ZoneOffset ignored) {
        OffsetDate a = OffsetDate.date(y, m, d, OFFSET_PONE);
        OffsetDate b = OffsetDate.date(y, m, d + 1, OFFSET_PONE);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleDates")
    public void test_equals_false_offset_differs(int y, int m, int d, ZoneOffset ignored) {
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
