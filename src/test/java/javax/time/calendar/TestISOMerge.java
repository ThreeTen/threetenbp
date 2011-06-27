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

import static javax.time.calendar.ISODateTimeRule.AMPM_OF_DAY;
import static javax.time.calendar.ISODateTimeRule.CLOCK_HOUR_OF_AMPM;
import static javax.time.calendar.ISODateTimeRule.CLOCK_HOUR_OF_DAY;
import static javax.time.calendar.ISODateTimeRule.DAY_OF_MONTH;
import static javax.time.calendar.ISODateTimeRule.DAY_OF_WEEK;
import static javax.time.calendar.ISODateTimeRule.DAY_OF_YEAR;
import static javax.time.calendar.ISODateTimeRule.HOUR_OF_AMPM;
import static javax.time.calendar.ISODateTimeRule.HOUR_OF_DAY;
import static javax.time.calendar.ISODateTimeRule.MILLI_OF_MINUTE;
import static javax.time.calendar.ISODateTimeRule.MILLI_OF_SECOND;
import static javax.time.calendar.ISODateTimeRule.MINUTE_OF_DAY;
import static javax.time.calendar.ISODateTimeRule.MINUTE_OF_HOUR;
import static javax.time.calendar.ISODateTimeRule.MONTH_OF_QUARTER;
import static javax.time.calendar.ISODateTimeRule.MONTH_OF_YEAR;
import static javax.time.calendar.ISODateTimeRule.NANO_OF_DAY;
import static javax.time.calendar.ISODateTimeRule.NANO_OF_MILLI;
import static javax.time.calendar.ISODateTimeRule.NANO_OF_SECOND;
import static javax.time.calendar.ISODateTimeRule.QUARTER_OF_YEAR;
import static javax.time.calendar.ISODateTimeRule.SECOND_OF_DAY;
import static javax.time.calendar.ISODateTimeRule.SECOND_OF_HOUR;
import static javax.time.calendar.ISODateTimeRule.SECOND_OF_MINUTE;
import static javax.time.calendar.ISODateTimeRule.YEAR;
import static javax.time.calendar.ISODateTimeRule.ZERO_EPOCH_MONTH;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import javax.time.CalendricalException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test merging of ISO fields.
 */
@Test
public class TestISOMerge {

    //-----------------------------------------------------------------------
    @DataProvider(name = "normalized")
    public Object[][] data_normalized() {
        return new Object[][] {
            {dtf(YEAR, 2011), dtf(YEAR, 2011)},
            {dtf(QUARTER_OF_YEAR, 2), dtf(QUARTER_OF_YEAR, 2)},
            {dtf(MONTH_OF_YEAR, 6), dtf(MONTH_OF_YEAR, 6)},
            {dtf(MONTH_OF_QUARTER, 3), dtf(MONTH_OF_QUARTER, 3)},
            {dtf(DAY_OF_YEAR, 34), dtf(DAY_OF_YEAR, 34)},
            {dtf(DAY_OF_MONTH, 4), dtf(DAY_OF_MONTH, 4)},
            {dtf(DAY_OF_WEEK, 6), dtf(DAY_OF_WEEK, 6)},
            
            {dtf(MONTH_OF_YEAR, 6, DAY_OF_MONTH, 2), dtf(MONTH_OF_YEAR, 6, DAY_OF_MONTH, 2)},
            {dtf(YEAR, 2011, DAY_OF_MONTH, 2), dtf(YEAR, 2011, DAY_OF_MONTH, 2)},
            {dtf(DAY_OF_MONTH, 13, DAY_OF_WEEK, 5), dtf(DAY_OF_MONTH, 13, DAY_OF_WEEK, 5)},
            {dtf(HOUR_OF_DAY, 14, SECOND_OF_MINUTE, 30), dtf(HOUR_OF_DAY, 14, SECOND_OF_MINUTE, 30)},
            
            {dtf(HOUR_OF_DAY, 15), dtf(HOUR_OF_DAY, 15)},
            {dtf(HOUR_OF_AMPM, 5), dtf(HOUR_OF_AMPM, 5)},
            
            {dtf(CLOCK_HOUR_OF_DAY, 15), dtf(HOUR_OF_DAY, 15)},
            {dtf(CLOCK_HOUR_OF_DAY, 24), dtf(HOUR_OF_DAY, 0)},
            {dtf(CLOCK_HOUR_OF_AMPM, 5), dtf(HOUR_OF_AMPM, 5)},
            {dtf(CLOCK_HOUR_OF_AMPM, 12), dtf(HOUR_OF_AMPM, 0)},
            
            {dtf(CLOCK_HOUR_OF_DAY, 15, DAY_OF_MONTH, 2), dtf(HOUR_OF_DAY, 15, DAY_OF_MONTH, 2)},
            {dtf(CLOCK_HOUR_OF_DAY, 15, HOUR_OF_DAY, 15), dtf(HOUR_OF_DAY, 15)},
            
            {dtf(AMPM_OF_DAY, 1, HOUR_OF_AMPM, 3), dtf(HOUR_OF_DAY, 15)},
            {dtf(HOUR_OF_DAY, 14, MINUTE_OF_HOUR, 3), dtf(MINUTE_OF_DAY, 14 * 60 + 3)},
            {dtf(MINUTE_OF_HOUR, 5, SECOND_OF_MINUTE, 30), dtf(SECOND_OF_HOUR, 5 * 60 + 30)},
            {dtf(SECOND_OF_MINUTE, 30, MILLI_OF_SECOND, 12), dtf(MILLI_OF_MINUTE, 30012)},
            {dtf(MILLI_OF_SECOND, 12, NANO_OF_MILLI, 56), dtf(NANO_OF_SECOND, 12000056)},
            {dtf(SECOND_OF_DAY, 1223, NANO_OF_SECOND, 12), dtf(NANO_OF_DAY, 1223000000012L)},
            
            {dtf(HOUR_OF_DAY, 14, MINUTE_OF_HOUR, 5, SECOND_OF_MINUTE, 30), dtf(SECOND_OF_DAY, (14 * 60 + 5) * 60 + 30)},
            {dtf(HOUR_OF_DAY, 14, MINUTE_OF_HOUR, 5, SECOND_OF_MINUTE, 30, NANO_OF_SECOND, 1), dtf(NANO_OF_DAY, ((14 * 60 + 5) * 60 + 30) * 1000000000L + 1L)},
            
            {dtf(MINUTE_OF_DAY, 14 * 60 + 5, MINUTE_OF_HOUR, 5), dtf(MINUTE_OF_DAY, 14 * 60 + 5)},
            
            {dtf(QUARTER_OF_YEAR, 2, MONTH_OF_QUARTER, 3), dtf(MONTH_OF_YEAR, 6)},
            {dtf(YEAR, 2011, MONTH_OF_YEAR, 3), dtf(ZERO_EPOCH_MONTH, 2011 * 12 + 3 - 1)},
            
            {dtf(CLOCK_HOUR_OF_DAY, 15, HOUR_OF_DAY, 9), null},
            {dtf(HOUR_OF_DAY, 14, MINUTE_OF_HOUR, 3, MINUTE_OF_DAY, 99), null},
        };
    }

