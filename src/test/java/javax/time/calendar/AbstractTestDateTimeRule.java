/*
 * Copyright (c) 2009-2011, Stephen Colebourne & Michael Nascimento Santos
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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.time.CalendricalException;
import javax.time.calendar.format.MockSimpleCalendrical;

import org.testng.annotations.Test;

/**
 * Base test for DateTimeFieldRule implementations.
 *
 * @author Stephen Colebourne
 */
@Test
public abstract class AbstractTestDateTimeRule {

    private Calendrical testingCalendrical;
    private long testingValue;

    /**
     * Specify a valid set of fields for testing.
     * @param cal
     * @param testingValue
     */
    protected AbstractTestDateTimeRule(Calendrical cal, long testingValue) {
        this.testingCalendrical = cal;
        this.testingValue = testingValue;
    }

    //-----------------------------------------------------------------------
    // Rule
    //-----------------------------------------------------------------------
    protected abstract DateTimeRule rule();

    //-----------------------------------------------------------------------
    // Basics
    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(DateTimeRule.class.isAssignableFrom(rule().getClass()));
        assertTrue(Serializable.class.isAssignableFrom(rule().getClass()));
    }

    public void test_immutable() {
        Class<?> cls = rule().getClass();
        //assertTrue(Modifier.isPublic(cls.getModifiers()) == false);
        assertTrue(Modifier.isFinal(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                assertTrue(Modifier.isFinal(field.getModifiers()), "Field:" + field.getName());
            } else {
                assertFalse(Modifier.isPublic(field.getModifiers()), "Field:" + field.getName());
                assertFalse(Modifier.isProtected(field.getModifiers()), "Field:" + field.getName());
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
    // checkIntValue()
    //-----------------------------------------------------------------------
    @Test(expectedExceptions=CalendricalRuleException.class)
    public void test_checkIntValue_valid() {
        if (rule().getValueRange().isIntValue()) {
            rule().checkIntValue();
            throw new CalendricalRuleException("Dummy", rule());
        } else {
            rule().checkIntValue();
        }
    }

    //-----------------------------------------------------------------------
    // checkValidValue(long)
    //-----------------------------------------------------------------------
    public void test_checkValue_long_valid() {
        rule().checkValidValue(rule().getValueRange().getLargestMinimum());
        rule().checkValidValue(testingValue);
        rule().checkValidValue(rule().getValueRange().getSmallestMaximum());
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_checkValue_long_invalidSmall() {
        if (rule().getValueRange().getMinimum() > Long.MIN_VALUE) {
            rule().checkValidValue((rule().getValueRange().getMinimum() - 1));
        } else {
            throw new IllegalCalendarFieldValueException(rule(), 0);
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_checkValue_long_invalidBig() {
        if (rule().getValueRange().getMaximum() < Long.MAX_VALUE) {
            rule().checkValidValue((rule().getValueRange().getMaximum() + 1));
        } else {
            throw new IllegalCalendarFieldValueException(rule(), 0);
        }
    }

    //-----------------------------------------------------------------------
    // getRange(Calendrical)
    //-----------------------------------------------------------------------
    public void test_getRange_Calendrical_noData() {
        Calendrical cal = new MockSimpleCalendrical();
        assertSame(rule().getValueRange(cal), rule().getValueRange());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_getRange_Calendrical_null() {
        rule().getValueRange(null);
    }

    //-----------------------------------------------------------------------
    // getValue(Calendrical)
    //-----------------------------------------------------------------------
    public void test_getValue_fromTestingFields() {
        assertEquals(rule().getValue(testingCalendrical), DateTimeField.of(rule(), testingValue));
    }

    public void test_getValue_Calendrical_cannotDerive() {
        Calendrical cal = new MockSimpleCalendrical();
        assertEquals(rule().getValue(cal), null);
    }

    //-----------------------------------------------------------------------
    // getValueChecked(Calendrical)
    //-----------------------------------------------------------------------
    public void test_getValueChecked_fromTestingFields() {
        assertEquals(rule().getValueChecked(testingCalendrical), DateTimeField.of(rule(), testingValue));
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_getValueChecked_Calendrical_cannotDerive() {
        Calendrical cal = new MockSimpleCalendrical();
        rule().getValueChecked(cal);
    }

    //-----------------------------------------------------------------------
    // field(int)
    //-----------------------------------------------------------------------
    public void test_field_long() {
        assertEquals(rule().field(testingValue), DateTimeField.of(rule(), testingValue));
    }

}
