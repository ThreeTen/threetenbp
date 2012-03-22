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

import static javax.time.MonthOfYear.DECEMBER;
import static javax.time.MonthOfYear.MARCH;
import static org.testng.Assert.assertEquals;

import javax.time.LocalDate;
import javax.time.calendrical.DateTimeRuleRange;

import org.testng.annotations.Test;

@Test
public class TestISOChrono {

    ISOChrono chrono = ISOChrono.INSTANCE;
    LocalDate march15 = LocalDate.of(2012, MARCH, 15);
    LocalDate dec28 = LocalDate.of(2011, DECEMBER, 28);
    LocalDate dec28leap = LocalDate.of(2012, DECEMBER, 28);

    //-----------------------------------------------------------------------
    @Test(groups = "tck")
    public void era_values() {
        assertEquals(chrono.getDateValue(march15, StandardDateTimeField.ERA), 1);
        assertEquals(chrono.setDate(march15, StandardDateTimeField.ERA, 1), march15);
        assertEquals(chrono.setDate(march15, StandardDateTimeField.ERA, 0), LocalDate.of(-2011, MARCH, 15));
    }

    @Test(groups = "tck")
    public void era_range() {
        assertEquals(chrono.getDateValueRange(StandardDateTimeField.ERA, null), DateTimeRuleRange.of(0, 1));
        assertEquals(chrono.getDateValueRange(StandardDateTimeField.ERA, march15), DateTimeRuleRange.of(0, 1));
    }

    //-----------------------------------------------------------------------
    @Test(groups = "tck")
    public void year_values() {
        assertEquals(chrono.getDateValue(march15, StandardDateTimeField.ERA), 1);
        assertEquals(chrono.setDate(march15, StandardDateTimeField.YEAR, 2012), march15);
        assertEquals(chrono.setDate(march15, StandardDateTimeField.YEAR, 2010), LocalDate.of(2010, MARCH, 15));
    }

    @Test(groups = "tck")
    public void year_range() {
        assertEquals(chrono.getDateValueRange(StandardDateTimeField.YEAR, null), DateTimeRuleRange.of(-999999998, 999999999));
        assertEquals(chrono.getDateValueRange(StandardDateTimeField.YEAR, march15), DateTimeRuleRange.of(-999999998, 999999999));
    }

    //-----------------------------------------------------------------------
    @Test(groups = "tck")
    public void monthOfYear_values() {
        assertEquals(chrono.getDateValue(march15, StandardDateTimeField.MONTH_OF_YEAR), 3);
        assertEquals(chrono.setDate(march15, StandardDateTimeField.MONTH_OF_YEAR, 3), march15);
        assertEquals(chrono.setDate(march15, StandardDateTimeField.MONTH_OF_YEAR, 2), LocalDate.of(2012, 2, 15));
    }

    @Test(groups = "tck")
    public void monthOfYear_range() {
        assertEquals(chrono.getDateValueRange(StandardDateTimeField.MONTH_OF_YEAR, null), DateTimeRuleRange.of(1, 12));
        assertEquals(chrono.getDateValueRange(StandardDateTimeField.MONTH_OF_YEAR, march15), DateTimeRuleRange.of(1, 12));
    }

    //-----------------------------------------------------------------------
    @Test(groups = "tck")
    public void dayOfMonth_values() {
        assertEquals(chrono.getDateValue(march15, StandardDateTimeField.DAY_OF_MONTH), 15);
        assertEquals(chrono.setDate(march15, StandardDateTimeField.DAY_OF_MONTH, 15), march15);
        assertEquals(chrono.setDate(march15, StandardDateTimeField.DAY_OF_MONTH, 20), LocalDate.of(2012, 3, 20));
    }

    @Test(groups = "tck")
    public void dayOfMonth_range() {
        assertEquals(chrono.getDateValueRange(StandardDateTimeField.DAY_OF_MONTH, null), DateTimeRuleRange.of(1, 28, 31));
        assertEquals(chrono.getDateValueRange(StandardDateTimeField.DAY_OF_MONTH, LocalDate.of(2011, 2, 20)), DateTimeRuleRange.of(1, 28));
        assertEquals(chrono.getDateValueRange(StandardDateTimeField.DAY_OF_MONTH, LocalDate.of(2012, 2, 20)), DateTimeRuleRange.of(1, 29));
        assertEquals(chrono.getDateValueRange(StandardDateTimeField.DAY_OF_MONTH, LocalDate.of(2012, 3, 20)), DateTimeRuleRange.of(1, 31));
        assertEquals(chrono.getDateValueRange(StandardDateTimeField.DAY_OF_MONTH, LocalDate.of(2012, 4, 20)), DateTimeRuleRange.of(1, 30));
    }

}
