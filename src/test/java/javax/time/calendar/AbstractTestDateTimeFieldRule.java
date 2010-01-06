/*
 * Copyright (c) 2009-2010, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendar;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.time.calendar.format.MockSimpleCalendrical;

import org.testng.annotations.Test;

/**
 * Base test for DateTimeFieldRule implementations.
 *
 * @author Stephen Colebourne
 */
@Test
public abstract class AbstractTestDateTimeFieldRule {

    private Calendrical testingCalendrical;
    private Object testingValue;
    private int testingInt;

    /**
     * Specify a valid set of fields for testing.
     * @param cal
     * @param value
     * @param intValue
     */
    protected AbstractTestDateTimeFieldRule(Calendrical cal, Object value, int intValue) {
        testingCalendrical = cal;
        testingValue = value;
        testingInt = intValue;
    }

    //-----------------------------------------------------------------------
    // Rule
    //-----------------------------------------------------------------------
    protected abstract DateTimeFieldRule<?> rule();

    //-----------------------------------------------------------------------
    // Basics
    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(DateTimeFieldRule.class.isAssignableFrom(rule().getClass()));
        assertTrue(Serializable.class.isAssignableFrom(rule().getClass()));
    }

    public void test_immutable() {
        Class<?> cls = rule().getClass();
        assertTrue(Modifier.isPublic(cls.getModifiers()) == false);
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
    // Singleton
    //-----------------------------------------------------------------------
    public void test_singleton() {
        assertSame(rule(), rule());
    }

    //-----------------------------------------------------------------------
    // Serialization
    //-----------------------------------------------------------------------
    public void test_serialization() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(rule());
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertSame(ois.readObject(), rule());
    }

    //-----------------------------------------------------------------------
    // convertValueToInt(T)
    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void test_convertValueToInt() {
        DateTimeFieldRule rule = rule();
        assertEquals(rule.convertValueToInt(1), 1);
        assertEquals(rule.convertValueToInt(6), 6);
    }

    //-----------------------------------------------------------------------
    // convertIntToValue(int)
    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void test_convertIntToValue() {
        DateTimeFieldRule rule = rule();
        assertEquals(rule.convertIntToValue(1), 1);
        assertEquals(rule.convertIntToValue(3), 3);
    }

    //-----------------------------------------------------------------------
    // isValidValue(int)
    //-----------------------------------------------------------------------
    public void test_isValidValue_int() {
        assertEquals(rule().isValidValue(rule().getLargestMinimumValue()), true);
        assertEquals(rule().isValidValue(rule().getSmallestMaximumValue()), true);
        assertEquals(rule().isValidValue(rule().getMinimumValue() - 1), false);
        assertEquals(rule().isValidValue(rule().getMaximumValue() + 1), false);
    }

    //-----------------------------------------------------------------------
    // isValidValue(long)
    //-----------------------------------------------------------------------
    public void test_isValidValue_long() {
        assertEquals(rule().isValidValue((long) rule().getLargestMinimumValue()), true);
        assertEquals(rule().isValidValue((long) rule().getSmallestMaximumValue()), true);
        assertEquals(rule().isValidValue((long) (rule().getMinimumValue() - 1)), false);
        assertEquals(rule().isValidValue((long) (rule().getMaximumValue() + 1)), false);
    }

    //-----------------------------------------------------------------------
    // checkValue(int)
    //-----------------------------------------------------------------------
    public void test_checkValue_int_valid() {
        rule().checkValue(rule().getLargestMinimumValue());
        rule().checkValue(rule().getSmallestMaximumValue());
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_checkValue_int_invalidSmall() {
        rule().checkValue(rule().getMinimumValue() - 1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_checkValue_int_invalidBig() {
        rule().checkValue(rule().getMaximumValue() + 1);
    }

    //-----------------------------------------------------------------------
    // checkValue(long)
    //-----------------------------------------------------------------------
    public void test_checkValue_long_valid() {
        rule().checkValue((long) rule().getLargestMinimumValue());
        rule().checkValue((long) rule().getSmallestMaximumValue());
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_checkValue_long_invalidSmall() {
        rule().checkValue((long) (rule().getMinimumValue() - 1));
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_checkValue_long_invalidBig() {
        rule().checkValue((long) (rule().getMaximumValue() + 1));
    }

    //-----------------------------------------------------------------------
    // getMinimumValue(Calendrical)
    //-----------------------------------------------------------------------
    public void test_getMinimumValue_Calendrical_noData() {
        Calendrical cal = new MockSimpleCalendrical();
        assertEquals(rule().getMinimumValue(cal), rule().getMinimumValue());
    }

    //-----------------------------------------------------------------------
    // getMaximumValue(Calendrical)
    //-----------------------------------------------------------------------
    public void test_getMaximumValue_Calendrical_noData() {
        Calendrical cal = new MockSimpleCalendrical();
        assertEquals(rule().getMaximumValue(cal), rule().getMaximumValue());
    }

    //-----------------------------------------------------------------------
    // getValue(Calendrical)
    //-----------------------------------------------------------------------
    public void test_getValue_fromTestingFields() {
        assertEquals(rule().getValue(testingCalendrical), testingValue);
    }

    public void test_getValue_Calendrical_cannotDerive() {
        Calendrical cal = new MockSimpleCalendrical();
        assertEquals(rule().getValue(cal), null);
    }

    //-----------------------------------------------------------------------
    // getValueChecked(Calendrical)
    //-----------------------------------------------------------------------
    public void test_getValueChecked_fromTestingFields() {
        assertEquals(rule().getValueChecked(testingCalendrical), testingValue);
    }

    @Test(expectedExceptions=UnsupportedRuleException.class)
    public void test_getValueChecked_Calendrical_cannotDerive() {
        Calendrical cal = new MockSimpleCalendrical();
        rule().getValueChecked(cal);
    }

    //-----------------------------------------------------------------------
    // getInteger(Calendrical)
    //-----------------------------------------------------------------------
    public void test_getInteger_fromTestingFields() {
        assertEquals(rule().getInteger(testingCalendrical), (Integer) testingInt);
    }

    public void test_getInteger_Calendrical_cannotDerive() {
        Calendrical cal = new MockSimpleCalendrical();
        assertEquals(rule().getInteger(cal), null);
    }

    //-----------------------------------------------------------------------
    // getInt(Calendrical)
    //-----------------------------------------------------------------------
    public void test_getInt_fromTestingFields() {
        assertEquals(rule().getInt(testingCalendrical), testingInt);
    }

    @Test(expectedExceptions=UnsupportedRuleException.class)
    public void test_getInt_Calendrical_cannotDerive() {
        Calendrical cal = new MockSimpleCalendrical();
        rule().getInt(cal);
    }

}
