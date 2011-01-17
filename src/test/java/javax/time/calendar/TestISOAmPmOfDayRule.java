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
 * Test ISO AmPmOfDay rule.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestISOAmPmOfDayRule extends AbstractTestDateTimeFieldRule {

    public TestISOAmPmOfDayRule() {
        super(LocalDateTime.of(2009, 12, 26, 13, 30, 40, 50), AmPmOfDay.PM, 1);
    }

    @Override
    protected DateTimeFieldRule<AmPmOfDay> rule() {
        return ISOChronology.amPmOfDayRule();
    }

    //-----------------------------------------------------------------------
    // Basics
    //-----------------------------------------------------------------------
    public void test_basics() throws Exception {
        DateTimeFieldRule<AmPmOfDay> rule = ISOChronology.amPmOfDayRule();
        assertEquals(rule.getReifiedType(), AmPmOfDay.class);
        assertEquals(rule.getID(), "ISO.AmPmOfDay");
        assertEquals(rule.getName(), "AmPmOfDay");
        assertEquals(rule.getMinimumValue(), 0);
        assertEquals(rule.getLargestMinimumValue(), 0);
        assertEquals(rule.getMaximumValue(), 1);
        assertEquals(rule.getSmallestMaximumValue(), 1);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), ISOChronology.period12Hours());
        assertEquals(rule.getPeriodRange(), ISOChronology.periodDays());
    }

    public void test_values() throws Exception {
        LocalDateTime dt = LocalDateTime.of(2009, 12, 26, 13, 30, 40, 50);
        for (int i = 0; i < 24; i++) {
            dt = dt.withHourOfDay(i);
            assertEquals(dt.get(rule()), i < 12 ? AmPmOfDay.AM : AmPmOfDay.PM);
        }
    }

    //-----------------------------------------------------------------------
    // convertValueToInt(T)
    //-----------------------------------------------------------------------
    @Override
    public void test_convertValueToInt() {
        assertEquals(rule().convertValueToInt(AmPmOfDay.AM), 0);
        assertEquals(rule().convertValueToInt(AmPmOfDay.PM), 1);
    }

    //-----------------------------------------------------------------------
    // convertIntToValue(int)
    //-----------------------------------------------------------------------
    @Override
    public void test_convertIntToValue() {
        assertEquals(rule().convertIntToValue(0), AmPmOfDay.AM);
        assertEquals(rule().convertIntToValue(1), AmPmOfDay.PM);
    }

    //-----------------------------------------------------------------------
    // getValue(Calendrical)
    //-----------------------------------------------------------------------
    public void test_getValue_Calendrical_time() {
        Calendrical cal = LocalTime.of(13, 30, 40, 50);
        assertEquals(rule().getValue(cal), AmPmOfDay.PM);
    }

    public void test_getValue_Calendrical_dateTime() {
        Calendrical cal = LocalDateTime.of(2009, 12, 26, 13, 30, 40, 50);
        assertEquals(rule().getValue(cal), AmPmOfDay.PM);
    }

    public void test_getValue_Calendrical_dateTimeFields() {
        Calendrical cal = DateTimeFields.of(rule(), 0);
        assertEquals(rule().getValue(cal), AmPmOfDay.AM);
    }

}
