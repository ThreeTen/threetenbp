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

import static javax.time.calendrical.LocalDateTimeField.ALIGNED_DAY_OF_WEEK_IN_MONTH;
import static javax.time.calendrical.LocalDateTimeField.ALIGNED_DAY_OF_WEEK_IN_YEAR;
import static javax.time.calendrical.LocalDateTimeField.ALIGNED_WEEK_OF_MONTH;
import static javax.time.calendrical.LocalDateTimeField.ALIGNED_WEEK_OF_YEAR;
import static javax.time.calendrical.LocalDateTimeField.DAY_OF_MONTH;
import static javax.time.calendrical.LocalDateTimeField.DAY_OF_WEEK;
import static javax.time.calendrical.LocalDateTimeField.DAY_OF_YEAR;
import static javax.time.calendrical.LocalDateTimeField.EPOCH_DAY;
import static javax.time.calendrical.LocalDateTimeField.EPOCH_MONTH;
import static javax.time.calendrical.LocalDateTimeField.ERA;
import static javax.time.calendrical.LocalDateTimeField.MONTH_OF_YEAR;
import static javax.time.calendrical.LocalDateTimeField.WEEK_BASED_YEAR;
import static javax.time.calendrical.LocalDateTimeField.WEEK_OF_MONTH;
import static javax.time.calendrical.LocalDateTimeField.WEEK_OF_WEEK_BASED_YEAR;
import static javax.time.calendrical.LocalDateTimeField.WEEK_OF_YEAR;
import static javax.time.calendrical.LocalDateTimeField.YEAR;
import static javax.time.calendrical.LocalDateTimeField.YEAR_OF_ERA;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
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
import javax.time.calendrical.PeriodUnit;
import javax.time.format.CalendricalFormatter;
import javax.time.format.DateTimeParseException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test LocalDate.
 */
@Test
public class TestLocalDate extends AbstractDateTimeTest {

    private static final ZoneOffset OFFSET_PONE = ZoneOffset.ofHours(1);
    private static final ZoneOffset OFFSET_PTWO = ZoneOffset.ofHours(2);
    private static final ZoneId ZONE_PARIS = ZoneId.of("Europe/Paris");
    private static final ZoneId ZONE_GAZA = ZoneId.of("Asia/Gaza");

    private LocalDate TEST_2007_07_15;
    private long MAX_VALID_EPOCHDAYS;
    private long MIN_VALID_EPOCHDAYS;
    private LocalDate MAX_DATE;
    private LocalDate MIN_DATE;
    private Instant MAX_INSTANT;
    private Instant MIN_INSTANT;

    @BeforeMethod(groups={"tck", "implementation"})
    public void setUp() {
        TEST_2007_07_15 = LocalDate.of(2007, 7, 15);

        LocalDate max = LocalDate.MAX_DATE;
        LocalDate min = LocalDate.MIN_DATE;
        MAX_VALID_EPOCHDAYS = max.getLong(LocalDateTimeField.EPOCH_DAY);
        MIN_VALID_EPOCHDAYS = min.getLong(LocalDateTimeField.EPOCH_DAY);
        MAX_DATE = max;
        MIN_DATE = min;
        MAX_INSTANT = max.atOffset(ZoneOffset.UTC).atTime(LocalTime.MIDNIGHT).toInstant();
        MIN_INSTANT = min.atOffset(ZoneOffset.UTC).atTime(LocalTime.MIDNIGHT).toInstant();
    }

    //-----------------------------------------------------------------------
    @Override
    protected List<DateTimeAccessor> samples() {
        DateTimeAccessor[] array = {TEST_2007_07_15, LocalDate.MAX_DATE, LocalDate.MIN_DATE, };
        return Arrays.asList(array);
    }

    @Override
    protected List<DateTimeField> validFields() {
        DateTimeField[] array = {
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
        Object obj = TEST_2007_07_15;
        assertTrue(obj instanceof DateTimeAccessor);
        assertTrue(obj instanceof Serializable);
        assertTrue(obj instanceof Comparable<?>);
    }

    //-----------------------------------------------------------------------
    private void check(LocalDate test_2008_02_29, int y, int m, int d) {
        assertEquals(test_2008_02_29.getYear(), y);
        assertEquals(test_2008_02_29.getMonth().getValue(), m);
        assertEquals(test_2008_02_29.getDayOfMonth(), d);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"implementation"})
    public void constant_MIN_DATE_TIME() {
        check(LocalDate.MIN_DATE, Year.MIN_YEAR, 1, 1);
    }

    @Test(groups={"implementation"})
    public void constant_MAX_DATE_TIME() {
        check(LocalDate.MAX_DATE, Year.MAX_YEAR, 12, 31);
    }

