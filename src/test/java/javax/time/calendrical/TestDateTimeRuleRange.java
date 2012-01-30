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
package javax.time.calendrical;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.testng.annotations.Test;

/**
 * Base test for DateTimeRuleRangeRule implementations.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestDateTimeRuleRange {

    //-----------------------------------------------------------------------
    // Basics
    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Serializable.class.isAssignableFrom(DateTimeRuleRange.class));
    }

    public void test_immutable() {
        Class<?> cls = DateTimeRuleRange.class;
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
    // Serialization
    //-----------------------------------------------------------------------
    public void test_serialization() throws Exception {
        Object obj = DateTimeRuleRange.of(1, 2, 3, 4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(ois.readObject(), obj);
    }

    //-----------------------------------------------------------------------
    // of(long,long)
    //-----------------------------------------------------------------------
    public void test_of_longlong() {
        DateTimeRuleRange test = DateTimeRuleRange.of(1, 12);
        assertEquals(test.getMinimum(), 1);
        assertEquals(test.getLargestMinimum(), 1);
        assertEquals(test.getSmallestMaximum(), 12);
        assertEquals(test.getMaximum(), 12);
        assertEquals(test.isFixed(), true);
        assertEquals(test.isIntValue(), true);
    }

    public void test_of_longlong_big() {
        DateTimeRuleRange test = DateTimeRuleRange.of(1, 123456789012345L);
        assertEquals(test.getMinimum(), 1);
        assertEquals(test.getLargestMinimum(), 1);
        assertEquals(test.getSmallestMaximum(), 123456789012345L);
        assertEquals(test.getMaximum(), 123456789012345L);
        assertEquals(test.isFixed(), true);
        assertEquals(test.isIntValue(), false);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_of_longlong_minGtMax() {
        DateTimeRuleRange.of(12, 1);
    }

    //-----------------------------------------------------------------------
    // of(long,long,long)
    //-----------------------------------------------------------------------
    public void test_of_longlonglong() {
        DateTimeRuleRange test = DateTimeRuleRange.of(1, 28, 31);
        assertEquals(test.getMinimum(), 1);
        assertEquals(test.getLargestMinimum(), 1);
        assertEquals(test.getSmallestMaximum(), 28);
        assertEquals(test.getMaximum(), 31);
        assertEquals(test.isFixed(), false);
        assertEquals(test.isIntValue(), true);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_of_longlonglong_minGtMax() {
        DateTimeRuleRange.of(12, 1, 2);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_of_longlonglong_smallestmaxminGtMax() {
        DateTimeRuleRange.of(1, 31, 28);
    }

    //-----------------------------------------------------------------------
    // of(long,long,long,long)
    //-----------------------------------------------------------------------
    public void test_of_longlonglonglong() {
        DateTimeRuleRange test = DateTimeRuleRange.of(1, 2, 28, 31);
        assertEquals(test.getMinimum(), 1);
        assertEquals(test.getLargestMinimum(), 2);
        assertEquals(test.getSmallestMaximum(), 28);
        assertEquals(test.getMaximum(), 31);
        assertEquals(test.isFixed(), false);
        assertEquals(test.isIntValue(), true);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_of_longlonglonglong_minGtMax() {
        DateTimeRuleRange.of(12, 13, 1, 2);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_of_longlonglonglong_smallestmaxminGtMax() {
        DateTimeRuleRange.of(1, 2, 31, 28);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_of_longlonglong_minGtLargestMin() {
        DateTimeRuleRange.of(2, 1, 31, 28);
    }

    //-----------------------------------------------------------------------
    // isValidValue(long)
    //-----------------------------------------------------------------------
    public void test_isValidValue_long() {
        DateTimeRuleRange test = DateTimeRuleRange.of(1, 28, 31);
        assertEquals(test.isValidValue(0), false);
        assertEquals(test.isValidValue(1), true);
        assertEquals(test.isValidValue(2), true);
        assertEquals(test.isValidValue(30), true);
        assertEquals(test.isValidValue(31), true);
        assertEquals(test.isValidValue(32), false);
    }

    //-----------------------------------------------------------------------
    // isValidIntValue(long)
    //-----------------------------------------------------------------------
    public void test_isValidValue_long_int() {
        DateTimeRuleRange test = DateTimeRuleRange.of(1, 28, 31);
        assertEquals(test.isValidValue(0), false);
        assertEquals(test.isValidValue(1), true);
        assertEquals(test.isValidValue(31), true);
        assertEquals(test.isValidValue(32), false);
    }

    public void test_isValidValue_long_long() {
        DateTimeRuleRange test = DateTimeRuleRange.of(1, 28, Integer.MAX_VALUE + 1L);
        assertEquals(test.isValidIntValue(0), false);
        assertEquals(test.isValidIntValue(1), false);
        assertEquals(test.isValidIntValue(31), false);
        assertEquals(test.isValidIntValue(32), false);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    public void test_equals1() {
        DateTimeRuleRange a = DateTimeRuleRange.of(1, 2, 3, 4);
        DateTimeRuleRange b = DateTimeRuleRange.of(1, 2, 3, 4);
        assertEquals(a.equals(a), true);
        assertEquals(a.equals(b), true);
        assertEquals(b.equals(a), true);
        assertEquals(b.equals(b), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
    }

    public void test_equals2() {
        DateTimeRuleRange a = DateTimeRuleRange.of(1, 2, 3, 4);
        assertEquals(a.equals(DateTimeRuleRange.of(0, 2, 3, 4)), false);
        assertEquals(a.equals(DateTimeRuleRange.of(1, 3, 3, 4)), false);
        assertEquals(a.equals(DateTimeRuleRange.of(1, 2, 4, 4)), false);
        assertEquals(a.equals(DateTimeRuleRange.of(1, 2, 3, 5)), false);
    }

    public void test_equals_otherType() {
        DateTimeRuleRange a = DateTimeRuleRange.of(1, 12);
        assertEquals(a.equals("Rubbish"), false);
    }

    public void test_equals_null() {
        DateTimeRuleRange a = DateTimeRuleRange.of(1, 12);
        assertEquals(a.equals(null), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        assertEquals(DateTimeRuleRange.of(1, 1, 4, 4).toString(), "1 - 4");
        assertEquals(DateTimeRuleRange.of(1, 1, 3, 4).toString(), "1 - 3/4");
        assertEquals(DateTimeRuleRange.of(1, 2, 3, 4).toString(), "1/2 - 3/4");
        assertEquals(DateTimeRuleRange.of(1, 2, 4, 4).toString(), "1/2 - 4");
    }

}
