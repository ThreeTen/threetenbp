/*
9 * Copyright (c) 2007-2013, Stephen Colebourne & Michael Nascimento Santos
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
import static org.testng.Assert.assertNotNull;
import static org.threeten.bp.Month.DECEMBER;
import static org.threeten.bp.temporal.ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH;
import static org.threeten.bp.temporal.ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR;
import static org.threeten.bp.temporal.ChronoField.ALIGNED_WEEK_OF_MONTH;
import static org.threeten.bp.temporal.ChronoField.ALIGNED_WEEK_OF_YEAR;
import static org.threeten.bp.temporal.ChronoField.DAY_OF_MONTH;
import static org.threeten.bp.temporal.ChronoField.DAY_OF_WEEK;
import static org.threeten.bp.temporal.ChronoField.DAY_OF_YEAR;
import static org.threeten.bp.temporal.ChronoField.EPOCH_DAY;
import static org.threeten.bp.temporal.ChronoField.EPOCH_MONTH;
import static org.threeten.bp.temporal.ChronoField.ERA;
import static org.threeten.bp.temporal.ChronoField.MONTH_OF_YEAR;
import static org.threeten.bp.temporal.ChronoField.OFFSET_SECONDS;
import static org.threeten.bp.temporal.ChronoField.YEAR;
import static org.threeten.bp.temporal.ChronoField.YEAR_OF_ERA;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeFormatters;
import org.threeten.bp.format.DateTimeParseException;
import org.threeten.bp.temporal.ChronoField;
import org.threeten.bp.temporal.ChronoUnit;
import org.threeten.bp.temporal.ISOChrono;
import org.threeten.bp.temporal.JulianFields;
import org.threeten.bp.temporal.Temporal;
import org.threeten.bp.temporal.TemporalAccessor;
import org.threeten.bp.temporal.TemporalAdder;
import org.threeten.bp.temporal.TemporalAdjuster;
import org.threeten.bp.temporal.TemporalField;
import org.threeten.bp.temporal.TemporalQueries;
import org.threeten.bp.temporal.TemporalSubtractor;

/**
 * Test OffsetDate.
 */
@Test
public class TestOffsetDate extends AbstractDateTimeTest {
    private static final ZoneOffset OFFSET_PONE = ZoneOffset.ofHours(1);
    private static final ZoneOffset OFFSET_PTWO = ZoneOffset.ofHours(2);

    private OffsetDate TEST_2007_07_15_PONE;

