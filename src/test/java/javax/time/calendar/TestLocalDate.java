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
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.QuarterOfYear;
import javax.time.calendar.field.WeekOfMonth;
import javax.time.calendar.field.WeekOfWeekyear;
import javax.time.calendar.field.Weekyear;
import javax.time.calendar.field.Year;
import javax.time.calendar.format.FlexiDateTime;

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
public class TestLocalDate {

    private static final String MIN_YEAR_STR = Integer.toString(Year.MIN_YEAR);
    private static final String MAX_YEAR_STR = Integer.toString(Year.MAX_YEAR);
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
        assertTrue(TEST_2007_07_15 instanceof DateProvider);
        assertTrue(TEST_2007_07_15 instanceof DateMatcher);
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(TEST_2007_07_15);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), TEST_2007_07_15);
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
    public void factory_date_objects() {
        assertEquals(TEST_2007_07_15, LocalDate.date(Year.isoYear(2007), MonthOfYear.JULY, DayOfMonth.dayOfMonth(15)));
    }

    public void factory_date_objects_leapYear() {
        LocalDate test_2008_02_29 = LocalDate.date(Year.isoYear(2008), MonthOfYear.FEBRUARY, DayOfMonth.dayOfMonth(29));
        assertEquals(test_2008_02_29.getYear(), Year.isoYear(2008));
        assertEquals(test_2008_02_29.getMonthOfYear(), MonthOfYear.FEBRUARY);
        assertEquals(test_2008_02_29.getDayOfMonth(), DayOfMonth.dayOfMonth(29));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_date_objects_nullYear() {
        LocalDate.date(null, MonthOfYear.JULY, DayOfMonth.dayOfMonth(15));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_date_objects_nullMonth() {
        LocalDate.date(Year.isoYear(2007), null, DayOfMonth.dayOfMonth(15));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_date_objects_nullDay() {
        LocalDate.date(Year.isoYear(2007), MonthOfYear.JULY, null);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_date_objects_nonleapYear() {
        LocalDate.date(Year.isoYear(2007), MonthOfYear.FEBRUARY, DayOfMonth.dayOfMonth(29));
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_date_objects_dayTooBig() {
        LocalDate.date(Year.isoYear(2007), MonthOfYear.APRIL, DayOfMonth.dayOfMonth(31));
    }

    //-----------------------------------------------------------------------
    public void factory_date_intsMonth() {
        assertEquals(TEST_2007_07_15, LocalDate.date(2007, MonthOfYear.JULY, 15));
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_date_intsMonth_dayTooLow() {
        LocalDate.date(2007, MonthOfYear.JANUARY, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_date_intsMonth_dayTooHigh() {
        LocalDate.date(2007, MonthOfYear.JANUARY, 32);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_date_intsMonth_yearTooLow() {
        LocalDate.date(Integer.MIN_VALUE, MonthOfYear.JANUARY, 1);
    }

    //-----------------------------------------------------------------------
    public void factory_date_ints() {
        assertEquals(TEST_2007_07_15.getYear(), Year.isoYear(2007));
        assertEquals(TEST_2007_07_15.getMonthOfYear(), MonthOfYear.JULY);
        assertEquals(TEST_2007_07_15.getDayOfMonth(), DayOfMonth.dayOfMonth(15));
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
    public void factory_date_DateProvider() {
        assertEquals(TEST_2007_07_15, LocalDate.date(TEST_2007_07_15));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_date_DateProvider_null() {
        LocalDate.date(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_date_DateProvider_null_toLocalDate() {
        LocalDate.date(new DateProvider() {
            public LocalDate toLocalDate() {
                return null;
            }

            public FlexiDateTime toFlexiDateTime() {
                return null;
            }
        });
    }

    //-----------------------------------------------------------------------
    // Since plusDays/minusDays actually depends on MJDays, it cannot be used for testing
    private LocalDate next(LocalDate date) {
        int newDayOfMonth = date.getDayOfMonth().getValue() + 1;

        if (newDayOfMonth <= date.getMonthOfYear().lengthInDays(date.getYear())) {
            return date.withDayOfMonth(newDayOfMonth);
        }

        date = date.withDayOfMonth(1);

        if (date.getMonthOfYear() == MonthOfYear.DECEMBER) {
            date = date.with(date.getYear().next());
        }

        return date.with(date.getMonthOfYear().next());
    }

    private LocalDate previous(LocalDate date) {
        int newDayOfMonth = date.getDayOfMonth().getValue() - 1;

        if (newDayOfMonth > 0) {
            return date.withDayOfMonth(newDayOfMonth);
        }

        date = date.with(date.getMonthOfYear().previous());

        if (date.getMonthOfYear() == MonthOfYear.DECEMBER) {
            date = date.with(date.getYear().previous());
        }

        return date.with(date.getMonthOfYear().getLastDayOfMonth(date.getYear()));
    }

    //-----------------------------------------------------------------------
    public void factory_fromMJDays() {
        LocalDate test = LocalDate.date(0, 1, 1);
        for (int i = -678941; i < 700000; i++) {
            assertEquals(LocalDate.fromMJDays(i), test);
            test = next(test);
        }

        test = LocalDate.date(0, 1, 1);
        for (int i = -678941; i > -2000000; i--) {
            assertEquals(LocalDate.fromMJDays(i), test);
            test = previous(test);
        }

        assertEquals(LocalDate.fromMJDays(40587), LocalDate.date(1970, 1, 1));
        assertEquals(LocalDate.fromMJDays(-678942), LocalDate.date(-1, 12, 31));
    }

    //-----------------------------------------------------------------------
    public void test_getChronology() {
        assertSame(ISOChronology.INSTANCE, TEST_2007_07_15.getChronology());
    }

    //-----------------------------------------------------------------------
    //TODO: implement this test
    public void test_isSupported() {
//        assertTrue(TEST_2007_07_15.isSupported(Era.RULE));
//        assertTrue(TEST_2007_07_15.isSupported(MilleniumOfEra.RULE));
//        assertTrue(TEST_2007_07_15.isSupported(CenturyOfEra.RULE));
//        assertTrue(TEST_2007_07_15.isSupported(DecadeOfCentury.RULE));
//        assertTrue(TEST_2007_07_15.isSupported(Year.rule()));
//        assertTrue(TEST_2007_07_15.isSupported(YearOfEra.RULE));
//        assertTrue(TEST_2007_07_15.isSupported(QuarterOfYear.rule()));
//        assertTrue(TEST_2007_07_15.isSupported(MonthOfYear.rule()));
//        assertTrue(TEST_2007_07_15.isSupported(MonthOfQuarter.RULE));
//        assertTrue(TEST_2007_07_15.isSupported(DayOfMonth.rule()));
//        assertTrue(TEST_2007_07_15.isSupported(DayOfWeek.rule()));
//        assertTrue(TEST_2007_07_15.isSupported(DayOfYear.rule()));
//        assertTrue(TEST_2007_07_15.isSupported(WeekOfMonth.rule()));
//        assertTrue(TEST_2007_07_15.isSupported(WeekOfWeekyear.rule()));
//        assertTrue(TEST_2007_07_15.isSupported(Weekyear.rule()));
//
//        assertFalse(TEST_2007_07_15.isSupported(HourOfDay.RULE));
//        assertFalse(TEST_2007_07_15.isSupported(MinuteOfHour.RULE));
//        assertFalse(TEST_2007_07_15.isSupported(MinuteOfDay.RULE));
//        assertFalse(TEST_2007_07_15.isSupported(SecondOfMinute.RULE));
//        assertFalse(TEST_2007_07_15.isSupported(SecondOfDay.RULE));
//        assertFalse(TEST_2007_07_15.isSupported(NanoOfSecond.RULE));
//        assertFalse(TEST_2007_07_15.isSupported(HourOfMeridiem.RULE));
//        assertFalse(TEST_2007_07_15.isSupported(MeridiemOfDay.RULE));
    }

    // TODO: enable all assertions
    public void test_get() {
        assertEquals(TEST_2007_07_15.get(Era.RULE), Era.AD.getValue());
//        assertEquals(TEST_2007_07_15.get(MilleniumOfEra.RULE), TEST_2007_07_15.getYear().getMilleniumOfEra());
//        assertEquals(TEST_2007_07_15.get(CenturyOfEra.RULE), TEST_2007_07_15.getYear().getCenturyOfEra());
//        assertEquals(TEST_2007_07_15.get(DecadeOfCentury.RULE), TEST_2007_07_15.getYear().getDecadeOfCentury());
        assertEquals(TEST_2007_07_15.get(Year.rule()), TEST_2007_07_15.getYear().getValue());
//        assertEquals(TEST_2007_07_15.get(YearOfEra.RULE), TEST_2007_07_15.getYear().getYearOfEra());
        assertEquals(TEST_2007_07_15.get(QuarterOfYear.rule()), TEST_2007_07_15.getMonthOfYear().getQuarterOfYear().getValue());
        assertEquals(TEST_2007_07_15.get(MonthOfYear.rule()), TEST_2007_07_15.getMonthOfYear().getValue());
//        assertEquals(TEST_2007_07_15.get(MonthOfQuarter.RULE), TEST_2007_07_15.getMonthOfYear().getMonthOfQuarter());
        assertEquals(TEST_2007_07_15.get(DayOfMonth.rule()), TEST_2007_07_15.getDayOfMonth().getValue());
        assertEquals(TEST_2007_07_15.get(DayOfWeek.rule()), TEST_2007_07_15.getDayOfWeek().getValue());
        assertEquals(TEST_2007_07_15.get(DayOfYear.rule()), TEST_2007_07_15.getDayOfYear().getValue());
        assertEquals(TEST_2007_07_15.get(WeekOfMonth.rule()), WeekOfMonth.weekOfMonth(TEST_2007_07_15).getValue());
        assertEquals(TEST_2007_07_15.get(WeekOfWeekyear.rule()), WeekOfWeekyear.weekOfWeekyear(TEST_2007_07_15).getValue());
        assertEquals(TEST_2007_07_15.get(Weekyear.rule()), Weekyear.weekyear(TEST_2007_07_15).getValue());
    }

    @Test(expectedExceptions=UnsupportedCalendarFieldException.class)
    public void test_get_unsupported() {
        TEST_2007_07_15.get(HourOfDay.RULE);
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
    public void test_getYearMonth(int y, int m, int d) {
        assertEquals(LocalDate.date(y, m, d).getYearMonth(), YearMonth.yearMonth(y, m));
    }

    @Test(dataProvider="sampleDates")
    public void test_getMonthDay(int y, int m, int d) {
        assertEquals(LocalDate.date(y, m, d).getMonthDay(), MonthDay.monthDay(m, d));
    }

    @Test(dataProvider="sampleDates")
    public void test_get(int y, int m, int d) {
        LocalDate a = LocalDate.date(y, m, d);
        assertEquals(a.getYear(), Year.isoYear(y));
        assertEquals(a.getMonthOfYear(), MonthOfYear.monthOfYear(m));
        assertEquals(a.getDayOfMonth(), DayOfMonth.dayOfMonth(d));
    }

    @Test(dataProvider="sampleDates")
    public void test_getDOY(int y, int m, int d) {
        Year year = Year.isoYear(y);
        LocalDate a = LocalDate.date(y, m, d);
        int total = 0;
        for (int i = 1; i < m; i++) {
            total += MonthOfYear.monthOfYear(i).lengthInDays(year);
        }
        int doy = total + d;
        assertEquals(a.getDayOfYear(), DayOfYear.dayOfYear(doy));
    }

    //-----------------------------------------------------------------------
    // with()
    //-----------------------------------------------------------------------
    public void test_with() {
        DateAdjustor dateAdjustor = DateAdjustors.lastDayOfMonth();
        assertEquals(TEST_2007_07_15.with(dateAdjustor), dateAdjustor.adjustDate(TEST_2007_07_15));
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_with_null_adjustDate() {
        TEST_2007_07_15.with(new DateAdjustor() {
            public LocalDate adjustDate(LocalDate date) {
                return null;
            }
        });
    }

    //-----------------------------------------------------------------------
    // withYear()
    //-----------------------------------------------------------------------
    public void test_withYear_int_normal() {
        LocalDate t = TEST_2007_07_15.withYear(2008);
        assertEquals(t, LocalDate.date(2008, 7, 15));
    }

    public void test_withYear_int_noChange() {
        LocalDate t = TEST_2007_07_15.withYear(2007);
        assertEquals(t, LocalDate.date(2007, 7, 15));
    }
    
    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withYear_int_invalid() {
        TEST_2007_07_15.withYear(Year.MIN_YEAR - 1);
    }

    public void test_withYear_int_adjustDay() {
        LocalDate t = LocalDate.date(2008, 2, 29).withYear(2007);
        LocalDate expected = LocalDate.date(2007, 2, 28);
        assertEquals(t, expected);
    }

    public void test_withYear_int_DateResolver_normal() {
        LocalDate t = TEST_2007_07_15.withYear(2008, DateResolvers.strict());
        assertEquals(t, LocalDate.date(2008, 7, 15));
    }

    public void test_withYear_int_DateResolver_noChange() {
        LocalDate t = TEST_2007_07_15.withYear(2007, DateResolvers.strict());
        assertEquals(t, LocalDate.date(2007, 7, 15));
    }
    
    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withYear_int_DateResolver_invalid() {
        TEST_2007_07_15.withYear(Year.MIN_YEAR - 1, DateResolvers.nextValid());
    }

    public void test_withYear_int_DateResolver_adjustDay() {
        LocalDate t = LocalDate.date(2008, 2, 29).withYear(2007, DateResolvers.nextValid());
        LocalDate expected = LocalDate.date(2007, 3, 1);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withYear_int_DateResolver_adjustDay_invalid() {
        LocalDate.date(2008, 2, 29).withYear(2007, DateResolvers.strict());
    }

    //-----------------------------------------------------------------------
    // withMonthOfYear()
    //-----------------------------------------------------------------------
    public void test_withMonthOfYear_int_normal() {
        LocalDate t = TEST_2007_07_15.withMonthOfYear(1);
        assertEquals(t, LocalDate.date(2007, 1, 15));
    }

    public void test_withMonthOfYear_int_noChange() {
        LocalDate t = TEST_2007_07_15.withMonthOfYear(7);
        assertEquals(t, LocalDate.date(2007, 7, 15));
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withMonthOfYear_int_invalid() {
        TEST_2007_07_15.withMonthOfYear(13);
    }

    public void test_withMonthOfYear_int_adjustDay() {
        LocalDate t = LocalDate.date(2007, 12, 31).withMonthOfYear(11);
        LocalDate expected = LocalDate.date(2007, 11, 30);
        assertEquals(t, expected);
    }

    public void test_withMonthOfYear_int_DateResolver_normal() {
        LocalDate t = TEST_2007_07_15.withMonthOfYear(1, DateResolvers.strict());
        assertEquals(t, LocalDate.date(2007, 1, 15));
    }

    public void test_withMonthOfYear_int_DateResolver_noChange() {
        LocalDate t = TEST_2007_07_15.withMonthOfYear(7, DateResolvers.strict());
        assertEquals(t, LocalDate.date(2007, 7, 15));
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withMonthOfYear_int_DateResolver_invalid() {
        TEST_2007_07_15.withMonthOfYear(13, DateResolvers.nextValid());
    }

    public void test_withMonthOfYear_int_DateResolver_adjustDay() {
        LocalDate t = LocalDate.date(2007, 12, 31).withMonthOfYear(11, DateResolvers.nextValid());
        LocalDate expected = LocalDate.date(2007, 12, 1);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withMonthOfYear_int_DateResolver_adjustDay_invalid() {
        LocalDate.date(2007, 12, 31).withMonthOfYear(11, DateResolvers.strict());
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
    // plusYears()
    //-----------------------------------------------------------------------
    public void test_plusYears_int_normal() {
        LocalDate t = TEST_2007_07_15.plusYears(1);
        assertEquals(t, LocalDate.date(2008, 7, 15));
    }

    public void test_plusYears_int_noChange() {
        LocalDate t = TEST_2007_07_15.plusYears(0);
        assertEquals(t, LocalDate.date(2007, 7, 15));
    }

    public void test_plusYears_int_negative() {
        LocalDate t = TEST_2007_07_15.plusYears(-1);
        assertEquals(t, LocalDate.date(2006, 7, 15));
    }

    public void test_plusYears_int_adjustDay() {
        LocalDate t = LocalDate.date(2008, 2, 29).plusYears(1);
        LocalDate expected = LocalDate.date(2009, 2, 28);
        assertEquals(t, expected);
    }

    public void test_plusYears_int_invalidTooLarge() {
        try {
            LocalDate.date(Year.MAX_YEAR, 1, 1).plusYears(1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            String actual = Long.toString(((long) Year.MAX_YEAR) + 1);
            assertEquals(ex.getMessage(), "Illegal value for Year field, value " + actual +
                " is not in the range " + MIN_YEAR_STR + " to " + MAX_YEAR_STR);
        }
    }

    public void test_plusYears_int_invalidTooSmall() {
        try {
            LocalDate.date(Year.MIN_YEAR, 1, 1).plusYears(-1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            String actual = Long.toString(((long) Year.MIN_YEAR) - 1);
            assertEquals(ex.getMessage(), "Illegal value for Year field, value " + actual +
                " is not in the range " + MIN_YEAR_STR + " to " + MAX_YEAR_STR);
        }
    }

    public void test_plusYears_int_DateResolver_normal() {
        LocalDate t = TEST_2007_07_15.plusYears(1, DateResolvers.nextValid());
        assertEquals(t, LocalDate.date(2008, 7, 15));
    }

    public void test_plusYears_int_DateResolver_noChange() {
        LocalDate t = TEST_2007_07_15.plusYears(0, DateResolvers.nextValid());
        assertEquals(t, LocalDate.date(2007, 7, 15));
    }

    public void test_plusYears_int_DateResolver_negative() {
        LocalDate t = TEST_2007_07_15.plusYears(-1, DateResolvers.nextValid());
        assertEquals(t, LocalDate.date(2006, 7, 15));
    }

    public void test_plusYears_int_DateResolver_adjustDay() {
        LocalDate t = LocalDate.date(2008, 2, 29).plusYears(1, DateResolvers.nextValid());
        LocalDate expected = LocalDate.date(2009, 3, 1);
        assertEquals(t, expected);
    }

    public void test_plusYears_int_DateResolver_invalidTooLarge() {
        try {
            LocalDate.date(Year.MAX_YEAR, 1, 1).plusYears(1, DateResolvers.nextValid());
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", ((long) Year.MAX_YEAR) + 1, Year.MIN_YEAR, Year.MAX_YEAR).getMessage());
        }
    }

    public void test_plusYears_int_DateResolver_invalidTooSmall() {
        try {
            LocalDate.date(Year.MIN_YEAR, 1, 1).plusYears(-1, DateResolvers.nextValid());
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", ((long) Year.MIN_YEAR) - 1, Year.MIN_YEAR, Year.MAX_YEAR).getMessage());
        }
    }

    //-----------------------------------------------------------------------
    // plusMonths()
    //-----------------------------------------------------------------------
    public void test_plusMonths_int_normal() {
        LocalDate t = TEST_2007_07_15.plusMonths(1);
        assertEquals(t, LocalDate.date(2007, 8, 15));
    }

    public void test_plusMonths_int_noChange() {
        LocalDate t = TEST_2007_07_15.plusMonths(0);
        assertEquals(t, LocalDate.date(2007, 7, 15));
    }

    public void test_plusMonths_int_overYears() {
        LocalDate t = TEST_2007_07_15.plusMonths(25);
        assertEquals(t, LocalDate.date(2009, 8, 15));
    }

    public void test_plusMonths_int_negative() {
        LocalDate t = TEST_2007_07_15.plusMonths(-1);
        assertEquals(t, LocalDate.date(2007, 6, 15));
    }

    public void test_plusMonths_int_negativeAcrossYear() {
        LocalDate t = TEST_2007_07_15.plusMonths(-7);
        assertEquals(t, LocalDate.date(2006, 12, 15));
    }

    public void test_plusMonths_int_negativeOverYears() {
        LocalDate t = TEST_2007_07_15.plusMonths(-31);
        assertEquals(t, LocalDate.date(2004, 12, 15));
    }

    public void test_plusMonths_int_adjustDayFromLeapYear() {
        LocalDate t = LocalDate.date(2008, 2, 29).plusMonths(12);
        LocalDate expected = LocalDate.date(2009, 2, 28);
        assertEquals(t, expected);
    }

    public void test_plusMonths_int_adjustDayFromMonthLength() {
        LocalDate t = LocalDate.date(2007, 3, 31).plusMonths(1);
        LocalDate expected = LocalDate.date(2007, 4, 30);
        assertEquals(t, expected);
    }

    public void test_plusMonths_int_invalidTooLarge() {
        try {
            LocalDate.date(Year.MAX_YEAR, 12, 1).plusMonths(1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", ((long) Year.MAX_YEAR) + 1, Year.MIN_YEAR, Year.MAX_YEAR).getMessage());
        }
    }

    public void test_plusMonths_int_invalidTooSmall() {
        try {
            LocalDate.date(Year.MIN_YEAR, 1, 1).plusMonths(-1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", ((long) Year.MIN_YEAR) - 1, Year.MIN_YEAR, Year.MAX_YEAR).getMessage());
        }
    }

    public void test_plusMonths_int_DateResolver_normal() {
        LocalDate t = TEST_2007_07_15.plusMonths(1, DateResolvers.nextValid());
        assertEquals(t, LocalDate.date(2007, 8, 15));
    }

    public void test_plusMonths_int_DateResolver_noChange() {
        LocalDate t = TEST_2007_07_15.plusMonths(0, DateResolvers.nextValid());
        assertEquals(t, LocalDate.date(2007, 7, 15));
    }

    public void test_plusMonths_int_DateResolver_overYears() {
        LocalDate t = TEST_2007_07_15.plusMonths(25, DateResolvers.nextValid());
        assertEquals(t, LocalDate.date(2009, 8, 15));
    }

    public void test_plusMonths_int_DateResolver_negative() {
        LocalDate t = TEST_2007_07_15.plusMonths(-1, DateResolvers.nextValid());
        assertEquals(t, LocalDate.date(2007, 6, 15));
    }

    public void test_plusMonths_int_DateResolver_negativeAcrossYear() {
        LocalDate t = TEST_2007_07_15.plusMonths(-7, DateResolvers.nextValid());
        assertEquals(t, LocalDate.date(2006, 12, 15));
    }

    public void test_plusMonths_int_DateResolver_negativeOverYears() {
        LocalDate t = TEST_2007_07_15.plusMonths(-31, DateResolvers.nextValid());
        assertEquals(t, LocalDate.date(2004, 12, 15));
    }

    public void test_plusMonths_int_DateResolver_adjustDayFromLeapYear() {
        LocalDate t = LocalDate.date(2008, 2, 29).plusMonths(12, DateResolvers.nextValid());
        LocalDate expected = LocalDate.date(2009, 3, 1);
        assertEquals(t, expected);
    }

    public void test_plusMonths_int_DateResolver_adjustDayFromMonthLength() {
        LocalDate t = LocalDate.date(2007, 3, 31).plusMonths(1, DateResolvers.nextValid());
        LocalDate expected = LocalDate.date(2007, 5, 1);
        assertEquals(t, expected);
    }

    public void test_plusMonths_int_DateResolver_invalidTooLarge() {
        try {
            LocalDate.date(Year.MAX_YEAR, 12, 1).plusMonths(1, DateResolvers.nextValid());
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", ((long) Year.MAX_YEAR) + 1, Year.MIN_YEAR, Year.MAX_YEAR).getMessage());
        }
    }

    public void test_plusMonths_int_DateResolver_invalidTooSmall() {
        try {
            LocalDate.date(Year.MIN_YEAR, 1, 1).plusMonths(-1, DateResolvers.nextValid());
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", ((long) Year.MIN_YEAR) - 1, Year.MIN_YEAR, Year.MAX_YEAR).getMessage());
        }
    }

    //-----------------------------------------------------------------------
    // plusWeeks()
    //-----------------------------------------------------------------------
    @DataProvider(name="samplePlusWeeksSymmetry")
    Object[][] provider_samplePlusWeeksSymmetry() {
        return new Object[][] {
            {LocalDate.date(-1, 1, 1)},
            {LocalDate.date(-1, 2, 28)},
            {LocalDate.date(-1, 3, 1)},
            {LocalDate.date(-1, 12, 31)},
            {LocalDate.date(0, 1, 1)},
            {LocalDate.date(0, 2, 28)},
            {LocalDate.date(0, 2, 29)},
            {LocalDate.date(0, 3, 1)},
            {LocalDate.date(0, 12, 31)},
            {LocalDate.date(2007, 1, 1)},
            {LocalDate.date(2007, 2, 28)},
            {LocalDate.date(2007, 3, 1)},
            {LocalDate.date(2007, 12, 31)},
            {LocalDate.date(2008, 1, 1)},
            {LocalDate.date(2008, 2, 28)},
            {LocalDate.date(2008, 2, 29)},
            {LocalDate.date(2008, 3, 1)},
            {LocalDate.date(2008, 12, 31)},
            {LocalDate.date(2099, 1, 1)},
            {LocalDate.date(2099, 2, 28)},
            {LocalDate.date(2099, 3, 1)},
            {LocalDate.date(2099, 12, 31)},
            {LocalDate.date(2100, 1, 1)},
            {LocalDate.date(2100, 2, 28)},
            {LocalDate.date(2100, 3, 1)},
            {LocalDate.date(2100, 12, 31)},
        };
    }
    
    @Test(dataProvider="samplePlusWeeksSymmetry")
    private void test_plusWeeks_symmetry(LocalDate reference) {
        for (int weeks = 0; weeks < 365 * 8; weeks++) {
            LocalDate t = reference.plusWeeks(weeks).plusWeeks(-weeks);
            assertEquals(t, reference);

            t = reference.plusWeeks(-weeks).plusWeeks(weeks);
            assertEquals(t, reference);
        }
    }

    public void test_plusWeeks_normal() {
        LocalDate t = TEST_2007_07_15.plusWeeks(1);
        assertEquals(t, LocalDate.date(2007, 7, 22));
    }

    public void test_plusWeeks_noChange() {
        LocalDate t = TEST_2007_07_15.plusWeeks(0);
        assertEquals(t, LocalDate.date(2007, 7, 15));
    }

    public void test_plusWeeks_overMonths() {
        LocalDate t = TEST_2007_07_15.plusWeeks(9);
        assertEquals(t, LocalDate.date(2007, 9, 16));
    }

    public void test_plusWeeks_overYears() {
        LocalDate t = LocalDate.date(2006, 7, 16).plusWeeks(52);
        assertEquals(t, TEST_2007_07_15);
    }

    public void test_plusWeeks_overLeapYears() {
        LocalDate t = TEST_2007_07_15.plusYears(-1).plusWeeks(104);
        assertEquals(t, LocalDate.date(2008, 7, 12));
    }

    public void test_plusWeeks_negative() {
        LocalDate t = TEST_2007_07_15.plusWeeks(-1);
        assertEquals(t, LocalDate.date(2007, 7, 8));
    }

    public void test_plusWeeks_negativeAcrossYear() {
        LocalDate t = TEST_2007_07_15.plusWeeks(-28);
        assertEquals(t, LocalDate.date(2006, 12, 31));
    }

    public void test_plusWeeks_negativeOverYears() {
        LocalDate t = TEST_2007_07_15.plusWeeks(-104);
        assertEquals(t, LocalDate.date(2005, 7, 17));
    }

    public void test_plusWeeks_maximum() {
        LocalDate t = LocalDate.date(Year.MAX_YEAR, 12, 24).plusWeeks(1);
        LocalDate expected = LocalDate.date(Year.MAX_YEAR, 12, 31);
        assertEquals(t, expected);
    }

    public void test_plusWeeks_minimum() {
        LocalDate t = LocalDate.date(Year.MIN_YEAR, 1, 8).plusWeeks(-1);
        LocalDate expected = LocalDate.date(Year.MIN_YEAR, 1, 1);
        assertEquals(t, expected);
    }

    public void test_plusWeeks_invalidTooLarge() {
        try {
            LocalDate.date(Year.MAX_YEAR, 12, 25).plusWeeks(1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", Year.MAX_YEAR + 1L, Year.MIN_YEAR, 
                    Year.MAX_YEAR).getMessage());
        }
    }

    public void test_plusWeeks_invalidTooSmall() {
        try {
            LocalDate.date(Year.MIN_YEAR, 1, 7).plusWeeks(-1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", Year.MIN_YEAR - 1L, Year.MIN_YEAR, 
                    Year.MAX_YEAR).getMessage());
        }
    }

    //-----------------------------------------------------------------------
    // plusDays()
    //-----------------------------------------------------------------------
    @DataProvider(name="samplePlusDaysSymmetry")
    Object[][] provider_samplePlusDaysSymmetry() {
        return new Object[][] {
            {LocalDate.date(-1, 1, 1)},
            {LocalDate.date(-1, 2, 28)},
            {LocalDate.date(-1, 3, 1)},
            {LocalDate.date(-1, 12, 31)},
            {LocalDate.date(0, 1, 1)},
            {LocalDate.date(0, 2, 28)},
            {LocalDate.date(0, 2, 29)},
            {LocalDate.date(0, 3, 1)},
            {LocalDate.date(0, 12, 31)},
            {LocalDate.date(2007, 1, 1)},
            {LocalDate.date(2007, 2, 28)},
            {LocalDate.date(2007, 3, 1)},
            {LocalDate.date(2007, 12, 31)},
            {LocalDate.date(2008, 1, 1)},
            {LocalDate.date(2008, 2, 28)},
            {LocalDate.date(2008, 2, 29)},
            {LocalDate.date(2008, 3, 1)},
            {LocalDate.date(2008, 12, 31)},
            {LocalDate.date(2099, 1, 1)},
            {LocalDate.date(2099, 2, 28)},
            {LocalDate.date(2099, 3, 1)},
            {LocalDate.date(2099, 12, 31)},
            {LocalDate.date(2100, 1, 1)},
            {LocalDate.date(2100, 2, 28)},
            {LocalDate.date(2100, 3, 1)},
            {LocalDate.date(2100, 12, 31)},
        };
    }
    
    @Test(dataProvider="samplePlusDaysSymmetry")
    private void test_plusDays_symmetry(LocalDate reference) {
        for (int days = 0; days < 365 * 8; days++) {
            LocalDate t = reference.plusDays(days).plusDays(-days);
            assertEquals(t, reference);

            t = reference.plusDays(-days).plusDays(days);
            assertEquals(t, reference);
        }
    }

    public void test_plusDays_normal() {
        LocalDate t = TEST_2007_07_15.plusDays(1);
        assertEquals(t, LocalDate.date(2007, 7, 16));
    }

    public void test_plusDays_noChange() {
        LocalDate t = TEST_2007_07_15.plusDays(0);
        assertEquals(t, LocalDate.date(2007, 7, 15));
    }

    public void test_plusDays_overMonths() {
        LocalDate t = TEST_2007_07_15.plusDays(62);
        assertEquals(t, LocalDate.date(2007, 9, 15));
    }

    public void test_plusDays_overYears() {
        LocalDate t = LocalDate.date(2006, 7, 14).plusDays(366);
        assertEquals(t, TEST_2007_07_15);
    }

    public void test_plusDays_overLeapYears() {
        LocalDate t = TEST_2007_07_15.plusYears(-1).plusDays(365 + 366);
        assertEquals(t, LocalDate.date(2008, 7, 15));
    }

    public void test_plusDays_negative() {
        LocalDate t = TEST_2007_07_15.plusDays(-1);
        assertEquals(t, LocalDate.date(2007, 7, 14));
    }

    public void test_plusDays_negativeAcrossYear() {
        LocalDate t = TEST_2007_07_15.plusDays(-196);
        assertEquals(t, LocalDate.date(2006, 12, 31));
    }

    public void test_plusDays_negativeOverYears() {
        LocalDate t = TEST_2007_07_15.plusDays(-730);
        assertEquals(t, LocalDate.date(2005, 7, 15));
    }

    public void test_plusDays_maximum() {
        LocalDate t = LocalDate.date(Year.MAX_YEAR, 12, 30).plusDays(1);
        LocalDate expected = LocalDate.date(Year.MAX_YEAR, 12, 31);
        assertEquals(t, expected);
    }

    public void test_plusDays_minimum() {
        LocalDate t = LocalDate.date(Year.MIN_YEAR, 1, 2).plusDays(-1);
        LocalDate expected = LocalDate.date(Year.MIN_YEAR, 1, 1);
        assertEquals(t, expected);
    }

    public void test_plusDays_invalidTooLarge() {
        try {
            LocalDate.date(Year.MAX_YEAR, 12, 31).plusDays(1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", Year.MAX_YEAR + 1L, Year.MIN_YEAR, 
                    Year.MAX_YEAR).getMessage());
        }
    }

    public void test_plusDays_invalidTooSmall() {
        try {
            LocalDate.date(Year.MIN_YEAR, 1, 1).plusDays(-1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", Year.MIN_YEAR - 1L, Year.MIN_YEAR, 
                    Year.MAX_YEAR).getMessage());
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_plusDays_overflowTooLarge() {
        LocalDate.date(Year.MAX_YEAR, 12, 31).plusDays(Long.MAX_VALUE);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_plusDays_overflowTooSmall() {
        LocalDate.date(Year.MIN_YEAR, 1, 1).plusDays(Long.MIN_VALUE);
    }

    //-----------------------------------------------------------------------
    // minusYears()
    //-----------------------------------------------------------------------
    public void test_minusYears_int_normal() {
        LocalDate t = TEST_2007_07_15.minusYears(1);
        assertEquals(t, LocalDate.date(2006, 7, 15));
    }

    public void test_minusYears_int_noChange() {
        LocalDate t = TEST_2007_07_15.minusYears(0);
        assertEquals(t, LocalDate.date(2007, 7, 15));
    }

    public void test_minusYears_int_negative() {
        LocalDate t = TEST_2007_07_15.minusYears(-1);
        assertEquals(t, LocalDate.date(2008, 7, 15));
    }

    public void test_minusYears_int_adjustDay() {
        LocalDate t = LocalDate.date(2008, 2, 29).minusYears(1);
        LocalDate expected = LocalDate.date(2007, 2, 28);
        assertEquals(t, expected);
    }

    public void test_minusYears_int_invalidTooLarge() {
        try {
            LocalDate.date(Year.MAX_YEAR, 1, 1).minusYears(-1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            String actual = Long.toString(((long) Year.MAX_YEAR) + 1);
            assertEquals(ex.getMessage(), "Illegal value for Year field, value " + actual +
                " is not in the range " + MIN_YEAR_STR + " to " + MAX_YEAR_STR);
        }
    }

    public void test_minusYears_int_invalidTooSmall() {
        try {
            LocalDate.date(Year.MIN_YEAR, 1, 1).minusYears(1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            String actual = Long.toString(((long) Year.MIN_YEAR) - 1);
            assertEquals(ex.getMessage(), "Illegal value for Year field, value " + actual +
                " is not in the range " + MIN_YEAR_STR + " to " + MAX_YEAR_STR);
        }
    }

    public void test_minusYears_int_DateResolver_normal() {
        LocalDate t = TEST_2007_07_15.minusYears(1, DateResolvers.nextValid());
        assertEquals(t, LocalDate.date(2006, 7, 15));
    }

    public void test_minusYears_int_DateResolver_noChange() {
        LocalDate t = TEST_2007_07_15.minusYears(0, DateResolvers.nextValid());
        assertEquals(t, LocalDate.date(2007, 7, 15));
    }

    public void test_minusYears_int_DateResolver_negative() {
        LocalDate t = TEST_2007_07_15.minusYears(-1, DateResolvers.nextValid());
        assertEquals(t, LocalDate.date(2008, 7, 15));
    }

    public void test_minusYears_int_DateResolver_adjustDay() {
        LocalDate t = LocalDate.date(2008, 2, 29).minusYears(1, DateResolvers.nextValid());
        LocalDate expected = LocalDate.date(2007, 3, 1);
        assertEquals(t, expected);
    }

    public void test_minusYears_int_DateResolver_invalidTooLarge() {
        try {
            LocalDate.date(Year.MAX_YEAR, 1, 1).minusYears(-1, DateResolvers.nextValid());
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", ((long) Year.MAX_YEAR) + 1, Year.MIN_YEAR, Year.MAX_YEAR).getMessage());
        }
    }

    public void test_minusYears_int_DateResolver_invalidTooSmall() {
        try {
            LocalDate.date(Year.MIN_YEAR, 1, 1).minusYears(1, DateResolvers.nextValid());
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", ((long) Year.MIN_YEAR) - 1, Year.MIN_YEAR, Year.MAX_YEAR).getMessage());
        }
    }

    //-----------------------------------------------------------------------
    // minusMonths()
    //-----------------------------------------------------------------------
    public void test_minusMonths_int_normal() {
        LocalDate t = TEST_2007_07_15.minusMonths(1);
        assertEquals(t, LocalDate.date(2007, 6, 15));
    }

    public void test_minusMonths_int_noChange() {
        LocalDate t = TEST_2007_07_15.minusMonths(0);
        assertEquals(t, LocalDate.date(2007, 7, 15));
    }

    public void test_minusMonths_int_overYears() {
        LocalDate t = TEST_2007_07_15.minusMonths(25);
        assertEquals(t, LocalDate.date(2005, 6, 15));
    }

    public void test_minusMonths_int_negative() {
        LocalDate t = TEST_2007_07_15.minusMonths(-1);
        assertEquals(t, LocalDate.date(2007, 8, 15));
    }

    public void test_minusMonths_int_negativeAcrossYear() {
        LocalDate t = TEST_2007_07_15.minusMonths(-7);
        assertEquals(t, LocalDate.date(2008, 2, 15));
    }

    public void test_minusMonths_int_negativeOverYears() {
        LocalDate t = TEST_2007_07_15.minusMonths(-31);
        assertEquals(t, LocalDate.date(2010, 2, 15));
    }

    public void test_minusMonths_int_adjustDayFromLeapYear() {
        LocalDate t = LocalDate.date(2008, 2, 29).minusMonths(12);
        LocalDate expected = LocalDate.date(2007, 2, 28);
        assertEquals(t, expected);
    }

    public void test_minusMonths_int_adjustDayFromMonthLength() {
        LocalDate t = LocalDate.date(2007, 3, 31).minusMonths(1);
        LocalDate expected = LocalDate.date(2007, 2, 28);
        assertEquals(t, expected);
    }

    public void test_minusMonths_int_invalidTooLarge() {
        try {
            LocalDate.date(Year.MAX_YEAR, 12, 1).minusMonths(-1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", ((long) Year.MAX_YEAR) + 1, Year.MIN_YEAR, Year.MAX_YEAR).getMessage());
        }
    }

    public void test_minusMonths_int_invalidTooSmall() {
        try {
            LocalDate t = LocalDate.date(Year.MIN_YEAR, 1, 1).minusMonths(1);
            fail(t.toString());
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", ((long) Year.MIN_YEAR) - 1, Year.MIN_YEAR, Year.MAX_YEAR).getMessage());
        }
    }

    public void test_minusMonths_int_DateResolver_normal() {
        LocalDate t = TEST_2007_07_15.minusMonths(1, DateResolvers.nextValid());
        assertEquals(t, LocalDate.date(2007, 6, 15));
    }

    public void test_minusMonths_int_DateResolver_noChange() {
        LocalDate t = TEST_2007_07_15.minusMonths(0, DateResolvers.nextValid());
        assertEquals(t, LocalDate.date(2007, 7, 15));
    }

    public void test_minusMonths_int_DateResolver_overYears() {
        LocalDate t = TEST_2007_07_15.minusMonths(25, DateResolvers.nextValid());
        assertEquals(t, LocalDate.date(2005, 6, 15));
    }

    public void test_minusMonths_int_DateResolver_negative() {
        LocalDate t = TEST_2007_07_15.minusMonths(-1, DateResolvers.nextValid());
        assertEquals(t, LocalDate.date(2007, 8, 15));
    }

    public void test_minusMonths_int_DateResolver_negativeAcrossYear() {
        LocalDate t = TEST_2007_07_15.minusMonths(-7, DateResolvers.nextValid());
        assertEquals(t, LocalDate.date(2008, 2, 15));
    }

    public void test_minusMonths_int_DateResolver_negativeOverYears() {
        LocalDate t = TEST_2007_07_15.minusMonths(-31, DateResolvers.nextValid());
        assertEquals(t, LocalDate.date(2010, 2, 15));
    }

    public void test_minusMonths_int_DateResolver_adjustDayFromLeapYear() {
        LocalDate t = LocalDate.date(2008, 2, 29).minusMonths(12, DateResolvers.nextValid());
        LocalDate expected = LocalDate.date(2007, 3, 1);
        assertEquals(t, expected);
    }

    public void test_minusMonths_int_DateResolver_adjustDayFromMonthLength() {
        LocalDate t = LocalDate.date(2007, 3, 31).minusMonths(1, DateResolvers.nextValid());
        LocalDate expected = LocalDate.date(2007, 3, 1);
        assertEquals(t, expected);
    }

    public void test_minusMonths_int_DateResolver_invalidTooLarge() {
        try {
            LocalDate.date(Year.MAX_YEAR, 12, 1).minusMonths(-1, DateResolvers.nextValid());
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", ((long) Year.MAX_YEAR) + 1, Year.MIN_YEAR, Year.MAX_YEAR).getMessage());
        }
    }

    public void test_minusMonths_int_DateResolver_invalidTooSmall() {
        try {
            LocalDate.date(Year.MIN_YEAR, 1, 1).minusMonths(1, DateResolvers.nextValid());
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", ((long) Year.MIN_YEAR) - 1, Year.MIN_YEAR, Year.MAX_YEAR).getMessage());
        }
    }

    //-----------------------------------------------------------------------
    // minusWeeks()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleMinusWeeksSymmetry")
    Object[][] provider_sampleMinusWeeksSymmetry() {
        return new Object[][] {
            {LocalDate.date(-1, 1, 1)},
            {LocalDate.date(-1, 2, 28)},
            {LocalDate.date(-1, 3, 1)},
            {LocalDate.date(-1, 12, 31)},
            {LocalDate.date(0, 1, 1)},
            {LocalDate.date(0, 2, 28)},
            {LocalDate.date(0, 2, 29)},
            {LocalDate.date(0, 3, 1)},
            {LocalDate.date(0, 12, 31)},
            {LocalDate.date(2007, 1, 1)},
            {LocalDate.date(2007, 2, 28)},
            {LocalDate.date(2007, 3, 1)},
            {LocalDate.date(2007, 12, 31)},
            {LocalDate.date(2008, 1, 1)},
            {LocalDate.date(2008, 2, 28)},
            {LocalDate.date(2008, 2, 29)},
            {LocalDate.date(2008, 3, 1)},
            {LocalDate.date(2008, 12, 31)},
            {LocalDate.date(2099, 1, 1)},
            {LocalDate.date(2099, 2, 28)},
            {LocalDate.date(2099, 3, 1)},
            {LocalDate.date(2099, 12, 31)},
            {LocalDate.date(2100, 1, 1)},
            {LocalDate.date(2100, 2, 28)},
            {LocalDate.date(2100, 3, 1)},
            {LocalDate.date(2100, 12, 31)},
        };
    }
    
    @Test(dataProvider="sampleMinusWeeksSymmetry")
    private void test_minusWeeks_symmetry(LocalDate reference) {
        for (int weeks = 0; weeks < 365 * 8; weeks++) {
            LocalDate t = reference.minusWeeks(weeks).minusWeeks(-weeks);
            assertEquals(t, reference);

            t = reference.minusWeeks(-weeks).minusWeeks(weeks);
            assertEquals(t, reference);
        }
    }

    public void test_minusWeeks_normal() {
        LocalDate t = TEST_2007_07_15.minusWeeks(1);
        assertEquals(t, LocalDate.date(2007, 7, 8));
    }

    public void test_minusWeeks_noChange() {
        LocalDate t = TEST_2007_07_15.minusWeeks(0);
        assertEquals(t, LocalDate.date(2007, 7, 15));
    }

    public void test_minusWeeks_overMonths() {
        LocalDate t = TEST_2007_07_15.minusWeeks(9);
        assertEquals(t, LocalDate.date(2007, 5, 13));
    }

    public void test_minusWeeks_overYears() {
        LocalDate t = LocalDate.date(2008, 7, 13).minusWeeks(52);
        assertEquals(t, TEST_2007_07_15);
    }

    public void test_minusWeeks_overLeapYears() {
        LocalDate t = TEST_2007_07_15.minusYears(-1).minusWeeks(104);
        assertEquals(t, LocalDate.date(2006, 7, 18));
    }

    public void test_minusWeeks_negative() {
        LocalDate t = TEST_2007_07_15.minusWeeks(-1);
        assertEquals(t, LocalDate.date(2007, 7, 22));
    }

    public void test_minusWeeks_negativeAcrossYear() {
        LocalDate t = TEST_2007_07_15.minusWeeks(-28);
        assertEquals(t, LocalDate.date(2008, 1, 27));
    }

    public void test_minusWeeks_negativeOverYears() {
        LocalDate t = TEST_2007_07_15.minusWeeks(-104);
        assertEquals(t, LocalDate.date(2009, 7, 12));
    }

    public void test_minusWeeks_maximum() {
        LocalDate t = LocalDate.date(Year.MAX_YEAR, 12, 24).minusWeeks(-1);
        LocalDate expected = LocalDate.date(Year.MAX_YEAR, 12, 31);
        assertEquals(t, expected);
    }

    public void test_minusWeeks_minimum() {
        LocalDate t = LocalDate.date(Year.MIN_YEAR, 1, 8).minusWeeks(1);
        LocalDate expected = LocalDate.date(Year.MIN_YEAR, 1, 1);
        assertEquals(t, expected);
    }

    public void test_minusWeeks_invalidTooLarge() {
        try {
            LocalDate.date(Year.MAX_YEAR, 12, 25).minusWeeks(-1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", Year.MAX_YEAR + 1L, Year.MIN_YEAR, 
                    Year.MAX_YEAR).getMessage());
        }
    }

    public void test_minusWeeks_invalidTooSmall() {
        try {
            LocalDate.date(Year.MIN_YEAR, 1, 7).minusWeeks(1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", Year.MIN_YEAR - 1L, Year.MIN_YEAR, 
                    Year.MAX_YEAR).getMessage());
        }
    }

    //-----------------------------------------------------------------------
    // minusDays()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleMinusDaysSymmetry")
    Object[][] provider_sampleMinusDaysSymmetry() {
        return new Object[][] {
            {LocalDate.date(-1, 1, 1)},
            {LocalDate.date(-1, 2, 28)},
            {LocalDate.date(-1, 3, 1)},
            {LocalDate.date(-1, 12, 31)},
            {LocalDate.date(0, 1, 1)},
            {LocalDate.date(0, 2, 28)},
            {LocalDate.date(0, 2, 29)},
            {LocalDate.date(0, 3, 1)},
            {LocalDate.date(0, 12, 31)},
            {LocalDate.date(2007, 1, 1)},
            {LocalDate.date(2007, 2, 28)},
            {LocalDate.date(2007, 3, 1)},
            {LocalDate.date(2007, 12, 31)},
            {LocalDate.date(2008, 1, 1)},
            {LocalDate.date(2008, 2, 28)},
            {LocalDate.date(2008, 2, 29)},
            {LocalDate.date(2008, 3, 1)},
            {LocalDate.date(2008, 12, 31)},
            {LocalDate.date(2099, 1, 1)},
            {LocalDate.date(2099, 2, 28)},
            {LocalDate.date(2099, 3, 1)},
            {LocalDate.date(2099, 12, 31)},
            {LocalDate.date(2100, 1, 1)},
            {LocalDate.date(2100, 2, 28)},
            {LocalDate.date(2100, 3, 1)},
            {LocalDate.date(2100, 12, 31)},
        };
    }
    
    @Test(dataProvider="sampleMinusDaysSymmetry")
    private void test_minusDays_symmetry(LocalDate reference) {
        for (int days = 0; days < 365 * 8; days++) {
            LocalDate t = reference.minusDays(days).minusDays(-days);
            assertEquals(t, reference);

            t = reference.minusDays(-days).minusDays(days);
            assertEquals(t, reference);
        }
    }

    public void test_minusDays_normal() {
        LocalDate t = TEST_2007_07_15.minusDays(1);
        assertEquals(t, LocalDate.date(2007, 7, 14));
    }

    public void test_minusDays_noChange() {
        LocalDate t = TEST_2007_07_15.minusDays(0);
        assertEquals(t, LocalDate.date(2007, 7, 15));
    }

    public void test_minusDays_overMonths() {
        LocalDate t = TEST_2007_07_15.minusDays(62);
        assertEquals(t, LocalDate.date(2007, 5, 14));
    }

    public void test_minusDays_overYears() {
        LocalDate t = LocalDate.date(2008, 7, 16).minusDays(367);
        assertEquals(t, TEST_2007_07_15);
    }

    public void test_minusDays_overLeapYears() {
        LocalDate t = TEST_2007_07_15.plusYears(2).minusDays(365 + 366);
        assertEquals(t, TEST_2007_07_15);
    }

    public void test_minusDays_negative() {
        LocalDate t = TEST_2007_07_15.minusDays(-1);
        assertEquals(t, LocalDate.date(2007, 7, 16));
    }

    public void test_minusDays_negativeAcrossYear() {
        LocalDate t = TEST_2007_07_15.minusDays(-169);
        assertEquals(t, LocalDate.date(2007, 12, 31));
    }

    public void test_minusDays_negativeOverYears() {
        LocalDate t = TEST_2007_07_15.minusDays(-731);
        assertEquals(t, LocalDate.date(2009, 7, 15));
    }

    public void test_minusDays_maximum() {
        LocalDate t = LocalDate.date(Year.MAX_YEAR, 12, 30).minusDays(-1);
        LocalDate expected = LocalDate.date(Year.MAX_YEAR, 12, 31);
        assertEquals(t, expected);
    }

    public void test_minusDays_minimum() {
        LocalDate t = LocalDate.date(Year.MIN_YEAR, 1, 2).minusDays(1);
        LocalDate expected = LocalDate.date(Year.MIN_YEAR, 1, 1);
        assertEquals(t, expected);
    }

    public void test_minusDays_invalidTooLarge() {
        try {
            LocalDate.date(Year.MAX_YEAR, 12, 31).minusDays(-1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", Year.MAX_YEAR + 1L, Year.MIN_YEAR, 
                    Year.MAX_YEAR).getMessage());
        }
    }

    public void test_minusDays_invalidTooSmall() {
        try {
            LocalDate.date(Year.MIN_YEAR, 1, 1).minusDays(1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getMessage(), new IllegalCalendarFieldValueException("Year", Year.MIN_YEAR - 1L, Year.MIN_YEAR, 
                    Year.MAX_YEAR).getMessage());
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_minusDays_overflowTooLarge() {
        LocalDate.date(Year.MAX_YEAR, 12, 31).minusDays(Long.MIN_VALUE);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_minusDays_overflowTooSmall() {
        LocalDate.date(Year.MIN_YEAR, 1, 1).minusDays(Long.MAX_VALUE);
    }

    //-----------------------------------------------------------------------
    // matches()
    //-----------------------------------------------------------------------
    public void test_matches() {
        assertTrue(TEST_2007_07_15.matches(Era.AD));
        assertFalse(TEST_2007_07_15.matches(Era.BC));
        assertTrue(TEST_2007_07_15.matches(Year.isoYear(2007)));
        assertFalse(TEST_2007_07_15.matches(Year.isoYear(2006)));
        assertTrue(TEST_2007_07_15.matches(QuarterOfYear.Q3));
        assertFalse(TEST_2007_07_15.matches(QuarterOfYear.Q2));
        assertTrue(TEST_2007_07_15.matches(MonthOfYear.JULY));
        assertFalse(TEST_2007_07_15.matches(MonthOfYear.JUNE));
        assertTrue(TEST_2007_07_15.matches(DayOfMonth.dayOfMonth(15)));
        assertFalse(TEST_2007_07_15.matches(DayOfMonth.dayOfMonth(14)));
        assertTrue(TEST_2007_07_15.matches(DayOfWeek.SUNDAY));
        assertFalse(TEST_2007_07_15.matches(DayOfWeek.MONDAY));
    }

    //-----------------------------------------------------------------------
    // toLocalDate()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_toLocalDate(int year, int month, int day) {
        LocalDate t = LocalDate.date(year, month, day);
        assertSame(t.toLocalDate(), t);
    }

    //-----------------------------------------------------------------------
    // toFlexiDateTime()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_toFlexiDateTime(int year, int month, int day) {
        LocalDate t = LocalDate.date(year, month, day);
        assertEquals(t.toFlexiDateTime(), new FlexiDateTime(t, null, null, null));
    }

    //-----------------------------------------------------------------------
    // toMJDays()
    //-----------------------------------------------------------------------
    public void test_toMJDays() {
        LocalDate test = LocalDate.date(0, 1, 1);
        for (int i = -678941; i < 700000; i++) {
            assertEquals(test.toMJDays(), i);
            test = next(test);
        }
        
        test = LocalDate.date(0, 1, 1);
        for (int i = -678941; i > -2000000; i--) {
            assertEquals(test.toMJDays(), i);
            test = previous(test);
        }

        assertEquals(LocalDate.date(1970, 1, 1).toMJDays(), 40587);
        assertEquals(LocalDate.date(-1, 12, 31).toMJDays(), -678942);
    }

    public void test_toMJDays_fromMJDays_simmetry() {
        LocalDate test = LocalDate.date(0, 1, 1);
        for (int i = -678941; i < 700000; i++) {
            assertEquals(LocalDate.fromMJDays(test.toMJDays()), test);
            test = next(test);
        }

        test = LocalDate.date(0, 1, 1);
        for (int i = -678941; i > -2000000; i--) {
            assertEquals(LocalDate.fromMJDays(test.toMJDays()), test);
            test = previous(test);
        }
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_comparisons() {
        doTest_comparisons_LocalDate(
            LocalDate.date(Year.MIN_YEAR, 1, 1),
            LocalDate.date(Year.MIN_YEAR, 12, 31),
            LocalDate.date(-1, 1, 1),
            LocalDate.date(-1, 12, 31),
            LocalDate.date(0, 1, 1),
            LocalDate.date(0, 12, 31),
            LocalDate.date(1, 1, 1),
            LocalDate.date(1, 12, 31),
            LocalDate.date(2006, 1, 1),
            LocalDate.date(2006, 12, 31),
            LocalDate.date(2007, 1, 1),
            LocalDate.date(2007, 12, 31),
            LocalDate.date(2008, 1, 1),
            LocalDate.date(2008, 2, 29),
            LocalDate.date(2008, 12, 31),
            LocalDate.date(Year.MAX_YEAR, 1, 1),
            LocalDate.date(Year.MAX_YEAR, 12, 31)
        );
    }

    void doTest_comparisons_LocalDate(LocalDate... LocalDates) {
        for (int i = 0; i < LocalDates.length; i++) {
            LocalDate a = LocalDates[i];
            for (int j = 0; j < LocalDates.length; j++) {
                LocalDate b = LocalDates[j];
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
        TEST_2007_07_15.compareTo(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isBefore_ObjectNull() {
        TEST_2007_07_15.isBefore(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isAfter_ObjectNull() {
        TEST_2007_07_15.isAfter(null);
    }

    @Test(expectedExceptions=ClassCastException.class)
    @SuppressWarnings("unchecked")
    public void compareToNonLocalDate() {
       Comparable c = TEST_2007_07_15;
       c.compareTo(new Object());
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

    public void test_equals_null_false() {
        assertEquals(TEST_2007_07_15.equals(null), false);
    }

    //-----------------------------------------------------------------------
    // hashCode()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_hashCode(int y, int m, int d) {
        LocalDate a = LocalDate.date(y, m, d);
        assertEquals(a.hashCode(), a.hashCode());
        LocalDate b = LocalDate.date(y, m, d);
        assertEquals(a.hashCode(), b.hashCode());
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleToString")
    Object[][] provider_sampleToString() {
        return new Object[][] {
            {2008, 7, 5, "2008-07-05"},
            {2007, 12, 31, "2007-12-31"},
            {999, 12, 31, "0999-12-31"},
            {-1, 1, 2, "-0001-01-02"},
        };
    }

    @Test(dataProvider="sampleToString")
    public void test_toString(int y, int m, int d, String expected) {
        LocalDate t = LocalDate.date(y, m, d);
        String str = t.toString();
        assertEquals(str, expected);
    }
    
    //-----------------------------------------------------------------------
    // matchesDate()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_matchesDate_true(int y, int m, int d) {
        LocalDate a = LocalDate.date(y, m, d);
        LocalDate b = LocalDate.date(y, m, d);
        assertEquals(a.matchesDate(b), true);
    }
    @Test(dataProvider="sampleDates")
    public void test_matchesDate_false_year_differs(int y, int m, int d) {
        LocalDate a = LocalDate.date(y, m, d);
        LocalDate b = LocalDate.date(y + 1, m, d);
        assertEquals(a.matchesDate(b), false);
    }
    @Test(dataProvider="sampleDates")
    public void test_matchesDate_false_month_differs(int y, int m, int d) {
        LocalDate a = LocalDate.date(y, m, d);
        LocalDate b = LocalDate.date(y, m + 1, d);
        assertEquals(a.matchesDate(b), false);
    }
    @Test(dataProvider="sampleDates")
    public void test_matchesDate_false_day_differs(int y, int m, int d) {
        LocalDate a = LocalDate.date(y, m, d);
        LocalDate b = LocalDate.date(y, m, d + 1);
        assertEquals(a.matchesDate(b), false);
    }

    public void test_matchesDate_itself_true() {
        assertEquals(TEST_2007_07_15.matchesDate(TEST_2007_07_15), true);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_matchesDate_null() {
        TEST_2007_07_15.matchesDate(null);
    }
}
