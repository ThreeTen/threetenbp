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
import org.testng.Reporter;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test Duration.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestDuration {

    //-----------------------------------------------------------------------
    public void test_isSerializable() {
        Duration t = Duration.duration(0L, 0);
        assertTrue(t instanceof Serializable);
    }

    public void test_isComparable() {
        Duration t = Duration.duration(0L, 0);
        assertTrue(t instanceof Comparable);
    }

    public void test_zero() {
        assertEquals(Duration.ZERO.getSeconds(), 0L);
        assertEquals(Duration.ZERO.getNanoOfSecond(), 0);
    }

    //-----------------------------------------------------------------------
    public void factory_duration_long() {
        for (long i = -2; i <= 2; i++) {
            Duration t = Duration.duration(i);
            assertEquals(t.getSeconds(), i);
            assertEquals(t.getNanoOfSecond(), 0);
        }
    }

    public void factory_duration_long_int() {
        for (long i = -2; i <= 2; i++) {
            for (int j = 0; j < 10; j++) {
                Duration t = Duration.duration(i, j);
                assertEquals(t.getSeconds(), i);
                assertEquals(t.getNanoOfSecond(), j);
            }
            for (int j = 999999990; j < 1000000000; j++) {
                Duration t = Duration.duration(i, j);
                assertEquals(t.getSeconds(), i);
                assertEquals(t.getNanoOfSecond(), j);
            }
        }
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_duration_long_int_nanosNegative() {
        Duration.duration(0L, -1);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_duration_long_int_nanosTooLarge() {
        Duration.duration(0L, 1000000000);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="MillisDurationNoNanos")
    Object[][] provider_factory_millisDuration_long() {
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

    @Test(dataProvider="MillisDurationNoNanos")
    public void factory_millisDuration_long(long millis, long expectedSeconds, int expectedNanoOfSecond) {
        Duration t = Duration.millisDuration(millis);
        assertEquals(t.getSeconds(), expectedSeconds);
        assertEquals(t.getNanoOfSecond(), expectedNanoOfSecond);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="MillisDurationWithNanos")
    Object[][] provider_factory_millisDuration_long_int() {
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

    @Test(dataProvider="MillisDurationWithNanos")
    public void factory_millisDuration_long_int(long millis, int nanos, long expectedSeconds, int expectedNanoOfSecond) {
        Duration t = Duration.millisDuration(millis, nanos);
        assertEquals(t.getSeconds(), expectedSeconds);
        assertEquals(t.getNanoOfSecond(), expectedNanoOfSecond);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_millisDuration_long_int_nanosNegative() {
        Duration.millisDuration(0L, -1);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_millisDuration_long_int_nanosTooLarge() {
        Duration.millisDuration(0L, 1000000000);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="DurationBetween")
    Object[][] provider_factory_durationBetween_Instant_Instant() {
        return new Object[][] {
            {0, 0, 0, 0, 0, 0},
            {3, 0, 7, 0, 4, 0},
            {3, 20, 7, 50, 4, 30},
            {3, 80, 7, 50, 3, 999999970},
            {7, 0, 3, 0, -4, 0},
        };
    }

    @Test(dataProvider="DurationBetween")
    public void factory_durationBetween_Instant_Instant(long secs1, int nanos1, long secs2, int nanos2, long expectedSeconds, int expectedNanoOfSecond) {
        Instant start = Instant.instant(secs1, nanos1);
        Instant end = Instant.instant(secs2, nanos2);
        Duration t = Duration.durationBetween(start, end);
        assertEquals(t.getSeconds(), expectedSeconds);
        assertEquals(t.getNanoOfSecond(), expectedNanoOfSecond);
    }

    //-----------------------------------------------------------------------
    public void test_deserializationSingleton() throws Exception {
        Duration orginal = Duration.duration(2);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(orginal);
        out.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bais);
        Duration ser = (Duration) in.readObject();
        assertEquals(Duration.duration(2), ser);
    }

    //-----------------------------------------------------------------------
    public void test_comparisons() {
        doTest_comparisons_Duration(
            Duration.duration(-2L, 0),
            Duration.duration(-2L, 999999998),
            Duration.duration(-2L, 999999999),
            Duration.duration(-1L, 0),
            Duration.duration(-1L, 1),
            Duration.duration(-1L, 999999998),
            Duration.duration(-1L, 999999999),
            Duration.duration(0L, 0),
            Duration.duration(0L, 1),
            Duration.duration(0L, 2),
            Duration.duration(0L, 999999999),
            Duration.duration(1L, 0),
            Duration.duration(2L, 0)
        );
    }

    void doTest_comparisons_Duration(Duration... durations) {
        for (int i = 0; i < durations.length; i++) {
            Duration a = durations[i];
            for (int j = 0; j < durations.length; j++) {
                Duration b = durations[j];
                if (i < j) {
                    assertEquals(a.compareTo(b), -1, a + " <=> " + b);
                    assertEquals(a.isLessThan(b), true, a + " <=> " + b);
                    assertEquals(a.isGreaterThan(b), false, a + " <=> " + b);
                    assertEquals(a.equals(b), false, a + " <=> " + b);
                } else if (i > j) {
                    assertEquals(a.compareTo(b), 1, a + " <=> " + b);
                    assertEquals(a.isLessThan(b), false, a + " <=> " + b);
                    assertEquals(a.isGreaterThan(b), true, a + " <=> " + b);
                    assertEquals(a.equals(b), false, a + " <=> " + b);
                } else {
                    assertEquals(a.compareTo(b), 0, a + " <=> " + b);
                    assertEquals(a.isLessThan(b), false, a + " <=> " + b);
                    assertEquals(a.isGreaterThan(b), false, a + " <=> " + b);
                    assertEquals(a.equals(b), true, a + " <=> " + b);
                }
            }
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_compareTo_ObjectNull() {
        Duration a = Duration.duration(0L, 0);
        a.compareTo(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isLessThan_ObjectNull() {
        Duration a = Duration.duration(0L, 0);
        a.isLessThan(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isGreaterThan_ObjectNull() {
        Duration a = Duration.duration(0L, 0);
        a.isGreaterThan(null);
    }

    @Test(expectedExceptions=ClassCastException.class)
    @SuppressWarnings("unchecked")
    public void compareToNonDuration() {
       Comparable c = Duration.duration(0L);
       c.compareTo(new Object());
    }
    
    //-----------------------------------------------------------------------
    public void test_equals() {
        Duration test5a = Duration.duration(5L, 20);
        Duration test5b = Duration.duration(5L, 20);
        Duration test5n = Duration.duration(5L, 30);
        Duration test6 = Duration.duration(6L, 20);
        
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
        Duration test5 = Duration.duration(5L, 20);
        assertEquals(test5.equals(null), false);
    }

    public void test_equals_otherClass() {
        Duration test5 = Duration.duration(5L, 20);
        assertEquals(test5.equals(""), false);
    }

    //-----------------------------------------------------------------------
    public void test_hashCode() {
        Duration test5a = Duration.duration(5L, 20);
        Duration test5b = Duration.duration(5L, 20);
        Duration test5n = Duration.duration(5L, 30);
        Duration test6 = Duration.duration(6L, 20);
        
        assertEquals(test5a.hashCode() == test5a.hashCode(), true);
        assertEquals(test5a.hashCode() == test5b.hashCode(), true);
        assertEquals(test5b.hashCode() == test5b.hashCode(), true);
        
        assertEquals(test5a.hashCode() == test5n.hashCode(), false);
        assertEquals(test5a.hashCode() == test6.hashCode(), false);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="Plus")
    Object[][] provider_plus() {
        return new Object[][] {
            {Long.MIN_VALUE, 0, Long.MAX_VALUE, 0, -1, 0},

            {-4, 666666667, -4, 666666667, -7, 333333334},
            {-4, 666666667, -3,         0, -7, 666666667},
            {-4, 666666667, -2,         0, -6, 666666667},
            {-4, 666666667, -1,         0, -5, 666666667},
            {-4, 666666667, -1, 333333334, -4,         1},
            {-4, 666666667, -1, 666666667, -4, 333333334},
            {-4, 666666667, -1, 999999999, -4, 666666666},
            {-4, 666666667,  0,         0, -4, 666666667},
            {-4, 666666667,  0,         1, -4, 666666668},
            {-4, 666666667,  0, 333333333, -3,         0},
            {-4, 666666667,  0, 666666666, -3, 333333333},
            {-4, 666666667,  1,         0, -3, 666666667},
            {-4, 666666667,  2,         0, -2, 666666667},
            {-4, 666666667,  3,         0, -1, 666666667},
            {-4, 666666667,  3, 333333333,  0,         0},

            {-3, 0, -4, 666666667, -7, 666666667},
            {-3, 0, -3,         0, -6,         0},
            {-3, 0, -2,         0, -5,         0},
            {-3, 0, -1,         0, -4,         0},
            {-3, 0, -1, 333333334, -4, 333333334},
            {-3, 0, -1, 666666667, -4, 666666667},
            {-3, 0, -1, 999999999, -4, 999999999},
            {-3, 0,  0,         0, -3,         0},
            {-3, 0,  0,         1, -3,         1},
            {-3, 0,  0, 333333333, -3, 333333333},
            {-3, 0,  0, 666666666, -3, 666666666},
            {-3, 0,  1,         0, -2,         0},
            {-3, 0,  2,         0, -1,         0},
            {-3, 0,  3,         0,  0,         0},
            {-3, 0,  3, 333333333,  0, 333333333},

            {-2, 0, -4, 666666667, -6, 666666667},
            {-2, 0, -3,         0, -5,         0},
            {-2, 0, -2,         0, -4,         0},
            {-2, 0, -1,         0, -3,         0},
            {-2, 0, -1, 333333334, -3, 333333334},
            {-2, 0, -1, 666666667, -3, 666666667},
            {-2, 0, -1, 999999999, -3, 999999999},
            {-2, 0,  0,         0, -2,         0},
            {-2, 0,  0,         1, -2,         1},
            {-2, 0,  0, 333333333, -2, 333333333},
            {-2, 0,  0, 666666666, -2, 666666666},
            {-2, 0,  1,         0, -1,         0},
            {-2, 0,  2,         0,  0,         0},
            {-2, 0,  3,         0,  1,         0},
            {-2, 0,  3, 333333333,  1, 333333333},

            {-1, 0, -4, 666666667, -5, 666666667},
            {-1, 0, -3,         0, -4,         0},
            {-1, 0, -2,         0, -3,         0},
            {-1, 0, -1,         0, -2,         0},
            {-1, 0, -1, 333333334, -2, 333333334},
            {-1, 0, -1, 666666667, -2, 666666667},
            {-1, 0, -1, 999999999, -2, 999999999},
            {-1, 0,  0,         0, -1,         0},
            {-1, 0,  0,         1, -1,         1},
            {-1, 0,  0, 333333333, -1, 333333333},
            {-1, 0,  0, 666666666, -1, 666666666},
            {-1, 0,  1,         0,  0,         0},
            {-1, 0,  2,         0,  1,         0},
            {-1, 0,  3,         0,  2,         0},
            {-1, 0,  3, 333333333,  2, 333333333},

            {-1, 666666667, -4, 666666667, -4, 333333334},
            {-1, 666666667, -3,         0, -4, 666666667},
            {-1, 666666667, -2,         0, -3, 666666667},
            {-1, 666666667, -1,         0, -2, 666666667},
            {-1, 666666667, -1, 333333334, -1,         1},
            {-1, 666666667, -1, 666666667, -1, 333333334},
            {-1, 666666667, -1, 999999999, -1, 666666666},
            {-1, 666666667,  0,         0, -1, 666666667},
            {-1, 666666667,  0,         1, -1, 666666668},
            {-1, 666666667,  0, 333333333,  0,         0},
            {-1, 666666667,  0, 666666666,  0, 333333333},
            {-1, 666666667,  1,         0,  0, 666666667},
            {-1, 666666667,  2,         0,  1, 666666667},
            {-1, 666666667,  3,         0,  2, 666666667},
            {-1, 666666667,  3, 333333333,  3,         0},

            {0, 0, -4, 666666667, -4, 666666667},
            {0, 0, -3,         0, -3,         0},
            {0, 0, -2,         0, -2,         0},
            {0, 0, -1,         0, -1,         0},
            {0, 0, -1, 333333334, -1, 333333334},
            {0, 0, -1, 666666667, -1, 666666667},
            {0, 0, -1, 999999999, -1, 999999999},
            {0, 0,  0,         0,  0,         0},
            {0, 0,  0,         1,  0,         1},
            {0, 0,  0, 333333333,  0, 333333333},
            {0, 0,  0, 666666666,  0, 666666666},
            {0, 0,  1,         0,  1,         0},
            {0, 0,  2,         0,  2,         0},
            {0, 0,  3,         0,  3,         0},
            {0, 0,  3, 333333333,  3, 333333333},

            {0, 333333333, -4, 666666667, -3,         0},
            {0, 333333333, -3,         0, -3, 333333333},
            {0, 333333333, -2,         0, -2, 333333333},
            {0, 333333333, -1,         0, -1, 333333333},
            {0, 333333333, -1, 333333334, -1, 666666667},
            {0, 333333333, -1, 666666667,  0,         0},
            {0, 333333333, -1, 999999999,  0, 333333332},
            {0, 333333333,  0,         0,  0, 333333333},
            {0, 333333333,  0,         1,  0, 333333334},
            {0, 333333333,  0, 333333333,  0, 666666666},
            {0, 333333333,  0, 666666666,  0, 999999999},
            {0, 333333333,  1,         0,  1, 333333333},
            {0, 333333333,  2,         0,  2, 333333333},
            {0, 333333333,  3,         0,  3, 333333333},
            {0, 333333333,  3, 333333333,  3, 666666666},

            {1, 0, -4, 666666667, -3, 666666667},
            {1, 0, -3,         0, -2,         0},
            {1, 0, -2,         0, -1,         0},
            {1, 0, -1,         0,  0,         0},
            {1, 0, -1, 333333334,  0, 333333334},
            {1, 0, -1, 666666667,  0, 666666667},
            {1, 0, -1, 999999999,  0, 999999999},
            {1, 0,  0,         0,  1,         0},
            {1, 0,  0,         1,  1,         1},
            {1, 0,  0, 333333333,  1, 333333333},
            {1, 0,  0, 666666666,  1, 666666666},
            {1, 0,  1,         0,  2,         0},
            {1, 0,  2,         0,  3,         0},
            {1, 0,  3,         0,  4,         0},
            {1, 0,  3, 333333333,  4, 333333333},

            {2, 0, -4, 666666667, -2, 666666667},
            {2, 0, -3,         0, -1,         0},
            {2, 0, -2,         0,  0,         0},
            {2, 0, -1,         0,  1,         0},
            {2, 0, -1, 333333334,  1, 333333334},
            {2, 0, -1, 666666667,  1, 666666667},
            {2, 0, -1, 999999999,  1, 999999999},
            {2, 0,  0,         0,  2,         0},
            {2, 0,  0,         1,  2,         1},
            {2, 0,  0, 333333333,  2, 333333333},
            {2, 0,  0, 666666666,  2, 666666666},
            {2, 0,  1,         0,  3,         0},
            {2, 0,  2,         0,  4,         0},
            {2, 0,  3,         0,  5,         0},
            {2, 0,  3, 333333333,  5, 333333333},

            {3, 0, -4, 666666667, -1, 666666667},
            {3, 0, -3,         0,  0,         0},
            {3, 0, -2,         0,  1,         0},
            {3, 0, -1,         0,  2,         0},
            {3, 0, -1, 333333334,  2, 333333334},
            {3, 0, -1, 666666667,  2, 666666667},
            {3, 0, -1, 999999999,  2, 999999999},
            {3, 0,  0,         0,  3,         0},
            {3, 0,  0,         1,  3,         1},
            {3, 0,  0, 333333333,  3, 333333333},
            {3, 0,  0, 666666666,  3, 666666666},
            {3, 0,  1,         0,  4,         0},
            {3, 0,  2,         0,  5,         0},
            {3, 0,  3,         0,  6,         0},
            {3, 0,  3, 333333333,  6, 333333333},

            {3, 333333333, -4, 666666667,  0,         0},
            {3, 333333333, -3,         0,  0, 333333333},
            {3, 333333333, -2,         0,  1, 333333333},
            {3, 333333333, -1,         0,  2, 333333333},
            {3, 333333333, -1, 333333334,  2, 666666667},
            {3, 333333333, -1, 666666667,  3,         0},
            {3, 333333333, -1, 999999999,  3, 333333332},
            {3, 333333333,  0,         0,  3, 333333333},
            {3, 333333333,  0,         1,  3, 333333334},
            {3, 333333333,  0, 333333333,  3, 666666666},
            {3, 333333333,  0, 666666666,  3, 999999999},
            {3, 333333333,  1,         0,  4, 333333333},
            {3, 333333333,  2,         0,  5, 333333333},
            {3, 333333333,  3,         0,  6, 333333333},
            {3, 333333333,  3, 333333333,  6, 666666666},

            {Long.MAX_VALUE, 0, Long.MIN_VALUE, 0, -1, 0},
       };
    }
    
    @Test(dataProvider="Plus") 
    public void plus(long seconds, int nanos, long otherSeconds, int otherNanos, long expectedSeconds, int expectedNanoOfSecond) {
       Duration t = Duration.duration(seconds, nanos).plus(Duration.duration(otherSeconds, otherNanos));
       assertEquals(t.getSeconds(), expectedSeconds);
       assertEquals(t.getNanoOfSecond(), expectedNanoOfSecond);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void plusOverflowTooBig() {
       Duration t = Duration.duration(Long.MAX_VALUE, 999999999);
       t.plus(Duration.duration(0, 1));
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void plusOverflowTooSmall() {
       Duration t = Duration.duration(Long.MIN_VALUE);
       t.plus(Duration.duration(-1, 999999999));
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
        Duration t = Duration.duration(seconds, nanos);
        t = t.plusSeconds(amount);
        assertEquals(t.getSeconds(), expectedSeconds);
        assertEquals(t.getNanoOfSecond(), expectedNanoOfSecond);
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void plusSeconds_long_overflowTooBig() {
        Duration t = Duration.duration(1, 0);
        t.plusSeconds(Long.MAX_VALUE);
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void plusSeconds_long_overflowTooSmall() {
        Duration t = Duration.duration(-1, 0);
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
        Duration t = Duration.duration(seconds, nanos);
        t = t.plusMillis(amount);
        assertEquals(t.getSeconds(), expectedSeconds);
        assertEquals(t.getNanoOfSecond(), expectedNanoOfSecond);
    }
    @Test(dataProvider="PlusMillis")
    public void plusMillis_long_oneMore(long seconds, int nanos, long amount, long expectedSeconds, int expectedNanoOfSecond) {
        Duration t = Duration.duration(seconds + 1, nanos);
        t = t.plusMillis(amount);
        assertEquals(t.getSeconds(), expectedSeconds + 1);
        assertEquals(t.getNanoOfSecond(), expectedNanoOfSecond);
    }
    @Test(dataProvider="PlusMillis")
    public void plusMillis_long_minusOneLess(long seconds, int nanos, long amount, long expectedSeconds, int expectedNanoOfSecond) {
        Duration t = Duration.duration(seconds - 1, nanos);
        t = t.plusMillis(amount);
        assertEquals(t.getSeconds(), expectedSeconds - 1);
        assertEquals(t.getNanoOfSecond(), expectedNanoOfSecond);
    }

    public void plusMillis_long_max() {
        Duration t = Duration.duration(Long.MAX_VALUE, 998999999);
        t = t.plusMillis(1);
        assertEquals(t.getSeconds(), Long.MAX_VALUE);
        assertEquals(t.getNanoOfSecond(), 999999999);
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void plusMillis_long_overflowTooBig() {
        Duration t = Duration.duration(Long.MAX_VALUE, 999000000);
        t.plusMillis(1);
    }

    public void plusMillis_long_min() {
        Duration t = Duration.duration(Long.MIN_VALUE, 1000000);
        t = t.plusMillis(-1);
        assertEquals(t.getSeconds(), Long.MIN_VALUE);
        assertEquals(t.getNanoOfSecond(), 0);
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void plusMillis_long_overflowTooSmall() {
        Duration t = Duration.duration(Long.MIN_VALUE, 0);
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
        Duration t = Duration.duration(seconds, nanos);
        t = t.plusNanos(amount);
        assertEquals(t.getSeconds(), expectedSeconds);
        assertEquals(t.getNanoOfSecond(), expectedNanoOfSecond);
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void plusNanos_long_overflowTooBig() {
        Duration t = Duration.duration(Long.MAX_VALUE, 999999999);
        t.plusNanos(1);
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void plusNanos_long_overflowTooSmall() {
        Duration t = Duration.duration(Long.MIN_VALUE, 0);
        t.plusNanos(-1);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="Minus")
    Object[][] provider_minus() {
        return new Object[][] {
            {Long.MIN_VALUE, 0, Long.MIN_VALUE + 1, 0, -1, 0},

            {-4, 666666667, -4, 666666667,  0,         0},
            {-4, 666666667, -3,         0, -1, 666666667},
            {-4, 666666667, -2,         0, -2, 666666667},
            {-4, 666666667, -1,         0, -3, 666666667},
            {-4, 666666667, -1, 333333334, -3, 333333333},
            {-4, 666666667, -1, 666666667, -3,         0},
            {-4, 666666667, -1, 999999999, -4, 666666668},
            {-4, 666666667,  0,         0, -4, 666666667},
            {-4, 666666667,  0,         1, -4, 666666666},
            {-4, 666666667,  0, 333333333, -4, 333333334},
            {-4, 666666667,  0, 666666666, -4,         1},
            {-4, 666666667,  1,         0, -5, 666666667},
            {-4, 666666667,  2,         0, -6, 666666667},
            {-4, 666666667,  3,         0, -7, 666666667},
            {-4, 666666667,  3, 333333333, -7, 333333334},

            {-3, 0, -4, 666666667,  0, 333333333},
            {-3, 0, -3,         0,  0,         0},
            {-3, 0, -2,         0, -1,         0},
            {-3, 0, -1,         0, -2,         0},
            {-3, 0, -1, 333333334, -3, 666666666},
            {-3, 0, -1, 666666667, -3, 333333333},
            {-3, 0, -1, 999999999, -3,         1},
            {-3, 0,  0,         0, -3,         0},
            {-3, 0,  0,         1, -4, 999999999},
            {-3, 0,  0, 333333333, -4, 666666667},
            {-3, 0,  0, 666666666, -4, 333333334},
            {-3, 0,  1,         0, -4,         0},
            {-3, 0,  2,         0, -5,         0},
            {-3, 0,  3,         0, -6,         0},
            {-3, 0,  3, 333333333, -7, 666666667},

            {-2, 0, -4, 666666667,  1, 333333333},
            {-2, 0, -3,         0,  1,         0},
            {-2, 0, -2,         0,  0,         0},
            {-2, 0, -1,         0, -1,         0},
            {-2, 0, -1, 333333334, -2, 666666666},
            {-2, 0, -1, 666666667, -2, 333333333},
            {-2, 0, -1, 999999999, -2,         1},
            {-2, 0,  0,         0, -2,         0},
            {-2, 0,  0,         1, -3, 999999999},
            {-2, 0,  0, 333333333, -3, 666666667},
            {-2, 0,  0, 666666666, -3, 333333334},
            {-2, 0,  1,         0, -3,         0},
            {-2, 0,  2,         0, -4,         0},
            {-2, 0,  3,         0, -5,         0},
            {-2, 0,  3, 333333333, -6, 666666667},

            {-1, 0, -4, 666666667,  2, 333333333},
            {-1, 0, -3,         0,  2,         0},
            {-1, 0, -2,         0,  1,         0},
            {-1, 0, -1,         0,  0,         0},
            {-1, 0, -1, 333333334, -1, 666666666},
            {-1, 0, -1, 666666667, -1, 333333333},
            {-1, 0, -1, 999999999, -1,         1},
            {-1, 0,  0,         0, -1,         0},
            {-1, 0,  0,         1, -2, 999999999},
            {-1, 0,  0, 333333333, -2, 666666667},
            {-1, 0,  0, 666666666, -2, 333333334},
            {-1, 0,  1,         0, -2,         0},
            {-1, 0,  2,         0, -3,         0},
            {-1, 0,  3,         0, -4,         0},
            {-1, 0,  3, 333333333, -5, 666666667},

            {-1, 666666667, -4, 666666667,  3,         0},
            {-1, 666666667, -3,         0,  2, 666666667},
            {-1, 666666667, -2,         0,  1, 666666667},
            {-1, 666666667, -1,         0,  0, 666666667},
            {-1, 666666667, -1, 333333334,  0, 333333333},
            {-1, 666666667, -1, 666666667,  0,         0},
            {-1, 666666667, -1, 999999999, -1, 666666668},
            {-1, 666666667,  0,         0, -1, 666666667},
            {-1, 666666667,  0,         1, -1, 666666666},
            {-1, 666666667,  0, 333333333, -1, 333333334},
            {-1, 666666667,  0, 666666666, -1,         1},
            {-1, 666666667,  1,         0, -2, 666666667},
            {-1, 666666667,  2,         0, -3, 666666667},
            {-1, 666666667,  3,         0, -4, 666666667},
            {-1, 666666667,  3, 333333333, -4, 333333334},

            {0, 0, -4, 666666667,  3, 333333333},
            {0, 0, -3,         0,  3,         0},
            {0, 0, -2,         0,  2,         0},
            {0, 0, -1,         0,  1,         0},
            {0, 0, -1, 333333334,  0, 666666666},
            {0, 0, -1, 666666667,  0, 333333333},
            {0, 0, -1, 999999999,  0,         1},
            {0, 0,  0,         0,  0,         0},
            {0, 0,  0,         1, -1, 999999999},
            {0, 0,  0, 333333333, -1, 666666667},
            {0, 0,  0, 666666666, -1, 333333334},
            {0, 0,  1,         0, -1,         0},
            {0, 0,  2,         0, -2,         0},
            {0, 0,  3,         0, -3,         0},
            {0, 0,  3, 333333333, -4, 666666667},

            {0, 333333333, -4, 666666667,  3, 666666666},
            {0, 333333333, -3,         0,  3, 333333333},
            {0, 333333333, -2,         0,  2, 333333333},
            {0, 333333333, -1,         0,  1, 333333333},
            {0, 333333333, -1, 333333334,  0, 999999999},
            {0, 333333333, -1, 666666667,  0, 666666666},
            {0, 333333333, -1, 999999999,  0, 333333334},
            {0, 333333333,  0,         0,  0, 333333333},
            {0, 333333333,  0,         1,  0, 333333332},
            {0, 333333333,  0, 333333333,  0,         0},
            {0, 333333333,  0, 666666666, -1, 666666667},
            {0, 333333333,  1,         0, -1, 333333333},
            {0, 333333333,  2,         0, -2, 333333333},
            {0, 333333333,  3,         0, -3, 333333333},
            {0, 333333333,  3, 333333333, -3,         0},

            {1, 0, -4, 666666667,  4, 333333333},
            {1, 0, -3,         0,  4,         0},
            {1, 0, -2,         0,  3,         0},
            {1, 0, -1,         0,  2,         0},
            {1, 0, -1, 333333334,  1, 666666666},
            {1, 0, -1, 666666667,  1, 333333333},
            {1, 0, -1, 999999999,  1,         1},
            {1, 0,  0,         0,  1,         0},
            {1, 0,  0,         1,  0, 999999999},
            {1, 0,  0, 333333333,  0, 666666667},
            {1, 0,  0, 666666666,  0, 333333334},
            {1, 0,  1,         0,  0,         0},
            {1, 0,  2,         0, -1,         0},
            {1, 0,  3,         0, -2,         0},
            {1, 0,  3, 333333333, -3, 666666667},

            {2, 0, -4, 666666667,  5, 333333333},
            {2, 0, -3,         0,  5,         0},
            {2, 0, -2,         0,  4,         0},
            {2, 0, -1,         0,  3,         0},
            {2, 0, -1, 333333334,  2, 666666666},
            {2, 0, -1, 666666667,  2, 333333333},
            {2, 0, -1, 999999999,  2,         1},
            {2, 0,  0,         0,  2,         0},
            {2, 0,  0,         1,  1, 999999999},
            {2, 0,  0, 333333333,  1, 666666667},
            {2, 0,  0, 666666666,  1, 333333334},
            {2, 0,  1,         0,  1,         0},
            {2, 0,  2,         0,  0,         0},
            {2, 0,  3,         0, -1,         0},
            {2, 0,  3, 333333333, -2, 666666667},

            {3, 0, -4, 666666667,  6, 333333333},
            {3, 0, -3,         0,  6,         0},
            {3, 0, -2,         0,  5,         0},
            {3, 0, -1,         0,  4,         0},
            {3, 0, -1, 333333334,  3, 666666666},
            {3, 0, -1, 666666667,  3, 333333333},
            {3, 0, -1, 999999999,  3,         1},
            {3, 0,  0,         0,  3,         0},
            {3, 0,  0,         1,  2, 999999999},
            {3, 0,  0, 333333333,  2, 666666667},
            {3, 0,  0, 666666666,  2, 333333334},
            {3, 0,  1,         0,  2,         0},
            {3, 0,  2,         0,  1,         0},
            {3, 0,  3,         0,  0,         0},
            {3, 0,  3, 333333333, -1, 666666667},

            {3, 333333333, -4, 666666667,  6, 666666666},
            {3, 333333333, -3,         0,  6, 333333333},
            {3, 333333333, -2,         0,  5, 333333333},
            {3, 333333333, -1,         0,  4, 333333333},
            {3, 333333333, -1, 333333334,  3, 999999999},
            {3, 333333333, -1, 666666667,  3, 666666666},
            {3, 333333333, -1, 999999999,  3, 333333334},
            {3, 333333333,  0,         0,  3, 333333333},
            {3, 333333333,  0,         1,  3, 333333332},
            {3, 333333333,  0, 333333333,  3,         0},
            {3, 333333333,  0, 666666666,  2, 666666667},
            {3, 333333333,  1,         0,  2, 333333333},
            {3, 333333333,  2,         0,  1, 333333333},
            {3, 333333333,  3,         0,  0, 333333333},
            {3, 333333333,  3, 333333333,  0,         0},

            {Long.MAX_VALUE, 0, Long.MAX_VALUE, 0, 0, 0},
       };
    }
    
    @Test(dataProvider="Minus") 
    public void minus(long seconds, int nanos, long otherSeconds, int otherNanos, long expectedSeconds, int expectedNanoOfSecond) {
       Duration t = Duration.duration(seconds, nanos).minus(Duration.duration(otherSeconds, otherNanos));
       assertEquals(t.getSeconds(), expectedSeconds);
       assertEquals(t.getNanoOfSecond(), expectedNanoOfSecond);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void minusOverflowTooSmall() {
       Duration t = Duration.duration(Long.MIN_VALUE);
       t.minus(Duration.duration(0, 1));
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void minusOverflowTooBig() {
       Duration t = Duration.duration(Long.MAX_VALUE, 999999999);
       t.minus(Duration.duration(-1, 999999999));
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="MinusSeconds")
    Object[][] provider_minusSeconds_long() {
        return new Object[][] {
            {0, 0, 0, 0, 0},
            {0, 0, 1, -1, 0},
            {0, 0, -1, 1, 0},
            {0, 0, Long.MAX_VALUE, -Long.MAX_VALUE, 0},
            {0, 0, Long.MIN_VALUE + 1, Long.MAX_VALUE, 0},
            {1, 0, 0, 1, 0},
            {1, 0, 1, 0, 0},
            {1, 0, -1, 2, 0},
            {1, 0, Long.MAX_VALUE - 1, -Long.MAX_VALUE + 2, 0},
            {1, 0, Long.MIN_VALUE + 2, Long.MAX_VALUE, 0},
            {1, 1, 0, 1, 1},
            {1, 1, 1, 0, 1},
            {1, 1, -1, 2, 1},
            {1, 1, Long.MAX_VALUE, -Long.MAX_VALUE + 1, 1},
            {1, 1, Long.MIN_VALUE + 2, Long.MAX_VALUE, 1},
            {-1, 1, 0, -1, 1},
            {-1, 1, 1, -2, 1},
            {-1, 1, -1, 0, 1},
            {-1, 1, Long.MAX_VALUE, Long.MIN_VALUE, 1},
            {-1, 1, Long.MIN_VALUE + 1, Long.MAX_VALUE - 1, 1},
        };
    }

    @Test(dataProvider="MinusSeconds")
    public void minusSeconds_long(long seconds, int nanos, long amount, long expectedSeconds, int expectedNanoOfSecond) {
        Duration t = Duration.duration(seconds, nanos);
        t = t.minusSeconds(amount);
        assertEquals(t.getSeconds(), expectedSeconds);
        assertEquals(t.getNanoOfSecond(), expectedNanoOfSecond);
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void minusSeconds_long_overflowTooBig() {
        Duration t = Duration.duration(1, 0);
        t.minusSeconds(Long.MIN_VALUE + 1);
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void minusSeconds_long_overflowTooSmall() {
        Duration t = Duration.duration(-2, 0);
        t.minusSeconds(Long.MAX_VALUE);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="MinusMillis")
    Object[][] provider_minusMillis_long() {
        return new Object[][] {
            {0, 0, 0,       0, 0},
            {0, 0, 1,      -1, 999000000},
            {0, 0, 999,    -1, 1000000},
            {0, 0, 1000,   -1, 0},
            {0, 0, 1001,   -2, 999000000},
            {0, 0, 1999,   -2, 1000000},
            {0, 0, 2000,   -2, 0},
            {0, 0, -1,      0, 1000000},
            {0, 0, -999,    0, 999000000},
            {0, 0, -1000,   1, 0},
            {0, 0, -1001,   1, 1000000},
            {0, 0, -1999,   1, 999000000},
            
            {0, 1, 0,       0, 1},
            {0, 1, 1,      -1, 999000001},
            {0, 1, 998,    -1, 2000001},
            {0, 1, 999,    -1, 1000001},
            {0, 1, 1000,   -1, 1},
            {0, 1, 1998,   -2, 2000001},
            {0, 1, 1999,   -2, 1000001},
            {0, 1, 2000,   -2, 1},
            {0, 1, -1,      0, 1000001},
            {0, 1, -2,      0, 2000001},
            {0, 1, -1000,   1, 1},
            {0, 1, -1001,   1, 1000001},
            
            {0, 1000000, 0,       0, 1000000},
            {0, 1000000, 1,       0, 0},
            {0, 1000000, 998,    -1, 3000000},
            {0, 1000000, 999,    -1, 2000000},
            {0, 1000000, 1000,   -1, 1000000},
            {0, 1000000, 1998,   -2, 3000000},
            {0, 1000000, 1999,   -2, 2000000},
            {0, 1000000, 2000,   -2, 1000000},
            {0, 1000000, -1,      0, 2000000},
            {0, 1000000, -2,      0, 3000000},
            {0, 1000000, -999,    1, 0},
            {0, 1000000, -1000,   1, 1000000},
            {0, 1000000, -1001,   1, 2000000},
            {0, 1000000, -1002,   1, 3000000},
            
            {0, 999999999, 0,     0, 999999999},
            {0, 999999999, 1,     0, 998999999},
            {0, 999999999, 999,   0, 999999},
            {0, 999999999, 1000, -1, 999999999},
            {0, 999999999, 1001, -1, 998999999},
            {0, 999999999, -1,    1, 999999},
            {0, 999999999, -1000, 1, 999999999},
            {0, 999999999, -1001, 2, 999999},
        };
    }

    @Test(dataProvider="MinusMillis")
    public void minusMillis_long(long seconds, int nanos, long amount, long expectedSeconds, int expectedNanoOfSecond) {
        Duration t = Duration.duration(seconds, nanos);
        t = t.minusMillis(amount);
        assertEquals(t.getSeconds(), expectedSeconds);
        assertEquals(t.getNanoOfSecond(), expectedNanoOfSecond);
    }
    @Test(dataProvider="MinusMillis")
    public void minusMillis_long_oneMore(long seconds, int nanos, long amount, long expectedSeconds, int expectedNanoOfSecond) {
        Duration t = Duration.duration(seconds + 1, nanos);
        t = t.minusMillis(amount);
        assertEquals(t.getSeconds(), expectedSeconds + 1);
        assertEquals(t.getNanoOfSecond(), expectedNanoOfSecond);
    }
    @Test(dataProvider="MinusMillis")
    public void minusMillis_long_minusOneLess(long seconds, int nanos, long amount, long expectedSeconds, int expectedNanoOfSecond) {
        Duration t = Duration.duration(seconds - 1, nanos);
        t = t.minusMillis(amount);
        assertEquals(t.getSeconds(), expectedSeconds - 1);
        assertEquals(t.getNanoOfSecond(), expectedNanoOfSecond);
    }

    public void minusMillis_long_max() {
        Duration t = Duration.duration(Long.MAX_VALUE, 998999999);
        t = t.minusMillis(-1);
        assertEquals(t.getSeconds(), Long.MAX_VALUE);
        assertEquals(t.getNanoOfSecond(), 999999999);
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void minusMillis_long_overflowTooBig() {
        Duration t = Duration.duration(Long.MAX_VALUE, 999000000);
        t.minusMillis(-1);
    }

    public void minusMillis_long_min() {
        Duration t = Duration.duration(Long.MIN_VALUE, 1000000);
        t = t.minusMillis(1);
        assertEquals(t.getSeconds(), Long.MIN_VALUE);
        assertEquals(t.getNanoOfSecond(), 0);
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void minusMillis_long_overflowTooSmall() {
        Duration t = Duration.duration(Long.MIN_VALUE, 0);
        t.minusMillis(1);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="MinusNanos")
    Object[][] provider_minusNanos_long() {
        return new Object[][] {
            {0, 0, 0,           0, 0},
            {0, 0, 1,          -1, 999999999},
            {0, 0, 999999999,  -1, 1},
            {0, 0, 1000000000, -1, 0},
            {0, 0, 1000000001, -2, 999999999},
            {0, 0, 1999999999, -2, 1},
            {0, 0, 2000000000, -2, 0},
            {0, 0, -1,          0, 1},
            {0, 0, -999999999,  0, 999999999},
            {0, 0, -1000000000, 1, 0},
            {0, 0, -1000000001, 1, 1},
            {0, 0, -1999999999, 1, 999999999},
            
            {1, 0, 0,            1, 0},
            {1, 0, 1,            0, 999999999},
            {1, 0, 999999999,    0, 1},
            {1, 0, 1000000000,   0, 0},
            {1, 0, 1000000001,  -1, 999999999},
            {1, 0, 1999999999,  -1, 1},
            {1, 0, 2000000000,  -1, 0},
            {1, 0, -1,           1, 1},
            {1, 0, -999999999,   1, 999999999},
            {1, 0, -1000000000,  2, 0},
            {1, 0, -1000000001,  2, 1},
            {1, 0, -1999999999,  2, 999999999},
            
            {-1, 0, 0,           -1, 0},
            {-1, 0, 1,           -2, 999999999},
            {-1, 0, 999999999,   -2, 1},
            {-1, 0, 1000000000,  -2, 0},
            {-1, 0, 1000000001,  -3, 999999999},
            {-1, 0, 1999999999,  -3, 1},
            {-1, 0, 2000000000,  -3, 0},
            {-1, 0, -1,          -1, 1},
            {-1, 0, -999999999,  -1, 999999999},
            {-1, 0, -1000000000,  0, 0},
            {-1, 0, -1000000001,  0, 1},
            {-1, 0, -1999999999,  0, 999999999},
            
            {1, 1, 0,           1, 1},
            {1, 1, 1,           1, 0},
            {1, 1, 999999998,   0, 3},
            {1, 1, 999999999,   0, 2},
            {1, 1, 1000000000,  0, 1},
            {1, 1, 1999999998, -1, 3},
            {1, 1, 1999999999, -1, 2},
            {1, 1, 2000000000, -1, 1},
            {1, 1, -1,          1, 2},
            {1, 1, -2,          1, 3},
            {1, 1, -1000000000, 2, 1},
            {1, 1, -1000000001, 2, 2},
            {1, 1, -1000000002, 2, 3},
            {1, 1, -2000000000, 3, 1},
            
            {1, 999999999, 0,           1, 999999999},
            {1, 999999999, 1,           1, 999999998},
            {1, 999999999, 999999999,   1, 0},
            {1, 999999999, 1000000000,  0, 999999999},
            {1, 999999999, 1000000001,  0, 999999998},
            {1, 999999999, -1,          2, 0},
            {1, 999999999, -1000000000, 2, 999999999},
            {1, 999999999, -1000000001, 3, 0},
            {1, 999999999, -1999999999, 3, 999999998},
            {1, 999999999, -2000000000, 3, 999999999},
            
            {Long.MAX_VALUE, 0, -999999999, Long.MAX_VALUE, 999999999},
            {Long.MAX_VALUE - 1, 0, -1999999999, Long.MAX_VALUE, 999999999},
            {Long.MIN_VALUE, 1, 1, Long.MIN_VALUE, 0},
            {Long.MIN_VALUE + 1, 1, 1000000001, Long.MIN_VALUE, 0},
        };
    }

    @Test(dataProvider="MinusNanos")
    public void minusNanos_long(long seconds, int nanos, long amount, long expectedSeconds, int expectedNanoOfSecond) {
        Duration t = Duration.duration(seconds, nanos);
        t = t.minusNanos(amount);
        assertEquals(t.getSeconds(), expectedSeconds);
        assertEquals(t.getNanoOfSecond(), expectedNanoOfSecond);
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void minusNanos_long_overflowTooBig() {
        Duration t = Duration.duration(Long.MAX_VALUE, 999999999);
        t.minusNanos(-1);
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void minusNanos_long_overflowTooSmall() {
        Duration t = Duration.duration(Long.MIN_VALUE, 0);
        t.minusNanos(1);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="MultipliedBy")
    Object[][] provider_multipliedBy() {
       return new Object[][] {
          {-4, 666666667, -3,   9, 999999999},
          {-4, 666666667, -2,   6, 666666666},
          {-4, 666666667, -1,   3, 333333333},
          {-4, 666666667,  0,   0,         0},
          {-4, 666666667,  1,  -4, 666666667},
          {-4, 666666667,  2,  -7, 333333334},
          {-4, 666666667,  3, -10, 000000001},

          {-3, 0, -3,  9, 0},
          {-3, 0, -2,  6, 0},
          {-3, 0, -1,  3, 0},
          {-3, 0,  0,  0, 0},
          {-3, 0,  1, -3, 0},
          {-3, 0,  2, -6, 0},
          {-3, 0,  3, -9, 0},

          {-2, 0, -3,  6, 0},
          {-2, 0, -2,  4, 0},
          {-2, 0, -1,  2, 0},
          {-2, 0,  0,  0, 0},
          {-2, 0,  1, -2, 0},
          {-2, 0,  2, -4, 0},
          {-2, 0,  3, -6, 0},

          {-1, 0, -3,  3, 0},
          {-1, 0, -2,  2, 0},
          {-1, 0, -1,  1, 0},
          {-1, 0,  0,  0, 0},
          {-1, 0,  1, -1, 0},
          {-1, 0,  2, -2, 0},
          {-1, 0,  3, -3, 0},

          {-1, 500000000, -3,  1, 500000000},
          {-1, 500000000, -2,  1,         0},
          {-1, 500000000, -1,  0, 500000000},
          {-1, 500000000,  0,  0,         0},
          {-1, 500000000,  1, -1, 500000000},
          {-1, 500000000,  2, -1,         0},
          {-1, 500000000,  3, -2, 500000000},

          {0, 0, -3, 0, 0},
          {0, 0, -2, 0, 0},
          {0, 0, -1, 0, 0},
          {0, 0,  0, 0, 0},
          {0, 0,  1, 0, 0},
          {0, 0,  2, 0, 0},
          {0, 0,  3, 0, 0},

          {0, 500000000, -3, -2, 500000000},
          {0, 500000000, -2, -1,         0},
          {0, 500000000, -1, -1, 500000000},
          {0, 500000000,  0,  0,         0},
          {0, 500000000,  1,  0, 500000000},
          {0, 500000000,  2,  1,         0},
          {0, 500000000,  3,  1, 500000000},

          {1, 0, -3, -3, 0},
          {1, 0, -2, -2, 0},
          {1, 0, -1, -1, 0},
          {1, 0,  0,  0, 0},
          {1, 0,  1,  1, 0},
          {1, 0,  2,  2, 0},
          {1, 0,  3,  3, 0},

          {2, 0, -3, -6, 0},
          {2, 0, -2, -4, 0},
          {2, 0, -1, -2, 0},
          {2, 0,  0,  0, 0},
          {2, 0,  1,  2, 0},
          {2, 0,  2,  4, 0},
          {2, 0,  3,  6, 0},

          {3, 0, -3, -9, 0},
          {3, 0, -2, -6, 0},
          {3, 0, -1, -3, 0},
          {3, 0,  0,  0, 0},
          {3, 0,  1,  3, 0},
          {3, 0,  2,  6, 0},
          {3, 0,  3,  9, 0},

          {3, 333333333, -3, -10, 000000001},
          {3, 333333333, -2,  -7, 333333334},
          {3, 333333333, -1,  -4, 666666667},
          {3, 333333333,  0,   0,         0},
          {3, 333333333,  1,   3, 333333333},
          {3, 333333333,  2,   6, 666666666},
          {3, 333333333,  3,   9, 999999999},
       };
    }
    
    @Test(dataProvider="MultipliedBy")
    public void multipliedBy(long seconds, int nanos, int multiplicand, long expectedSeconds, int expectedNanos) {
        Duration t = Duration.duration(seconds, nanos);
        t = t.multipliedBy(multiplicand);
        assertEquals(t.getSeconds(), expectedSeconds);
        assertEquals(t.getNanoOfSecond(), expectedNanos);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="DividedBy")
    Object[][] provider_dividedBy() {
       return new Object[][] {
          {-4, 666666667, -3,  1, 111111111},
          {-4, 666666667, -2,  1, 666666666},
          {-4, 666666667, -1,  3, 333333333},
          {-4, 666666667,  1, -4, 666666667},
          {-4, 666666667,  2, -2, 333333334},
          {-4, 666666667,  3, -2, 888888889},

          {-3, 0, -3,  1, 0},
          {-3, 0, -2,  1, 500000000},
          {-3, 0, -1,  3, 0},
          {-3, 0,  1, -3, 0},
          {-3, 0,  2, -2, 500000000},
          {-3, 0,  3, -1, 0},

          {-2, 0, -3,  0, 666666666},
          {-2, 0, -2,  1,         0},
          {-2, 0, -1,  2,         0},
          {-2, 0,  1, -2,         0},
          {-2, 0,  2, -1,         0},
          {-2, 0,  3, -1, 333333334},

          {-1, 0, -3,  0, 333333333},
          {-1, 0, -2,  0, 500000000},
          {-1, 0, -1,  1,         0},
          {-1, 0,  1, -1,         0},
          {-1, 0,  2, -1, 500000000},
          {-1, 0,  3, -1, 666666667},

          {-1, 500000000, -3,  0, 166666666},
          {-1, 500000000, -2,  0, 250000000},
          {-1, 500000000, -1,  0, 500000000},
          {-1, 500000000,  1, -1, 500000000},
          {-1, 500000000,  2, -1, 750000000},
          {-1, 500000000,  3, -1, 833333334},

          {0, 0, -3, 0, 0},
          {0, 0, -2, 0, 0},
          {0, 0, -1, 0, 0},
          {0, 0,  1, 0, 0},
          {0, 0,  2, 0, 0},
          {0, 0,  3, 0, 0},

          {0, 500000000, -3, -1, 833333334},
          {0, 500000000, -2, -1, 750000000},
          {0, 500000000, -1, -1, 500000000},
          {0, 500000000,  1,  0, 500000000},
          {0, 500000000,  2,  0, 250000000},
          {0, 500000000,  3,  0, 166666666},

          {1, 0, -3, -1, 666666667},
          {1, 0, -2, -1, 500000000},
          {1, 0, -1, -1,         0},
          {1, 0,  1,  1,         0},
          {1, 0,  2,  0, 500000000},
          {1, 0,  3,  0, 333333333},

          {2, 0, -3, -1, 333333334},
          {2, 0, -2, -1,         0},
          {2, 0, -1, -2,         0},
          {2, 0,  1,  2,         0},
          {2, 0,  2,  1,         0},
          {2, 0,  3,  0, 666666666},

          {3, 0, -3, -1,         0},
          {3, 0, -2, -2, 500000000},
          {3, 0, -1, -3,         0},
          {3, 0,  1,  3,         0},
          {3, 0,  2,  1, 500000000},
          {3, 0,  3,  1,         0},

          {3, 333333333, -3, -2, 888888889},
          {3, 333333333, -2, -2, 333333334},
          {3, 333333333, -1, -4, 666666667},
          {3, 333333333,  1,  3, 333333333},
          {3, 333333333,  2,  1, 666666666},
          {3, 333333333,  3,  1, 111111111},
       };
    }
    
    @Test(dataProvider="DividedBy")
    public void dividedBy(long seconds, int nanos, int divisor, long expectedSeconds, int expectedNanos) {
        Duration t = Duration.duration(seconds, nanos);
        t = t.dividedBy(divisor);
        assertEquals(t.getSeconds(), expectedSeconds);
        assertEquals(t.getNanoOfSecond(), expectedNanos);
    }

    @Test(dataProvider="DividedBy", expectedExceptions=ArithmeticException.class)
    public void dividedByZero(long seconds, int nanos, int divisor, long expectedSeconds, int expectedNanos) {
       Duration t = Duration.duration(seconds, nanos);
       t.dividedBy(0);
       fail(t + " divided by zero did not throw ArithmeticException");
    }

          
    //-----------------------------------------------------------------------
    @DataProvider(name="ToString")
    Object[][] provider_toString() {
        return new Object[][] {
            {0, 0, "PT0S"},
            {0, 1, "PT0.000000001S"},
            {0, 10, "PT0.00000001S"},
            {0, 100, "PT0.0000001S"},
            {0, 1000, "PT0.000001S"},
            {0, 10000, "PT0.00001S"},
            {0, 100000, "PT0.0001S"},
            {0, 1000000, "PT0.001S"},
            {0, 10000000, "PT0.01S"},
            {0, 100000000, "PT0.1S"},
            {0, 120000000, "PT0.12S"},
            {0, 123000000, "PT0.123S"},
            {0, 123400000, "PT0.1234S"},
            {0, 123450000, "PT0.12345S"},
            {0, 123456000, "PT0.123456S"},
            {0, 123456700, "PT0.1234567S"},
            {0, 123456780, "PT0.12345678S"},
            {0, 123456789, "PT0.123456789S"},
            {1, 0, "PT1S"},
            {-1, 0, "PT-1S"},
            {-1, 1000, "PT-0.999999S"},
            {-1, 900000000, "PT-0.1S"},
            {Long.MAX_VALUE, 0, "PT9223372036854775807S"},
            {Long.MIN_VALUE, 0, "PT-9223372036854775808S"},
        };
    }

    @Test(dataProvider="ToString")
    public void test_toString(long seconds, int nanos, String expected) {
        Duration t = Duration.duration(seconds, nanos);
        assertEquals(t.toString(), expected);
    }
}
