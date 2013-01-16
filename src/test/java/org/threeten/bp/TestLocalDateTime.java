/*
 * Copyright (c) 2007-2013, Stephen Colebourne & Michael Nascimento Santos
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
package org.threeten.bp;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.threeten.bp.temporal.ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH;
import static org.threeten.bp.temporal.ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR;
import static org.threeten.bp.temporal.ChronoField.ALIGNED_WEEK_OF_MONTH;
import static org.threeten.bp.temporal.ChronoField.ALIGNED_WEEK_OF_YEAR;
import static org.threeten.bp.temporal.ChronoField.AMPM_OF_DAY;
import static org.threeten.bp.temporal.ChronoField.CLOCK_HOUR_OF_AMPM;
import static org.threeten.bp.temporal.ChronoField.CLOCK_HOUR_OF_DAY;
import static org.threeten.bp.temporal.ChronoField.DAY_OF_MONTH;
import static org.threeten.bp.temporal.ChronoField.DAY_OF_WEEK;
import static org.threeten.bp.temporal.ChronoField.DAY_OF_YEAR;
import static org.threeten.bp.temporal.ChronoField.EPOCH_DAY;
import static org.threeten.bp.temporal.ChronoField.EPOCH_MONTH;
import static org.threeten.bp.temporal.ChronoField.ERA;
import static org.threeten.bp.temporal.ChronoField.HOUR_OF_AMPM;
import static org.threeten.bp.temporal.ChronoField.HOUR_OF_DAY;
import static org.threeten.bp.temporal.ChronoField.MICRO_OF_DAY;
import static org.threeten.bp.temporal.ChronoField.MICRO_OF_SECOND;
import static org.threeten.bp.temporal.ChronoField.MILLI_OF_DAY;
import static org.threeten.bp.temporal.ChronoField.MILLI_OF_SECOND;
import static org.threeten.bp.temporal.ChronoField.MINUTE_OF_DAY;
import static org.threeten.bp.temporal.ChronoField.MINUTE_OF_HOUR;
import static org.threeten.bp.temporal.ChronoField.MONTH_OF_YEAR;
import static org.threeten.bp.temporal.ChronoField.NANO_OF_DAY;
import static org.threeten.bp.temporal.ChronoField.NANO_OF_SECOND;
import static org.threeten.bp.temporal.ChronoField.SECOND_OF_DAY;
import static org.threeten.bp.temporal.ChronoField.SECOND_OF_MINUTE;
import static org.threeten.bp.temporal.ChronoField.YEAR;
import static org.threeten.bp.temporal.ChronoField.YEAR_OF_ERA;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.threeten.bp.temporal.ChronoField;
import org.threeten.bp.temporal.ChronoUnit;
import org.threeten.bp.temporal.JulianFields;
import org.threeten.bp.temporal.TemporalAccessor;
import org.threeten.bp.temporal.TemporalField;

/**
 * Test LocalDateTime.
 */
@Test
public class TestLocalDateTime extends AbstractDateTimeTest {

    private LocalDateTime TEST_2007_07_15_12_30_40_987654321 = LocalDateTime.of(2007, 7, 15, 12, 30, 40, 987654321);

    //-----------------------------------------------------------------------
    @Override
    protected List<TemporalAccessor> samples() {
        TemporalAccessor[] array = {TEST_2007_07_15_12_30_40_987654321, LocalDateTime.MAX_DATE_TIME, LocalDateTime.MIN_DATE_TIME, };
        return Arrays.asList(array);
    }

    @Override
    protected List<TemporalField> validFields() {
        TemporalField[] array = {
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
            DAY_OF_WEEK,
            ALIGNED_DAY_OF_WEEK_IN_MONTH,
            ALIGNED_DAY_OF_WEEK_IN_YEAR,
            DAY_OF_MONTH,
            DAY_OF_YEAR,
            EPOCH_DAY,
            ALIGNED_WEEK_OF_MONTH,
            ALIGNED_WEEK_OF_YEAR,
            MONTH_OF_YEAR,
            EPOCH_MONTH,
            YEAR_OF_ERA,
            YEAR,
            ERA,
            JulianFields.JULIAN_DAY,
            JulianFields.MODIFIED_JULIAN_DAY,
            JulianFields.RATA_DIE,
        };
        return Arrays.asList(array);
    }

