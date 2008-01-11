/*
 * Copyright (c) 2007, Stephen Colebourne & Michael Nascimento Santos
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
import static org.testng.Assert.*;

import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.Year;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test DateAdjustors.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestDateAdjustors {

    private static final Year YEAR_2007 = year(2007);
    private static final Year YEAR_2008 = year(2008);

    @BeforeMethod
    public void setUp() {
    }

    //-----------------------------------------------------------------------
    // lastDayOfMonth()
    //-----------------------------------------------------------------------
//    public void test_interfaces_lastDayOfMonth() {
//        assertTrue(DateAdjustors.lastDayOfMonth() instanceof Serializable);
//    }

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
//    public void test_interfaces_lastDayOfYear() {
//        assertTrue(DateAdjustors.lastDayOfYear() instanceof Serializable);
//    }

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
//    public void test_interfaces_dayOfWeekInMonth() {
//        assertTrue(DateAdjustors.dayOfWeekInMonth() instanceof Serializable);
//    }

    public void factory_dayOfWeekInMonth() {
        assertNotNull(DateAdjustors.dayOfWeekInMonth(1, DayOfWeek.MONDAY));
        assertEquals(DateAdjustors.dayOfWeekInMonth(1, DayOfWeek.MONDAY), DateAdjustors.dayOfWeekInMonth(1, DayOfWeek.MONDAY));
        assertEquals(DateAdjustors.dayOfWeekInMonth(2, DayOfWeek.MONDAY), DateAdjustors.dayOfWeekInMonth(2, DayOfWeek.MONDAY));
    }

    public void test_dayOfWeekInMonth_first() {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int i = 1; i <= month.lengthInDays(YEAR_2007); i++) {
                for (DayOfWeek dow : DayOfWeek.values()) {
                    LocalDate date = date(YEAR_2007, month, dayOfMonth(i));
                    LocalDate test = DateAdjustors.dayOfWeekInMonth(1, dow).adjustDate(date);
                    System.out.println(date + " " + test);
                    assertEquals(test.getYear(), YEAR_2007);
                    assertEquals(test.getMonthOfYear(), month);
                    assertTrue(test.getDayOfMonth().getValue() < 8);
                    assertEquals(test.getDayOfWeek(), dow);
                }
            }
        }
    }

}
