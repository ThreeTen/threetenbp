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
import javax.time.LocalDate;
import javax.time.LocalTime;
import javax.time.calendrical.Calendrical;
import javax.time.calendrical.CalendricalMatcher;
import javax.time.calendrical.DateTimeFields;
import javax.time.calendrical.DateTimeRule;
import javax.time.calendrical.ISODateTimeRule;
import javax.time.calendrical.IllegalCalendarFieldValueException;
import javax.time.calendrical.TimeAdjuster;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test SecondOfMinute.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestSecondOfMinute {

    private static final DateTimeRule RULE = ISODateTimeRule.SECOND_OF_MINUTE;
    private static final int MAX_LENGTH = 59;

    @BeforeMethod
    public void setUp() {
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Calendrical.class.isAssignableFrom(SecondOfMinute.class));
        assertTrue(Serializable.class.isAssignableFrom(SecondOfMinute.class));
        assertTrue(Comparable.class.isAssignableFrom(SecondOfMinute.class));
        assertTrue(TimeAdjuster.class.isAssignableFrom(SecondOfMinute.class));
        assertTrue(CalendricalMatcher.class.isAssignableFrom(SecondOfMinute.class));
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        SecondOfMinute test = SecondOfMinute.secondOfMinute(1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), test);
    }

    public void test_immutable() {
        Class<SecondOfMinute> cls = SecondOfMinute.class;
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
        assertEquals(SecondOfMinute.rule(), RULE);
    }

    //-----------------------------------------------------------------------
    public void test_factory_int() {
        for (int i = 0; i <= MAX_LENGTH; i++) {
            SecondOfMinute test = SecondOfMinute.secondOfMinute(i);
            assertEquals(test.getValue(), i);
            assertEquals(SecondOfMinute.secondOfMinute(i), test);
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_minuteTooLow() {
        SecondOfMinute.secondOfMinute(-1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_hourTooHigh() {
        SecondOfMinute.secondOfMinute(60);
    }

    //-----------------------------------------------------------------------
    public void test_factory_Calendrical() {
        LocalTime time = LocalTime.of(5, 10, 0, 20);
        for (int i = 0; i <= MAX_LENGTH; i++) {
            SecondOfMinute test = SecondOfMinute.secondOfMinute(time);
            assertEquals(test.getValue(), i);
            time = time.plusSeconds(1);
        }
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_factory_Calendrical_noData() {
        SecondOfMinute.secondOfMinute(DateTimeFields.EMPTY);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_nullCalendrical() {
        SecondOfMinute.secondOfMinute((Calendrical) null);
    }

    //-----------------------------------------------------------------------
    // adjustTime()
    //-----------------------------------------------------------------------
    public void test_adjustTime() {
        LocalTime base = LocalTime.of(5, 10, 0, 20);
        LocalTime expected = base;
        for (int i = 0; i <= MAX_LENGTH; i++) {
            SecondOfMinute test = SecondOfMinute.secondOfMinute(i);
            assertEquals(test.adjustTime(base), expected);
            expected = expected.plusSeconds(1);
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustTime_nullLocalTime() {
        SecondOfMinute test = SecondOfMinute.secondOfMinute(1);
        test.adjustTime((LocalTime) null);
    }

    //-----------------------------------------------------------------------
    // matchesCalendrical(Calendrical)
    //-----------------------------------------------------------------------
    public void test_matchesCalendrical() {
        LocalTime work = LocalTime.of(5, 10, 0, 20);
        for (int i = 0; i <= MAX_LENGTH; i++) {
            for (int j = 0; j <= MAX_LENGTH; j++) {
                SecondOfMinute test = SecondOfMinute.secondOfMinute(j);
                assertEquals(test.matchesCalendrical(work), i == j);
            }
            work = work.plusSeconds(1);
        }
    }

    public void test_matchesCalendrical_noData() {
        assertEquals(SecondOfMinute.secondOfMinute(12).matchesCalendrical(LocalDate.of(2008, 6, 30)), false);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_matchesCalendrical_null() {
        SecondOfMinute test = SecondOfMinute.secondOfMinute(1);
        test.matchesCalendrical(null);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo() {
        for (int i = 0; i <= MAX_LENGTH; i++) {
            SecondOfMinute a = SecondOfMinute.secondOfMinute(i);
            for (int j = 0; j <= MAX_LENGTH; j++) {
                SecondOfMinute b = SecondOfMinute.secondOfMinute(j);
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
    public void test_compareTo_nullSecondOfMinute() {
        SecondOfMinute doy = null;
        SecondOfMinute test = SecondOfMinute.secondOfMinute(1);
        test.compareTo(doy);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    public void test_equals() {
        for (int i = 0; i <= MAX_LENGTH; i++) {
            SecondOfMinute a = SecondOfMinute.secondOfMinute(i);
            for (int j = 0; j <= MAX_LENGTH; j++) {
                SecondOfMinute b = SecondOfMinute.secondOfMinute(j);
                assertEquals(a.equals(b), i == j);
                assertEquals(a.hashCode() == b.hashCode(), i == j);
            }
        }
    }

    public void test_equals_nullSecondOfMinute() {
        SecondOfMinute doy = null;
        SecondOfMinute test = SecondOfMinute.secondOfMinute(1);
        assertEquals(test.equals(doy), false);
    }

    public void test_equals_incorrectType() {
        SecondOfMinute test = SecondOfMinute.secondOfMinute(1);
        assertEquals(test.equals("Incorrect type"), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        for (int i = 0; i <= MAX_LENGTH; i++) {
            SecondOfMinute a = SecondOfMinute.secondOfMinute(i);
            assertEquals(a.toString(), "SecondOfMinute=" + i);
        }
    }

}
