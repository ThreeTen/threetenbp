/*
 * Copyright (c) 2010-2011, Stephen Colebourne & Michael Nascimento Santos
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
 * Test ISO MonthOfQuarter rule.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestISOMonthOfQuarterRule extends AbstractTestDateTimeRule {

    public TestISOMonthOfQuarterRule() {
        super(LocalDate.of(2009, 12, 26), 3);
    }

    @Override
    protected DateTimeRule rule() {
        return ISODateTimeRule.MONTH_OF_QUARTER;
    }

    //-----------------------------------------------------------------------
    // Basics
    //-----------------------------------------------------------------------
    public void test_basics() throws Exception {
        DateTimeRule rule = ISODateTimeRule.MONTH_OF_QUARTER;
        assertEquals(rule.getReifiedType(), DateTimeField.class);
        assertEquals(rule.getName(), "MonthOfQuarter");
        assertEquals(rule.getRange(), DateTimeRuleRange.of(1, 3));
        assertEquals(rule.getPeriodUnit(), ISOPeriodUnit.MONTHS);
        assertEquals(rule.getPeriodRange(), ISOPeriodUnit.QUARTERS);
    }

    //-----------------------------------------------------------------------
    // getValue(Calendrical)
    //-----------------------------------------------------------------------
    public void test_getValue_Calendrical_date() {
        assertEquals(rule().getValue(LocalDate.of(2007, 1, 20)), rule().field(1));
        assertEquals(rule().getValue(LocalDate.of(2007, 2, 20)), rule().field(2));
        assertEquals(rule().getValue(LocalDate.of(2007, 3, 20)), rule().field(3));
        assertEquals(rule().getValue(LocalDate.of(2007, 4, 20)), rule().field(1));
        assertEquals(rule().getValue(LocalDate.of(2007, 5, 20)), rule().field(2));
        assertEquals(rule().getValue(LocalDate.of(2007, 6, 20)), rule().field(3));
        assertEquals(rule().getValue(LocalDate.of(2007, 7, 20)), rule().field(1));
        assertEquals(rule().getValue(LocalDate.of(2007, 8, 20)), rule().field(2));
        assertEquals(rule().getValue(LocalDate.of(2007, 9, 20)), rule().field(3));
        assertEquals(rule().getValue(LocalDate.of(2007, 10, 20)), rule().field(1));
        assertEquals(rule().getValue(LocalDate.of(2007, 11, 20)), rule().field(2));
        assertEquals(rule().getValue(LocalDate.of(2007, 12, 20)), rule().field(3));
    }

    public void test_getValue_Calendrical_dateTime() {
        assertEquals(rule().getValue(LocalDateTime.of(2007, 1, 20, 12, 30)), rule().field(1));
        assertEquals(rule().getValue(LocalDateTime.of(2007, 2, 20, 12, 30)), rule().field(2));
        assertEquals(rule().getValue(LocalDateTime.of(2007, 3, 20, 12, 30)), rule().field(3));
        assertEquals(rule().getValue(LocalDateTime.of(2007, 4, 20, 12, 30)), rule().field(1));
        assertEquals(rule().getValue(LocalDateTime.of(2007, 5, 20, 12, 30)), rule().field(2));
        assertEquals(rule().getValue(LocalDateTime.of(2007, 6, 20, 12, 30)), rule().field(3));
        assertEquals(rule().getValue(LocalDateTime.of(2007, 7, 20, 12, 30)), rule().field(1));
        assertEquals(rule().getValue(LocalDateTime.of(2007, 8, 20, 12, 30)), rule().field(2));
        assertEquals(rule().getValue(LocalDateTime.of(2007, 9, 20, 12, 30)), rule().field(3));
        assertEquals(rule().getValue(LocalDateTime.of(2007, 10, 20, 12, 30)), rule().field(1));
        assertEquals(rule().getValue(LocalDateTime.of(2007, 11, 20, 12, 30)), rule().field(2));
        assertEquals(rule().getValue(LocalDateTime.of(2007, 12, 20, 12, 30)), rule().field(3));
    }

    public void test_getValue_Calendrical_yearMonth() {
        Calendrical cal = YearMonth.of(1966, 6);
        assertEquals(rule().getValue(cal), rule().field(3));
    }

}
