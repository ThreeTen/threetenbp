/*
 * Copyright (c) 2007-2012, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.calendrical.ChronoField.AMPM_OF_DAY;
import static javax.time.calendrical.ChronoField.CLOCK_HOUR_OF_AMPM;
import static javax.time.calendrical.ChronoField.CLOCK_HOUR_OF_DAY;
import static javax.time.calendrical.ChronoField.HOUR_OF_AMPM;
import static javax.time.calendrical.ChronoField.HOUR_OF_DAY;
import static javax.time.calendrical.ChronoField.MICRO_OF_DAY;
import static javax.time.calendrical.ChronoField.MICRO_OF_SECOND;
import static javax.time.calendrical.ChronoField.MILLI_OF_DAY;
import static javax.time.calendrical.ChronoField.MILLI_OF_SECOND;
import static javax.time.calendrical.ChronoField.MINUTE_OF_DAY;
import static javax.time.calendrical.ChronoField.MINUTE_OF_HOUR;
import static javax.time.calendrical.ChronoField.NANO_OF_DAY;
import static javax.time.calendrical.ChronoField.NANO_OF_SECOND;
import static javax.time.calendrical.ChronoField.SECOND_OF_DAY;
import static javax.time.calendrical.ChronoField.SECOND_OF_MINUTE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.time.calendrical.ChronoField;
import javax.time.calendrical.ChronoUnit;
import javax.time.calendrical.DateTimeAccessor;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.JulianDayField;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test LocalTime.
 */
@Test
public class TestLocalTime extends AbstractDateTimeTest {

    private LocalTime TEST_12_30_40_987654321;

    @BeforeMethod(groups={"tck","implementation"})
    public void setUp() {
        TEST_12_30_40_987654321 = LocalTime.of(12, 30, 40, 987654321);
    }

    //-----------------------------------------------------------------------
    @Override
    protected List<DateTimeAccessor> samples() {
        DateTimeAccessor[] array = {TEST_12_30_40_987654321, LocalTime.MIN_TIME, LocalTime.MAX_TIME, LocalTime.MIDNIGHT, LocalTime.MIDDAY};
        return Arrays.asList(array);
    }

    @Override
    protected List<DateTimeField> validFields() {
        DateTimeField[] array = {
            NANO_OF_SECOND,
            NANO_OF_DAY,
            MICRO_OF_SECOND,
            MICRO_OF_DAY,
            MILLI_OF_SECOND,
            MILLI_OF_DAY,
            SECOND_OF_MINUTE,
            SECOND_OF_DAY,
            MINUTE_OF_HOUR,
            MINUTE_OF_DAY,
            CLOCK_HOUR_OF_AMPM,
            HOUR_OF_AMPM,
            CLOCK_HOUR_OF_DAY,
            HOUR_OF_DAY,
            AMPM_OF_DAY,
        };
        return Arrays.asList(array);
    }

    @Override
    protected List<DateTimeField> invalidFields() {
        List<DateTimeField> list = new ArrayList<>(Arrays.<DateTimeField>asList(ChronoField.values()));
        list.removeAll(validFields());
        list.add(JulianDayField.JULIAN_DAY);
        list.add(JulianDayField.MODIFIED_JULIAN_DAY);
        list.add(JulianDayField.RATA_DIE);
        return list;
    }