    @Test(dataProvider = "normalized")
    public void test_normalized(DateTimeFields input, DateTimeFields output) {
        if (output != null) {
            assertEquals(input.normalized(), output);
        } else {
            try {
                input.normalized();
                fail();
            } catch (CalendricalException ex) {
                // expected
                System.out.println(ex.getMessage());
            }
        }
    }

//    //-----------------------------------------------------------------------
//    @DataProvider(name = "merge2")
//    public Object[][] data_merge2() {
//        return new Object[][] {
//            {dtf(DAY_OF_YEAR, 1, YEAR, 2011), LocalDate.rule(), LocalDate.of(2011, 1, 1)},
//            {dtf(DAY_OF_YEAR, 2, YEAR, 2011), LocalDate.rule(), LocalDate.of(2011, 1, 2)},
//            {dtf(DAY_OF_YEAR, 32, YEAR, 2011), LocalDate.rule(), LocalDate.of(2011, 2, 1)},
//            {dtf(DAY_OF_YEAR, 365, YEAR, 2011), LocalDate.rule(), LocalDate.of(2011, 12, 31)},
//            
//            {dtf(DAY_OF_MONTH, 1, MONTH_OF_YEAR, 1), null, null},
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

    private DateTimeFields dtf(DateTimeRule r1, long v1) {
        return DateTimeFields.of(DateTimeField.of(r1, v1));
    }

    private DateTimeFields dtf(DateTimeRule r1, long v1, DateTimeRule r2, long v2) {
        return DateTimeFields.of(DateTimeField.of(r1, v1), DateTimeField.of(r2, v2));
    }

    private DateTimeFields dtf(DateTimeRule r1, long v1, DateTimeRule r2, long v2, DateTimeRule r3, long v3) {
        return DateTimeFields.of(DateTimeField.of(r1, v1), DateTimeField.of(r2, v2), DateTimeField.of(r3, v3));
    }

    private DateTimeFields dtf(DateTimeRule r1, long v1, DateTimeRule r2, long v2, DateTimeRule r3, long v3, DateTimeRule r4, long v4) {
        return DateTimeFields.of(DateTimeField.of(r1, v1), DateTimeField.of(r2, v2), DateTimeField.of(r3, v3), DateTimeField.of(r4, v4));
    }

}
