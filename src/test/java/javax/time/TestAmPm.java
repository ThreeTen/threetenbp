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

import static javax.time.calendrical.LocalPeriodUnit.DAYS;
import static javax.time.calendrical.LocalPeriodUnit.HALF_DAYS;
import static javax.time.calendrical.LocalPeriodUnit.HOURS;
import static javax.time.calendrical.LocalPeriodUnit.WEEKS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.Serializable;

import javax.time.calendrical.DateTime;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test AmPm.
 */
@Test
public class TestAmPm {

    @BeforeMethod
    public void setUp() {
    }

    //-----------------------------------------------------------------------
    @Test(groups={"implementation"})
    public void test_interfaces() {
        assertTrue(Enum.class.isAssignableFrom(AmPm.class));
        assertTrue(Serializable.class.isAssignableFrom(AmPm.class));
        assertTrue(Comparable.class.isAssignableFrom(AmPm.class));
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_factory_int_singleton_equals() {
        for (int i = 0; i <= 1; i++) {
            AmPm test = AmPm.of(i);
            assertEquals(test.getValue(), i);
        }
    }
    
    @Test(groups={"implementation"})
    public void test_factory_int_singleton_same() {
        for (int i = 0; i <= 1; i++) {
            AmPm test = AmPm.of(i);
            assertEquals(test.getValue(), i);
            assertSame(AmPm.of(i), test);
        }
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_factory_int_valueTooLow() {
        AmPm.of(-1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_factory_int_valueTooHigh() {
        AmPm.of(2);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_factory_CalendricalObject() {
        assertEquals(AmPm.from(LocalTime.of(8, 30)), AmPm.AM);
        assertEquals(AmPm.from(LocalTime.of(17, 30)), AmPm.PM);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_factory_CalendricalObject_invalid_noDerive() {
        AmPm.from(LocalDate.of(2007, 7, 30));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_factory_CalendricalObject_null() {
        AmPm.from((DateTime) null);
    }

    //-----------------------------------------------------------------------
    // getText()
    //-----------------------------------------------------------------------
//    @Test(groups={"tck"})
//    public void test_getText() {
//        assertEquals(AmPm.AM.getText(TextStyle.SHORT, Locale.US), "AM");
//    }
//
//    @Test(expectedExceptions = NullPointerException.class, groups={"tck"})
//    public void test_getText_nullStyle() {
//        AmPm.AM.getText(null, Locale.US);
//    }
//
//    @Test(expectedExceptions = NullPointerException.class, groups={"tck"})
//    public void test_getText_nullLocale() {
//        AmPm.AM.getText(TextStyle.FULL, null);
//    }

    //-----------------------------------------------------------------------
    // plus(long,unit)
    //-----------------------------------------------------------------------
    @DataProvider(name="plus")
    Object[][] data_plus() {
        return new Object[][] {
            {0, -2, 0},
            {0, -1, 1},
            {0, 0, 0},
            {0, 1, 1},
            {0, 2, 0},
            
            {0, 1, 1},
            {1, 1, 0},
            
            {0, -1, 1},
            {1, -1, 0},
        };
    }

    @Test(dataProvider="plus", groups={"tck"})
    public void test_plus_long_unit(int base, long amount, int expected) {
        assertEquals(AmPm.of(base).plus(amount, HALF_DAYS), AmPm.of(expected));
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_plus_long_unit_invalidUnit() {
        AmPm.AM.plus(1, HOURS);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_plus_long_unit_null() {
        AmPm.AM.plus(1, null);
    }

    @Test(groups={"tck"})
    public void test_plus_long_unitMultiples() {
        for (int i = 0; i <= 1; i++) {
            assertEquals(AmPm.of(i).plus(1, DAYS), AmPm.of(i));
            assertEquals(AmPm.of(i).plus(1, WEEKS), AmPm.of(i));
        }
    }

    //-----------------------------------------------------------------------
    // minus(long,unit)
    //-----------------------------------------------------------------------
    @DataProvider(name="minus")
    Object[][] data_minus() {
        return new Object[][] {
            {0, -2, 0},
            {0, -1, 1},
            {0, 0, 0},
            {0, 1, 1},
            {0, 2, 0},
        };
    }

    @Test(dataProvider="minus", groups={"tck"})
    public void test_minus_long_unitAmPms(int base, long amount, int expected) {
        assertEquals(AmPm.of(base).minus(amount, HALF_DAYS), AmPm.of(expected));
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_minus_long_unit_invalidUnit() {
        AmPm.AM.minus(1, HOURS);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_minus_long_unit_null() {
        AmPm.AM.minus(1, null);
    }

    @Test(groups={"tck"})
    public void test_minus_long_unitYearMultiples() {
        for (int i = 0; i <= 1; i++) {
            assertEquals(AmPm.of(i).minus(1, DAYS), AmPm.of(i));
            assertEquals(AmPm.of(i).minus(1, WEEKS), AmPm.of(i));
        }
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_toString() {
        assertEquals(AmPm.AM.toString(), "AM");
        assertEquals(AmPm.PM.toString(), "PM");
    }

    //-----------------------------------------------------------------------
    // generated methods
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_enum() {
        assertEquals(AmPm.valueOf("AM"), AmPm.AM);
        assertEquals(AmPm.values()[0], AmPm.AM);
    }

}