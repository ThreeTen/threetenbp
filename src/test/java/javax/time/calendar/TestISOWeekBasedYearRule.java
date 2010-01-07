/*
 * Copyright (c) 2010, Stephen Colebourne & Michael Nascimento Santos
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
 * Test ISO WeekOfWeekBasedYear rule.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestISOWeekBasedYearRule extends AbstractTestDateTimeFieldRule {

    public TestISOWeekBasedYearRule() {
        super(LocalDate.date(2008, 12, 30), 2009, 2009);
    }

    @Override
    protected DateTimeFieldRule<Integer> rule() {
        return ISOChronology.weekBasedYearRule();
    }

    //-----------------------------------------------------------------------
    // Basics
    //-----------------------------------------------------------------------
    public void test_basics() throws Exception {
        DateTimeFieldRule<Integer> rule = ISOChronology.weekBasedYearRule();
        assertEquals(rule.getReifiedType(), Integer.class);
        assertEquals(rule.getID(), "ISO.WeekBasedYear");
        assertEquals(rule.getName(), "WeekBasedYear");
        assertEquals(rule.getMinimumValue(), ISOChronology.MIN_WEEK_BASED_YEAR);
        assertEquals(rule.getMinimumValue(LocalDate.date(2007, 6, 20)), ISOChronology.MIN_WEEK_BASED_YEAR);
        assertEquals(rule.getLargestMinimumValue(), ISOChronology.MIN_WEEK_BASED_YEAR);
        assertEquals(rule.getMaximumValue(), ISOChronology.MAX_WEEK_BASED_YEAR);
        assertEquals(rule.getMaximumValue(LocalDate.date(2007, 6, 20)), ISOChronology.MAX_WEEK_BASED_YEAR);
        assertEquals(rule.getSmallestMaximumValue(), ISOChronology.MAX_WEEK_BASED_YEAR);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), ISOChronology.periodWeekBasedYears());
        assertEquals(rule.getPeriodRange(), null);
    }

    //-----------------------------------------------------------------------
    // getValue(Calendrical)
    //-----------------------------------------------------------------------
    public void test_getValue_Calendrical_date_nonLeap_52weeks() {
        LocalDate date = LocalDate.date(2007, 1, 1);  // 2007-W1-1
        for (int i = 0; i < 364; i++) {  // 52 weeks
            assertEquals(rule().getValue(date), (Integer) 2007);
            date = date.plusDays(1);
        }
        assertEquals(date, LocalDate.date(2007, 12, 31));
        assertEquals(rule().getValue(date), (Integer) 2008);  // 2008-W1-1
    }

    public void test_getValue_Calendrical_date_leap_52weeks() {
        LocalDate date = LocalDate.date(2007, 12, 31);  // 2008-W1-1
        for (int i = 0; i < 364; i++) {  // 52 weeks
            assertEquals(rule().getValue(date), (Integer) 2008);
            date = date.plusDays(1);
        }
        assertEquals(date, LocalDate.date(2008, 12, 29));
        assertEquals(rule().getValue(date), (Integer) 2009);  // 2009-W1-1
    }

    public void test_getValue_Calendrical_date_nonLeap_53weeks() {
        LocalDate date = LocalDate.date(2008, 12, 29);  // 2009-W1-1
        for (int i = 0; i < 371; i++) {  // 53 weeks
            assertEquals(rule().getValue(date), (Integer) 2009);
            date = date.plusDays(1);
        }
        assertEquals(date, LocalDate.date(2010, 1, 4));
        assertEquals(rule().getValue(date), (Integer) 2010);  // 2010-W1-1
    }

    public void test_getValue_Calendrical_dateTime() {
        assertEquals(rule().getValue(LocalDateTime.dateTime(2007, 1, 20, 12, 30)), (Integer) 2007);
    }

}
