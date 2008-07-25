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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

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
 * Test HourOfDay.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestHourOfDay {

    private static final DateTimeFieldRule RULE = ISOChronology.hourOfDayRule();
    private static final int MAX_LENGTH = 23;

    @BeforeMethod
    public void setUp() {
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(CalendricalProvider.class.isAssignableFrom(HourOfDay.class));
        assertTrue(Serializable.class.isAssignableFrom(HourOfDay.class));
        assertTrue(Comparable.class.isAssignableFrom(HourOfDay.class));
        assertTrue(TimeAdjuster.class.isAssignableFrom(HourOfDay.class));
        assertTrue(TimeMatcher.class.isAssignableFrom(HourOfDay.class));
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        HourOfDay test = HourOfDay.hourOfDay(1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), test);
    }

    public void test_immutable() {
        Class<HourOfDay> cls = HourOfDay.class;
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                assertTrue(Modifier.isFinal(field.getModifiers()), "Field:" + field.getName());
            } else {
                assertTrue(Modifier.isPrivate(field.getModifiers()), "Field:" + field.getName());
                assertTrue(Modifier.isFinal(field.getModifiers()), "Field:" + field.getName());
            }
        }
    }

    //-----------------------------------------------------------------------
    public void test_rule() {
        assertEquals(HourOfDay.rule(), RULE);
    }

    //-----------------------------------------------------------------------
    public void test_factory_int() {
        for (int i = 0; i <= MAX_LENGTH; i++) {
            HourOfDay test = HourOfDay.hourOfDay(i);
            assertEquals(test.getValue(), i);
            assertEquals(HourOfDay.hourOfDay(i), test);
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_minuteTooLow() {
        HourOfDay.hourOfDay(-1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_hourTooHigh() {
        HourOfDay.hourOfDay(24);
    }

    //-----------------------------------------------------------------------
    public void test_factory_AmPmInt() {
        for (int i = 0; i <= MAX_LENGTH; i++) {
            HourOfDay test = HourOfDay.hourOfDay(i < 12 ? AmPmOfDay.AM : AmPmOfDay.PM, i % 12);
            assertEquals(test.getValue(), i);
            assertEquals(HourOfDay.hourOfDay(i), test);
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_AmPmInt_hourTooLow() {
        HourOfDay.hourOfDay(AmPmOfDay.AM, -1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_AmPmInt_hourTooHigh() {
        HourOfDay.hourOfDay(AmPmOfDay.AM, 12);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_AmPmInt_nullAmPm() {
        HourOfDay.hourOfDay((AmPmOfDay) null, 1);
    }

    //-----------------------------------------------------------------------
    public void test_factory_TimeProvider() {
        LocalTime time = LocalTime.time(0, 20);
        for (int i = 0; i <= MAX_LENGTH; i++) {
            HourOfDay test = HourOfDay.hourOfDay(time);
            assertEquals(test.getValue(), i);
            time = time.plusHours(1);
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_nullTimeProvider() {
        HourOfDay.hourOfDay((TimeProvider) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_badTimeProvider() {
        HourOfDay.hourOfDay(new MockTimeProviderReturnsNull());
    }

    //-----------------------------------------------------------------------
    // adjustTime()
    //-----------------------------------------------------------------------
    public void test_adjustTime() {
        LocalTime base = LocalTime.time(0, 20);
        LocalTime expected = base;
        for (int i = 0; i <= MAX_LENGTH; i++) {
            HourOfDay test = HourOfDay.hourOfDay(i);
            assertEquals(test.adjustTime(base), expected);
            expected = expected.plusHours(1);
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustTime_nullLocalTime() {
        HourOfDay test = HourOfDay.hourOfDay(1);
        test.adjustTime((LocalTime) null);
    }

    //-----------------------------------------------------------------------
    // matchesTime()
    //-----------------------------------------------------------------------
    public void test_matchesTime() {
        LocalTime work = LocalTime.time(0, 20);
        for (int i = 0; i <= MAX_LENGTH; i++) {
            for (int j = 0; j <= MAX_LENGTH; j++) {
                HourOfDay test = HourOfDay.hourOfDay(j);
                assertEquals(test.matchesTime(work), i == j);
            }
            work = work.plusHours(1);
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_matchesTime_nullLocalTime() {
        HourOfDay test = HourOfDay.hourOfDay(1);
        test.matchesTime((LocalTime) null);
    }

    //-----------------------------------------------------------------------
    // getAmPm()
    //-----------------------------------------------------------------------
    public void test_getAmPm() {
        for (int i = 0; i <= MAX_LENGTH; i++) {
            HourOfDay test = HourOfDay.hourOfDay(i);
            assertEquals(test.getAmPm(), i < 12 ? AmPmOfDay.AM : AmPmOfDay.PM);
        }
    }

    //-----------------------------------------------------------------------
    // getHourOfAmPm()
    //-----------------------------------------------------------------------
    public void test_getHourOfAmPm() {
        for (int i = 0; i <= MAX_LENGTH; i++) {
            HourOfDay test = HourOfDay.hourOfDay(i);
            assertEquals(test.getHourOfAmPm(), i % 12);
        }
    }

    //-----------------------------------------------------------------------
    // getClockHourOfAmPm()
    //-----------------------------------------------------------------------
    public void test_getClockHourOfAmPm() {
        for (int i = 0; i <= MAX_LENGTH; i++) {
            HourOfDay test = HourOfDay.hourOfDay(i);
            assertEquals(test.getClockHourOfAmPm(), (i % 12 == 0 ? 12 : i % 12));
        }
    }

    //-----------------------------------------------------------------------
    // getClockHourOfDay()
    //-----------------------------------------------------------------------
    public void test_getClockHourOfDay() {
        for (int i = 0; i <= MAX_LENGTH; i++) {
            HourOfDay test = HourOfDay.hourOfDay(i);
            assertEquals(test.getClockHourOfDay(), (i == 0 ? 24 : i));
        }
    }

    //-----------------------------------------------------------------------
    // toCalendrical()
    //-----------------------------------------------------------------------
    public void test_toCalendrical() {
        for (int i = 0; i <= MAX_LENGTH; i++) {
            HourOfDay test = HourOfDay.hourOfDay(i);
            assertEquals(test.toCalendrical(), Calendrical.calendrical(RULE, i));
        }
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo() {
        for (int i = 0; i <= MAX_LENGTH; i++) {
            HourOfDay a = HourOfDay.hourOfDay(i);
            for (int j = 0; j <= MAX_LENGTH; j++) {
                HourOfDay b = HourOfDay.hourOfDay(j);
                if (i < j) {
                    assertEquals(a.compareTo(b), -1);
                    assertEquals(b.compareTo(a), 1);
                } else if (i > j) {
                    assertEquals(a.compareTo(b), 1);
                    assertEquals(b.compareTo(a), -1);
                } else {
                    assertEquals(a.compareTo(b), 0);
                    assertEquals(b.compareTo(a), 0);
                }
            }
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_compareTo_nullHourOfDay() {
        HourOfDay doy = null;
        HourOfDay test = HourOfDay.hourOfDay(1);
        test.compareTo(doy);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    public void test_equals() {
        for (int i = 0; i <= MAX_LENGTH; i++) {
            HourOfDay a = HourOfDay.hourOfDay(i);
            for (int j = 0; j <= MAX_LENGTH; j++) {
                HourOfDay b = HourOfDay.hourOfDay(j);
                assertEquals(a.equals(b), i == j);
                assertEquals(a.hashCode() == b.hashCode(), i == j);
            }
        }
    }

    public void test_equals_nullHourOfDay() {
        HourOfDay doy = null;
        HourOfDay test = HourOfDay.hourOfDay(1);
        assertEquals(test.equals(doy), false);
    }

    public void test_equals_incorrectType() {
        HourOfDay test = HourOfDay.hourOfDay(1);
        assertEquals(test.equals("Incorrect type"), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        for (int i = 0; i <= MAX_LENGTH; i++) {
            HourOfDay a = HourOfDay.hourOfDay(i);
            assertEquals(a.toString(), "HourOfDay=" + i);
        }
    }

}
