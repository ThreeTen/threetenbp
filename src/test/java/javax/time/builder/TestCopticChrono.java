/*
 * Copyright (c) 2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.builder;

import static javax.time.MonthOfYear.AUGUST;
import static javax.time.MonthOfYear.DECEMBER;
import static javax.time.MonthOfYear.MARCH;
import static javax.time.builder.StandardDateTimeField.DAY_OF_MONTH;
import static javax.time.builder.StandardDateTimeField.DAY_OF_YEAR;
import static javax.time.builder.StandardDateTimeField.MONTH_OF_YEAR;
import static javax.time.builder.StandardDateTimeField.YEAR;
import static org.testng.Assert.assertEquals;

import javax.time.LocalDate;
import javax.time.calendrical.DateTimeRuleRange;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test
public class TestCopticChrono {

    CopticChrono chrono = CopticChrono.INSTANCE;
    LocalDate march15 = LocalDate.of(2012, MARCH, 15);
    LocalDate dec28 = LocalDate.of(2011, DECEMBER, 28);
    LocalDate dec28leap = LocalDate.of(2012, DECEMBER, 28);

    @Test(groups = "tck")
    public void monthRanges() {
        DateTimeRuleRange monthRange = chrono.getRange(MONTH_OF_YEAR);
        assertEquals(1, monthRange.getMinimum());
        assertEquals(1, monthRange.getLargestMinimum());
        assertEquals(13, monthRange.getMaximum());
        assertEquals(13, monthRange.getSmallestMaximum());
    }

    @Test(groups = "tck")
    public void dayRanges() {
        DateTimeRuleRange monthRange = chrono.getRange(DAY_OF_MONTH);
        assertEquals(1, monthRange.getMinimum());
        assertEquals(1, monthRange.getLargestMinimum());
        assertEquals(30, monthRange.getMaximum());
        assertEquals(5, monthRange.getSmallestMaximum());
    }

    @Test(groups = "tck")
    public void specificRanges() {
        assertEquals(chrono.getRange(DAY_OF_MONTH, march15, null).getMaximum(), 30);
        assertEquals(chrono.getRange(DAY_OF_MONTH, dec28, null).getMaximum(), 30);
        assertEquals(chrono.getRange(DAY_OF_MONTH, dec28leap, null).getMaximum(), 30);
        assertEquals(chrono.getRange(DAY_OF_YEAR, march15, null).getMaximum(), 366);
    }

    @DataProvider(name="dateEquivalences")
    Object[][] dateEquivalencesProvider() {
        return new Object[][]{
            {1, 1, 1, 1, LocalDate.of(284, AUGUST, 29)},
            {1728, 7, 6, 186, march15},
            {1728, 4, 18, 108, dec28},
            {1729, 4, 19, 109, dec28leap},
        };
    }
    
    @Test(dataProvider="dateEquivalences", groups={"tck"})
    void coptic_getYear(int year, int month, int day, int doy, LocalDate isoDate) {
        assertEquals(chrono.getDateValue(isoDate, YEAR), year);
    }
    
    @Test(dataProvider="dateEquivalences", groups={"tck"})
    void coptic_getDayOfYear(int year, int month, int day, int doy, LocalDate isoDate) {
        assertEquals(chrono.getDateValue(isoDate, DAY_OF_YEAR), doy);
    }
    
    @Test(dataProvider="dateEquivalences", groups={"tck"})
    void coptic_getMonth(int year, int month, int day, int doy, LocalDate isoDate) {
        assertEquals(chrono.getDateValue(isoDate, MONTH_OF_YEAR), month);
    }
    
    @Test(dataProvider="dateEquivalences", groups={"tck"})
    void coptic_getDayOfMonth(int year, int month, int day, int doy, LocalDate isoDate) {
        assertEquals(chrono.getDateValue(isoDate, DAY_OF_MONTH), day);
    }
    
    @Test(dataProvider="dateEquivalences", groups={"tck"})
    void coptic_setDate(int year, int month, int day, int doy, LocalDate isoDate) {
        LocalDate value = chrono.setDate(LocalDate.now(), YEAR, year);
        value = chrono.setDate(value, MONTH_OF_YEAR, month);
        value = chrono.setDate(value, DAY_OF_MONTH, day);
        assertEquals(value, isoDate);
    }
}
