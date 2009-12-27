/*
 * Copyright (c) 2009, Stephen Colebourne & Michael Nascimento Santos
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

import static org.testng.Assert.assertEquals;

import javax.time.calendar.field.Year;
import javax.time.calendar.format.MockSimpleCalendrical;
import javax.time.period.PeriodUnits;

import org.testng.annotations.Test;

/**
 * Test ISO Year rule.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestISOYearRule extends AbstractTestDateTimeFieldRule {

    public TestISOYearRule() {
        super(LocalDate.date(2009, 12, 26), 2009, 2009);
    }

    @Override
    protected DateTimeFieldRule<Integer> rule() {
        return ISOChronology.yearRule();
    }

    //-----------------------------------------------------------------------
    // Basics
    //-----------------------------------------------------------------------
    public void test_basics() throws Exception {
        DateTimeFieldRule<Integer> rule = ISOChronology.yearRule();
        assertEquals(rule.getReifiedType(), Integer.class);
        assertEquals(rule.getID(), "ISO.Year");
        assertEquals(rule.getName(), "Year");
        assertEquals(rule.getMinimumValue(), Year.MIN_YEAR);
        assertEquals(rule.getMinimumValue(new MockSimpleCalendrical()), Year.MIN_YEAR);
        assertEquals(rule.getLargestMinimumValue(), Year.MIN_YEAR);
        assertEquals(rule.getMaximumValue(), Year.MAX_YEAR);
        assertEquals(rule.getMaximumValue(new MockSimpleCalendrical()), Year.MAX_YEAR);
        assertEquals(rule.getSmallestMaximumValue(), Year.MAX_YEAR);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.YEARS);
        assertEquals(rule.getPeriodRange(), null);
    }

    //-----------------------------------------------------------------------
    // getValue(Calendrical)
    //-----------------------------------------------------------------------
    public void test_getValue_Calendrical_date() {
        Calendrical cal = LocalDate.date(2007, 6, 20);
        assertEquals(rule().getValue(cal), (Integer) 2007);
    }

    public void test_getValue_Calendrical_dateTime() {
        Calendrical cal = LocalDateTime.dateTime(2007, 6, 20, 12, 30);
        assertEquals(rule().getValue(cal), (Integer) 2007);
    }

    public void test_getValue_Calendrical_yearMonth() {
        Calendrical cal = YearMonth.yearMonth(2007, 6);
        assertEquals(rule().getValue(cal), (Integer) 2007);
    }

}
