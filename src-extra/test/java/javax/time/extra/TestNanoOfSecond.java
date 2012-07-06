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
package javax.time.extra;

import static javax.time.calendrical.LocalDateTimeField.NANO_OF_SECOND;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
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
import javax.time.calendrical.CalendricalObject;
import javax.time.calendrical.DateTimeAdjuster;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test NanoOfSecond.
 */
@Test
public class TestNanoOfSecond {

    private static final int MAX_LENGTH = 999999999;
    private static final int SKIP = 500000;

    @BeforeMethod
    public void setUp() {
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Serializable.class.isAssignableFrom(NanoOfSecond.class));
        assertTrue(Comparable.class.isAssignableFrom(NanoOfSecond.class));
        assertTrue(DateTimeAdjuster.class.isAssignableFrom(NanoOfSecond.class));
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        NanoOfSecond test = NanoOfSecond.of(1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), test);
    }

    public void test_immutable() {
        Class<NanoOfSecond> cls = NanoOfSecond.class;
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
    public void test_constant_ZERO() {
        assertEquals(NanoOfSecond.ZERO.getValue(), 0);
        assertSame(NanoOfSecond.ZERO, NanoOfSecond.of(0));
    }

    //-----------------------------------------------------------------------
    public void test_factory_int() {
        for (int i = 0; i <= MAX_LENGTH; i += SKIP) {
            NanoOfSecond test = NanoOfSecond.of(i);
            assertEquals(test.getValue(), i);
            assertEquals(NanoOfSecond.of(i), test);
        }
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_factory_int_minuteTooLow() {
        NanoOfSecond.of(-1);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_factory_int_hourTooHigh() {
        NanoOfSecond.of(1000000000);
    }

    //-----------------------------------------------------------------------
    public void test_factory_CalendricalObject() {
        LocalTime time = LocalTime.of(5, 10, 20, 0);
        for (int i = 0; i <= MAX_LENGTH; i += SKIP) {
            NanoOfSecond test = NanoOfSecond.from(time);
            assertEquals(test.getValue(), i);
            time = time.plusNanos(SKIP);
        }
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_factory_Calendrical_noDerive() {
        NanoOfSecond.from(LocalDate.of(2012, 3, 2));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_CalendricalObject_null() {
        NanoOfSecond.from((CalendricalObject) null);
    }

//    //-----------------------------------------------------------------------
//    // getFractionalValue()
//    //-----------------------------------------------------------------------
//    public void test_getFractionalValue() {
//        // separate test for zero, due to BigDecimal bug 6480539
//        NanoOfSecond test = NanoOfSecond.of(0);
//        assertEquals(test.getFractionalValue(), new BigDecimal(0));
//        
//        for (int i = SKIP; i <= MAX_LENGTH; i += SKIP) {
//            test = NanoOfSecond.of(i);
//            assertEquals(test.getFractionalValue(), new BigDecimal(i).movePointLeft(9).stripTrailingZeros());
//        }
//    }

    //-----------------------------------------------------------------------
    public void test_getField() {
        assertSame(NanoOfSecond.of(1).getField(), NANO_OF_SECOND);
    }

    //-----------------------------------------------------------------------
    // adjustTime()
    //-----------------------------------------------------------------------
    public void test_adjustTime() {
        LocalTime base = LocalTime.of(5, 10, 20, 0);
        LocalTime expected = base;
        for (int i = 0; i <= MAX_LENGTH; i += SKIP) {
            NanoOfSecond test = NanoOfSecond.of(i);
            assertEquals(test.adjustCalendrical(base), expected);
            expected = expected.plusNanos(SKIP);
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustTime_nullLocalTime() {
        NanoOfSecond test = NanoOfSecond.of(1);
        test.adjustCalendrical((LocalTime) null);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo() {
        for (int i = 0; i <= MAX_LENGTH; i += SKIP) {
            NanoOfSecond a = NanoOfSecond.of(i);
            for (int j = 0; j <= MAX_LENGTH; j += SKIP) {
                NanoOfSecond b = NanoOfSecond.of(j);
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
    public void test_compareTo_nullNanoOfSecond() {
        NanoOfSecond doy = null;
        NanoOfSecond test = NanoOfSecond.of(1);
        test.compareTo(doy);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    public void test_equals() {
        for (int i = 0; i <= MAX_LENGTH; i += SKIP) {
            NanoOfSecond a = NanoOfSecond.of(i);
            for (int j = 0; j <= MAX_LENGTH; j += SKIP) {
                NanoOfSecond b = NanoOfSecond.of(j);
                assertEquals(a.equals(b), i == j);
                assertEquals(a.hashCode() == b.hashCode(), i == j);
            }
        }
    }

    public void test_equals_nullNanoOfSecond() {
        NanoOfSecond doy = null;
        NanoOfSecond test = NanoOfSecond.of(1);
        assertEquals(test.equals(doy), false);
    }

    public void test_equals_incorrectType() {
        NanoOfSecond test = NanoOfSecond.of(1);
        assertEquals(test.equals("Incorrect type"), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        for (int i = 0; i <= MAX_LENGTH; i += SKIP) {
            NanoOfSecond a = NanoOfSecond.of(i);
            assertEquals(a.toString(), "NanoOfSecond=" + i);
        }
    }

}
