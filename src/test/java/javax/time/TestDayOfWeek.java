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
import static javax.time.calendrical.LocalPeriodUnit.WEEKS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.Locale;

import javax.time.calendrical.DateTime;
import javax.time.calendrical.LocalPeriodUnit;
import javax.time.calendrical.PeriodUnit;
import javax.time.format.TextStyle;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test DayOfWeek.
 */
@Test
public class TestDayOfWeek {

    private static final PeriodUnit[] INVALID_UNITS;
    static {
        EnumSet<LocalPeriodUnit> set = EnumSet.allOf(LocalPeriodUnit.class);
        set.remove(DAYS);
        set.remove(WEEKS);
        INVALID_UNITS = (PeriodUnit[]) set.toArray(new PeriodUnit[set.size()]);
    }

    @BeforeMethod
    public void setUp() {
    }

    //-----------------------------------------------------------------------
    @Test(groups={"implementation"})
    public void test_interfaces() {
        assertTrue(Enum.class.isAssignableFrom(DayOfWeek.class));
        assertTrue(Serializable.class.isAssignableFrom(DayOfWeek.class));
        assertTrue(Comparable.class.isAssignableFrom(DayOfWeek.class));
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_factory_int_singleton() {
        for (int i = 1; i <= 7; i++) {
            DayOfWeek test = DayOfWeek.of(i);
            assertEquals(test.getValue(), i);
            assertSame(DayOfWeek.of(i), test);
        }
    }

    @Test(expectedExceptions=DateTimeException.class, groups={"tck"})
    public void test_factory_int_valueTooLow() {
        DayOfWeek.of(0);
    }

    @Test(expectedExceptions=DateTimeException.class, groups={"tck"})
    public void test_factory_int_valueTooHigh() {
        DayOfWeek.of(8);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_factory_CalendricalObject() {
        assertEquals(DayOfWeek.from(LocalDate.of(2011, 6, 6)), DayOfWeek.MONDAY);
    }

    @Test(expectedExceptions=DateTimeException.class, groups={"tck"})
    public void test_factory_CalendricalObject_invalid_noDerive() {
        DayOfWeek.from(LocalTime.of(12, 30));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_factory_CalendricalObject_null() {
        DayOfWeek.from((DateTime) null);
    }

    //-----------------------------------------------------------------------
    // getText()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_getText() {
        assertEquals(DayOfWeek.MONDAY.getText(TextStyle.SHORT, Locale.US), "Mon");
    }

    @Test(expectedExceptions = NullPointerException.class, groups={"tck"})
    public void test_getText_nullStyle() {
        DayOfWeek.MONDAY.getText(null, Locale.US);
    }

    @Test(expectedExceptions = NullPointerException.class, groups={"tck"})
    public void test_getText_nullLocale() {
        DayOfWeek.MONDAY.getText(TextStyle.FULL, null);
    }

    //-----------------------------------------------------------------------
    // plus(long), plus(long,unit)
    //-----------------------------------------------------------------------
    @DataProvider(name="plus")
    Object[][] data_plus() {
        return new Object[][] {
            {1, -8, 7},
            {1, -7, 1},
            {1, -6, 2},
            {1, -5, 3},
            {1, -4, 4},
            {1, -3, 5},
            {1, -2, 6},
            {1, -1, 7},
            {1, 0, 1},
            {1, 1, 2},
            {1, 2, 3},
            {1, 3, 4},
            {1, 4, 5},
            {1, 5, 6},
            {1, 6, 7},
            {1, 7, 1},
            {1, 8, 2},
            
            {1, 1, 2},
            {2, 1, 3},
            {3, 1, 4},
            {4, 1, 5},
            {5, 1, 6},
            {6, 1, 7},
            {7, 1, 1},
            
            {1, -1, 7},
            {2, -1, 1},
            {3, -1, 2},
            {4, -1, 3},
            {5, -1, 4},
            {6, -1, 5},
            {7, -1, 6},
        };
    }

    @Test(dataProvider="plus", groups={"tck"})
    public void test_plus_long(int base, long amount, int expected) {
        assertEquals(DayOfWeek.of(base).plus(amount), DayOfWeek.of(expected));
    }

    @Test(dataProvider="plus", groups={"tck"})
    public void test_plus_long_unit(int base, long amount, int expected) {
        assertEquals(DayOfWeek.of(base).plus(amount, DAYS), DayOfWeek.of(expected));
    }

    @Test(groups={"tck"})
    public void test_plus_long_unit_invalidUnit() {
        for (PeriodUnit unit : INVALID_UNITS) {
            try {
                DayOfWeek.MONDAY.plus(1, unit);
                fail("Unit should not be allowed " + unit);
            } catch (DateTimeException ex) {
                // expected
            }
        }
    }

    @Test(groups={"tck"})
    public void test_plus_long_multiples() {
        for (int i = 1; i <= 7; i++) {
            assertEquals(DayOfWeek.of(i).plus(0, WEEKS), DayOfWeek.of(i));
            assertEquals(DayOfWeek.of(i).plus(1, WEEKS), DayOfWeek.of(i));
            assertEquals(DayOfWeek.of(i).plus(2, WEEKS), DayOfWeek.of(i));
            assertEquals(DayOfWeek.of(i).plus(-3, WEEKS), DayOfWeek.of(i));
        }
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_plus_long_unit_null() {
        DayOfWeek.MONDAY.plus(1, null);
    }

    //-----------------------------------------------------------------------
    // minus(long), minus(long,unit)
    //-----------------------------------------------------------------------
    @DataProvider(name="minus")
    Object[][] data_minus() {
        return new Object[][] {
            {1, -8, 2},
            {1, -7, 1},
            {1, -6, 7},
            {1, -5, 6},
            {1, -4, 5},
            {1, -3, 4},
            {1, -2, 3},
            {1, -1, 2},
            {1, 0, 1},
            {1, 1, 7},
            {1, 2, 6},
            {1, 3, 5},
            {1, 4, 4},
            {1, 5, 3},
            {1, 6, 2},
            {1, 7, 1},
            {1, 8, 7},
        };
    }

    @Test(dataProvider="minus", groups={"tck"})
    public void test_minus_long(int base, long amount, int expected) {
        assertEquals(DayOfWeek.of(base).minus(amount), DayOfWeek.of(expected));
    }

    @Test(dataProvider="minus", groups={"tck"})
    public void test_minus_long_unitDayOfWeeks(int base, long amount, int expected) {
        assertEquals(DayOfWeek.of(base).minus(amount, DAYS), DayOfWeek.of(expected));
    }

    @Test(groups={"tck"})
    public void test_minus_long_unit_invalidUnit() {
        for (PeriodUnit unit : INVALID_UNITS) {
            try {
                DayOfWeek.MONDAY.minus(1, unit);
                fail("Unit should not be allowed " + unit);
            } catch (DateTimeException ex) {
                // expected
            }
        }
    }

    @Test(groups={"tck"})
    public void test_minus_long_unit_multiples() {
        for (int i = 1; i <= 7; i++) {
            assertEquals(DayOfWeek.of(i).minus(0, WEEKS), DayOfWeek.of(i));
            assertEquals(DayOfWeek.of(i).minus(1, WEEKS), DayOfWeek.of(i));
            assertEquals(DayOfWeek.of(i).minus(2, WEEKS), DayOfWeek.of(i));
            assertEquals(DayOfWeek.of(i).minus(-3, WEEKS), DayOfWeek.of(i));
        }
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_minus_long_unit_null() {
        DayOfWeek.MONDAY.minus(1, null);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_toString() {
        assertEquals(DayOfWeek.MONDAY.toString(), "MONDAY");
        assertEquals(DayOfWeek.TUESDAY.toString(), "TUESDAY");
        assertEquals(DayOfWeek.WEDNESDAY.toString(), "WEDNESDAY");
        assertEquals(DayOfWeek.THURSDAY.toString(), "THURSDAY");
        assertEquals(DayOfWeek.FRIDAY.toString(), "FRIDAY");
        assertEquals(DayOfWeek.SATURDAY.toString(), "SATURDAY");
        assertEquals(DayOfWeek.SUNDAY.toString(), "SUNDAY");
    }

    //-----------------------------------------------------------------------
    // generated methods
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_enum() {
        assertEquals(DayOfWeek.valueOf("MONDAY"), DayOfWeek.MONDAY);
        assertEquals(DayOfWeek.values()[0], DayOfWeek.MONDAY);
    }

}
