/*
 * Copyright (c) 2007-2012, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.DayOfWeek.MONDAY;
import static javax.time.DayOfWeek.SUNDAY;
import static javax.time.DayOfWeek.TUESDAY;
import static javax.time.Month.DECEMBER;
import static javax.time.Month.JANUARY;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;

import javax.time.DayOfWeek;
import javax.time.LocalDate;
import javax.time.Month;
import javax.time.calendrical.DateTime.WithAdjuster;

import org.testng.annotations.Test;

/**
 * Test DateTimeAdjusters.
 */
@Test
public class TestDateTimeAdjusters {

    @SuppressWarnings("rawtypes")
    @Test(groups={"implementation"})
    public void test_constructor() throws Exception {
        for (Constructor constructor : DateTimeAdjusters.class.getDeclaredConstructors()) {
            assertTrue(Modifier.isPrivate(constructor.getModifiers()));
            constructor.setAccessible(true);
            constructor.newInstance(Collections.nCopies(constructor.getParameterTypes().length, null).toArray());
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test(expectedExceptions = InvocationTargetException.class, groups={"tck"})
    public void test_forceCoverage() throws Exception {
        Enum en = (Enum) DateTimeAdjusters.lastDayOfYear();
        Class cls = en.getClass();
        Method m = cls.getMethod("valueOf", String.class);
        m.invoke(null, en.name());
        m.invoke(null, "NOTREAL");
    }

    //-----------------------------------------------------------------------
    // firstDayOfMonth()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_firstDayOfMonth_serialization() throws IOException, ClassNotFoundException {
        WithAdjuster firstDayOfMonth = DateTimeAdjusters.firstDayOfMonth();
        assertTrue(firstDayOfMonth instanceof Serializable);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(firstDayOfMonth);
        oos.close();
        
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertSame(ois.readObject(), firstDayOfMonth);
    }

    @Test(groups={"tck"})
    public void factory_firstDayOfMonth() {
        assertNotNull(DateTimeAdjusters.firstDayOfMonth());
    }
    
    @Test(groups={"implementation"})
    public void factory_firstDayOfMonthSame() {
    	assertSame(DateTimeAdjusters.firstDayOfMonth(), DateTimeAdjusters.firstDayOfMonth());
    }

    @Test(groups={"tck"})
    public void test_firstDayOfMonth_nonLeap() {
        for (Month month : Month.values()) {
            for (int i = 1; i <= month.length(false); i++) {
                LocalDate date = date(2007, month, i);
                LocalDate test = (LocalDate) DateTimeAdjusters.firstDayOfMonth().doWithAdjustment(date);
                assertEquals(test.getYear(), 2007);
                assertEquals(test.getMonth(), month);
                assertEquals(test.getDayOfMonth(), 1);
            }
        }
    }

    @Test(groups={"tck"})
    public void test_firstDayOfMonth_leap() {
        for (Month month : Month.values()) {
            for (int i = 1; i <= month.length(true); i++) {
                LocalDate date = date(2008, month, i);
                LocalDate test = (LocalDate) DateTimeAdjusters.firstDayOfMonth().doWithAdjustment(date);
                assertEquals(test.getYear(), 2008);
                assertEquals(test.getMonth(), month);
                assertEquals(test.getDayOfMonth(), 1);
            }
        }
    }

    //-----------------------------------------------------------------------
    // lastDayOfMonth()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_lastDayOfMonth_serialization() throws IOException, ClassNotFoundException {
        WithAdjuster lastDayOfMonth = DateTimeAdjusters.lastDayOfMonth();
        assertTrue(lastDayOfMonth instanceof Serializable);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(lastDayOfMonth);
        oos.close();
        
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertSame(ois.readObject(), lastDayOfMonth);
    }

    @Test(groups={"tck"})
    public void factory_lastDayOfMonth() {
        assertNotNull(DateTimeAdjusters.lastDayOfMonth());
    }
    
    @Test(groups={"implementation"})
    public void factory_lastDayOfMonthSame() {
    	assertSame(DateTimeAdjusters.lastDayOfMonth(), DateTimeAdjusters.lastDayOfMonth());
    }

