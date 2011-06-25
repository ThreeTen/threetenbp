/*
 * Copyright (c) 2008-2011, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.calendar.ISODateTimeRule.DAY_OF_WEEK;
import static javax.time.calendar.ISODateTimeRule.QUARTER_OF_YEAR;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.Serializable;
import java.util.Locale;

import javax.time.calendar.format.DateTimeFormatterBuilder.TextStyle;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test QuarterOfYear.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestQuarterOfYear {

    @BeforeMethod
    public void setUp() {
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Enum.class.isAssignableFrom(QuarterOfYear.class));
        assertTrue(Serializable.class.isAssignableFrom(QuarterOfYear.class));
        assertTrue(Comparable.class.isAssignableFrom(QuarterOfYear.class));
        assertTrue(Calendrical.class.isAssignableFrom(Year.class));
    }

    //-----------------------------------------------------------------------
    public void test_rule() {
        assertEquals(QuarterOfYear.rule().getName(), "QuarterOfYear");
        assertEquals(QuarterOfYear.rule().getType(), QuarterOfYear.class);
    }

    //-----------------------------------------------------------------------
    public void test_factory_int_singleton() {
        for (int i = 1; i <= 4; i++) {
            QuarterOfYear test = QuarterOfYear.of(i);
            assertEquals(test.getValue(), i);
            assertSame(QuarterOfYear.of(i), test);
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_valueTooLow() {
        QuarterOfYear.of(0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_valueTooHigh() {
        QuarterOfYear.of(5);
    }

    //-----------------------------------------------------------------------
    // get()
    //-----------------------------------------------------------------------
    public void test_get() {
        assertEquals(QuarterOfYear.Q1.get(QuarterOfYear.rule()), QuarterOfYear.Q1);
        assertEquals(QuarterOfYear.Q4.get(QuarterOfYear.rule()), QuarterOfYear.Q4);
        
        assertEquals(QuarterOfYear.Q1.get(QUARTER_OF_YEAR), QUARTER_OF_YEAR.field(1));
        assertEquals(QuarterOfYear.Q3.get(QUARTER_OF_YEAR), QUARTER_OF_YEAR.field(3));
        
        assertEquals(QuarterOfYear.Q1.get(DAY_OF_WEEK), null);
    }

    //-----------------------------------------------------------------------
    // getText()
    //-----------------------------------------------------------------------
    public void test_getText() {
        assertEquals(QuarterOfYear.Q1.getText(TextStyle.SHORT, Locale.US), "Q1");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_getText_nullStyle() {
        QuarterOfYear.Q1.getText(null, Locale.US);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_getText_nullLocale() {
        QuarterOfYear.Q1.getText(TextStyle.FULL, null);
    }

    //-----------------------------------------------------------------------
    // next()
    //-----------------------------------------------------------------------
    public void test_next() {
        assertEquals(QuarterOfYear.Q1.next(), QuarterOfYear.Q2);
        assertEquals(QuarterOfYear.Q2.next(), QuarterOfYear.Q3);
        assertEquals(QuarterOfYear.Q3.next(), QuarterOfYear.Q4);
        assertEquals(QuarterOfYear.Q4.next(), QuarterOfYear.Q1);
    }

    //-----------------------------------------------------------------------
    // previous()
    //-----------------------------------------------------------------------
    public void test_previous() {
        assertEquals(QuarterOfYear.Q1.previous(), QuarterOfYear.Q4);
        assertEquals(QuarterOfYear.Q2.previous(), QuarterOfYear.Q1);
        assertEquals(QuarterOfYear.Q3.previous(), QuarterOfYear.Q2);
        assertEquals(QuarterOfYear.Q4.previous(), QuarterOfYear.Q3);
    }

    //-----------------------------------------------------------------------
    // roll(int)
    //-----------------------------------------------------------------------
    public void test_roll() {
        assertEquals(QuarterOfYear.Q1.roll(-4), QuarterOfYear.Q1);
        assertEquals(QuarterOfYear.Q1.roll(-3), QuarterOfYear.Q2);
        assertEquals(QuarterOfYear.Q1.roll(-2), QuarterOfYear.Q3);
        assertEquals(QuarterOfYear.Q1.roll(-1), QuarterOfYear.Q4);
        assertEquals(QuarterOfYear.Q1.roll(0), QuarterOfYear.Q1);
        assertEquals(QuarterOfYear.Q1.roll(1), QuarterOfYear.Q2);
        assertEquals(QuarterOfYear.Q1.roll(2), QuarterOfYear.Q3);
        assertEquals(QuarterOfYear.Q1.roll(3), QuarterOfYear.Q4);
        assertEquals(QuarterOfYear.Q1.roll(4), QuarterOfYear.Q1);
    }

    //-----------------------------------------------------------------------
    // getFirstMonthOfQuarter()
    //-----------------------------------------------------------------------
    public void test_getFirstMonthOfQuarter() {
        assertEquals(QuarterOfYear.Q1.getFirstMonthOfQuarter(), MonthOfYear.JANUARY);
        assertEquals(QuarterOfYear.Q2.getFirstMonthOfQuarter(), MonthOfYear.APRIL);
        assertEquals(QuarterOfYear.Q3.getFirstMonthOfQuarter(), MonthOfYear.JULY);
        assertEquals(QuarterOfYear.Q4.getFirstMonthOfQuarter(), MonthOfYear.OCTOBER);
    }

    //-----------------------------------------------------------------------
    // toField()
    //-----------------------------------------------------------------------
    public void test_toField() {
        assertEquals(QuarterOfYear.Q1.toField(), QUARTER_OF_YEAR.field(1));
        assertEquals(QuarterOfYear.Q3.toField(), QUARTER_OF_YEAR.field(3));
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        assertEquals(QuarterOfYear.Q1.toString(), "Q1");
        assertEquals(QuarterOfYear.Q2.toString(), "Q2");
        assertEquals(QuarterOfYear.Q3.toString(), "Q3");
        assertEquals(QuarterOfYear.Q4.toString(), "Q4");
    }

    //-----------------------------------------------------------------------
    // generated methods
    //-----------------------------------------------------------------------
    public void test_enum() {
        assertEquals(QuarterOfYear.valueOf("Q4"), QuarterOfYear.Q4);
        assertEquals(QuarterOfYear.values()[0], QuarterOfYear.Q1);
    }

}
