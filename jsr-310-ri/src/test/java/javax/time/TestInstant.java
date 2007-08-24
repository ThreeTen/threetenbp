/*
 * Copyright (c) 2007, Stephen Colebourne & Michael Nascimento Santos
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

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test Instant.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestInstant {

    //-----------------------------------------------------------------------
    public void test_isSerializable() {
        Instant t = Instant.instant(0L, 0);
        assertTrue(t instanceof Serializable);
    }

    public void test_isComparable() {
        Instant t = Instant.instant(0L, 0);
        assertTrue(t instanceof Comparable);
    }

    //-----------------------------------------------------------------------
    public void factory_instant_long() {
        for (long i = -2; i <= 2; i++) {
            Instant t = Instant.instant(i);
            assertEquals(t.getEpochSeconds(), i);
            assertEquals(t.getNanoOfSecond(), 0);
        }
    }

    public void factory_instant_long_int() {
        for (long i = -2; i <= 2; i++) {
            for (int j = 0; j < 10; j++) {
                Instant t = Instant.instant(i, j);
                assertEquals(t.getEpochSeconds(), i);
                assertEquals(t.getNanoOfSecond(), j);
            }
            for (int j = 999999990; j < 1000000000; j++) {
                Instant t = Instant.instant(i, j);
                assertEquals(t.getEpochSeconds(), i);
                assertEquals(t.getNanoOfSecond(), j);
            }
        }
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_instant_long_int_nanosNegative() {
        Instant.instant(0L, -1);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_instant_long_int_nanosTooLarge() {
        Instant.instant(0L, 1000000000);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="MillisInstantNoNanos")
    Object[][] provider_factory_millisInstant_long() {
        return new Object[][] {
            {0, 0, 0},
            {1, 0, 1000000},
            {2, 0, 2000000},
            {999, 0, 999000000},
            {1000, 1, 0},
            {1001, 1, 1000000},
            {-1, -1, 999000000},
            {-2, -1, 998000000},
            {-999, -1, 1000000},
            {-1000, -1, 0},
            {-1001, -2, 999000000},
        };
    }

    @Test(dataProvider="MillisInstantNoNanos")
    public void factory_millisInstant_long(long millis, long expectedSeconds, int expectedNanoOfSecond) {
        Instant t = Instant.millisInstant(millis);
        assertEquals(t.getEpochSeconds(), expectedSeconds);
        assertEquals(t.getNanoOfSecond(), expectedNanoOfSecond);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="MillisInstantWithNanos")
    Object[][] provider_factory_millisInstant_long_int() {
        return new Object[][] {
            {0, 0, 0, 0},
            {1, 0, 0, 1000000},
            {2, 0, 0, 2000000},
            {999, 0, 0, 999000000},
            {1000, 0, 1, 0},
            {1001, 0, 1, 1000000},
            {-1, 0, -1, 999000000},
            {-2, 0, -1, 998000000},
            {-999, 0, -1, 1000000},
            {-1000, 0, -1, 0},
            {-1001, 0, -2, 999000000},
            {0, 1, 0, 1},
            {1, 1, 0, 1000001},
            {999, 1, 0, 999000001},
            {1000, 1, 1, 1},
            {1001, 1, 1, 1000001},
            {-1, 1, -1, 999000001},
            {-2, 1, -1, 998000001},
            {-999, 1, -1, 1000001},
            {-1000, 1, -1, 1},
            {-1001, 1, -2, 999000001},
            {0, 999999, 0, 999999},
            {1, 999999, 0, 1999999},
            {999, 999999, 0, 999999999},
            {1000, 999999, 1, 999999},
            {1001, 999999, 1, 1999999},
            {-1, 999999, -1, 999999999},
            {-2, 999999, -1, 998999999},
            {-999, 999999, -1, 1999999},
            {-1000, 999999, -1, 999999},
            {-1001, 999999, -2, 999999999},
        };
    }

    @Test(dataProvider="MillisInstantWithNanos")
    public void factory_millisInstant_long_int(long millis, int nanos, long expectedSeconds, int expectedNanoOfSecond) {
        Instant t = Instant.millisInstant(millis, nanos);
        assertEquals(t.getEpochSeconds(), expectedSeconds);
        assertEquals(t.getNanoOfSecond(), expectedNanoOfSecond);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_millisInstant_long_int_nanosNegative() {
        Instant.millisInstant(0L, -1);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_millisInstant_long_int_nanosTooLarge() {
        Instant.millisInstant(0L, 1000000000);
    }

    //-----------------------------------------------------------------------
    public void test_deserializationSingleton() throws Exception {
        Instant orginal = Instant.instant(12345);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(orginal);
        out.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bais);
        Instant ser = (Instant) in.readObject();
        assertEquals(ser.getEpochSeconds(), 12345);
    }

    //-----------------------------------------------------------------------
    public void test_comparisons() {
        doTest_comparisons_Instant(
            Instant.instant(-2L, 0),
            Instant.instant(-2L, 999999998),
            Instant.instant(-2L, 999999999),
            Instant.instant(-1L, 0),
            Instant.instant(-1L, 1),
            Instant.instant(-1L, 999999998),
            Instant.instant(-1L, 999999999),
            Instant.instant(0L, 0),
            Instant.instant(0L, 1),
            Instant.instant(0L, 2),
            Instant.instant(0L, 999999999),
            Instant.instant(1L, 0),
            Instant.instant(2L, 0)
        );
    }

    void doTest_comparisons_Instant(Instant... instants) {
        for (int i = 0; i < instants.length; i++) {
            Instant a = instants[i];
            for (int j = 0; j < instants.length; j++) {
                Instant b = instants[j];
                if (i < j) {
                    assertEquals(a.compareTo(b), -1, a + " <=> " + b);
                    assertEquals(a.isBefore(b), true, a + " <=> " + b);
                    assertEquals(a.isAfter(b), false, a + " <=> " + b);
                    assertEquals(a.equals(b), false, a + " <=> " + b);
                } else if (i > j) {
                    assertEquals(a.compareTo(b), 1, a + " <=> " + b);
                    assertEquals(a.isBefore(b), false, a + " <=> " + b);
                    assertEquals(a.isAfter(b), true, a + " <=> " + b);
                    assertEquals(a.equals(b), false, a + " <=> " + b);
                } else {
                    assertEquals(a.compareTo(b), 0, a + " <=> " + b);
                    assertEquals(a.isBefore(b), false, a + " <=> " + b);
                    assertEquals(a.isAfter(b), false, a + " <=> " + b);
                    assertEquals(a.equals(b), true, a + " <=> " + b);
                }
            }
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_compareTo_ObjectNull() {
        Instant a = Instant.instant(0L, 0);
        a.compareTo(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isBefore_ObjectNull() {
        Instant a = Instant.instant(0L, 0);
        a.isBefore(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isAfter_ObjectNull() {
        Instant a = Instant.instant(0L, 0);
        a.isAfter(null);
    }

    //-----------------------------------------------------------------------
    public void test_equals() {
        Instant test5a = Instant.instant(5L, 20);
        Instant test5b = Instant.instant(5L, 20);
        Instant test5n = Instant.instant(5L, 30);
        Instant test6 = Instant.instant(6L, 20);
        
        assertEquals(test5a.equals(test5a), true);
        assertEquals(test5a.equals(test5b), true);
        assertEquals(test5a.equals(test5n), false);
        assertEquals(test5a.equals(test6), false);
        
        assertEquals(test5b.equals(test5a), true);
        assertEquals(test5b.equals(test5b), true);
        assertEquals(test5b.equals(test5n), false);
        assertEquals(test5b.equals(test6), false);
        
        assertEquals(test5n.equals(test5a), false);
        assertEquals(test5n.equals(test5b), false);
        assertEquals(test5n.equals(test5n), true);
        assertEquals(test5n.equals(test6), false);
        
        assertEquals(test6.equals(test5a), false);
        assertEquals(test6.equals(test5b), false);
        assertEquals(test6.equals(test5n), false);
        assertEquals(test6.equals(test6), true);
    }

    public void test_equals_null() {
        Instant test5 = Instant.instant(5L, 20);
        assertEquals(test5.equals(null), false);
    }

    public void test_equals_otherClass() {
        Instant test5 = Instant.instant(5L, 20);
        assertEquals(test5.equals(""), false);
    }

    //-----------------------------------------------------------------------
    public void test_hashCode() {
        Instant test5a = Instant.instant(5L, 20);
        Instant test5b = Instant.instant(5L, 20);
        Instant test5n = Instant.instant(5L, 30);
        Instant test6 = Instant.instant(6L, 20);
        
        assertEquals(test5a.hashCode() == test5a.hashCode(), true);
        assertEquals(test5a.hashCode() == test5b.hashCode(), true);
        assertEquals(test5b.hashCode() == test5b.hashCode(), true);
        
        assertEquals(test5a.hashCode() == test5n.hashCode(), false);
        assertEquals(test5a.hashCode() == test6.hashCode(), false);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="PlusSeconds")
    Object[][] provider_plusSeconds_long() {
        return new Object[][] {
            {0, 0, 0, 0, 0},
            {0, 0, 1, 1, 0},
            {0, 0, -1, -1, 0},
            {0, 0, Long.MAX_VALUE, Long.MAX_VALUE, 0},
            {0, 0, Long.MIN_VALUE, Long.MIN_VALUE, 0},
            {1, 0, 0, 1, 0},
            {1, 0, 1, 2, 0},
            {1, 0, -1, 0, 0},
            {1, 0, Long.MAX_VALUE - 1, Long.MAX_VALUE, 0},
            {1, 0, Long.MIN_VALUE, Long.MIN_VALUE + 1, 0},
            {1, 1, 0, 1, 1},
            {1, 1, 1, 2, 1},
            {1, 1, -1, 0, 1},
            {1, 1, Long.MAX_VALUE - 1, Long.MAX_VALUE, 1},
            {1, 1, Long.MIN_VALUE, Long.MIN_VALUE + 1, 1},
            {-1, 1, 0, -1, 1},
            {-1, 1, 1, 0, 1},
            {-1, 1, -1, -2, 1},
            {-1, 1, Long.MAX_VALUE, Long.MAX_VALUE - 1, 1},
            {-1, 1, Long.MIN_VALUE + 1, Long.MIN_VALUE, 1},
        };
    }

    @Test(dataProvider="PlusSeconds")
    public void plusSeconds_long(long seconds, int nanos, long amount, long expectedSeconds, int expectedNanoOfSecond) {
        Instant t = Instant.instant(seconds, nanos);
        t = t.plusSeconds(amount);
        assertEquals(t.getEpochSeconds(), expectedSeconds);
        assertEquals(t.getNanoOfSecond(), expectedNanoOfSecond);
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void plusSeconds_long_overflowTooBig() {
        Instant t = Instant.instant(1, 0);
        t.plusSeconds(Long.MAX_VALUE);
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void plusSeconds_long_overflowTooSmall() {
        Instant t = Instant.instant(-1, 0);
        t.plusSeconds(Long.MIN_VALUE);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="PlusMillis")
    Object[][] provider_plusMillis_long() {
        return new Object[][] {
            {0, 0, 0,       0, 0},
            {0, 0, 1,       0, 1000000},
            {0, 0, 999,     0, 999000000},
            {0, 0, 1000,    1, 0},
            {0, 0, 1001,    1, 1000000},
            {0, 0, 1999,    1, 999000000},
            {0, 0, 2000,    2, 0},
            {0, 0, -1,      -1, 999000000},
            {0, 0, -999,    -1, 1000000},
            {0, 0, -1000,   -1, 0},
            {0, 0, -1001,   -2, 999000000},
            {0, 0, -1999,   -2, 1000000},
            
            {0, 1, 0,       0, 1},
            {0, 1, 1,       0, 1000001},
            {0, 1, 998,     0, 998000001},
            {0, 1, 999,     0, 999000001},
            {0, 1, 1000,    1, 1},
            {0, 1, 1998,    1, 998000001},
            {0, 1, 1999,    1, 999000001},
            {0, 1, 2000,    2, 1},
            {0, 1, -1,      -1, 999000001},
            {0, 1, -2,      -1, 998000001},
            {0, 1, -1000,   -1, 1},
            {0, 1, -1001,   -2, 999000001},
            
            {0, 1000000, 0,       0, 1000000},
            {0, 1000000, 1,       0, 2000000},
            {0, 1000000, 998,     0, 999000000},
            {0, 1000000, 999,     1, 0},
            {0, 1000000, 1000,    1, 1000000},
            {0, 1000000, 1998,    1, 999000000},
            {0, 1000000, 1999,    2, 0},
            {0, 1000000, 2000,    2, 1000000},
            {0, 1000000, -1,      0, 0},
            {0, 1000000, -2,      -1, 999000000},
            {0, 1000000, -999,    -1, 2000000},
            {0, 1000000, -1000,   -1, 1000000},
            {0, 1000000, -1001,   -1, 0},
            {0, 1000000, -1002,   -2, 999000000},
            
            {0, 999999999, 0,     0, 999999999},
            {0, 999999999, 1,     1, 999999},
            {0, 999999999, 999,   1, 998999999},
            {0, 999999999, 1000,  1, 999999999},
            {0, 999999999, 1001,  2, 999999},
            {0, 999999999, -1,    0, 998999999},
            {0, 999999999, -1000, -1, 999999999},
            {0, 999999999, -1001, -1, 998999999},
        };
    }

    @Test(dataProvider="PlusMillis")
    public void plusMillis_long(long seconds, int nanos, long amount, long expectedSeconds, int expectedNanoOfSecond) {
        Instant t = Instant.instant(seconds, nanos);
        t = t.plusMillis(amount);
        assertEquals(t.getEpochSeconds(), expectedSeconds);
        assertEquals(t.getNanoOfSecond(), expectedNanoOfSecond);
    }
    @Test(dataProvider="PlusMillis")
    public void plusMillis_long_oneMore(long seconds, int nanos, long amount, long expectedSeconds, int expectedNanoOfSecond) {
        Instant t = Instant.instant(seconds + 1, nanos);
        t = t.plusMillis(amount);
        assertEquals(t.getEpochSeconds(), expectedSeconds + 1);
        assertEquals(t.getNanoOfSecond(), expectedNanoOfSecond);
    }
    @Test(dataProvider="PlusMillis")
    public void plusMillis_long_minusOneLess(long seconds, int nanos, long amount, long expectedSeconds, int expectedNanoOfSecond) {
        Instant t = Instant.instant(seconds - 1, nanos);
        t = t.plusMillis(amount);
        assertEquals(t.getEpochSeconds(), expectedSeconds - 1);
        assertEquals(t.getNanoOfSecond(), expectedNanoOfSecond);
    }

    public void plusMillis_long_max() {
        Instant t = Instant.instant(Long.MAX_VALUE, 998999999);
        t = t.plusMillis(1);
        assertEquals(t.getEpochSeconds(), Long.MAX_VALUE);
        assertEquals(t.getNanoOfSecond(), 999999999);
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void plusMillis_long_overflowTooBig() {
        Instant t = Instant.instant(Long.MAX_VALUE, 999000000);
        t.plusMillis(1);
    }

    public void plusMillis_long_min() {
        Instant t = Instant.instant(Long.MIN_VALUE, 1000000);
        t = t.plusMillis(-1);
        assertEquals(t.getEpochSeconds(), Long.MIN_VALUE);
        assertEquals(t.getNanoOfSecond(), 0);
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void plusMillis_long_overflowTooSmall() {
        Instant t = Instant.instant(Long.MIN_VALUE, 0);
        t.plusMillis(-1);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="PlusNanos")
    Object[][] provider_plusNanos_long() {
        return new Object[][] {
            {0, 0, 0,           0, 0},
            {0, 0, 1,           0, 1},
            {0, 0, 999999999,   0, 999999999},
            {0, 0, 1000000000,  1, 0},
            {0, 0, 1000000001,  1, 1},
            {0, 0, 1999999999,  1, 999999999},
            {0, 0, 2000000000,  2, 0},
            {0, 0, -1,          -1, 999999999},
            {0, 0, -999999999,  -1, 1},
            {0, 0, -1000000000, -1, 0},
            {0, 0, -1000000001, -2, 999999999},
            {0, 0, -1999999999, -2, 1},
            
            {1, 0, 0,           1, 0},
            {1, 0, 1,           1, 1},
            {1, 0, 999999999,   1, 999999999},
            {1, 0, 1000000000,  2, 0},
            {1, 0, 1000000001,  2, 1},
            {1, 0, 1999999999,  2, 999999999},
            {1, 0, 2000000000,  3, 0},
            {1, 0, -1,          0, 999999999},
            {1, 0, -999999999,  0, 1},
            {1, 0, -1000000000, 0, 0},
            {1, 0, -1000000001, -1, 999999999},
            {1, 0, -1999999999, -1, 1},
            
            {-1, 0, 0,           -1, 0},
            {-1, 0, 1,           -1, 1},
            {-1, 0, 999999999,   -1, 999999999},
            {-1, 0, 1000000000,  0, 0},
            {-1, 0, 1000000001,  0, 1},
            {-1, 0, 1999999999,  0, 999999999},
            {-1, 0, 2000000000,  1, 0},
            {-1, 0, -1,          -2, 999999999},
            {-1, 0, -999999999,  -2, 1},
            {-1, 0, -1000000000, -2, 0},
            {-1, 0, -1000000001, -3, 999999999},
            {-1, 0, -1999999999, -3, 1},
            
            {1, 1, 0,           1, 1},
            {1, 1, 1,           1, 2},
            {1, 1, 999999998,   1, 999999999},
            {1, 1, 999999999,   2, 0},
            {1, 1, 1000000000,  2, 1},
            {1, 1, 1999999998,  2, 999999999},
            {1, 1, 1999999999,  3, 0},
            {1, 1, 2000000000,  3, 1},
            {1, 1, -1,          1, 0},
            {1, 1, -2,          0, 999999999},
            {1, 1, -1000000000, 0, 1},
            {1, 1, -1000000001, 0, 0},
            {1, 1, -1000000002, -1, 999999999},
            {1, 1, -2000000000, -1, 1},
            
            {1, 999999999, 0,           1, 999999999},
            {1, 999999999, 1,           2, 0},
            {1, 999999999, 999999999,   2, 999999998},
            {1, 999999999, 1000000000,  2, 999999999},
            {1, 999999999, 1000000001,  3, 0},
            {1, 999999999, -1,          1, 999999998},
            {1, 999999999, -1000000000, 0, 999999999},
            {1, 999999999, -1000000001, 0, 999999998},
            {1, 999999999, -1999999999, 0, 0},
            {1, 999999999, -2000000000, -1, 999999999},
            
            {Long.MAX_VALUE, 0, 999999999, Long.MAX_VALUE, 999999999},
            {Long.MAX_VALUE - 1, 0, 1999999999, Long.MAX_VALUE, 999999999},
            {Long.MIN_VALUE, 1, -1, Long.MIN_VALUE, 0},
            {Long.MIN_VALUE + 1, 1, -1000000001, Long.MIN_VALUE, 0},
        };
    }

    @Test(dataProvider="PlusNanos")
    public void plusNanos_long(long seconds, int nanos, long amount, long expectedSeconds, int expectedNanoOfSecond) {
        Instant t = Instant.instant(seconds, nanos);
        t = t.plusNanos(amount);
        assertEquals(t.getEpochSeconds(), expectedSeconds);
        assertEquals(t.getNanoOfSecond(), expectedNanoOfSecond);
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void plusNanos_long_overflowTooBig() {
        Instant t = Instant.instant(Long.MAX_VALUE, 999999999);
        t.plusNanos(1);
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void plusNanos_long_overflowTooSmall() {
        Instant t = Instant.instant(Long.MIN_VALUE, 0);
        t.plusNanos(-1);
    }

    //-----------------------------------------------------------------------
    @Test(enabled=false)
    public void test_toString() {
        Instant t = Instant.instant(0L, 567);
        assertEquals(t.toString(), "1970-01-01T00:00:00.000000567Z");
    }

}
