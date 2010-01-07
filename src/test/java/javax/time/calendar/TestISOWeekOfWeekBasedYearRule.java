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

import javax.time.calendar.field.DayOfWeek;

import org.testng.annotations.Test;

/**
 * Test ISO WeekOfWeekBasedYear rule.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestISOWeekOfWeekBasedYearRule extends AbstractTestDateTimeFieldRule {

    public TestISOWeekOfWeekBasedYearRule() {
        super(LocalDate.of(2009, 12, 26), 52, 52);
    }

    @Override
    protected DateTimeFieldRule<Integer> rule() {
        return ISOChronology.weekOfWeekBasedYearRule();
    }

    //-----------------------------------------------------------------------
    // Basics
    //-----------------------------------------------------------------------
    public void test_basics() throws Exception {
        DateTimeFieldRule<Integer> rule = ISOChronology.weekOfWeekBasedYearRule();
        assertEquals(rule.getReifiedType(), Integer.class);
        assertEquals(rule.getID(), "ISO.WeekOfWeekBasedYear");
        assertEquals(rule.getName(), "WeekOfWeekBasedYear");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getMinimumValue(LocalDate.of(2007, 6, 20)), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 53);
        assertEquals(rule.getSmallestMaximumValue(), 52);
        assertEquals(rule.isFixedValueSet(), false);
        assertEquals(rule.getPeriodUnit(), ISOChronology.periodWeeks());
        assertEquals(rule.getPeriodRange(), ISOChronology.periodWeekBasedYears());
    }

    //-----------------------------------------------------------------------
    // getMaximumValue(Calendrical)
    //-----------------------------------------------------------------------
    // TODO: Test derive from WeekBasedYear
    
    public void test_getMaximumValue_Calendrical_loop() {
        LocalDate date = LocalDate.of(1900, 1, 1);
        while (date.getYear() <= 2100) {
            if (date.getDayOfWeek() == DayOfWeek.THURSDAY ) {
                assertEquals(rule().getMaximumValue(date), 53);
            } else if (date.getDayOfWeek() == DayOfWeek.WEDNESDAY && date.toYear().isLeap()) {
                assertEquals(rule().getMaximumValue(date), 53);
            } else {
                assertEquals(rule().getMaximumValue(date), 52);
            }
            date = date.plusYears(1);
        }
    }

    public void test_getMaximumValue_Calendrical_sample() {
        assertEquals(rule().getMaximumValue(LocalDate.of(2007, 6, 1)), 52);
        assertEquals(rule().getMaximumValue(LocalDate.of(2008, 6, 1)), 52);
        assertEquals(rule().getMaximumValue(LocalDate.of(2009, 6, 1)), 53);
        assertEquals(rule().getMaximumValue(LocalDate.of(2010, 6, 1)), 52);
        assertEquals(rule().getMaximumValue(LocalDate.of(2011, 6, 1)), 52);
    }

    //-----------------------------------------------------------------------
    // getValue(Calendrical)
    //-----------------------------------------------------------------------
    public void test_getValue_Calendrical_date_nonLeap_52weeks() {
        LocalDate date = LocalDate.of(2007, 1, 1);  // 2007-W1-1
        for (int i = 0; i < 364; i++) {  // 52 weeks
            int week = (i / 7) + 1;
            assertEquals(rule().getValue(date), (Integer) week);
            date = date.plusDays(1);
        }
        assertEquals(date, LocalDate.of(2007, 12, 31));
        assertEquals(rule().getValue(date), (Integer) 1);  // 2008-W1-1
    }

    public void test_getValue_Calendrical_date_leap_52weeks() {
        LocalDate date = LocalDate.of(2007, 12, 31);  // 2008-W1-1
        for (int i = 0; i < 364; i++) {  // 52 weeks
            int week = (i / 7) + 1;
            assertEquals(rule().getValue(date), (Integer) week);
            date = date.plusDays(1);
        }
        assertEquals(date, LocalDate.of(2008, 12, 29));
        assertEquals(rule().getValue(date), (Integer) 1);  // 2009-W1-1
    }

    public void test_getValue_Calendrical_date_nonLeap_53weeks() {
        LocalDate date = LocalDate.of(2008, 12, 29);  // 2009-W1-1
        for (int i = 0; i < 371; i++) {  // 53 weeks
            int week = (i / 7) + 1;
            assertEquals(rule().getValue(date), (Integer) week);
            date = date.plusDays(1);
        }
        assertEquals(date, LocalDate.of(2010, 1, 4));
        assertEquals(rule().getValue(date), (Integer) 1);  // 2010-W1-1
    }

    public void test_getValue_Calendrical_dateTime() {
        assertEquals(rule().getValue(LocalDateTime.of(2007, 1, 20, 12, 30)), (Integer) 3);
    }

}
