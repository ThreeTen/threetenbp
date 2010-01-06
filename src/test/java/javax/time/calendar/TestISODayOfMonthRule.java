/*
 * Copyright (c) 2008-2010, Stephen Colebourne & Michael Nascimento Santos
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

import javax.time.calendar.field.MonthOfYear;

import org.testng.annotations.Test;

/**
 * Test ISO DayOfMonth rule.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestISODayOfMonthRule extends AbstractTestDateTimeFieldRule {

    public TestISODayOfMonthRule() {
        super(LocalDate.date(2009, 12, 26), 26, 26);
    }

    @Override
    protected DateTimeFieldRule<Integer> rule() {
        return ISOChronology.dayOfMonthRule();
    }

    //-----------------------------------------------------------------------
    // Basics
    //-----------------------------------------------------------------------
    public void test_basics() throws Exception {
        DateTimeFieldRule<Integer> rule = ISOChronology.dayOfMonthRule();
        assertEquals(rule.getReifiedType(), Integer.class);
        assertEquals(rule.getID(), "ISO.DayOfMonth");
        assertEquals(rule.getName(), "DayOfMonth");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getMinimumValue(LocalDate.date(2007, 6, 20)), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 31);
        assertEquals(rule.getSmallestMaximumValue(), 28);
        assertEquals(rule.isFixedValueSet(), false);
        assertEquals(rule.getPeriodUnit(), ISOChronology.periodDays());
        assertEquals(rule.getPeriodRange(), ISOChronology.periodMonths());
    }

    //-----------------------------------------------------------------------
    // getMaximumValue(Calendrical)
    //-----------------------------------------------------------------------
    public void test_getMaximumValue_Calendrical_june() {
        Calendrical cal = YearMonth.yearMonth(2007, MonthOfYear.JUNE);
        assertEquals(rule().getMaximumValue(cal), 30);
    }

    public void test_getMaximumValue_Calendrical_july() {
        Calendrical cal = YearMonth.yearMonth(2007, MonthOfYear.JULY);
        assertEquals(rule().getMaximumValue(cal), 31);
    }

    public void test_getMaximumValue_Calendrical_febLeap() {
        Calendrical cal = YearMonth.yearMonth(2008, MonthOfYear.FEBRUARY);
        assertEquals(rule().getMaximumValue(cal), 29);
    }

    public void test_getMaximumValue_Calendrical_febNonLeap() {
        Calendrical cal = YearMonth.yearMonth(2007, MonthOfYear.FEBRUARY);
        assertEquals(rule().getMaximumValue(cal), 28);
    }

    public void test_getMaximumValue_Calendrical_juneNoYear() {
        Calendrical cal = MonthOfYear.JUNE;
        assertEquals(rule().getMaximumValue(cal), 30);
    }

    public void test_getMaximumValue_Calendrical_julyNoYear() {
        Calendrical cal = MonthOfYear.JULY;
        assertEquals(rule().getMaximumValue(cal), 31);
    }

    public void test_getMaximumValue_Calendrical_febNoYear() {
        Calendrical cal = MonthOfYear.FEBRUARY;
        assertEquals(rule().getMaximumValue(cal), 29);
    }

    //-----------------------------------------------------------------------
    // getValue(Calendrical)
    //-----------------------------------------------------------------------
    public void test_getValue_Calendrical_date() {
        Calendrical cal = LocalDate.date(2007, 6, 20);
        assertEquals(rule().getValue(cal), (Integer) 20);
    }

    public void test_getValue_Calendrical_dateTime() {
        Calendrical cal = LocalDateTime.dateTime(2007, 6, 20, 12, 30);
        assertEquals(rule().getValue(cal), (Integer) 20);
    }

    public void test_getValue_Calendrical_monthDay() {
        Calendrical cal = MonthDay.monthDay(6, 20);
        assertEquals(rule().getValue(cal), (Integer) 20);
    }

    public void test_getValue_Calendrical_dateTimeFields() {
        Calendrical cal = DateTimeFields.fields(rule(), 20);
        assertEquals(rule().getValue(cal), (Integer) 20);
    }

}
