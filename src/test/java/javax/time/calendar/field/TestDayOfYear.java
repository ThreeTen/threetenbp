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
package javax.time.calendar.field;

import static org.testng.Assert.*;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.time.calendar.CalendricalProvider;
import javax.time.calendar.DateAdjustor;
import javax.time.calendar.DateMatcher;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.LocalDate;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test DayOfYear.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestDayOfYear {

    private static final int STANDARD_YEAR_LENGTH = 365;
    private static final int LEAP_YEAR_LENGTH = 366;

    @BeforeMethod
    public void setUp() {
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(CalendricalProvider.class.isAssignableFrom(DayOfYear.class));
        assertTrue(Serializable.class.isAssignableFrom(DayOfYear.class));
        assertTrue(Comparable.class.isAssignableFrom(DayOfYear.class));
        assertTrue(DateAdjustor.class.isAssignableFrom(DayOfYear.class));
        assertTrue(DateMatcher.class.isAssignableFrom(DayOfYear.class));
    }

    public void test_immutable() {
        Class<DayOfYear> cls = DayOfYear.class;
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
    public void test_factory_int_singleton() {
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            DayOfYear test = DayOfYear.dayOfYear(i);
            assertEquals(test.getValue(), i);
            assertSame(DayOfYear.dayOfYear(i), test);
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_minuteTooLow() {
        DayOfYear.dayOfYear(0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_hourTooHigh() {
        DayOfYear.dayOfYear(367);
    }

    //-----------------------------------------------------------------------
    public void test_factory_DateProvider_notLeapYear() {
        LocalDate date = LocalDate.date(2007, 1, 1);
        for (int i = 1; i <= STANDARD_YEAR_LENGTH; i++) {
            DayOfYear test = DayOfYear.dayOfYear(date);
            assertEquals(test.getValue(), i);
            date = date.plusDays(1);
        }
    }

    public void test_factory_DateProvider_leapYear() {
        LocalDate date = LocalDate.date(2008, 1, 1);
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            DayOfYear test = DayOfYear.dayOfYear(date);
            assertEquals(test.getValue(), i);
            date = date.plusDays(1);
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_nullDateProvider() {
        LocalDate date = null;
        DayOfYear.dayOfYear(date);
    }

    //-----------------------------------------------------------------------
    // adjustDate()
    //-----------------------------------------------------------------------
    public void test_adjustDate_fromStartOfYear_notLeapYear() {
        LocalDate base = LocalDate.date(2007, 1, 1);
        LocalDate expected = base;
        for (int i = 1; i <= STANDARD_YEAR_LENGTH; i++) {
            DayOfYear test = DayOfYear.dayOfYear(i);
            assertEquals(test.adjustDate(base), expected);
            expected = expected.plusDays(1);
        }
    }

    public void test_adjustDate_fromEndOfYear_notLeapYear() {
        LocalDate base = LocalDate.date(2007, 12, 31);
        LocalDate expected = LocalDate.date(2007, 1, 1);
        for (int i = 1; i <= STANDARD_YEAR_LENGTH; i++) {
            DayOfYear test = DayOfYear.dayOfYear(i);
            assertEquals(test.adjustDate(base), expected);
            expected = expected.plusDays(1);
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_adjustDate_fromStartOfYear_notLeapYear_day366() {
        LocalDate base = LocalDate.date(2007, 1, 1);
        DayOfYear test = DayOfYear.dayOfYear(LEAP_YEAR_LENGTH);
        test.adjustDate(base);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_adjustDate_fromEndOfYear_notLeapYear_day366() {
        LocalDate base = LocalDate.date(2007, 12, 31);
        DayOfYear test = DayOfYear.dayOfYear(LEAP_YEAR_LENGTH);
        test.adjustDate(base);
    }

    public void test_adjustDate_fromStartOfYear_leapYear() {
        LocalDate base = LocalDate.date(2008, 1, 1);
        LocalDate expected = base;
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            DayOfYear test = DayOfYear.dayOfYear(i);
            assertEquals(test.adjustDate(base), expected);
            expected = expected.plusDays(1);
        }
    }

    public void test_adjustDate_fromEndOfYear_leapYear() {
        LocalDate base = LocalDate.date(2008, 12, 31);
        LocalDate expected = LocalDate.date(2008, 1, 1);
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            DayOfYear test = DayOfYear.dayOfYear(i);
            assertEquals(test.adjustDate(base), expected);
            expected = expected.plusDays(1);
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustDate_nullLocalDate() {
        LocalDate date = null;
        DayOfYear test = DayOfYear.dayOfYear(1);
        test.adjustDate(date);
    }

    //-----------------------------------------------------------------------
    // matchesDate()
    //-----------------------------------------------------------------------
    public void test_matchesDate_notLeapYear() {
        LocalDate work = LocalDate.date(2007, 1, 1);
        for (int i = 1; i <= STANDARD_YEAR_LENGTH; i++) {
            for (int j = 1; j <= LEAP_YEAR_LENGTH; j++) {
                DayOfYear test = DayOfYear.dayOfYear(j);
                assertEquals(test.matchesDate(work), i == j);
            }
            work = work.plusDays(1);
        }
    }

    public void test_matchesDate_leapYear() {
        LocalDate work = LocalDate.date(2008, 1, 1);
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            for (int j = 1; j <= LEAP_YEAR_LENGTH; j++) {
                DayOfYear test = DayOfYear.dayOfYear(j);
                assertEquals(test.matchesDate(work), i == j);
            }
            work = work.plusDays(1);
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_matchesDate_nullLocalDate() {
        LocalDate date = null;
        DayOfYear test = DayOfYear.dayOfYear(1);
        test.matchesDate(date);
    }

    //-----------------------------------------------------------------------
    // isValid()
    //-----------------------------------------------------------------------
    public void test_isValid_notLeapYear() {
        Year year = Year.isoYear(2007);
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            DayOfYear test = DayOfYear.dayOfYear(i);
            assertEquals(test.isValid(year), i < LEAP_YEAR_LENGTH);
        }
    }

    public void test_isValid_leapYear() {
        Year year = Year.isoYear(2008);
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            DayOfYear test = DayOfYear.dayOfYear(i);
            assertEquals(test.isValid(year), true);
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isValid_nullYear_day1() {
        Year year = null;
        DayOfYear test = DayOfYear.dayOfYear(1);
        test.isValid(year);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isValid_nullYear_day366() {
        Year year = null;
        DayOfYear test = DayOfYear.dayOfYear(LEAP_YEAR_LENGTH);
        test.isValid(year);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo() {
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            DayOfYear a = DayOfYear.dayOfYear(i);
            for (int j = 1; j <= LEAP_YEAR_LENGTH; j++) {
                DayOfYear b = DayOfYear.dayOfYear(j);
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
    public void test_compareTo_nullDayOfYear() {
        DayOfYear doy = null;
        DayOfYear test = DayOfYear.dayOfYear(1);
        test.compareTo(doy);
    }

    @SuppressWarnings("unchecked")
    @Test(expectedExceptions=ClassCastException.class)
    public void test_compareTo_incorrectType() {
        Comparable test = DayOfYear.dayOfYear(1);
        test.compareTo("Incorrect type");
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    public void test_equals() {
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            DayOfYear a = DayOfYear.dayOfYear(i);
            for (int j = 1; j <= LEAP_YEAR_LENGTH; j++) {
                DayOfYear b = DayOfYear.dayOfYear(j);
                assertEquals(a.equals(b), i == j);
                assertEquals(a.hashCode() == b.hashCode(), i == j);
            }
        }
    }

    public void test_equals_nullDayOfYear() {
        DayOfYear doy = null;
        DayOfYear test = DayOfYear.dayOfYear(1);
        assertEquals(test.equals(doy), false);
    }

    public void test_equals_incorrectType() {
        DayOfYear test = DayOfYear.dayOfYear(1);
        assertEquals(test.equals("Incorrect type"), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            DayOfYear a = DayOfYear.dayOfYear(i);
            assertEquals(a.toString(), "DayOfYear=" + i);
        }
    }

}
