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

import java.io.Serializable;

import javax.time.calendar.Calendrical;
import javax.time.calendar.DateYMD;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.field.MonthOfYear;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test DateYMD.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestDateYMD {

    private DateYMD TEST_2007_07_15;

    @BeforeMethod
    public void setUp() {
        TEST_2007_07_15 = DateYMD.date(2007, 7, 15);
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(TEST_2007_07_15 instanceof Calendrical);
        assertTrue(TEST_2007_07_15 instanceof Serializable);
        assertTrue(TEST_2007_07_15 instanceof Comparable);
    }

    //-----------------------------------------------------------------------
    public void factory_date_ints() {
        assertEquals(TEST_2007_07_15.getYear(), 2007);
        assertEquals(TEST_2007_07_15.getMonthOfYear(), MonthOfYear.JULY);
        assertEquals(TEST_2007_07_15.getDayOfMonth(), 15);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_date_ints_dayTooLow() {
        DateYMD.date(2007, 1, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_date_ints_dayTooHigh() {
        DateYMD.date(2007, 1, 32);
    }


    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_date_ints_monthTooLow() {
        DateYMD.date(2007, 0, 1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_date_ints_monthTooHigh() {
        DateYMD.date(2007, 13, 1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_date_ints_yearTooLow() {
        DateYMD.date(Integer.MIN_VALUE, 1, 1);
    }

//    //-----------------------------------------------------------------------
//    @DataProvider(name="MillisDateYMDNoNanos")
//    Object[][] provider_factory_millisDateYMD_long() {
//        return new Object[][] {
//            {0, 0, 0},
//            {1, 0, 1000000},
//            {2, 0, 2000000},
//            {999, 0, 999000000},
//            {1000, 1, 0},
//            {1001, 1, 1000000},
//            {-1, -1, 999000000},
//            {-2, -1, 998000000},
//            {-999, -1, 1000000},
//            {-1000, -1, 0},
//            {-1001, -2, 999000000},
//        };
//    }
//
//    @Test(dataProvider="MillisDateYMDNoNanos")
//    public void factory_millisDateYMD_long(long millis, long expectedSeconds, int expectedNanoOfSecond) {
//        DateYMD t = DateYMD.millisDateYMD(millis);
//        assertEquals(t.getSeconds(), expectedSeconds);
//        assertEquals(t.getNanoOfSecond(), expectedNanoOfSecond);
//    }
//
//    //-----------------------------------------------------------------------
//    @DataProvider(name="MillisDateYMDWithNanos")
//    Object[][] provider_factory_millisDateYMD_long_int() {
//        return new Object[][] {
//            {0, 0, 0, 0},
//            {1, 0, 0, 1000000},
//            {2, 0, 0, 2000000},
//            {999, 0, 0, 999000000},
//            {1000, 0, 1, 0},
//            {1001, 0, 1, 1000000},
//            {-1, 0, -1, 999000000},
//            {-2, 0, -1, 998000000},
//            {-999, 0, -1, 1000000},
//            {-1000, 0, -1, 0},
//            {-1001, 0, -2, 999000000},
//            {0, 1, 0, 1},
//            {1, 1, 0, 1000001},
//            {999, 1, 0, 999000001},
//            {1000, 1, 1, 1},
//            {1001, 1, 1, 1000001},
//            {-1, 1, -1, 999000001},
//            {-2, 1, -1, 998000001},
//            {-999, 1, -1, 1000001},
//            {-1000, 1, -1, 1},
//            {-1001, 1, -2, 999000001},
//            {0, 999999, 0, 999999},
//            {1, 999999, 0, 1999999},
//            {999, 999999, 0, 999999999},
//            {1000, 999999, 1, 999999},
//            {1001, 999999, 1, 1999999},
//            {-1, 999999, -1, 999999999},
//            {-2, 999999, -1, 998999999},
//            {-999, 999999, -1, 1999999},
//            {-1000, 999999, -1, 999999},
//            {-1001, 999999, -2, 999999999},
//        };
//    }
//
//    @Test(dataProvider="MillisDateYMDWithNanos")
//    public void factory_millisDateYMD_long_int(long millis, int nanos, long expectedSeconds, int expectedNanoOfSecond) {
//        DateYMD t = DateYMD.millisDateYMD(millis, nanos);
//        assertEquals(t.getSeconds(), expectedSeconds);
//        assertEquals(t.getNanoOfSecond(), expectedNanoOfSecond);
//    }
//
//    @Test(expectedExceptions=IllegalArgumentException.class)
//    public void test_factory_millisDateYMD_long_int_nanosNegative() {
//        DateYMD.millisDateYMD(0L, -1);
//    }
//
//    @Test(expectedExceptions=IllegalArgumentException.class)
//    public void test_factory_millisDateYMD_long_int_nanosTooLarge() {
//        DateYMD.millisDateYMD(0L, 1000000000);
//    }
//
//    //-----------------------------------------------------------------------
//    public void test_deserializationSingleton() throws Exception {
//        DateYMD orginal = DateYMD.date(2);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ObjectOutputStream out = new ObjectOutputStream(baos);
//        out.writeObject(orginal);
//        out.close();
//        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
//        ObjectInputStream in = new ObjectInputStream(bais);
//        DateYMD ser = (DateYMD) in.readObject();
//        assertEquals(DateYMD.date(2), ser);
//    }
//
//    //-----------------------------------------------------------------------
//    public void test_comparisons() {
//        doTest_comparisons_DateYMD(
//            DateYMD.date(-2L, 0),
//            DateYMD.date(-2L, 999999998),
//            DateYMD.date(-2L, 999999999),
//            DateYMD.date(-1L, 0),
//            DateYMD.date(-1L, 1),
//            DateYMD.date(-1L, 999999998),
//            DateYMD.date(-1L, 999999999),
//            DateYMD.date(0L, 0),
//            DateYMD.date(0L, 1),
//            DateYMD.date(0L, 2),
//            DateYMD.date(0L, 999999999),
//            DateYMD.date(1L, 0),
//            DateYMD.date(2L, 0)
//        );
//    }
//
//    void doTest_comparisons_DateYMD(DateYMD... dates) {
//        for (int i = 0; i < dates.length; i++) {
//            DateYMD a = dates[i];
//            for (int j = 0; j < dates.length; j++) {
//                DateYMD b = dates[j];
//                if (i < j) {
//                    assertEquals(a.compareTo(b), -1, a + " <=> " + b);
//                    assertEquals(a.isBefore(b), true, a + " <=> " + b);
//                    assertEquals(a.isAfter(b), false, a + " <=> " + b);
//                    assertEquals(a.equals(b), false, a + " <=> " + b);
//                } else if (i > j) {
//                    assertEquals(a.compareTo(b), 1, a + " <=> " + b);
//                    assertEquals(a.isBefore(b), false, a + " <=> " + b);
//                    assertEquals(a.isAfter(b), true, a + " <=> " + b);
//                    assertEquals(a.equals(b), false, a + " <=> " + b);
//                } else {
//                    assertEquals(a.compareTo(b), 0, a + " <=> " + b);
//                    assertEquals(a.isBefore(b), false, a + " <=> " + b);
//                    assertEquals(a.isAfter(b), false, a + " <=> " + b);
//                    assertEquals(a.equals(b), true, a + " <=> " + b);
//                }
//            }
//        }
//    }
//
//    @Test(expectedExceptions=NullPointerException.class)
//    public void test_compareTo_ObjectNull() {
//        DateYMD a = DateYMD.date(0L, 0);
//        a.compareTo(null);
//    }
//
//    @Test(expectedExceptions=NullPointerException.class)
//    public void test_isBefore_ObjectNull() {
//        DateYMD a = DateYMD.date(0L, 0);
//        a.isBefore(null);
//    }
//
//    @Test(expectedExceptions=NullPointerException.class)
//    public void test_isAfter_ObjectNull() {
//        DateYMD a = DateYMD.date(0L, 0);
//        a.isAfter(null);
//    }
//
//    //-----------------------------------------------------------------------
//    public void test_equals() {
//        DateYMD test5a = DateYMD.date(5L, 20);
//        DateYMD test5b = DateYMD.date(5L, 20);
//        DateYMD test5n = DateYMD.date(5L, 30);
//        DateYMD test6 = DateYMD.date(6L, 20);
//        
//        assertEquals(test5a.equals(test5a), true);
//        assertEquals(test5a.equals(test5b), true);
//        assertEquals(test5a.equals(test5n), false);
//        assertEquals(test5a.equals(test6), false);
//        
//        assertEquals(test5b.equals(test5a), true);
//        assertEquals(test5b.equals(test5b), true);
//        assertEquals(test5b.equals(test5n), false);
//        assertEquals(test5b.equals(test6), false);
//        
//        assertEquals(test5n.equals(test5a), false);
//        assertEquals(test5n.equals(test5b), false);
//        assertEquals(test5n.equals(test5n), true);
//        assertEquals(test5n.equals(test6), false);
//        
//        assertEquals(test6.equals(test5a), false);
//        assertEquals(test6.equals(test5b), false);
//        assertEquals(test6.equals(test5n), false);
//        assertEquals(test6.equals(test6), true);
//    }
//
//    public void test_equals_null() {
//        DateYMD test5 = DateYMD.date(5L, 20);
//        assertEquals(test5.equals(null), false);
//    }
//
//    public void test_equals_otherClass() {
//        DateYMD test5 = DateYMD.date(5L, 20);
//        assertEquals(test5.equals(""), false);
//    }
//
//    //-----------------------------------------------------------------------
//    public void test_hashCode() {
//        DateYMD test5a = DateYMD.date(5L, 20);
//        DateYMD test5b = DateYMD.date(5L, 20);
//        DateYMD test5n = DateYMD.date(5L, 30);
//        DateYMD test6 = DateYMD.date(6L, 20);
//        
//        assertEquals(test5a.hashCode() == test5a.hashCode(), true);
//        assertEquals(test5a.hashCode() == test5b.hashCode(), true);
//        assertEquals(test5b.hashCode() == test5b.hashCode(), true);
//        
//        assertEquals(test5a.hashCode() == test5n.hashCode(), false);
//        assertEquals(test5a.hashCode() == test6.hashCode(), false);
//    }
//
//    //-----------------------------------------------------------------------
//    @DataProvider(name="PlusSeconds")
//    Object[][] provider_plusSeconds_long() {
//        return new Object[][] {
//            {0, 0, 0, 0, 0},
//            {0, 0, 1, 1, 0},
//            {0, 0, -1, -1, 0},
//            {0, 0, Long.MAX_VALUE, Long.MAX_VALUE, 0},
//            {0, 0, Long.MIN_VALUE, Long.MIN_VALUE, 0},
//            {1, 0, 0, 1, 0},
//            {1, 0, 1, 2, 0},
//            {1, 0, -1, 0, 0},
//            {1, 0, Long.MAX_VALUE - 1, Long.MAX_VALUE, 0},
//            {1, 0, Long.MIN_VALUE, Long.MIN_VALUE + 1, 0},
//            {1, 1, 0, 1, 1},
//            {1, 1, 1, 2, 1},
//            {1, 1, -1, 0, 1},
//            {1, 1, Long.MAX_VALUE - 1, Long.MAX_VALUE, 1},
//            {1, 1, Long.MIN_VALUE, Long.MIN_VALUE + 1, 1},
//            {-1, 1, 0, -1, 1},
//            {-1, 1, 1, 0, 1},
//            {-1, 1, -1, -2, 1},
//            {-1, 1, Long.MAX_VALUE, Long.MAX_VALUE - 1, 1},
//            {-1, 1, Long.MIN_VALUE + 1, Long.MIN_VALUE, 1},
//        };
//    }
//
//    @Test(dataProvider="PlusSeconds")
//    public void plusSeconds_long(long seconds, int nanos, long amount, long expectedSeconds, int expectedNanoOfSecond) {
//        DateYMD t = DateYMD.date(seconds, nanos);
//        t = t.plusSeconds(amount);
//        assertEquals(t.getSeconds(), expectedSeconds);
//        assertEquals(t.getNanoOfSecond(), expectedNanoOfSecond);
//    }
//
//    @Test(expectedExceptions = {ArithmeticException.class})
//    public void plusSeconds_long_overflowTooBig() {
//        DateYMD t = DateYMD.date(1, 0);
//        t.plusSeconds(Long.MAX_VALUE);
//    }
//
//    @Test(expectedExceptions = {ArithmeticException.class})
//    public void plusSeconds_long_overflowTooSmall() {
//        DateYMD t = DateYMD.date(-1, 0);
//        t.plusSeconds(Long.MIN_VALUE);
//    }
//
//    //-----------------------------------------------------------------------
//    @DataProvider(name="PlusMillis")
//    Object[][] provider_plusMillis_long() {
//        return new Object[][] {
//            {0, 0, 0,       0, 0},
//            {0, 0, 1,       0, 1000000},
//            {0, 0, 999,     0, 999000000},
//            {0, 0, 1000,    1, 0},
//            {0, 0, 1001,    1, 1000000},
//            {0, 0, 1999,    1, 999000000},
//            {0, 0, 2000,    2, 0},
//            {0, 0, -1,      -1, 999000000},
//            {0, 0, -999,    -1, 1000000},
//            {0, 0, -1000,   -1, 0},
//            {0, 0, -1001,   -2, 999000000},
//            {0, 0, -1999,   -2, 1000000},
//            
//            {0, 1, 0,       0, 1},
//            {0, 1, 1,       0, 1000001},
//            {0, 1, 998,     0, 998000001},
//            {0, 1, 999,     0, 999000001},
//            {0, 1, 1000,    1, 1},
//            {0, 1, 1998,    1, 998000001},
//            {0, 1, 1999,    1, 999000001},
//            {0, 1, 2000,    2, 1},
//            {0, 1, -1,      -1, 999000001},
//            {0, 1, -2,      -1, 998000001},
//            {0, 1, -1000,   -1, 1},
//            {0, 1, -1001,   -2, 999000001},
//            
//            {0, 1000000, 0,       0, 1000000},
//            {0, 1000000, 1,       0, 2000000},
//            {0, 1000000, 998,     0, 999000000},
//            {0, 1000000, 999,     1, 0},
//            {0, 1000000, 1000,    1, 1000000},
//            {0, 1000000, 1998,    1, 999000000},
//            {0, 1000000, 1999,    2, 0},
//            {0, 1000000, 2000,    2, 1000000},
//            {0, 1000000, -1,      0, 0},
//            {0, 1000000, -2,      -1, 999000000},
//            {0, 1000000, -999,    -1, 2000000},
//            {0, 1000000, -1000,   -1, 1000000},
//            {0, 1000000, -1001,   -1, 0},
//            {0, 1000000, -1002,   -2, 999000000},
//            
//            {0, 999999999, 0,     0, 999999999},
//            {0, 999999999, 1,     1, 999999},
//            {0, 999999999, 999,   1, 998999999},
//            {0, 999999999, 1000,  1, 999999999},
//            {0, 999999999, 1001,  2, 999999},
//            {0, 999999999, -1,    0, 998999999},
//            {0, 999999999, -1000, -1, 999999999},
//            {0, 999999999, -1001, -1, 998999999},
//        };
//    }
//
//    @Test(dataProvider="PlusMillis")
//    public void plusMillis_long(long seconds, int nanos, long amount, long expectedSeconds, int expectedNanoOfSecond) {
//        DateYMD t = DateYMD.date(seconds, nanos);
//        t = t.plusMillis(amount);
//        assertEquals(t.getSeconds(), expectedSeconds);
//        assertEquals(t.getNanoOfSecond(), expectedNanoOfSecond);
//    }
//    @Test(dataProvider="PlusMillis")
//    public void plusMillis_long_oneMore(long seconds, int nanos, long amount, long expectedSeconds, int expectedNanoOfSecond) {
//        DateYMD t = DateYMD.date(seconds + 1, nanos);
//        t = t.plusMillis(amount);
//        assertEquals(t.getSeconds(), expectedSeconds + 1);
//        assertEquals(t.getNanoOfSecond(), expectedNanoOfSecond);
//    }
//    @Test(dataProvider="PlusMillis")
//    public void plusMillis_long_minusOneLess(long seconds, int nanos, long amount, long expectedSeconds, int expectedNanoOfSecond) {
//        DateYMD t = DateYMD.date(seconds - 1, nanos);
//        t = t.plusMillis(amount);
//        assertEquals(t.getSeconds(), expectedSeconds - 1);
//        assertEquals(t.getNanoOfSecond(), expectedNanoOfSecond);
//    }
//
//    public void plusMillis_long_max() {
//        DateYMD t = DateYMD.date(Long.MAX_VALUE, 998999999);
//        t = t.plusMillis(1);
//        assertEquals(t.getSeconds(), Long.MAX_VALUE);
//        assertEquals(t.getNanoOfSecond(), 999999999);
//    }
//
//    @Test(expectedExceptions = {ArithmeticException.class})
//    public void plusMillis_long_overflowTooBig() {
//        DateYMD t = DateYMD.date(Long.MAX_VALUE, 999000000);
//        t.plusMillis(1);
//    }
//
//    public void plusMillis_long_min() {
//        DateYMD t = DateYMD.date(Long.MIN_VALUE, 1000000);
//        t = t.plusMillis(-1);
//        assertEquals(t.getSeconds(), Long.MIN_VALUE);
//        assertEquals(t.getNanoOfSecond(), 0);
//    }
//
//    @Test(expectedExceptions = {ArithmeticException.class})
//    public void plusMillis_long_overflowTooSmall() {
//        DateYMD t = DateYMD.date(Long.MIN_VALUE, 0);
//        t.plusMillis(-1);
//    }
//
//    //-----------------------------------------------------------------------
//    @DataProvider(name="PlusNanos")
//    Object[][] provider_plusNanos_long() {
//        return new Object[][] {
//            {0, 0, 0,           0, 0},
//            {0, 0, 1,           0, 1},
//            {0, 0, 999999999,   0, 999999999},
//            {0, 0, 1000000000,  1, 0},
//            {0, 0, 1000000001,  1, 1},
//            {0, 0, 1999999999,  1, 999999999},
//            {0, 0, 2000000000,  2, 0},
//            {0, 0, -1,          -1, 999999999},
//            {0, 0, -999999999,  -1, 1},
//            {0, 0, -1000000000, -1, 0},
//            {0, 0, -1000000001, -2, 999999999},
//            {0, 0, -1999999999, -2, 1},
//            
//            {1, 0, 0,           1, 0},
//            {1, 0, 1,           1, 1},
//            {1, 0, 999999999,   1, 999999999},
//            {1, 0, 1000000000,  2, 0},
//            {1, 0, 1000000001,  2, 1},
//            {1, 0, 1999999999,  2, 999999999},
//            {1, 0, 2000000000,  3, 0},
//            {1, 0, -1,          0, 999999999},
//            {1, 0, -999999999,  0, 1},
//            {1, 0, -1000000000, 0, 0},
//            {1, 0, -1000000001, -1, 999999999},
//            {1, 0, -1999999999, -1, 1},
//            
//            {-1, 0, 0,           -1, 0},
//            {-1, 0, 1,           -1, 1},
//            {-1, 0, 999999999,   -1, 999999999},
//            {-1, 0, 1000000000,  0, 0},
//            {-1, 0, 1000000001,  0, 1},
//            {-1, 0, 1999999999,  0, 999999999},
//            {-1, 0, 2000000000,  1, 0},
//            {-1, 0, -1,          -2, 999999999},
//            {-1, 0, -999999999,  -2, 1},
//            {-1, 0, -1000000000, -2, 0},
//            {-1, 0, -1000000001, -3, 999999999},
//            {-1, 0, -1999999999, -3, 1},
//            
//            {1, 1, 0,           1, 1},
//            {1, 1, 1,           1, 2},
//            {1, 1, 999999998,   1, 999999999},
//            {1, 1, 999999999,   2, 0},
//            {1, 1, 1000000000,  2, 1},
//            {1, 1, 1999999998,  2, 999999999},
//            {1, 1, 1999999999,  3, 0},
//            {1, 1, 2000000000,  3, 1},
//            {1, 1, -1,          1, 0},
//            {1, 1, -2,          0, 999999999},
//            {1, 1, -1000000000, 0, 1},
//            {1, 1, -1000000001, 0, 0},
//            {1, 1, -1000000002, -1, 999999999},
//            {1, 1, -2000000000, -1, 1},
//            
//            {1, 999999999, 0,           1, 999999999},
//            {1, 999999999, 1,           2, 0},
//            {1, 999999999, 999999999,   2, 999999998},
//            {1, 999999999, 1000000000,  2, 999999999},
//            {1, 999999999, 1000000001,  3, 0},
//            {1, 999999999, -1,          1, 999999998},
//            {1, 999999999, -1000000000, 0, 999999999},
//            {1, 999999999, -1000000001, 0, 999999998},
//            {1, 999999999, -1999999999, 0, 0},
//            {1, 999999999, -2000000000, -1, 999999999},
//            
//            {Long.MAX_VALUE, 0, 999999999, Long.MAX_VALUE, 999999999},
//            {Long.MAX_VALUE - 1, 0, 1999999999, Long.MAX_VALUE, 999999999},
//            {Long.MIN_VALUE, 1, -1, Long.MIN_VALUE, 0},
//            {Long.MIN_VALUE + 1, 1, -1000000001, Long.MIN_VALUE, 0},
//        };
//    }
//
//    @Test(dataProvider="PlusNanos")
//    public void plusNanos_long(long seconds, int nanos, long amount, long expectedSeconds, int expectedNanoOfSecond) {
//        DateYMD t = DateYMD.date(seconds, nanos);
//        t = t.plusNanos(amount);
//        assertEquals(t.getSeconds(), expectedSeconds);
//        assertEquals(t.getNanoOfSecond(), expectedNanoOfSecond);
//    }
//
//    @Test(expectedExceptions = {ArithmeticException.class})
//    public void plusNanos_long_overflowTooBig() {
//        DateYMD t = DateYMD.date(Long.MAX_VALUE, 999999999);
//        t.plusNanos(1);
//    }
//
//    @Test(expectedExceptions = {ArithmeticException.class})
//    public void plusNanos_long_overflowTooSmall() {
//        DateYMD t = DateYMD.date(Long.MIN_VALUE, 0);
//        t.plusNanos(-1);
//    }
//
//    //-----------------------------------------------------------------------
//    @DataProvider(name="ToString")
//    Object[][] provider_toString() {
//        return new Object[][] {
//            {0, 0, "P0S"},
//            {0, 1, "P0.000000001S"},
//            {0, 10, "P0.00000001S"},
//            {0, 100, "P0.0000001S"},
//            {0, 1000, "P0.000001S"},
//            {0, 10000, "P0.00001S"},
//            {0, 100000, "P0.0001S"},
//            {0, 1000000, "P0.001S"},
//            {0, 10000000, "P0.01S"},
//            {0, 100000000, "P0.1S"},
//            {0, 120000000, "P0.12S"},
//            {0, 123000000, "P0.123S"},
//            {0, 123400000, "P0.1234S"},
//            {0, 123450000, "P0.12345S"},
//            {0, 123456000, "P0.123456S"},
//            {0, 123456700, "P0.1234567S"},
//            {0, 123456780, "P0.12345678S"},
//            {0, 123456789, "P0.123456789S"},
//            {1, 0, "P1S"},
//            {-1, 0, "P-1S"},
//            {-1, 1000, "P-1.000001S"},
//            {Long.MAX_VALUE, 0, "P9223372036854775807S"},
//            {Long.MIN_VALUE, 0, "P-9223372036854775808S"},
//        };
//    }
//
//    @Test(dataProvider="ToString")
//    public void test_toString(long seconds, int nanos, String expected) {
//        DateYMD t = DateYMD.date(seconds, nanos);
//        assertEquals(t.toString(), expected);
//    }
//
}