    //-----------------------------------------------------------------------
    private void check(LocalTime time, int h, int m, int s, int n) {
        assertEquals(time.getHour(), h);
        assertEquals(time.getMinute(), m);
        assertEquals(time.getSecond(), s);
        assertEquals(time.getNano(), n);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"implementation"})
    public void test_interfaces() {
        Object obj = TEST_12_30_40_987654321;
        assertTrue(obj instanceof DateTimeAccessor);
        assertTrue(obj instanceof Serializable);
        assertTrue(obj instanceof Comparable<?>);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck","implementation"})
    public void constant_MIDNIGHT() {
        check(LocalTime.MIDNIGHT, 0, 0, 0, 0);
    }

    @Test(groups={"implementation"})
    public void constant_MIDNIGHT_same() {
        assertSame(LocalTime.MIDNIGHT, LocalTime.MIDNIGHT);
        assertSame(LocalTime.MIDNIGHT, LocalTime.of(0, 0));
    }

    @Test(groups={"tck","implementation"})
    public void constant_MIDDAY() {
        check(LocalTime.MIDDAY, 12, 0, 0, 0);
    }

    @Test(groups={"implementation"})
    public void constant_MIDDAY_same() {
        assertSame(LocalTime.MIDDAY, LocalTime.MIDDAY);
        assertSame(LocalTime.MIDDAY, LocalTime.of(12, 0));
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck","implementation"})
    public void constant_MIN_TIME() {
        check(LocalTime.MIN_TIME, 0, 0, 0, 0);
    }

    @Test(groups={"implementation"})
    public void constant_MIN_TIME_same() {
        assertSame(LocalTime.MIN_TIME, LocalTime.of(0, 0));
    }

    @Test(groups={"tck","implementation"})
    public void constant_MAX_TIME() {
        check(LocalTime.MAX_TIME, 23, 59, 59, 999999999);
    }

    @Test(groups={"implementation"})
    public void constant_MAX_TIME_same() {
        assertSame(LocalTime.MIDDAY, LocalTime.MIDDAY);
        assertSame(LocalTime.MIDDAY, LocalTime.of(12, 0));
    }

    @Test(groups={"implementation"})
    public void factory_time_2ints_singletons() {
        for (int i = 0; i < 24; i++) {
            LocalTime test1 = LocalTime.of(i, 0);
            LocalTime test2 = LocalTime.of(i, 0);
            assertSame(test1, test2);
        }
    }

    @Test(groups={"implementation"})
    public void factory_time_3ints_singletons() {
        for (int i = 0; i < 24; i++) {
            LocalTime test1 = LocalTime.of(i, 0, 0);
            LocalTime test2 = LocalTime.of(i, 0, 0);
            assertSame(test1, test2);
        }
    }

    @Test(groups={"implementation"})
    public void factory_time_4ints_singletons() {
        for (int i = 0; i < 24; i++) {
            LocalTime test1 = LocalTime.of(i, 0, 0, 0);
            LocalTime test2 = LocalTime.of(i, 0, 0, 0);
            assertSame(test1, test2);
        }
    }

    @Test(groups={"implementation"})
    public void factory_ofSecondOfDay_singletons() {
        for (int i = 0; i < 24; i++) {
            LocalTime test1 = LocalTime.ofSecondOfDay(i * 60L * 60L);
            LocalTime test2 = LocalTime.of(i, 0);
            assertSame(test1, test2);
        }
    }

    @Test(groups={"implementation"})
    public void factory_ofSecondOfDay7_long_int_singletons() {
        for (int i = 0; i < 24; i++) {
            LocalTime test1 = LocalTime.ofSecondOfDay(i * 60L * 60L, 0);
            LocalTime test2 = LocalTime.of(i, 0);
            assertSame(test1, test2);
        }
    }

    @Test(groups={"implementation"})
    public void factory_ofNanoOfDay_singletons() {
        for (int i = 0; i < 24; i++) {
            LocalTime test1 = LocalTime.ofNanoOfDay(i * 1000000000L * 60L * 60L);
            LocalTime test2 = LocalTime.of(i, 0);
            assertSame(test1, test2);
        }
    }

    @Test(groups={"implementation"})
    public void test_withHour_noChange_same() {
        LocalTime t = TEST_12_30_40_987654321.withHour(12);
        assertSame(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_withHour_toMidnight_same() {
        LocalTime t = LocalTime.of(1, 0).withHour(0);
        assertSame(t, LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_withHour_toMidday_same() {
        LocalTime t = LocalTime.of(1, 0).withHour(12);
        assertSame(t, LocalTime.MIDDAY);
    }

    @Test(groups={"implementation"})
    public void test_withMinute_noChange_same() {
        LocalTime t = TEST_12_30_40_987654321.withMinute(30);
        assertSame(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_withMinute_toMidnight_same() {
        LocalTime t = LocalTime.of(0, 1).withMinute(0);
        assertSame(t, LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_withMinute_toMidday_same() {
        LocalTime t = LocalTime.of(12, 1).withMinute(0);
        assertSame(t, LocalTime.MIDDAY);
    }

    @Test(groups={"implementation"})
    public void test_withSecond_noChange_same() {
        LocalTime t = TEST_12_30_40_987654321.withSecond(40);
        assertSame(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_withSecond_toMidnight_same() {
        LocalTime t = LocalTime.of(0, 0, 1).withSecond(0);
        assertSame(t, LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_withSecond_toMidday_same() {
        LocalTime t = LocalTime.of(12, 0, 1).withSecond(0);
        assertSame(t, LocalTime.MIDDAY);
    }

    @Test(groups={"implementation"})
    public void test_withNanoOfSecond_noChange_same() {
        LocalTime t = TEST_12_30_40_987654321.withNano(987654321);
        assertSame(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_withNanoOfSecond_toMidnight_same() {
        LocalTime t = LocalTime.of(0, 0, 0, 1).withNano(0);
        assertSame(t, LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_withNanoOfSecond_toMidday_same() {
        LocalTime t = LocalTime.of(12, 0, 0, 1).withNano(0);
        assertSame(t, LocalTime.MIDDAY);
    }

    @Test(groups={"implementation"})
    public void test_plus_Period_zero() {
        LocalTime t = TEST_12_30_40_987654321.plus(MockSimplePeriod.ZERO_SECONDS);
        assertSame(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_plus_longPeriodUnit_zero() {
        LocalTime t = TEST_12_30_40_987654321.plus(0, ChronoUnit.SECONDS);
        assertSame(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_plus_adjuster_zero_same() {
        LocalTime t = TEST_12_30_40_987654321.plus(Period.ZERO);
        assertSame(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_plusHours_noChange_same() {
        LocalTime t = TEST_12_30_40_987654321.plusHours(0);
        assertSame(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_plusHours_toMidnight_same() {
        LocalTime t = LocalTime.of(23, 0).plusHours(1);
        assertSame(t, LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_plusHours_toMidday_same() {
        LocalTime t = LocalTime.of(11, 0).plusHours(1);
        assertSame(t, LocalTime.MIDDAY);
    }

    @Test(groups={"implementation"})
    public void test_plusMinutes_noChange_same() {
        LocalTime t = TEST_12_30_40_987654321.plusMinutes(0);
        assertSame(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_plusMinutes_noChange_oneDay_same() {
        LocalTime t = TEST_12_30_40_987654321.plusMinutes(24 * 60);
        assertSame(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_plusMinutes_toMidnight_same() {
        LocalTime t = LocalTime.of(23, 59).plusMinutes(1);
        assertSame(t, LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_plusMinutes_toMidday_same() {
        LocalTime t = LocalTime.of(11, 59).plusMinutes(1);
        assertSame(t, LocalTime.MIDDAY);
    }

    @Test(groups={"implementation"})
    public void test_plusSeconds_noChange_same() {
        LocalTime t = TEST_12_30_40_987654321.plusSeconds(0);
        assertSame(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_plusSeconds_noChange_oneDay_same() {
        LocalTime t = TEST_12_30_40_987654321.plusSeconds(24 * 60 * 60);
        assertSame(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_plusSeconds_toMidnight_same() {
        LocalTime t = LocalTime.of(23, 59, 59).plusSeconds(1);
        assertSame(t, LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_plusSeconds_toMidday_same() {
        LocalTime t = LocalTime.of(11, 59, 59).plusSeconds(1);
        assertSame(t, LocalTime.MIDDAY);
    }

    @Test(groups={"implementation"})
    public void test_plusSeconds_big() {
        LocalTime t = LocalTime.of(2, 30).plusSeconds(Long.MAX_VALUE);
        int secs = (int) (Long.MAX_VALUE % (24L * 60L * 60L));
        assertEquals(t, LocalTime.of(2, 30).plusSeconds(secs));
    }

    @Test(groups={"implementation"})
    public void test_plusNanos_noChange_same() {
        LocalTime t = TEST_12_30_40_987654321.plusNanos(0);
        assertSame(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_plusNanos_noChange_oneDay_same() {
        LocalTime t = TEST_12_30_40_987654321.plusNanos(24 * 60 * 60 * 1000000000L);
        assertSame(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_plusNanos_toMidnight_same() {
        LocalTime t = LocalTime.of(23, 59, 59, 999999999).plusNanos(1);
        assertSame(t, LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_plusNanos_toMidday_same() {
        LocalTime t = LocalTime.of(11, 59, 59, 999999999).plusNanos(1);
        assertSame(t, LocalTime.MIDDAY);
    }

    @Test(groups={"implementation"})
    public void test_minus_adjuster_zero_same() {
        LocalTime t = TEST_12_30_40_987654321.minus(Period.ZERO);
        assertSame(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_minus_Period_zero() {
        LocalTime t = TEST_12_30_40_987654321.minus(MockSimplePeriod.ZERO_SECONDS);
        assertSame(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_minus_longPeriodUnit_zero() {
        LocalTime t = TEST_12_30_40_987654321.minus(0, ChronoUnit.SECONDS);
        assertSame(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_minusHours_noChange_same() {
        LocalTime t = TEST_12_30_40_987654321.minusHours(0);
        assertSame(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_minusHours_toMidnight_same() {
        LocalTime t = LocalTime.of(1, 0).minusHours(1);
        assertSame(t, LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_minusHours_toMidday_same() {
        LocalTime t = LocalTime.of(13, 0).minusHours(1);
        assertSame(t, LocalTime.MIDDAY);
    }

    @Test(groups={"implementation"})
    public void test_minusMinutes_noChange_same() {
        LocalTime t = TEST_12_30_40_987654321.minusMinutes(0);
        assertSame(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_minusMinutes_noChange_oneDay_same() {
        LocalTime t = TEST_12_30_40_987654321.minusMinutes(24 * 60);
        assertSame(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_minusMinutes_toMidnight_same() {
        LocalTime t = LocalTime.of(0, 1).minusMinutes(1);
        assertSame(t, LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_minusMinutes_toMidday_same() {
        LocalTime t = LocalTime.of(12, 1).minusMinutes(1);
        assertSame(t, LocalTime.MIDDAY);
    }

    @Test(groups={"implementation"})
    public void test_minusSeconds_noChange_same() {
        LocalTime t = TEST_12_30_40_987654321.minusSeconds(0);
        assertSame(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_minusSeconds_noChange_oneDay_same() {
        LocalTime t = TEST_12_30_40_987654321.minusSeconds(24 * 60 * 60);
        assertSame(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_minusSeconds_toMidnight_same() {
        LocalTime t = LocalTime.of(0, 0, 1).minusSeconds(1);
        assertSame(t, LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_minusSeconds_toMidday_same() {
        LocalTime t = LocalTime.of(12, 0, 1).minusSeconds(1);
        assertSame(t, LocalTime.MIDDAY);
    }

    @Test(groups={"implementation"})
    public void test_minusNanos_noChange_same() {
        LocalTime t = TEST_12_30_40_987654321.minusNanos(0);
        assertSame(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_minusNanos_noChange_oneDay_same() {
        LocalTime t = TEST_12_30_40_987654321.minusNanos(24 * 60 * 60 * 1000000000L);
        assertSame(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_minusNanos_toMidnight_same() {
        LocalTime t = LocalTime.of(0, 0, 0, 1).minusNanos(1);
        assertSame(t, LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_minusNanos_toMidday_same() {
        LocalTime t = LocalTime.of(12, 0, 0, 1).minusNanos(1);
        assertSame(t, LocalTime.MIDDAY);
    }

    void doTest_comparisons_LocalTime(LocalTime... localTimes) {
        for (int i = 0; i < localTimes.length; i++) {
            LocalTime a = localTimes[i];
            for (int j = 0; j < localTimes.length; j++) {
                LocalTime b = localTimes[j];
                if (i < j) {
                    assertTrue(a.compareTo(b) < 0, a + " <=> " + b);
                    assertEquals(a.isBefore(b), true, a + " <=> " + b);
                    assertEquals(a.isAfter(b), false, a + " <=> " + b);
                    assertEquals(a.equals(b), false, a + " <=> " + b);
                } else if (i > j) {
                    assertTrue(a.compareTo(b) > 0, a + " <=> " + b);
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

}
