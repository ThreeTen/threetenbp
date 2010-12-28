/*
 * Copyright (c) 2007-2010, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendar;

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
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import javax.time.CalendricalException;
import javax.time.Instant;
import javax.time.TimeSource;
import javax.time.calendar.format.CalendricalParseException;
import javax.time.calendar.format.DateTimeFormatters;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test OffsetDate.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestOffsetDate {
    private static final ZoneOffset OFFSET_PONE = ZoneOffset.ofHours(1);
    private static final ZoneOffset OFFSET_PTWO = ZoneOffset.ofHours(2);
    private static final TimeZone ZONE_PARIS = TimeZone.of("Europe/Paris");
    private static final TimeZone ZONE_GAZA = TimeZone.of("Asia/Gaza");
    
    private OffsetDate TEST_2007_07_15_PONE;
    private OffsetDate MAX_DATE;
    private OffsetDate MIN_DATE;
    private Instant MAX_INSTANT;
    private Instant MIN_INSTANT;

    @BeforeMethod
    public void setUp() {
        TEST_2007_07_15_PONE = OffsetDate.of(2007, 7, 15, OFFSET_PONE);
        
        OffsetDateTime max = OffsetDateTime.ofMidnight(Year.MAX_YEAR, 12, 31, ZoneOffset.UTC);
        OffsetDateTime min = OffsetDateTime.ofMidnight(Year.MIN_YEAR, 1, 1, ZoneOffset.UTC);
        MAX_DATE = max.toOffsetDate();
        MIN_DATE = min.toOffsetDate();
        MAX_INSTANT = max.toInstant();
        MIN_INSTANT = min.toInstant();
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        Object obj = TEST_2007_07_15_PONE;
        assertTrue(obj instanceof Calendrical);
        assertTrue(obj instanceof Serializable);
        assertTrue(obj instanceof Comparable<?>);
        assertTrue(obj instanceof DateProvider);
        assertTrue(obj instanceof CalendricalMatcher);
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(TEST_2007_07_15_PONE);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), TEST_2007_07_15_PONE);
    }

    public void test_immutable() {
        Class<OffsetDate> cls = OffsetDate.class;
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            assertTrue(Modifier.isPrivate(field.getModifiers()));
            assertTrue(Modifier.isFinal(field.getModifiers()));
        }
    }

    //-----------------------------------------------------------------------
    // now()
    //-----------------------------------------------------------------------
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

    //-----------------------------------------------------------------------
    // now(Clock)
    //-----------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class)
    public void now_Clock_nullClock() {
        OffsetDate.now(null);
    }

    public void now_Clock_allSecsInDay_utc() {
        for (int i = 0; i < (2 * 24 * 60 * 60); i++) {
            Instant instant = Instant.ofEpochSeconds(i);
            Clock clock = Clock.clock(TimeSource.fixed(instant), TimeZone.UTC);
            OffsetDate test = OffsetDate.now(clock);
            assertEquals(test.getYear(), 1970);
            assertEquals(test.getMonthOfYear(), MonthOfYear.JANUARY);
            assertEquals(test.getDayOfMonth(), (i < 24 * 60 * 60 ? 1 : 2));
            assertEquals(test.getOffset(), ZoneOffset.UTC);
        }
    }

    public void now_Clock_allSecsInDay_beforeEpoch() {
        for (int i =-1; i >= -(2 * 24 * 60 * 60); i--) {
            Instant instant = Instant.ofEpochSeconds(i);
            Clock clock = Clock.clock(TimeSource.fixed(instant), TimeZone.UTC);
            OffsetDate test = OffsetDate.now(clock);
            assertEquals(test.getYear(), 1969);
            assertEquals(test.getMonthOfYear(), MonthOfYear.DECEMBER);
            assertEquals(test.getDayOfMonth(), (i >= -24 * 60 * 60 ? 31 : 30));
            assertEquals(test.getOffset(), ZoneOffset.UTC);
        }
    }

    public void now_Clock_offsets() {
        OffsetDateTime base = OffsetDateTime.of(1970, 1, 1, 12, 0, ZoneOffset.UTC);
        for (int i = -9; i < 15; i++) {
            ZoneOffset offset = ZoneOffset.ofHours(i);
            Clock clock = Clock.clock(TimeSource.fixed(base.toInstant()), TimeZone.of(offset));
            OffsetDate test = OffsetDate.now(clock);
            assertEquals(test.getYear(), 1970);
            assertEquals(test.getMonthOfYear(), MonthOfYear.JANUARY);
            assertEquals(test.getDayOfMonth(), i >= 12 ? 2 : 1);
            assertEquals(test.getOffset(), offset);
        }
    }

    //-----------------------------------------------------------------------
    // factories
    //-----------------------------------------------------------------------
    void check(OffsetDate test, int y, int mo, int d, ZoneOffset offset) {
        assertEquals(test.getYear(), y);
        assertEquals(test.getMonthOfYear().getValue(), mo);
        assertEquals(test.getDayOfMonth(), d);
        assertEquals(test.getOffset(), offset);
    }

    //-----------------------------------------------------------------------
    public void factory_date_intMonthInt() {
        OffsetDate test = OffsetDate.of(2007, MonthOfYear.JULY, 15, OFFSET_PONE);
        check(test, 2007, 7, 15, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    public void factory_date_ints() {
        OffsetDate test = OffsetDate.of(2007, 7, 15, OFFSET_PONE);
        check(test, 2007, 7, 15, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    public void factory_date_intsMonthOffset() {
        assertEquals(TEST_2007_07_15_PONE, OffsetDate.of(2007, MonthOfYear.JULY, 15, OFFSET_PONE));
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_date_intsMonthOffset_dayTooLow() {
        OffsetDate.of(2007, MonthOfYear.JANUARY, 0, OFFSET_PONE);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_date_intsMonthOffset_dayTooHigh() {
        OffsetDate.of(2007, MonthOfYear.JANUARY, 32, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_date_intsMonthOffset_nullMonth() {
        OffsetDate.of(2007, null, 30, OFFSET_PONE);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_date_intsMonthOffset_yearTooLow() {
        OffsetDate.of(Integer.MIN_VALUE, MonthOfYear.JANUARY, 1, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_date_intsMonthOffset_nullOffset() {
        OffsetDate.of(2007, MonthOfYear.JANUARY, 30, null);
    }

    //-----------------------------------------------------------------------
    public void factory_date_intsOffset() {
        OffsetDate test = OffsetDate.of(2007, 7, 15, OFFSET_PONE);
        check(test, 2007, 7, 15, OFFSET_PONE);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_date_ints_dayTooLow() {
        OffsetDate.of(2007, 1, 0, OFFSET_PONE);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_date_ints_dayTooHigh() {
        OffsetDate.of(2007, 1, 32, OFFSET_PONE);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_date_ints_monthTooLow() {
        OffsetDate.of(2007, 0, 1, OFFSET_PONE);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_date_ints_monthTooHigh() {
        OffsetDate.of(2007, 13, 1, OFFSET_PONE);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_date_ints_yearTooLow() {
        OffsetDate.of(Integer.MIN_VALUE, 1, 1, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_date_ints_nullOffset() {
        OffsetDate.of(2007, 1, 1, (ZoneOffset) null);
    }

    //-----------------------------------------------------------------------
    public void factory_date_DateProvider() {
        DateProvider localDate = LocalDate.of(2008, 6, 30);
        OffsetDate test = OffsetDate.of(localDate, OFFSET_PONE);
        check(test, 2008, 6, 30, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_date_DateProvider_null() {
        OffsetDate.of(null, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_date_DateProvider_null_toLocalDate() {
        OffsetDate.of(new MockDateProviderReturnsNull(), OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    public void factory_date_multiProvider_checkAmbiguous() {
        MockMultiProvider mmp = new MockMultiProvider(2008, 6, 30, 11, 30, 10, 500);
        OffsetDate test = OffsetDate.of(mmp, OFFSET_PTWO);
        check(test, 2008, 6, 30, OFFSET_PTWO);
    }

    //-----------------------------------------------------------------------
    // fromInstant()
    //-----------------------------------------------------------------------
    public void factory_fromInstant_multiProvider_checkAmbiguous() {
        MockMultiProvider mmp = new MockMultiProvider(2008, 6, 30, 11, 30, 10, 500);
        OffsetDate test = OffsetDate.ofInstant(mmp, ZoneOffset.UTC);
        check(test, 2008, 6, 30, ZoneOffset.UTC);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_InstantProvider_nullInstant() {
        OffsetDate.ofInstant((Instant) null, ZoneOffset.UTC);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_InstantProvider_nullOffset() {
        Instant instant = Instant.ofEpochSeconds(0L);
        OffsetDate.ofInstant(instant, (ZoneOffset) null);
    }

    public void factory_fromInstant_InstantProvider_allSecsInDay_utc() {
        for (int i = 0; i < (2 * 24 * 60 * 60); i++) {
            Instant instant = Instant.ofEpochSeconds(i);
            OffsetDate test = OffsetDate.ofInstant(instant, ZoneOffset.UTC);
            assertEquals(test.getYear(), 1970);
            assertEquals(test.getMonthOfYear(), MonthOfYear.JANUARY);
            assertEquals(test.getDayOfMonth(), (i < 24 * 60 * 60 ? 1 : 2));
        }
    }

    public void factory_fromInstant_InstantProvider_allSecsInDay_offset() {
        for (int i = 0; i < (2 * 24 * 60 * 60); i++) {
            Instant instant = Instant.ofEpochSeconds(i);
            OffsetDate test = OffsetDate.ofInstant(instant.minusSeconds(OFFSET_PONE.getAmountSeconds()), OFFSET_PONE);
            assertEquals(test.getYear(), 1970);
            assertEquals(test.getMonthOfYear(), MonthOfYear.JANUARY);
            assertEquals(test.getDayOfMonth(), (i < 24 * 60 * 60) ? 1 : 2);
        }
    }

    public void factory_fromInstant_InstantProvider_beforeEpoch() {
        for (int i =-1; i >= -(24 * 60 * 60); i--) {
            Instant instant = Instant.ofEpochSeconds(i);
            OffsetDate test = OffsetDate.ofInstant(instant, ZoneOffset.UTC);
            assertEquals(test.getYear(), 1969);
            assertEquals(test.getMonthOfYear(), MonthOfYear.DECEMBER);
            assertEquals(test.getDayOfMonth(), 31);
        }
    }

    //-----------------------------------------------------------------------
    public void factory_fromInstant_InstantProvider_maxYear() {
        OffsetDate test = OffsetDate.ofInstant(MAX_INSTANT, ZoneOffset.UTC);
        assertEquals(test, MAX_DATE);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_fromInstant_InstantProvider_tooBig() {
        try {
            OffsetDate.ofInstant(MAX_INSTANT.plusSeconds(24 * 60 * 60), ZoneOffset.UTC);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), ISOChronology.yearRule());
            throw ex;
        }
    }

    public void factory_fromInstant_InstantProvider_minYear() {
        OffsetDate test = OffsetDate.ofInstant(MIN_INSTANT, ZoneOffset.UTC);
        assertEquals(test, MIN_DATE);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_fromInstant_InstantProvider_tooLow() {
        try {
            OffsetDate.ofInstant(MIN_INSTANT.minusNanos(1), ZoneOffset.UTC);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), ISOChronology.yearRule());
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // parse()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleToString")
    public void factory_parse_validText(int y, int m, int d, String offsetId, String parsable) {
        OffsetDate t = OffsetDate.parse(parsable);
        assertNotNull(t, parsable);
        assertEquals(t.getYear(), y, parsable);
        assertEquals(t.getMonthOfYear().getValue(), m, parsable);
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

    @Test(dataProvider="sampleBadParse", expectedExceptions={CalendricalParseException.class})
    public void factory_parse_invalidText(String unparsable) {
        OffsetDate.parse(unparsable);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_parse_illegalValue() {
        OffsetDate.parse("2008-06-32+01:00");
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
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
    public void factory_parse_formatter() {
        OffsetDate t = OffsetDate.parse("20101203+0100", DateTimeFormatters.pattern("yyyyMMddZ"));
        assertEquals(t, OffsetDate.of(2010, 12, 3, ZoneOffset.ofHours(1)));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_parse_formatter_nullText() {
        OffsetDate.parse((String) null, DateTimeFormatters.pattern("yyyyMMddZ"));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_parse_formatter_nullFormatter() {
        OffsetDate.parse("", null);
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
    public void test_getChronology() {
        assertSame(ISOChronology.INSTANCE, TEST_2007_07_15_PONE.getChronology());
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
    public void test_get(int y, int m, int d, ZoneOffset offset) {
        LocalDate localDate = LocalDate.of(y, m, d);
        OffsetDate a = OffsetDate.of(localDate, offset);
        assertSame(a.getOffset(), offset);
        assertEquals(a.getChronology(), ISOChronology.INSTANCE);
        
        assertEquals(a.getYear(), localDate.getYear());
        assertEquals(a.getMonthOfYear(), localDate.getMonthOfYear());
        assertEquals(a.getDayOfMonth(), localDate.getDayOfMonth());
        assertEquals(a.getDayOfYear(), localDate.getDayOfYear());
        assertEquals(a.getDayOfWeek(), localDate.getDayOfWeek());
        
        assertSame(a.toLocalDate(), localDate);
        assertEquals(a.toString(), localDate.toString() + offset.toString());
        assertEquals(a.getOffset(), offset);
    }

    @Test(dataProvider="sampleDates")
    public void test_getDOY(int y, int m, int d, ZoneOffset offset) {
        OffsetDate a = OffsetDate.of(y, m, d, offset);
        int total = 0;
        for (int i = 1; i < m; i++) {
            total += MonthOfYear.of(i).lengthInDays(ISOChronology.isLeapYear(y));
        }
        int doy = total + d;
        assertEquals(a.getDayOfYear(), doy);
    }

    //-----------------------------------------------------------------------
    // get(CalendricalRule)
    //-----------------------------------------------------------------------
    public void test_get_CalendricalRule() {
        OffsetDate test = OffsetDate.of(2008, 6, 30, OFFSET_PONE);
        assertEquals(test.get(Chronology.rule()), ISOChronology.INSTANCE);
        assertEquals(test.get(ISOChronology.yearRule()), (Integer) 2008);
        assertEquals(test.get(ISOChronology.quarterOfYearRule()), QuarterOfYear.Q2);
        assertEquals(test.get(ISOChronology.monthOfYearRule()), MonthOfYear.JUNE);
        assertEquals(test.get(ISOChronology.monthOfQuarterRule()), (Integer) 3);
        assertEquals(test.get(ISOChronology.dayOfMonthRule()),  (Integer) 30);
        assertEquals(test.get(ISOChronology.dayOfWeekRule()), DayOfWeek.MONDAY);
        assertEquals(test.get(ISOChronology.dayOfYearRule()),  (Integer) 182);
        assertEquals(test.get(ISOChronology.weekOfWeekBasedYearRule()), (Integer) 27);
        assertEquals(test.get(ISOChronology.weekBasedYearRule()), (Integer) 2008);
        
        assertEquals(test.get(ISOChronology.hourOfDayRule()), null);
        assertEquals(test.get(ISOChronology.minuteOfHourRule()), null);
        assertEquals(test.get(ISOChronology.secondOfMinuteRule()), null);
        assertEquals(test.get(ISOChronology.nanoOfSecondRule()), null);
        assertEquals(test.get(ISOChronology.hourOfAmPmRule()), null);
        assertEquals(test.get(ISOChronology.amPmOfDayRule()), null);
        
        assertEquals(test.get(LocalDate.rule()), test.toLocalDate());
        assertEquals(test.get(LocalTime.rule()), null);
        assertEquals(test.get(LocalDateTime.rule()), null);
        assertEquals(test.get(OffsetDate.rule()), test);
        assertEquals(test.get(OffsetTime.rule()), null);
        assertEquals(test.get(OffsetDateTime.rule()), null);
        assertEquals(test.get(ZonedDateTime.rule()), null);
        assertEquals(test.get(ZoneOffset.rule()), test.getOffset());
        assertEquals(test.get(TimeZone.rule()), null);
        assertEquals(test.get(YearMonth.rule()), YearMonth.of(2008, 6));
        assertEquals(test.get(MonthDay.rule()), MonthDay.of(6, 30));
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_get_CalendricalRule_null() {
        OffsetDate test = OffsetDate.of(2008, 6, 30, OFFSET_PONE);
        test.get((CalendricalRule<?>) null);
    }

    public void test_get_unsupported() {
        OffsetDate test = OffsetDate.of(2008, 6, 30, OFFSET_PONE);
        assertEquals(test.get(MockRuleNoValue.INSTANCE), null);
    }

    //-----------------------------------------------------------------------
    // getDayOfWeek()
    //-----------------------------------------------------------------------
    public void test_getDayOfWeek() {
        DayOfWeek dow = DayOfWeek.MONDAY;
        ZoneOffset[] offsets = new ZoneOffset[] {OFFSET_PONE, OFFSET_PTWO};

        for (MonthOfYear month : MonthOfYear.values()) {
            int length = month.lengthInDays(false);
            for (int i = 1; i <= length; i++) {
                OffsetDate d = OffsetDate.of(2007, month, i, offsets[i % 2]);
                assertSame(d.getDayOfWeek(), dow);
                dow = dow.next();
            }
        }
    }

    //-----------------------------------------------------------------------
    // isLeapYear()
    //-----------------------------------------------------------------------
    public void test_isLeapYear() {
        assertEquals(OffsetDate.of(1999, 1, 1, OFFSET_PONE).isLeapYear(), false);
        assertEquals(OffsetDate.of(2000, 1, 1, OFFSET_PONE).isLeapYear(), true);
        assertEquals(OffsetDate.of(2001, 1, 1, OFFSET_PONE).isLeapYear(), false);
        assertEquals(OffsetDate.of(2002, 1, 1, OFFSET_PONE).isLeapYear(), false);
        assertEquals(OffsetDate.of(2003, 1, 1, OFFSET_PONE).isLeapYear(), false);
        assertEquals(OffsetDate.of(2004, 1, 1, OFFSET_PONE).isLeapYear(), true);
        assertEquals(OffsetDate.of(2005, 1, 1, OFFSET_PONE).isLeapYear(), false);
    }

    //-----------------------------------------------------------------------
    // withDate()
    //-----------------------------------------------------------------------
    public void test_withDate() {
        OffsetDate base = OffsetDate.of(2008, 6, 30, OFFSET_PONE);
        LocalDate date = LocalDate.of(2008, 7, 1);
        OffsetDate test = base.withDate(date);
        assertSame(test.toLocalDate(), date);
        assertSame(test.getOffset(), base.getOffset());
    }

    public void test_withDate_noChange() {
        OffsetDate base = OffsetDate.of(2008, 6, 30, OFFSET_PONE);
        LocalDate date = LocalDate.of(2008, 6, 30);
        OffsetDate test = base.withDate(date);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_withDate_null() {
        OffsetDate base = OffsetDate.of(2008, 6, 30, OFFSET_PONE);
        base.withDate(null);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_withDate_badProvider() {
        OffsetDate base = OffsetDate.of(2008, 6, 30, OFFSET_PONE);
        base.withDate(new MockDateProviderReturnsNull());
    }

    //-----------------------------------------------------------------------
    // withOffset()
    //-----------------------------------------------------------------------
    public void test_withOffset() {
        OffsetDate base = OffsetDate.of(2008, 6, 30, OFFSET_PONE);
        OffsetDate test = base.withOffset(OFFSET_PTWO);
        assertSame(test.toLocalDate(), base.toLocalDate());
        assertSame(test.getOffset(), OFFSET_PTWO);
    }

    public void test_withOffset_noChange() {
        OffsetDate base = OffsetDate.of(2008, 6, 30, OFFSET_PONE);
        OffsetDate test = base.withOffset(OFFSET_PONE);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_withOffset_null() {
        OffsetDate base = OffsetDate.of(2008, 6, 30, OFFSET_PONE);
        base.withOffset(null);
    }

    //-----------------------------------------------------------------------
    // with(DateAdjuster)
    //-----------------------------------------------------------------------
    public void test_with() {
        OffsetDate base = OffsetDate.of(2008, 6, 30, OFFSET_PONE);
        OffsetDate test = base.with(Year.of(2007));
        assertEquals(test.toLocalDate(), LocalDate.of(2007, 6, 30));
        assertSame(test.getOffset(), base.getOffset());
    }

    public void test_with_noChange() {
        LocalDate date = LocalDate.of(2008, 6, 30);
        OffsetDate base = OffsetDate.of(date, OFFSET_PONE);
        OffsetDate test = base.with(date);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_with_null() {
        OffsetDate base = OffsetDate.of(2008, 6, 30, OFFSET_PONE);
        base.with((DateAdjuster) null);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_with_badAdjuster() {
        OffsetDate base = OffsetDate.of(2008, 6, 30, OFFSET_PONE);
        base.with(new MockDateAdjusterReturnsNull());
    }

    //-----------------------------------------------------------------------
    // withYear()
    //-----------------------------------------------------------------------
    public void test_withYear_int_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.withYear(2008);
        assertEquals(t, OffsetDate.of(2008, 7, 15, OFFSET_PONE));
    }

    public void test_withYear_int_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.withYear(2007);
        assertSame(t, TEST_2007_07_15_PONE);
    }
    
    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withYear_int_invalid() {
        TEST_2007_07_15_PONE.withYear(Year.MIN_YEAR - 1);
    }

    public void test_withYear_int_adjustDay() {
        OffsetDate t = OffsetDate.of(2008, 2, 29, OFFSET_PONE).withYear(2007);
        OffsetDate expected = OffsetDate.of(2007, 2, 28, OFFSET_PONE);
        assertEquals(t, expected);
    }

    public void test_withYear_int_DateResolver_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.withYear(2008, DateResolvers.strict());
        assertEquals(t, OffsetDate.of(2008, 7, 15, OFFSET_PONE));
    }

    public void test_withYear_int_DateResolver_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.withYear(2007, DateResolvers.strict());
        assertSame(t, TEST_2007_07_15_PONE);
    }
    
    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withYear_int_DateResolver_invalid() {
        TEST_2007_07_15_PONE.withYear(Year.MIN_YEAR - 1, DateResolvers.nextValid());
    }

    public void test_withYear_int_DateResolver_adjustDay() {
        OffsetDate t = OffsetDate.of(2008, 2, 29, OFFSET_PONE).withYear(2007, DateResolvers.nextValid());
        OffsetDate expected = OffsetDate.of(2007, 3, 1, OFFSET_PONE);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_withYear_int_DateResolver_null_adjustDay() {
        TEST_2007_07_15_PONE.withYear(2008, new MockDateResolverReturnsNull());
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_withYear_int_DateResolver_adjustDay_invalid() {
        OffsetDate.of(2008, 2, 29, OFFSET_PONE).withYear(2007, DateResolvers.strict());
    }

    //-----------------------------------------------------------------------
    // withMonthOfYear()
    //-----------------------------------------------------------------------
    public void test_withMonthOfYear_int_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.withMonthOfYear(1);
        assertEquals(t, OffsetDate.of(2007, 1, 15, OFFSET_PONE));
    }

    public void test_withMonthOfYear_int_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.withMonthOfYear(7);
        assertSame(t, TEST_2007_07_15_PONE);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withMonthOfYear_int_invalid() {
        TEST_2007_07_15_PONE.withMonthOfYear(13);
    }

    public void test_withMonthOfYear_int_adjustDay() {
        OffsetDate t = OffsetDate.of(2007, 12, 31, OFFSET_PONE).withMonthOfYear(11);
        OffsetDate expected = OffsetDate.of(2007, 11, 30, OFFSET_PONE);
        assertEquals(t, expected);
    }

    public void test_withMonthOfYear_int_DateResolver_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.withMonthOfYear(1, DateResolvers.strict());
        assertEquals(t, OffsetDate.of(2007, 1, 15, OFFSET_PONE));
    }

    public void test_withMonthOfYear_int_DateResolver_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.withMonthOfYear(7, DateResolvers.strict());
        assertSame(t, TEST_2007_07_15_PONE);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withMonthOfYear_int_DateResolver_invalid() {
        TEST_2007_07_15_PONE.withMonthOfYear(13, DateResolvers.nextValid());
    }

    public void test_withMonthOfYear_int_DateResolver_adjustDay() {
        OffsetDate t = OffsetDate.of(2007, 12, 31, OFFSET_PONE).withMonthOfYear(11, DateResolvers.nextValid());
        OffsetDate expected = OffsetDate.of(2007, 12, 1, OFFSET_PONE);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_withMonthOfYear_int_DateResolver_null_adjustDay() {
        TEST_2007_07_15_PONE.withMonthOfYear(1, new MockDateResolverReturnsNull());
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_withMonthOfYear_int_DateResolver_adjustDay_invalid() {
        OffsetDate.of(2007, 12, 31, OFFSET_PONE).withMonthOfYear(11, DateResolvers.strict());
    }

    //-----------------------------------------------------------------------
    // withDayOfMonth()
    //-----------------------------------------------------------------------
    public void test_withDayOfMonth_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.withDayOfMonth(1);
        assertEquals(t, OffsetDate.of(2007, 7, 1, OFFSET_PONE));
    }

    public void test_withDayOfMonth_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.withDayOfMonth(15);
        assertEquals(t, OffsetDate.of(2007, 7, 15, OFFSET_PONE));
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_withDayOfMonth_invalidForMonth() {
        try {
            OffsetDate.of(2007, 11, 30, OFFSET_PONE).withDayOfMonth(31);
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getRule(), ISOChronology.dayOfMonthRule());
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withDayOfMonth_invalidAlways() {
        try {
            OffsetDate.of(2007, 11, 30, OFFSET_PONE).withDayOfMonth(32);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), ISOChronology.dayOfMonthRule());
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // withDayOfYear(int)
    //-----------------------------------------------------------------------
    public void test_withDayOfYear_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.withDayOfYear(33);
        assertEquals(t, OffsetDate.of(2007, 2, 2, OFFSET_PONE));
    }

    public void test_withDayOfYear_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.withDayOfYear(31 + 28 + 31 + 30 + 31 + 30 + 15);
        assertSame(t, TEST_2007_07_15_PONE);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withDayOfYear_illegal() {
        TEST_2007_07_15_PONE.withDayOfYear(367);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_withDayOfYear_invalid() {
        TEST_2007_07_15_PONE.withDayOfYear(366);
    }

    //-----------------------------------------------------------------------
    // plus(PeriodProvider)
    //-----------------------------------------------------------------------
    public void test_plus_PeriodProvider() {
        PeriodProvider provider = Period.ofDateFields(1, 2, 3);
        OffsetDate t = TEST_2007_07_15_PONE.plus(provider);
        assertEquals(t, OffsetDate.of(2008, 9, 18, OFFSET_PONE));
    }

    public void test_plus_PeriodProvider_zero() {
        OffsetDate t = TEST_2007_07_15_PONE.plus(Period.ZERO);
        assertSame(t, TEST_2007_07_15_PONE);
    }

    //-----------------------------------------------------------------------
    // plusYears()
    //-----------------------------------------------------------------------
    public void test_plusYears_int_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.plusYears(1);
        assertEquals(t, OffsetDate.of(2008, 7, 15, OFFSET_PONE));
    }

    public void test_plusYears_int_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.plusYears(0);
        assertSame(t, TEST_2007_07_15_PONE);
    }

    public void test_plusYears_int_negative() {
        OffsetDate t = TEST_2007_07_15_PONE.plusYears(-1);
        assertEquals(t, OffsetDate.of(2006, 7, 15, OFFSET_PONE));
    }

    public void test_plusYears_int_adjustDay() {
        OffsetDate t = OffsetDate.of(2008, 2, 29, OFFSET_PONE).plusYears(1);
        OffsetDate expected = OffsetDate.of(2009, 2, 28, OFFSET_PONE);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusYears_int_invalidTooLarge() {
        OffsetDate.of(Year.MAX_YEAR, 1, 1, OFFSET_PONE).plusYears(1);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusYears_int_invalidTooSmall() {
        OffsetDate.of(Year.MIN_YEAR, 1, 1, OFFSET_PONE).plusYears(-1);
    }

    public void test_plusYears_int_DateResolver_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.plusYears(1, DateResolvers.nextValid());
        assertEquals(t, OffsetDate.of(2008, 7, 15, OFFSET_PONE));
    }

    public void test_plusYears_int_DateResolver_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.plusYears(0, DateResolvers.nextValid());
        assertSame(t, TEST_2007_07_15_PONE);
    }

    public void test_plusYears_int_DateResolver_negative() {
        OffsetDate t = TEST_2007_07_15_PONE.plusYears(-1, DateResolvers.nextValid());
        assertEquals(t, OffsetDate.of(2006, 7, 15, OFFSET_PONE));
    }

    public void test_plusYears_int_DateResolver_adjustDay() {
        OffsetDate t = OffsetDate.of(2008, 2, 29, OFFSET_PONE).plusYears(1, DateResolvers.nextValid());
        OffsetDate expected = OffsetDate.of(2009, 3, 1, OFFSET_PONE);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_plusYears_int_DateResolver_null_adjustDay() {
        TEST_2007_07_15_PONE.plusYears(1, new MockDateResolverReturnsNull());
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusYears_int_DateResolver_invalidTooLarge() {
        OffsetDate.of(Year.MAX_YEAR, 1, 1, OFFSET_PONE).plusYears(1, DateResolvers.nextValid());
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusYears_int_DateResolver_invalidTooSmall() {
         OffsetDate.of(Year.MIN_YEAR, 1, 1, OFFSET_PONE).plusYears(-1, DateResolvers.nextValid());
    }

    //-----------------------------------------------------------------------
    // plusMonths()
    //-----------------------------------------------------------------------
    public void test_plusMonths_int_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.plusMonths(1);
        assertEquals(t, OffsetDate.of(2007, 8, 15, OFFSET_PONE));
    }

    public void test_plusMonths_int_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.plusMonths(0);
        assertSame(t, TEST_2007_07_15_PONE);
    }

    public void test_plusMonths_int_overYears() {
        OffsetDate t = TEST_2007_07_15_PONE.plusMonths(25);
        assertEquals(t, OffsetDate.of(2009, 8, 15, OFFSET_PONE));
    }

    public void test_plusMonths_int_negative() {
        OffsetDate t = TEST_2007_07_15_PONE.plusMonths(-1);
        assertEquals(t, OffsetDate.of(2007, 6, 15, OFFSET_PONE));
    }

    public void test_plusMonths_int_negativeAcrossYear() {
        OffsetDate t = TEST_2007_07_15_PONE.plusMonths(-7);
        assertEquals(t, OffsetDate.of(2006, 12, 15, OFFSET_PONE));
    }

    public void test_plusMonths_int_negativeOverYears() {
        OffsetDate t = TEST_2007_07_15_PONE.plusMonths(-31);
        assertEquals(t, OffsetDate.of(2004, 12, 15, OFFSET_PONE));
    }

    public void test_plusMonths_int_adjustDayFromLeapYear() {
        OffsetDate t = OffsetDate.of(2008, 2, 29, OFFSET_PONE).plusMonths(12);
        OffsetDate expected = OffsetDate.of(2009, 2, 28, OFFSET_PONE);
        assertEquals(t, expected);
    }

    public void test_plusMonths_int_adjustDayFromMonthLength() {
        OffsetDate t = OffsetDate.of(2007, 3, 31, OFFSET_PONE).plusMonths(1);
        OffsetDate expected = OffsetDate.of(2007, 4, 30, OFFSET_PONE);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_plusMonths_int_invalidTooLarge() {
        OffsetDate.of(Year.MAX_YEAR, 12, 1, OFFSET_PONE).plusMonths(1);
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_plusMonths_int_invalidTooSmall() {
        OffsetDate.of(Year.MIN_YEAR, 1, 1, OFFSET_PONE).plusMonths(-1);
    }

    public void test_plusMonths_int_DateResolver_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.plusMonths(1, DateResolvers.nextValid());
        assertEquals(t, OffsetDate.of(2007, 8, 15, OFFSET_PONE));
    }

    public void test_plusMonths_int_DateResolver_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.plusMonths(0, DateResolvers.nextValid());
        assertSame(t, TEST_2007_07_15_PONE);
    }

    public void test_plusMonths_int_DateResolver_overYears() {
        OffsetDate t = TEST_2007_07_15_PONE.plusMonths(25, DateResolvers.nextValid());
        assertEquals(t, OffsetDate.of(2009, 8, 15, OFFSET_PONE));
    }

    public void test_plusMonths_int_DateResolver_negative() {
        OffsetDate t = TEST_2007_07_15_PONE.plusMonths(-1, DateResolvers.nextValid());
        assertEquals(t, OffsetDate.of(2007, 6, 15, OFFSET_PONE));
    }

    public void test_plusMonths_int_DateResolver_negativeAcrossYear() {
        OffsetDate t = TEST_2007_07_15_PONE.plusMonths(-7, DateResolvers.nextValid());
        assertEquals(t, OffsetDate.of(2006, 12, 15, OFFSET_PONE));
    }

    public void test_plusMonths_int_DateResolver_negativeOverYears() {
        OffsetDate t = TEST_2007_07_15_PONE.plusMonths(-31, DateResolvers.nextValid());
        assertEquals(t, OffsetDate.of(2004, 12, 15, OFFSET_PONE));
    }

    public void test_plusMonths_int_DateResolver_adjustDayFromLeapYear() {
        OffsetDate t = OffsetDate.of(2008, 2, 29, OFFSET_PONE).plusMonths(12, DateResolvers.nextValid());
        OffsetDate expected = OffsetDate.of(2009, 3, 1, OFFSET_PONE);
        assertEquals(t, expected);
    }

    public void test_plusMonths_int_DateResolver_adjustDayFromMonthLength() {
        OffsetDate t = OffsetDate.of(2007, 3, 31, OFFSET_PONE).plusMonths(1, DateResolvers.nextValid());
        OffsetDate expected = OffsetDate.of(2007, 5, 1, OFFSET_PONE);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_plusMonths_int_DateResolver_null_adjustDay() {
        TEST_2007_07_15_PONE.plusMonths(1, new MockDateResolverReturnsNull());
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_plusMonths_int_DateResolver_invalidTooLarge() {
        OffsetDate.of(Year.MAX_YEAR, 12, 1, OFFSET_PONE).plusMonths(1, DateResolvers.nextValid());
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_plusMonths_int_DateResolver_invalidTooSmall() {
        OffsetDate.of(Year.MIN_YEAR, 1, 1, OFFSET_PONE).plusMonths(-1, DateResolvers.nextValid());
    }

    //-----------------------------------------------------------------------
    // plusWeeks()
    //-----------------------------------------------------------------------
    @DataProvider(name="samplePlusWeeksSymmetry")
    Object[][] provider_samplePlusWeeksSymmetry() {
        return new Object[][] {
            {OffsetDate.of(-1, 1, 1, OFFSET_PONE)},
            {OffsetDate.of(-1, 2, 28, OFFSET_PTWO)},
            {OffsetDate.of(-1, 3, 1, OFFSET_PONE)},
            {OffsetDate.of(-1, 12, 31, OFFSET_PTWO)},
            {OffsetDate.of(0, 1, 1, OFFSET_PONE)},
            {OffsetDate.of(0, 2, 28, OFFSET_PTWO)},
            {OffsetDate.of(0, 2, 29, OFFSET_PTWO)},
            {OffsetDate.of(0, 3, 1, OFFSET_PONE)},
            {OffsetDate.of(0, 12, 31, OFFSET_PTWO)},
            {OffsetDate.of(2007, 1, 1, OFFSET_PONE)},
            {OffsetDate.of(2007, 2, 28, OFFSET_PTWO)},
            {OffsetDate.of(2007, 3, 1, OFFSET_PONE)},
            {OffsetDate.of(2007, 12, 31, OFFSET_PTWO)},
            {OffsetDate.of(2008, 1, 1, OFFSET_PONE)},
            {OffsetDate.of(2008, 2, 28, OFFSET_PTWO)},
            {OffsetDate.of(2008, 2, 29, OFFSET_PTWO)},
            {OffsetDate.of(2008, 3, 1, OFFSET_PONE)},
            {OffsetDate.of(2008, 12, 31, OFFSET_PTWO)},
            {OffsetDate.of(2099, 1, 1, OFFSET_PONE)},
            {OffsetDate.of(2099, 2, 28, OFFSET_PTWO)},
            {OffsetDate.of(2099, 3, 1, OFFSET_PONE)},
            {OffsetDate.of(2099, 12, 31, OFFSET_PTWO)},
            {OffsetDate.of(2100, 1, 1, OFFSET_PONE)},
            {OffsetDate.of(2100, 2, 28, OFFSET_PTWO)},
            {OffsetDate.of(2100, 3, 1, OFFSET_PONE)},
            {OffsetDate.of(2100, 12, 31, OFFSET_PTWO)},
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

    public void test_plusWeeks_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.plusWeeks(1);
        assertEquals(t, OffsetDate.of(2007, 7, 22, OFFSET_PONE));
    }

    public void test_plusWeeks_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.plusWeeks(0);
        assertSame(t, TEST_2007_07_15_PONE);
    }

    public void test_plusWeeks_overMonths() {
        OffsetDate t = TEST_2007_07_15_PONE.plusWeeks(9);
        assertEquals(t, OffsetDate.of(2007, 9, 16, OFFSET_PONE));
    }

    public void test_plusWeeks_overYears() {
        OffsetDate t = OffsetDate.of(2006, 7, 16, OFFSET_PONE).plusWeeks(52);
        assertEquals(t, TEST_2007_07_15_PONE);
    }

    public void test_plusWeeks_overLeapYears() {
        OffsetDate t = TEST_2007_07_15_PONE.plusYears(-1).plusWeeks(104);
        assertEquals(t, OffsetDate.of(2008, 7, 12, OFFSET_PONE));
    }

    public void test_plusWeeks_negative() {
        OffsetDate t = TEST_2007_07_15_PONE.plusWeeks(-1);
        assertEquals(t, OffsetDate.of(2007, 7, 8, OFFSET_PONE));
    }

    public void test_plusWeeks_negativeAcrossYear() {
        OffsetDate t = TEST_2007_07_15_PONE.plusWeeks(-28);
        assertEquals(t, OffsetDate.of(2006, 12, 31, OFFSET_PONE));
    }

    public void test_plusWeeks_negativeOverYears() {
        OffsetDate t = TEST_2007_07_15_PONE.plusWeeks(-104);
        assertEquals(t, OffsetDate.of(2005, 7, 17, OFFSET_PONE));
    }

    public void test_plusWeeks_maximum() {
        OffsetDate t = OffsetDate.of(Year.MAX_YEAR, 12, 24, OFFSET_PONE).plusWeeks(1);
        OffsetDate expected = OffsetDate.of(Year.MAX_YEAR, 12, 31, OFFSET_PONE);
        assertEquals(t, expected);
    }

    public void test_plusWeeks_minimum() {
        OffsetDate t = OffsetDate.of(Year.MIN_YEAR, 1, 8, OFFSET_PONE).plusWeeks(-1);
        OffsetDate expected = OffsetDate.of(Year.MIN_YEAR, 1, 1, OFFSET_PONE);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_plusWeeks_invalidTooLarge() {
        OffsetDate.of(Year.MAX_YEAR, 12, 25, OFFSET_PONE).plusWeeks(1);
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_plusWeeks_invalidTooSmall() {
        OffsetDate.of(Year.MIN_YEAR, 1, 7, OFFSET_PONE).plusWeeks(-1);
    }

    //-----------------------------------------------------------------------
    // plusDays()
    //-----------------------------------------------------------------------
    @DataProvider(name="samplePlusDaysSymmetry")
    Object[][] provider_samplePlusDaysSymmetry() {
        return new Object[][] {
            {OffsetDate.of(-1, 1, 1, OFFSET_PONE)},
            {OffsetDate.of(-1, 2, 28, OFFSET_PTWO)},
            {OffsetDate.of(-1, 3, 1, OFFSET_PONE)},
            {OffsetDate.of(-1, 12, 31, OFFSET_PTWO)},
            {OffsetDate.of(0, 1, 1, OFFSET_PONE)},
            {OffsetDate.of(0, 2, 28, OFFSET_PTWO)},
            {OffsetDate.of(0, 2, 29, OFFSET_PTWO)},
            {OffsetDate.of(0, 3, 1, OFFSET_PONE)},
            {OffsetDate.of(0, 12, 31, OFFSET_PTWO)},
            {OffsetDate.of(2007, 1, 1, OFFSET_PONE)},
            {OffsetDate.of(2007, 2, 28, OFFSET_PTWO)},
            {OffsetDate.of(2007, 3, 1, OFFSET_PONE)},
            {OffsetDate.of(2007, 12, 31, OFFSET_PTWO)},
            {OffsetDate.of(2008, 1, 1, OFFSET_PONE)},
            {OffsetDate.of(2008, 2, 28, OFFSET_PTWO)},
            {OffsetDate.of(2008, 2, 29, OFFSET_PTWO)},
            {OffsetDate.of(2008, 3, 1, OFFSET_PONE)},
            {OffsetDate.of(2008, 12, 31, OFFSET_PTWO)},
            {OffsetDate.of(2099, 1, 1, OFFSET_PONE)},
            {OffsetDate.of(2099, 2, 28, OFFSET_PTWO)},
            {OffsetDate.of(2099, 3, 1, OFFSET_PONE)},
            {OffsetDate.of(2099, 12, 31, OFFSET_PTWO)},
            {OffsetDate.of(2100, 1, 1, OFFSET_PONE)},
            {OffsetDate.of(2100, 2, 28, OFFSET_PTWO)},
            {OffsetDate.of(2100, 3, 1, OFFSET_PONE)},
            {OffsetDate.of(2100, 12, 31, OFFSET_PTWO)},
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

    public void test_plusDays_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.plusDays(1);
        assertEquals(t, OffsetDate.of(2007, 7, 16, OFFSET_PONE));
    }

    public void test_plusDays_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.plusDays(0);
        assertSame(t, TEST_2007_07_15_PONE);
    }

    public void test_plusDays_overMonths() {
        OffsetDate t = TEST_2007_07_15_PONE.plusDays(62);
        assertEquals(t, OffsetDate.of(2007, 9, 15, OFFSET_PONE));
    }

    public void test_plusDays_overYears() {
        OffsetDate t = OffsetDate.of(2006, 7, 14, OFFSET_PONE).plusDays(366);
        assertEquals(t, TEST_2007_07_15_PONE);
    }

    public void test_plusDays_overLeapYears() {
        OffsetDate t = TEST_2007_07_15_PONE.plusYears(-1).plusDays(365 + 366);
        assertEquals(t, OffsetDate.of(2008, 7, 15, OFFSET_PONE));
    }

    public void test_plusDays_negative() {
        OffsetDate t = TEST_2007_07_15_PONE.plusDays(-1);
        assertEquals(t, OffsetDate.of(2007, 7, 14, OFFSET_PONE));
    }

    public void test_plusDays_negativeAcrossYear() {
        OffsetDate t = TEST_2007_07_15_PONE.plusDays(-196);
        assertEquals(t, OffsetDate.of(2006, 12, 31, OFFSET_PONE));
    }

    public void test_plusDays_negativeOverYears() {
        OffsetDate t = TEST_2007_07_15_PONE.plusDays(-730);
        assertEquals(t, OffsetDate.of(2005, 7, 15, OFFSET_PONE));
    }

    public void test_plusDays_maximum() {
        OffsetDate t = OffsetDate.of(Year.MAX_YEAR, 12, 30, OFFSET_PONE).plusDays(1);
        OffsetDate expected = OffsetDate.of(Year.MAX_YEAR, 12, 31, OFFSET_PONE);
        assertEquals(t, expected);
    }

    public void test_plusDays_minimum() {
        OffsetDate t = OffsetDate.of(Year.MIN_YEAR, 1, 2, OFFSET_PONE).plusDays(-1);
        OffsetDate expected = OffsetDate.of(Year.MIN_YEAR, 1, 1, OFFSET_PONE);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_plusDays_invalidTooLarge() {
        OffsetDate.of(Year.MAX_YEAR, 12, 31, OFFSET_PONE).plusDays(1);
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_plusDays_invalidTooSmall() {
        OffsetDate.of(Year.MIN_YEAR, 1, 1, OFFSET_PONE).plusDays(-1);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusDays_overflowTooLarge() {
        OffsetDate.of(Year.MAX_YEAR, 12, 31, OFFSET_PONE).plusDays(Long.MAX_VALUE);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusDays_overflowTooSmall() {
        OffsetDate.of(Year.MIN_YEAR, 1, 1, OFFSET_PONE).plusDays(Long.MIN_VALUE);
    }

    //-----------------------------------------------------------------------
    // minus(PeriodProvider)
    //-----------------------------------------------------------------------
    public void test_minus_PeriodProvider() {
        PeriodProvider provider = Period.ofDateFields(1, 2, 3);
        OffsetDate t = TEST_2007_07_15_PONE.minus(provider);
        assertEquals(t, OffsetDate.of(2006, 5, 12, OFFSET_PONE));
    }

    public void test_minus_PeriodProvider_zero() {
        OffsetDate t = TEST_2007_07_15_PONE.minus(Period.ZERO);
        assertSame(t, TEST_2007_07_15_PONE);
    }

    //-----------------------------------------------------------------------
    // minusYears()
    //-----------------------------------------------------------------------
    public void test_minusYears_int_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.minusYears(1);
        assertEquals(t, OffsetDate.of(2006, 7, 15, OFFSET_PONE));
    }

    public void test_minusYears_int_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.minusYears(0);
        assertSame(t, TEST_2007_07_15_PONE);
    }

    public void test_minusYears_int_negative() {
        OffsetDate t = TEST_2007_07_15_PONE.minusYears(-1);
        assertEquals(t, OffsetDate.of(2008, 7, 15, OFFSET_PONE));
    }

    public void test_minusYears_int_adjustDay() {
        OffsetDate t = OffsetDate.of(2008, 2, 29, OFFSET_PONE).minusYears(1);
        OffsetDate expected = OffsetDate.of(2007, 2, 28, OFFSET_PONE);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusYears_int_invalidTooLarge() {
        OffsetDate.of(Year.MAX_YEAR, 1, 1, OFFSET_PONE).minusYears(-1);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusYears_int_invalidTooSmall() {
        OffsetDate.of(Year.MIN_YEAR, 1, 1, OFFSET_PONE).minusYears(1);
    }

    public void test_minusYears_int_DateResolver_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.minusYears(1, DateResolvers.nextValid());
        assertEquals(t, OffsetDate.of(2006, 7, 15, OFFSET_PONE));
    }

    public void test_minusYears_int_DateResolver_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.minusYears(0, DateResolvers.nextValid());
        assertSame(t, TEST_2007_07_15_PONE);
    }

    public void test_minusYears_int_DateResolver_negative() {
        OffsetDate t = TEST_2007_07_15_PONE.minusYears(-1, DateResolvers.nextValid());
        assertEquals(t, OffsetDate.of(2008, 7, 15, OFFSET_PONE));
    }

    public void test_minusYears_int_DateResolver_adjustDay() {
        OffsetDate t = OffsetDate.of(2008, 2, 29, OFFSET_PONE).minusYears(1, DateResolvers.nextValid());
        OffsetDate expected = OffsetDate.of(2007, 3, 1, OFFSET_PONE);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_minusYears_int_DateResolver_null_adjustDay() {
        TEST_2007_07_15_PONE.minusYears(1, new MockDateResolverReturnsNull());
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_minusYears_int_DateResolver_invalidTooLarge() {
        OffsetDate.of(Year.MAX_YEAR, 1, 1, OFFSET_PONE).minusYears(-1, DateResolvers.nextValid());
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_minusYears_int_DateResolver_invalidTooSmall() {
        OffsetDate.of(Year.MIN_YEAR, 1, 1, OFFSET_PONE).minusYears(1, DateResolvers.nextValid());
    }

    //-----------------------------------------------------------------------
    // minusMonths()
    //-----------------------------------------------------------------------
    public void test_minusMonths_int_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.minusMonths(1);
        assertEquals(t, OffsetDate.of(2007, 6, 15, OFFSET_PONE));
    }

    public void test_minusMonths_int_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.minusMonths(0);
        assertSame(t, TEST_2007_07_15_PONE);
    }

    public void test_minusMonths_int_overYears() {
        OffsetDate t = TEST_2007_07_15_PONE.minusMonths(25);
        assertEquals(t, OffsetDate.of(2005, 6, 15, OFFSET_PONE));
    }

    public void test_minusMonths_int_negative() {
        OffsetDate t = TEST_2007_07_15_PONE.minusMonths(-1);
        assertEquals(t, OffsetDate.of(2007, 8, 15, OFFSET_PONE));
    }

    public void test_minusMonths_int_negativeAcrossYear() {
        OffsetDate t = TEST_2007_07_15_PONE.minusMonths(-7);
        assertEquals(t, OffsetDate.of(2008, 2, 15, OFFSET_PONE));
    }

    public void test_minusMonths_int_negativeOverYears() {
        OffsetDate t = TEST_2007_07_15_PONE.minusMonths(-31);
        assertEquals(t, OffsetDate.of(2010, 2, 15, OFFSET_PONE));
    }

    public void test_minusMonths_int_adjustDayFromLeapYear() {
        OffsetDate t = OffsetDate.of(2008, 2, 29, OFFSET_PONE).minusMonths(12);
        OffsetDate expected = OffsetDate.of(2007, 2, 28, OFFSET_PONE);
        assertEquals(t, expected);
    }

    public void test_minusMonths_int_adjustDayFromMonthLength() {
        OffsetDate t = OffsetDate.of(2007, 3, 31, OFFSET_PONE).minusMonths(1);
        OffsetDate expected = OffsetDate.of(2007, 2, 28, OFFSET_PONE);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_minusMonths_int_invalidTooLarge() {
        OffsetDate.of(Year.MAX_YEAR, 12, 1, OFFSET_PONE).minusMonths(-1);
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_minusMonths_int_invalidTooSmall() {
        OffsetDate.of(Year.MIN_YEAR, 1, 1, OFFSET_PONE).minusMonths(1);
    }

    public void test_minusMonths_int_DateResolver_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.minusMonths(1, DateResolvers.nextValid());
        assertEquals(t, OffsetDate.of(2007, 6, 15, OFFSET_PONE));
    }

    public void test_minusMonths_int_DateResolver_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.minusMonths(0, DateResolvers.nextValid());
        assertSame(t, TEST_2007_07_15_PONE);
    }

    public void test_minusMonths_int_DateResolver_overYears() {
        OffsetDate t = TEST_2007_07_15_PONE.minusMonths(25, DateResolvers.nextValid());
        assertEquals(t, OffsetDate.of(2005, 6, 15, OFFSET_PONE));
    }

    public void test_minusMonths_int_DateResolver_negative() {
        OffsetDate t = TEST_2007_07_15_PONE.minusMonths(-1, DateResolvers.nextValid());
        assertEquals(t, OffsetDate.of(2007, 8, 15, OFFSET_PONE));
    }

    public void test_minusMonths_int_DateResolver_negativeAcrossYear() {
        OffsetDate t = TEST_2007_07_15_PONE.minusMonths(-7, DateResolvers.nextValid());
        assertEquals(t, OffsetDate.of(2008, 2, 15, OFFSET_PONE));
    }

    public void test_minusMonths_int_DateResolver_negativeOverYears() {
        OffsetDate t = TEST_2007_07_15_PONE.minusMonths(-31, DateResolvers.nextValid());
        assertEquals(t, OffsetDate.of(2010, 2, 15, OFFSET_PONE));
    }

    public void test_minusMonths_int_DateResolver_adjustDayFromLeapYear() {
        OffsetDate t = OffsetDate.of(2008, 2, 29, OFFSET_PONE).minusMonths(12, DateResolvers.nextValid());
        OffsetDate expected = OffsetDate.of(2007, 3, 1, OFFSET_PONE);
        assertEquals(t, expected);
    }

    public void test_minusMonths_int_DateResolver_adjustDayFromMonthLength() {
        OffsetDate t = OffsetDate.of(2007, 3, 31, OFFSET_PONE).minusMonths(1, DateResolvers.nextValid());
        OffsetDate expected = OffsetDate.of(2007, 3, 1, OFFSET_PONE);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_minusMonths_int_DateResolver_null_adjustDay() {
        TEST_2007_07_15_PONE.minusMonths(1, new MockDateResolverReturnsNull());
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_minusMonths_int_DateResolver_invalidTooLarge() {
        OffsetDate.of(Year.MAX_YEAR, 12, 1, OFFSET_PONE).minusMonths(-1, DateResolvers.nextValid());
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_minusMonths_int_DateResolver_invalidTooSmall() {
        OffsetDate.of(Year.MIN_YEAR, 1, 1, OFFSET_PONE).minusMonths(1, DateResolvers.nextValid());
    }

    //-----------------------------------------------------------------------
    // minusWeeks()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleMinusWeeksSymmetry")
    Object[][] provider_sampleMinusWeeksSymmetry() {
        return new Object[][] {
            {OffsetDate.of(-1, 1, 1, OFFSET_PONE)},
            {OffsetDate.of(-1, 2, 28, OFFSET_PTWO)},
            {OffsetDate.of(-1, 3, 1, OFFSET_PONE)},
            {OffsetDate.of(-1, 12, 31, OFFSET_PTWO)},
            {OffsetDate.of(0, 1, 1, OFFSET_PONE)},
            {OffsetDate.of(0, 2, 28, OFFSET_PTWO)},
            {OffsetDate.of(0, 2, 29, OFFSET_PTWO)},
            {OffsetDate.of(0, 3, 1, OFFSET_PONE)},
            {OffsetDate.of(0, 12, 31, OFFSET_PTWO)},
            {OffsetDate.of(2007, 1, 1, OFFSET_PONE)},
            {OffsetDate.of(2007, 2, 28, OFFSET_PTWO)},
            {OffsetDate.of(2007, 3, 1, OFFSET_PONE)},
            {OffsetDate.of(2007, 12, 31, OFFSET_PTWO)},
            {OffsetDate.of(2008, 1, 1, OFFSET_PONE)},
            {OffsetDate.of(2008, 2, 28, OFFSET_PTWO)},
            {OffsetDate.of(2008, 2, 29, OFFSET_PTWO)},
            {OffsetDate.of(2008, 3, 1, OFFSET_PONE)},
            {OffsetDate.of(2008, 12, 31, OFFSET_PTWO)},
            {OffsetDate.of(2099, 1, 1, OFFSET_PONE)},
            {OffsetDate.of(2099, 2, 28, OFFSET_PTWO)},
            {OffsetDate.of(2099, 3, 1, OFFSET_PONE)},
            {OffsetDate.of(2099, 12, 31, OFFSET_PTWO)},
            {OffsetDate.of(2100, 1, 1, OFFSET_PONE)},
            {OffsetDate.of(2100, 2, 28, OFFSET_PTWO)},
            {OffsetDate.of(2100, 3, 1, OFFSET_PONE)},
            {OffsetDate.of(2100, 12, 31, OFFSET_PTWO)},
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

    public void test_minusWeeks_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.minusWeeks(1);
        assertEquals(t, OffsetDate.of(2007, 7, 8, OFFSET_PONE));
    }

    public void test_minusWeeks_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.minusWeeks(0);
        assertSame(t, TEST_2007_07_15_PONE);
    }

    public void test_minusWeeks_overMonths() {
        OffsetDate t = TEST_2007_07_15_PONE.minusWeeks(9);
        assertEquals(t, OffsetDate.of(2007, 5, 13, OFFSET_PONE));
    }

    public void test_minusWeeks_overYears() {
        OffsetDate t = OffsetDate.of(2008, 7, 13, OFFSET_PONE).minusWeeks(52);
        assertEquals(t, TEST_2007_07_15_PONE);
    }

    public void test_minusWeeks_overLeapYears() {
        OffsetDate t = TEST_2007_07_15_PONE.minusYears(-1).minusWeeks(104);
        assertEquals(t, OffsetDate.of(2006, 7, 18, OFFSET_PONE));
    }

    public void test_minusWeeks_negative() {
        OffsetDate t = TEST_2007_07_15_PONE.minusWeeks(-1);
        assertEquals(t, OffsetDate.of(2007, 7, 22, OFFSET_PONE));
    }

    public void test_minusWeeks_negativeAcrossYear() {
        OffsetDate t = TEST_2007_07_15_PONE.minusWeeks(-28);
        assertEquals(t, OffsetDate.of(2008, 1, 27, OFFSET_PONE));
    }

    public void test_minusWeeks_negativeOverYears() {
        OffsetDate t = TEST_2007_07_15_PONE.minusWeeks(-104);
        assertEquals(t, OffsetDate.of(2009, 7, 12, OFFSET_PONE));
    }

    public void test_minusWeeks_maximum() {
        OffsetDate t = OffsetDate.of(Year.MAX_YEAR, 12, 24, OFFSET_PONE).minusWeeks(-1);
        OffsetDate expected = OffsetDate.of(Year.MAX_YEAR, 12, 31, OFFSET_PONE);
        assertEquals(t, expected);
    }

    public void test_minusWeeks_minimum() {
        OffsetDate t = OffsetDate.of(Year.MIN_YEAR, 1, 8, OFFSET_PONE).minusWeeks(1);
        OffsetDate expected = OffsetDate.of(Year.MIN_YEAR, 1, 1, OFFSET_PONE);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_minusWeeks_invalidTooLarge() {
        OffsetDate.of(Year.MAX_YEAR, 12, 25, OFFSET_PONE).minusWeeks(-1);
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_minusWeeks_invalidTooSmall() {
        OffsetDate.of(Year.MIN_YEAR, 1, 7, OFFSET_PONE).minusWeeks(1);
    }

    //-----------------------------------------------------------------------
    // minusDays()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleMinusDaysSymmetry")
    Object[][] provider_sampleMinusDaysSymmetry() {
        return new Object[][] {
            {OffsetDate.of(-1, 1, 1, OFFSET_PONE)},
            {OffsetDate.of(-1, 2, 28, OFFSET_PTWO)},
            {OffsetDate.of(-1, 3, 1, OFFSET_PONE)},
            {OffsetDate.of(-1, 12, 31, OFFSET_PTWO)},
            {OffsetDate.of(0, 1, 1, OFFSET_PONE)},
            {OffsetDate.of(0, 2, 28, OFFSET_PTWO)},
            {OffsetDate.of(0, 2, 29, OFFSET_PTWO)},
            {OffsetDate.of(0, 3, 1, OFFSET_PONE)},
            {OffsetDate.of(0, 12, 31, OFFSET_PTWO)},
            {OffsetDate.of(2007, 1, 1, OFFSET_PONE)},
            {OffsetDate.of(2007, 2, 28, OFFSET_PTWO)},
            {OffsetDate.of(2007, 3, 1, OFFSET_PONE)},
            {OffsetDate.of(2007, 12, 31, OFFSET_PTWO)},
            {OffsetDate.of(2008, 1, 1, OFFSET_PONE)},
            {OffsetDate.of(2008, 2, 28, OFFSET_PTWO)},
            {OffsetDate.of(2008, 2, 29, OFFSET_PTWO)},
            {OffsetDate.of(2008, 3, 1, OFFSET_PONE)},
            {OffsetDate.of(2008, 12, 31, OFFSET_PTWO)},
            {OffsetDate.of(2099, 1, 1, OFFSET_PONE)},
            {OffsetDate.of(2099, 2, 28, OFFSET_PTWO)},
            {OffsetDate.of(2099, 3, 1, OFFSET_PONE)},
            {OffsetDate.of(2099, 12, 31, OFFSET_PTWO)},
            {OffsetDate.of(2100, 1, 1, OFFSET_PONE)},
            {OffsetDate.of(2100, 2, 28, OFFSET_PTWO)},
            {OffsetDate.of(2100, 3, 1, OFFSET_PONE)},
            {OffsetDate.of(2100, 12, 31, OFFSET_PTWO)},
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

    public void test_minusDays_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.minusDays(1);
        assertEquals(t, OffsetDate.of(2007, 7, 14, OFFSET_PONE));
    }

    public void test_minusDays_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.minusDays(0);
        assertSame(t, TEST_2007_07_15_PONE);
    }

    public void test_minusDays_overMonths() {
        OffsetDate t = TEST_2007_07_15_PONE.minusDays(62);
        assertEquals(t, OffsetDate.of(2007, 5, 14, OFFSET_PONE));
    }

    public void test_minusDays_overYears() {
        OffsetDate t = OffsetDate.of(2008, 7, 16, OFFSET_PONE).minusDays(367);
        assertEquals(t, TEST_2007_07_15_PONE);
    }

    public void test_minusDays_overLeapYears() {
        OffsetDate t = TEST_2007_07_15_PONE.plusYears(2).minusDays(365 + 366);
        assertEquals(t, TEST_2007_07_15_PONE);
    }

    public void test_minusDays_negative() {
        OffsetDate t = TEST_2007_07_15_PONE.minusDays(-1);
        assertEquals(t, OffsetDate.of(2007, 7, 16, OFFSET_PONE));
    }

    public void test_minusDays_negativeAcrossYear() {
        OffsetDate t = TEST_2007_07_15_PONE.minusDays(-169);
        assertEquals(t, OffsetDate.of(2007, 12, 31, OFFSET_PONE));
    }

    public void test_minusDays_negativeOverYears() {
        OffsetDate t = TEST_2007_07_15_PONE.minusDays(-731);
        assertEquals(t, OffsetDate.of(2009, 7, 15, OFFSET_PONE));
    }

    public void test_minusDays_maximum() {
        OffsetDate t = OffsetDate.of(Year.MAX_YEAR, 12, 30, OFFSET_PONE).minusDays(-1);
        OffsetDate expected = OffsetDate.of(Year.MAX_YEAR, 12, 31, OFFSET_PONE);
        assertEquals(t, expected);
    }

    public void test_minusDays_minimum() {
        OffsetDate t = OffsetDate.of(Year.MIN_YEAR, 1, 2, OFFSET_PONE).minusDays(1);
        OffsetDate expected = OffsetDate.of(Year.MIN_YEAR, 1, 1, OFFSET_PONE);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_minusDays_invalidTooLarge() {
        OffsetDate.of(Year.MAX_YEAR, 12, 31, OFFSET_PONE).minusDays(-1);
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_minusDays_invalidTooSmall() {
        OffsetDate.of(Year.MIN_YEAR, 1, 1, OFFSET_PONE).minusDays(1);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusDays_overflowTooLarge() {
        OffsetDate.of(Year.MAX_YEAR, 12, 31, OFFSET_PONE).minusDays(Long.MIN_VALUE);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusDays_overflowTooSmall() {
        OffsetDate.of(Year.MIN_YEAR, 1, 1, OFFSET_PONE).minusDays(Long.MAX_VALUE);
    }

    //-----------------------------------------------------------------------
    // matches()
    //-----------------------------------------------------------------------
    public void test_matches() {
        assertTrue(TEST_2007_07_15_PONE.matches(new CalendricalMatcher() {
            public boolean matchesCalendrical(Calendrical calendrical) {
                return true;
            }
        }));
        assertFalse(TEST_2007_07_15_PONE.matches(new CalendricalMatcher() {
            public boolean matchesCalendrical(Calendrical calendrical) {
                return false;
            }
        }));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_matches_null() {
        TEST_2007_07_15_PONE.matches(null);
    }

    //-----------------------------------------------------------------------
    // atTime()
    //-----------------------------------------------------------------------
    public void test_atTime() {
        OffsetDate t = OffsetDate.of(2008, 6, 30, OFFSET_PTWO);
        assertEquals(t.atTime(LocalTime.of(11, 30)), OffsetDateTime.of(2008, 6, 30, 11, 30, OFFSET_PTWO));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_atTime_nullLocalTime() {
        OffsetDate t = OffsetDate.of(2008, 6, 30, OFFSET_PTWO);
        t.atTime((LocalTime) null);
    }

    public void test_atTime_int_int() {
        OffsetDate t = OffsetDate.of(2008, 6, 30, OFFSET_PTWO);
        assertEquals(t.atTime(11, 30), OffsetDateTime.of(2008, 6, 30, 11, 30, OFFSET_PTWO));
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_hourTooSmall() {
        OffsetDate t = OffsetDate.of(2008, 6, 30, OFFSET_PTWO);
        t.atTime(-1, 30);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_hourTooBig() {
        OffsetDate t = OffsetDate.of(2008, 6, 30, OFFSET_PTWO);
        t.atTime(24, 30);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_minuteTooSmall() {
        OffsetDate t = OffsetDate.of(2008, 6, 30, OFFSET_PTWO);
        t.atTime(11, -1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_minuteTooBig() {
        OffsetDate t = OffsetDate.of(2008, 6, 30, OFFSET_PTWO);
        t.atTime(11, 60);
    }

    public void test_atTime_int_int_int() {
        OffsetDate t = OffsetDate.of(2008, 6, 30, OFFSET_PTWO);
        assertEquals(t.atTime(11, 30, 40), OffsetDateTime.of(2008, 6, 30, 11, 30, 40, OFFSET_PTWO));
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_int_hourTooSmall() {
        OffsetDate t = OffsetDate.of(2008, 6, 30, OFFSET_PTWO);
        t.atTime(-1, 30, 40);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_int_hourTooBig() {
        OffsetDate t = OffsetDate.of(2008, 6, 30, OFFSET_PTWO);
        t.atTime(24, 30, 40);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_int_minuteTooSmall() {
        OffsetDate t = OffsetDate.of(2008, 6, 30, OFFSET_PTWO);
        t.atTime(11, -1, 40);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_int_minuteTooBig() {
        OffsetDate t = OffsetDate.of(2008, 6, 30, OFFSET_PTWO);
        t.atTime(11, 60, 40);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_int_secondTooSmall() {
        OffsetDate t = OffsetDate.of(2008, 6, 30, OFFSET_PTWO);
        t.atTime(11, 30, -1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_int_secondTooBig() {
        OffsetDate t = OffsetDate.of(2008, 6, 30, OFFSET_PTWO);
        t.atTime(11, 30, 60);
    }

    public void test_atTime_int_int_int_int() {
        OffsetDate t = OffsetDate.of(2008, 6, 30, OFFSET_PTWO);
        assertEquals(t.atTime(11, 30, 40, 50), OffsetDateTime.of(2008, 6, 30, 11, 30, 40, 50, OFFSET_PTWO));
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_int_int_hourTooSmall() {
        OffsetDate t = OffsetDate.of(2008, 6, 30, OFFSET_PTWO);
        t.atTime(-1, 30, 40, 50);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_int_int_hourTooBig() {
        OffsetDate t = OffsetDate.of(2008, 6, 30, OFFSET_PTWO);
        t.atTime(24, 30, 40, 50);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_int_int_minuteTooSmall() {
        OffsetDate t = OffsetDate.of(2008, 6, 30, OFFSET_PTWO);
        t.atTime(11, -1, 40, 50);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_int_int_minuteTooBig() {
        OffsetDate t = OffsetDate.of(2008, 6, 30, OFFSET_PTWO);
        t.atTime(11, 60, 40, 50);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_int_int_secondTooSmall() {
        OffsetDate t = OffsetDate.of(2008, 6, 30, OFFSET_PTWO);
        t.atTime(11, 30, -1, 50);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_int_int_secondTooBig() {
        OffsetDate t = OffsetDate.of(2008, 6, 30, OFFSET_PTWO);
        t.atTime(11, 30, 60, 50);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_int_int_nanoTooSmall() {
        OffsetDate t = OffsetDate.of(2008, 6, 30, OFFSET_PTWO);
        t.atTime(11, 30, 40, -1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_int_int_nanoTooBig() {
        OffsetDate t = OffsetDate.of(2008, 6, 30, OFFSET_PTWO);
        t.atTime(11, 30, 40, 1000000000);
    }

    //-----------------------------------------------------------------------
    // atMidnight()
    //-----------------------------------------------------------------------
    public void test_atMidnight() {
        OffsetDate t = OffsetDate.of(2008, 6, 30, OFFSET_PTWO);
        assertEquals(t.atMidnight(), OffsetDateTime.of(2008, 6, 30, 0, 0, OFFSET_PTWO));
    }

    //-----------------------------------------------------------------------
    // atStartOfDayInZone()
    //-----------------------------------------------------------------------
    public void test_atStartOfDayInZone() {
        OffsetDate t = OffsetDate.of(2008, 6, 30, OFFSET_PTWO);
        assertEquals(t.atStartOfDayInZone(ZONE_PARIS),
                ZonedDateTime.of(LocalDateTime.of(2008, 6, 30, 0, 0), ZONE_PARIS));
    }

    public void test_atStartOfDayInZone_dstGap() {
        OffsetDate t = OffsetDate.of(2007, 4, 1, OFFSET_PTWO);
        assertEquals(t.atStartOfDayInZone(ZONE_GAZA),
                ZonedDateTime.of(LocalDateTime.of(2007, 4, 1, 1, 0), ZONE_GAZA));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_atStartOfDayInZone_nullZoneOffset() {
        OffsetDate t = OffsetDate.of(2008, 6, 30, OFFSET_PTWO);
        t.atStartOfDayInZone((TimeZone) null);
    }

    //-----------------------------------------------------------------------
    // toInstant()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_toInstant(int year, int month, int day, ZoneOffset offset) {
        OffsetDate d = OffsetDate.of(year, month, day, offset);
        assertEquals(d.toInstant(), d.atMidnight().toInstant());
    }

    //-----------------------------------------------------------------------
    // toLocalDate()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_toLocalDate(int year, int month, int day, ZoneOffset offset) {
        LocalDate t = LocalDate.of(year, month, day);
        assertEquals(OffsetDate.of(year, month, day, offset).toLocalDate(), t);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo_date() {
        OffsetDate a = OffsetDate.of(2008, 6, 29, OFFSET_PONE);
        OffsetDate b = OffsetDate.of(2008, 6, 30, OFFSET_PONE);  // a is before b due to date
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
        assertEquals(a.toInstant().compareTo(b.toInstant()) < 0, true);
    }

    public void test_compareTo_offset() {
        OffsetDate a = OffsetDate.of(2008, 6, 30, OFFSET_PTWO);
        OffsetDate b = OffsetDate.of(2008, 6, 30, OFFSET_PONE);  // a is before b due to offset
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
        assertEquals(a.toInstant().compareTo(b.toInstant()) < 0, true);
    }

    public void test_compareTo_both() {
        OffsetDate a = OffsetDate.of(2008, 6, 29, OFFSET_PTWO);
        OffsetDate b = OffsetDate.of(2008, 6, 30, OFFSET_PONE);  // a is before b on instant scale
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
        assertEquals(a.toInstant().compareTo(b.toInstant()) < 0, true);
    }

    public void test_compareTo_24hourDifference() {
        OffsetDate a = OffsetDate.of(2008, 6, 29, ZoneOffset.ofHours(-12));
        OffsetDate b = OffsetDate.of(2008, 6, 30, ZoneOffset.ofHours(12));  // a is before b despite being same time-line time
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
        assertEquals(a.toInstant().compareTo(b.toInstant()) == 0, true);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_compareTo_null() {
        OffsetDate a = OffsetDate.of(2008, 6, 30, OFFSET_PONE);
        a.compareTo(null);
    }

    @Test(expectedExceptions=ClassCastException.class)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void compareToNonOffsetDate() {
       Comparable c = TEST_2007_07_15_PONE;
       c.compareTo(new Object());
    }

    //-----------------------------------------------------------------------
    // isAfter() / isBefore()
    //-----------------------------------------------------------------------
    public void test_isBeforeIsAfter() {
        OffsetDate a = OffsetDate.of(2008, 6, 29, OFFSET_PONE);
        OffsetDate b = OffsetDate.of(2008, 6, 30, OFFSET_PONE);  // a is before b due to date
        assertEquals(a.isBefore(b), true);
        assertEquals(a.isAfter(b), false);
        assertEquals(b.isBefore(a), false);
        assertEquals(b.isAfter(a), true);
        assertEquals(a.isBefore(a), false);
        assertEquals(b.isBefore(b), false);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isBefore_null() {
        OffsetDate a = OffsetDate.of(2008, 6, 30, OFFSET_PONE);
        a.isBefore(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isAfter_null() {
        OffsetDate a = OffsetDate.of(2008, 6, 30, OFFSET_PONE);
        a.isAfter(null);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_equals_true(int y, int m, int d, ZoneOffset offset) {
        OffsetDate a = OffsetDate.of(y, m, d, offset);
        OffsetDate b = OffsetDate.of(y, m, d, offset);
        assertEquals(a.equals(b), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
    }
    @Test(dataProvider="sampleDates")
    public void test_equals_false_year_differs(int y, int m, int d, ZoneOffset offset) {
        OffsetDate a = OffsetDate.of(y, m, d, offset);
        OffsetDate b = OffsetDate.of(y + 1, m, d, offset);
        assertEquals(a.equals(b), false);
    }

    @Test(dataProvider="sampleDates")
    public void test_equals_false_month_differs(int y, int m, int d, ZoneOffset offset) {
        OffsetDate a = OffsetDate.of(y, m, d, offset);
        OffsetDate b = OffsetDate.of(y, m + 1, d, offset);
        assertEquals(a.equals(b), false);
    }

    @Test(dataProvider="sampleDates")
    public void test_equals_false_day_differs(int y, int m, int d, ZoneOffset offset) {
        OffsetDate a = OffsetDate.of(y, m, d, offset);
        OffsetDate b = OffsetDate.of(y, m, d + 1, offset);
        assertEquals(a.equals(b), false);
    }

    @Test(dataProvider="sampleDates")
    public void test_equals_false_offset_differs(int y, int m, int d, ZoneOffset ignored) {
        OffsetDate a = OffsetDate.of(y, m, d, OFFSET_PONE);
        OffsetDate b = OffsetDate.of(y, m, d, OFFSET_PTWO);
        assertEquals(a.equals(b), false);
    }

    public void test_equals_itself_true() {
        assertEquals(TEST_2007_07_15_PONE.equals(TEST_2007_07_15_PONE), true);
    }

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
        OffsetDate t = OffsetDate.of(y, m, d, ZoneOffset.of(offsetId));
        String str = t.toString();
        assertEquals(str, expected);
    }

    //-----------------------------------------------------------------------
    // toString(DateTimeFormatter)
    //-----------------------------------------------------------------------
    public void test_toString_formatter() {
        String t = OffsetDate.of(2010, 12, 3, OFFSET_PONE).toString(DateTimeFormatters.pattern("yyyyMMdd"));
        assertEquals(t, "20101203");
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_toString_formatter_null() {
        OffsetDate.of(2010, 12, 3, OFFSET_PONE).toString(null);
    }

    //-----------------------------------------------------------------------
    // matchesCalendrical() - parameter is larger calendrical
    //-----------------------------------------------------------------------
    public void test_matchesCalendrical_true_date() {
        OffsetDate test = TEST_2007_07_15_PONE;
        OffsetDateTime cal = TEST_2007_07_15_PONE.atMidnight();
        assertEquals(test.matchesCalendrical(cal), true);
    }

    public void test_matchesCalendrical_false_date() {
        OffsetDate test = TEST_2007_07_15_PONE;
        OffsetDateTime cal = TEST_2007_07_15_PONE.plusYears(1).atMidnight();
        assertEquals(test.matchesCalendrical(cal), false);
    }

    public void test_matchesCalendrical_itself_true() {
        assertEquals(TEST_2007_07_15_PONE.matchesCalendrical(TEST_2007_07_15_PONE), true);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_matchesCalendrical_null() {
        TEST_2007_07_15_PONE.matchesCalendrical(null);
    }

    //-----------------------------------------------------------------------
    // adjustDate()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_adjustDate(int y, int m, int d, ZoneOffset offset) {
        OffsetDate a = OffsetDate.of(y, m, d, offset);
        assertSame(a.adjustDate(TEST_2007_07_15_PONE.toLocalDate()), a.toLocalDate());
        assertSame(TEST_2007_07_15_PONE.adjustDate(a.toLocalDate()), TEST_2007_07_15_PONE.toLocalDate());
    }

    public void test_adjustDate_same() {
        assertSame(OffsetDate.of(2007, 7, 15, OFFSET_PONE).adjustDate(TEST_2007_07_15_PONE.toLocalDate()),
              TEST_2007_07_15_PONE.toLocalDate());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustDate_null() {
        TEST_2007_07_15_PONE.adjustDate(null);
    }
}
