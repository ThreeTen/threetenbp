/*
 * Copyright (c) 2011, Stephen Colebourne & Michael Nascimento Santos
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
 * Test ISO MinuteOfDay rule.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestISOMinuteOfDayRule extends AbstractTestDateTimeRule {

    public TestISOMinuteOfDayRule() {
        super(LocalDateTime.of(2009, 12, 26, 13, 30, 40, 50), 13 * 60 + 30);
    }

    @Override
    protected DateTimeRule rule() {
        return ISODateTimeRule.MINUTE_OF_DAY;
    }

    //-----------------------------------------------------------------------
    // Basics
    //-----------------------------------------------------------------------
    public void test_basics() throws Exception {
        DateTimeRule rule = ISODateTimeRule.MINUTE_OF_DAY;
        assertEquals(rule.getReifiedType(), DateTimeField.class);
        assertEquals(rule.getID(), "ISO.MinuteOfDay");
        assertEquals(rule.getName(), "MinuteOfDay");
        assertEquals(rule.getRange(), DateTimeRuleRange.of(0, 24 * 60 - 1));
        assertEquals(rule.getPeriodUnit(), ISOPeriodUnit.MINUTES);
        assertEquals(rule.getPeriodRange(), ISOPeriodUnit.DAYS);
    }

    public void test_values() throws Exception {
        for (int i = 0; i < (24 * 60); i++) {
            LocalTime time = LocalTime.ofSecondOfDay(i * 60);
            assertEquals(time.get(rule()), rule().field(i));
        }
    }

    //-----------------------------------------------------------------------
    // getValue(Calendrical)
    //-----------------------------------------------------------------------
    public void test_getValue_Calendrical_time() {
        Calendrical cal = LocalTime.of(13, 30, 40, 50);
        assertEquals(rule().getValue(cal), rule().field(13 * 60 + 30));
    }

    public void test_getValue_Calendrical_dateTime() {
        Calendrical cal = LocalDateTime.of(2009, 12, 26, 13, 30, 40, 50);
        assertEquals(rule().getValue(cal), rule().field(13 * 60 + 30));
    }

    public void test_getValue_Calendrical_dateTimeFields() {
        Calendrical cal = DateTimeFields.of(rule(), 11);
        assertEquals(rule().getValue(cal), rule().field(11));
    }

}
