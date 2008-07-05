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
import javax.time.calendar.DateMatcher;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.LocalDate;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test DayOfWeek.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestDayOfWeek {

    @BeforeMethod
    public void setUp() {
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(CalendricalProvider.class.isAssignableFrom(DayOfWeek.class));
        assertTrue(Serializable.class.isAssignableFrom(DayOfWeek.class));
        assertTrue(Comparable.class.isAssignableFrom(DayOfWeek.class));
//        assertTrue(DateAdjustor.class.isAssignableFrom(DayOfWeek.class));
        assertTrue(DateMatcher.class.isAssignableFrom(DayOfWeek.class));
    }

    public void test_immutable() {
        Class<DayOfWeek> cls = DayOfWeek.class;
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
        for (int i = 1; i <= 7; i++) {
            DayOfWeek test = DayOfWeek.dayOfWeek(i);
            assertEquals(test.getValue(), i);
            assertSame(DayOfWeek.dayOfWeek(i), test);
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_valueTooLow() {
        DayOfWeek.dayOfWeek(0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_valueTooHigh() {
        DayOfWeek.dayOfWeek(8);
    }

    //-----------------------------------------------------------------------
    public void test_factory_DateProvider() {
        LocalDate date = LocalDate.date(2007, 1, 1);  // Monday
        for (int i = 0; i <= 1500; i++) {
            DayOfWeek test = DayOfWeek.dayOfWeek(date);
            assertEquals(test.getValue(), (i % 7) + 1);
            date = date.plusDays(1);
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_nullDateProvider() {
        LocalDate date = null;
        DayOfWeek.dayOfWeek(date);
    }

//    //-----------------------------------------------------------------------
//    // adjustDate()
//    //-----------------------------------------------------------------------
//    public void test_adjustDate_fromStartOfYear_notLeapYear() {
//        LocalDate base = LocalDate.date(2007, 1, 1);
//        LocalDate expected = base;
//        for (int i = 1; i <= STANDARD_YEAR_LENGTH; i++) {
//            DayOfWeek test = DayOfWeek.dayOfWeek(i);
//            assertEquals(test.adjustDate(base), expected);
//            expected = expected.plusDays(1);
//        }
//    }
//
//    public void test_adjustDate_fromEndOfYear_notLeapYear() {
//        LocalDate base = LocalDate.date(2007, 12, 31);
//        LocalDate expected = LocalDate.date(2007, 1, 1);
//        for (int i = 1; i <= STANDARD_YEAR_LENGTH; i++) {
//            DayOfWeek test = DayOfWeek.dayOfWeek(i);
//            assertEquals(test.adjustDate(base), expected);
//            expected = expected.plusDays(1);
//        }
//    }
//
//    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
//    public void test_adjustDate_fromStartOfYear_notLeapYear_day366() {
//        LocalDate base = LocalDate.date(2007, 1, 1);
//        DayOfWeek test = DayOfWeek.dayOfWeek(LEAP_YEAR_LENGTH);
//        test.adjustDate(base);
//    }
//
//    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
//    public void test_adjustDate_fromEndOfYear_notLeapYear_day366() {
//        LocalDate base = LocalDate.date(2007, 12, 31);
//        DayOfWeek test = DayOfWeek.dayOfWeek(LEAP_YEAR_LENGTH);
//        test.adjustDate(base);
//    }
//
//    public void test_adjustDate_fromStartOfYear_leapYear() {
//        LocalDate base = LocalDate.date(2008, 1, 1);
//        LocalDate expected = base;
//        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
//            DayOfWeek test = DayOfWeek.dayOfWeek(i);
//            assertEquals(test.adjustDate(base), expected);
//            expected = expected.plusDays(1);
//        }
//    }
//
//    public void test_adjustDate_fromEndOfYear_leapYear() {
//        LocalDate base = LocalDate.date(2008, 12, 31);
//        LocalDate expected = LocalDate.date(2008, 1, 1);
//        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
//            DayOfWeek test = DayOfWeek.dayOfWeek(i);
//            assertEquals(test.adjustDate(base), expected);
//            expected = expected.plusDays(1);
//        }
//    }
//
//    @Test(expectedExceptions=NullPointerException.class)
//    public void test_adjustDate_nullLocalDate() {
//        LocalDate date = null;
//        DayOfWeek test = DayOfWeek.dayOfWeek(1);
//        test.adjustDate(date);
//    }

    //-----------------------------------------------------------------------
    // next()
    //-----------------------------------------------------------------------
    public void test_next() {
        assertEquals(DayOfWeek.MONDAY.next(), DayOfWeek.TUESDAY);
        assertEquals(DayOfWeek.TUESDAY.next(), DayOfWeek.WEDNESDAY);
        assertEquals(DayOfWeek.WEDNESDAY.next(), DayOfWeek.THURSDAY);
        assertEquals(DayOfWeek.THURSDAY.next(), DayOfWeek.FRIDAY);
        assertEquals(DayOfWeek.FRIDAY.next(), DayOfWeek.SATURDAY);
        assertEquals(DayOfWeek.SATURDAY.next(), DayOfWeek.SUNDAY);
        assertEquals(DayOfWeek.SUNDAY.next(), DayOfWeek.MONDAY);
    }

    //-----------------------------------------------------------------------
    // next()
    //-----------------------------------------------------------------------
    public void test_previous() {
        assertEquals(DayOfWeek.MONDAY.previous(), DayOfWeek.SUNDAY);
        assertEquals(DayOfWeek.TUESDAY.previous(), DayOfWeek.MONDAY);
        assertEquals(DayOfWeek.WEDNESDAY.previous(), DayOfWeek.TUESDAY);
        assertEquals(DayOfWeek.THURSDAY.previous(), DayOfWeek.WEDNESDAY);
        assertEquals(DayOfWeek.FRIDAY.previous(), DayOfWeek.THURSDAY);
        assertEquals(DayOfWeek.SATURDAY.previous(), DayOfWeek.FRIDAY);
        assertEquals(DayOfWeek.SUNDAY.previous(), DayOfWeek.SATURDAY);
    }

    //-----------------------------------------------------------------------
    // matchesDate()
    //-----------------------------------------------------------------------
    public void test_matchesDate_monday() {
        assertEquals(DayOfWeek.MONDAY.matchesDate(LocalDate.date(2008, 5, 5)), true);
        assertEquals(DayOfWeek.TUESDAY.matchesDate(LocalDate.date(2008, 5, 6)), true);
        assertEquals(DayOfWeek.WEDNESDAY.matchesDate(LocalDate.date(2008, 5, 7)), true);
        assertEquals(DayOfWeek.THURSDAY.matchesDate(LocalDate.date(2008, 5, 8)), true);
        assertEquals(DayOfWeek.FRIDAY.matchesDate(LocalDate.date(2008, 5, 9)), true);
        assertEquals(DayOfWeek.SATURDAY.matchesDate(LocalDate.date(2008, 5, 10)), true);
        assertEquals(DayOfWeek.SUNDAY.matchesDate(LocalDate.date(2008, 5, 11)), true);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_matchesDate_nullLocalDate() {
        LocalDate date = null;
        DayOfWeek test = DayOfWeek.dayOfWeek(1);
        test.matchesDate(date);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo() {
        for (int i = 1; i <= 7; i++) {
            DayOfWeek a = DayOfWeek.dayOfWeek(i);
            for (int j = 1; j <= 7; j++) {
                DayOfWeek b = DayOfWeek.dayOfWeek(j);
                if (i < j) {
                    assertEquals(a.compareTo(b) < 0, true);
                    assertEquals(b.compareTo(a) > 0, true);
                } else if (i > j) {
                    assertEquals(a.compareTo(b) > 0, true);
                    assertEquals(b.compareTo(a) < 0, true);
                } else {
                    assertEquals(a.compareTo(b), 0);
                    assertEquals(b.compareTo(a), 0);
                }
            }
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_compareTo_nullDayOfWeek() {
        DayOfWeek doy = null;
        DayOfWeek test = DayOfWeek.MONDAY;
        test.compareTo(doy);
    }

    @SuppressWarnings("unchecked")
    @Test(expectedExceptions=ClassCastException.class)
    public void test_compareTo_incorrectType() {
        Comparable test = MonthOfYear.JANUARY;
        test.compareTo("Incorrect type");
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    public void test_equals() {
        for (int i = 1; i <= 7; i++) {
            DayOfWeek a = DayOfWeek.dayOfWeek(i);
            for (int j = 1; j <= 7; j++) {
                DayOfWeek b = DayOfWeek.dayOfWeek(j);
                assertEquals(a.equals(b), i == j);
                assertEquals(a.hashCode() == b.hashCode(), i == j);
            }
        }
    }

    public void test_equals_nullDayOfWeek() {
        DayOfWeek doy = null;
        DayOfWeek test = DayOfWeek.dayOfWeek(1);
        assertEquals(test.equals(doy), false);
    }

    public void test_equals_incorrectType() {
        MonthOfYear test = MonthOfYear.JANUARY;
        assertEquals(test.equals("Incorrect type"), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        assertEquals(DayOfWeek.MONDAY.toString(), "DayOfWeek=MONDAY");
        assertEquals(DayOfWeek.TUESDAY.toString(), "DayOfWeek=TUESDAY");
        assertEquals(DayOfWeek.WEDNESDAY.toString(), "DayOfWeek=WEDNESDAY");
        assertEquals(DayOfWeek.THURSDAY.toString(), "DayOfWeek=THURSDAY");
        assertEquals(DayOfWeek.FRIDAY.toString(), "DayOfWeek=FRIDAY");
        assertEquals(DayOfWeek.SATURDAY.toString(), "DayOfWeek=SATURDAY");
        assertEquals(DayOfWeek.SUNDAY.toString(), "DayOfWeek=SUNDAY");
    }

}
