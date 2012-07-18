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
package javax.time.calendrical;

import static javax.time.calendrical.LocalPeriodUnit.CENTURIES;
import static javax.time.calendrical.LocalPeriodUnit.DECADES;
import static javax.time.calendrical.LocalPeriodUnit.HALF_YEARS;
import static javax.time.calendrical.LocalPeriodUnit.HOURS;
import static javax.time.calendrical.LocalPeriodUnit.MILLENNIA;
import static javax.time.calendrical.LocalPeriodUnit.QUARTER_YEARS;
import static javax.time.calendrical.LocalPeriodUnit.YEARS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.Serializable;

import javax.time.CalendricalException;
import javax.time.LocalDate;
import javax.time.LocalDateTime;
import javax.time.LocalTime;
import javax.time.Month;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
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
    // ofMonth(Month)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_getQuarterOfYear() {
        assertEquals(QuarterOfYear.ofMonth(Month.JANUARY), QuarterOfYear.Q1);
        assertEquals(QuarterOfYear.ofMonth(Month.FEBRUARY), QuarterOfYear.Q1);
        assertEquals(QuarterOfYear.ofMonth(Month.MARCH), QuarterOfYear.Q1);
        assertEquals(QuarterOfYear.ofMonth(Month.APRIL), QuarterOfYear.Q2);
        assertEquals(QuarterOfYear.ofMonth(Month.MAY), QuarterOfYear.Q2);
        assertEquals(QuarterOfYear.ofMonth(Month.JUNE), QuarterOfYear.Q2);
        assertEquals(QuarterOfYear.ofMonth(Month.JULY), QuarterOfYear.Q3);
        assertEquals(QuarterOfYear.ofMonth(Month.AUGUST), QuarterOfYear.Q3);
        assertEquals(QuarterOfYear.ofMonth(Month.SEPTEMBER), QuarterOfYear.Q3);
        assertEquals(QuarterOfYear.ofMonth(Month.OCTOBER), QuarterOfYear.Q4);
        assertEquals(QuarterOfYear.ofMonth(Month.NOVEMBER), QuarterOfYear.Q4);
        assertEquals(QuarterOfYear.ofMonth(Month.DECEMBER), QuarterOfYear.Q4);
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
        QuarterOfYear.from((DateTime) null);
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
    // plus(long), plus(long,unit)
    //-----------------------------------------------------------------------
    @DataProvider(name="plus")
    Object[][] data_plus() {
        return new Object[][] {
            {1, -5, 4},
            {1, -4, 1},
            {1, -3, 2},
            {1, -2, 3},
            {1, -1, 4},
            {1, 0, 1},
            {1, 1, 2},
            {1, 2, 3},
            {1, 3, 4},
            {1, 4, 1},
            {1, 5, 2},
        };
    }

    @Test(dataProvider="plus", groups={"tck"})
    public void test_plus_long(int base, long amount, int expected) {
        assertEquals(QuarterOfYear.of(base).plus(amount), QuarterOfYear.of(expected));
    }

    @Test(dataProvider="plus", groups={"tck"})
    public void test_plus_long_unit(int base, long amount, int expected) {
        assertEquals(QuarterOfYear.of(base).plus(amount, QUARTER_YEARS), QuarterOfYear.of(expected));
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_plus_long_unit_invalidUnit() {
        QuarterOfYear.Q1.plus(1, HOURS);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_plus_long_unit_null() {
        QuarterOfYear.Q1.plus(1, null);
    }

    @Test(groups={"tck"})
    public void test_plus_long_unitHalfYears() {
        for (int i = 1; i <= 4; i++) {
            for (int j = 0; j < 5; j++) {
                assertEquals(QuarterOfYear.of(i).plus(j, HALF_YEARS), QuarterOfYear.of(i).minus(j * 2));
            }
        }
    }

    @Test(groups={"tck"})
    public void test_plus_long_unitMultiples() {
        for (int i = 1; i <= 4; i++) {
            assertEquals(QuarterOfYear.of(i).plus(1, YEARS), QuarterOfYear.of(i));
            assertEquals(QuarterOfYear.of(i).plus(1, DECADES), QuarterOfYear.of(i));
            assertEquals(QuarterOfYear.of(i).plus(1, CENTURIES), QuarterOfYear.of(i));
            assertEquals(QuarterOfYear.of(i).plus(1, MILLENNIA), QuarterOfYear.of(i));
        }
    }

    //-----------------------------------------------------------------------
    // minus(long), minus(long,unit)
    //-----------------------------------------------------------------------
    @DataProvider(name="minus")
    Object[][] data_minus() {
        return new Object[][] {
            {1, -5, 2},
            {1, -4, 1},
            {1, -3, 4},
            {1, -2, 3},
            {1, -1, 2},
            {1, 0, 1},
            {1, 1, 4},
            {1, 2, 3},
            {1, 3, 2},
            {1, 4, 1},
            {1, 5, 4},
        };
    }

    @Test(dataProvider="minus", groups={"tck"})
    public void test_minus_long(int base, long amount, int expected) {
        assertEquals(QuarterOfYear.of(base).minus(amount), QuarterOfYear.of(expected));
    }

    @Test(dataProvider="minus", groups={"tck"})
    public void test_minus_long_unit(int base, long amount, int expected) {
        assertEquals(QuarterOfYear.of(base).minus(amount, QUARTER_YEARS), QuarterOfYear.of(expected));
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_minus_long_unit_invalidUnit() {
        QuarterOfYear.Q1.minus(1, HOURS);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_minus_long_unit_null() {
        QuarterOfYear.Q1.minus(1, null);
    }

    @Test(groups={"tck"})
    public void test_minus_long_unitHalfYears() {
        for (int i = 1; i <= 4; i++) {
            for (int j = 0; j < 5; j++) {
                assertEquals(QuarterOfYear.of(i).minus(j, HALF_YEARS), QuarterOfYear.of(i).minus(j * 2));
            }
        }
    }

    @Test(groups={"tck"})
    public void test_minus_long_unitMultiples() {
        for (int i = 1; i <= 4; i++) {
            assertEquals(QuarterOfYear.of(i).minus(1, YEARS), QuarterOfYear.of(i));
            assertEquals(QuarterOfYear.of(i).minus(1, DECADES), QuarterOfYear.of(i));
            assertEquals(QuarterOfYear.of(i).minus(1, CENTURIES), QuarterOfYear.of(i));
            assertEquals(QuarterOfYear.of(i).minus(1, MILLENNIA), QuarterOfYear.of(i));
        }
    }

    //-----------------------------------------------------------------------
    // getFirstMonthOfQuarter()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_getFirstMonthOfQuarter() {
        assertEquals(QuarterOfYear.Q1.getFirstMonthOfQuarter(), Month.JANUARY);
        assertEquals(QuarterOfYear.Q2.getFirstMonthOfQuarter(), Month.APRIL);
        assertEquals(QuarterOfYear.Q3.getFirstMonthOfQuarter(), Month.JULY);
        assertEquals(QuarterOfYear.Q4.getFirstMonthOfQuarter(), Month.OCTOBER);
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
