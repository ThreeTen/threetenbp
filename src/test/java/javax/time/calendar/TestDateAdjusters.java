/*
 * Copyright (c) 2007-2011, Stephen Colebourne & Michael Nascimento Santos
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
import static javax.time.calendar.MonthOfYear.DECEMBER;
import static javax.time.calendar.MonthOfYear.JANUARY;
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

import org.testng.annotations.Test;

/**
 * Test DateAdjusters.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestDateAdjusters {

    @SuppressWarnings("rawtypes")
    @Test(groups={"implementation"})
    public void test_constructor() throws Exception {
        for (Constructor constructor : DateAdjusters.class.getDeclaredConstructors()) {
            assertTrue(Modifier.isPrivate(constructor.getModifiers()));
            constructor.setAccessible(true);
            constructor.newInstance(Collections.nCopies(constructor.getParameterTypes().length, null).toArray());
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test(expectedExceptions = InvocationTargetException.class, groups={"tck"})
    public void test_forceCoverage() throws Exception {
        Enum en = (Enum) DateAdjusters.lastDayOfYear();
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
        DateAdjuster firstDayOfMonth = DateAdjusters.firstDayOfMonth();
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
        assertNotNull(DateAdjusters.firstDayOfMonth());
    }
    
    @Test(groups={"implementation"})
    public void factory_firstDayOfMonthSame() {
    	assertSame(DateAdjusters.firstDayOfMonth(), DateAdjusters.firstDayOfMonth());
    }

    @Test(groups={"tck"})
    public void test_firstDayOfMonth_nonLeap() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(false); i++) {
                LocalDate date = date(2007, month, i);
                LocalDate test = DateAdjusters.firstDayOfMonth().adjustDate(date);
                assertEquals(test.getYear(), 2007);
                assertEquals(test.getMonthOfYear(), month);
                assertEquals(test.getDayOfMonth(), 1);
            }
        }
    }

    @Test(groups={"tck"})
    public void test_firstDayOfMonth_leap() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(true); i++) {
                LocalDate date = date(2008, month, i);
                LocalDate test = DateAdjusters.firstDayOfMonth().adjustDate(date);
                assertEquals(test.getYear(), 2008);
                assertEquals(test.getMonthOfYear(), month);
                assertEquals(test.getDayOfMonth(), 1);
            }
        }
    }

    //-----------------------------------------------------------------------
    // lastDayOfMonth()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_lastDayOfMonth_serialization() throws IOException, ClassNotFoundException {
        DateAdjuster lastDayOfMonth = DateAdjusters.lastDayOfMonth();
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
        assertNotNull(DateAdjusters.lastDayOfMonth());
    }
    
    @Test(groups={"implementation"})
    public void factory_lastDayOfMonthSame() {
    	assertSame(DateAdjusters.lastDayOfMonth(), DateAdjusters.lastDayOfMonth());
    }

    @Test(groups={"tck"})
    public void test_lastDayOfMonth_nonLeap() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(false); i++) {
                LocalDate date = date(2007, month, i);
                LocalDate test = DateAdjusters.lastDayOfMonth().adjustDate(date);
                assertEquals(test.getYear(), 2007);
                assertEquals(test.getMonthOfYear(), month);
                assertEquals(test.getDayOfMonth(), month.lengthInDays(false));
            }
        }
    }

    @Test(groups={"tck"})
    public void test_lastDayOfMonth_leap() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(true); i++) {
                LocalDate date = date(2008, month, i);
                LocalDate test = DateAdjusters.lastDayOfMonth().adjustDate(date);
                assertEquals(test.getYear(), 2008);
                assertEquals(test.getMonthOfYear(), month);
                assertEquals(test.getDayOfMonth(), month.lengthInDays(true));
            }
        }
    }

    //-----------------------------------------------------------------------
    // firstDayOfNextMonth()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_firstDayOfNextMonth_serialization() throws IOException, ClassNotFoundException {
        DateAdjuster firstDayOfMonth = DateAdjusters.firstDayOfNextMonth();
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
        assertNotNull(DateAdjusters.firstDayOfNextMonth());
    }
    
    @Test(groups={"implementation"})
    public void factory_firstDayOfNextMonthSame() {
    	assertSame(DateAdjusters.firstDayOfNextMonth(), DateAdjusters.firstDayOfNextMonth());
    }

    @Test(groups={"tck"})
    public void test_firstDayOfNextMonth_nonLeap() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(false); i++) {
                LocalDate date = date(2007, month, i);
                LocalDate test = DateAdjusters.firstDayOfNextMonth().adjustDate(date);
                assertEquals(test.getYear(), month == DECEMBER ? 2008 : 2007);
                assertEquals(test.getMonthOfYear(), month.next());
                assertEquals(test.getDayOfMonth(), 1);
            }
        }
    }

    @Test(groups={"tck"})
    public void test_firstDayOfNextMonth_leap() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(true); i++) {
                LocalDate date = date(2008, month, i);
                LocalDate test = DateAdjusters.firstDayOfNextMonth().adjustDate(date);
                assertEquals(test.getYear(), month == DECEMBER ? 2009 : 2008);
                assertEquals(test.getMonthOfYear(), month.next());
                assertEquals(test.getDayOfMonth(), 1);
            }
        }
    }

    //-----------------------------------------------------------------------
    // firstDayOfYear()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_firstDayOfYear_serialization() throws IOException, ClassNotFoundException {
        DateAdjuster firstDayOfYear = DateAdjusters.firstDayOfYear();
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
        assertNotNull(DateAdjusters.firstDayOfYear());
    }
    
    @Test(groups={"implementation"})
    public void factory_firstDayOfYearSame() {
    	assertSame(DateAdjusters.firstDayOfYear(), DateAdjusters.firstDayOfYear());
    }

    @Test(groups={"tck"})
    public void test_firstDayOfYear_nonLeap() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(false); i++) {
                LocalDate date = date(2007, month, i);
                LocalDate test = DateAdjusters.firstDayOfYear().adjustDate(date);
                assertEquals(test.getYear(), 2007);
                assertEquals(test.getMonthOfYear(), MonthOfYear.JANUARY);
                assertEquals(test.getDayOfMonth(), 1);
            }
        }
    }

    @Test(groups={"tck"})
    public void test_firstDayOfYear_leap() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(true); i++) {
                LocalDate date = date(2008, month, i);
                LocalDate test = DateAdjusters.firstDayOfYear().adjustDate(date);
                assertEquals(test.getYear(), 2008);
                assertEquals(test.getMonthOfYear(), MonthOfYear.JANUARY);
                assertEquals(test.getDayOfMonth(), 1);
            }
        }
    }

    //-----------------------------------------------------------------------
    // lastDayOfYear()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_lastDayOfYear_serialization() throws IOException, ClassNotFoundException {
        DateAdjuster lastDayOfYear = DateAdjusters.lastDayOfYear();
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
        assertNotNull(DateAdjusters.lastDayOfYear());
    }
    
    @Test(groups={"implementation"})
    public void factory_lastDayOfYearSame() {
    	assertSame(DateAdjusters.lastDayOfYear(), DateAdjusters.lastDayOfYear());
    }

    @Test(groups={"tck"})
    public void test_lastDayOfYear_nonLeap() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(false); i++) {
                LocalDate date = date(2007, month, i);
                LocalDate test = DateAdjusters.lastDayOfYear().adjustDate(date);
                assertEquals(test.getYear(), 2007);
                assertEquals(test.getMonthOfYear(), MonthOfYear.DECEMBER);
                assertEquals(test.getDayOfMonth(), 31);
            }
        }
    }

    @Test(groups={"tck"})
    public void test_lastDayOfYear_leap() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(true); i++) {
                LocalDate date = date(2008, month, i);
                LocalDate test = DateAdjusters.lastDayOfYear().adjustDate(date);
                assertEquals(test.getYear(), 2008);
                assertEquals(test.getMonthOfYear(), MonthOfYear.DECEMBER);
                assertEquals(test.getDayOfMonth(), 31);
            }
        }
    }

    //-----------------------------------------------------------------------
    // firstDayOfNextYear()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_firstDayOfNextYear_serialization() throws IOException, ClassNotFoundException {
        DateAdjuster firstDayOfMonth = DateAdjusters.firstDayOfNextYear();
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
        assertNotNull(DateAdjusters.firstDayOfNextYear());
    }
    
    @Test(groups={"implementation"})
    public void factory_firstDayOfNextYearSame() {
    	assertSame(DateAdjusters.firstDayOfNextYear(), DateAdjusters.firstDayOfNextYear());
    }

    @Test(groups={"tck"})
    public void test_firstDayOfNextYear_nonLeap() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(false); i++) {
                LocalDate date = date(2007, month, i);
                LocalDate test = DateAdjusters.firstDayOfNextYear().adjustDate(date);
                assertEquals(test.getYear(), 2008);
                assertEquals(test.getMonthOfYear(), JANUARY);
                assertEquals(test.getDayOfMonth(), 1);
            }
        }
    }

    @Test(groups={"tck"})
    public void test_firstDayOfNextYear_leap() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(true); i++) {
                LocalDate date = date(2008, month, i);
                LocalDate test = DateAdjusters.firstDayOfNextYear().adjustDate(date);
                assertEquals(test.getYear(), 2009);
                assertEquals(test.getMonthOfYear(), JANUARY);
                assertEquals(test.getDayOfMonth(), 1);
            }
        }
    }

    //-----------------------------------------------------------------------
    // dayOfWeekInMonth()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_dayOfWeekInMonth_serialization() throws IOException, ClassNotFoundException {
        DateAdjuster dayOfWeekInMonth = DateAdjusters.dayOfWeekInMonth(1, SUNDAY);
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
        assertNotNull(DateAdjusters.dayOfWeekInMonth(1, MONDAY));
        assertEquals(DateAdjusters.dayOfWeekInMonth(1, MONDAY), DateAdjusters.dayOfWeekInMonth(1, MONDAY));
        assertEquals(DateAdjusters.dayOfWeekInMonth(2, MONDAY), DateAdjusters.dayOfWeekInMonth(2, MONDAY));
    }

    @Test(expectedExceptions=IllegalArgumentException.class, groups={"tck"})
    public void factory_dayOfWeekInMonth_ordinalTooSmall() {
        DateAdjusters.dayOfWeekInMonth(0, MONDAY);
    }

    @Test(expectedExceptions=IllegalArgumentException.class, groups={"tck"})
    public void factory_dayOfWeekInMonth_ordinalTooBig() {
        DateAdjusters.dayOfWeekInMonth(6, MONDAY);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_dayOfWeekInMonth_nullDayOfWeek() {
        DateAdjusters.dayOfWeekInMonth(1, null);
    }

    @Test(groups={"tck"})
    public void test_dayOfWeekInMonth_equals() {
        final DateAdjuster mondayInFirstWeek = DateAdjusters.dayOfWeekInMonth(1, MONDAY);
        assertFalse(mondayInFirstWeek.equals(null));
        assertFalse(mondayInFirstWeek.equals(new Object()));
        assertFalse(mondayInFirstWeek.equals(DateAdjusters.lastDayOfMonth()));
        assertFalse(mondayInFirstWeek.equals(DateAdjusters.dayOfWeekInMonth(2, MONDAY)));
        assertFalse(mondayInFirstWeek.equals(DateAdjusters.dayOfWeekInMonth(1, TUESDAY)));
        assertTrue(mondayInFirstWeek.equals(mondayInFirstWeek));
        assertTrue(mondayInFirstWeek.equals(DateAdjusters.dayOfWeekInMonth(1, MONDAY)));
    }
    
    @Test(groups={"tck"})
    public void test_dayOfWeekInMonth_hashCode() {
        assertEquals(DateAdjusters.dayOfWeekInMonth(1, MONDAY).hashCode(), DateAdjusters.dayOfWeekInMonth(1, MONDAY).hashCode());
        assertEquals(DateAdjusters.dayOfWeekInMonth(1, TUESDAY).hashCode(), DateAdjusters.dayOfWeekInMonth(1, TUESDAY).hashCode());
        assertEquals(DateAdjusters.dayOfWeekInMonth(2, MONDAY).hashCode(), DateAdjusters.dayOfWeekInMonth(2, MONDAY).hashCode());
    }

    @Test(groups={"tck"})
    public void test_dayOfWeekInMonth_firstToForth() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(false); i++) {
                LocalDate date = date(2007, month, i);

                for (DayOfWeek dow : DayOfWeek.values()) {
                    for (int ordinal = 1; ordinal <= 4; ordinal++) {
                        LocalDate test = DateAdjusters.dayOfWeekInMonth(ordinal, dow).adjustDate(date);
                        assertEquals(test.getYear(), 2007);
                        assertEquals(test.getMonthOfYear(), month);
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
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(false); i++) {
                LocalDate date = date(2007, month, i);

                for (DayOfWeek dow : DayOfWeek.values()) {
                    LocalDate test = DateAdjusters.dayOfWeekInMonth(5, dow).adjustDate(date);

                    assertEquals(test.getDayOfWeek(), dow);

                    if (test.getMonthOfYear() == month) {
                        assertEquals(test.getYear(), 2007);
                        assertTrue(test.getDayOfMonth() > 28);
                    } else {
                        LocalDate lastForthOcurrence = date(2007, month, 28);
                        int lastForthOcurrenceOrdinal = lastForthOcurrence.getDayOfWeek().ordinal();
                        int lastDayUnadjustedOrdinal = (month.lengthInDays(false) - 28 + lastForthOcurrenceOrdinal) % 7;

                        if (lastDayUnadjustedOrdinal >= lastForthOcurrenceOrdinal) {
                            assertFalse(dow.ordinal() > lastForthOcurrenceOrdinal && dow.ordinal() < lastDayUnadjustedOrdinal);
                        } else {
                            assertFalse(dow.ordinal() > lastForthOcurrenceOrdinal || dow.ordinal() < lastDayUnadjustedOrdinal, date +
                                    "; " + dow);
                        }

                        assertSame(month.next(), test.getMonthOfYear());

                        if (test.getYear() != 2007) {
                            assertSame(month, MonthOfYear.DECEMBER);
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
        DateAdjuster firstInMonth = DateAdjusters.firstInMonth(SUNDAY);
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
        assertNotNull(DateAdjusters.firstInMonth(MONDAY));
        assertEquals(DateAdjusters.firstInMonth(MONDAY), DateAdjusters.firstInMonth(MONDAY));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_firstInMonth_nullDayOfWeek() {
        DateAdjusters.firstInMonth(null);
    }

    @Test(groups={"tck"})
    public void test_firstInMonth_equals() {
        final DateAdjuster mondayInFirstWeek = DateAdjusters.firstInMonth(MONDAY);
        assertFalse(mondayInFirstWeek.equals(null));
        assertFalse(mondayInFirstWeek.equals(new Object()));
        assertFalse(mondayInFirstWeek.equals(DateAdjusters.lastDayOfMonth()));
        assertFalse(mondayInFirstWeek.equals(DateAdjusters.firstInMonth(TUESDAY)));
        assertTrue(mondayInFirstWeek.equals(mondayInFirstWeek));
        assertTrue(mondayInFirstWeek.equals(DateAdjusters.firstInMonth(MONDAY)));
    }

    @Test(groups={"tck"})
    public void test_firstInMonth_hashCode() {
        assertEquals(DateAdjusters.firstInMonth(MONDAY).hashCode(), DateAdjusters.firstInMonth(MONDAY).hashCode());
        assertEquals(DateAdjusters.firstInMonth(TUESDAY).hashCode(), DateAdjusters.firstInMonth(TUESDAY).hashCode());
    }

    @Test(groups={"tck"})
    public void test_firstInMonth() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(false); i++) {
                LocalDate date = date(2007, month, i);

                for (DayOfWeek dow : DayOfWeek.values()) {
                    LocalDate test = DateAdjusters.firstInMonth(dow).adjustDate(date);
                    assertEquals(test.getYear(), 2007);
                    assertEquals(test.getMonthOfYear(), month);
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
        DateAdjuster next = DateAdjusters.next(SUNDAY);
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
        assertNotNull(DateAdjusters.next(MONDAY));
        assertEquals(DateAdjusters.next(MONDAY), DateAdjusters.next(MONDAY));
    }

    @Test(expectedExceptions = NullPointerException.class, groups={"tck"})
    public void factory_next_nullDayOfWeek() {
        DateAdjusters.next(null);
    }

    @Test(groups={"tck"})
    public void test_next_equals() {
        final DateAdjuster mondayInFirstWeek = DateAdjusters.next(MONDAY);
        assertFalse(mondayInFirstWeek.equals(null));
        assertFalse(mondayInFirstWeek.equals(new Object()));
        assertFalse(mondayInFirstWeek.equals(DateAdjusters.lastDayOfMonth()));
        assertFalse(mondayInFirstWeek.equals(DateAdjusters.next(TUESDAY)));
        assertTrue(mondayInFirstWeek.equals(mondayInFirstWeek));
        assertTrue(mondayInFirstWeek.equals(DateAdjusters.next(MONDAY)));
    }

    @Test(groups={"tck"})
    public void test_next_hashCode() {
        assertEquals(DateAdjusters.next(MONDAY).hashCode(), DateAdjusters.next(MONDAY).hashCode());
        assertEquals(DateAdjusters.next(TUESDAY).hashCode(), DateAdjusters.next(TUESDAY).hashCode());
    }

    @Test(groups={"tck"})
    public void test_next() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(false); i++) {
                LocalDate date = date(2007, month, i);

                for (DayOfWeek dow : DayOfWeek.values()) {
                    LocalDate test = DateAdjusters.next(dow).adjustDate(date);

                    assertSame(test.getDayOfWeek(), dow, date + " " + test);

                    if (test.getYear() == 2007) {
                        int dayDiff = test.getDayOfYear() - date.getDayOfYear();
                        assertTrue(dayDiff > 0 && dayDiff < 8);
                    } else {
                        assertSame(month, MonthOfYear.DECEMBER);
                        assertTrue(date.getDayOfMonth() > 24);
                        assertEquals(test.getYear(), 2008);
                        assertSame(test.getMonthOfYear(), MonthOfYear.JANUARY);
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
        DateAdjuster nextOrCurrent = DateAdjusters.nextOrCurrent(SUNDAY);
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
        assertNotNull(DateAdjusters.nextOrCurrent(MONDAY));
        assertEquals(DateAdjusters.nextOrCurrent(MONDAY), DateAdjusters.nextOrCurrent(MONDAY));
    }

    @Test(expectedExceptions = NullPointerException.class, groups={"tck"})
    public void factory_nextOrCurrent_nullDayOfWeek() {
        DateAdjusters.nextOrCurrent(null);
    }

    @Test(groups={"tck"})
    public void test_nextOrCurrent_equals() {
        final DateAdjuster mondayInFirstWeek = DateAdjusters.nextOrCurrent(MONDAY);
        assertFalse(mondayInFirstWeek.equals(null));
        assertFalse(mondayInFirstWeek.equals(new Object()));
        assertFalse(mondayInFirstWeek.equals(DateAdjusters.lastDayOfMonth()));
        assertFalse(mondayInFirstWeek.equals(DateAdjusters.nextOrCurrent(TUESDAY)));
        assertFalse(mondayInFirstWeek.equals(DateAdjusters.next(MONDAY)));
        assertTrue(mondayInFirstWeek.equals(mondayInFirstWeek));
        assertTrue(mondayInFirstWeek.equals(DateAdjusters.nextOrCurrent(MONDAY)));
    }

    @Test(groups={"tck"})
    public void test_nextOrCurrent_hashCode() {
        assertEquals(DateAdjusters.nextOrCurrent(MONDAY).hashCode(), DateAdjusters.nextOrCurrent(MONDAY).hashCode());
        assertEquals(DateAdjusters.nextOrCurrent(TUESDAY).hashCode(), DateAdjusters.nextOrCurrent(TUESDAY).hashCode());
    }

    @Test(groups={"tck"})
    public void test_nextOrCurrent() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(false); i++) {
                LocalDate date = date(2007, month, i);

                for (DayOfWeek dow : DayOfWeek.values()) {
                    LocalDate test = DateAdjusters.nextOrCurrent(dow).adjustDate(date);

                    assertSame(test.getDayOfWeek(), dow);

                    if (test.getYear() == 2007) {
                        int dayDiff = test.getDayOfYear() - date.getDayOfYear();
                        assertTrue(dayDiff < 8);
                        assertEquals(date.equals(test), date.getDayOfWeek() == dow);
                    } else {
                        assertFalse(date.getDayOfWeek() == dow);
                        assertSame(month, MonthOfYear.DECEMBER);
                        assertTrue(date.getDayOfMonth() > 24);
                        assertEquals(test.getYear(), 2008);
                        assertSame(test.getMonthOfYear(), MonthOfYear.JANUARY);
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
        DateAdjuster previous = DateAdjusters.previous(SUNDAY);
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
        assertNotNull(DateAdjusters.previous(MONDAY));
        assertEquals(DateAdjusters.previous(MONDAY), DateAdjusters.previous(MONDAY));
    }

    @Test(expectedExceptions = NullPointerException.class, groups={"tck"})
    public void factory_previous_nullDayOfWeek() {
        DateAdjusters.previous(null);
    }

    @Test(groups={"tck"})
    public void test_previous_equals() {
        final DateAdjuster mondayInFirstWeek = DateAdjusters.previous(MONDAY);
        assertFalse(mondayInFirstWeek.equals(null));
        assertFalse(mondayInFirstWeek.equals(new Object()));
        assertFalse(mondayInFirstWeek.equals(DateAdjusters.lastDayOfMonth()));
        assertFalse(mondayInFirstWeek.equals(DateAdjusters.previous(TUESDAY)));
        assertTrue(mondayInFirstWeek.equals(mondayInFirstWeek));
        assertTrue(mondayInFirstWeek.equals(DateAdjusters.previous(MONDAY)));
    }

    @Test(groups={"tck"})
    public void test_previous_hashCode() {
        assertEquals(DateAdjusters.previous(MONDAY).hashCode(), DateAdjusters.previous(MONDAY).hashCode());
        assertEquals(DateAdjusters.previous(TUESDAY).hashCode(), DateAdjusters.previous(TUESDAY).hashCode());
    }

    @Test(groups={"tck"})
    public void test_previous() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(false); i++) {
                LocalDate date = date(2007, month, i);

                for (DayOfWeek dow : DayOfWeek.values()) {
                    LocalDate test = DateAdjusters.previous(dow).adjustDate(date);

                    assertSame(test.getDayOfWeek(), dow, date + " " + test);

                    if (test.getYear() == 2007) {
                        int dayDiff = test.getDayOfYear() - date.getDayOfYear();
                        assertTrue(dayDiff < 0 && dayDiff > -8, dayDiff + " " + test);
                    } else {
                        assertSame(month, MonthOfYear.JANUARY);
                        assertTrue(date.getDayOfMonth() < 8);
                        assertEquals(test.getYear(), 2006);
                        assertSame(test.getMonthOfYear(), MonthOfYear.DECEMBER);
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
        DateAdjuster previousOrCurrent = DateAdjusters.previousOrCurrent(SUNDAY);
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
        assertNotNull(DateAdjusters.previousOrCurrent(MONDAY));
        assertEquals(DateAdjusters.previousOrCurrent(MONDAY), DateAdjusters.previousOrCurrent(MONDAY));
    }

    @Test(expectedExceptions = NullPointerException.class, groups={"tck"})
    public void factory_previousOrCurrent_nullDayOfWeek() {
        DateAdjusters.previousOrCurrent(null);
    }

    @Test(groups={"tck"})
    public void test_previousOrCurrent_equals() {
        final DateAdjuster mondayInFirstWeek = DateAdjusters.previousOrCurrent(MONDAY);
        assertFalse(mondayInFirstWeek.equals(null));
        assertFalse(mondayInFirstWeek.equals(new Object()));
        assertFalse(mondayInFirstWeek.equals(DateAdjusters.lastDayOfMonth()));
        assertFalse(mondayInFirstWeek.equals(DateAdjusters.previousOrCurrent(TUESDAY)));
        assertFalse(mondayInFirstWeek.equals(DateAdjusters.previous(MONDAY)));
        assertTrue(mondayInFirstWeek.equals(mondayInFirstWeek));
        assertTrue(mondayInFirstWeek.equals(DateAdjusters.previousOrCurrent(MONDAY)));
    }

    @Test(groups={"tck"})
    public void test_previousOrCurrent_hashCode() {
        assertEquals(DateAdjusters.previousOrCurrent(MONDAY).hashCode(), DateAdjusters.previousOrCurrent(MONDAY).hashCode());
        assertEquals(DateAdjusters.previousOrCurrent(TUESDAY).hashCode(), DateAdjusters.previousOrCurrent(TUESDAY).hashCode());
    }

    @Test(groups={"tck"})
    public void test_previousOrCurrent() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(false); i++) {
                LocalDate date = date(2007, month, i);

                for (DayOfWeek dow : DayOfWeek.values()) {
                    LocalDate test = DateAdjusters.previousOrCurrent(dow).adjustDate(date);

                    assertSame(test.getDayOfWeek(), dow);

                    if (test.getYear() == 2007) {
                        int dayDiff = test.getDayOfYear() - date.getDayOfYear();
                        assertTrue(dayDiff <= 0 && dayDiff > -7);
                        assertEquals(date.equals(test), date.getDayOfWeek() == dow);
                    } else {
                        assertFalse(date.getDayOfWeek() == dow);
                        assertSame(month, MonthOfYear.JANUARY);
                        assertTrue(date.getDayOfMonth() < 7);
                        assertEquals(test.getYear(), 2006);
                        assertSame(test.getMonthOfYear(), MonthOfYear.DECEMBER);
                        assertTrue(test.getDayOfMonth() > 25);
                    }
                }
            }
        }
    }

    private LocalDate date(int year, MonthOfYear month, int day) {
        return LocalDate.of(year, month, day);
    }

}
