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
package javax.time.calendrical;

import static org.testng.Assert.assertEquals;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;

import javax.time.LocalDate;
import javax.time.LocalTime;
import javax.time.MonthOfYear;
import javax.time.calendrical.CalendricalMatcher;
import javax.time.calendrical.CalendricalMatchers;

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

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test(expectedExceptions = InvocationTargetException.class)
    public void test_forceCoverage() throws Exception {
        Enum en = (Enum) CalendricalMatchers.lastDayOfYear();
        Class cls = en.getClass();
        Method m = cls.getMethod("valueOf", String.class);
        m.invoke(null, en.name());
        m.invoke(null, "NOTREAL");
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
        LocalDate date = LocalDate.of(2007, MonthOfYear.JANUARY, 1);
        for (int i = 1; i <= 365; i++) {
            assertEquals(CalendricalMatchers.leapDay().matchesCalendrical(date), false);
            date = date.plusDays(1);
        }
    }

    public void test_leapDay_leap() {
        LocalDate date = LocalDate.of(2008, MonthOfYear.JANUARY, 1);
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
                LocalDate date = LocalDate.of(2007, month, i);
                assertEquals(CalendricalMatchers.lastDayOfMonth().matchesCalendrical(date), lastDayOfMonthValue == i);
            }
        }
    }

    public void test_lastDayOfMonth_leap() {
        for (MonthOfYear month : MonthOfYear.values()) {
            int lastDayOfMonthValue = month.lengthInDays(true);
            for (int i = 1; i <= lastDayOfMonthValue; i++) {
                LocalDate date = LocalDate.of(2008, month, i);
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
                LocalDate date = LocalDate.of(2007, month, i);
                assertEquals(CalendricalMatchers.lastDayOfYear().matchesCalendrical(date), month == MonthOfYear.DECEMBER && i == 31);
            }
        }
    }

    public void test_lastDayOfYear_leap() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(true); i++) {
                LocalDate date = LocalDate.of(2008, month, i);
                assertEquals(CalendricalMatchers.lastDayOfYear().matchesCalendrical(date), month == MonthOfYear.DECEMBER && i == 31);
            }
        }
    }

    public void test_lastDayOfYear_noData() {
        assertEquals(CalendricalMatchers.lastDayOfYear().matchesCalendrical(LocalTime.of(12, 30)), false);
    }

}
