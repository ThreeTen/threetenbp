/*
 * Copyright (c) 2008-2011, Stephen Colebourne & Michael Nascimento Santos
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

import org.testng.annotations.Test;

/**
 * Test ISO DayOfMonth rule.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestISODayOfMonthRule extends AbstractTestDateTimeRule {

    public TestISODayOfMonthRule() {
        super(LocalDate.of(2009, 12, 26), 26);
    }

    @Override
    protected DateTimeRule rule() {
        return ISODateTimeRule.DAY_OF_MONTH;
    }

    //-----------------------------------------------------------------------
    // Basics
    //-----------------------------------------------------------------------
    public void test_basics() throws Exception {
        DateTimeRule rule = ISODateTimeRule.DAY_OF_MONTH;
        assertEquals(rule.getReifiedType(), DateTimeField.class);
        assertEquals(rule.getID(), "ISO.DayOfMonth");
        assertEquals(rule.getName(), "DayOfMonth");
        assertEquals(rule.getRange(), DateTimeRuleRange.of(1, 28, 31));
        assertEquals(rule.getPeriodUnit(), ISOPeriodUnit.DAYS);
        assertEquals(rule.getPeriodRange(), ISOPeriodUnit.MONTHS);
    }

    //-----------------------------------------------------------------------
    // getMaximumValue(Calendrical)
    //-----------------------------------------------------------------------
    public void test_getMaximumValue_Calendrical_june() {
        Calendrical cal = YearMonth.of(2007, MonthOfYear.JUNE);
        assertEquals(rule().getRange(cal), DateTimeRuleRange.of(1, 30));
    }

    public void test_getMaximumValue_Calendrical_july() {
        Calendrical cal = YearMonth.of(2007, MonthOfYear.JULY);
        assertEquals(rule().getRange(cal), DateTimeRuleRange.of(1, 31));
    }

    public void test_getMaximumValue_Calendrical_febLeap() {
        Calendrical cal = YearMonth.of(2008, MonthOfYear.FEBRUARY);
        assertEquals(rule().getRange(cal), DateTimeRuleRange.of(1, 29));
    }

    public void test_getMaximumValue_Calendrical_febNonLeap() {
        Calendrical cal = YearMonth.of(2007, MonthOfYear.FEBRUARY);
        assertEquals(rule().getRange(cal), DateTimeRuleRange.of(1, 28));
    }

    public void test_getMaximumValue_Calendrical_juneNoYear() {
        Calendrical cal = DateTimeField.of(ISODateTimeRule.MONTH_OF_YEAR, 6);
        assertEquals(rule().getRange(cal), DateTimeRuleRange.of(1, 30));
    }

    public void test_getMaximumValue_Calendrical_julyNoYear() {
        Calendrical cal = DateTimeField.of(ISODateTimeRule.MONTH_OF_YEAR, 7);
        assertEquals(rule().getRange(cal), DateTimeRuleRange.of(1, 31));
    }

    public void test_getMaximumValue_Calendrical_febNoYear() {
        Calendrical cal = DateTimeField.of(ISODateTimeRule.MONTH_OF_YEAR, 2);
        assertEquals(rule().getRange(cal), DateTimeRuleRange.of(1, 28, 29));
    }

    //-----------------------------------------------------------------------
    // getValue(Calendrical)
    //-----------------------------------------------------------------------
    public void test_getValue_Calendrical_date() {
        Calendrical cal = LocalDate.of(2007, 6, 20);
        assertEquals(rule().getValue(cal), rule().field(20));
    }

    public void test_getValue_Calendrical_dateTime() {
        Calendrical cal = LocalDateTime.of(2007, 6, 20, 12, 30);
        assertEquals(rule().getValue(cal), rule().field(20));
    }

    public void test_getValue_Calendrical_monthDay() {
        Calendrical cal = MonthDay.of(6, 20);
        assertEquals(rule().getValue(cal), rule().field(20));
    }

    public void test_getValue_Calendrical_dateTimeFields() {
        Calendrical cal = DateTimeFields.of(rule(), 20);
        assertEquals(rule().getValue(cal), rule().field(20));
    }

}
