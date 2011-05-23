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

import static javax.time.calendar.ISODateTimeRule.CLOCK_HOUR_OF_AMPM;
import static javax.time.calendar.ISODateTimeRule.CLOCK_HOUR_OF_DAY;
import static javax.time.calendar.ISODateTimeRule.DAY_OF_MONTH;
import static javax.time.calendar.ISODateTimeRule.DAY_OF_WEEK;
import static javax.time.calendar.ISODateTimeRule.DAY_OF_YEAR;
import static javax.time.calendar.ISODateTimeRule.EPOCH_MONTH;
import static javax.time.calendar.ISODateTimeRule.HOUR_OF_AMPM;
import static javax.time.calendar.ISODateTimeRule.HOUR_OF_DAY;
import static javax.time.calendar.ISODateTimeRule.MINUTE_OF_DAY;
import static javax.time.calendar.ISODateTimeRule.MINUTE_OF_HOUR;
import static javax.time.calendar.ISODateTimeRule.MONTH_OF_QUARTER;
import static javax.time.calendar.ISODateTimeRule.MONTH_OF_YEAR;
import static javax.time.calendar.ISODateTimeRule.QUARTER_OF_YEAR;
import static javax.time.calendar.ISODateTimeRule.YEAR;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test merging of ISO fields.
 */
@Test
public class TestISOMerge {

    //-----------------------------------------------------------------------
    @DataProvider(name = "normalize")
    public Object[][] data_normalize() {
        return new Object[][] {
            {DateTimeFields.of(YEAR, 2011), DateTimeFields.of(YEAR, 2011)},
            {DateTimeFields.of(QUARTER_OF_YEAR, 2), DateTimeFields.of(QUARTER_OF_YEAR, 2)},
            {DateTimeFields.of(MONTH_OF_YEAR, 6), DateTimeFields.of(MONTH_OF_YEAR, 6)},
            {DateTimeFields.of(MONTH_OF_QUARTER, 3), DateTimeFields.of(MONTH_OF_QUARTER, 3)},
            {DateTimeFields.of(DAY_OF_YEAR, 34), DateTimeFields.of(DAY_OF_YEAR, 34)},
            {DateTimeFields.of(DAY_OF_MONTH, 4), DateTimeFields.of(DAY_OF_MONTH, 4)},
            {DateTimeFields.of(DAY_OF_WEEK, 6), DateTimeFields.of(DAY_OF_WEEK, 6)},
            
            {DateTimeFields.of(MONTH_OF_YEAR, 6, DAY_OF_MONTH, 2), DateTimeFields.of(MONTH_OF_YEAR, 6, DAY_OF_MONTH, 2)},
            {DateTimeFields.of(YEAR, 2011, DAY_OF_MONTH, 2), DateTimeFields.of(YEAR, 2011, DAY_OF_MONTH, 2)},
            {DateTimeFields.of(DAY_OF_MONTH, 13, DAY_OF_WEEK, 5), DateTimeFields.of(DAY_OF_MONTH, 13, DAY_OF_WEEK, 5)},
            
            {DateTimeFields.of(HOUR_OF_DAY, 15), DateTimeFields.of(HOUR_OF_DAY, 15)},
            {DateTimeFields.of(HOUR_OF_AMPM, 5), DateTimeFields.of(HOUR_OF_AMPM, 5)},
            
            {DateTimeFields.of(CLOCK_HOUR_OF_DAY, 15), DateTimeFields.of(HOUR_OF_DAY, 15)},
            {DateTimeFields.of(CLOCK_HOUR_OF_DAY, 24), DateTimeFields.of(HOUR_OF_DAY, 0)},
            {DateTimeFields.of(CLOCK_HOUR_OF_AMPM, 5), DateTimeFields.of(HOUR_OF_AMPM, 5)},
            {DateTimeFields.of(CLOCK_HOUR_OF_AMPM, 12), DateTimeFields.of(HOUR_OF_AMPM, 0)},
            
            {DateTimeFields.of(CLOCK_HOUR_OF_DAY, 15, DAY_OF_MONTH, 2), DateTimeFields.of(HOUR_OF_DAY, 15, DAY_OF_MONTH, 2)},
            {DateTimeFields.of(CLOCK_HOUR_OF_DAY, 15, HOUR_OF_DAY, 15), DateTimeFields.of(HOUR_OF_DAY, 15)},
            
            {DateTimeFields.of(HOUR_OF_DAY, 14, MINUTE_OF_HOUR, 3), DateTimeFields.of(MINUTE_OF_DAY, 14 * 60 + 3)},
            {DateTimeFields.of(QUARTER_OF_YEAR, 2, MONTH_OF_QUARTER, 3), DateTimeFields.of(MONTH_OF_YEAR, 6)},
            {DateTimeFields.of(YEAR, 2011, MONTH_OF_YEAR, 3), DateTimeFields.of(EPOCH_MONTH, 41 * 12 + 3 - 1)},
        };
    }

    @Test(dataProvider = "normalize")
    public void test_merge2(DateTimeFields input, DateTimeFields output) {
        assertEquals(input.normalized(), output);
    }

//    //-----------------------------------------------------------------------
//    @DataProvider(name = "merge2")
//    public Object[][] data_merge2() {
//        return new Object[][] {
//            {DateTimeFields.of(DAY_OF_YEAR, 1, YEAR, 2011), LocalDate.rule(), LocalDate.of(2011, 1, 1)},
//            {DateTimeFields.of(DAY_OF_YEAR, 2, YEAR, 2011), LocalDate.rule(), LocalDate.of(2011, 1, 2)},
//            {DateTimeFields.of(DAY_OF_YEAR, 32, YEAR, 2011), LocalDate.rule(), LocalDate.of(2011, 2, 1)},
//            {DateTimeFields.of(DAY_OF_YEAR, 365, YEAR, 2011), LocalDate.rule(), LocalDate.of(2011, 12, 31)},
//            
//            {DateTimeFields.of(DAY_OF_MONTH, 1, MONTH_OF_YEAR, 1), null, null},
//        };
//    }
//
//    @Test(dataProvider = "merge2")
//    public void test_merge2(DateTimeFields input, CalendricalRule<?> expectedRule, Object expectedVal) {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        for (DateTimeField field : input) {
//            m.getInputMap().put(field.getRule(), field);
//        }
//        m.merge();
//        if (expectedRule == null) {
//            for (DateTimeField field : input) {
//                assertEquals(m.getValue(field.getRule()), field);
//            }
//        } else {
//            assertEquals(m.getValue(expectedRule), expectedVal);
//        }
//    }

}
