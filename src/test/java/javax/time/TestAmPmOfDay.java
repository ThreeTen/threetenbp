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
package javax.time;

import static javax.time.calendrical.ISODateTimeRule.AMPM_OF_DAY;
import static javax.time.calendrical.ISODateTimeRule.DAY_OF_WEEK;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.Serializable;
import java.util.Locale;

import javax.time.calendrical.Calendrical;
import javax.time.calendrical.CalendricalMatcher;
import javax.time.calendrical.IllegalCalendarFieldValueException;
import javax.time.format.TextStyle;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test AmPmOfDay.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestAmPmOfDay {

    @BeforeMethod
    public void setUp() {
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Enum.class.isAssignableFrom(AmPmOfDay.class));
        assertTrue(Serializable.class.isAssignableFrom(AmPmOfDay.class));
        assertTrue(Comparable.class.isAssignableFrom(AmPmOfDay.class));
        assertTrue(Calendrical.class.isAssignableFrom(AmPmOfDay.class));
        assertTrue(CalendricalMatcher.class.isAssignableFrom(AmPmOfDay.class));
    }

    //-----------------------------------------------------------------------
    public void test_rule() {
        assertEquals(AmPmOfDay.rule().getName(), "AmPmOfDay");
        assertEquals(AmPmOfDay.rule().getType(), AmPmOfDay.class);
    }

    //-----------------------------------------------------------------------
    public void test_factory_int_singleton() {
        for (int i = 0; i <= 1; i++) {
            AmPmOfDay test = AmPmOfDay.of(i);
            assertEquals(test.getValue(), i);
            assertSame(AmPmOfDay.of(i), test);
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_valueTooLow() {
        AmPmOfDay.of(-1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_valueTooHigh() {
        try {
            AmPmOfDay.of(2);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), AMPM_OF_DAY);
            assertEquals(ex.getActual(), 2);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    public void test_factory_Calendricals() {
        assertEquals(AmPmOfDay.from(LocalTime.of(8, 30)), AmPmOfDay.AM);
        assertEquals(AmPmOfDay.from(LocalTime.of(17, 30)), AmPmOfDay.PM);
        assertEquals(AmPmOfDay.from(AMPM_OF_DAY.field(1)), AmPmOfDay.PM);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_factory_Calendricals_invalid_clash() {
        AmPmOfDay.from(AmPmOfDay.AM, AmPmOfDay.PM.toField());
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_factory_Calendricals_invalid_noDerive() {
        AmPmOfDay.from(LocalDate.of(2007, 7, 30));
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_factory_Calendricals_invalid_empty() {
        AmPmOfDay.from();
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_Calendricals_nullArray() {
        AmPmOfDay.from((Calendrical[]) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_Calendricals_null() {
        AmPmOfDay.from((Calendrical) null);
    }

    //-----------------------------------------------------------------------
    // get()
    //-----------------------------------------------------------------------
    public void test_get() {
        assertEquals(AmPmOfDay.AM.get(AmPmOfDay.rule()), AmPmOfDay.AM);
        assertEquals(AmPmOfDay.PM.get(AmPmOfDay.rule()), AmPmOfDay.PM);
        
        assertEquals(AmPmOfDay.AM.get(AMPM_OF_DAY), AMPM_OF_DAY.field(0));
        assertEquals(AmPmOfDay.PM.get(AMPM_OF_DAY), AMPM_OF_DAY.field(1));
        
        assertEquals(AmPmOfDay.AM.get(DAY_OF_WEEK), null);
    }

    //-----------------------------------------------------------------------
    // getText()
    //-----------------------------------------------------------------------
    public void test_getText() {
        assertEquals(AmPmOfDay.AM.getText(TextStyle.SHORT, Locale.US), "AM");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_getText_nullStyle() {
        AmPmOfDay.AM.getText(null, Locale.US);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_getText_nullLocale() {
        AmPmOfDay.AM.getText(TextStyle.FULL, null);
    }

    //-----------------------------------------------------------------------
    // matcher
    //-----------------------------------------------------------------------
    public void test_matcher() {
        assertEquals(AmPmOfDay.AM.matchesCalendrical(AMPM_OF_DAY.field(0)), true);
        assertEquals(AmPmOfDay.AM.matchesCalendrical(AmPmOfDay.AM), true);
        assertEquals(AmPmOfDay.AM.matchesCalendrical(AmPmOfDay.PM), false);
        
        assertEquals(AmPmOfDay.AM.matchesCalendrical(LocalTime.of(11, 30)), true);
        assertEquals(AmPmOfDay.AM.matchesCalendrical(LocalTime.of(12, 30)), false);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_matcher_null() {
        AmPmOfDay.AM.matchesCalendrical(null);
    }

    //-----------------------------------------------------------------------
    // toField()
    //-----------------------------------------------------------------------
    public void test_toField() {
        assertEquals(AmPmOfDay.AM.toField(), AMPM_OF_DAY.field(0));
        assertEquals(AmPmOfDay.PM.toField(), AMPM_OF_DAY.field(1));
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        assertEquals(AmPmOfDay.AM.toString(), "AM");
        assertEquals(AmPmOfDay.PM.toString(), "PM");
    }

    //-----------------------------------------------------------------------
    // generated methods
    //-----------------------------------------------------------------------
    public void test_enum() {
        assertEquals(AmPmOfDay.valueOf("AM"), AmPmOfDay.AM);
        assertEquals(AmPmOfDay.values()[0], AmPmOfDay.AM);
    }

}
