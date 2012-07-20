/*
 * Copyright (c) 2008-2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.chrono;

import static javax.time.calendrical.LocalDateTimeField.DAY_OF_MONTH;
import static javax.time.calendrical.LocalDateTimeField.MONTH_OF_YEAR;
import static javax.time.calendrical.LocalDateTimeField.YEAR;
import static javax.time.calendrical.LocalPeriodUnit.DAYS;
import static javax.time.calendrical.LocalPeriodUnit.MONTHS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import javax.time.LocalDate;
import javax.time.Period;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestISODate {

    private ChronoDate TEST_2007_07_15;
    private ChronoDate TEST_2007_07_16;
    private LocalDate TEST_LOCAL_2007_07_15;

    @BeforeMethod(groups="tck")
    public void setUp() {
        TEST_2007_07_15 = ISOChronology.INSTANCE.date(2007, 7, 15);
        TEST_2007_07_16 = ISOChronology.INSTANCE.date(2007, 7, 16);
        TEST_LOCAL_2007_07_15 = LocalDate.of(2007, 7, 15);
    }

    //-----------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class, groups="tck")
    public void testFromFactory_null() {
        ISODate.from(null);
    }

    //-----------------------------------------------------------------
    @Test(groups="tck")
    public void extractLocal() {
        assertEquals(TEST_2007_07_15.extract(LocalDate.class), TEST_LOCAL_2007_07_15);
    }
    
    @Test(groups="tck")
    public void extractChronoDate() {
        assertEquals(TEST_2007_07_15.extract(ChronoDate.class), TEST_2007_07_15);
    }
    
    @Test(groups="tck")
    public void extractChrono() {
        assertEquals(TEST_2007_07_15.extract(Chronology.class), ISOChronology.INSTANCE);
    }
    
    @Test(groups="tck")
    public void extractClass() {
        assertEquals(TEST_2007_07_15.extract(Class.class), ChronoDate.class);
    }
    
    @Test(groups="tck")
    public void extractLocal_null() {
        assertNull(TEST_2007_07_15.extract(null));
    }
    
    //-----------------------------------------------------------------
    @Test(groups="tck")
    public void testGetters() {
        check(TEST_2007_07_15, 2007, 7, 15);
    }

    //-----------------------------------------------------------------
    @Test(groups="tck")
    public void testFieldGetters() {
        assertEquals(TEST_2007_07_15.get(YEAR), 2007);
        assertEquals(TEST_2007_07_15.get(MONTH_OF_YEAR), 7);
        assertEquals(TEST_2007_07_15.get(DAY_OF_MONTH), 15);
    }
    
    @Test(expectedExceptions=NullPointerException.class, groups="tck")
    public void testFieldGetters_null() {
        TEST_2007_07_15.get(null);
    }

    //-----------------------------------------------------------------
    @Test(groups="tck")
    public void testChronology() {
        assertEquals(TEST_2007_07_15.getChronology(), ISOChronology.INSTANCE);
    }

    //-----------------------------------------------------------------
    @Test(groups="tck")
    public void testIsAfter() {
        assertTrue(TEST_2007_07_16.isAfter(TEST_2007_07_15));
        assertFalse(TEST_2007_07_15.isAfter(TEST_2007_07_16));
        assertFalse(TEST_2007_07_15.isAfter(TEST_2007_07_15));
    }
    
    @Test(expectedExceptions=NullPointerException.class, groups="tck")
    public void testIsAfter_null() {
        TEST_2007_07_15.isAfter(null);
    }
    
    //-----------------------------------------------------------------
    @Test(groups="tck")
    public void testIsBefore() {
        assertTrue(TEST_2007_07_15.isBefore(TEST_2007_07_16));
        assertFalse(TEST_2007_07_16.isBefore(TEST_2007_07_15));
        assertFalse(TEST_2007_07_15.isBefore(TEST_2007_07_15));
    }
    
    @Test(expectedExceptions=NullPointerException.class, groups="tck")
    public void testIsBefore_null() {
        TEST_2007_07_15.isBefore(null);
    }
    
    //-----------------------------------------------------------------
    @DataProvider(name="leapYears")
    Object[][] leapYearInformation() {
        return new Object[][] {
            {2000, true},
            {1996, true},
            {1600, true},
            
            {1900, false},
            {2100, false},
        };
    }
    
    @Test(dataProvider="leapYears", groups="tck")
    public void testIsLeapYear(int year, boolean isLeapYear) {
        assertEquals(TEST_2007_07_15.withYearOfEra(year).isLeapYear(), isLeapYear);
    }
    
    //-----------------------------------------------------------------
    @DataProvider(name="monthLengths")
    Object[][] monthLengths() {
        return new Object[][] {
            {2000, 4, 30},
            {2001, 4, 30},
            {2000, 2, 29},
            {2001, 2, 28},
            {2000, 5, 31},
            {2001, 5, 31},
        };
    }
    
    @Test(dataProvider="monthLengths", groups="tck")
    public void testLengthOfMonth(int year, int monthOfYear, int daysInMonth) {
        ChronoDate date = ISOChronology.INSTANCE.date(year, monthOfYear, 1);
        assertEquals(date.lengthOfMonth(), daysInMonth);
    }
    
    //-----------------------------------------------------------------
    @Test(dataProvider="leapYears", groups="tck")
    public void testLengthOfYear(int year, boolean isLeapYear) {
        int lengthOfYear = isLeapYear ? 366 : 365;
        assertEquals(TEST_2007_07_15.withYearOfEra(year).lengthOfYear(), lengthOfYear);
    }
    
    //-----------------------------------------------------------------
    @DataProvider(name="minusDays")
    Object[][] minusDays() {
        return new Object[][] {
            {1, 2007, 7, 14},
            {14, 2007, 7, 1},
            {15, 2007, 6, 30},
            {365, 2006, 7, 15},
        };
    }
    
    @Test(dataProvider="minusDays", groups="tck")
    public void testMinusDays(long daysToSubtract, int year, int month, int dayOfMonth) {
        ChronoDate newDate = TEST_2007_07_15.minusDays(daysToSubtract);
        check(newDate, year, month, dayOfMonth);
    }
    
    //-----------------------------------------------------------------
    @DataProvider(name="minusMonths")
    Object[][] minusMonths() {
        return new Object[][] {
            {1, 2007, 6, 15},
            {7, 2006, 12, 15},
            {12, 2006, 7, 15},
            {24, 2005, 7, 15},
        };
    }
    
    @Test(dataProvider="minusMonths", groups="tck")
    public void testMinusMonths(long monthsToSubtract, int year, int month, int dayOfMonth) {
        ChronoDate newDate = TEST_2007_07_15.minusMonths(monthsToSubtract);
        check(newDate, year, month, dayOfMonth);
    }
    
    @Test(groups="tck")
    public void testMinusMonths_missingDay() {
        ChronoDate newDate = ISOChronology.INSTANCE.date(2012, 3, 31).minusMonths(1);
        check(newDate, 2012, 2, 29);
    }
    
    //-----------------------------------------------------------------
    @Test(dataProvider="minusDays", groups="tck")
    public void testMinus_daysPeriod(long daysToSubtract, int year, int month, int dayOfMonth) {
        ChronoDate newDate = TEST_2007_07_15.minus(daysToSubtract, DAYS);
        check(newDate, year, month, dayOfMonth);
    }

    @Test(dataProvider="minusMonths", groups="tck")
    public void testMinus_monthsPeriod(long monthsToSubtract, int year, int month, int dayOfMonth) {
        ChronoDate newDate = TEST_2007_07_15.minus(monthsToSubtract, MONTHS);
        check(newDate, year, month, dayOfMonth);
    }

    @Test(groups="tck")
    public void testMinus_monthsPeriod_missingDay() {
        ChronoDate newDate = ISOChronology.INSTANCE.date(2012, 3, 31).minus(1, MONTHS);
        check(newDate, 2012, 2, 29);
    }
    
    @Test(expectedExceptions=NullPointerException.class, groups="tck")
    public void testMinus_null() {
        TEST_2007_07_15.minus(1, null);
    }
    
    //-----------------------------------------------------------------
    @Test(dataProvider="minusDays", groups="tck")
    public void testMinusPeriod_daysPeriod(long daysToSubtract, int year, int month, int dayOfMonth) {
        ChronoDate newDate = TEST_2007_07_15.minus(Period.of(daysToSubtract, DAYS));
        check(newDate, year, month, dayOfMonth);
    }

    @Test(dataProvider="minusMonths", groups="tck")
    public void testMinusPeriod_monthsPeriod(long monthsToSubtract, int year, int month, int dayOfMonth) {
        ChronoDate newDate = TEST_2007_07_15.minus(Period.of(monthsToSubtract, MONTHS));
        check(newDate, year, month, dayOfMonth);
    }

    @Test(groups="tck")
    public void testMinusPeriod_monthsPeriod_missingDay() {
        ChronoDate newDate = ISOChronology.INSTANCE.date(2012, 3, 31).minus(Period.of(1, MONTHS));
        check(newDate, 2012, 2, 29);
    }
    
    @Test(expectedExceptions=NullPointerException.class, groups="tck")
    public void testMinusPeriod_null() {
        TEST_2007_07_15.minus(null);
    }

    //-----------------------------------------------------------------
    // TODO: minus years, weeks
    // TODO: with and plus methods
    
    //-----------------------------------------------------------------
    private void check(ChronoDate date, int prolepticYear, int month, int dayOfMonth) {
        assertEquals(date.getProlepticYear(), prolepticYear);
        assertEquals(date.getMonth(), month);
        assertEquals(date.getDayOfMonth(), dayOfMonth);
    }
    
}