    //-----------------------------------------------------------------------
    // Since plusDays/minusDays actually depends on MJDays, it cannot be used for testing
    private LocalDate next(LocalDate date) {
        int newDayOfMonth = date.getDayOfMonth() + 1;
        if (newDayOfMonth <= date.getMonth().length(isIsoLeap(date.getYear()))) {
            return date.withDayOfMonth(newDayOfMonth);
        }
        date = date.withDayOfMonth(1);
        if (date.getMonth() == Month.DECEMBER) {
            date = date.withYear(date.getYear() + 1);
        }
        return date.with(date.getMonth().plus(1));
    }

    private LocalDate previous(LocalDate date) {
        int newDayOfMonth = date.getDayOfMonth() - 1;
        if (newDayOfMonth > 0) {
            return date.withDayOfMonth(newDayOfMonth);
        }
        date = date.with(date.getMonth().minus(1));
        if (date.getMonth() == Month.DECEMBER) {
            date = date.withYear(date.getYear() - 1);
        }
        return date.withDayOfMonth(date.getMonth().length(isIsoLeap(date.getYear())));
    }

    @Test(groups={"implementation"})
    public void test_with_DateTimeField_long_noChange_same() {
        LocalDate t = TEST_2007_07_15.with(YEAR, 2007);
        assertSame(t, TEST_2007_07_15);
    }

    @Test(groups={"implementation"})
    public void test_withYear_int_noChange_same() {
        LocalDate t = TEST_2007_07_15.withYear(2007);
        assertSame(t, TEST_2007_07_15);
    }

    @Test(groups={"implementation"})
    public void test_withMonth_int_noChange_same() {
        LocalDate t = TEST_2007_07_15.withMonth(7);
        assertSame(t, TEST_2007_07_15);
    }

    @Test(groups={"implementation"})
    public void test_withDayOfMonth_noChange_same() {
        LocalDate t = TEST_2007_07_15.withDayOfMonth(15);
        assertSame(t, TEST_2007_07_15);
    }

    @Test(groups={"implementation"})
    public void test_withDayOfYear_noChange_same() {
        LocalDate t = TEST_2007_07_15.withDayOfYear(31 + 28 + 31 + 30 + 31 + 30 + 15);
        assertSame(t, TEST_2007_07_15);
    }

    @Test(groups={"implementation"})
    public void test_plus_Period_zero() {
        LocalDate t = TEST_2007_07_15.plus(MockSimplePeriod.ZERO_DAYS);
        assertSame(t, TEST_2007_07_15);
    }

    @Test(groups={"implementation"})
    public void test_plus_longPeriodUnit_zero() {
        LocalDate t = TEST_2007_07_15.plus(0, LocalPeriodUnit.DAYS);
        assertSame(t, TEST_2007_07_15);
    }

    @Test(groups={"implementation"})
    public void test_plusYears_long_noChange_same() {
        LocalDate t = TEST_2007_07_15.plusYears(0);
        assertSame(t, TEST_2007_07_15);
    }

    @Test(groups={"implementation"})
    public void test_plusMonths_long_noChange_same() {
        LocalDate t = TEST_2007_07_15.plusMonths(0);
        assertSame(t, TEST_2007_07_15);
    }

