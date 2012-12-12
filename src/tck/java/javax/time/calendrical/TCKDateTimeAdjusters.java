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
import static javax.time.DayOfWeek.TUESDAY;
import static javax.time.Month.DECEMBER;
import static javax.time.Month.JANUARY;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import javax.time.DayOfWeek;
import javax.time.LocalDate;
import javax.time.Month;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test DateTimeAdjusters.
 */
@Test
public class TCKDateTimeAdjusters {

    //-----------------------------------------------------------------------
    // firstDayOfMonth()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_firstDayOfMonth() {
        assertNotNull(DateTimeAdjusters.firstDayOfMonth());
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
    public void factory_lastDayOfMonth() {
        assertNotNull(DateTimeAdjusters.lastDayOfMonth());
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
    public void factory_firstDayOfNextMonth() {
        assertNotNull(DateTimeAdjusters.firstDayOfNextMonth());
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
    public void factory_firstDayOfYear() {
        assertNotNull(DateTimeAdjusters.firstDayOfYear());
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
    public void factory_lastDayOfYear() {
        assertNotNull(DateTimeAdjusters.lastDayOfYear());
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
    public void factory_firstDayOfNextYear() {
        assertNotNull(DateTimeAdjusters.firstDayOfNextYear());
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
    public void factory_dayOfWeekInMonth() {
        assertNotNull(DateTimeAdjusters.dayOfWeekInMonth(1, MONDAY));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_dayOfWeekInMonth_nullDayOfWeek() {
        DateTimeAdjusters.dayOfWeekInMonth(1, null);
    }

    @DataProvider(name = "dayOfWeekInMonth_positive")
    Object[][] data_dayOfWeekInMonth_positive() {
        return new Object[][] {
            {2011, 1, TUESDAY, date(2011, 1, 4)},
            {2011, 2, TUESDAY, date(2011, 2, 1)},
            {2011, 3, TUESDAY, date(2011, 3, 1)},
            {2011, 4, TUESDAY, date(2011, 4, 5)},
            {2011, 5, TUESDAY, date(2011, 5, 3)},
            {2011, 6, TUESDAY, date(2011, 6, 7)},
            {2011, 7, TUESDAY, date(2011, 7, 5)},
            {2011, 8, TUESDAY, date(2011, 8, 2)},
            {2011, 9, TUESDAY, date(2011, 9, 6)},
            {2011, 10, TUESDAY, date(2011, 10, 4)},
            {2011, 11, TUESDAY, date(2011, 11, 1)},
            {2011, 12, TUESDAY, date(2011, 12, 6)},
        };
    }

    @Test(groups={"tck"}, dataProvider = "dayOfWeekInMonth_positive")
    public void test_dayOfWeekInMonth_positive(int year, int month, DayOfWeek dow, LocalDate expected) {
        for (int ordinal = 1; ordinal <= 5; ordinal++) {
            for (int day = 1; day <= Month.of(month).length(false); day++) {
                LocalDate date = date(year, month, day);
                LocalDate test = (LocalDate) DateTimeAdjusters.dayOfWeekInMonth(ordinal, dow).doWithAdjustment(date);
                assertEquals(test, expected.plusWeeks(ordinal - 1));
            }
        }
    }

    @DataProvider(name = "dayOfWeekInMonth_zero")
    Object[][] data_dayOfWeekInMonth_zero() {
        return new Object[][] {
            {2011, 1, TUESDAY, date(2010, 12, 28)},
            {2011, 2, TUESDAY, date(2011, 1, 25)},
            {2011, 3, TUESDAY, date(2011, 2, 22)},
            {2011, 4, TUESDAY, date(2011, 3, 29)},
            {2011, 5, TUESDAY, date(2011, 4, 26)},
            {2011, 6, TUESDAY, date(2011, 5, 31)},
            {2011, 7, TUESDAY, date(2011, 6, 28)},
            {2011, 8, TUESDAY, date(2011, 7, 26)},
            {2011, 9, TUESDAY, date(2011, 8, 30)},
            {2011, 10, TUESDAY, date(2011, 9, 27)},
            {2011, 11, TUESDAY, date(2011, 10, 25)},
            {2011, 12, TUESDAY, date(2011, 11, 29)},
        };
    }

    @Test(groups={"tck"}, dataProvider = "dayOfWeekInMonth_zero")
    public void test_dayOfWeekInMonth_zero(int year, int month, DayOfWeek dow, LocalDate expected) {
        for (int day = 1; day <= Month.of(month).length(false); day++) {
            LocalDate date = date(year, month, day);
            LocalDate test = (LocalDate) DateTimeAdjusters.dayOfWeekInMonth(0, dow).doWithAdjustment(date);
            assertEquals(test, expected);
        }
    }

    @DataProvider(name = "dayOfWeekInMonth_negative")
    Object[][] data_dayOfWeekInMonth_negative() {
        return new Object[][] {
            {2011, 1, TUESDAY, date(2011, 1, 25)},
            {2011, 2, TUESDAY, date(2011, 2, 22)},
            {2011, 3, TUESDAY, date(2011, 3, 29)},
            {2011, 4, TUESDAY, date(2011, 4, 26)},
            {2011, 5, TUESDAY, date(2011, 5, 31)},
            {2011, 6, TUESDAY, date(2011, 6, 28)},
            {2011, 7, TUESDAY, date(2011, 7, 26)},
            {2011, 8, TUESDAY, date(2011, 8, 30)},
            {2011, 9, TUESDAY, date(2011, 9, 27)},
            {2011, 10, TUESDAY, date(2011, 10, 25)},
            {2011, 11, TUESDAY, date(2011, 11, 29)},
            {2011, 12, TUESDAY, date(2011, 12, 27)},
        };
    }

    @Test(groups={"tck"}, dataProvider = "dayOfWeekInMonth_negative")
    public void test_dayOfWeekInMonth_negative(int year, int month, DayOfWeek dow, LocalDate expected) {
        for (int ordinal = 0; ordinal < 5; ordinal++) {
            for (int day = 1; day <= Month.of(month).length(false); day++) {
                LocalDate date = date(year, month, day);
                LocalDate test = (LocalDate) DateTimeAdjusters.dayOfWeekInMonth(-1 - ordinal, dow).doWithAdjustment(date);
                assertEquals(test, expected.minusWeeks(ordinal));
            }
        }
    }

    //-----------------------------------------------------------------------
    // firstInMonth()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_firstInMonth() {
        assertNotNull(DateTimeAdjusters.firstInMonth(MONDAY));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_firstInMonth_nullDayOfWeek() {
        DateTimeAdjusters.firstInMonth(null);
    }

    @Test(groups={"tck"}, dataProvider = "dayOfWeekInMonth_positive")
    public void test_firstInMonth(int year, int month, DayOfWeek dow, LocalDate expected) {
        for (int day = 1; day <= Month.of(month).length(false); day++) {
            LocalDate date = date(year, month, day);
            LocalDate test = (LocalDate) DateTimeAdjusters.firstInMonth(dow).doWithAdjustment(date);
            assertEquals(test, expected, "day-of-month=" + day);
        }
    }

    //-----------------------------------------------------------------------
    // lastInMonth()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_lastInMonth() {
        assertNotNull(DateTimeAdjusters.lastInMonth(MONDAY));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_lastInMonth_nullDayOfWeek() {
        DateTimeAdjusters.lastInMonth(null);
    }

    @Test(groups={"tck"}, dataProvider = "dayOfWeekInMonth_negative")
    public void test_lastInMonth(int year, int month, DayOfWeek dow, LocalDate expected) {
        for (int day = 1; day <= Month.of(month).length(false); day++) {
            LocalDate date = date(year, month, day);
            LocalDate test = (LocalDate) DateTimeAdjusters.lastInMonth(dow).doWithAdjustment(date);
            assertEquals(test, expected, "day-of-month=" + day);
        }
    }

    //-----------------------------------------------------------------------
    // next()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_next() {
        assertNotNull(DateTimeAdjusters.next(MONDAY));
    }

    @Test(expectedExceptions = NullPointerException.class, groups={"tck"})
    public void factory_next_nullDayOfWeek() {
        DateTimeAdjusters.next(null);
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
    // nextOrSame()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_nextOrSame() {
        assertNotNull(DateTimeAdjusters.nextOrSame(MONDAY));
    }

    @Test(expectedExceptions = NullPointerException.class, groups={"tck"})
    public void factory_nextOrSame_nullDayOfWeek() {
        DateTimeAdjusters.nextOrSame(null);
    }

    @Test(groups={"tck"})
    public void test_nextOrSame() {
        for (Month month : Month.values()) {
            for (int i = 1; i <= month.length(false); i++) {
                LocalDate date = date(2007, month, i);

                for (DayOfWeek dow : DayOfWeek.values()) {
                    LocalDate test = (LocalDate) DateTimeAdjusters.nextOrSame(dow).doWithAdjustment(date);

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
    public void factory_previous() {
        assertNotNull(DateTimeAdjusters.previous(MONDAY));
    }

    @Test(expectedExceptions = NullPointerException.class, groups={"tck"})
    public void factory_previous_nullDayOfWeek() {
        DateTimeAdjusters.previous(null);
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
    // previousOrSame()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_previousOrSame() {
        assertNotNull(DateTimeAdjusters.previousOrSame(MONDAY));
    }

    @Test(expectedExceptions = NullPointerException.class, groups={"tck"})
    public void factory_previousOrSame_nullDayOfWeek() {
        DateTimeAdjusters.previousOrSame(null);
    }

    @Test(groups={"tck"})
    public void test_previousOrSame() {
        for (Month month : Month.values()) {
            for (int i = 1; i <= month.length(false); i++) {
                LocalDate date = date(2007, month, i);

                for (DayOfWeek dow : DayOfWeek.values()) {
                    LocalDate test = (LocalDate) DateTimeAdjusters.previousOrSame(dow).doWithAdjustment(date);

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

    private LocalDate date(int year, int month, int day) {
        return LocalDate.of(year, month, day);
    }

}
