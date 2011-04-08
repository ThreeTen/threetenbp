/*
 * Copyright (c) 2008-2010, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.calendar.DayOfWeek.MONDAY;
import static javax.time.calendar.DayOfWeek.SUNDAY;
import static javax.time.calendar.DayOfWeek.TUESDAY;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collections;

import org.testng.annotations.Test;

/**
 * Test DateMatchers.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestCalendricalMatchers {

    @SuppressWarnings("rawtypes")
    public void test_constructor() throws Exception {
        for (Constructor constructor : CalendricalMatchers.class.getDeclaredConstructors()) {
            assertTrue(Modifier.isPrivate(constructor.getModifiers()));
            constructor.setAccessible(true);
            constructor.newInstance(Collections.nCopies(constructor.getParameterTypes().length, null).toArray());
        }
    }

    //-----------------------------------------------------------------------
    // leapYear()
    //-----------------------------------------------------------------------
    public void test_leapYear_serialization() throws IOException, ClassNotFoundException {
        CalendricalMatcher leapYear = CalendricalMatchers.leapYear();
        assertTrue(leapYear instanceof Serializable);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(leapYear);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertSame(ois.readObject(), leapYear);
    }

    public void factory_leapYear() {
        assertNotNull(CalendricalMatchers.leapYear());
        assertSame(CalendricalMatchers.leapYear(), CalendricalMatchers.leapYear());
    }

    public void test_leapYear() {
        assertEquals(CalendricalMatchers.leapYear().matchesCalendrical(LocalDate.of(1999, 1, 1)), false);
        assertEquals(CalendricalMatchers.leapYear().matchesCalendrical(LocalDate.of(2000, 1, 1)), true);
        assertEquals(CalendricalMatchers.leapYear().matchesCalendrical(LocalDate.of(2001, 1, 1)), false);
        assertEquals(CalendricalMatchers.leapYear().matchesCalendrical(LocalDate.of(2002, 1, 1)), false);
        assertEquals(CalendricalMatchers.leapYear().matchesCalendrical(LocalDate.of(2003, 1, 1)), false);
        assertEquals(CalendricalMatchers.leapYear().matchesCalendrical(LocalDate.of(2004, 1, 1)), true);
        assertEquals(CalendricalMatchers.leapYear().matchesCalendrical(LocalDate.of(2005, 1, 1)), false);
        
        assertEquals(CalendricalMatchers.leapYear().matchesCalendrical(LocalDate.of(1500, 1, 1)), false);
        assertEquals(CalendricalMatchers.leapYear().matchesCalendrical(LocalDate.of(1600, 1, 1)), true);
        assertEquals(CalendricalMatchers.leapYear().matchesCalendrical(LocalDate.of(1700, 1, 1)), false);
        assertEquals(CalendricalMatchers.leapYear().matchesCalendrical(LocalDate.of(1800, 1, 1)), false);
        assertEquals(CalendricalMatchers.leapYear().matchesCalendrical(LocalDate.of(1900, 1, 1)), false);
    }

    public void test_leapYear_noData() {
        assertEquals(CalendricalMatchers.leapYear().matchesCalendrical(LocalTime.of(12, 30)), false);
    }

    //-----------------------------------------------------------------------
    // leapDay()
    //-----------------------------------------------------------------------
    public void test_leapDay_serialization() throws IOException, ClassNotFoundException {
        CalendricalMatcher leapDay = CalendricalMatchers.leapDay();
        assertTrue(leapDay instanceof Serializable);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(leapDay);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertSame(ois.readObject(), leapDay);
    }

    public void factory_leapDay() {
        assertNotNull(CalendricalMatchers.leapDay());
        assertSame(CalendricalMatchers.leapDay(), CalendricalMatchers.leapDay());
    }

    public void test_leapDay_nonLeap() {
        LocalDate date = date(2007, MonthOfYear.JANUARY, 1);
        for (int i = 1; i <= 365; i++) {
            assertEquals(CalendricalMatchers.leapDay().matchesCalendrical(date), false);
            date = date.plusDays(1);
        }
    }

    public void test_leapDay_leap() {
        LocalDate date = date(2008, MonthOfYear.JANUARY, 1);
        for (int i = 1; i <= 366; i++) {
            assertEquals(CalendricalMatchers.leapDay().matchesCalendrical(date), i == 60);
            date = date.plusDays(1);
        }
    }

    public void test_leapDay_noData() {
        assertEquals(CalendricalMatchers.leapDay().matchesCalendrical(LocalTime.of(12, 30)), false);
    }

    //-----------------------------------------------------------------------
    // lastDayOfMonth()
    //-----------------------------------------------------------------------
    public void test_lastDayOfMonth_serialization() throws IOException, ClassNotFoundException {
        CalendricalMatcher lastDayOfMonth = CalendricalMatchers.lastDayOfMonth();
        assertTrue(lastDayOfMonth instanceof Serializable);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(lastDayOfMonth);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertSame(ois.readObject(), lastDayOfMonth);
    }

    public void factory_lastDayOfMonth() {
        assertNotNull(CalendricalMatchers.lastDayOfMonth());
        assertSame(CalendricalMatchers.lastDayOfMonth(), CalendricalMatchers.lastDayOfMonth());
    }

    public void test_lastDayOfMonth_nonLeap() {
        for (MonthOfYear month : MonthOfYear.values()) {
            int lastDayOfMonthValue = month.lengthInDays(false);
            for (int i = 1; i <= lastDayOfMonthValue; i++) {
                LocalDate date = date(2007, month, i);
                assertEquals(CalendricalMatchers.lastDayOfMonth().matchesCalendrical(date), lastDayOfMonthValue == i);
            }
        }
    }

    public void test_lastDayOfMonth_leap() {
        for (MonthOfYear month : MonthOfYear.values()) {
            int lastDayOfMonthValue = month.lengthInDays(true);
            for (int i = 1; i <= lastDayOfMonthValue; i++) {
                LocalDate date = date(2008, month, i);
                assertEquals(CalendricalMatchers.lastDayOfMonth().matchesCalendrical(date), lastDayOfMonthValue == i);
            }
        }
    }

    public void test_lastDayOfMonth_noData() {
        assertEquals(CalendricalMatchers.lastDayOfMonth().matchesCalendrical(LocalTime.of(12, 30)), false);
    }

    //-----------------------------------------------------------------------
    // lastDayOfYear()
    //-----------------------------------------------------------------------
    public void test_lastDayOfYear_serialization() throws IOException, ClassNotFoundException {
        CalendricalMatcher lastDayOfYear = CalendricalMatchers.lastDayOfYear();
        assertTrue(lastDayOfYear instanceof Serializable);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(lastDayOfYear);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertSame(ois.readObject(), lastDayOfYear);
    }

    public void factory_lastDayOfYear() {
        assertNotNull(CalendricalMatchers.lastDayOfYear());
        assertSame(CalendricalMatchers.lastDayOfYear(), CalendricalMatchers.lastDayOfYear());
    }

    public void test_lastDayOfYear_nonLeap() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(false); i++) {
                LocalDate date = date(2007, month, i);
                assertEquals(CalendricalMatchers.lastDayOfYear().matchesCalendrical(date), month == MonthOfYear.DECEMBER && i == 31);
            }
        }
    }

    public void test_lastDayOfYear_leap() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(true); i++) {
                LocalDate date = date(2008, month, i);
                assertEquals(CalendricalMatchers.lastDayOfYear().matchesCalendrical(date), month == MonthOfYear.DECEMBER && i == 31);
            }
        }
    }

    public void test_lastDayOfYear_noData() {
        assertEquals(CalendricalMatchers.lastDayOfYear().matchesCalendrical(LocalTime.of(12, 30)), false);
    }

    //-----------------------------------------------------------------------
    // dayOfWeekInMonth()
    //-----------------------------------------------------------------------
    public void test_dayOfWeekInMonth_serialization() throws IOException, ClassNotFoundException {
        CalendricalMatcher dayOfWeekInMonth = CalendricalMatchers.dayOfWeekInMonth(1, SUNDAY);
        assertTrue(dayOfWeekInMonth instanceof Serializable);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(dayOfWeekInMonth);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), dayOfWeekInMonth);
    }

    public void factory_dayOfWeekInMonth() {
        assertNotNull(CalendricalMatchers.dayOfWeekInMonth(1, MONDAY));
        assertEquals(CalendricalMatchers.dayOfWeekInMonth(1, MONDAY), CalendricalMatchers.dayOfWeekInMonth(1, MONDAY));
        assertEquals(CalendricalMatchers.dayOfWeekInMonth(2, MONDAY), CalendricalMatchers.dayOfWeekInMonth(2, MONDAY));
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void factory_dayOfWeekInMonth_ordinalTooSmall() {
        CalendricalMatchers.dayOfWeekInMonth(0, MONDAY);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void factory_dayOfWeekInMonth_ordinalTooBig() {
        CalendricalMatchers.dayOfWeekInMonth(6, MONDAY);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_dayOfWeekInMonth_nullDayOfWeek() {
        CalendricalMatchers.dayOfWeekInMonth(1, null);
    }

    public void test_dayOfWeekInMonth_equals() {
        final CalendricalMatcher mondayInFirstWeek = CalendricalMatchers.dayOfWeekInMonth(1, MONDAY);
        assertFalse(mondayInFirstWeek.equals(null));
        assertFalse(mondayInFirstWeek.equals(new Object()));
        assertFalse(mondayInFirstWeek.equals(CalendricalMatchers.lastDayOfMonth()));
        assertFalse(mondayInFirstWeek.equals(CalendricalMatchers.dayOfWeekInMonth(2, MONDAY)));
        assertFalse(mondayInFirstWeek.equals(CalendricalMatchers.dayOfWeekInMonth(1, TUESDAY)));
        assertTrue(mondayInFirstWeek.equals(mondayInFirstWeek));
        assertTrue(mondayInFirstWeek.equals(CalendricalMatchers.dayOfWeekInMonth(1, MONDAY)));
    }

    public void test_dayOfWeekInMonth_hashCode() {
        assertEquals(CalendricalMatchers.dayOfWeekInMonth(1, MONDAY).hashCode(), CalendricalMatchers.dayOfWeekInMonth(1, MONDAY).hashCode());
        assertEquals(CalendricalMatchers.dayOfWeekInMonth(1, TUESDAY).hashCode(), CalendricalMatchers.dayOfWeekInMonth(1, TUESDAY).hashCode());
        assertEquals(CalendricalMatchers.dayOfWeekInMonth(2, MONDAY).hashCode(), CalendricalMatchers.dayOfWeekInMonth(2, MONDAY).hashCode());
    }

    public void test_dayOfWeekInMonth() {
        for (MonthOfYear month : MonthOfYear.values()) {
            DayOfWeek firstReference = null;
            int expectedOrdinal = 1;

            for (int i = 1; i <= month.lengthInDays(false); i++) {
                LocalDate date = date(2007, month, i);
                DayOfWeek expectedDOW = date.getDayOfWeek();

                if (firstReference == null) {
                    firstReference = date.getDayOfWeek();
                } else if (expectedDOW == firstReference) {
                    expectedOrdinal++;
                }

                for (DayOfWeek dow : DayOfWeek.values()) {
                    for (int ordinal = 1; ordinal <= 5; ordinal++) {
                        assertEquals(CalendricalMatchers.dayOfWeekInMonth(ordinal, dow).matchesCalendrical(date), ordinal == expectedOrdinal && 
                                dow == expectedDOW);
                    }
                }
            }
        }
    }

    public void test_dayOfWeekInMonth_noData() {
        assertEquals(CalendricalMatchers.dayOfWeekInMonth(1, MONDAY).matchesCalendrical(LocalTime.of(12, 30)), false);
    }

    //-----------------------------------------------------------------------
    // firstInMonth()
    //-----------------------------------------------------------------------
    public void test_firstInMonth_serialization() throws IOException, ClassNotFoundException {
        CalendricalMatcher firstInMonth = CalendricalMatchers.firstInMonth(SUNDAY);
        assertTrue(firstInMonth instanceof Serializable);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(firstInMonth);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), firstInMonth);
    }

    public void factory_firstInMonth() {
        assertNotNull(CalendricalMatchers.firstInMonth(MONDAY));
        assertEquals(CalendricalMatchers.firstInMonth(MONDAY), CalendricalMatchers.firstInMonth(MONDAY));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_firstInMonth_nullDayOfWeek() {
        CalendricalMatchers.firstInMonth(null);
    }

    public void test_firstInMonth_equals() {
        final CalendricalMatcher mondayInFirstWeek = CalendricalMatchers.firstInMonth(MONDAY);
        assertFalse(mondayInFirstWeek.equals(null));
        assertFalse(mondayInFirstWeek.equals(new Object()));
        assertFalse(mondayInFirstWeek.equals(CalendricalMatchers.lastDayOfMonth()));
        assertFalse(mondayInFirstWeek.equals(CalendricalMatchers.firstInMonth(TUESDAY)));
        assertTrue(mondayInFirstWeek.equals(mondayInFirstWeek));
        assertTrue(mondayInFirstWeek.equals(CalendricalMatchers.firstInMonth(MONDAY)));
    }

    public void test_firstInMonth_hashCode() {
        assertEquals(CalendricalMatchers.firstInMonth(MONDAY).hashCode(), CalendricalMatchers.firstInMonth(MONDAY).hashCode());
        assertEquals(CalendricalMatchers.firstInMonth(TUESDAY).hashCode(), CalendricalMatchers.firstInMonth(TUESDAY).hashCode());
    }

    public void test_firstInMonth() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(false); i++) {
                LocalDate date = date(2007, month, i);

                for (DayOfWeek dow : DayOfWeek.values()) {
                    assertEquals(CalendricalMatchers.firstInMonth(dow).matchesCalendrical(date), i < 8 && dow == date.getDayOfWeek());
                }
            }
        }
    }

    public void test_firstInMonth_noData() {
        assertEquals(CalendricalMatchers.firstInMonth(MONDAY).matchesCalendrical(LocalTime.of(12, 30)), false);
    }

    //-----------------------------------------------------------------------
    // weekendDay()
    //-----------------------------------------------------------------------
    public void test_weekendDay_serialization() throws IOException, ClassNotFoundException {
        CalendricalMatcher weekendDay = CalendricalMatchers.weekendDay();
        assertTrue(weekendDay instanceof Serializable);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(weekendDay);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertSame(ois.readObject(), weekendDay);
    }

    public void factory_weekendDay() {
        assertNotNull(CalendricalMatchers.weekendDay());
        assertSame(CalendricalMatchers.weekendDay(), CalendricalMatchers.weekendDay());
    }

    public void test_weekendDay() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(false); i++) {
                LocalDate date = date(2007, month, i);
                DayOfWeek dayOfWeek = date.getDayOfWeek();
                assertEquals(CalendricalMatchers.weekendDay().matchesCalendrical(date),
                        dayOfWeek == DayOfWeek.SATURDAY || 
                        dayOfWeek == DayOfWeek.SUNDAY);
            }
        }
    }

    public void test_weekendDay_noData() {
        assertEquals(CalendricalMatchers.weekendDay().matchesCalendrical(LocalTime.of(12, 30)), false);
    }

    //-----------------------------------------------------------------------
    // nonWeekendDay()
    //-----------------------------------------------------------------------
    public void test_nonWeekendDay_serialization() throws IOException, ClassNotFoundException {
        CalendricalMatcher nonWeekendDay = CalendricalMatchers.nonWeekendDay();
        assertTrue(nonWeekendDay instanceof Serializable);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(nonWeekendDay);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertSame(ois.readObject(), nonWeekendDay);
    }

    public void factory_nonWeekendDay() {
        assertNotNull(CalendricalMatchers.nonWeekendDay());
        assertSame(CalendricalMatchers.nonWeekendDay(), CalendricalMatchers.nonWeekendDay());
    }

    public void test_nonWeekendDay() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(false); i++) {
                LocalDate date = date(2007, month, i);
                DayOfWeek dayOfWeek = date.getDayOfWeek();
                assertEquals(CalendricalMatchers.nonWeekendDay().matchesCalendrical(date),
                        dayOfWeek != DayOfWeek.SATURDAY && 
                        dayOfWeek != DayOfWeek.SUNDAY);
            }
        }
    }

    public void test_nonWeekendDay_noData() {
        assertEquals(CalendricalMatchers.nonWeekendDay().matchesCalendrical(LocalTime.of(12, 30)), false);
    }

    private LocalDate date(int year, MonthOfYear month, int day) {
        return LocalDate.of(year, month, day);
    }

}
