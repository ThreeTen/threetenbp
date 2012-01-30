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
package javax.time.calendrical;

import static org.testng.Assert.assertEquals;

import javax.time.LocalDate;
import javax.time.LocalDateTime;

import org.testng.annotations.Test;

/**
 * Test ISO DayOfYear rule.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestISODayOfYearRule extends AbstractTestDateTimeRule {

    public TestISODayOfYearRule() {
        super(LocalDate.of(2009, 12, 26), 360);
    }

    @Override
    protected DateTimeRule rule() {
        return ISODateTimeRule.DAY_OF_YEAR;
    }

    //-----------------------------------------------------------------------
    // Basics
    //-----------------------------------------------------------------------
    public void test_basics() throws Exception {
        DateTimeRule rule = ISODateTimeRule.DAY_OF_YEAR;
        assertEquals(rule.getType(), DateTimeField.class);
        assertEquals(rule.getName(), "DayOfYear");
        assertEquals(rule.getValueRange(), DateTimeRuleRange.of(1, 365, 366));
        assertEquals(rule.getPeriodUnit(), ISOPeriodUnit.DAYS);
        assertEquals(rule.getPeriodRange(), ISOPeriodUnit.YEARS);
    }

    //-----------------------------------------------------------------------
    // getMaximumValue(Calendrical)
    //-----------------------------------------------------------------------
    public void test_getMaximumValue_Calendrical_leap() {
        Calendrical cal = LocalDate.of(2008, 1, 1);
        assertEquals(rule().getValueRange(cal), DateTimeRuleRange.of(1, 366));
    }

    public void test_getMaximumValue_Calendrical_nonLeap() {
        Calendrical cal = LocalDate.of(2007, 1, 1);
        assertEquals(rule().getValueRange(cal), DateTimeRuleRange.of(1, 365));
    }

    //-----------------------------------------------------------------------
    // getValue(Calendrical)
    //-----------------------------------------------------------------------
    public void test_getValue_Calendrical_date() {
        Calendrical cal = LocalDate.of(2007, 6, 20);
        assertEquals(rule().getValue(cal), rule().field(31 + 28 + 31 + 30 + 31 + 20));
    }

    public void test_getValue_Calendrical_dateTime() {
        Calendrical cal = LocalDateTime.of(2007, 6, 20, 12, 30);
        assertEquals(rule().getValue(cal), rule().field(31 + 28 + 31 + 30 + 31 + 20));
    }

}
