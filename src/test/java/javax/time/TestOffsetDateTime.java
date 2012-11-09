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
package javax.time;

import static javax.time.Month.DECEMBER;
import static javax.time.calendrical.LocalDateTimeField.ALIGNED_DAY_OF_WEEK_IN_MONTH;
import static javax.time.calendrical.LocalDateTimeField.ALIGNED_DAY_OF_WEEK_IN_YEAR;
import static javax.time.calendrical.LocalDateTimeField.ALIGNED_WEEK_OF_MONTH;
import static javax.time.calendrical.LocalDateTimeField.ALIGNED_WEEK_OF_YEAR;
import static javax.time.calendrical.LocalDateTimeField.AMPM_OF_DAY;
import static javax.time.calendrical.LocalDateTimeField.CLOCK_HOUR_OF_AMPM;
import static javax.time.calendrical.LocalDateTimeField.CLOCK_HOUR_OF_DAY;
import static javax.time.calendrical.LocalDateTimeField.DAY_OF_MONTH;
import static javax.time.calendrical.LocalDateTimeField.DAY_OF_WEEK;
import static javax.time.calendrical.LocalDateTimeField.DAY_OF_YEAR;
import static javax.time.calendrical.LocalDateTimeField.EPOCH_DAY;
import static javax.time.calendrical.LocalDateTimeField.EPOCH_MONTH;
import static javax.time.calendrical.LocalDateTimeField.ERA;
import static javax.time.calendrical.LocalDateTimeField.HOUR_OF_AMPM;
import static javax.time.calendrical.LocalDateTimeField.HOUR_OF_DAY;
import static javax.time.calendrical.LocalDateTimeField.INSTANT_SECONDS;
import static javax.time.calendrical.LocalDateTimeField.MICRO_OF_DAY;
import static javax.time.calendrical.LocalDateTimeField.MICRO_OF_SECOND;
import static javax.time.calendrical.LocalDateTimeField.MILLI_OF_DAY;
import static javax.time.calendrical.LocalDateTimeField.MILLI_OF_SECOND;
import static javax.time.calendrical.LocalDateTimeField.MINUTE_OF_DAY;
import static javax.time.calendrical.LocalDateTimeField.MINUTE_OF_HOUR;
import static javax.time.calendrical.LocalDateTimeField.MONTH_OF_YEAR;
import static javax.time.calendrical.LocalDateTimeField.NANO_OF_DAY;
import static javax.time.calendrical.LocalDateTimeField.NANO_OF_SECOND;
import static javax.time.calendrical.LocalDateTimeField.OFFSET_SECONDS;
import static javax.time.calendrical.LocalDateTimeField.SECOND_OF_DAY;
import static javax.time.calendrical.LocalDateTimeField.SECOND_OF_MINUTE;
import static javax.time.calendrical.LocalDateTimeField.WEEK_BASED_YEAR;
import static javax.time.calendrical.LocalDateTimeField.WEEK_OF_MONTH;
import static javax.time.calendrical.LocalDateTimeField.WEEK_OF_WEEK_BASED_YEAR;
import static javax.time.calendrical.LocalDateTimeField.WEEK_OF_YEAR;
import static javax.time.calendrical.LocalDateTimeField.YEAR;
import static javax.time.calendrical.LocalDateTimeField.YEAR_OF_ERA;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTime.WithAdjuster;
import javax.time.calendrical.DateTimeAccessor;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.JulianDayField;
import javax.time.calendrical.LocalDateTimeField;
import javax.time.calendrical.LocalPeriodUnit;
import javax.time.calendrical.MockFieldNoValue;
import javax.time.calendrical.MockZoneResolverReturnsNull;
import javax.time.format.CalendricalFormatter;
import javax.time.format.DateTimeParseException;
import javax.time.zone.ZoneResolver;
import javax.time.zone.ZoneResolvers;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test OffsetDateTime.
 */
@Test
public class TestOffsetDateTime extends AbstractDateTimeTest {

    private static final ZoneId ZONE_PARIS = ZoneId.of("Europe/Paris");
    private static final ZoneId ZONE_GAZA = ZoneId.of("Asia/Gaza");
    private static final ZoneOffset OFFSET_PONE = ZoneOffset.ofHours(1);
    private static final ZoneOffset OFFSET_PTWO = ZoneOffset.ofHours(2);
    private static final ZoneOffset OFFSET_MONE = ZoneOffset.ofHours(-1);
    private static final ZoneOffset OFFSET_MTWO = ZoneOffset.ofHours(-2);
    private OffsetDateTime TEST_2008_6_30_11_30_59_000000500;

