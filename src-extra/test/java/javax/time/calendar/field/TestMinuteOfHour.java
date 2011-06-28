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
package javax.time.calendar.field;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.time.CalendricalException;
import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalMatcher;
import javax.time.calendar.DateTimeFields;
import javax.time.calendar.DateTimeRule;
import javax.time.calendar.ISODateTimeRule;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.LocalDate;
import javax.time.calendar.LocalTime;
import javax.time.calendar.TimeAdjuster;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test MinuteOfHour.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestMinuteOfHour {

    private static final DateTimeRule RULE = ISODateTimeRule.MINUTE_OF_HOUR;
    private static final int MAX_LENGTH = 59;

    @BeforeMethod
    public void setUp() {
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Calendrical.class.isAssignableFrom(MinuteOfHour.class));
        assertTrue(Serializable.class.isAssignableFrom(MinuteOfHour.class));
        assertTrue(Comparable.class.isAssignableFrom(MinuteOfHour.class));
        assertTrue(TimeAdjuster.class.isAssignableFrom(MinuteOfHour.class));
        assertTrue(CalendricalMatcher.class.isAssignableFrom(MinuteOfHour.class));
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        MinuteOfHour test = MinuteOfHour.minuteOfHour(1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), test);
    }

    public void test_immutable() {
        Class<MinuteOfHour> cls = MinuteOfHour.class;
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
        assertEquals(MinuteOfHour.rule(), RULE);
    }

    //-----------------------------------------------------------------------
    public void test_factory_int() {
        for (int i = 0; i <= MAX_LENGTH; i++) {
            MinuteOfHour test = MinuteOfHour.minuteOfHour(i);
            assertEquals(test.getValue(), i);
            assertEquals(MinuteOfHour.minuteOfHour(i), test);
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_minuteTooLow() {
        MinuteOfHour.minuteOfHour(-1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_hourTooHigh() {
        MinuteOfHour.minuteOfHour(60);
    }

    //-----------------------------------------------------------------------
    public void test_factory_Calendrical() {
        LocalTime time = LocalTime.of(5, 0, 10, 20);
        for (int i = 0; i <= MAX_LENGTH; i++) {
            MinuteOfHour test = MinuteOfHour.minuteOfHour(time);
            assertEquals(test.getValue(), i);
            time = time.plusMinutes(1);
        }
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_factory_Calendrical_noData() {
        MinuteOfHour.minuteOfHour(DateTimeFields.EMPTY);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_nullCalendrical() {
        MinuteOfHour.minuteOfHour((Calendrical) null);
    }

    //-----------------------------------------------------------------------
    // adjustTime()
    //-----------------------------------------------------------------------
    public void test_adjustTime() {
        LocalTime base = LocalTime.of(5, 0, 10, 20);
        LocalTime expected = base;
        for (int i = 0; i <= MAX_LENGTH; i++) {
            MinuteOfHour test = MinuteOfHour.minuteOfHour(i);
            assertEquals(test.adjustTime(base), expected);
            expected = expected.plusMinutes(1);
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustTime_nullLocalTime() {
        MinuteOfHour test = MinuteOfHour.minuteOfHour(1);
        test.adjustTime((LocalTime) null);
    }

    //-----------------------------------------------------------------------
    // matchesCalendrical(Calendrical)
    //-----------------------------------------------------------------------
    public void test_matchesCalendrical() {
        LocalTime work = LocalTime.of(5, 0, 10, 20);
        for (int i = 0; i <= MAX_LENGTH; i++) {
            for (int j = 0; j <= MAX_LENGTH; j++) {
                MinuteOfHour test = MinuteOfHour.minuteOfHour(j);
                assertEquals(test.matchesCalendrical(work), i == j);
            }
            work = work.plusMinutes(1);
        }
    }

    public void test_matchesCalendrical_noData() {
        assertEquals(MinuteOfHour.minuteOfHour(12).matchesCalendrical(LocalDate.of(2008, 6, 30)), false);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_matchesCalendrical_null() {
        MinuteOfHour test = MinuteOfHour.minuteOfHour(1);
        test.matchesCalendrical(null);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo() {
        for (int i = 0; i <= MAX_LENGTH; i++) {
            MinuteOfHour a = MinuteOfHour.minuteOfHour(i);
            for (int j = 0; j <= MAX_LENGTH; j++) {
                MinuteOfHour b = MinuteOfHour.minuteOfHour(j);
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
    public void test_compareTo_nullMinuteOfHour() {
        MinuteOfHour doy = null;
        MinuteOfHour test = MinuteOfHour.minuteOfHour(1);
        test.compareTo(doy);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    public void test_equals() {
        for (int i = 0; i <= MAX_LENGTH; i++) {
            MinuteOfHour a = MinuteOfHour.minuteOfHour(i);
            for (int j = 0; j <= MAX_LENGTH; j++) {
                MinuteOfHour b = MinuteOfHour.minuteOfHour(j);
                assertEquals(a.equals(b), i == j);
                assertEquals(a.hashCode() == b.hashCode(), i == j);
            }
        }
    }

    public void test_equals_nullMinuteOfHour() {
        MinuteOfHour doy = null;
        MinuteOfHour test = MinuteOfHour.minuteOfHour(1);
        assertEquals(test.equals(doy), false);
    }

    public void test_equals_incorrectType() {
        MinuteOfHour test = MinuteOfHour.minuteOfHour(1);
        assertEquals(test.equals("Incorrect type"), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        for (int i = 0; i <= MAX_LENGTH; i++) {
            MinuteOfHour a = MinuteOfHour.minuteOfHour(i);
            assertEquals(a.toString(), "MinuteOfHour=" + i);
        }
    }

}
