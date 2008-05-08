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
package javax.time.calendar.i18n;

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.time.calendar.Calendrical;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.LocalDate;
import javax.time.calendar.ReadableDate;
import javax.time.calendar.UnsupportedCalendarFieldException;
import javax.time.calendar.field.HourOfDay;
import javax.time.calendar.field.Year;
import javax.time.calendar.format.FlexiDateTime;
import javax.time.i18n.CopticChronology;
import javax.time.i18n.CopticDate;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test CopticDate.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestCopticDate {

    private static final String MIN_YEAR_STR = Integer.toString(Year.MIN_YEAR);
    private static final String MAX_YEAR_STR = Integer.toString(Year.MAX_YEAR);
    private CopticDate TEST_2007_07_15;

    @BeforeMethod
    public void setUp() {
        TEST_2007_07_15 = CopticDate.copticDate(2007, 7, 15);
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(TEST_2007_07_15 instanceof Calendrical);
        assertTrue(TEST_2007_07_15 instanceof Serializable);
        assertTrue(TEST_2007_07_15 instanceof Comparable);
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
        Class<CopticDate> cls = CopticDate.class;
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
        assertEquals(TEST_2007_07_15.getMonthOfYear(), 7);
        assertEquals(TEST_2007_07_15.getDayOfMonth(), 15);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_date_ints_dayTooLow() {
        CopticDate.copticDate(2007, 1, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_date_ints_dayTooHigh() {
        CopticDate.copticDate(2007, 1, 31);
    }


    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_date_ints_monthTooLow() {
        CopticDate.copticDate(2007, 0, 1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_date_ints_monthTooHigh() {
        CopticDate.copticDate(2007, 13, 1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_date_ints_yearTooLow() {
        CopticDate.copticDate(CopticChronology.MAX_YEAR - 1, 1, 1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_date_ints_yearTooHigh() {
        CopticDate.copticDate(CopticChronology.MAX_YEAR + 1, 1, 1);
    }

    //-----------------------------------------------------------------------
    public void factory_date_ReadableDate() {
        assertEquals(TEST_2007_07_15, CopticDate.copticDate(TEST_2007_07_15));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_date_ReadableDate_null() {
        CopticDate.copticDate(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_date_ReadableDate_null_toDate() {
        CopticDate.copticDate(new ReadableDate() {
            public LocalDate toLocalDate() {
                return null;
            }
            public FlexiDateTime toFlexiDateTime() {
                return null;
            }
        });
    }

    //-----------------------------------------------------------------------
    public void test_getChronology() {
        assertSame(CopticChronology.INSTANCE, TEST_2007_07_15.getChronology());
    }

    //-----------------------------------------------------------------------
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

    public void test_get() {
        assertEquals(TEST_2007_07_15.get(CopticChronology.INSTANCE.year()), TEST_2007_07_15.getYear());
        assertEquals(TEST_2007_07_15.get(CopticChronology.INSTANCE.monthOfYear()), TEST_2007_07_15.getMonthOfYear());
        assertEquals(TEST_2007_07_15.get(CopticChronology.INSTANCE.dayOfMonth()), TEST_2007_07_15.getDayOfMonth());
        assertEquals(TEST_2007_07_15.get(CopticChronology.INSTANCE.dayOfYear()), TEST_2007_07_15.getDayOfYear());
        assertEquals(TEST_2007_07_15.get(CopticChronology.INSTANCE.dayOfWeek()), TEST_2007_07_15.getDayOfWeek());
    }

    @Test(expectedExceptions=UnsupportedCalendarFieldException.class)
    public void test_get_unsupported() {
        TEST_2007_07_15.get(HourOfDay.RULE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_get_null() {
        TEST_2007_07_15.get((DateTimeFieldRule) null);
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

//    //-----------------------------------------------------------------------
//    // get*()
//    //-----------------------------------------------------------------------
//    @Test(dataProvider="sampleDates")
//    public void test_getYearMonth(int y, int m, int d) {
//        assertEquals(CopticDate.copticDate(y, m, d).getYearMonth(), YearMonth.yearMonth(y, m));
//    }
//
//    @Test(dataProvider="sampleDates")
//    public void test_getMonthDay(int y, int m, int d) {
//        assertEquals(CopticDate.copticDate(y, m, d).getMonthDay(), MonthDay.monthDay(m, d));
//    }
//
//    @Test(dataProvider="sampleDates")
//    public void test_get(int y, int m, int d) {
//        CopticDate a = CopticDate.copticDate(y, m, d);
//        assertEquals(a.getYear(), Year.isoYear(y));
//        assertEquals(a.getMonthOfYear(), MonthOfYear.monthOfYear(m));
//        assertEquals(a.getDayOfMonth(), DayOfMonth.dayOfMonth(d));
//    }
//
//    @Test(dataProvider="sampleDates")
//    public void test_getDOY(int y, int m, int d) {
//        Year year = Year.isoYear(y);
//        CopticDate a = CopticDate.copticDate(y, m, d);
//        int total = 0;
//        for (int i = 1; i < m; i++) {
//            total += MonthOfYear.monthOfYear(i).lengthInDays(year);
//        }
//        int doy = total + d;
//        assertEquals(a.getDayOfYear(), DayOfYear.dayOfYear(doy));
//    }
//
//    //-----------------------------------------------------------------------
//    // with()
//    //-----------------------------------------------------------------------
//    public void test_with() {
//        DateAdjustor dateAdjustor = DateAdjustors.lastDayOfMonth();
//        assertEquals(TEST_2007_07_15.with(dateAdjustor), dateAdjustor.adjustDate(TEST_2007_07_15));
//    }
//
//    @Test(expectedExceptions=IllegalArgumentException.class)
//    public void test_with_null_adjustDate() {
//        TEST_2007_07_15.with(new DateAdjustor() {
//            public CopticDate adjustDate(CopticDate date) {
//                return null;
//            }
//        });
//    }
//
//    //-----------------------------------------------------------------------
//    // withYear()
//    //-----------------------------------------------------------------------
//    public void test_withYear_int_normal() {
//        CopticDate t = TEST_2007_07_15.withYear(2008);
//        assertEquals(t, CopticDate.copticDate(2008, 7, 15));
//    }
//
//    public void test_withYear_int_noChange() {
//        CopticDate t = TEST_2007_07_15.withYear(2007);
//        assertEquals(t, CopticDate.copticDate(2007, 7, 15));
//    }
//    
//    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
//    public void test_withYear_int_invalid() {
//        TEST_2007_07_15.withYear(Year.MIN_YEAR - 1);
//    }
//
//    public void test_withYear_int_adjustDay() {
//        CopticDate t = CopticDate.copticDate(2008, 2, 29).withYear(2007);
//        CopticDate expected = CopticDate.copticDate(2007, 2, 28);
//        assertEquals(t, expected);
//    }
//
//    public void test_withYear_int_DateResolver_normal() {
//        CopticDate t = TEST_2007_07_15.withYear(2008, DateResolvers.strict());
//        assertEquals(t, CopticDate.copticDate(2008, 7, 15));
//    }
//
//    public void test_withYear_int_DateResolver_noChange() {
//        CopticDate t = TEST_2007_07_15.withYear(2007, DateResolvers.strict());
//        assertEquals(t, CopticDate.copticDate(2007, 7, 15));
//    }
//    
//    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
//    public void test_withYear_int_DateResolver_invalid() {
//        TEST_2007_07_15.withYear(Year.MIN_YEAR - 1, DateResolvers.nextValid());
//    }
//
//    public void test_withYear_int_DateResolver_adjustDay() {
//        CopticDate t = CopticDate.copticDate(2008, 2, 29).withYear(2007, DateResolvers.nextValid());
//        CopticDate expected = CopticDate.copticDate(2007, 3, 1);
//        assertEquals(t, expected);
//    }
//
//    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
//    public void test_withYear_int_DateResolver_adjustDay_invalid() {
//        CopticDate.copticDate(2008, 2, 29).withYear(2007, DateResolvers.strict());
//    }
//
//    //-----------------------------------------------------------------------
//    // withMonthOfYear()
//    //-----------------------------------------------------------------------
//    public void test_withMonthOfYear_int_normal() {
//        CopticDate t = TEST_2007_07_15.withMonthOfYear(1);
//        assertEquals(t, CopticDate.copticDate(2007, 1, 15));
//    }
//
//    public void test_withMonthOfYear_int_noChange() {
//        CopticDate t = TEST_2007_07_15.withMonthOfYear(7);
//        assertEquals(t, CopticDate.copticDate(2007, 7, 15));
//    }
//
//    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
//    public void test_withMonthOfYear_int_invalid() {
//        TEST_2007_07_15.withMonthOfYear(13);
//    }
//
//    public void test_withMonthOfYear_int_adjustDay() {
//        CopticDate t = CopticDate.copticDate(2007, 12, 31).withMonthOfYear(11);
//        CopticDate expected = CopticDate.copticDate(2007, 11, 30);
//        assertEquals(t, expected);
//    }
//
//    public void test_withMonthOfYear_int_DateResolver_normal() {
//        CopticDate t = TEST_2007_07_15.withMonthOfYear(1, DateResolvers.strict());
//        assertEquals(t, CopticDate.copticDate(2007, 1, 15));
//    }
//
//    public void test_withMonthOfYear_int_DateResolver_noChange() {
//        CopticDate t = TEST_2007_07_15.withMonthOfYear(7, DateResolvers.strict());
//        assertEquals(t, CopticDate.copticDate(2007, 7, 15));
//    }
//
//    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
//    public void test_withMonthOfYear_int_DateResolver_invalid() {
//        TEST_2007_07_15.withMonthOfYear(13, DateResolvers.nextValid());
//    }
//
//    public void test_withMonthOfYear_int_DateResolver_adjustDay() {
//        CopticDate t = CopticDate.copticDate(2007, 12, 31).withMonthOfYear(11, DateResolvers.nextValid());
//        CopticDate expected = CopticDate.copticDate(2007, 12, 1);
//        assertEquals(t, expected);
//    }
//
//    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
//    public void test_withMonthOfYear_int_DateResolver_adjustDay_invalid() {
//        CopticDate.copticDate(2007, 12, 31).withMonthOfYear(11, DateResolvers.strict());
//    }
//
//    //-----------------------------------------------------------------------
//    // withDayOfMonth()
//    //-----------------------------------------------------------------------
//    public void test_withDayOfMonth_normal() {
//        CopticDate t = TEST_2007_07_15.withDayOfMonth(1);
//        assertEquals(t, CopticDate.copticDate(2007, 7, 1));
//    }
//
//    public void test_withDayOfMonth_noChange() {
//        CopticDate t = TEST_2007_07_15.withDayOfMonth(15);
//        assertEquals(t, CopticDate.copticDate(2007, 7, 15));
//    }
//
//    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
//    public void test_withDayOfMonth_invalid() {
//        CopticDate.copticDate(2007, 11, 30).withDayOfMonth(31);
//    }
//
//    //-----------------------------------------------------------------------
//    // plusYears()
//    //-----------------------------------------------------------------------
//    public void test_plusYears_int_normal() {
//        CopticDate t = TEST_2007_07_15.plusYears(1);
//        assertEquals(t, CopticDate.copticDate(2008, 7, 15));
//    }
//
//    public void test_plusYears_int_noChange() {
//        CopticDate t = TEST_2007_07_15.plusYears(0);
//        assertEquals(t, CopticDate.copticDate(2007, 7, 15));
//    }
//
//    public void test_plusYears_int_negative() {
//        CopticDate t = TEST_2007_07_15.plusYears(-1);
//        assertEquals(t, CopticDate.copticDate(2006, 7, 15));
//    }
//
//    public void test_plusYears_int_adjustDay() {
//        CopticDate t = CopticDate.copticDate(2008, 2, 29).plusYears(1);
//        CopticDate expected = CopticDate.copticDate(2009, 2, 28);
//        assertEquals(t, expected);
//    }
//
//    public void test_plusYears_int_invalidTooLarge() {
//        try {
//            CopticDate.copticDate(Year.MAX_YEAR, 1, 1).plusYears(1);
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
//            CopticDate.copticDate(Year.MIN_YEAR, 1, 1).plusYears(-1);
//            fail();
//        } catch (IllegalCalendarFieldValueException ex) {
//            String actual = Long.toString(((long) Year.MIN_YEAR) - 1);
//            assertEquals(ex.getMessage(), "Illegal value for Year field, value " + actual +
//                " is not in the range " + MIN_YEAR_STR + " to " + MAX_YEAR_STR);
//        }
//    }
//
//    public void test_plusYears_int_DateResolver_normal() {
//        CopticDate t = TEST_2007_07_15.plusYears(1, DateResolvers.nextValid());
//        assertEquals(t, CopticDate.copticDate(2008, 7, 15));
//    }
//
//    public void test_plusYears_int_DateResolver_noChange() {
//        CopticDate t = TEST_2007_07_15.plusYears(0, DateResolvers.nextValid());
//        assertEquals(t, CopticDate.copticDate(2007, 7, 15));
//    }
//
//    public void test_plusYears_int_DateResolver_negative() {
//        CopticDate t = TEST_2007_07_15.plusYears(-1, DateResolvers.nextValid());
//        assertEquals(t, CopticDate.copticDate(2006, 7, 15));
//    }
//
//    public void test_plusYears_int_DateResolver_adjustDay() {
//        CopticDate t = CopticDate.copticDate(2008, 2, 29).plusYears(1, DateResolvers.nextValid());
//        CopticDate expected = CopticDate.copticDate(2009, 3, 1);
//        assertEquals(t, expected);
//    }
//
//    public void test_plusYears_int_DateResolver_invalidTooLarge() {
//        try {
//            CopticDate.copticDate(Year.MAX_YEAR, 1, 1).plusYears(1, DateResolvers.nextValid());
//            fail();
//        } catch (IllegalCalendarFieldValueException ex) {
//            String actual = Long.toString(((long) Year.MAX_YEAR) + 1);
//            assertEquals(ex.getMessage(), "Illegal value for Year field, value " + actual +
//                " is not in the range " + MIN_YEAR_STR + " to " + MAX_YEAR_STR);
//        }
//    }
//
//    public void test_plusYears_int_DateResolver_invalidTooSmall() {
//        try {
//            CopticDate.copticDate(Year.MIN_YEAR, 1, 1).plusYears(-1, DateResolvers.nextValid());
//            fail();
//        } catch (IllegalCalendarFieldValueException ex) {
//            String actual = Long.toString(((long) Year.MIN_YEAR) - 1);
//            assertEquals(ex.getMessage(), "Illegal value for Year field, value " + actual +
//                " is not in the range " + MIN_YEAR_STR + " to " + MAX_YEAR_STR);
//        }
//    }
//
//    //-----------------------------------------------------------------------
//    // plusMonths()
//    //-----------------------------------------------------------------------
//    public void test_plusMonths_normal() {
//        CopticDate t = TEST_2007_07_15.plusMonths(1);
//        assertEquals(t, CopticDate.copticDate(2007, 8, 15));
//    }
//
//    public void test_plusMonths_noChange() {
//        CopticDate t = TEST_2007_07_15.plusMonths(0);
//        assertEquals(t, CopticDate.copticDate(2007, 7, 15));
//    }
//
//    public void test_plusMonths_negative() {
//        CopticDate t = TEST_2007_07_15.plusMonths(-1);
//        assertEquals(t, CopticDate.copticDate(2007, 6, 15));
//    }
//
//    public void test_plusMonths_negativeAcrossYear() {
//        CopticDate t = TEST_2007_07_15.plusMonths(-7);
//        assertEquals(t, CopticDate.copticDate(2006, 12, 15));
//    }
//
//    public void test_plusMonths_adjustDayFromLeapYear() {
//        CopticDate t = CopticDate.copticDate(2008, 2, 29).plusMonths(12);
//        CopticDate expected = CopticDate.copticDate(2009, 2, 28);
//        assertEquals(t, expected);
//    }
//
//    public void test_plusMonths_adjustDayFromMonthLength() {
//        CopticDate t = CopticDate.copticDate(2007, 3, 31).plusMonths(1);
//        CopticDate expected = CopticDate.copticDate(2007, 4, 30);
//        assertEquals(t, expected);
//    }
//
//    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
//    public void test_plusMonths_invalidTooLarge() {
//        try {
//            CopticDate.copticDate(Year.MAX_YEAR, 12, 1).plusMonths(1);
//            fail();
//        } catch (IllegalCalendarFieldValueException ex) {
//            String actual = Long.toString(((long) Year.MAX_YEAR) + 1);
//            assertEquals(ex.getMessage(), "Illegal value for Year field, value " + actual +
//                " is not in the range " + MIN_YEAR_STR + " to " + MAX_YEAR_STR);
//            throw ex;
//        }
//    }
//
//    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
//    public void test_plusMonths_invalidTooSmall() {
//        try {
//            CopticDate.copticDate(Year.MIN_YEAR, 1, 1).plusMonths(-1);
//            fail();
//        } catch (IllegalCalendarFieldValueException ex) {
//            String actual = Long.toString(((long) Year.MIN_YEAR) - 1);
//            assertEquals(ex.getMessage(), "Illegal value for Year field, value " + actual +
//                " is not in the range " + MIN_YEAR_STR + " to " + MAX_YEAR_STR);
//            throw ex;
//        }
//    }

//    //-----------------------------------------------------------------------
//    // toMJDays()
//    //-----------------------------------------------------------------------
//    public void test_toMJDays() {
//        CopticDate test = CopticDate.copticDate(0, 1, 1);
//        for (int i = -678941; i < 200000; i++) {
//            assertEquals(test.toMJDays(), i);
//            test = test.plusDays(1);
//        }
//        System.out.println(test);
//        
////        test = CopticDate.copticDate(0, 1, 1);   // TODO: Complete testing negative dates
////        for (int i = -678941; i > -1000000; i--) {
////            assertEquals(test.toMJDays(), i);
////            test = test.plusDays(-1);
////        }
////        System.out.println(test);
//        
////        assertEquals(CopticDate.copticDate(0, 1, 1).toMJDays(), 0);
////        assertEquals(CopticDate.copticDate(0, 1, 2).toMJDays(), 1);
////        assertEquals(CopticDate.copticDate(0, 1, 31).toMJDays(), 30);
////        assertEquals(CopticDate.copticDate(0, 2, 1).toMJDays(), 31);
////        assertEquals(CopticDate.copticDate(0, 2, 28).toMJDays(), 58);
////        assertEquals(CopticDate.copticDate(0, 2, 29).toMJDays(), 59);
////        assertEquals(CopticDate.copticDate(0, 3, 1).toMJDays(), 60);
////        assertEquals(CopticDate.copticDate(0, 4, 1).toMJDays(), 91);
////        assertEquals(CopticDate.copticDate(0, 5, 1).toMJDays(), 121);
////        assertEquals(CopticDate.copticDate(0, 6, 1).toMJDays(), 152);
////        assertEquals(CopticDate.copticDate(0, 7, 1).toMJDays(), 182);
////        assertEquals(CopticDate.copticDate(0, 8, 1).toMJDays(), 213);
////        assertEquals(CopticDate.copticDate(0, 9, 1).toMJDays(), 244);
////        assertEquals(CopticDate.copticDate(0, 10, 1).toMJDays(), 274);
////        assertEquals(CopticDate.copticDate(0, 11, 1).toMJDays(), 305);
////        assertEquals(CopticDate.copticDate(0, 12, 1).toMJDays(), 335);
////        assertEquals(CopticDate.copticDate(0, 12, 31).toMJDays(), 365);
////        assertEquals(CopticDate.copticDate(1, 1, 1).toMJDays(), 366);
//        assertEquals(CopticDate.copticDate(1970, 1, 1).toMJDays(), 40587);
//        assertEquals(CopticDate.copticDate(-1, 12, 31).toMJDays(), -678942);
//    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_comparisons() {
        doTest_comparisons_CopticDate(
            CopticDate.copticDate(CopticChronology.MIN_YEAR, 1, 1),
            CopticDate.copticDate(CopticChronology.MIN_YEAR, 12, 31),
            CopticDate.copticDate(-1, 1, 1),
            CopticDate.copticDate(-1, 12, 31),
            CopticDate.copticDate(0, 1, 1),
            CopticDate.copticDate(0, 12, 31),
            CopticDate.copticDate(1, 1, 1),
            CopticDate.copticDate(1, 12, 31),
            CopticDate.copticDate(2006, 1, 1),
            CopticDate.copticDate(2006, 12, 31),
            CopticDate.copticDate(2007, 1, 1),
            CopticDate.copticDate(2007, 12, 31),
            CopticDate.copticDate(2008, 1, 1),
            CopticDate.copticDate(2008, 2, 29),
            CopticDate.copticDate(2008, 12, 31),
            CopticDate.copticDate(CopticChronology.MAX_YEAR, 1, 1),
            CopticDate.copticDate(CopticChronology.MAX_YEAR, 12, 31)
        );
    }

    void doTest_comparisons_CopticDate(CopticDate... CopticDates) {
        for (int i = 0; i < CopticDates.length; i++) {
            CopticDate a = CopticDates[i];
            for (int j = 0; j < CopticDates.length; j++) {
                CopticDate b = CopticDates[j];
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
    public void compareToNonCopticDate() {
       Comparable c = TEST_2007_07_15;
       c.compareTo(new Object());
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_equals_true(int y, int m, int d) {
        CopticDate a = CopticDate.copticDate(y, m, d);
        CopticDate b = CopticDate.copticDate(y, m, d);
        assertEquals(a.equals(b), true);
    }
    @Test(dataProvider="sampleDates")
    public void test_equals_false_year_differs(int y, int m, int d) {
        CopticDate a = CopticDate.copticDate(y, m, d);
        CopticDate b = CopticDate.copticDate(y + 1, m, d);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleDates")
    public void test_equals_false_month_differs(int y, int m, int d) {
        CopticDate a = CopticDate.copticDate(y, m, d);
        CopticDate b = CopticDate.copticDate(y, m + 1, d);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleDates")
    public void test_equals_false_day_differs(int y, int m, int d) {
        CopticDate a = CopticDate.copticDate(y, m, d);
        CopticDate b = CopticDate.copticDate(y, m, d + 1);
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
            {2008, 7, 5, "2008-07-05"},
            {2007, 12, 31, "2007-12-31"},
            {999, 12, 31, "0999-12-31"},
            {-1, 1, 2, "-0001-01-02"},
        };
    }

    @Test(dataProvider="sampleToString")
    public void test_toString(int y, int m, int d, String expected) {
        CopticDate t = CopticDate.copticDate(y, m, d);
        String str = t.toString();
        assertEquals(str, expected);
    }

}
