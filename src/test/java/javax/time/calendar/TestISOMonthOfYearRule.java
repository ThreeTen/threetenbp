/*
 * Copyright (c) 2009-2011, Stephen Colebourne & Michael Nascimento Santos
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

import javax.time.calendar.format.MockSimpleCalendrical;

import org.testng.annotations.Test;

/**
 * Test ISO MonthOfYear rule.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestISOMonthOfYearRule extends AbstractTestDateTimeFieldRule {

    public TestISOMonthOfYearRule() {
        super(LocalDate.of(2009, 12, 26), MonthOfYear.DECEMBER, 12);
    }

    @Override
    protected DateTimeFieldRule<MonthOfYear> rule() {
        return ISOChronology.monthOfYearRule();
    }

    //-----------------------------------------------------------------------
    // Basics
    //-----------------------------------------------------------------------
    public void test_basics() throws Exception {
        DateTimeFieldRule<MonthOfYear> rule = ISOChronology.monthOfYearRule();
        assertEquals(rule.getReifiedType(), MonthOfYear.class);
        assertEquals(rule.getID(), "ISO.MonthOfYear");
        assertEquals(rule.getName(), "MonthOfYear");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getMinimumValue(new MockSimpleCalendrical()), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 12);
        assertEquals(rule.getMaximumValue(new MockSimpleCalendrical()), 12);
        assertEquals(rule.getSmallestMaximumValue(), 12);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), ISOPeriodUnit.MONTHS);
        assertEquals(rule.getPeriodRange(), ISOPeriodUnit.YEARS);
    }

    //-----------------------------------------------------------------------
    // convertValueToInt(T)
    //-----------------------------------------------------------------------
    @Override
    public void test_convertValueToInt() {
        assertEquals(rule().convertValueToInt(MonthOfYear.FEBRUARY), 2);
        assertEquals(rule().convertValueToInt(MonthOfYear.JUNE), 6);
    }

    //-----------------------------------------------------------------------
    // convertIntToValue(int)
    //-----------------------------------------------------------------------
    @Override
    public void test_convertIntToValue() {
        assertEquals(rule().convertIntToValue(2), MonthOfYear.FEBRUARY);
        assertEquals(rule().convertIntToValue(6), MonthOfYear.JUNE);
    }

    //-----------------------------------------------------------------------
    // getValue(Calendrical)
    //-----------------------------------------------------------------------
    public void test_getValue_Calendrical_date() {
        Calendrical cal = LocalDate.of(2007, 6, 20);
        assertEquals(rule().getValue(cal), MonthOfYear.JUNE);
    }

    public void test_getValue_Calendrical_dateTime() {
        Calendrical cal = LocalDateTime.of(2007, 6, 20, 12, 30);
        assertEquals(rule().getValue(cal), MonthOfYear.JUNE);
    }

    public void test_getValue_Calendrical_monthDay() {
        Calendrical cal = MonthDay.of(6, 20);
        assertEquals(rule().getValue(cal), MonthOfYear.JUNE);
    }

    public void test_getValue_Calendrical_yearMonth() {
        Calendrical cal = YearMonth.of(2007, 6);
        assertEquals(rule().getValue(cal), MonthOfYear.JUNE);
    }

}
