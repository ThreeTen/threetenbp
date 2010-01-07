/*
 * Copyright (c) 2009-2010, Stephen Colebourne & Michael Nascimento Santos
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

import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.format.MockSimpleCalendrical;

import org.testng.annotations.Test;

/**
 * Test ISO DayOfWeek rule.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestISODayOfWeekRule extends AbstractTestDateTimeFieldRule {

    public TestISODayOfWeekRule() {
        super(LocalDate.of(2009, 12, 26), DayOfWeek.SATURDAY, 6);
    }

    @Override
    protected DateTimeFieldRule<DayOfWeek> rule() {
        return ISOChronology.dayOfWeekRule();
    }

    //-----------------------------------------------------------------------
    // Basics
    //-----------------------------------------------------------------------
    public void test_basics() throws Exception {
        DateTimeFieldRule<DayOfWeek> rule = ISOChronology.dayOfWeekRule();
        assertEquals(rule.getReifiedType(), DayOfWeek.class);
        assertEquals(rule.getID(), "ISO.DayOfWeek");
        assertEquals(rule.getName(), "DayOfWeek");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getMinimumValue(new MockSimpleCalendrical()), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 7);
        assertEquals(rule.getMaximumValue(new MockSimpleCalendrical()), 7);
        assertEquals(rule.getSmallestMaximumValue(), 7);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), ISOChronology.periodDays());
        assertEquals(rule.getPeriodRange(), ISOChronology.periodWeeks());
    }

    //-----------------------------------------------------------------------
    // convertValueToInt(T)
    //-----------------------------------------------------------------------
    @Override
    public void test_convertValueToInt() {
        assertEquals(rule().convertValueToInt(DayOfWeek.MONDAY), 1);
        assertEquals(rule().convertValueToInt(DayOfWeek.SATURDAY), 6);
    }

    //-----------------------------------------------------------------------
    // convertIntToValue(int)
    //-----------------------------------------------------------------------
    @Override
    public void test_convertIntToValue() {
        assertEquals(rule().convertIntToValue(1), DayOfWeek.MONDAY);
        assertEquals(rule().convertIntToValue(6), DayOfWeek.SATURDAY);
    }

    //-----------------------------------------------------------------------
    // getValue(Calendrical)
    //-----------------------------------------------------------------------
    public void test_getValue_Calendrical_date() {
        Calendrical cal = LocalDate.of(2009, 12, 26);
        assertEquals(rule().getValue(cal), DayOfWeek.SATURDAY);
    }

    public void test_getValue_Calendrical_dateTime() {
        Calendrical cal = LocalDateTime.of(2009, 12, 26, 12, 30);
        assertEquals(rule().getValue(cal), DayOfWeek.SATURDAY);
    }

}
