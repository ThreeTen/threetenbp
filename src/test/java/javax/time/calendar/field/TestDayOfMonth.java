/*
 * Copyright (c) 2008-2009, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendar.field;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.time.calendar.Calendrical;
import javax.time.calendar.DateAdjuster;
import javax.time.calendar.DateMatcher;
import javax.time.calendar.DateProvider;
import javax.time.calendar.DateResolver;
import javax.time.calendar.DateResolvers;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.InvalidCalendarFieldException;
import javax.time.calendar.LocalDate;
import javax.time.calendar.MockDateProviderReturnsNull;
import javax.time.calendar.MockDateResolverReturnsNull;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test DayOfMonth.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestDayOfMonth {

    private static final DateTimeFieldRule<Integer> RULE = ISOChronology.dayOfMonthRule();
    private static final Year YEAR_STANDARD = Year.isoYear(2007);
    private static final Year YEAR_LEAP = Year.isoYear(2008);
    private static final int STANDARD_YEAR_LENGTH = 365;
    private static final int LEAP_YEAR_LENGTH = 366;
    private static final int MAX_LENGTH = 31;

    @BeforeMethod
    public void setUp() {
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Calendrical.class.isAssignableFrom(DayOfMonth.class));
        assertTrue(Serializable.class.isAssignableFrom(DayOfMonth.class));
        assertTrue(Comparable.class.isAssignableFrom(DayOfMonth.class));
        assertTrue(DateAdjuster.class.isAssignableFrom(DayOfMonth.class));
        assertTrue(DateMatcher.class.isAssignableFrom(DayOfMonth.class));
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        DayOfMonth test = DayOfMonth.dayOfMonth(1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), test);
    }

    public void test_immutable() {
        Class<DayOfMonth> cls = DayOfMonth.class;
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
    public void test_rule() {
        assertSame(DayOfMonth.rule(), RULE);
    }

    //-----------------------------------------------------------------------
    public void test_factory_int_singleton() {
        for (int i = 1; i <= MAX_LENGTH; i++) {
            DayOfMonth test = DayOfMonth.dayOfMonth(i);
            assertEquals(test.getValue(), i);
            assertEquals(DayOfMonth.dayOfMonth(i), test);
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_minuteTooLow() {
        DayOfMonth.dayOfMonth(0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_hourTooHigh() {
        DayOfMonth.dayOfMonth(32);
    }

    //-----------------------------------------------------------------------
    public void test_factory_DateProvider_notLeapYear() {
        LocalDate date = LocalDate.date(2007, 1, 1);
        for (int i = 1; i <= 31; i++) {  // Jan
            assertEquals(DayOfMonth.dayOfMonth(date).getValue(), i);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 28; i++) {  // Feb
            assertEquals(DayOfMonth.dayOfMonth(date).getValue(), i);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 31; i++) {  // Mar
            assertEquals(DayOfMonth.dayOfMonth(date).getValue(), i);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 30; i++) {  // Apr
            assertEquals(DayOfMonth.dayOfMonth(date).getValue(), i);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 31; i++) {  // May
            assertEquals(DayOfMonth.dayOfMonth(date).getValue(), i);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 30; i++) {  // Jun
            assertEquals(DayOfMonth.dayOfMonth(date).getValue(), i);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 31; i++) {  // Jul
            assertEquals(DayOfMonth.dayOfMonth(date).getValue(), i);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 31; i++) {  // Aug
            assertEquals(DayOfMonth.dayOfMonth(date).getValue(), i);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 30; i++) {  // Sep
            assertEquals(DayOfMonth.dayOfMonth(date).getValue(), i);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 31; i++) {  // Oct
            assertEquals(DayOfMonth.dayOfMonth(date).getValue(), i);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 30; i++) {  // Nov
            assertEquals(DayOfMonth.dayOfMonth(date).getValue(), i);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 31; i++) {  // Dec
            assertEquals(DayOfMonth.dayOfMonth(date).getValue(), i);
            date = date.plusDays(1);
        }
    }

    public void test_factory_DateProvider_leapYear() {
        LocalDate date = LocalDate.date(2008, 1, 1);
        for (int i = 1; i <= 31; i++) {  // Jan
            assertEquals(DayOfMonth.dayOfMonth(date).getValue(), i);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 29; i++) {  // Feb
            assertEquals(DayOfMonth.dayOfMonth(date).getValue(), i);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 31; i++) {  // Mar
            assertEquals(DayOfMonth.dayOfMonth(date).getValue(), i);
            date = date.plusDays(1);
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_nullDateProvider() {
        DayOfMonth.dayOfMonth((DateProvider) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_badDateProvider() {
        DayOfMonth.dayOfMonth(new MockDateProviderReturnsNull());
    }

    //-----------------------------------------------------------------------
    // adjustDate(LocalDate)
    //-----------------------------------------------------------------------
    public void test_adjustDate() {
        LocalDate base = LocalDate.date(2007, 1, 1);
        LocalDate expected = base;
        for (int i = 1; i <= MAX_LENGTH; i++) {  // Jan
            LocalDate result = DayOfMonth.dayOfMonth(i).adjustDate(base);
            assertEquals(result, expected);
            expected = expected.plusDays(1);
        }
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_adjustDate_april31() {
        LocalDate base = LocalDate.date(2007, 4, 1);
        DayOfMonth test = DayOfMonth.dayOfMonth(31);
        try {
            test.adjustDate(base);
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getRule(), RULE);
            throw ex;
        }
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_adjustDate_february29_notLeapYear() {
        LocalDate base = LocalDate.date(2007, 2, 1);
        DayOfMonth test = DayOfMonth.dayOfMonth(29);
        try {
            test.adjustDate(base);
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getRule(), RULE);
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustDate_nullLocalDate() {
        LocalDate date = null;
        DayOfMonth test = DayOfMonth.dayOfMonth(1);
        test.adjustDate(date);
    }

    //-----------------------------------------------------------------------
    // adjustDate(LocalDate,DateResolver)
    //-----------------------------------------------------------------------
    public void test_adjustDate_strictResolver() {
        LocalDate base = LocalDate.date(2007, 1, 1);
        LocalDate expected = base;
        for (int i = 1; i <= MAX_LENGTH; i++) {  // Jan
            LocalDate result = DayOfMonth.dayOfMonth(i).adjustDate(base, DateResolvers.strict());
            assertEquals(result, expected);
            expected = expected.plusDays(1);
        }
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_adjustDate_strictResolver_april31() {
        LocalDate base = LocalDate.date(2007, 4, 1);
        DayOfMonth test = DayOfMonth.dayOfMonth(31);
        try {
            test.adjustDate(base, DateResolvers.strict());
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getRule(), RULE);
            throw ex;
        }
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_adjustDate_strictResolver_february29_notLeapYear() {
        LocalDate base = LocalDate.date(2007, 2, 1);
        DayOfMonth test = DayOfMonth.dayOfMonth(29);
        try {
            test.adjustDate(base, DateResolvers.strict());
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getRule(), RULE);
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustDate_resolver_nullLocalDate() {
        DayOfMonth test = DayOfMonth.dayOfMonth(1);
        test.adjustDate((LocalDate) null, DateResolvers.strict());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustDate_resolver_nullResolver() {
        LocalDate date = LocalDate.date(2007, 1, 1);
        DayOfMonth test = DayOfMonth.dayOfMonth(1);
        test.adjustDate(date, (DateResolver) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustDate_resolver_badResolver() {
        LocalDate date = LocalDate.date(2007, 2, 1);
        DayOfMonth test = DayOfMonth.dayOfMonth(31);
        test.adjustDate(date, new MockDateResolverReturnsNull());
    }

    //-----------------------------------------------------------------------
    // matchesDate(LocalDate)
    //-----------------------------------------------------------------------
    public void test_matchesDate_notLeapYear() {
        LocalDate work = LocalDate.date(2007, 1, 1);
        for (int i = 1; i <= STANDARD_YEAR_LENGTH; i++) {
            for (int j = 1; j <= MAX_LENGTH; j++) {
                DayOfMonth test = DayOfMonth.dayOfMonth(j);
                assertEquals(test.matchesDate(work), work.getDayOfMonth() == j);
            }
            work = work.plusDays(1);
        }
    }

    public void test_matchesDate_leapYear() {
        LocalDate work = LocalDate.date(2008, 1, 1);
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            for (int j = 1; j <= MAX_LENGTH; j++) {
                DayOfMonth test = DayOfMonth.dayOfMonth(j);
                assertEquals(test.matchesDate(work), work.getDayOfMonth() == j);
            }
            work = work.plusDays(1);
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_matchesDate_nullLocalDate() {
        LocalDate date = null;
        DayOfMonth test = DayOfMonth.dayOfMonth(1);
        test.matchesDate(date);
    }

    //-----------------------------------------------------------------------
    // isValid(Year,MonthOfYear)
    //-----------------------------------------------------------------------
    public void test_isValid_jan() {
        MonthOfYear moy = MonthOfYear.JANUARY;
        for (int i = 1; i <= MAX_LENGTH; i++) {
            DayOfMonth test = DayOfMonth.dayOfMonth(i);
            assertEquals(test.isValid(YEAR_STANDARD, moy), true);
        }
    }

    public void test_isValid_apr() {
        MonthOfYear moy = MonthOfYear.APRIL;
        for (int i = 1; i <= MAX_LENGTH; i++) {
            DayOfMonth test = DayOfMonth.dayOfMonth(i);
            assertEquals(test.isValid(YEAR_STANDARD, moy), i <= 30);
        }
    }

    public void test_isValid_febNotLeapYear() {
        MonthOfYear moy = MonthOfYear.FEBRUARY;
        for (int i = 1; i <= MAX_LENGTH; i++) {
            DayOfMonth test = DayOfMonth.dayOfMonth(i);
            assertEquals(test.isValid(YEAR_STANDARD, moy), i <= 28);
        }
    }

    public void test_isValid_febLeapYear() {
        MonthOfYear moy = MonthOfYear.FEBRUARY;
        for (int i = 1; i <= MAX_LENGTH; i++) {
            DayOfMonth test = DayOfMonth.dayOfMonth(i);
            assertEquals(test.isValid(YEAR_LEAP, moy), i <= 29);
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isValid_nullYear_day1() {
        MonthOfYear moy = MonthOfYear.FEBRUARY;
        DayOfMonth test = DayOfMonth.dayOfMonth(1);
        test.isValid((Year) null, moy);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isValid_nullYear_day31() {
        MonthOfYear moy = MonthOfYear.FEBRUARY;
        DayOfMonth test = DayOfMonth.dayOfMonth(31);
        test.isValid((Year) null, moy);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isValid_nullMonthOfYear_day1() {
        MonthOfYear moy = null;
        DayOfMonth test = DayOfMonth.dayOfMonth(1);
        test.isValid(YEAR_STANDARD, moy);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isValid_nullMonthOfYear_day31() {
        MonthOfYear moy = null;
        DayOfMonth test = DayOfMonth.dayOfMonth(31);
        test.isValid(YEAR_STANDARD, moy);
    }

    //-----------------------------------------------------------------------
    // isValid(int,MonthOfYear)
    //-----------------------------------------------------------------------
    public void test_isValid_intMonthOfYear_jan() {
        MonthOfYear moy = MonthOfYear.JANUARY;
        for (int i = 1; i <= MAX_LENGTH; i++) {
            DayOfMonth test = DayOfMonth.dayOfMonth(i);
            assertEquals(test.isValid(2007, moy), true);
        }
    }

    public void test_isValid_intMonthOfYear_apr() {
        MonthOfYear moy = MonthOfYear.APRIL;
        for (int i = 1; i <= MAX_LENGTH; i++) {
            DayOfMonth test = DayOfMonth.dayOfMonth(i);
            assertEquals(test.isValid(2007, moy), i <= 30);
        }
    }

    public void test_isValid_intMonthOfYear_febNotLeapYear() {
        MonthOfYear moy = MonthOfYear.FEBRUARY;
        for (int i = 1; i <= MAX_LENGTH; i++) {
            DayOfMonth test = DayOfMonth.dayOfMonth(i);
            assertEquals(test.isValid(2007, moy), i <= 28);
        }
    }

    public void test_isValid_intMonthOfYear_febLeapYear() {
        MonthOfYear moy = MonthOfYear.FEBRUARY;
        for (int i = 1; i <= MAX_LENGTH; i++) {
            DayOfMonth test = DayOfMonth.dayOfMonth(i);
            assertEquals(test.isValid(2008, moy), i <= 29);
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_isValid_intMonthOfYear_invalidYear_day1() {
        MonthOfYear moy = MonthOfYear.FEBRUARY;
        DayOfMonth test = DayOfMonth.dayOfMonth(1);
        test.isValid(Integer.MIN_VALUE, moy);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_isValid_intMonthOfYear_invalidYear_day31() {
        MonthOfYear moy = MonthOfYear.FEBRUARY;
        DayOfMonth test = DayOfMonth.dayOfMonth(31);
        test.isValid(Integer.MIN_VALUE, moy);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isValid_intMonthOfYear_nullMonthOfYear_day1() {
        MonthOfYear moy = null;
        DayOfMonth test = DayOfMonth.dayOfMonth(1);
        test.isValid(2007, moy);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isValid_intMonthOfYear_nullMonthOfYear_day31() {
        MonthOfYear moy = null;
        DayOfMonth test = DayOfMonth.dayOfMonth(31);
        test.isValid(2007, moy);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo() {
        for (int i = 1; i <= MAX_LENGTH; i++) {
            DayOfMonth a = DayOfMonth.dayOfMonth(i);
            for (int j = 1; j <= MAX_LENGTH; j++) {
                DayOfMonth b = DayOfMonth.dayOfMonth(j);
                if (i < j) {
                    assertEquals(a.compareTo(b), -1);
                    assertEquals(b.compareTo(a), 1);
                } else if (i > j) {
                    assertEquals(a.compareTo(b), 1);
                    assertEquals(b.compareTo(a), -1);
                } else {
                    assertEquals(a.compareTo(b), 0);
                    assertEquals(b.compareTo(a), 0);
                }
            }
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_compareTo_nullDayOfMonth() {
        DayOfMonth doy = null;
        DayOfMonth test = DayOfMonth.dayOfMonth(1);
        test.compareTo(doy);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    public void test_equals() {
        for (int i = 1; i <= MAX_LENGTH; i++) {
            DayOfMonth a = DayOfMonth.dayOfMonth(i);
            for (int j = 1; j <= MAX_LENGTH; j++) {
                DayOfMonth b = DayOfMonth.dayOfMonth(j);
                assertEquals(a.equals(b), i == j);
                assertEquals(a.hashCode() == b.hashCode(), i == j);
            }
        }
    }

    public void test_equals_nullDayOfMonth() {
        DayOfMonth doy = null;
        DayOfMonth test = DayOfMonth.dayOfMonth(1);
        assertEquals(test.equals(doy), false);
    }

    public void test_equals_incorrectType() {
        DayOfMonth test = DayOfMonth.dayOfMonth(1);
        assertEquals(test.equals("Incorrect type"), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        for (int i = 1; i <= MAX_LENGTH; i++) {
            DayOfMonth a = DayOfMonth.dayOfMonth(i);
            assertEquals(a.toString(), "DayOfMonth=" + i);
        }
    }

}