    @BeforeMethod
    public void setUp() {
        TEST_2007_07_15_PONE = OffsetDate.of(LocalDate.of(2007, 7, 15), OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    @Override
    protected List<TemporalAccessor> samples() {
        TemporalAccessor[] array = {TEST_2007_07_15_PONE, OffsetDate.MIN, OffsetDate.MAX};
        return Arrays.asList(array);
    }

    @Override
    protected List<TemporalField> validFields() {
        TemporalField[] array = {
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
            OFFSET_SECONDS,
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
    @Test
    public void test_serialization() throws ClassNotFoundException, IOException {
        assertSerializable(TEST_2007_07_15_PONE);
        assertSerializable(OffsetDate.MIN);
        assertSerializable(OffsetDate.MAX);
    }

    @Test
    public void test_serialization_format() throws ClassNotFoundException, IOException {
        assertEqualsSerialisedForm(OffsetDate.of(LocalDate.of(2012, 9, 16), ZoneOffset.of("+01:00")));
    }

    //-----------------------------------------------------------------------
    // constants
    //-----------------------------------------------------------------------
    @Test
    public void constant_MIN() {
        check(OffsetDate.MIN, Year.MIN_VALUE, 1, 1, ZoneOffset.MAX);
    }

    @Test
    public void constant_MAX() {
        check(OffsetDate.MAX, Year.MAX_VALUE, 12, 31, ZoneOffset.MIN);
    }

    //-----------------------------------------------------------------------
    // now()
    //-----------------------------------------------------------------------
    @Test
    public void now() {
        OffsetDate expected = OffsetDate.now(Clock.systemDefaultZone());
        OffsetDate test = OffsetDate.now();
        for (int i = 0; i < 100; i++) {
            if (expected.equals(test)) {
                return;
            }
            expected = OffsetDate.now(Clock.systemDefaultZone());
            test = OffsetDate.now();
        }
        assertEquals(test, expected);
    }

    @Test
    public void now_Clock_allSecsInDay_utc() {
        for (int i = 0; i < (2 * 24 * 60 * 60); i++) {
            Instant instant = Instant.ofEpochSecond(i);
            Clock clock = Clock.fixed(instant, ZoneOffset.UTC);
            OffsetDate test = OffsetDate.now(clock);
            check(test, 1970, 1, (i < 24 * 60 * 60 ? 1 : 2), ZoneOffset.UTC);
        }
    }

    @Test
    public void now_Clock_allSecsInDay_beforeEpoch() {
        for (int i =-1; i >= -(2 * 24 * 60 * 60); i--) {
            Instant instant = Instant.ofEpochSecond(i);
            Clock clock = Clock.fixed(instant, ZoneOffset.UTC);
            OffsetDate test = OffsetDate.now(clock);
            check(test, 1969, 12, (i >= -24 * 60 * 60 ? 31 : 30), ZoneOffset.UTC);
        }
    }

    @Test
    public void now_Clock_offsets() {
        Instant base = LocalDateTime.of(1970, 1, 1, 12, 0).toInstant(ZoneOffset.UTC);
        for (int i = -9; i < 15; i++) {
            ZoneOffset offset = ZoneOffset.ofHours(i);
            Clock clock = Clock.fixed(base, offset);
            OffsetDate test = OffsetDate.now(clock);
            check(test, 1970, 1, (i >= 12 ? 2 : 1), offset);
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void now_Clock_nullZoneId() {
        OffsetDate.now((ZoneId) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void now_Clock_nullClock() {
        OffsetDate.now((Clock) null);
    }

    //-----------------------------------------------------------------------
    // factories
    //-----------------------------------------------------------------------
    private void check(OffsetDate test, int y, int mo, int d, ZoneOffset offset) {
        assertEquals(test.getDate(), LocalDate.of(y, mo, d));
        assertEquals(test.getOffset(), offset);

        assertEquals(test.getYear(), y);
        assertEquals(test.getMonth().getValue(), mo);
        assertEquals(test.getDayOfMonth(), d);

        assertEquals(test, test);
        assertEquals(test.hashCode(), test.hashCode());
        assertEquals(OffsetDate.of(LocalDate.of(y, mo, d), offset), test);
    }

    //-----------------------------------------------------------------------
    @Test
    public void factory_of_intMonthInt() {
        OffsetDate test = OffsetDate.of(LocalDate.of(2007, Month.JULY, 15), OFFSET_PONE);
        check(test, 2007, 7, 15, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    @Test
    public void factory_of_ints() {
        OffsetDate test = OffsetDate.of(LocalDate.of(2007, 7, 15), OFFSET_PONE);
        check(test, 2007, 7, 15, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    @Test
    public void factory_of_intsMonthOffset() {
        assertEquals(TEST_2007_07_15_PONE, OffsetDate.of(LocalDate.of(2007, Month.JULY, 15), OFFSET_PONE));
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void factory_of_intsMonthOffset_dayTooLow() {
        OffsetDate.of(LocalDate.of(2007, Month.JANUARY, 0), OFFSET_PONE);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void factory_of_intsMonthOffset_dayTooHigh() {
        OffsetDate.of(LocalDate.of(2007, Month.JANUARY, 32), OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_of_intsMonthOffset_nullMonth() {
        OffsetDate.of(LocalDate.of(2007, null, 30), OFFSET_PONE);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void factory_of_intsMonthOffset_yearTooLow() {
        OffsetDate.of(LocalDate.of(Integer.MIN_VALUE, Month.JANUARY, 1), OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_of_intsMonthOffset_nullOffset() {
        OffsetDate.of(LocalDate.of(2007, Month.JANUARY, 30), null);
    }

    //-----------------------------------------------------------------------
    @Test
    public void factory_of_intsOffset() {
        OffsetDate test = OffsetDate.of(LocalDate.of(2007, 7, 15), OFFSET_PONE);
        check(test, 2007, 7, 15, OFFSET_PONE);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void factory_of_ints_dayTooLow() {
        OffsetDate.of(LocalDate.of(2007, 1, 0), OFFSET_PONE);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void factory_of_ints_dayTooHigh() {
        OffsetDate.of(LocalDate.of(2007, 1, 32), OFFSET_PONE);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void factory_of_ints_monthTooLow() {
        OffsetDate.of(LocalDate.of(2007, 0, 1), OFFSET_PONE);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void factory_of_ints_monthTooHigh() {
        OffsetDate.of(LocalDate.of(2007, 13, 1), OFFSET_PONE);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void factory_of_ints_yearTooLow() {
        OffsetDate.of(LocalDate.of(Integer.MIN_VALUE, 1, 1), OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_of_ints_nullOffset() {
        OffsetDate.of(LocalDate.of(2007, 1, 1), (ZoneOffset) null);
    }

    //-----------------------------------------------------------------------
    @Test
    public void factory_of_LocalDateZoneOffset() {
        LocalDate localDate = LocalDate.of(2008, 6, 30);
        OffsetDate test = OffsetDate.of(localDate, OFFSET_PONE);
        check(test, 2008, 6, 30, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_of_LocalDateZoneOffset_nullDate() {
        OffsetDate.of((LocalDate) null, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_of_LocalDateZoneOffset_nullOffset() {
        LocalDate localDate = LocalDate.of(2008, 6, 30);
        OffsetDate.of(localDate, (ZoneOffset) null);
    }

    //-----------------------------------------------------------------------
    // from(TemporalAccessor)
    //-----------------------------------------------------------------------
    @Test
    public void test_from_TemporalAccessor_OD() {
        assertEquals(OffsetDate.from(TEST_2007_07_15_PONE), TEST_2007_07_15_PONE);
    }

    @Test
    public void test_from_TemporalAccessor_ZDT() {
        ZonedDateTime base = LocalDateTime.of(2007, 7, 15, 17, 30).atZone(OFFSET_PONE);
        assertEquals(OffsetDate.from(base), TEST_2007_07_15_PONE);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_from_TemporalAccessor_invalid_noDerive() {
        OffsetDate.from(LocalTime.of(12, 30));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_from_TemporalAccessor_null() {
        OffsetDate.from((TemporalAccessor) null);
    }

    //-----------------------------------------------------------------------
    // parse()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleToString")
    public void factory_parse_validText(int y, int m, int d, String offsetId, String parsable) {
        OffsetDate t = OffsetDate.parse(parsable);
        assertNotNull(t, parsable);
        assertEquals(t.getYear(), y, parsable);
        assertEquals(t.getMonth().getValue(), m, parsable);
        assertEquals(t.getDayOfMonth(), d, parsable);
        assertEquals(t.getOffset(), ZoneOffset.of(offsetId));
    }

    @DataProvider(name="sampleBadParse")
    Object[][] provider_sampleBadParse() {
        return new Object[][]{
                {"2008/07/05"},
                {"10000-01-01"},
                {"2008-1-1"},
                {"2008--01"},
                {"ABCD-02-01"},
                {"2008-AB-01"},
                {"2008-02-AB"},
                {"-0000-02-01"},
                {"2008-02-01Y"},
                {"2008-02-01+19:00"},
                {"2008-02-01+01/00"},
                {"2008-02-01+1900"},
                {"2008-02-01+01:60"},
                {"2008-02-01+01:30:123"},
                {"2008-02-01"},
                {"2008-02-01+01:00[Europe/Paris]"},
        };
    }

    @Test(dataProvider="sampleBadParse", expectedExceptions=DateTimeParseException.class)
    public void factory_parse_invalidText(String unparsable) {
        OffsetDate.parse(unparsable);
    }

    @Test(expectedExceptions=DateTimeParseException.class)
    public void factory_parse_illegalValue() {
        OffsetDate.parse("2008-06-32+01:00");
    }

    @Test(expectedExceptions=DateTimeParseException.class)
    public void factory_parse_invalidValue() {
        OffsetDate.parse("2008-06-31+01:00");
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_parse_nullText() {
        OffsetDate.parse((String) null);
    }

    //-----------------------------------------------------------------------
    // parse(DateTimeFormatter)
    //-----------------------------------------------------------------------
    @Test
    public void factory_parse_formatter() {
        DateTimeFormatter f = DateTimeFormatters.pattern("y M d XXX");
        OffsetDate test = OffsetDate.parse("2010 12 3 +01:00", f);
        assertEquals(test, OffsetDate.of(LocalDate.of(2010, 12, 3), ZoneOffset.ofHours(1)));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_parse_formatter_nullText() {
        DateTimeFormatter f = DateTimeFormatters.pattern("y M d");
        OffsetDate.parse((String) null, f);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_parse_formatter_nullFormatter() {
        OffsetDate.parse("ANY", null);
    }

    //-----------------------------------------------------------------------
    // constructor
    //-----------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class)
    public void constructor_nullDate() throws Throwable  {
        Constructor<OffsetDate> con = OffsetDate.class.getDeclaredConstructor(LocalDate.class, ZoneOffset.class);
        con.setAccessible(true);
        try {
            con.newInstance(null, OFFSET_PONE);
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void constructor_nullOffset() throws Throwable  {
        Constructor<OffsetDate> con = OffsetDate.class.getDeclaredConstructor(LocalDate.class, ZoneOffset.class);
        con.setAccessible(true);
        try {
            con.newInstance(LocalDate.of(2008, 6, 30), null);
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }

    //-----------------------------------------------------------------------
    // basics
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleDates")
    Object[][] provider_sampleDates() {
        return new Object[][] {
            {2008, 7, 5, OFFSET_PTWO},
            {2007, 7, 5, OFFSET_PONE},
            {2006, 7, 5, OFFSET_PTWO},
            {2005, 7, 5, OFFSET_PONE},
            {2004, 1, 1, OFFSET_PTWO},
            {-1, 1, 2, OFFSET_PONE},
            {999999, 11, 20, ZoneOffset.ofHoursMinutesSeconds(6, 9, 12)},
        };
    }

    @Test(dataProvider="sampleDates")
    public void test_get_OffsetDate(int y, int m, int d, ZoneOffset offset) {
        LocalDate localDate = LocalDate.of(y, m, d);
        OffsetDate a = OffsetDate.of(localDate, offset);

        assertEquals(a.getDate(), localDate);
        assertEquals(a.getOffset(), offset);
        assertEquals(a.toString(), localDate.toString() + offset.toString());
        assertEquals(a.getYear(), localDate.getYear());
        assertEquals(a.getMonth(), localDate.getMonth());
        assertEquals(a.getDayOfMonth(), localDate.getDayOfMonth());
        assertEquals(a.getDayOfYear(), localDate.getDayOfYear());
        assertEquals(a.getDayOfWeek(), localDate.getDayOfWeek());
    }

    //-----------------------------------------------------------------------
    // get(TemporalField)
    //-----------------------------------------------------------------------
    @Test
    public void test_get_TemporalField() {
        OffsetDate test = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PONE);
        assertEquals(test.get(ChronoField.YEAR), 2008);
        assertEquals(test.get(ChronoField.MONTH_OF_YEAR), 6);
        assertEquals(test.get(ChronoField.DAY_OF_MONTH), 30);
        assertEquals(test.get(ChronoField.DAY_OF_WEEK), 1);
        assertEquals(test.get(ChronoField.DAY_OF_YEAR), 182);

        assertEquals(test.get(ChronoField.OFFSET_SECONDS), 3600);
    }

    @Test
    public void test_getLong_TemporalField() {
        OffsetDate test = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PONE);
        assertEquals(test.getLong(ChronoField.YEAR), 2008);
        assertEquals(test.getLong(ChronoField.MONTH_OF_YEAR), 6);
        assertEquals(test.getLong(ChronoField.DAY_OF_MONTH), 30);
        assertEquals(test.getLong(ChronoField.DAY_OF_WEEK), 1);
        assertEquals(test.getLong(ChronoField.DAY_OF_YEAR), 182);

        assertEquals(test.getLong(ChronoField.OFFSET_SECONDS), 3600);
    }

    //-----------------------------------------------------------------------
    // query(TemporalQuery)
    //-----------------------------------------------------------------------
    @Test
    public void test_query_chrono() {
        assertEquals(TEST_2007_07_15_PONE.query(TemporalQueries.chrono()), ISOChrono.INSTANCE);
        assertEquals(TemporalQueries.chrono().queryFrom(TEST_2007_07_15_PONE), ISOChrono.INSTANCE);
    }

    @Test
    public void test_query_zoneId() {
        assertEquals(TEST_2007_07_15_PONE.query(TemporalQueries.zoneId()), null);
        assertEquals(TemporalQueries.zoneId().queryFrom(TEST_2007_07_15_PONE), null);
    }

    @Test
    public void test_query_precision() {
        assertEquals(TEST_2007_07_15_PONE.query(TemporalQueries.precision()), ChronoUnit.DAYS);
        assertEquals(TemporalQueries.precision().queryFrom(TEST_2007_07_15_PONE), ChronoUnit.DAYS);
    }

    @Test
    public void test_query_offset() {
        assertEquals(TEST_2007_07_15_PONE.query(TemporalQueries.offset()), OFFSET_PONE);
        assertEquals(TemporalQueries.offset().queryFrom(TEST_2007_07_15_PONE), OFFSET_PONE);
    }

    @Test
    public void test_query_zone() {
        assertEquals(TEST_2007_07_15_PONE.query(TemporalQueries.zone()), OFFSET_PONE);
        assertEquals(TemporalQueries.zone().queryFrom(TEST_2007_07_15_PONE), OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_query_null() {
        TEST_2007_07_15_PONE.query(null);
    }

    //-----------------------------------------------------------------------
    // withOffset()
    //-----------------------------------------------------------------------
    @Test
    public void test_withOffset() {
        OffsetDate base = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PONE);
        OffsetDate test = base.withOffset(OFFSET_PTWO);
        assertEquals(test.getDate(), base.getDate());
        assertEquals(test.getOffset(), OFFSET_PTWO);
    }

    @Test
    public void test_withOffset_noChange() {
        OffsetDate base = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PONE);
        OffsetDate test = base.withOffset(OFFSET_PONE);
        assertEquals(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_withOffset_null() {
        TEST_2007_07_15_PONE.withOffset(null);
    }

    //-----------------------------------------------------------------------
    // with(WithAdjuster)
    //-----------------------------------------------------------------------
    @Test
    public void test_with_adjustment() {
        final OffsetDate sample = OffsetDate.of(LocalDate.of(2012, 3, 4), OFFSET_PONE);
        TemporalAdjuster adjuster = new TemporalAdjuster() {
            @Override
            public Temporal adjustInto(Temporal dateTime) {
                return sample;
            }
        };
        assertEquals(TEST_2007_07_15_PONE.with(adjuster), sample);
    }

    @Test
    public void test_with_adjustment_LocalDate() {
        OffsetDate test = TEST_2007_07_15_PONE.with(LocalDate.of(2008, 6, 30));
        assertEquals(test, OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PONE));
    }

    @Test
    public void test_with_adjustment_OffsetDate() {
        OffsetDate test = TEST_2007_07_15_PONE.with(OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PTWO));
        assertEquals(test, OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PTWO));
    }

    @Test
    public void test_with_adjustment_ZoneOffset() {
        OffsetDate test = TEST_2007_07_15_PONE.with(OFFSET_PTWO);
        assertEquals(test, OffsetDate.of(LocalDate.of(2007, 7, 15), OFFSET_PTWO));
    }

    @Test
    public void test_with_adjustment_Month() {
        OffsetDate test = TEST_2007_07_15_PONE.with(DECEMBER);
        assertEquals(test, OffsetDate.of(LocalDate.of(2007, 12, 15), OFFSET_PONE));
    }

    @Test
    public void test_with_adjustment_offsetUnchanged() {
        OffsetDate base = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PONE);
        OffsetDate test = base.with(Year.of(2008));
        assertEquals(test, base);
    }

    @Test
    public void test_with_adjustment_noChange() {
        LocalDate date = LocalDate.of(2008, 6, 30);
        OffsetDate base = OffsetDate.of(date, OFFSET_PONE);
        OffsetDate test = base.with(date);
        assertEquals(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_with_adjustment_null() {
        TEST_2007_07_15_PONE.with((TemporalAdjuster) null);
    }

    //-----------------------------------------------------------------------
    // with(TemporalField, long)
    //-----------------------------------------------------------------------
    @Test
    public void test_with_TemporalField() {
        OffsetDate test = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PONE);
        assertEquals(test.with(ChronoField.YEAR, 2009), OffsetDate.of(LocalDate.of(2009, 6, 30), OFFSET_PONE));
        assertEquals(test.with(ChronoField.MONTH_OF_YEAR, 7), OffsetDate.of(LocalDate.of(2008, 7, 30), OFFSET_PONE));
        assertEquals(test.with(ChronoField.DAY_OF_MONTH, 1), OffsetDate.of(LocalDate.of(2008, 6, 1), OFFSET_PONE));
        assertEquals(test.with(ChronoField.DAY_OF_WEEK, 2), OffsetDate.of(LocalDate.of(2008, 7, 1), OFFSET_PONE));
        assertEquals(test.with(ChronoField.DAY_OF_YEAR, 183), OffsetDate.of(LocalDate.of(2008, 7, 1), OFFSET_PONE));

        assertEquals(test.with(ChronoField.OFFSET_SECONDS, 7205), OffsetDate.of(LocalDate.of(2008, 6, 30), ZoneOffset.ofHoursMinutesSeconds(2, 0, 5)));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_with_TemporalField_null() {
        TEST_2007_07_15_PONE.with((TemporalField) null, 0);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_with_TemporalField_invalidField() {
        TEST_2007_07_15_PONE.with(ChronoField.AMPM_OF_DAY, 0);
    }

    //-----------------------------------------------------------------------
    // withYear()
    //-----------------------------------------------------------------------
    @Test
    public void test_withYear_int_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.withYear(2008);
        assertEquals(t, OffsetDate.of(LocalDate.of(2008, 7, 15), OFFSET_PONE));
    }

    @Test
    public void test_withYear_int_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.withYear(2007);
        assertEquals(t, TEST_2007_07_15_PONE);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_withYear_int_invalid() {
        TEST_2007_07_15_PONE.withYear(Year.MIN_VALUE - 1);
    }

    @Test
    public void test_withYear_int_adjustDay() {
        OffsetDate t = OffsetDate.of(LocalDate.of(2008, 2, 29), OFFSET_PONE).withYear(2007);
        OffsetDate expected = OffsetDate.of(LocalDate.of(2007, 2, 28), OFFSET_PONE);
        assertEquals(t, expected);
    }

    //-----------------------------------------------------------------------
    // withMonth()
    //-----------------------------------------------------------------------
    @Test
    public void test_withMonth_int_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.withMonth(1);
        assertEquals(t, OffsetDate.of(LocalDate.of(2007, 1, 15), OFFSET_PONE));
    }

    @Test
    public void test_withMonth_int_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.withMonth(7);
        assertEquals(t, TEST_2007_07_15_PONE);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_withMonth_int_invalid() {
        TEST_2007_07_15_PONE.withMonth(13);
    }

    @Test
    public void test_withMonth_int_adjustDay() {
        OffsetDate t = OffsetDate.of(LocalDate.of(2007, 12, 31), OFFSET_PONE).withMonth(11);
        OffsetDate expected = OffsetDate.of(LocalDate.of(2007, 11, 30), OFFSET_PONE);
        assertEquals(t, expected);
    }

    //-----------------------------------------------------------------------
    // withDayOfMonth()
    //-----------------------------------------------------------------------
    @Test
    public void test_withDayOfMonth_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.withDayOfMonth(1);
        assertEquals(t, OffsetDate.of(LocalDate.of(2007, 7, 1), OFFSET_PONE));
    }

    @Test
    public void test_withDayOfMonth_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.withDayOfMonth(15);
        assertEquals(t, OffsetDate.of(LocalDate.of(2007, 7, 15), OFFSET_PONE));
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_withDayOfMonth_invalidForMonth() {
        OffsetDate.of(LocalDate.of(2007, 11, 30), OFFSET_PONE).withDayOfMonth(31);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_withDayOfMonth_invalidAlways() {
        OffsetDate.of(LocalDate.of(2007, 11, 30), OFFSET_PONE).withDayOfMonth(32);
    }

    //-----------------------------------------------------------------------
    // withDayOfYear(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_withDayOfYear_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.withDayOfYear(33);
        assertEquals(t, OffsetDate.of(LocalDate.of(2007, 2, 2), OFFSET_PONE));
    }

    @Test
    public void test_withDayOfYear_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.withDayOfYear(31 + 28 + 31 + 30 + 31 + 30 + 15);
        assertEquals(t, TEST_2007_07_15_PONE);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_withDayOfYear_illegal() {
        TEST_2007_07_15_PONE.withDayOfYear(367);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_withDayOfYear_invalid() {
        TEST_2007_07_15_PONE.withDayOfYear(366);
    }

    //-----------------------------------------------------------------------
    // plus(PlusAdjuster)
    //-----------------------------------------------------------------------
    @Test
    public void test_plus_PlusAdjuster() {
        MockSimplePeriod period = MockSimplePeriod.of(7, ChronoUnit.MONTHS);
        OffsetDate t = TEST_2007_07_15_PONE.plus(period);
        assertEquals(t, OffsetDate.of(LocalDate.of(2008, 2, 15), OFFSET_PONE));
    }

    @Test
    public void test_plus_PlusAdjuster_noChange() {
        MockSimplePeriod period = MockSimplePeriod.of(0, ChronoUnit.MONTHS);
        OffsetDate t = TEST_2007_07_15_PONE.plus(period);
        assertEquals(t, TEST_2007_07_15_PONE);
    }

    @Test
    public void test_plus_PlusAdjuster_zero() {
        OffsetDate t = TEST_2007_07_15_PONE.plus(Period.ZERO);
        assertEquals(t, TEST_2007_07_15_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_plus_PlusAdjuster_null() {
        TEST_2007_07_15_PONE.plus((TemporalAdder) null);
    }

    //-----------------------------------------------------------------------
    // plusYears()
    //-----------------------------------------------------------------------
    @Test
    public void test_plusYears_long_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.plusYears(1);
        assertEquals(t, OffsetDate.of(LocalDate.of(2008, 7, 15), OFFSET_PONE));
    }

    @Test
    public void test_plusYears_long_negative() {
        OffsetDate t = TEST_2007_07_15_PONE.plusYears(-1);
        assertEquals(t, OffsetDate.of(LocalDate.of(2006, 7, 15), OFFSET_PONE));
    }

    @Test
    public void test_plusYears_long_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.plusYears(0);
        assertEquals(t, TEST_2007_07_15_PONE);
    }

    @Test
    public void test_plusYears_long_adjustDay() {
        OffsetDate t = OffsetDate.of(LocalDate.of(2008, 2, 29), OFFSET_PONE).plusYears(1);
        OffsetDate expected = OffsetDate.of(LocalDate.of(2009, 2, 28), OFFSET_PONE);
        assertEquals(t, expected);
    }

    @Test
    public void test_plusYears_long_big() {
        long years = 20L + Year.MAX_VALUE;
        OffsetDate test = OffsetDate.of(LocalDate.of(-40, 6, 1), OFFSET_PONE).plusYears(years);
        assertEquals(test, OffsetDate.of(LocalDate.of((int) (-40L + years), 6, 1), OFFSET_PONE));
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_plusYears_long_invalidTooLarge() {
        OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 1, 1), OFFSET_PONE).plusYears(1);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_plusYears_long_invalidTooLargeMaxAddMax() {
        OffsetDate test = OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 1), OFFSET_PONE);
        test.plusYears(Long.MAX_VALUE);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_plusYears_long_invalidTooLargeMaxAddMin() {
        OffsetDate test = OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 1), OFFSET_PONE);
        test.plusYears(Long.MIN_VALUE);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_plusYears_long_invalidTooSmall() {
        OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 1), OFFSET_PONE).plusYears(-1);
    }

    //-----------------------------------------------------------------------
    // plusMonths()
    //-----------------------------------------------------------------------
    @Test
    public void test_plusMonths_long_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.plusMonths(1);
        assertEquals(t, OffsetDate.of(LocalDate.of(2007, 8, 15), OFFSET_PONE));
    }

    @Test
    public void test_plusMonths_long_overYears() {
        OffsetDate t = TEST_2007_07_15_PONE.plusMonths(25);
        assertEquals(t, OffsetDate.of(LocalDate.of(2009, 8, 15), OFFSET_PONE));
    }

    @Test
    public void test_plusMonths_long_negative() {
        OffsetDate t = TEST_2007_07_15_PONE.plusMonths(-1);
        assertEquals(t, OffsetDate.of(LocalDate.of(2007, 6, 15), OFFSET_PONE));
    }

    @Test
    public void test_plusMonths_long_negativeAcrossYear() {
        OffsetDate t = TEST_2007_07_15_PONE.plusMonths(-7);
        assertEquals(t, OffsetDate.of(LocalDate.of(2006, 12, 15), OFFSET_PONE));
    }

    @Test
    public void test_plusMonths_long_negativeOverYears() {
        OffsetDate t = TEST_2007_07_15_PONE.plusMonths(-31);
        assertEquals(t, OffsetDate.of(LocalDate.of(2004, 12, 15), OFFSET_PONE));
    }

    @Test
    public void test_plusMonths_long_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.plusMonths(0);
        assertEquals(t, TEST_2007_07_15_PONE);
    }

    @Test
    public void test_plusMonths_long_adjustDayFromLeapYear() {
        OffsetDate t = OffsetDate.of(LocalDate.of(2008, 2, 29), OFFSET_PONE).plusMonths(12);
        OffsetDate expected = OffsetDate.of(LocalDate.of(2009, 2, 28), OFFSET_PONE);
        assertEquals(t, expected);
    }

    @Test
    public void test_plusMonths_long_adjustDayFromMonthLength() {
        OffsetDate t = OffsetDate.of(LocalDate.of(2007, 3, 31), OFFSET_PONE).plusMonths(1);
        OffsetDate expected = OffsetDate.of(LocalDate.of(2007, 4, 30), OFFSET_PONE);
        assertEquals(t, expected);
    }

    @Test
    public void test_plusMonths_long_big() {
        long months = 20L + Integer.MAX_VALUE;
        OffsetDate test = OffsetDate.of(LocalDate.of(-40, 6, 1), OFFSET_PONE).plusMonths(months);
        assertEquals(test, OffsetDate.of(LocalDate.of((int) (-40L + months / 12), 6 + (int) (months % 12), 1), OFFSET_PONE));
    }

    @Test(expectedExceptions={DateTimeException.class})
    public void test_plusMonths_long_invalidTooLarge() {
        OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 1), OFFSET_PONE).plusMonths(1);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_plusMonths_long_invalidTooLargeMaxAddMax() {
        OffsetDate test = OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 1), OFFSET_PONE);
        test.plusMonths(Long.MAX_VALUE);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_plusMonths_long_invalidTooLargeMaxAddMin() {
        OffsetDate test = OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 1), OFFSET_PONE);
        test.plusMonths(Long.MIN_VALUE);
    }

    @Test(expectedExceptions={DateTimeException.class})
    public void test_plusMonths_long_invalidTooSmall() {
        OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 1), OFFSET_PONE).plusMonths(-1);
    }

    //-----------------------------------------------------------------------
    // plusWeeks()
    //-----------------------------------------------------------------------
    @DataProvider(name="samplePlusWeeksSymmetry")
    Object[][] provider_samplePlusWeeksSymmetry() {
        return new Object[][] {
            {OffsetDate.of(LocalDate.of(-1, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(-1, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(-1, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(-1, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(0, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(0, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(0, 2, 29), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(0, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(0, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2007, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2007, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2007, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2007, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2008, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2008, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2008, 2, 29), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2008, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2008, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2099, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2099, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2099, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2099, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2100, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2100, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2100, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2100, 12, 31), OFFSET_PTWO)},
        };
    }

    @Test(dataProvider="samplePlusWeeksSymmetry")
    public void test_plusWeeks_symmetry(OffsetDate reference) {
        for (int weeks = 0; weeks < 365 * 8; weeks++) {
            OffsetDate t = reference.plusWeeks(weeks).plusWeeks(-weeks);
            assertEquals(t, reference);

            t = reference.plusWeeks(-weeks).plusWeeks(weeks);
            assertEquals(t, reference);
        }
    }

    @Test
    public void test_plusWeeks_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.plusWeeks(1);
        assertEquals(t, OffsetDate.of(LocalDate.of(2007, 7, 22), OFFSET_PONE));
    }

    @Test
    public void test_plusWeeks_overMonths() {
        OffsetDate t = TEST_2007_07_15_PONE.plusWeeks(9);
        assertEquals(t, OffsetDate.of(LocalDate.of(2007, 9, 16), OFFSET_PONE));
    }

    @Test
    public void test_plusWeeks_overYears() {
        OffsetDate t = OffsetDate.of(LocalDate.of(2006, 7, 16), OFFSET_PONE).plusWeeks(52);
        assertEquals(t, TEST_2007_07_15_PONE);
    }

    @Test
    public void test_plusWeeks_overLeapYears() {
        OffsetDate t = TEST_2007_07_15_PONE.plusYears(-1).plusWeeks(104);
        assertEquals(t, OffsetDate.of(LocalDate.of(2008, 7, 12), OFFSET_PONE));
    }

    @Test
    public void test_plusWeeks_negative() {
        OffsetDate t = TEST_2007_07_15_PONE.plusWeeks(-1);
        assertEquals(t, OffsetDate.of(LocalDate.of(2007, 7, 8), OFFSET_PONE));
    }

    @Test
    public void test_plusWeeks_negativeAcrossYear() {
        OffsetDate t = TEST_2007_07_15_PONE.plusWeeks(-28);
        assertEquals(t, OffsetDate.of(LocalDate.of(2006, 12, 31), OFFSET_PONE));
    }

    @Test
    public void test_plusWeeks_negativeOverYears() {
        OffsetDate t = TEST_2007_07_15_PONE.plusWeeks(-104);
        assertEquals(t, OffsetDate.of(LocalDate.of(2005, 7, 17), OFFSET_PONE));
    }

    @Test
    public void test_plusWeeks_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.plusWeeks(0);
        assertEquals(t, TEST_2007_07_15_PONE);
    }

    @Test
    public void test_plusWeeks_maximum() {
        OffsetDate t = OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 24), OFFSET_PONE).plusWeeks(1);
        OffsetDate expected = OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 31), OFFSET_PONE);
        assertEquals(t, expected);
    }

    @Test
    public void test_plusWeeks_minimum() {
        OffsetDate t = OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 8), OFFSET_PONE).plusWeeks(-1);
        OffsetDate expected = OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 1), OFFSET_PONE);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions={DateTimeException.class})
    public void test_plusWeeks_invalidTooLarge() {
        OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 25), OFFSET_PONE).plusWeeks(1);
    }

    @Test(expectedExceptions={DateTimeException.class})
    public void test_plusWeeks_invalidTooSmall() {
        OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 7), OFFSET_PONE).plusWeeks(-1);
    }

    @Test(expectedExceptions={ArithmeticException.class})
    public void test_plusWeeks_invalidMaxMinusMax() {
        OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 25), OFFSET_PONE).plusWeeks(Long.MAX_VALUE);
    }

    @Test(expectedExceptions={ArithmeticException.class})
    public void test_plusWeeks_invalidMaxMinusMin() {
        OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 25), OFFSET_PONE).plusWeeks(Long.MIN_VALUE);
    }

    //-----------------------------------------------------------------------
    // plusDays()
    //-----------------------------------------------------------------------
    @DataProvider(name="samplePlusDaysSymmetry")
    Object[][] provider_samplePlusDaysSymmetry() {
        return new Object[][] {
            {OffsetDate.of(LocalDate.of(-1, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(-1, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(-1, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(-1, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(0, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(0, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(0, 2, 29), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(0, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(0, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2007, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2007, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2007, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2007, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2008, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2008, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2008, 2, 29), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2008, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2008, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2099, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2099, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2099, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2099, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2100, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2100, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2100, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2100, 12, 31), OFFSET_PTWO)},
        };
    }

    @Test(dataProvider="samplePlusDaysSymmetry")
    public void test_plusDays_symmetry(OffsetDate reference) {
        for (int days = 0; days < 365 * 8; days++) {
            OffsetDate t = reference.plusDays(days).plusDays(-days);
            assertEquals(t, reference);

            t = reference.plusDays(-days).plusDays(days);
            assertEquals(t, reference);
        }
    }

    @Test
    public void test_plusDays_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.plusDays(1);
        assertEquals(t, OffsetDate.of(LocalDate.of(2007, 7, 16), OFFSET_PONE));
    }

    @Test
    public void test_plusDays_overMonths() {
        OffsetDate t = TEST_2007_07_15_PONE.plusDays(62);
        assertEquals(t, OffsetDate.of(LocalDate.of(2007, 9, 15), OFFSET_PONE));
    }

    @Test
    public void test_plusDays_overYears() {
        OffsetDate t = OffsetDate.of(LocalDate.of(2006, 7, 14), OFFSET_PONE).plusDays(366);
        assertEquals(t, TEST_2007_07_15_PONE);
    }

    @Test
    public void test_plusDays_overLeapYears() {
        OffsetDate t = TEST_2007_07_15_PONE.plusYears(-1).plusDays(365 + 366);
        assertEquals(t, OffsetDate.of(LocalDate.of(2008, 7, 15), OFFSET_PONE));
    }

    @Test
    public void test_plusDays_negative() {
        OffsetDate t = TEST_2007_07_15_PONE.plusDays(-1);
        assertEquals(t, OffsetDate.of(LocalDate.of(2007, 7, 14), OFFSET_PONE));
    }

    @Test
    public void test_plusDays_negativeAcrossYear() {
        OffsetDate t = TEST_2007_07_15_PONE.plusDays(-196);
        assertEquals(t, OffsetDate.of(LocalDate.of(2006, 12, 31), OFFSET_PONE));
    }

    @Test
    public void test_plusDays_negativeOverYears() {
        OffsetDate t = TEST_2007_07_15_PONE.plusDays(-730);
        assertEquals(t, OffsetDate.of(LocalDate.of(2005, 7, 15), OFFSET_PONE));
    }

    @Test
    public void test_plusDays_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.plusDays(0);
        assertEquals(t, TEST_2007_07_15_PONE);
    }

    @Test
    public void test_plusDays_maximum() {
        OffsetDate t = OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 30), OFFSET_PONE).plusDays(1);
        OffsetDate expected = OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 31), OFFSET_PONE);
        assertEquals(t, expected);
    }

    @Test
    public void test_plusDays_minimum() {
        OffsetDate t = OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 2), OFFSET_PONE).plusDays(-1);
        OffsetDate expected = OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 1), OFFSET_PONE);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions={DateTimeException.class})
    public void test_plusDays_invalidTooLarge() {
        OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 31), OFFSET_PONE).plusDays(1);
    }

    @Test(expectedExceptions={DateTimeException.class})
    public void test_plusDays_invalidTooSmall() {
        OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 1), OFFSET_PONE).plusDays(-1);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusDays_overflowTooLarge() {
        OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 31), OFFSET_PONE).plusDays(Long.MAX_VALUE);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusDays_overflowTooSmall() {
        OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 1), OFFSET_PONE).plusDays(Long.MIN_VALUE);
    }

    //-----------------------------------------------------------------------
    // minus(MinusAdjuster)
    //-----------------------------------------------------------------------
    @Test
    public void test_minus_MinusAdjuster() {
        MockSimplePeriod period = MockSimplePeriod.of(7, ChronoUnit.MONTHS);
        OffsetDate t = TEST_2007_07_15_PONE.minus(period);
        assertEquals(t, OffsetDate.of(LocalDate.of(2006, 12, 15), OFFSET_PONE));
    }

    @Test
    public void test_minus_MinusAdjuster_noChange() {
        MockSimplePeriod period = MockSimplePeriod.of(0, ChronoUnit.MONTHS);
        OffsetDate t = TEST_2007_07_15_PONE.minus(period);
        assertEquals(t, TEST_2007_07_15_PONE);
    }

    @Test
    public void test_minus_MinusAdjuster_zero() {
        OffsetDate t = TEST_2007_07_15_PONE.minus(Period.ZERO);
        assertEquals(t, TEST_2007_07_15_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_plus_MinusAdjuster_null() {
        TEST_2007_07_15_PONE.minus((TemporalSubtractor) null);
    }

    //-----------------------------------------------------------------------
    // minusYears()
    //-----------------------------------------------------------------------
    @Test
    public void test_minusYears_long_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.minusYears(1);
        assertEquals(t, OffsetDate.of(LocalDate.of(2006, 7, 15), OFFSET_PONE));
    }

    @Test
    public void test_minusYears_long_negative() {
        OffsetDate t = TEST_2007_07_15_PONE.minusYears(-1);
        assertEquals(t, OffsetDate.of(LocalDate.of(2008, 7, 15), OFFSET_PONE));
    }

    @Test
    public void test_minusYears_long_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.minusYears(0);
        assertEquals(t, TEST_2007_07_15_PONE);
    }

    @Test
    public void test_minusYears_long_adjustDay() {
        OffsetDate t = OffsetDate.of(LocalDate.of(2008, 2, 29), OFFSET_PONE).minusYears(1);
        OffsetDate expected = OffsetDate.of(LocalDate.of(2007, 2, 28), OFFSET_PONE);
        assertEquals(t, expected);
    }

    @Test
    public void test_minusYears_long_big() {
        long years = 20L + Year.MAX_VALUE;
        OffsetDate test = OffsetDate.of(LocalDate.of(40, 6, 1), OFFSET_PONE).minusYears(years);
        assertEquals(test, OffsetDate.of(LocalDate.of((int) (40L - years), 6, 1), OFFSET_PONE));
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_minusYears_long_invalidTooLarge() {
        OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 1, 1), OFFSET_PONE).minusYears(-1);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_minusYears_long_invalidTooLargeMaxAddMax() {
        OffsetDate test = OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 1), OFFSET_PONE);
        test.minusYears(Long.MAX_VALUE);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_minusYears_long_invalidTooLargeMaxAddMin() {
        OffsetDate test = OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 1), OFFSET_PONE);
        test.minusYears(Long.MIN_VALUE);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_minusYears_long_invalidTooSmall() {
        OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 1), OFFSET_PONE).minusYears(1);
    }

    //-----------------------------------------------------------------------
    // minusMonths()
    //-----------------------------------------------------------------------
    @Test
    public void test_minusMonths_long_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.minusMonths(1);
        assertEquals(t, OffsetDate.of(LocalDate.of(2007, 6, 15), OFFSET_PONE));
    }

    @Test
    public void test_minusMonths_long_overYears() {
        OffsetDate t = TEST_2007_07_15_PONE.minusMonths(25);
        assertEquals(t, OffsetDate.of(LocalDate.of(2005, 6, 15), OFFSET_PONE));
    }

    @Test
    public void test_minusMonths_long_negative() {
        OffsetDate t = TEST_2007_07_15_PONE.minusMonths(-1);
        assertEquals(t, OffsetDate.of(LocalDate.of(2007, 8, 15), OFFSET_PONE));
    }

    @Test
    public void test_minusMonths_long_negativeAcrossYear() {
        OffsetDate t = TEST_2007_07_15_PONE.minusMonths(-7);
        assertEquals(t, OffsetDate.of(LocalDate.of(2008, 2, 15), OFFSET_PONE));
    }

    @Test
    public void test_minusMonths_long_negativeOverYears() {
        OffsetDate t = TEST_2007_07_15_PONE.minusMonths(-31);
        assertEquals(t, OffsetDate.of(LocalDate.of(2010, 2, 15), OFFSET_PONE));
    }

    @Test
    public void test_minusMonths_long_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.minusMonths(0);
        assertEquals(t, TEST_2007_07_15_PONE);
    }

    @Test
    public void test_minusMonths_long_adjustDayFromLeapYear() {
        OffsetDate t = OffsetDate.of(LocalDate.of(2008, 2, 29), OFFSET_PONE).minusMonths(12);
        OffsetDate expected = OffsetDate.of(LocalDate.of(2007, 2, 28), OFFSET_PONE);
        assertEquals(t, expected);
    }

    @Test
    public void test_minusMonths_long_adjustDayFromMonthLength() {
        OffsetDate t = OffsetDate.of(LocalDate.of(2007, 3, 31), OFFSET_PONE).minusMonths(1);
        OffsetDate expected = OffsetDate.of(LocalDate.of(2007, 2, 28), OFFSET_PONE);
        assertEquals(t, expected);
    }

    @Test
    public void test_minusMonths_long_big() {
        long months = 20L + Integer.MAX_VALUE;
        OffsetDate test = OffsetDate.of(LocalDate.of(40, 6, 1), OFFSET_PONE).minusMonths(months);
        assertEquals(test, OffsetDate.of(LocalDate.of((int) (40L - months / 12), 6 - (int) (months % 12), 1), OFFSET_PONE));
    }

    @Test(expectedExceptions={DateTimeException.class})
    public void test_minusMonths_long_invalidTooLarge() {
        OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 1), OFFSET_PONE).minusMonths(-1);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_minusMonths_long_invalidTooLargeMaxAddMax() {
        OffsetDate test = OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 1), OFFSET_PONE);
        test.minusMonths(Long.MAX_VALUE);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_minusMonths_long_invalidTooLargeMaxAddMin() {
        OffsetDate test = OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 1), OFFSET_PONE);
        test.minusMonths(Long.MIN_VALUE);
    }

    @Test(expectedExceptions={DateTimeException.class})
    public void test_minusMonths_long_invalidTooSmall() {
        OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 1), OFFSET_PONE).minusMonths(1);
    }

    //-----------------------------------------------------------------------
    // minusWeeks()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleMinusWeeksSymmetry")
    Object[][] provider_sampleMinusWeeksSymmetry() {
        return new Object[][] {
            {OffsetDate.of(LocalDate.of(-1, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(-1, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(-1, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(-1, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(0, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(0, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(0, 2, 29), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(0, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(0, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2007, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2007, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2007, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2007, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2008, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2008, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2008, 2, 29), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2008, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2008, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2099, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2099, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2099, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2099, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2100, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2100, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2100, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2100, 12, 31), OFFSET_PTWO)},
        };
    }

    @Test(dataProvider="sampleMinusWeeksSymmetry")
    public void test_minusWeeks_symmetry(OffsetDate reference) {
        for (int weeks = 0; weeks < 365 * 8; weeks++) {
            OffsetDate t = reference.minusWeeks(weeks).minusWeeks(-weeks);
            assertEquals(t, reference);

            t = reference.minusWeeks(-weeks).minusWeeks(weeks);
            assertEquals(t, reference);
        }
    }

    @Test
    public void test_minusWeeks_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.minusWeeks(1);
        assertEquals(t, OffsetDate.of(LocalDate.of(2007, 7, 8), OFFSET_PONE));
    }

    @Test
    public void test_minusWeeks_overMonths() {
        OffsetDate t = TEST_2007_07_15_PONE.minusWeeks(9);
        assertEquals(t, OffsetDate.of(LocalDate.of(2007, 5, 13), OFFSET_PONE));
    }

    @Test
    public void test_minusWeeks_overYears() {
        OffsetDate t = OffsetDate.of(LocalDate.of(2008, 7, 13), OFFSET_PONE).minusWeeks(52);
        assertEquals(t, TEST_2007_07_15_PONE);
    }

    @Test
    public void test_minusWeeks_overLeapYears() {
        OffsetDate t = TEST_2007_07_15_PONE.minusYears(-1).minusWeeks(104);
        assertEquals(t, OffsetDate.of(LocalDate.of(2006, 7, 18), OFFSET_PONE));
    }

    @Test
    public void test_minusWeeks_negative() {
        OffsetDate t = TEST_2007_07_15_PONE.minusWeeks(-1);
        assertEquals(t, OffsetDate.of(LocalDate.of(2007, 7, 22), OFFSET_PONE));
    }

    @Test
    public void test_minusWeeks_negativeAcrossYear() {
        OffsetDate t = TEST_2007_07_15_PONE.minusWeeks(-28);
        assertEquals(t, OffsetDate.of(LocalDate.of(2008, 1, 27), OFFSET_PONE));
    }

    @Test
    public void test_minusWeeks_negativeOverYears() {
        OffsetDate t = TEST_2007_07_15_PONE.minusWeeks(-104);
        assertEquals(t, OffsetDate.of(LocalDate.of(2009, 7, 12), OFFSET_PONE));
    }

    @Test
    public void test_minusWeeks_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.minusWeeks(0);
        assertEquals(t, TEST_2007_07_15_PONE);
    }

    @Test
    public void test_minusWeeks_maximum() {
        OffsetDate t = OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 24), OFFSET_PONE).minusWeeks(-1);
        OffsetDate expected = OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 31), OFFSET_PONE);
        assertEquals(t, expected);
    }

    @Test
    public void test_minusWeeks_minimum() {
        OffsetDate t = OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 8), OFFSET_PONE).minusWeeks(1);
        OffsetDate expected = OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 1), OFFSET_PONE);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions={DateTimeException.class})
    public void test_minusWeeks_invalidTooLarge() {
        OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 25), OFFSET_PONE).minusWeeks(-1);
    }

    @Test(expectedExceptions={DateTimeException.class})
    public void test_minusWeeks_invalidTooSmall() {
        OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 7), OFFSET_PONE).minusWeeks(1);
    }

    @Test(expectedExceptions={ArithmeticException.class})
    public void test_minusWeeks_invalidMaxMinusMax() {
        OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 25), OFFSET_PONE).minusWeeks(Long.MAX_VALUE);
    }

    @Test(expectedExceptions={ArithmeticException.class})
    public void test_minusWeeks_invalidMaxMinusMin() {
        OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 25), OFFSET_PONE).minusWeeks(Long.MIN_VALUE);
    }

    //-----------------------------------------------------------------------
    // minusDays()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleMinusDaysSymmetry")
    Object[][] provider_sampleMinusDaysSymmetry() {
        return new Object[][] {
            {OffsetDate.of(LocalDate.of(-1, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(-1, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(-1, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(-1, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(0, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(0, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(0, 2, 29), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(0, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(0, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2007, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2007, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2007, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2007, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2008, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2008, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2008, 2, 29), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2008, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2008, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2099, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2099, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2099, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2099, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2100, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2100, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2100, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2100, 12, 31), OFFSET_PTWO)},
        };
    }

    @Test(dataProvider="sampleMinusDaysSymmetry")
    public void test_minusDays_symmetry(OffsetDate reference) {
        for (int days = 0; days < 365 * 8; days++) {
            OffsetDate t = reference.minusDays(days).minusDays(-days);
            assertEquals(t, reference);

            t = reference.minusDays(-days).minusDays(days);
            assertEquals(t, reference);
        }
    }

    @Test
    public void test_minusDays_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.minusDays(1);
        assertEquals(t, OffsetDate.of(LocalDate.of(2007, 7, 14), OFFSET_PONE));
    }

    @Test
    public void test_minusDays_overMonths() {
        OffsetDate t = TEST_2007_07_15_PONE.minusDays(62);
        assertEquals(t, OffsetDate.of(LocalDate.of(2007, 5, 14), OFFSET_PONE));
    }

    @Test
    public void test_minusDays_overYears() {
        OffsetDate t = OffsetDate.of(LocalDate.of(2008, 7, 16), OFFSET_PONE).minusDays(367);
        assertEquals(t, TEST_2007_07_15_PONE);
    }

    @Test
    public void test_minusDays_overLeapYears() {
        OffsetDate t = TEST_2007_07_15_PONE.plusYears(2).minusDays(365 + 366);
        assertEquals(t, TEST_2007_07_15_PONE);
    }

    @Test
    public void test_minusDays_negative() {
        OffsetDate t = TEST_2007_07_15_PONE.minusDays(-1);
        assertEquals(t, OffsetDate.of(LocalDate.of(2007, 7, 16), OFFSET_PONE));
    }

    @Test
    public void test_minusDays_negativeAcrossYear() {
        OffsetDate t = TEST_2007_07_15_PONE.minusDays(-169);
        assertEquals(t, OffsetDate.of(LocalDate.of(2007, 12, 31), OFFSET_PONE));
    }

    @Test
    public void test_minusDays_negativeOverYears() {
        OffsetDate t = TEST_2007_07_15_PONE.minusDays(-731);
        assertEquals(t, OffsetDate.of(LocalDate.of(2009, 7, 15), OFFSET_PONE));
    }

    @Test
    public void test_minusDays_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.minusDays(0);
        assertEquals(t, TEST_2007_07_15_PONE);
    }

    @Test
    public void test_minusDays_maximum() {
        OffsetDate t = OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 30), OFFSET_PONE).minusDays(-1);
        OffsetDate expected = OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 31), OFFSET_PONE);
        assertEquals(t, expected);
    }

    @Test
    public void test_minusDays_minimum() {
        OffsetDate t = OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 2), OFFSET_PONE).minusDays(1);
        OffsetDate expected = OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 1), OFFSET_PONE);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions={DateTimeException.class})
    public void test_minusDays_invalidTooLarge() {
        OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 31), OFFSET_PONE).minusDays(-1);
    }

    @Test(expectedExceptions={DateTimeException.class})
    public void test_minusDays_invalidTooSmall() {
        OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 1), OFFSET_PONE).minusDays(1);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusDays_overflowTooLarge() {
        OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 31), OFFSET_PONE).minusDays(Long.MIN_VALUE);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusDays_overflowTooSmall() {
        OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 1), OFFSET_PONE).minusDays(Long.MAX_VALUE);
    }

    //-----------------------------------------------------------------------
    // atTime()
    //-----------------------------------------------------------------------
    @Test
    public void test_atTime_Local() {
        OffsetDate t = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PTWO);
        assertEquals(t.atTime(LocalTime.of(11, 30)),
                OffsetDateTime.of(LocalDate.of(2008, 6, 30), LocalTime.of(11, 30), OFFSET_PTWO));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_atTime_Local_nullLocalTime() {
        OffsetDate t = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PTWO);
        t.atTime((LocalTime) null);
    }

    //-----------------------------------------------------------------------
    // getDate()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_getDate(int year, int month, int day, ZoneOffset offset) {
        LocalDate t = LocalDate.of(year, month, day);
        assertEquals(OffsetDate.of(LocalDate.of(year, month, day), offset).getDate(), t);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    @Test
    public void test_compareTo_date() {
        OffsetDate a = OffsetDate.of(LocalDate.of(2008, 6, 29), OFFSET_PONE);
        OffsetDate b = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PONE);  // a is before b due to date
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
        assertEquals(a.atTime(LocalTime.MIDNIGHT).toInstant().compareTo(b.atTime(LocalTime.MIDNIGHT).toInstant()) < 0, true);
    }

    @Test
    public void test_compareTo_offset() {
        OffsetDate a = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PTWO);
        OffsetDate b = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PONE);  // a is before b due to offset
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
        assertEquals(a.atTime(LocalTime.MIDNIGHT).toInstant().compareTo(b.atTime(LocalTime.MIDNIGHT).toInstant()) < 0, true);
    }

    @Test
    public void test_compareTo_both() {
        OffsetDate a = OffsetDate.of(LocalDate.of(2008, 6, 29), OFFSET_PTWO);
        OffsetDate b = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PONE);  // a is before b on instant scale
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
        assertEquals(a.atTime(LocalTime.MIDNIGHT).toInstant().compareTo(b.atTime(LocalTime.MIDNIGHT).toInstant()) < 0, true);
    }

    @Test
    public void test_compareTo_24hourDifference() {
        OffsetDate a = OffsetDate.of(LocalDate.of(2008, 6, 29), ZoneOffset.ofHours(-12));
        OffsetDate b = OffsetDate.of(LocalDate.of(2008, 6, 30), ZoneOffset.ofHours(12));  // a is before b despite being same time-line time
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
        assertEquals(a.atTime(LocalTime.MIDNIGHT).toInstant().compareTo(b.atTime(LocalTime.MIDNIGHT).toInstant()) == 0, true);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_compareTo_null() {
        OffsetDate a = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PONE);
        a.compareTo(null);
    }

    @Test(expectedExceptions=ClassCastException.class)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void compareToNonOffsetDate() {
       Comparable c = TEST_2007_07_15_PONE;
       c.compareTo(new Object());
    }

    //-----------------------------------------------------------------------
    // isAfter() / isBefore() / isEqual()
    //-----------------------------------------------------------------------
    @Test
    public void test_isBeforeIsAfterIsEqual1() {
        OffsetDate a = OffsetDate.of(LocalDate.of(2008, 6, 29), OFFSET_PONE);
        OffsetDate b = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PONE);  // a is before b due to time
        assertEquals(a.isBefore(b), true);
        assertEquals(a.isEqual(b), false);
        assertEquals(a.isAfter(b), false);

        assertEquals(b.isBefore(a), false);
        assertEquals(b.isEqual(a), false);
        assertEquals(b.isAfter(a), true);

        assertEquals(a.isBefore(a), false);
        assertEquals(b.isBefore(b), false);

        assertEquals(a.isEqual(a), true);
        assertEquals(b.isEqual(b), true);

        assertEquals(a.isAfter(a), false);
        assertEquals(b.isAfter(b), false);
    }

    @Test
    public void test_isBeforeIsAfterIsEqual2() {
        OffsetDate a = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PTWO);
        OffsetDate b = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PONE);  // a is before b due to offset
        assertEquals(a.isBefore(b), true);
        assertEquals(a.isEqual(b), false);
        assertEquals(a.isAfter(b), false);

        assertEquals(b.isBefore(a), false);
        assertEquals(b.isEqual(a), false);
        assertEquals(b.isAfter(a), true);

        assertEquals(a.isBefore(a), false);
        assertEquals(b.isBefore(b), false);

        assertEquals(a.isEqual(a), true);
        assertEquals(b.isEqual(b), true);

        assertEquals(a.isAfter(a), false);
        assertEquals(b.isAfter(b), false);
    }

    @Test
    public void test_isBeforeIsAfterIsEqual_instantComparison() {
        OffsetDate a = OffsetDate.of(LocalDate.of(2008, 6, 30), ZoneOffset.ofHours(12));
        OffsetDate b = OffsetDate.of(LocalDate.of(2008, 6, 29), ZoneOffset.ofHours(-12));  // a is same instant as b
        assertEquals(a.isBefore(b), false);
        assertEquals(a.isEqual(b), true);
        assertEquals(a.isAfter(b), false);

        assertEquals(b.isBefore(a), false);
        assertEquals(b.isEqual(a), true);
        assertEquals(b.isAfter(a), false);

        assertEquals(a.isBefore(a), false);
        assertEquals(b.isBefore(b), false);

        assertEquals(a.isEqual(a), true);
        assertEquals(b.isEqual(b), true);

        assertEquals(a.isAfter(a), false);
        assertEquals(b.isAfter(b), false);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isBefore_null() {
        OffsetDate a = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PONE);
        a.isBefore(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isAfter_null() {
        OffsetDate a = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PONE);
        a.isAfter(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isEqual_null() {
        OffsetDate a = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PONE);
        a.isEqual(null);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_equals_true(int y, int m, int d, ZoneOffset offset) {
        OffsetDate a = OffsetDate.of(LocalDate.of(y, m, d), offset);
        OffsetDate b = OffsetDate.of(LocalDate.of(y, m, d), offset);
        assertEquals(a.equals(b), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
    }
    @Test(dataProvider="sampleDates")
    public void test_equals_false_year_differs(int y, int m, int d, ZoneOffset offset) {
        OffsetDate a = OffsetDate.of(LocalDate.of(y, m, d), offset);
        OffsetDate b = OffsetDate.of(LocalDate.of(y + 1, m, d), offset);
        assertEquals(a.equals(b), false);
    }

    @Test(dataProvider="sampleDates")
    public void test_equals_false_month_differs(int y, int m, int d, ZoneOffset offset) {
        OffsetDate a = OffsetDate.of(LocalDate.of(y, m, d), offset);
        OffsetDate b = OffsetDate.of(LocalDate.of(y, m + 1, d), offset);
        assertEquals(a.equals(b), false);
    }

    @Test(dataProvider="sampleDates")
    public void test_equals_false_day_differs(int y, int m, int d, ZoneOffset offset) {
        OffsetDate a = OffsetDate.of(LocalDate.of(y, m, d), offset);
        OffsetDate b = OffsetDate.of(LocalDate.of(y, m, d + 1), offset);
        assertEquals(a.equals(b), false);
    }

    @Test(dataProvider="sampleDates")
    public void test_equals_false_offset_differs(int y, int m, int d, ZoneOffset ignored) {
        OffsetDate a = OffsetDate.of(LocalDate.of(y, m, d), OFFSET_PONE);
        OffsetDate b = OffsetDate.of(LocalDate.of(y, m, d), OFFSET_PTWO);
        assertEquals(a.equals(b), false);
    }

    @Test
    public void test_equals_itself_true() {
        assertEquals(TEST_2007_07_15_PONE.equals(TEST_2007_07_15_PONE), true);
    }

    @Test
    public void test_equals_string_false() {
        assertEquals(TEST_2007_07_15_PONE.equals("2007-07-15"), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleToString")
    Object[][] provider_sampleToString() {
        return new Object[][] {
            {2008, 7, 5, "Z", "2008-07-05Z"},
            {2008, 7, 5, "+00", "2008-07-05Z"},
            {2008, 7, 5, "+0000", "2008-07-05Z"},
            {2008, 7, 5, "+00:00", "2008-07-05Z"},
            {2008, 7, 5, "+000000", "2008-07-05Z"},
            {2008, 7, 5, "+00:00:00", "2008-07-05Z"},
            {2008, 7, 5, "-00", "2008-07-05Z"},
            {2008, 7, 5, "-0000", "2008-07-05Z"},
            {2008, 7, 5, "-00:00", "2008-07-05Z"},
            {2008, 7, 5, "-000000", "2008-07-05Z"},
            {2008, 7, 5, "-00:00:00", "2008-07-05Z"},
            {2008, 7, 5, "+01", "2008-07-05+01:00"},
            {2008, 7, 5, "+0100", "2008-07-05+01:00"},
            {2008, 7, 5, "+01:00", "2008-07-05+01:00"},
            {2008, 7, 5, "+010000", "2008-07-05+01:00"},
            {2008, 7, 5, "+01:00:00", "2008-07-05+01:00"},
            {2008, 7, 5, "+0130", "2008-07-05+01:30"},
            {2008, 7, 5, "+01:30", "2008-07-05+01:30"},
            {2008, 7, 5, "+013000", "2008-07-05+01:30"},
            {2008, 7, 5, "+01:30:00", "2008-07-05+01:30"},
            {2008, 7, 5, "+013040", "2008-07-05+01:30:40"},
            {2008, 7, 5, "+01:30:40", "2008-07-05+01:30:40"},
        };
    }

    @Test(dataProvider="sampleToString")
    public void test_toString(int y, int m, int d, String offsetId, String expected) {
        OffsetDate t = OffsetDate.of(LocalDate.of(y, m, d), ZoneOffset.of(offsetId));
        String str = t.toString();
        assertEquals(str, expected);
    }

    //-----------------------------------------------------------------------
    // toString(DateTimeFormatter)
    //-----------------------------------------------------------------------
    @Test
    public void test_toString_formatter() {
        DateTimeFormatter f = DateTimeFormatters.pattern("y M d");
        String t = OffsetDate.of(LocalDate.of(2010, 12, 3), OFFSET_PONE).toString(f);
        assertEquals(t, "2010 12 3");
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_toString_formatter_null() {
        OffsetDate.of(LocalDate.of(2010, 12, 3), OFFSET_PONE).toString(null);
    }

}