    @Override
    protected List<TemporalField> invalidFields() {
        List<TemporalField> list = new ArrayList<>(Arrays.<TemporalField>asList(ChronoField.values()));
        list.removeAll(validFields());
        return list;
    }

    //-----------------------------------------------------------------------
    private void check(LocalDateTime dateTime, int y, int m, int d, int h, int mi, int s, int n) {
        assertEquals(dateTime.getYear(), y);
        assertEquals(dateTime.getMonth().getValue(), m);
        assertEquals(dateTime.getDayOfMonth(), d);
        assertEquals(dateTime.getHour(), h);
        assertEquals(dateTime.getMinute(), mi);
        assertEquals(dateTime.getSecond(), s);
        assertEquals(dateTime.getNano(), n);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"implementation"})
    public void test_interfaces() {
        Object obj = TEST_2007_07_15_12_30_40_987654321;
        assertTrue(obj instanceof TemporalAccessor);
        assertTrue(obj instanceof Serializable);
        assertTrue(obj instanceof Comparable<?>);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_serialization() throws IOException, ClassNotFoundException {
        assertSerializable(TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"tck"})
    public void test_serialization_format() throws ClassNotFoundException, IOException {
        LocalDate date = LocalDate.of(2012, 9, 16);
        LocalTime time = LocalTime.of(22, 17, 59, 459 * 1000000);
        assertEqualsSerialisedForm(LocalDateTime.of(date, time));
    }

    //-----------------------------------------------------------------------
    @Test(groups={"implementation"})
    public void constant_MIN_DATE_TIME() {
        check(LocalDateTime.MIN_DATE_TIME, Year.MIN_VALUE, 1, 1, 0, 0, 0, 0);
    }

    @Test(groups={"implementation"})
    public void constant_MAX_DATE_TIME() {
        check(LocalDateTime.MAX_DATE_TIME, Year.MAX_VALUE, 12, 31,  23, 59, 59, 999999999);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="sampleDates")
    Object[][] provider_sampleDates() {
        return new Object[][] {
            {2008, 7, 5},
            {2007, 7, 5},
            {2006, 7, 5},
            {2005, 7, 5},
            {2004, 1, 1},
            {-1, 1, 2},
        };
    }

    @DataProvider(name="sampleTimes")
    Object[][] provider_sampleTimes() {
        return new Object[][] {
            {0, 0, 0, 0},
            {0, 0, 0, 1},
            {0, 0, 1, 0},
            {0, 0, 1, 1},
            {0, 1, 0, 0},
            {0, 1, 0, 1},
            {0, 1, 1, 0},
            {0, 1, 1, 1},
            {1, 0, 0, 0},
            {1, 0, 0, 1},
            {1, 0, 1, 0},
            {1, 0, 1, 1},
            {1, 1, 0, 0},
            {1, 1, 0, 1},
            {1, 1, 1, 0},
            {1, 1, 1, 1},
        };
    }

    @Test(groups={"implementation"})
    public void test_withYear_int_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withYear(2007);
        assertSame(t.getDate(), TEST_2007_07_15_12_30_40_987654321.getDate());
        assertSame(t.getTime(), TEST_2007_07_15_12_30_40_987654321.getTime());
    }

    @Test(groups={"implementation"})
    public void test_withMonth_int_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withMonth(7);
        assertSame(t.getDate(), TEST_2007_07_15_12_30_40_987654321.getDate());
        assertSame(t.getTime(), TEST_2007_07_15_12_30_40_987654321.getTime());
    }

    @Test(groups={"implementation"})
    public void test_withDayOfMonth_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withDayOfMonth(15);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_withDayOfYear_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withDayOfYear(31 + 28 + 31 + 30 + 31 + 30 + 15);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_withHour_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withHour(12);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_withHour_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(1, 0)).withHour(0);
        assertSame(t.getTime(), LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_withHour_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(1, 0)).withHour(12);
        assertSame(t.getTime(), LocalTime.NOON);
    }

    @Test(groups={"implementation"})
    public void test_withMinute_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withMinute(30);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_withMinute_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(0, 1)).withMinute(0);
        assertSame(t.getTime(), LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_withMinute_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(12, 1)).withMinute(0);
        assertSame(t.getTime(), LocalTime.NOON);
    }

    @Test(groups={"implementation"})
    public void test_withSecond_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withSecond(40);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_withSecond_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(0, 0, 1)).withSecond(0);
        assertSame(t.getTime(), LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_withSecond_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(12, 0, 1)).withSecond(0);
        assertSame(t.getTime(), LocalTime.NOON);
    }

    @Test(groups={"implementation"})
    public void test_withNanoOfSecond_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withNano(987654321);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_withNanoOfSecond_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(0, 0, 0, 1)).withNano(0);
        assertSame(t.getTime(), LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_withNanoOfSecond_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(12, 0, 0, 1)).withNano(0);
        assertSame(t.getTime(), LocalTime.NOON);
    }

    @Test(groups={"implementation"})
    public void test_plus_adjuster_zero() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plus(Period.ZERO);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_plus_Period_zero() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plus(MockSimplePeriod.ZERO_DAYS);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_plus_longPeriodUnit_zero() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plus(0, ChronoUnit.DAYS);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_plusYears_int_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusYears(0);
        assertSame(TEST_2007_07_15_12_30_40_987654321, t);
    }

    @Test(groups={"implementation"})
    public void test_plusMonths_int_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusMonths(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_plusWeeks_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusWeeks(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_plusDays_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusDays(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_plusHours_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusHours(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_plusHours_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(23, 0)).plusHours(1);
        assertSame(t.getTime(), LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_plusHours_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(11, 0)).plusHours(1);
        assertSame(t.getTime(), LocalTime.NOON);
    }

    @Test(groups={"implementation"})
    public void test_plusMinutes_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusMinutes(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_plusMinutes_noChange_oneDay_same() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusMinutes(24 * 60);
        assertSame(t.getTime(), TEST_2007_07_15_12_30_40_987654321.getTime());
    }

    @Test(groups={"implementation"})
    public void test_plusMinutes_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(23, 59)).plusMinutes(1);
        assertSame(t.getTime(), LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_plusMinutes_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(11, 59)).plusMinutes(1);
        assertSame(t.getTime(), LocalTime.NOON);
    }

    @Test(groups={"implementation"})
    public void test_plusSeconds_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusSeconds(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_plusSeconds_noChange_oneDay_same() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusSeconds(24 * 60 * 60);
        assertSame(t.getTime(), TEST_2007_07_15_12_30_40_987654321.getTime());
    }

    @Test(groups={"implementation"})
    public void test_plusSeconds_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(23, 59, 59)).plusSeconds(1);
        assertSame(t.getTime(), LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_plusSeconds_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(11, 59, 59)).plusSeconds(1);
        assertSame(t.getTime(), LocalTime.NOON);
    }

    @Test(groups={"implementation"})
    public void test_plusNanos_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusNanos(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_plusNanos_noChange_oneDay_same() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusNanos(24 * 60 * 60 * 1000000000L);
        assertSame(t.getTime(), TEST_2007_07_15_12_30_40_987654321.getTime());
    }

    @Test(groups={"implementation"})
    public void test_plusNanos_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(23, 59, 59, 999999999)).plusNanos(1);
        assertSame(t.getTime(), LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_plusNanos_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(11, 59, 59, 999999999)).plusNanos(1);
        assertSame(t.getTime(), LocalTime.NOON);
    }

    @Test(groups={"implementation"})
    public void test_minus_adjuster_zero() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minus(Period.ZERO);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_minus_Period_zero() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minus(MockSimplePeriod.ZERO_DAYS);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_minus_longPeriodUnit_zero() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minus(0, ChronoUnit.DAYS);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_minusYears_int_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusYears(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_minusMonths_int_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusMonths(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_minusWeeks_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusWeeks(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_minusDays_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusDays(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_minusHours_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusHours(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_minusHours_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(1, 0)).minusHours(1);
        assertSame(t.getTime(), LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_minusHours_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(13, 0)).minusHours(1);
        assertSame(t.getTime(), LocalTime.NOON);
    }

    @Test(groups={"implementation"})
    public void test_minusMinutes_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusMinutes(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_minusMinutes_noChange_oneDay_same() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusMinutes(24 * 60);
        assertSame(t.getTime(), TEST_2007_07_15_12_30_40_987654321.getTime());
    }

    @Test(groups={"implementation"})
    public void test_minusMinutes_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(0, 1)).minusMinutes(1);
        assertSame(t.getTime(), LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_minusMinutes_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(12, 1)).minusMinutes(1);
        assertSame(t.getTime(), LocalTime.NOON);
    }

    @Test(groups={"implementation"})
    public void test_minusSeconds_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusSeconds(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_minusSeconds_noChange_oneDay() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusSeconds(24 * 60 * 60);
        assertEquals(t.getDate(), TEST_2007_07_15_12_30_40_987654321.getDate().minusDays(1));
        assertSame(t.getTime(), TEST_2007_07_15_12_30_40_987654321.getTime());
    }

    @Test(groups={"implementation"})
    public void test_minusSeconds_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(0, 0, 1)).minusSeconds(1);
        assertSame(t.getTime(), LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_minusSeconds_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(12, 0, 1)).minusSeconds(1);
        assertSame(t.getTime(), LocalTime.NOON);
    }

    @Test(groups={"implementation"})
    public void test_minusNanos_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusNanos(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_minusNanos_noChange_oneDay() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusNanos(24 * 60 * 60 * 1000000000L);
        assertEquals(t.getDate(), TEST_2007_07_15_12_30_40_987654321.getDate().minusDays(1));
        assertSame(t.getTime(), TEST_2007_07_15_12_30_40_987654321.getTime());
    }

    @Test(groups={"implementation"})
    public void test_minusNanos_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(0, 0, 0, 1)).minusNanos(1);
        assertSame(t.getTime(), LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_minusNanos_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(12, 0, 0, 1)).minusNanos(1);
        assertSame(t.getTime(), LocalTime.NOON);
    }

    //-----------------------------------------------------------------------
    // getDate()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates", groups={"implementation"})
    public void test_getDate(int year, int month, int day) {
        LocalDate d = LocalDate.of(year, month, day);
        LocalDateTime dt = LocalDateTime.of(d, LocalTime.MIDNIGHT);
        assertSame(dt.getDate(), d);
    }

    //-----------------------------------------------------------------------
    // getTime()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes", groups={"implementation"})
    public void test_getTime(int h, int m, int s, int ns) {
        LocalTime t = LocalTime.of(h, m, s, ns);
        LocalDateTime dt = LocalDateTime.of(LocalDate.of(2011, 7, 30), t);
        assertSame(dt.getTime(), t);
    }

    void test_comparisons_LocalDateTime(LocalDate... localDates) {
        test_comparisons_LocalDateTime(
            localDates,
            LocalTime.MIDNIGHT,
            LocalTime.of(0, 0, 0, 999999999),
            LocalTime.of(0, 0, 59, 0),
            LocalTime.of(0, 0, 59, 999999999),
            LocalTime.of(0, 59, 0, 0),
            LocalTime.of(0, 59, 59, 999999999),
            LocalTime.NOON,
            LocalTime.of(12, 0, 0, 999999999),
            LocalTime.of(12, 0, 59, 0),
            LocalTime.of(12, 0, 59, 999999999),
            LocalTime.of(12, 59, 0, 0),
            LocalTime.of(12, 59, 59, 999999999),
            LocalTime.of(23, 0, 0, 0),
            LocalTime.of(23, 0, 0, 999999999),
            LocalTime.of(23, 0, 59, 0),
            LocalTime.of(23, 0, 59, 999999999),
            LocalTime.of(23, 59, 0, 0),
            LocalTime.of(23, 59, 59, 999999999)
        );
    }

    void test_comparisons_LocalDateTime(LocalDate[] localDates, LocalTime... localTimes) {
        LocalDateTime[] localDateTimes = new LocalDateTime[localDates.length * localTimes.length];
        int i = 0;

        for (LocalDate localDate : localDates) {
            for (LocalTime localTime : localTimes) {
                localDateTimes[i++] = LocalDateTime.of(localDate, localTime);
            }
        }

        doTest_comparisons_LocalDateTime(localDateTimes);
    }

    void doTest_comparisons_LocalDateTime(LocalDateTime[] localDateTimes) {
        for (int i = 0; i < localDateTimes.length; i++) {
            LocalDateTime a = localDateTimes[i];
            for (int j = 0; j < localDateTimes.length; j++) {
                LocalDateTime b = localDateTimes[j];
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
