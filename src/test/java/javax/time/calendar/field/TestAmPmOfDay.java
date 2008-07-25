/*
 * Copyright (c) 2008, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendar.field;

import static org.testng.Assert.*;

import java.io.Serializable;
import java.util.Locale;

import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalProvider;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.LocalTime;
import javax.time.calendar.MockTimeProviderReturnsNull;
import javax.time.calendar.TimeAdjuster;
import javax.time.calendar.TimeMatcher;
import javax.time.calendar.TimeProvider;

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

    private static final DateTimeFieldRule RULE = ISOChronology.amPmOfDayRule();

    @BeforeMethod
    public void setUp() {
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(CalendricalProvider.class.isAssignableFrom(AmPmOfDay.class));
        assertTrue(Serializable.class.isAssignableFrom(AmPmOfDay.class));
        assertTrue(Comparable.class.isAssignableFrom(AmPmOfDay.class));
        assertTrue(TimeAdjuster.class.isAssignableFrom(AmPmOfDay.class));
        assertTrue(TimeMatcher.class.isAssignableFrom(AmPmOfDay.class));
    }

    //-----------------------------------------------------------------------
    public void test_rule() {
        assertEquals(AmPmOfDay.rule(), RULE);
    }

    //-----------------------------------------------------------------------
    public void test_factory_int_singleton() {
        for (int i = 0; i <= 1; i++) {
            AmPmOfDay test = AmPmOfDay.amPmOfDay(i);
            assertEquals(test.getValue(), i);
            assertSame(AmPmOfDay.amPmOfDay(i), test);
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_valueTooLow() {
        AmPmOfDay.amPmOfDay(-1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_valueTooHigh() {
        AmPmOfDay.amPmOfDay(2);
    }

    //-----------------------------------------------------------------------
    public void test_factory_TimeProvider_AM() {
        LocalTime time = LocalTime.time(11, 59);
        AmPmOfDay test = AmPmOfDay.amPmOfDay(time);
        assertEquals(test.getValue(), 0);
    }

    public void test_factory_TimeProvider_PM() {
        LocalTime time = LocalTime.time(12, 00);
        AmPmOfDay test = AmPmOfDay.amPmOfDay(time);
        assertEquals(test.getValue(), 1);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_nullTimeProvider() {
        AmPmOfDay.amPmOfDay((TimeProvider) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_badTimeProvider() {
        AmPmOfDay.amPmOfDay(new MockTimeProviderReturnsNull());
    }

    //-----------------------------------------------------------------------
    // getShortText()
    //-----------------------------------------------------------------------
    public void test_getShortText() {
        assertEquals(AmPmOfDay.AM.getShortText(Locale.US), "AM");
        assertEquals(AmPmOfDay.PM.getShortText(Locale.US), "PM");
    }

//    public void test_getShortText_noText() {
//        assertEquals(AmPmOfDay.AM.getShortText(new Locale("", "")), "0");
//        assertEquals(AmPmOfDay.PM.getShortText(new Locale("", "")), "1");
//    }

    //-----------------------------------------------------------------------
    // getText()
    //-----------------------------------------------------------------------
    public void test_getText() {
        assertEquals(AmPmOfDay.AM.getText(Locale.US), "AM");
        assertEquals(AmPmOfDay.PM.getText(Locale.US), "PM");
    }

//    public void test_getText_noText() {
//        assertEquals(AmPmOfDay.AM.getText(new Locale("", "")), "0");
//        assertEquals(AmPmOfDay.PM.getText(new Locale("", "")), "1");
//    }

    //-----------------------------------------------------------------------
    // isAM()
    //-----------------------------------------------------------------------
    public void test_isAm() {
        assertEquals(AmPmOfDay.AM.isAm(), true);
        assertEquals(AmPmOfDay.PM.isAm(), false);
    }

    public void test_isPm() {
        assertEquals(AmPmOfDay.AM.isPm(), false);
        assertEquals(AmPmOfDay.PM.isPm(), true);
    }

    //-----------------------------------------------------------------------
    // adjustTime()
    //-----------------------------------------------------------------------
    public void test_adjustTime_AMusingPM() {
        LocalTime base = LocalTime.time(11, 30);
        LocalTime result = AmPmOfDay.PM.adjustTime(base);
        assertEquals(result, LocalTime.time(23, 30));
    }

    public void test_adjustTime_AMusingAM() {
        LocalTime base = LocalTime.time(11, 30);
        LocalTime result = AmPmOfDay.AM.adjustTime(base);
        assertEquals(result, LocalTime.time(11, 30));
    }

    public void test_adjustTime_PMusingAM() {
        LocalTime base = LocalTime.time(23, 30);
        LocalTime result = AmPmOfDay.AM.adjustTime(base);
        assertEquals(result, LocalTime.time(11, 30));
    }

    public void test_adjustTime_PMusingPM() {
        LocalTime base = LocalTime.time(23, 30);
        LocalTime result = AmPmOfDay.PM.adjustTime(base);
        assertEquals(result, LocalTime.time(23, 30));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustTime_nullLocalTime() {
        AmPmOfDay.AM.adjustTime((LocalTime) null);
    }

    //-----------------------------------------------------------------------
    // matchesTime()
    //-----------------------------------------------------------------------
    public void test_matchesTime() {
        assertEquals(AmPmOfDay.AM.matchesTime(LocalTime.time(11, 30)), true);
        assertEquals(AmPmOfDay.PM.matchesTime(LocalTime.time(11, 30)), false);
        assertEquals(AmPmOfDay.AM.matchesTime(LocalTime.time(23, 30)), false);
        assertEquals(AmPmOfDay.PM.matchesTime(LocalTime.time(23, 30)), true);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_matchesTime_nullLocalTime() {
        AmPmOfDay.AM.matchesTime((LocalTime) null);
    }

    //-----------------------------------------------------------------------
    // toCalendrical()
    //-----------------------------------------------------------------------
    public void test_toCalendrical() {
        assertEquals(AmPmOfDay.AM.toCalendrical(), Calendrical.calendrical(RULE, 0));
        assertEquals(AmPmOfDay.PM.toCalendrical(), Calendrical.calendrical(RULE, 1));
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        assertEquals(AmPmOfDay.AM.toString(), "AmPmOfDay=AM");
        assertEquals(AmPmOfDay.PM.toString(), "AmPmOfDay=PM");
    }

    //-----------------------------------------------------------------------
    // generated methods
    //-----------------------------------------------------------------------
    public void test_enum() {
        assertEquals(AmPmOfDay.valueOf("AM"), AmPmOfDay.AM);
        assertEquals(AmPmOfDay.values()[0], AmPmOfDay.AM);
    }

}
