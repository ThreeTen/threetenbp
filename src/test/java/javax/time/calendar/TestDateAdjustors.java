/*
 * Copyright (c) 2007, 2008, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.calendar.Calendars.*;
import static javax.time.calendar.field.DayOfWeek.*;
import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.Year;

import org.testng.annotations.Test;

/**
 * Test DateAdjustors.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestDateAdjustors {

    private static final Year YEAR_2007 = year(2007);
    private static final Year YEAR_2008 = year(2008);

    //-----------------------------------------------------------------------
    // lastDayOfMonth()
    //-----------------------------------------------------------------------
    public void test_lastDayOfMonth_serialization() throws IOException, ClassNotFoundException {
        DateAdjustor lastDayOfMonth = DateAdjustors.lastDayOfMonth();
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
        assertNotNull(DateAdjustors.lastDayOfMonth());
        assertSame(DateAdjustors.lastDayOfMonth(), DateAdjustors.lastDayOfMonth());
    }

    public void test_lastDayOfMonth_nonLeap() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(YEAR_2007); i++) {
                LocalDate date = date(YEAR_2007, month, dayOfMonth(i));
                LocalDate test = DateAdjustors.lastDayOfMonth().adjustDate(date);
                assertEquals(test.getYear(), YEAR_2007);
                assertEquals(test.getMonthOfYear(), month);
                assertEquals(test.getDayOfMonth(), month.getLastDayOfMonth(YEAR_2007));
            }
        }
    }

    public void test_lastDayOfMonth_leap() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(YEAR_2008); i++) {
                LocalDate date = date(YEAR_2008, month, dayOfMonth(i));
                LocalDate test = DateAdjustors.lastDayOfMonth().adjustDate(date);
                assertEquals(test.getYear(), YEAR_2008);
                assertEquals(test.getMonthOfYear(), month);
                assertEquals(test.getDayOfMonth(), month.getLastDayOfMonth(YEAR_2008));
            }
        }
    }

    //-----------------------------------------------------------------------
    // lastDayOfYear()
    //-----------------------------------------------------------------------
    public void test_lastDayOfYear_serialization() throws IOException, ClassNotFoundException {
        DateAdjustor lastDayOfYear = DateAdjustors.lastDayOfYear();
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
        assertNotNull(DateAdjustors.lastDayOfYear());
        assertSame(DateAdjustors.lastDayOfYear(), DateAdjustors.lastDayOfYear());
    }

    public void test_lastDayOfYear_nonLeap() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(YEAR_2007); i++) {
                LocalDate date = date(YEAR_2007, month, dayOfMonth(i));
                LocalDate test = DateAdjustors.lastDayOfYear().adjustDate(date);
                assertEquals(test.getYear(), YEAR_2007);
                assertEquals(test.getMonthOfYear(), MonthOfYear.DECEMBER);
                assertEquals(test.getDayOfMonth(), dayOfMonth(31));
            }
        }
    }

    public void test_lastDayOfYear_leap() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(YEAR_2008); i++) {
                LocalDate date = date(YEAR_2008, month, dayOfMonth(i));
                LocalDate test = DateAdjustors.lastDayOfYear().adjustDate(date);
                assertEquals(test.getYear(), YEAR_2008);
                assertEquals(test.getMonthOfYear(), MonthOfYear.DECEMBER);
                assertEquals(test.getDayOfMonth(), dayOfMonth(31));
            }
        }
    }

    //-----------------------------------------------------------------------
    // dayOfWeekInMonth()
    //-----------------------------------------------------------------------
    public void test_dayOfWeekInMonth_serialization() throws IOException, ClassNotFoundException {
        DateAdjustor dayOfWeekInMonth = DateAdjustors.dayOfWeekInMonth(1, SUNDAY);
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
        assertNotNull(DateAdjustors.dayOfWeekInMonth(1, MONDAY));
        assertEquals(DateAdjustors.dayOfWeekInMonth(1, MONDAY), DateAdjustors.dayOfWeekInMonth(1, MONDAY));
        assertEquals(DateAdjustors.dayOfWeekInMonth(2, MONDAY), DateAdjustors.dayOfWeekInMonth(2, MONDAY));
    }
    
    @Test(expectedExceptions=IllegalArgumentException.class)
    public void factory_dayOfWeekInMonth_ordinalTooSmall() {
        DateAdjustors.dayOfWeekInMonth(0, MONDAY);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void factory_dayOfWeekInMonth_ordinalTooBig() {
        DateAdjustors.dayOfWeekInMonth(6, MONDAY);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_dayOfWeekInMonth_nullDayOfWeek() {
        DateAdjustors.dayOfWeekInMonth(1, null);
    }

    public void test_dayOfWeekInMonth_equals() {
        final DateAdjustor mondayInFirstWeek = DateAdjustors.dayOfWeekInMonth(1, MONDAY);
        assertFalse(mondayInFirstWeek.equals(null));
        assertFalse(mondayInFirstWeek.equals(new Object()));
        assertFalse(mondayInFirstWeek.equals(DateAdjustors.lastDayOfMonth()));
        assertFalse(mondayInFirstWeek.equals(DateAdjustors.dayOfWeekInMonth(2, MONDAY)));
        assertFalse(mondayInFirstWeek.equals(DateAdjustors.dayOfWeekInMonth(1, TUESDAY)));
        assertTrue(mondayInFirstWeek.equals(DateAdjustors.dayOfWeekInMonth(1, MONDAY)));
    }

    public void test_dayOfWeekInMonth_hashCode() {
        assertSame(DateAdjustors.dayOfWeekInMonth(1, MONDAY).hashCode(), DateAdjustors.dayOfWeekInMonth(1, MONDAY).hashCode());
        assertSame(DateAdjustors.dayOfWeekInMonth(1, TUESDAY).hashCode(), DateAdjustors.dayOfWeekInMonth(1, TUESDAY).hashCode());
        assertSame(DateAdjustors.dayOfWeekInMonth(2, MONDAY).hashCode(), DateAdjustors.dayOfWeekInMonth(2, MONDAY).hashCode());
    }

    public void test_dayOfWeekInMonth_firstToForth() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(YEAR_2007); i++) {
                for (DayOfWeek dow : DayOfWeek.values()) {
                    for (int ordinal = 1; ordinal <= 4; ordinal++) {
                        LocalDate date = date(YEAR_2007, month, dayOfMonth(i));
                        LocalDate test = DateAdjustors.dayOfWeekInMonth(ordinal, dow).adjustDate(date);
                        assertEquals(test.getYear(), YEAR_2007);
                        assertEquals(test.getMonthOfYear(), month);
                        assertTrue(test.getDayOfMonth().getValue() > (ordinal - 1) * 7);
                        assertTrue(test.getDayOfMonth().getValue() < ordinal * 7 + 1);
                        assertEquals(test.getDayOfWeek(), dow);
                    }
                }
            }
        }
    }

    public void test_dayOfWeekInMonth_fifth() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(YEAR_2007); i++) {
                for (DayOfWeek dow : DayOfWeek.values()) {
                    LocalDate date = date(YEAR_2007, month, dayOfMonth(i));
                    LocalDate test = DateAdjustors.dayOfWeekInMonth(5, dow).adjustDate(date);

                    assertEquals(test.getDayOfWeek(), dow);

                    if (test.getMonthOfYear() == month) {
                        assertEquals(test.getYear(), YEAR_2007);
                        assertTrue(test.getDayOfMonth().getValue() > 28);
                    } else {
                        LocalDate lastForthOcurrence = date(YEAR_2007, month, dayOfMonth(28));
                        int lastForthOcurrenceOrdinal = lastForthOcurrence.getDayOfWeek().ordinal();
                        int lastDayUnadjustedOrdinal = (month.lengthInDays(YEAR_2007) - 28 + lastForthOcurrenceOrdinal) % 7;

                        if (lastDayUnadjustedOrdinal >= lastForthOcurrenceOrdinal) {
                            assertFalse(dow.ordinal() > lastForthOcurrenceOrdinal && dow.ordinal() < lastDayUnadjustedOrdinal);
                        } else {
                            assertFalse(dow.ordinal() > lastForthOcurrenceOrdinal || dow.ordinal() < lastDayUnadjustedOrdinal, date + "; " + dow);
                        }

                        assertSame(month.next(), test.getMonthOfYear());

                        if (!test.getYear().equals(YEAR_2007)) {
                            assertSame(month, MonthOfYear.DECEMBER);
                            assertEquals(test.getYear(), YEAR_2008);
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
        DateAdjustor firstInMonth = DateAdjustors.firstInMonth(SUNDAY);
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
        assertNotNull(DateAdjustors.firstInMonth(MONDAY));
        assertEquals(DateAdjustors.firstInMonth(MONDAY), DateAdjustors.firstInMonth(MONDAY));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_firstInMonth_nullDayOfWeek() {
        DateAdjustors.firstInMonth(null);
    }

    public void test_firstInMonth_equals() {
        final DateAdjustor mondayInFirstWeek = DateAdjustors.firstInMonth(MONDAY);
        assertFalse(mondayInFirstWeek.equals(null));
        assertFalse(mondayInFirstWeek.equals(new Object()));
        assertFalse(mondayInFirstWeek.equals(DateAdjustors.lastDayOfMonth()));
        assertFalse(mondayInFirstWeek.equals(DateAdjustors.firstInMonth(TUESDAY)));
        assertTrue(mondayInFirstWeek.equals(DateAdjustors.firstInMonth(MONDAY)));
    }

    public void test_firstInMonth_hashCode() {
        assertSame(DateAdjustors.firstInMonth(MONDAY).hashCode(), DateAdjustors.firstInMonth(MONDAY).hashCode());
        assertSame(DateAdjustors.firstInMonth(TUESDAY).hashCode(), DateAdjustors.firstInMonth(TUESDAY).hashCode());
    }

    public void test_firstInMonth() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(YEAR_2007); i++) {
                for (DayOfWeek dow : DayOfWeek.values()) {
                    LocalDate date = date(YEAR_2007, month, dayOfMonth(i));
                    LocalDate test = DateAdjustors.firstInMonth(dow).adjustDate(date);
                    assertEquals(test.getYear(), YEAR_2007);
                    assertEquals(test.getMonthOfYear(), month);
                    assertTrue(test.getDayOfMonth().getValue() < 8);
                    assertEquals(test.getDayOfWeek(), dow);
                }
            }
        }
    }

    //-----------------------------------------------------------------------
    // nextNonWeekendDay()
    //-----------------------------------------------------------------------
    public void test_nextNonWeekendDay_serialization() throws IOException, ClassNotFoundException {
        DateAdjustor nextNonWeekendDay = DateAdjustors.nextNonWeekendDay();
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
        assertNotNull(DateAdjustors.nextNonWeekendDay());
        assertSame(DateAdjustors.nextNonWeekendDay(), DateAdjustors.nextNonWeekendDay());
    }

    public void test_nextNonWeekendDay() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(YEAR_2007); i++) {
                LocalDate date = date(YEAR_2007, month, dayOfMonth(i));
                LocalDate test = DateAdjustors.nextNonWeekendDay().adjustDate(date);
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

                if (test.getYear().equals(YEAR_2007)) {
                    int dayDiff = test.getDayOfYear().getValue() - date.getDayOfYear().getValue();

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
                    assertEquals(test.getYear(), YEAR_2008);
                    assertEquals(test.getMonthOfYear(), january());
                    assertEquals(test.getDayOfMonth().getValue(), 1);
                }
            }
        }
    }
    
    public void test_nextNonWeekendDay_yearChange() {
        LocalDate friday = LocalDate.date(year(2010), MonthOfYear.DECEMBER, dayOfMonth(31));
        LocalDate monday = DateAdjustors.nextNonWeekendDay().adjustDate(friday);
        assertEquals(LocalDate.date(year(2011), january(), dayOfMonth(3)), monday);

        LocalDate saturday = LocalDate.date(year(2011), MonthOfYear.DECEMBER, dayOfMonth(31));
        monday = DateAdjustors.nextNonWeekendDay().adjustDate(saturday);
        assertEquals(LocalDate.date(year(2012), january(), dayOfMonth(2)), monday);
    }
}
