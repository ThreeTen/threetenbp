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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test AmPmOfDay.
 */
@Test
public class TestAmPmOfDay {

    @BeforeMethod
    public void setUp() {
    }

    //-----------------------------------------------------------------------
    @Test(groups={"implementation"})
    public void test_interfaces() {
        assertTrue(Enum.class.isAssignableFrom(AmPmOfDay.class));
        assertTrue(Serializable.class.isAssignableFrom(AmPmOfDay.class));
        assertTrue(Comparable.class.isAssignableFrom(AmPmOfDay.class));
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_factory_int_singleton_equals() {
        for (int i = 0; i <= 1; i++) {
            AmPmOfDay test = AmPmOfDay.of(i);
            assertEquals(test.getValue(), i);
        }
    }
    
    @Test(groups={"implementation"})
    public void test_factory_int_singleton_same() {
        for (int i = 0; i <= 1; i++) {
            AmPmOfDay test = AmPmOfDay.of(i);
            assertEquals(test.getValue(), i);
            assertSame(AmPmOfDay.of(i), test);
        }
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_factory_int_valueTooLow() {
        AmPmOfDay.of(-1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_factory_int_valueTooHigh() {
        AmPmOfDay.of(2);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_factory_CalendricalObject() {
        assertEquals(AmPmOfDay.from(LocalTime.of(8, 30)), AmPmOfDay.AM);
        assertEquals(AmPmOfDay.from(LocalTime.of(17, 30)), AmPmOfDay.PM);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_factory_CalendricalObject_invalid_noDerive() {
        AmPmOfDay.from(LocalDate.of(2007, 7, 30));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_factory_CalendricalObject_null() {
        AmPmOfDay.from((CalendricalObject) null);
    }

    //-----------------------------------------------------------------------
    // getText()
    //-----------------------------------------------------------------------
//    @Test(groups={"tck"})
//    public void test_getText() {
//        assertEquals(AmPmOfDay.AM.getText(TextStyle.SHORT, Locale.US), "AM");
//    }
//
//    @Test(expectedExceptions = NullPointerException.class, groups={"tck"})
//    public void test_getText_nullStyle() {
//        AmPmOfDay.AM.getText(null, Locale.US);
//    }
//
//    @Test(expectedExceptions = NullPointerException.class, groups={"tck"})
//    public void test_getText_nullLocale() {
//        AmPmOfDay.AM.getText(TextStyle.FULL, null);
//    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_toString() {
        assertEquals(AmPmOfDay.AM.toString(), "AM");
        assertEquals(AmPmOfDay.PM.toString(), "PM");
    }

    //-----------------------------------------------------------------------
    // generated methods
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_enum() {
        assertEquals(AmPmOfDay.valueOf("AM"), AmPmOfDay.AM);
        assertEquals(AmPmOfDay.values()[0], AmPmOfDay.AM);
    }

}