    @Test(groups={"tck"})
    public void test_lastDayOfMonth_nonLeap() {
        for (Month month : Month.values()) {
            for (int i = 1; i <= month.length(false); i++) {
                LocalDate date = date(2007, month, i);
                LocalDate test = (LocalDate) DateTimeAdjusters.lastDayOfMonth().doWithAdjustment(date);
                assertEquals(test.getYear(), 2007);
                assertEquals(test.getMonth(), month);
                assertEquals(test.getDayOfMonth(), month.length(false));
            }
        }
    }

    @Test(groups={"tck"})
    public void test_lastDayOfMonth_leap() {
        for (Month month : Month.values()) {
            for (int i = 1; i <= month.length(true); i++) {
                LocalDate date = date(2008, month, i);
                LocalDate test = (LocalDate) DateTimeAdjusters.lastDayOfMonth().doWithAdjustment(date);
                assertEquals(test.getYear(), 2008);
                assertEquals(test.getMonth(), month);
                assertEquals(test.getDayOfMonth(), month.length(true));
            }
        }
    }

    //-----------------------------------------------------------------------
    // firstDayOfNextMonth()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_firstDayOfNextMonth_serialization() throws IOException, ClassNotFoundException {
        WithAdjuster firstDayOfMonth = DateTimeAdjusters.firstDayOfNextMonth();
        assertTrue(firstDayOfMonth instanceof Serializable);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(firstDayOfMonth);
        oos.close();
        
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertSame(ois.readObject(), firstDayOfMonth);
    }

    @Test(groups={"tck"})
    public void factory_firstDayOfNextMonth() {
        assertNotNull(DateTimeAdjusters.firstDayOfNextMonth());
    }
    
    @Test(groups={"implementation"})
    public void factory_firstDayOfNextMonthSame() {
    	assertSame(DateTimeAdjusters.firstDayOfNextMonth(), DateTimeAdjusters.firstDayOfNextMonth());
    }

    @Test(groups={"tck"})
    public void test_firstDayOfNextMonth_nonLeap() {
        for (Month month : Month.values()) {
            for (int i = 1; i <= month.length(false); i++) {
                LocalDate date = date(2007, month, i);
                LocalDate test = (LocalDate) DateTimeAdjusters.firstDayOfNextMonth().doWithAdjustment(date);
                assertEquals(test.getYear(), month == DECEMBER ? 2008 : 2007);
                assertEquals(test.getMonth(), month.plus(1));
                assertEquals(test.getDayOfMonth(), 1);
            }
        }
    }

    @Test(groups={"tck"})
    public void test_firstDayOfNextMonth_leap() {
        for (Month month : Month.values()) {
            for (int i = 1; i <= month.length(true); i++) {
                LocalDate date = date(2008, month, i);
                LocalDate test = (LocalDate) DateTimeAdjusters.firstDayOfNextMonth().doWithAdjustment(date);
                assertEquals(test.getYear(), month == DECEMBER ? 2009 : 2008);
                assertEquals(test.getMonth(), month.plus(1));
                assertEquals(test.getDayOfMonth(), 1);
            }
        }
    }

    //-----------------------------------------------------------------------
    // firstDayOfYear()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_firstDayOfYear_serialization() throws IOException, ClassNotFoundException {
        WithAdjuster firstDayOfYear = DateTimeAdjusters.firstDayOfYear();
        assertTrue(firstDayOfYear instanceof Serializable);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(firstDayOfYear);
        oos.close();
        
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertSame(ois.readObject(), firstDayOfYear);
    }

    @Test(groups={"tck"})
    public void factory_firstDayOfYear() {
        assertNotNull(DateTimeAdjusters.firstDayOfYear());
    }
    
    @Test(groups={"implementation"})
    public void factory_firstDayOfYearSame() {
    	assertSame(DateTimeAdjusters.firstDayOfYear(), DateTimeAdjusters.firstDayOfYear());
    }

    @Test(groups={"tck"})
    public void test_firstDayOfYear_nonLeap() {
        for (Month month : Month.values()) {
            for (int i = 1; i <= month.length(false); i++) {
                LocalDate date = date(2007, month, i);
                LocalDate test = (LocalDate) DateTimeAdjusters.firstDayOfYear().doWithAdjustment(date);
                assertEquals(test.getYear(), 2007);
                assertEquals(test.getMonth(), Month.JANUARY);
                assertEquals(test.getDayOfMonth(), 1);
            }
        }
    }

    @Test(groups={"tck"})
    public void test_firstDayOfYear_leap() {
        for (Month month : Month.values()) {
            for (int i = 1; i <= month.length(true); i++) {
                LocalDate date = date(2008, month, i);
                LocalDate test = (LocalDate) DateTimeAdjusters.firstDayOfYear().doWithAdjustment(date);
                assertEquals(test.getYear(), 2008);
                assertEquals(test.getMonth(), Month.JANUARY);
                assertEquals(test.getDayOfMonth(), 1);
            }
        }
    }

    //-----------------------------------------------------------------------
    // lastDayOfYear()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_lastDayOfYear_serialization() throws IOException, ClassNotFoundException {
        WithAdjuster lastDayOfYear = DateTimeAdjusters.lastDayOfYear();
        assertTrue(lastDayOfYear instanceof Serializable);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(lastDayOfYear);
        oos.close();
        
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertSame(ois.readObject(), lastDayOfYear);
    }

    @Test(groups={"tck"})
    public void factory_lastDayOfYear() {
        assertNotNull(DateTimeAdjusters.lastDayOfYear());
    }
    
    @Test(groups={"implementation"})
    public void factory_lastDayOfYearSame() {
    	assertSame(DateTimeAdjusters.lastDayOfYear(), DateTimeAdjusters.lastDayOfYear());
    }

    @Test(groups={"tck"})
    public void test_lastDayOfYear_nonLeap() {
        for (Month month : Month.values()) {
            for (int i = 1; i <= month.length(false); i++) {
                LocalDate date = date(2007, month, i);
                LocalDate test = (LocalDate) DateTimeAdjusters.lastDayOfYear().doWithAdjustment(date);
                assertEquals(test.getYear(), 2007);
                assertEquals(test.getMonth(), Month.DECEMBER);
                assertEquals(test.getDayOfMonth(), 31);
            }
        }
    }

    @Test(groups={"tck"})
    public void test_lastDayOfYear_leap() {
        for (Month month : Month.values()) {
            for (int i = 1; i <= month.length(true); i++) {
                LocalDate date = date(2008, month, i);
                LocalDate test = (LocalDate) DateTimeAdjusters.lastDayOfYear().doWithAdjustment(date);
                assertEquals(test.getYear(), 2008);
                assertEquals(test.getMonth(), Month.DECEMBER);
                assertEquals(test.getDayOfMonth(), 31);
            }
        }
    }

    //-----------------------------------------------------------------------
    // firstDayOfNextYear()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_firstDayOfNextYear_serialization() throws IOException, ClassNotFoundException {
        WithAdjuster firstDayOfMonth = DateTimeAdjusters.firstDayOfNextYear();
        assertTrue(firstDayOfMonth instanceof Serializable);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(firstDayOfMonth);
        oos.close();
        
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertSame(ois.readObject(), firstDayOfMonth);
    }

    @Test(groups={"tck"})
    public void factory_firstDayOfNextYear() {
        assertNotNull(DateTimeAdjusters.firstDayOfNextYear());
    }
    
    @Test(groups={"implementation"})
    public void factory_firstDayOfNextYearSame() {
    	assertSame(DateTimeAdjusters.firstDayOfNextYear(), DateTimeAdjusters.firstDayOfNextYear());
    }

    @Test(groups={"tck"})
    public void test_firstDayOfNextYear_nonLeap() {
        for (Month month : Month.values()) {
            for (int i = 1; i <= month.length(false); i++) {
                LocalDate date = date(2007, month, i);
                LocalDate test = (LocalDate) DateTimeAdjusters.firstDayOfNextYear().doWithAdjustment(date);
                assertEquals(test.getYear(), 2008);
                assertEquals(test.getMonth(), JANUARY);
                assertEquals(test.getDayOfMonth(), 1);
            }
        }
    }

    @Test(groups={"tck"})
    public void test_firstDayOfNextYear_leap() {
        for (Month month : Month.values()) {
            for (int i = 1; i <= month.length(true); i++) {
                LocalDate date = date(2008, month, i);
                LocalDate test = (LocalDate) DateTimeAdjusters.firstDayOfNextYear().doWithAdjustment(date);
                assertEquals(test.getYear(), 2009);
                assertEquals(test.getMonth(), JANUARY);
                assertEquals(test.getDayOfMonth(), 1);
            }
        }
    }

    //-----------------------------------------------------------------------
    // dayOfWeekInMonth()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_dayOfWeekInMonth_serialization() throws IOException, ClassNotFoundException {
        WithAdjuster dayOfWeekInMonth = DateTimeAdjusters.dayOfWeekInMonth(1, SUNDAY);
        assertTrue(dayOfWeekInMonth instanceof Serializable);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(dayOfWeekInMonth);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), dayOfWeekInMonth);
    }

    @Test(groups={"tck"})
    public void factory_dayOfWeekInMonth() {
        assertNotNull(DateTimeAdjusters.dayOfWeekInMonth(1, MONDAY));
        assertEquals(DateTimeAdjusters.dayOfWeekInMonth(1, MONDAY), DateTimeAdjusters.dayOfWeekInMonth(1, MONDAY));
        assertEquals(DateTimeAdjusters.dayOfWeekInMonth(2, MONDAY), DateTimeAdjusters.dayOfWeekInMonth(2, MONDAY));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_dayOfWeekInMonth_nullDayOfWeek() {
        DateTimeAdjusters.dayOfWeekInMonth(1, null);
    }

    @Test(groups={"tck"})
    public void test_dayOfWeekInMonth_equals() {
        final WithAdjuster mondayInFirstWeek = DateTimeAdjusters.dayOfWeekInMonth(1, MONDAY);
        assertFalse(mondayInFirstWeek.equals(null));
        assertFalse(mondayInFirstWeek.equals(new Object()));
        assertFalse(mondayInFirstWeek.equals(DateTimeAdjusters.lastDayOfMonth()));
        assertFalse(mondayInFirstWeek.equals(DateTimeAdjusters.dayOfWeekInMonth(2, MONDAY)));
        assertFalse(mondayInFirstWeek.equals(DateTimeAdjusters.dayOfWeekInMonth(1, TUESDAY)));
        assertTrue(mondayInFirstWeek.equals(mondayInFirstWeek));
        assertTrue(mondayInFirstWeek.equals(DateTimeAdjusters.dayOfWeekInMonth(1, MONDAY)));
    }
    
    @Test(groups={"tck"})
    public void test_dayOfWeekInMonth_hashCode() {
        assertEquals(DateTimeAdjusters.dayOfWeekInMonth(1, MONDAY).hashCode(), DateTimeAdjusters.dayOfWeekInMonth(1, MONDAY).hashCode());
        assertEquals(DateTimeAdjusters.dayOfWeekInMonth(1, TUESDAY).hashCode(), DateTimeAdjusters.dayOfWeekInMonth(1, TUESDAY).hashCode());
        assertEquals(DateTimeAdjusters.dayOfWeekInMonth(2, MONDAY).hashCode(), DateTimeAdjusters.dayOfWeekInMonth(2, MONDAY).hashCode());
    }

    @Test(groups={"tck"})
    public void test_dayOfWeekInMonth_firstToForth() {
        for (Month month : Month.values()) {
            for (int i = 1; i <= month.length(false); i++) {
                LocalDate date = date(2007, month, i);

                for (DayOfWeek dow : DayOfWeek.values()) {
                    for (int ordinal = 1; ordinal <= 4; ordinal++) {
                        LocalDate test = (LocalDate) DateTimeAdjusters.dayOfWeekInMonth(ordinal, dow).doWithAdjustment(date);
                        assertEquals(test.getYear(), 2007);
                        assertEquals(test.getMonth(), month);
                        assertTrue(test.getDayOfMonth() > (ordinal - 1) * 7);
                        assertTrue(test.getDayOfMonth() < ordinal * 7 + 1);
                        assertEquals(test.getDayOfWeek(), dow);
                    }
                }
            }
        }
    }

    @Test(groups={"tck"})
    public void test_dayOfWeekInMonth_fifth() {
        for (Month month : Month.values()) {
            for (int i = 1; i <= month.length(false); i++) {
                LocalDate date = date(2007, month, i);

                for (DayOfWeek dow : DayOfWeek.values()) {
                    LocalDate test = (LocalDate) DateTimeAdjusters.dayOfWeekInMonth(5, dow).doWithAdjustment(date);

                    assertEquals(test.getDayOfWeek(), dow);

                    if (test.getMonth() == month) {
                        assertEquals(test.getYear(), 2007);
                        assertTrue(test.getDayOfMonth() > 28);
                    } else {
                        LocalDate lastForthOcurrence = date(2007, month, 28);
                        int lastForthOcurrenceOrdinal = lastForthOcurrence.getDayOfWeek().ordinal();
                        int lastDayUnadjustedOrdinal = (month.length(false) - 28 + lastForthOcurrenceOrdinal) % 7;

                        if (lastDayUnadjustedOrdinal >= lastForthOcurrenceOrdinal) {
                            assertFalse(dow.ordinal() > lastForthOcurrenceOrdinal && dow.ordinal() < lastDayUnadjustedOrdinal);
                        } else {
                            assertFalse(dow.ordinal() > lastForthOcurrenceOrdinal || dow.ordinal() < lastDayUnadjustedOrdinal, date +
                                    "; " + dow);
                        }

                        assertSame(month.plus(1), test.getMonth());

                        if (test.getYear() != 2007) {
                            assertSame(month, Month.DECEMBER);
                            assertEquals(test.getYear(), 2008);
                        }
                    }
                }
            }
        }
    }

    //-----------------------------------------------------------------------
    // firstInMonth()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_firstInMonth_serialization() throws IOException, ClassNotFoundException {
        WithAdjuster firstInMonth = DateTimeAdjusters.firstInMonth(SUNDAY);
        assertTrue(firstInMonth instanceof Serializable);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(firstInMonth);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), firstInMonth);
    }

    @Test(groups={"tck"})
    public void factory_firstInMonth() {
        assertNotNull(DateTimeAdjusters.firstInMonth(MONDAY));
        assertEquals(DateTimeAdjusters.firstInMonth(MONDAY), DateTimeAdjusters.firstInMonth(MONDAY));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_firstInMonth_nullDayOfWeek() {
        DateTimeAdjusters.firstInMonth(null);
    }

    @Test(groups={"tck"})
    public void test_firstInMonth_equals() {
        final WithAdjuster mondayInFirstWeek = DateTimeAdjusters.firstInMonth(MONDAY);
        assertFalse(mondayInFirstWeek.equals(null));
        assertFalse(mondayInFirstWeek.equals(new Object()));
        assertFalse(mondayInFirstWeek.equals(DateTimeAdjusters.lastDayOfMonth()));
        assertFalse(mondayInFirstWeek.equals(DateTimeAdjusters.firstInMonth(TUESDAY)));
        assertTrue(mondayInFirstWeek.equals(mondayInFirstWeek));
        assertTrue(mondayInFirstWeek.equals(DateTimeAdjusters.firstInMonth(MONDAY)));
    }

    @Test(groups={"tck"})
    public void test_firstInMonth_hashCode() {
        assertEquals(DateTimeAdjusters.firstInMonth(MONDAY).hashCode(), DateTimeAdjusters.firstInMonth(MONDAY).hashCode());
        assertEquals(DateTimeAdjusters.firstInMonth(TUESDAY).hashCode(), DateTimeAdjusters.firstInMonth(TUESDAY).hashCode());
    }

    @Test(groups={"tck"})
    public void test_firstInMonth() {
        for (Month month : Month.values()) {
            for (int i = 1; i <= month.length(false); i++) {
                LocalDate date = date(2007, month, i);

                for (DayOfWeek dow : DayOfWeek.values()) {
                    LocalDate test = (LocalDate) DateTimeAdjusters.firstInMonth(dow).doWithAdjustment(date);
                    assertEquals(test.getYear(), 2007);
                    assertEquals(test.getMonth(), month);
                    assertTrue(test.getDayOfMonth() < 8);
                    assertEquals(test.getDayOfWeek(), dow);
                }
            }
        }
    }

    //-----------------------------------------------------------------------
    // next()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_next_serialization() throws IOException, ClassNotFoundException {
        WithAdjuster next = DateTimeAdjusters.next(SUNDAY);
        assertTrue(next instanceof Serializable);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(next);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), next);
    }

    @Test(groups={"tck"})
    public void factory_next() {
        assertNotNull(DateTimeAdjusters.next(MONDAY));
        assertEquals(DateTimeAdjusters.next(MONDAY), DateTimeAdjusters.next(MONDAY));
    }

    @Test(expectedExceptions = NullPointerException.class, groups={"tck"})
    public void factory_next_nullDayOfWeek() {
        DateTimeAdjusters.next(null);
    }

    @Test(groups={"tck"})
    public void test_next_equals() {
        final WithAdjuster mondayInFirstWeek = DateTimeAdjusters.next(MONDAY);
        assertFalse(mondayInFirstWeek.equals(null));
        assertFalse(mondayInFirstWeek.equals(new Object()));
        assertFalse(mondayInFirstWeek.equals(DateTimeAdjusters.lastDayOfMonth()));
        assertFalse(mondayInFirstWeek.equals(DateTimeAdjusters.next(TUESDAY)));
        assertTrue(mondayInFirstWeek.equals(mondayInFirstWeek));
        assertTrue(mondayInFirstWeek.equals(DateTimeAdjusters.next(MONDAY)));
    }

    @Test(groups={"tck"})
    public void test_next_hashCode() {
        assertEquals(DateTimeAdjusters.next(MONDAY).hashCode(), DateTimeAdjusters.next(MONDAY).hashCode());
        assertEquals(DateTimeAdjusters.next(TUESDAY).hashCode(), DateTimeAdjusters.next(TUESDAY).hashCode());
    }

    @Test(groups={"tck"})
    public void test_next() {
        for (Month month : Month.values()) {
            for (int i = 1; i <= month.length(false); i++) {
                LocalDate date = date(2007, month, i);

                for (DayOfWeek dow : DayOfWeek.values()) {
                    LocalDate test = (LocalDate) DateTimeAdjusters.next(dow).doWithAdjustment(date);

                    assertSame(test.getDayOfWeek(), dow, date + " " + test);

                    if (test.getYear() == 2007) {
                        int dayDiff = test.getDayOfYear() - date.getDayOfYear();
                        assertTrue(dayDiff > 0 && dayDiff < 8);
                    } else {
                        assertSame(month, Month.DECEMBER);
                        assertTrue(date.getDayOfMonth() > 24);
                        assertEquals(test.getYear(), 2008);
                        assertSame(test.getMonth(), Month.JANUARY);
                        assertTrue(test.getDayOfMonth() < 8);
                    }
                }
            }
        }
    }

    //-----------------------------------------------------------------------
    // nextOrCurrent()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_nextOrCurrent_serialization() throws IOException, ClassNotFoundException {
        WithAdjuster nextOrCurrent = DateTimeAdjusters.nextOrCurrent(SUNDAY);
        assertTrue(nextOrCurrent instanceof Serializable);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(nextOrCurrent);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), nextOrCurrent);
    }

    @Test(groups={"tck"})
    public void factory_nextOrCurrent() {
        assertNotNull(DateTimeAdjusters.nextOrCurrent(MONDAY));
        assertEquals(DateTimeAdjusters.nextOrCurrent(MONDAY), DateTimeAdjusters.nextOrCurrent(MONDAY));
    }

    @Test(expectedExceptions = NullPointerException.class, groups={"tck"})
    public void factory_nextOrCurrent_nullDayOfWeek() {
        DateTimeAdjusters.nextOrCurrent(null);
    }

    @Test(groups={"tck"})
    public void test_nextOrCurrent_equals() {
        final WithAdjuster mondayInFirstWeek = DateTimeAdjusters.nextOrCurrent(MONDAY);
        assertFalse(mondayInFirstWeek.equals(null));
        assertFalse(mondayInFirstWeek.equals(new Object()));
        assertFalse(mondayInFirstWeek.equals(DateTimeAdjusters.lastDayOfMonth()));
        assertFalse(mondayInFirstWeek.equals(DateTimeAdjusters.nextOrCurrent(TUESDAY)));
        assertFalse(mondayInFirstWeek.equals(DateTimeAdjusters.next(MONDAY)));
        assertTrue(mondayInFirstWeek.equals(mondayInFirstWeek));
        assertTrue(mondayInFirstWeek.equals(DateTimeAdjusters.nextOrCurrent(MONDAY)));
    }

    @Test(groups={"tck"})
    public void test_nextOrCurrent_hashCode() {
        assertEquals(DateTimeAdjusters.nextOrCurrent(MONDAY).hashCode(), DateTimeAdjusters.nextOrCurrent(MONDAY).hashCode());
        assertEquals(DateTimeAdjusters.nextOrCurrent(TUESDAY).hashCode(), DateTimeAdjusters.nextOrCurrent(TUESDAY).hashCode());
    }

    @Test(groups={"tck"})
    public void test_nextOrCurrent() {
        for (Month month : Month.values()) {
            for (int i = 1; i <= month.length(false); i++) {
                LocalDate date = date(2007, month, i);

                for (DayOfWeek dow : DayOfWeek.values()) {
                    LocalDate test = (LocalDate) DateTimeAdjusters.nextOrCurrent(dow).doWithAdjustment(date);

                    assertSame(test.getDayOfWeek(), dow);

                    if (test.getYear() == 2007) {
                        int dayDiff = test.getDayOfYear() - date.getDayOfYear();
                        assertTrue(dayDiff < 8);
                        assertEquals(date.equals(test), date.getDayOfWeek() == dow);
                    } else {
                        assertFalse(date.getDayOfWeek() == dow);
                        assertSame(month, Month.DECEMBER);
                        assertTrue(date.getDayOfMonth() > 24);
                        assertEquals(test.getYear(), 2008);
                        assertSame(test.getMonth(), Month.JANUARY);
                        assertTrue(test.getDayOfMonth() < 8);
                    }
                }
            }
        }
    }

    //-----------------------------------------------------------------------
    // previous()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_previous_serialization() throws IOException, ClassNotFoundException {
        WithAdjuster previous = DateTimeAdjusters.previous(SUNDAY);
        assertTrue(previous instanceof Serializable);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(previous);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), previous);
    }

    @Test(groups={"tck"})
    public void factory_previous() {
        assertNotNull(DateTimeAdjusters.previous(MONDAY));
        assertEquals(DateTimeAdjusters.previous(MONDAY), DateTimeAdjusters.previous(MONDAY));
    }

    @Test(expectedExceptions = NullPointerException.class, groups={"tck"})
    public void factory_previous_nullDayOfWeek() {
        DateTimeAdjusters.previous(null);
    }

    @Test(groups={"tck"})
    public void test_previous_equals() {
        final WithAdjuster mondayInFirstWeek = DateTimeAdjusters.previous(MONDAY);
        assertFalse(mondayInFirstWeek.equals(null));
        assertFalse(mondayInFirstWeek.equals(new Object()));
        assertFalse(mondayInFirstWeek.equals(DateTimeAdjusters.lastDayOfMonth()));
        assertFalse(mondayInFirstWeek.equals(DateTimeAdjusters.previous(TUESDAY)));
        assertTrue(mondayInFirstWeek.equals(mondayInFirstWeek));
        assertTrue(mondayInFirstWeek.equals(DateTimeAdjusters.previous(MONDAY)));
    }

    @Test(groups={"tck"})
    public void test_previous_hashCode() {
        assertEquals(DateTimeAdjusters.previous(MONDAY).hashCode(), DateTimeAdjusters.previous(MONDAY).hashCode());
        assertEquals(DateTimeAdjusters.previous(TUESDAY).hashCode(), DateTimeAdjusters.previous(TUESDAY).hashCode());
    }

    @Test(groups={"tck"})
    public void test_previous() {
        for (Month month : Month.values()) {
            for (int i = 1; i <= month.length(false); i++) {
                LocalDate date = date(2007, month, i);

                for (DayOfWeek dow : DayOfWeek.values()) {
                    LocalDate test = (LocalDate) DateTimeAdjusters.previous(dow).doWithAdjustment(date);

                    assertSame(test.getDayOfWeek(), dow, date + " " + test);

                    if (test.getYear() == 2007) {
                        int dayDiff = test.getDayOfYear() - date.getDayOfYear();
                        assertTrue(dayDiff < 0 && dayDiff > -8, dayDiff + " " + test);
                    } else {
                        assertSame(month, Month.JANUARY);
                        assertTrue(date.getDayOfMonth() < 8);
                        assertEquals(test.getYear(), 2006);
                        assertSame(test.getMonth(), Month.DECEMBER);
                        assertTrue(test.getDayOfMonth() > 24);
                    }
                }
            }
        }
    }

    //-----------------------------------------------------------------------
    // previousOrCurrent()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_previousOrCurrent_serialization() throws IOException, ClassNotFoundException {
        WithAdjuster previousOrCurrent = DateTimeAdjusters.previousOrCurrent(SUNDAY);
        assertTrue(previousOrCurrent instanceof Serializable);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(previousOrCurrent);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), previousOrCurrent);
    }

    @Test(groups={"tck"})
    public void factory_previousOrCurrent() {
        assertNotNull(DateTimeAdjusters.previousOrCurrent(MONDAY));
        assertEquals(DateTimeAdjusters.previousOrCurrent(MONDAY), DateTimeAdjusters.previousOrCurrent(MONDAY));
    }

    @Test(expectedExceptions = NullPointerException.class, groups={"tck"})
    public void factory_previousOrCurrent_nullDayOfWeek() {
        DateTimeAdjusters.previousOrCurrent(null);
    }

    @Test(groups={"tck"})
    public void test_previousOrCurrent_equals() {
        final WithAdjuster mondayInFirstWeek = DateTimeAdjusters.previousOrCurrent(MONDAY);
        assertFalse(mondayInFirstWeek.equals(null));
        assertFalse(mondayInFirstWeek.equals(new Object()));
        assertFalse(mondayInFirstWeek.equals(DateTimeAdjusters.lastDayOfMonth()));
        assertFalse(mondayInFirstWeek.equals(DateTimeAdjusters.previousOrCurrent(TUESDAY)));
        assertFalse(mondayInFirstWeek.equals(DateTimeAdjusters.previous(MONDAY)));
        assertTrue(mondayInFirstWeek.equals(mondayInFirstWeek));
        assertTrue(mondayInFirstWeek.equals(DateTimeAdjusters.previousOrCurrent(MONDAY)));
    }

    @Test(groups={"tck"})
    public void test_previousOrCurrent_hashCode() {
        assertEquals(DateTimeAdjusters.previousOrCurrent(MONDAY).hashCode(), DateTimeAdjusters.previousOrCurrent(MONDAY).hashCode());
        assertEquals(DateTimeAdjusters.previousOrCurrent(TUESDAY).hashCode(), DateTimeAdjusters.previousOrCurrent(TUESDAY).hashCode());
    }

    @Test(groups={"tck"})
    public void test_previousOrCurrent() {
        for (Month month : Month.values()) {
            for (int i = 1; i <= month.length(false); i++) {
                LocalDate date = date(2007, month, i);

                for (DayOfWeek dow : DayOfWeek.values()) {
                    LocalDate test = (LocalDate) DateTimeAdjusters.previousOrCurrent(dow).doWithAdjustment(date);

                    assertSame(test.getDayOfWeek(), dow);

                    if (test.getYear() == 2007) {
                        int dayDiff = test.getDayOfYear() - date.getDayOfYear();
                        assertTrue(dayDiff <= 0 && dayDiff > -7);
                        assertEquals(date.equals(test), date.getDayOfWeek() == dow);
                    } else {
                        assertFalse(date.getDayOfWeek() == dow);
                        assertSame(month, Month.JANUARY);
                        assertTrue(date.getDayOfMonth() < 7);
                        assertEquals(test.getYear(), 2006);
                        assertSame(test.getMonth(), Month.DECEMBER);
                        assertTrue(test.getDayOfMonth() > 25);
                    }
                }
            }
        }
    }

    private LocalDate date(int year, Month month, int day) {
        return LocalDate.of(year, month, day);
    }

}