    //-----------------------------------------------------------------------
    // plusWeeks()
    //-----------------------------------------------------------------------
    @DataProvider(name="samplePlusWeeksSymmetry")
    Object[][] provider_samplePlusWeeksSymmetry() {
        return new Object[][] {
            {LocalDate.of(-1, 1, 1)},
            {LocalDate.of(-1, 2, 28)},
            {LocalDate.of(-1, 3, 1)},
            {LocalDate.of(-1, 12, 31)},
            {LocalDate.of(0, 1, 1)},
            {LocalDate.of(0, 2, 28)},
            {LocalDate.of(0, 2, 29)},
            {LocalDate.of(0, 3, 1)},
            {LocalDate.of(0, 12, 31)},
            {LocalDate.of(2007, 1, 1)},
            {LocalDate.of(2007, 2, 28)},
            {LocalDate.of(2007, 3, 1)},
            {LocalDate.of(2007, 12, 31)},
            {LocalDate.of(2008, 1, 1)},
            {LocalDate.of(2008, 2, 28)},
            {LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 3, 1)},
            {LocalDate.of(2008, 12, 31)},
            {LocalDate.of(2099, 1, 1)},
            {LocalDate.of(2099, 2, 28)},
            {LocalDate.of(2099, 3, 1)},
            {LocalDate.of(2099, 12, 31)},
            {LocalDate.of(2100, 1, 1)},
            {LocalDate.of(2100, 2, 28)},
            {LocalDate.of(2100, 3, 1)},
            {LocalDate.of(2100, 12, 31)},
        };
    }

    @Test(dataProvider="samplePlusWeeksSymmetry", groups={"implementation"})
    public void test_plusWeeks_symmetry(LocalDate reference) {
        for (int weeks = 0; weeks < 365 * 8; weeks++) {
            LocalDate t = reference.plusWeeks(weeks).plusWeeks(-weeks);
            assertEquals(t, reference);

            t = reference.plusWeeks(-weeks).plusWeeks(weeks);
            assertEquals(t, reference);
        }
    }

    @Test(groups={"implementation"})
    public void test_plusWeeks_noChange_same() {
        LocalDate t = TEST_2007_07_15.plusWeeks(0);
        assertSame(t, TEST_2007_07_15);
    }

    //-----------------------------------------------------------------------
    // plusDays()
    //-----------------------------------------------------------------------
    @DataProvider(name="samplePlusDaysSymmetry")
    Object[][] provider_samplePlusDaysSymmetry() {
        return new Object[][] {
            {LocalDate.of(-1, 1, 1)},
            {LocalDate.of(-1, 2, 28)},
            {LocalDate.of(-1, 3, 1)},
            {LocalDate.of(-1, 12, 31)},
            {LocalDate.of(0, 1, 1)},
            {LocalDate.of(0, 2, 28)},
            {LocalDate.of(0, 2, 29)},
            {LocalDate.of(0, 3, 1)},
            {LocalDate.of(0, 12, 31)},
            {LocalDate.of(2007, 1, 1)},
            {LocalDate.of(2007, 2, 28)},
            {LocalDate.of(2007, 3, 1)},
            {LocalDate.of(2007, 12, 31)},
            {LocalDate.of(2008, 1, 1)},
            {LocalDate.of(2008, 2, 28)},
            {LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 3, 1)},
            {LocalDate.of(2008, 12, 31)},
            {LocalDate.of(2099, 1, 1)},
            {LocalDate.of(2099, 2, 28)},
            {LocalDate.of(2099, 3, 1)},
            {LocalDate.of(2099, 12, 31)},
            {LocalDate.of(2100, 1, 1)},
            {LocalDate.of(2100, 2, 28)},
            {LocalDate.of(2100, 3, 1)},
            {LocalDate.of(2100, 12, 31)},
        };
    }

    @Test(dataProvider="samplePlusDaysSymmetry", groups={"implementation"})
    public void test_plusDays_symmetry(LocalDate reference) {
        for (int days = 0; days < 365 * 8; days++) {
            LocalDate t = reference.plusDays(days).plusDays(-days);
            assertEquals(t, reference);

            t = reference.plusDays(-days).plusDays(days);
            assertEquals(t, reference);
        }
    }

    @Test(groups={"implementation"})
    public void test_plusDays_noChange_same() {
        LocalDate t = TEST_2007_07_15.plusDays(0);
        assertSame(t, TEST_2007_07_15);
    }

    @Test(groups={"implementation"})
    public void test_minus_Period_zero() {
        LocalDate t = TEST_2007_07_15.minus(MockSimplePeriod.ZERO_DAYS);
        assertSame(t, TEST_2007_07_15);
    }

    @Test(groups={"implementation"})
    public void test_minus_longPeriodUnit_zero() {
        LocalDate t = TEST_2007_07_15.minus(0, LocalPeriodUnit.DAYS);
        assertSame(t, TEST_2007_07_15);
    }

    @Test(groups={"implementation"})
    public void test_minusYears_long_noChange_same() {
        LocalDate t = TEST_2007_07_15.minusYears(0);
        assertSame(t, TEST_2007_07_15);
    }

    @Test(groups={"implementation"})
    public void test_minusMonths_long_noChange_same() {
        LocalDate t = TEST_2007_07_15.minusMonths(0);
        assertSame(t, TEST_2007_07_15);
    }

    //-----------------------------------------------------------------------
    // minusWeeks()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleMinusWeeksSymmetry")
    Object[][] provider_sampleMinusWeeksSymmetry() {
        return new Object[][] {
            {LocalDate.of(-1, 1, 1)},
            {LocalDate.of(-1, 2, 28)},
            {LocalDate.of(-1, 3, 1)},
            {LocalDate.of(-1, 12, 31)},
            {LocalDate.of(0, 1, 1)},
            {LocalDate.of(0, 2, 28)},
            {LocalDate.of(0, 2, 29)},
            {LocalDate.of(0, 3, 1)},
            {LocalDate.of(0, 12, 31)},
            {LocalDate.of(2007, 1, 1)},
            {LocalDate.of(2007, 2, 28)},
            {LocalDate.of(2007, 3, 1)},
            {LocalDate.of(2007, 12, 31)},
            {LocalDate.of(2008, 1, 1)},
            {LocalDate.of(2008, 2, 28)},
            {LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 3, 1)},
            {LocalDate.of(2008, 12, 31)},
            {LocalDate.of(2099, 1, 1)},
            {LocalDate.of(2099, 2, 28)},
            {LocalDate.of(2099, 3, 1)},
            {LocalDate.of(2099, 12, 31)},
            {LocalDate.of(2100, 1, 1)},
            {LocalDate.of(2100, 2, 28)},
            {LocalDate.of(2100, 3, 1)},
            {LocalDate.of(2100, 12, 31)},
        };
    }

    @Test(dataProvider="sampleMinusWeeksSymmetry", groups={"implementation"})
    public void test_minusWeeks_symmetry(LocalDate reference) {
        for (int weeks = 0; weeks < 365 * 8; weeks++) {
            LocalDate t = reference.minusWeeks(weeks).minusWeeks(-weeks);
            assertEquals(t, reference);

            t = reference.minusWeeks(-weeks).minusWeeks(weeks);
            assertEquals(t, reference);
        }
    }

    @Test(groups={"implementation"})
    public void test_minusWeeks_noChange_same() {
        LocalDate t = TEST_2007_07_15.minusWeeks(0);
        assertSame(t, TEST_2007_07_15);
    }

    //-----------------------------------------------------------------------
    // minusDays()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleMinusDaysSymmetry")
    Object[][] provider_sampleMinusDaysSymmetry() {
        return new Object[][] {
            {LocalDate.of(-1, 1, 1)},
            {LocalDate.of(-1, 2, 28)},
            {LocalDate.of(-1, 3, 1)},
            {LocalDate.of(-1, 12, 31)},
            {LocalDate.of(0, 1, 1)},
            {LocalDate.of(0, 2, 28)},
            {LocalDate.of(0, 2, 29)},
            {LocalDate.of(0, 3, 1)},
            {LocalDate.of(0, 12, 31)},
            {LocalDate.of(2007, 1, 1)},
            {LocalDate.of(2007, 2, 28)},
            {LocalDate.of(2007, 3, 1)},
            {LocalDate.of(2007, 12, 31)},
            {LocalDate.of(2008, 1, 1)},
            {LocalDate.of(2008, 2, 28)},
            {LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 3, 1)},
            {LocalDate.of(2008, 12, 31)},
            {LocalDate.of(2099, 1, 1)},
            {LocalDate.of(2099, 2, 28)},
            {LocalDate.of(2099, 3, 1)},
            {LocalDate.of(2099, 12, 31)},
            {LocalDate.of(2100, 1, 1)},
            {LocalDate.of(2100, 2, 28)},
            {LocalDate.of(2100, 3, 1)},
            {LocalDate.of(2100, 12, 31)},
        };
    }

    @Test(dataProvider="sampleMinusDaysSymmetry", groups={"implementation"})
    public void test_minusDays_symmetry(LocalDate reference) {
        for (int days = 0; days < 365 * 8; days++) {
            LocalDate t = reference.minusDays(days).minusDays(-days);
            assertEquals(t, reference);

            t = reference.minusDays(-days).minusDays(days);
            assertEquals(t, reference);
        }
    }

    @Test(groups={"implementation"})
    public void test_minusDays_noChange_same() {
        LocalDate t = TEST_2007_07_15.minusDays(0);
        assertSame(t, TEST_2007_07_15);
    }

    @Test(groups={"implementation"})
    public void test_toEpochDay_fromMJDays_symmetry() {
        long date_0000_01_01 = -678941 - 40587;

        LocalDate test = LocalDate.of(0, 1, 1);
        for (long i = date_0000_01_01; i < 700000; i++) {
            assertEquals(LocalDate.ofEpochDay(test.getLong(LocalDateTimeField.EPOCH_DAY)), test);
            test = next(test);
        }
        test = LocalDate.of(0, 1, 1);
        for (long i = date_0000_01_01; i > -2000000; i--) {
            assertEquals(LocalDate.ofEpochDay(test.getLong(LocalDateTimeField.EPOCH_DAY)), test);
            test = previous(test);
        }
    }

    void doTest_comparisons_LocalDate(LocalDate... localDates) {
        for (int i = 0; i < localDates.length; i++) {
            LocalDate a = localDates[i];
            for (int j = 0; j < localDates.length; j++) {
                LocalDate b = localDates[j];
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

    @Test(groups={"tck", "implementation"})
    public void test_equals_itself_true() {
        assertEquals(TEST_2007_07_15.equals(TEST_2007_07_15), true);
    }

}
