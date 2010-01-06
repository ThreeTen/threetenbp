/*
 * Copyright (c) 2007-2010, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.calendar.LocalDate.*;
import static javax.time.calendar.field.DayOfMonth.*;
import static javax.time.calendar.field.DayOfWeek.*;
import static javax.time.calendar.field.Year.*;
import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collections;

import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.MonthOfYear;

import org.testng.annotations.Test;

/**
 * Test DateAdjusters.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestDateAdjusters {

    @SuppressWarnings("unchecked")
    public void test_constructor() throws Exception {
        for (Constructor constructor : DateAdjusters.class.getDeclaredConstructors()) {
            assertTrue(Modifier.isPrivate(constructor.getModifiers()));
            constructor.setAccessible(true);
            constructor.newInstance(Collections.nCopies(constructor.getParameterTypes().length, null).toArray());
        }
    }

    //-----------------------------------------------------------------------
    // lastDayOfMonth()
    //-----------------------------------------------------------------------
    public void test_lastDayOfMonth_serialization() throws IOException, ClassNotFoundException {
        DateAdjuster lastDayOfMonth = DateAdjusters.lastDayOfMonth();
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
        assertNotNull(DateAdjusters.lastDayOfMonth());
        assertSame(DateAdjusters.lastDayOfMonth(), DateAdjusters.lastDayOfMonth());
    }

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
    // lastDayOfYear()
    //-----------------------------------------------------------------------
    public void test_lastDayOfYear_serialization() throws IOException, ClassNotFoundException {
        DateAdjuster lastDayOfYear = DateAdjusters.lastDayOfYear();
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
        assertNotNull(DateAdjusters.lastDayOfYear());
        assertSame(DateAdjusters.lastDayOfYear(), DateAdjusters.lastDayOfYear());
    }

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
    // dayOfWeekInMonth()
    //-----------------------------------------------------------------------
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

    public void factory_dayOfWeekInMonth() {
        assertNotNull(DateAdjusters.dayOfWeekInMonth(1, MONDAY));
        assertEquals(DateAdjusters.dayOfWeekInMonth(1, MONDAY), DateAdjusters.dayOfWeekInMonth(1, MONDAY));
        assertEquals(DateAdjusters.dayOfWeekInMonth(2, MONDAY), DateAdjusters.dayOfWeekInMonth(2, MONDAY));
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void factory_dayOfWeekInMonth_ordinalTooSmall() {
        DateAdjusters.dayOfWeekInMonth(0, MONDAY);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void factory_dayOfWeekInMonth_ordinalTooBig() {
        DateAdjusters.dayOfWeekInMonth(6, MONDAY);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_dayOfWeekInMonth_nullDayOfWeek() {
        DateAdjusters.dayOfWeekInMonth(1, null);
    }

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

    public void test_dayOfWeekInMonth_hashCode() {
        assertEquals(DateAdjusters.dayOfWeekInMonth(1, MONDAY).hashCode(), DateAdjusters.dayOfWeekInMonth(1, MONDAY).hashCode());
        assertEquals(DateAdjusters.dayOfWeekInMonth(1, TUESDAY).hashCode(), DateAdjusters.dayOfWeekInMonth(1, TUESDAY).hashCode());
        assertEquals(DateAdjusters.dayOfWeekInMonth(2, MONDAY).hashCode(), DateAdjusters.dayOfWeekInMonth(2, MONDAY).hashCode());
    }

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

    public void factory_firstInMonth() {
        assertNotNull(DateAdjusters.firstInMonth(MONDAY));
        assertEquals(DateAdjusters.firstInMonth(MONDAY), DateAdjusters.firstInMonth(MONDAY));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_firstInMonth_nullDayOfWeek() {
        DateAdjusters.firstInMonth(null);
    }

    public void test_firstInMonth_equals() {
        final DateAdjuster mondayInFirstWeek = DateAdjusters.firstInMonth(MONDAY);
        assertFalse(mondayInFirstWeek.equals(null));
        assertFalse(mondayInFirstWeek.equals(new Object()));
        assertFalse(mondayInFirstWeek.equals(DateAdjusters.lastDayOfMonth()));
        assertFalse(mondayInFirstWeek.equals(DateAdjusters.firstInMonth(TUESDAY)));
        assertTrue(mondayInFirstWeek.equals(mondayInFirstWeek));
        assertTrue(mondayInFirstWeek.equals(DateAdjusters.firstInMonth(MONDAY)));
    }

    public void test_firstInMonth_hashCode() {
        assertEquals(DateAdjusters.firstInMonth(MONDAY).hashCode(), DateAdjusters.firstInMonth(MONDAY).hashCode());
        assertEquals(DateAdjusters.firstInMonth(TUESDAY).hashCode(), DateAdjusters.firstInMonth(TUESDAY).hashCode());
    }

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
    // nextNonWeekendDay()
    //-----------------------------------------------------------------------
    public void test_nextNonWeekendDay_serialization() throws IOException, ClassNotFoundException {
        DateAdjuster nextNonWeekendDay = DateAdjusters.nextNonWeekendDay();
        assertTrue(nextNonWeekendDay instanceof Serializable);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(nextNonWeekendDay);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertSame(ois.readObject(), nextNonWeekendDay);
    }

    public void factory_nextNonWeekendDay() {
        assertNotNull(DateAdjusters.nextNonWeekendDay());
        assertSame(DateAdjusters.nextNonWeekendDay(), DateAdjusters.nextNonWeekendDay());
    }

    public void test_nextNonWeekendDay() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(false); i++) {
                LocalDate date = date(2007, month, i);
                LocalDate test = DateAdjusters.nextNonWeekendDay().adjustDate(date);
                assertTrue(test.isAfter(date));
                assertFalse(test.getDayOfWeek().equals(SATURDAY));
                assertFalse(test.getDayOfWeek().equals(SUNDAY));

                switch (date.getDayOfWeek()) {
                    case FRIDAY:
                    case SATURDAY:
                        assertEquals(test.getDayOfWeek(), MONDAY);
                        break;
                    default:
                        assertEquals(date.getDayOfWeek().next(), test.getDayOfWeek());
                }

                if (test.getYear() == 2007) {
                    int dayDiff = test.getDayOfYear() - date.getDayOfYear();

                    switch (date.getDayOfWeek()) {
                        case FRIDAY:
                            assertEquals(dayDiff, 3);
                            break;
                        case SATURDAY:
                            assertEquals(dayDiff, 2);
                            break;
                        default:
                            assertEquals(dayDiff, 1);
                    }
                } else {
                    assertEquals(test.getYear(), 2008);
                    assertEquals(test.getMonthOfYear(), MonthOfYear.JANUARY);
                    assertEquals(test.getDayOfMonth(), 1);
                }
            }
        }
    }

    public void test_nextNonWeekendDay_yearChange() {
        LocalDate friday = LocalDate.date(isoYear(2010), MonthOfYear.DECEMBER, dayOfMonth(31));
        LocalDate monday = DateAdjusters.nextNonWeekendDay().adjustDate(friday);
        assertEquals(LocalDate.date(isoYear(2011), MonthOfYear.JANUARY, dayOfMonth(3)), monday);

        LocalDate saturday = LocalDate.date(isoYear(2011), MonthOfYear.DECEMBER, dayOfMonth(31));
        monday = DateAdjusters.nextNonWeekendDay().adjustDate(saturday);
        assertEquals(LocalDate.date(isoYear(2012), MonthOfYear.JANUARY, dayOfMonth(2)), monday);
    }

//    //-----------------------------------------------------------------------
//    // nextMonday()
//    //-----------------------------------------------------------------------
//    public void test_nextMonday_serialization() throws IOException, ClassNotFoundException {
//        DateAdjuster nextMonday = DateAdjusters.nextMonday();
//        assertTrue(nextMonday instanceof Serializable);
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ObjectOutputStream oos = new ObjectOutputStream(baos);
//        oos.writeObject(nextMonday);
//        oos.close();
//
//        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
//                baos.toByteArray()));
//        assertEquals(ois.readObject(), nextMonday);
//    }
//
//    public void factory_nextMonday() {
//        assertNotNull(DateAdjusters.nextMonday());
//        assertEquals(DateAdjusters.nextMonday(), DateAdjusters.nextMonday());
//    }
//
//    public void test_nextMonday_equals() {
//        final DateAdjuster mondayInFirstWeek = DateAdjusters.nextMonday();
//        assertFalse(mondayInFirstWeek.equals(null));
//        assertFalse(mondayInFirstWeek.equals(new Object()));
//        assertFalse(mondayInFirstWeek.equals(DateAdjusters.lastDayOfMonth()));
//        assertTrue(mondayInFirstWeek.equals(mondayInFirstWeek));
//        assertTrue(mondayInFirstWeek.equals(DateAdjusters.nextMonday()));
//    }
//
//    public void test_nextMonday_hashCode() {
//        assertEquals(DateAdjusters.nextMonday().hashCode(), DateAdjusters.nextMonday().hashCode());
//    }
//
//    public void test_nextMonday() {
//        for (MonthOfYear month : MonthOfYear.values()) {
//            for (int i = 1; i <= month.lengthInDays(YEAR_2007); i++) {
//                LocalDate date = date(2007, month, i);
//                LocalDate test = DateAdjusters.nextMonday().adjustDate(date);
//
//                assertSame(test.getDayOfWeek(), MONDAY);
//
//                if (test.getYear().equals(YEAR_2007)) {
//                    int dayDiff = test.getDayOfYear() - date.getDayOfYear();
//                    assertTrue(dayDiff > 0 && dayDiff < 8);
//                } else {
//                    assertSame(month, MonthOfYear.DECEMBER);
//                    assertEquals(date.getDayOfMonth(), 31);
//                    assertEquals(test.getYear(), YEAR_2008);
//                    assertSame(test.getMonthOfYear(), MonthOfYear.JANUARY);
//                    assertEquals(test.getDayOfMonth(), 7);
//                }
//            }
//        }
//    }

    //-----------------------------------------------------------------------
    // next()
    //-----------------------------------------------------------------------
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

    public void factory_next() {
        assertNotNull(DateAdjusters.next(MONDAY));
        assertEquals(DateAdjusters.next(MONDAY), DateAdjusters.next(MONDAY));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void factory_next_nullDayOfWeek() {
        DateAdjusters.next(null);
    }

    public void test_next_equals() {
        final DateAdjuster mondayInFirstWeek = DateAdjusters.next(MONDAY);
        assertFalse(mondayInFirstWeek.equals(null));
        assertFalse(mondayInFirstWeek.equals(new Object()));
        assertFalse(mondayInFirstWeek.equals(DateAdjusters.lastDayOfMonth()));
        assertFalse(mondayInFirstWeek.equals(DateAdjusters.next(TUESDAY)));
        assertTrue(mondayInFirstWeek.equals(mondayInFirstWeek));
        assertTrue(mondayInFirstWeek.equals(DateAdjusters.next(MONDAY)));
    }

    public void test_next_hashCode() {
        assertEquals(DateAdjusters.next(MONDAY).hashCode(), DateAdjusters.next(MONDAY).hashCode());
        assertEquals(DateAdjusters.next(TUESDAY).hashCode(), DateAdjusters.next(TUESDAY).hashCode());
    }

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

    public void factory_nextOrCurrent() {
        assertNotNull(DateAdjusters.nextOrCurrent(MONDAY));
        assertEquals(DateAdjusters.nextOrCurrent(MONDAY), DateAdjusters.nextOrCurrent(MONDAY));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void factory_nextOrCurrent_nullDayOfWeek() {
        DateAdjusters.nextOrCurrent(null);
    }

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

    public void test_nextOrCurrent_hashCode() {
        assertEquals(DateAdjusters.nextOrCurrent(MONDAY).hashCode(), DateAdjusters.nextOrCurrent(MONDAY).hashCode());
        assertEquals(DateAdjusters.nextOrCurrent(TUESDAY).hashCode(), DateAdjusters.nextOrCurrent(TUESDAY).hashCode());
    }

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

    public void factory_previous() {
        assertNotNull(DateAdjusters.previous(MONDAY));
        assertEquals(DateAdjusters.previous(MONDAY), DateAdjusters.previous(MONDAY));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void factory_previous_nullDayOfWeek() {
        DateAdjusters.previous(null);
    }

    public void test_previous_equals() {
        final DateAdjuster mondayInFirstWeek = DateAdjusters.previous(MONDAY);
        assertFalse(mondayInFirstWeek.equals(null));
        assertFalse(mondayInFirstWeek.equals(new Object()));
        assertFalse(mondayInFirstWeek.equals(DateAdjusters.lastDayOfMonth()));
        assertFalse(mondayInFirstWeek.equals(DateAdjusters.previous(TUESDAY)));
        assertTrue(mondayInFirstWeek.equals(mondayInFirstWeek));
        assertTrue(mondayInFirstWeek.equals(DateAdjusters.previous(MONDAY)));
    }

    public void test_previous_hashCode() {
        assertEquals(DateAdjusters.previous(MONDAY).hashCode(), DateAdjusters.previous(MONDAY).hashCode());
        assertEquals(DateAdjusters.previous(TUESDAY).hashCode(), DateAdjusters.previous(TUESDAY).hashCode());
    }

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

    public void factory_previousOrCurrent() {
        assertNotNull(DateAdjusters.previousOrCurrent(MONDAY));
        assertEquals(DateAdjusters.previousOrCurrent(MONDAY), DateAdjusters.previousOrCurrent(MONDAY));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void factory_previousOrCurrent_nullDayOfWeek() {
        DateAdjusters.previousOrCurrent(null);
    }

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

    public void test_previousOrCurrent_hashCode() {
        assertEquals(DateAdjusters.previousOrCurrent(MONDAY).hashCode(), DateAdjusters.previousOrCurrent(MONDAY).hashCode());
        assertEquals(DateAdjusters.previousOrCurrent(TUESDAY).hashCode(), DateAdjusters.previousOrCurrent(TUESDAY).hashCode());
    }

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
}
