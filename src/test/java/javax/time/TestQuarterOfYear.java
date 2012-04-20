/*
 * Copyright (c) 2008-2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.Serializable;

import javax.time.calendrical.CalendricalObject;
import javax.time.extended.QuarterOfYear;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test QuarterOfYear.
 */
@Test
public class TestQuarterOfYear {

    @BeforeMethod
    public void setUp() {
    }

    //-----------------------------------------------------------------------
    @Test(groups={"implementation"})
    public void test_interfaces() {
        assertTrue(Enum.class.isAssignableFrom(QuarterOfYear.class));
        assertTrue(Serializable.class.isAssignableFrom(QuarterOfYear.class));
        assertTrue(Comparable.class.isAssignableFrom(QuarterOfYear.class));
    }

    //-----------------------------------------------------------------------
    // of(int)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_of_int_singleton() {
        for (int i = 1; i <= 4; i++) {
            QuarterOfYear test = QuarterOfYear.of(i);
            assertEquals(test.getValue(), i);
            assertSame(QuarterOfYear.of(i), test);
        }
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_of_int_valueTooLow() {
        QuarterOfYear.of(0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_of_int_valueTooHigh() {
        QuarterOfYear.of(5);
    }

    //-----------------------------------------------------------------------
    // ofMonth(MonthOfYear)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_getQuarterOfYear() {
        assertEquals(QuarterOfYear.ofMonth(MonthOfYear.JANUARY), QuarterOfYear.Q1);
        assertEquals(QuarterOfYear.ofMonth(MonthOfYear.FEBRUARY), QuarterOfYear.Q1);
        assertEquals(QuarterOfYear.ofMonth(MonthOfYear.MARCH), QuarterOfYear.Q1);
        assertEquals(QuarterOfYear.ofMonth(MonthOfYear.APRIL), QuarterOfYear.Q2);
        assertEquals(QuarterOfYear.ofMonth(MonthOfYear.MAY), QuarterOfYear.Q2);
        assertEquals(QuarterOfYear.ofMonth(MonthOfYear.JUNE), QuarterOfYear.Q2);
        assertEquals(QuarterOfYear.ofMonth(MonthOfYear.JULY), QuarterOfYear.Q3);
        assertEquals(QuarterOfYear.ofMonth(MonthOfYear.AUGUST), QuarterOfYear.Q3);
        assertEquals(QuarterOfYear.ofMonth(MonthOfYear.SEPTEMBER), QuarterOfYear.Q3);
        assertEquals(QuarterOfYear.ofMonth(MonthOfYear.OCTOBER), QuarterOfYear.Q4);
        assertEquals(QuarterOfYear.ofMonth(MonthOfYear.NOVEMBER), QuarterOfYear.Q4);
        assertEquals(QuarterOfYear.ofMonth(MonthOfYear.DECEMBER), QuarterOfYear.Q4);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_from_CalendricalObject() {
        assertEquals(QuarterOfYear.from(LocalDate.of(2011, 6, 6)), QuarterOfYear.Q2);
        assertEquals(QuarterOfYear.from(LocalDateTime.of(2012, 2, 3, 12, 30)), QuarterOfYear.Q1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_from_CalendricalObject_invalid_noDerive() {
        QuarterOfYear.from(LocalTime.of(12, 30));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_from_CalendricalObject_null() {
        QuarterOfYear.from((CalendricalObject) null);
    }

    //-----------------------------------------------------------------------
    // getText()
    //-----------------------------------------------------------------------
//    @Test(groups={"tck"})
//    public void test_getText() {
//        assertEquals(QuarterOfYear.Q1.getText(TextStyle.SHORT, Locale.US), "Q1");
//    }
//
//    @Test(expectedExceptions = NullPointerException.class, groups={"tck"})
//    public void test_getText_nullStyle() {
//        QuarterOfYear.Q1.getText(null, Locale.US);
//    }
//
//    @Test(expectedExceptions = NullPointerException.class, groups={"tck"})
//    public void test_getText_nullLocale() {
//        QuarterOfYear.Q1.getText(TextStyle.FULL, null);
//    }

    //-----------------------------------------------------------------------
    // next()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_next() {
        assertEquals(QuarterOfYear.Q1.next(), QuarterOfYear.Q2);
        assertEquals(QuarterOfYear.Q2.next(), QuarterOfYear.Q3);
        assertEquals(QuarterOfYear.Q3.next(), QuarterOfYear.Q4);
        assertEquals(QuarterOfYear.Q4.next(), QuarterOfYear.Q1);
    }

    //-----------------------------------------------------------------------
    // previous()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_previous() {
        assertEquals(QuarterOfYear.Q1.previous(), QuarterOfYear.Q4);
        assertEquals(QuarterOfYear.Q2.previous(), QuarterOfYear.Q1);
        assertEquals(QuarterOfYear.Q3.previous(), QuarterOfYear.Q2);
        assertEquals(QuarterOfYear.Q4.previous(), QuarterOfYear.Q3);
    }

    //-----------------------------------------------------------------------
    // roll(int)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
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
    @Test(groups={"tck"})
    public void test_getFirstMonthOfQuarter() {
        assertEquals(QuarterOfYear.Q1.getFirstMonthOfQuarter(), MonthOfYear.JANUARY);
        assertEquals(QuarterOfYear.Q2.getFirstMonthOfQuarter(), MonthOfYear.APRIL);
        assertEquals(QuarterOfYear.Q3.getFirstMonthOfQuarter(), MonthOfYear.JULY);
        assertEquals(QuarterOfYear.Q4.getFirstMonthOfQuarter(), MonthOfYear.OCTOBER);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_toString() {
        assertEquals(QuarterOfYear.Q1.toString(), "Q1");
        assertEquals(QuarterOfYear.Q2.toString(), "Q2");
        assertEquals(QuarterOfYear.Q3.toString(), "Q3");
        assertEquals(QuarterOfYear.Q4.toString(), "Q4");
    }

    //-----------------------------------------------------------------------
    // generated methods
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_enum() {
        assertEquals(QuarterOfYear.valueOf("Q4"), QuarterOfYear.Q4);
        assertEquals(QuarterOfYear.values()[0], QuarterOfYear.Q1);
    }

}