    @BeforeMethod(groups={"tck","implementation"})
    public void setUp() {
        TEST_2008_6_30_11_30_59_000000500 = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, 500, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    @Override
    protected List<DateTimeAccessor> samples() {
        DateTimeAccessor[] array = {TEST_2008_6_30_11_30_59_000000500, };
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
            DAY_OF_WEEK,
            ALIGNED_DAY_OF_WEEK_IN_MONTH,
            ALIGNED_DAY_OF_WEEK_IN_YEAR,
            DAY_OF_MONTH,
            DAY_OF_YEAR,
            EPOCH_DAY,
            ALIGNED_WEEK_OF_MONTH,
            WEEK_OF_MONTH,
            WEEK_OF_WEEK_BASED_YEAR,
            ALIGNED_WEEK_OF_YEAR,
            WEEK_OF_YEAR,
            MONTH_OF_YEAR,
            EPOCH_MONTH,
            WEEK_BASED_YEAR,
            YEAR_OF_ERA,
            YEAR,
            ERA,
            OFFSET_SECONDS,
            INSTANT_SECONDS,
            JulianDayField.JULIAN_DAY,
            JulianDayField.MODIFIED_JULIAN_DAY,
            JulianDayField.RATA_DIE,
        };
        return Arrays.asList(array);
    }

    @Override
    protected List<DateTimeField> invalidFields() {
        List<DateTimeField> list = new ArrayList<>(Arrays.<DateTimeField>asList(LocalDateTimeField.values()));
        list.removeAll(validFields());
        return list;
    }

    //-----------------------------------------------------------------------
    @Test(groups={"implementation"})
    public void test_interfaces() {
        Object obj = TEST_2008_6_30_11_30_59_000000500;
        assertTrue(obj instanceof DateTimeAccessor);
        assertTrue(obj instanceof Serializable);
        assertTrue(obj instanceof Comparable<?>);
    }

    //-----------------------------------------------------------------------
    private void check(OffsetDateTime test, int y, int mo, int d, int h, int m, int s, int n, ZoneOffset offset) {
        assertEquals(test.getYear(), y);
        assertEquals(test.getMonth().getValue(), mo);
        assertEquals(test.getDayOfMonth(), d);
        assertEquals(test.getHour(), h);
        assertEquals(test.getMinute(), m);
        assertEquals(test.getSecond(), s);
        assertEquals(test.getNano(), n);
        assertEquals(test.getOffset(), offset);
    }

    //-----------------------------------------------------------------------
    // basics
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleTimes")
    Object[][] provider_sampleTimes() {
        return new Object[][] {
            {2008, 6, 30, 11, 30, 20, 500, OFFSET_PONE},
            {2008, 6, 30, 11, 0, 0, 0, OFFSET_PONE},
            {2008, 6, 30, 23, 59, 59, 999999999, OFFSET_PONE},
            {-1, 1, 1, 0, 0, 0, 0, OFFSET_PONE},
        };
    }

    @Test(dataProvider="sampleTimes", groups={"implementation"})
    public void test_get_same(int y, int o, int d, int h, int m, int s, int n, ZoneOffset offset) {
        LocalDate localDate = LocalDate.of(y, o, d);
        LocalTime localTime = LocalTime.of(h, m, s, n);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        OffsetDateTime a = OffsetDateTime.of(localDateTime, offset);

        assertSame(a.getOffset(), offset);
        assertSame(a.getDate(), localDate);
        assertSame(a.getTime(), localTime);
        assertSame(a.getDateTime(), localDateTime);
    }

