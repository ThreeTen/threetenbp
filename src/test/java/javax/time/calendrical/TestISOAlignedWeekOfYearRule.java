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
package javax.time.calendrical;

import static org.testng.Assert.assertEquals;

import javax.time.LocalDate;
import javax.time.LocalDateTime;

import org.testng.annotations.Test;

/**
 * Test ISO AlignedWeekOfYear rule.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestISOAlignedWeekOfYearRule extends AbstractTestDateTimeRule {

    public TestISOAlignedWeekOfYearRule() {
        super(LocalDate.of(2009, 12, 26), 52);
    }

    @Override
    protected DateTimeRule rule() {
        return ISODateTimeRule.ALIGNED_WEEK_OF_YEAR;
    }

    //-----------------------------------------------------------------------
    // Basics
    //-----------------------------------------------------------------------
    public void test_basics() throws Exception {
        DateTimeRule rule = ISODateTimeRule.ALIGNED_WEEK_OF_YEAR;
        assertEquals(rule.getType(), DateTimeField.class);
        assertEquals(rule.getName(), "AlignedWeekOfYear");
        assertEquals(rule.getValueRange(), DateTimeRuleRange.of(1, 53));
        assertEquals(rule.getPeriodUnit(), ISOPeriodUnit.WEEKS);
        assertEquals(rule.getPeriodRange(), ISOPeriodUnit.YEARS);
    }

    //-----------------------------------------------------------------------
    // getValue(Calendrical)
    //-----------------------------------------------------------------------
    public void test_getValue_Calendrical_date_nonLeap() {
        LocalDate date = LocalDate.of(2007, 1, 1);
        for (int i = 0; i < 365; i++) {
            int week = (i / 7) + 1;
            assertEquals(rule().getValue(date), rule().field(week));
            date = date.plusDays(1);
        }
    }

    public void test_getValue_Calendrical_date_leap() {
        LocalDate date = LocalDate.of(2008, 1, 1);
        for (int i = 0; i < 366; i++) {
            int week = (i / 7) + 1;
            assertEquals(rule().getValue(date), rule().field(week));
            date = date.plusDays(1);
        }
    }

    public void test_getValue_Calendrical_dateTime() {
        assertEquals(rule().getValue(LocalDateTime.of(2007, 1, 20, 12, 30)), rule().field(3));
    }

}