    //-----------------------------------------------------------------------
    // withOffsetSameLocal()
    //-----------------------------------------------------------------------
    @Test(groups={"implementation"})
    public void test_withOffsetSameLocal() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withOffsetSameLocal(OFFSET_PTWO);
        assertSame(test.getDateTime(), base.getDateTime());
        assertSame(test.getOffset(), OFFSET_PTWO);
    }

    @Test(groups={"implementation"})
    public void test_withOffsetSameLocal_noChange() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withOffsetSameLocal(OFFSET_PONE);
        assertSame(test, base);
    }

    @Test(groups={"implementation"})
    public void test_withOffsetSameInstant_noChange() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withOffsetSameInstant(OFFSET_PONE);
        assertSame(test, base);
    }

    @Test(groups={"implementation"})
    public void test_withYear_noChange() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withYear(2008);
        assertSame(test, base);
    }

    @Test(groups={"implementation"})
    public void test_withMonth_noChange() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withMonth(6);
        assertSame(test, base);
    }

    @Test(groups={"implementation"})
    public void test_withDayOfMonth_noChange() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withDayOfMonth(30);
        assertSame(test, base);
    }

    @Test(groups={"implementation"})
    public void test_withDayOfYear_noChange() {
        OffsetDateTime t = TEST_2008_6_30_11_30_59_000000500.withDayOfYear(31 + 29 + 31 + 30 + 31 + 30);
        assertSame(t, TEST_2008_6_30_11_30_59_000000500);
    }

    @Test(groups={"implementation"})
    public void test_withDate_noChange() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, OFFSET_PONE);
        OffsetDateTime test = base.withDate(2008, 6, 30);
        assertSame(test, base);
    }

    @Test(groups={"implementation"})
    public void test_withHour_noChange() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withHour(11);
        assertSame(test, base);
    }

    @Test(groups={"implementation"})
    public void test_withMinute_noChange() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withMinute(30);
        assertSame(test, base);
    }

    @Test(groups={"implementation"})
    public void test_withSecond_noChange() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withSecond(59);
        assertSame(test, base);
    }

    @Test(groups={"implementation"})
    public void test_withNanoOfSecond_noChange() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, 1, OFFSET_PONE);
        OffsetDateTime test = base.withNano(1);
        assertSame(test, base);
    }

    @Test(groups={"implementation"})
    public void test_withTime_HM_noChange() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, OFFSET_PONE);
        OffsetDateTime test = base.withTime(11, 30);
        assertSame(test, base);
    }

    @Test(groups={"implementation"})
    public void test_withTime_HMS_noChange() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withTime(11, 30, 59);
        assertSame(test, base);
    }

    @Test(groups={"implementation"})
    public void test_withTime_HMSN_noChange() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, 500, OFFSET_PONE);
        OffsetDateTime test = base.withTime(11, 30, 59, 500);
        assertSame(test, base);
    }

    @Test(groups={"implementation"})
    public void test_plus_Period_zero() {
        OffsetDateTime t = TEST_2008_6_30_11_30_59_000000500.plus(MockSimplePeriod.ZERO_DAYS);
        assertSame(t, TEST_2008_6_30_11_30_59_000000500);
    }

    @Test(groups={"implementation"})
    public void test_plusYears_zero() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusYears(0);
        assertSame(test, base);
    }

    @Test(groups={"implementation"})
    public void test_plusMonths_zero() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusMonths(0);
        assertSame(test, base);
    }

    @Test(groups={"implementation"})
    public void test_plusWeeks_zero() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusWeeks(0);
        assertSame(test, base);
    }

    @Test(groups={"implementation"})
    public void test_plusDays_zero() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusDays(0);
        assertSame(test, base);
    }

    @Test(groups={"implementation"})
    public void test_plusHours_zero() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusHours(0);
        assertSame(test, base);
    }

    @Test(groups={"implementation"})
    public void test_plusMinutes_zero() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusMinutes(0);
        assertSame(test, base);
    }

    @Test(groups={"implementation"})
    public void test_plusSeconds_zero() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusSeconds(0);
        assertSame(test, base);
    }

    @Test(groups={"implementation"})
    public void test_plusNanos_zero() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusNanos(0);
        assertSame(test, base);
    }

    @Test(groups={"implementation"})
    public void test_minus_Period_zero() {
        OffsetDateTime t = TEST_2008_6_30_11_30_59_000000500.minus(MockSimplePeriod.ZERO_DAYS);
        assertSame(t, TEST_2008_6_30_11_30_59_000000500);
    }

    @Test(groups={"implementation"})
    public void test_minusYears_zero() {
        OffsetDateTime base = OffsetDateTime.of(2007, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusYears(0);
        assertSame(test, base);
    }

    @Test(groups={"implementation"})
    public void test_minusMonths_zero() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusMonths(0);
        assertSame(test, base);
    }

    @Test(groups={"implementation"})
    public void test_minusWeeks_zero() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusWeeks(0);
        assertSame(test, base);
    }

    @Test(groups={"implementation"})
    public void test_minusDays_zero() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusDays(0);
        assertSame(test, base);
    }

    @Test(groups={"implementation"})
    public void test_minusHours_zero() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusHours(0);
        assertSame(test, base);
    }

    @Test(groups={"implementation"})
    public void test_minusMinutes_zero() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusMinutes(0);
        assertSame(test, base);
    }

    @Test(groups={"implementation"})
    public void test_minusSeconds_zero() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusSeconds(0);
        assertSame(test, base);
    }

    @Test(groups={"implementation"})
    public void test_minusNanos_zero() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusNanos(0);
        assertSame(test, base);
    }

}
