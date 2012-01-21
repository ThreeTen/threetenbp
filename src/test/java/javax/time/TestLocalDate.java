/*
 * Copyright (c) 2007-2011, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.calendrical.ISODateTimeRule.AMPM_OF_DAY;
import static javax.time.calendrical.ISODateTimeRule.DAY_OF_MONTH;
import static javax.time.calendrical.ISODateTimeRule.DAY_OF_WEEK;
import static javax.time.calendrical.ISODateTimeRule.DAY_OF_YEAR;
import static javax.time.calendrical.ISODateTimeRule.HOUR_OF_AMPM;
import static javax.time.calendrical.ISODateTimeRule.HOUR_OF_DAY;
import static javax.time.calendrical.ISODateTimeRule.MINUTE_OF_HOUR;
import static javax.time.calendrical.ISODateTimeRule.MONTH_OF_QUARTER;
import static javax.time.calendrical.ISODateTimeRule.MONTH_OF_YEAR;
import static javax.time.calendrical.ISODateTimeRule.NANO_OF_SECOND;
import static javax.time.calendrical.ISODateTimeRule.QUARTER_OF_YEAR;
import static javax.time.calendrical.ISODateTimeRule.SECOND_OF_MINUTE;
import static javax.time.calendrical.ISODateTimeRule.WEEK_BASED_YEAR;
import static javax.time.calendrical.ISODateTimeRule.WEEK_OF_WEEK_BASED_YEAR;
import static javax.time.calendrical.ISODateTimeRule.YEAR;
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

import javax.time.CalendricalException;
import javax.time.Chronology;
import javax.time.Clock;
import javax.time.DayOfWeek;
import javax.time.Instant;
import javax.time.LocalDate;
import javax.time.LocalDateTime;
import javax.time.LocalTime;
import javax.time.MonthDay;
import javax.time.MonthOfYear;
import javax.time.OffsetDate;
import javax.time.OffsetDateTime;
import javax.time.OffsetTime;
import javax.time.Period;
import javax.time.TimeSource;
import javax.time.Year;
import javax.time.YearMonth;
import javax.time.ZoneId;
import javax.time.ZoneOffset;
import javax.time.ZonedDateTime;
import javax.time.calendrical.Calendrical;
import javax.time.calendrical.CalendricalMatcher;
import javax.time.calendrical.CalendricalRule;
import javax.time.calendrical.DateAdjuster;
import javax.time.calendrical.DateAdjusters;
import javax.time.calendrical.DateResolvers;
import javax.time.calendrical.ISOChronology;
import javax.time.calendrical.IllegalCalendarFieldValueException;
import javax.time.calendrical.InvalidCalendarFieldException;
import javax.time.calendrical.MockDateAdjusterReturnsNull;
import javax.time.calendrical.MockDateResolverReturnsNull;
import javax.time.calendrical.MockOtherChronology;
import javax.time.calendrical.MockPeriodProviderReturnsNull;
import javax.time.calendrical.MockRuleNoValue;
import javax.time.calendrical.PeriodFields;
import javax.time.calendrical.PeriodProvider;
import javax.time.format.CalendricalParseException;
import javax.time.format.DateTimeFormatters;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test LocalDate.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestLocalDate extends AbstractTest {

//    private static final String MIN_YEAR_STR = Integer.toString(Year.MIN_YEAR);
//    private static final String MAX_YEAR_STR = Integer.toString(Year.MAX_YEAR);
    private static final ZoneOffset OFFSET_PONE = ZoneOffset.ofHours(1);
    private static final ZoneOffset OFFSET_PTWO = ZoneOffset.ofHours(2);
    private static final ZoneId ZONE_PARIS = ZoneId.of("Europe/Paris");
    private static final ZoneId ZONE_GAZA = ZoneId.of("Asia/Gaza");
    
    private LocalDate TEST_2007_07_15;
    private long MAX_VALID_EPOCHDAYS;
    private long MIN_VALID_EPOCHDAYS;
    private long MAX_VALID_MJDAYS;
    private long MIN_VALID_MJDAYS;
    private LocalDate MAX_DATE;
    private LocalDate MIN_DATE;
    private Instant MAX_INSTANT;
    private Instant MIN_INSTANT;

    @BeforeMethod
    public void setUp() {
        TEST_2007_07_15 = LocalDate.of(2007, 7, 15);
        
        LocalDate max = LocalDate.MAX_DATE;
        LocalDate min = LocalDate.MIN_DATE;
        MAX_VALID_EPOCHDAYS = max.toEpochDay();
        MIN_VALID_EPOCHDAYS = min.toEpochDay();
        MAX_VALID_MJDAYS = max.toModifiedJulianDay();
        MIN_VALID_MJDAYS = min.toModifiedJulianDay();
        MAX_DATE = max;
        MIN_DATE = min;
        MAX_INSTANT = max.atOffset(ZoneOffset.UTC).atMidnight().toInstant();
        MIN_INSTANT = min.atOffset(ZoneOffset.UTC).atMidnight().toInstant();
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        Object obj = TEST_2007_07_15;
        assertTrue(obj instanceof Calendrical);
        assertTrue(obj instanceof Serializable);
        assertTrue(obj instanceof Comparable<?>);
        assertTrue(obj instanceof CalendricalMatcher);
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(TEST_2007_07_15);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), TEST_2007_07_15);
    }

    public void test_immutable() {
        Class<LocalDate> cls = LocalDate.class;
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
    private void check(LocalDate test_2008_02_29, int y, int m, int d) {
        assertEquals(test_2008_02_29.getYear(), y);
        assertEquals(test_2008_02_29.getMonthOfYear().getValue(), m);
        assertEquals(test_2008_02_29.getDayOfMonth(), d);
    }

    //-----------------------------------------------------------------------
    public void constant_MIN_DATE_TIME() {
        check(LocalDate.MIN_DATE, Year.MIN_YEAR, 1, 1);
    }

    public void constant_MAX_DATE_TIME() {
        check(LocalDate.MAX_DATE, Year.MAX_YEAR, 12, 31);
    }

    //-----------------------------------------------------------------------
    // now()
    //-----------------------------------------------------------------------
    public void now() {
        LocalDate expected = LocalDate.now(Clock.systemDefaultZone());
        LocalDate test = LocalDate.now();
        for (int i = 0; i < 100; i++) {
            if (expected.equals(test)) {
                return;
            }
            expected = LocalDate.now(Clock.systemDefaultZone());
            test = LocalDate.now();
        }
        assertEquals(test, expected);
    }

    //-----------------------------------------------------------------------
    // now(Clock)
    //-----------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class)
    public void now_Clock_nullClock() {
        LocalDate.now(null);
    }

    public void now_Clock_allSecsInDay_utc() {
        for (int i = 0; i < (2 * 24 * 60 * 60); i++) {
            Instant instant = Instant.ofEpochSecond(i);
            Clock clock = Clock.clock(TimeSource.fixed(instant), ZoneId.UTC);
            LocalDate test = LocalDate.now(clock);
            assertEquals(test.getYear(), 1970);
            assertEquals(test.getMonthOfYear(), MonthOfYear.JANUARY);
            assertEquals(test.getDayOfMonth(), (i < 24 * 60 * 60 ? 1 : 2));
        }
    }

    public void now_Clock_allSecsInDay_offset() {
        for (int i = 0; i < (2 * 24 * 60 * 60); i++) {
            Instant instant = Instant.ofEpochSecond(i);
            Clock clock = Clock.clock(TimeSource.fixed(instant.minusSeconds(OFFSET_PONE.getAmountSeconds())), ZoneId.of(OFFSET_PONE));
            LocalDate test = LocalDate.now(clock);
            assertEquals(test.getYear(), 1970);
            assertEquals(test.getMonthOfYear(), MonthOfYear.JANUARY);
            assertEquals(test.getDayOfMonth(), (i < 24 * 60 * 60) ? 1 : 2);
        }
    }

    public void now_Clock_allSecsInDay_beforeEpoch() {
        for (int i =-1; i >= -(2 * 24 * 60 * 60); i--) {
            Instant instant = Instant.ofEpochSecond(i);
            Clock clock = Clock.clock(TimeSource.fixed(instant), ZoneId.UTC);
            LocalDate test = LocalDate.now(clock);
            assertEquals(test.getYear(), 1969);
            assertEquals(test.getMonthOfYear(), MonthOfYear.DECEMBER);
            assertEquals(test.getDayOfMonth(), (i >= -24 * 60 * 60 ? 31 : 30));
        }
    }

    //-----------------------------------------------------------------------
    public void now_Clock_maxYear() {
        Clock clock = Clock.clock(TimeSource.fixed(MAX_INSTANT), ZoneId.UTC);
        LocalDate test = LocalDate.now(clock);
        assertEquals(test, MAX_DATE);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void now_Clock_tooBig() {
        Clock clock = Clock.clock(TimeSource.fixed(MAX_INSTANT.plusSeconds(24 * 60 * 60)), ZoneId.UTC);
        try {
            LocalDate.now(clock);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), YEAR);
            throw ex;
        }
    }

    public void now_Clock_minYear() {
        Clock clock = Clock.clock(TimeSource.fixed(MIN_INSTANT), ZoneId.UTC);
        LocalDate test = LocalDate.now(clock);
        assertEquals(test, MIN_DATE);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void now_Clock_tooLow() {
        Clock clock = Clock.clock(TimeSource.fixed(MIN_INSTANT.minusNanos(1)), ZoneId.UTC);
        try {
            LocalDate.now(clock);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), YEAR);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // of() factories
    //-----------------------------------------------------------------------
    public void factory_date_intsMonth() {
        assertEquals(TEST_2007_07_15, LocalDate.of(2007, MonthOfYear.JULY, 15));
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_date_intsMonth_dayTooLow() {
        LocalDate.of(2007, MonthOfYear.JANUARY, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_date_intsMonth_dayTooHigh() {
        LocalDate.of(2007, MonthOfYear.JANUARY, 32);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_date_intsMonth_nullMonth() {
        LocalDate.of(2007, null, 30);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_date_intsMonth_yearTooLow() {
        LocalDate.of(Integer.MIN_VALUE, MonthOfYear.JANUARY, 1);
    }

    //-----------------------------------------------------------------------
    public void factory_date_ints() {
        check(TEST_2007_07_15, 2007, 7, 15);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_date_ints_dayTooLow() {
        LocalDate.of(2007, 1, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_date_ints_dayTooHigh() {
        LocalDate.of(2007, 1, 32);
    }


    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_date_ints_monthTooLow() {
        LocalDate.of(2007, 0, 1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_date_ints_monthTooHigh() {
        LocalDate.of(2007, 13, 1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_date_ints_yearTooLow() {
        LocalDate.of(Integer.MIN_VALUE, 1, 1);
    }

    //-----------------------------------------------------------------------
    // Since plusDays/minusDays actually depends on MJDays, it cannot be used for testing
    private LocalDate next(LocalDate date) {
        int newDayOfMonth = date.getDayOfMonth() + 1;
        if (newDayOfMonth <= date.getMonthOfYear().lengthInDays(ISOChronology.isLeapYear(date.getYear()))) {
            return date.withDayOfMonth(newDayOfMonth);
        }
        date = date.withDayOfMonth(1);
        if (date.getMonthOfYear() == MonthOfYear.DECEMBER) {
            date = date.withYear(date.getYear() + 1);
        }
        return date.with(date.getMonthOfYear().next());
    }

    private LocalDate previous(LocalDate date) {
        int newDayOfMonth = date.getDayOfMonth() - 1;
        if (newDayOfMonth > 0) {
            return date.withDayOfMonth(newDayOfMonth);
        }
        date = date.with(date.getMonthOfYear().previous());
        if (date.getMonthOfYear() == MonthOfYear.DECEMBER) {
            date = date.withYear(date.getYear() - 1);
        }
        return date.withDayOfMonth(date.getMonthOfYear().getLastDayOfMonth(ISOChronology.isLeapYear(date.getYear())));
    }

    //-----------------------------------------------------------------------
    // ofEpochDay()
    //-----------------------------------------------------------------------
    public void factory_ofEpochDay() {
        long date_0000_01_01 = -678941 - 40587;
        assertEquals(LocalDate.ofEpochDay(0), LocalDate.of(1970, 1, 1));
        assertEquals(LocalDate.ofEpochDay(date_0000_01_01), LocalDate.of(0, 1, 1));
        assertEquals(LocalDate.ofEpochDay(date_0000_01_01 - 1), LocalDate.of(-1, 12, 31));
        assertEquals(LocalDate.ofEpochDay(MAX_VALID_EPOCHDAYS), LocalDate.of(Year.MAX_YEAR, 12, 31));
        assertEquals(LocalDate.ofEpochDay(MIN_VALID_EPOCHDAYS), LocalDate.of(Year.MIN_YEAR, 1, 1));
        
        LocalDate test = LocalDate.of(0, 1, 1);
        for (long i = date_0000_01_01; i < 700000; i++) {
            assertEquals(LocalDate.ofEpochDay(i), test);
            test = next(test);
        }
        test = LocalDate.of(0, 1, 1);
        for (long i = date_0000_01_01; i > -2000000; i--) {
            assertEquals(LocalDate.ofEpochDay(i), test);
            test = previous(test);
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_ofEpochDay_aboveMax() {
        LocalDate.ofEpochDay(MAX_VALID_EPOCHDAYS + 1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_ofEpochDay_belowMin() {
        LocalDate.ofEpochDay(MIN_VALID_EPOCHDAYS - 1);
    }

    //-----------------------------------------------------------------------
    // ofModifiedJulianDay()
    //-----------------------------------------------------------------------
    public void factory_ofModifiedJulianDay() {
        long date_0000_01_01 = -678941;
        assertEquals(LocalDate.ofModifiedJulianDay(40587), LocalDate.of(1970, 1, 1));
        assertEquals(LocalDate.ofModifiedJulianDay(date_0000_01_01), LocalDate.of(0, 1, 1));
        assertEquals(LocalDate.ofModifiedJulianDay(date_0000_01_01 - 1), LocalDate.of(-1, 12, 31));
        assertEquals(LocalDate.ofModifiedJulianDay(MAX_VALID_MJDAYS), LocalDate.of(Year.MAX_YEAR, 12, 31));
        assertEquals(LocalDate.ofModifiedJulianDay(MIN_VALID_MJDAYS), LocalDate.of(Year.MIN_YEAR, 1, 1));
        
        LocalDate test = LocalDate.of(0, 1, 1);
        for (long i = date_0000_01_01; i < 700000; i++) {
            assertEquals(LocalDate.ofModifiedJulianDay(i), test);
            test = next(test);
        }
        test = LocalDate.of(0, 1, 1);
        for (long i = date_0000_01_01; i > -2000000; i--) {
            assertEquals(LocalDate.ofModifiedJulianDay(i), test);
            test = previous(test);
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_ofModifiedJulianDay_aboveMax() {
        LocalDate.ofModifiedJulianDay(MAX_VALID_MJDAYS + 1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_ofModifiedJulianDay_belowMin() {
        LocalDate.ofModifiedJulianDay(MIN_VALID_MJDAYS - 1);
    }

    //-----------------------------------------------------------------------
    // from()
    //-----------------------------------------------------------------------
    public void test_factory_Calendricals() {
        assertEquals(LocalDate.from(YearMonth.of(2007, 7), DAY_OF_MONTH.field(15)), LocalDate.of(2007, 7, 15));
        assertEquals(LocalDate.from(MonthDay.of(7, 15), YEAR.field(2007)), LocalDate.of(2007, 7, 15));
        assertEquals(LocalDate.from(LocalDate.of(2007, 7, 15)), LocalDate.of(2007, 7, 15));
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_factory_Calendricals_invalid_clash() {
        LocalDate.from(YearMonth.of(2007, 7), MonthDay.of(9, 15));
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_factory_Calendricals_invalid_noDerive() {
        LocalDate.from(LocalTime.of(12, 30));
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_factory_Calendricals_invalid_empty() {
        LocalDate.from();
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_Calendricals_nullArray() {
        LocalDate.from((Calendrical[]) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_Calendricals_null() {
        LocalDate.from((Calendrical) null);
    }

    //-----------------------------------------------------------------------
    // parse()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleToString")
    public void factory_parse_validText(int y, int m, int d, String parsable) {
        LocalDate t = LocalDate.parse(parsable);
        assertNotNull(t, parsable);
        assertEquals(t.getYear(), y, parsable);
        assertEquals(t.getMonthOfYear().getValue(), m, parsable);
        assertEquals(t.getDayOfMonth(), d, parsable);
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
                {"2008-02-01Z"},
                {"2008-02-01+01:00"},
                {"2008-02-01+01:00[Europe/Paris]"},
        };
    }

    @Test(dataProvider="sampleBadParse", expectedExceptions={CalendricalParseException.class})
    public void factory_parse_invalidText(String unparsable) {
        LocalDate.parse(unparsable);
    }

    @Test(expectedExceptions=CalendricalParseException.class)
    public void factory_parse_illegalValue() {
        LocalDate.parse("2008-06-32");
    }

    @Test(expectedExceptions=CalendricalParseException.class)
    public void factory_parse_invalidValue() {
        LocalDate.parse("2008-06-31");
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_parse_nullText() {
        LocalDate.parse((String) null);
    }

    //-----------------------------------------------------------------------
    // parse(DateTimeFormatter)
    //-----------------------------------------------------------------------
    public void factory_parse_formatter() {
        LocalDate t = LocalDate.parse("20101203", DateTimeFormatters.basicIsoDate());
        assertEquals(t, LocalDate.of(2010, 12, 3));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_parse_formatter_nullText() {
        LocalDate.parse((String) null, DateTimeFormatters.basicIsoDate());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_parse_formatter_nullFormatter() {
        LocalDate.parse("20101203", null);
    }

    //-----------------------------------------------------------------------
    // get(CalendricalRule)
    //-----------------------------------------------------------------------
    public void test_get_CalendricalRule() {
        LocalDate test = LocalDate.of(2008, 6, 30);
        assertEquals(test.get(Chronology.rule()), ISOChronology.INSTANCE);
        assertEquals(test.get(YEAR).getValue(), 2008);
        assertEquals(test.get(QUARTER_OF_YEAR).getValue(), 2);
        assertEquals(test.get(MONTH_OF_YEAR).getValue(), 6);
        assertEquals(test.get(MONTH_OF_QUARTER).getValue(), 3);
        assertEquals(test.get(DAY_OF_MONTH).getValue(), 30);
        assertEquals(test.get(DAY_OF_WEEK).getValue(), 1);
        assertEquals(test.get(DAY_OF_YEAR).getValue(), 182);
        assertEquals(test.get(WEEK_OF_WEEK_BASED_YEAR).getValue(), 27);
        assertEquals(test.get(WEEK_BASED_YEAR).getValue(), 2008);
        
        assertEquals(test.get(HOUR_OF_DAY), null);
        assertEquals(test.get(MINUTE_OF_HOUR), null);
        assertEquals(test.get(SECOND_OF_MINUTE), null);
        assertEquals(test.get(NANO_OF_SECOND), null);
        assertEquals(test.get(HOUR_OF_AMPM), null);
        assertEquals(test.get(AMPM_OF_DAY), null);
        
        assertEquals(test.get(LocalDate.rule()), test);
        assertEquals(test.get(LocalTime.rule()), null);
        assertEquals(test.get(LocalDateTime.rule()), null);
        assertEquals(test.get(OffsetDate.rule()), null);
        assertEquals(test.get(OffsetTime.rule()), null);
        assertEquals(test.get(OffsetDateTime.rule()), null);
        assertEquals(test.get(ZonedDateTime.rule()), null);
        assertEquals(test.get(ZoneOffset.rule()), null);
        assertEquals(test.get(ZoneId.rule()), null);
        assertEquals(test.get(YearMonth.rule()), YearMonth.of(2008, 6));
        assertEquals(test.get(MonthDay.rule()), MonthDay.of(6, 30));
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_get_CalendricalRule_null() {
        TEST_2007_07_15.get((CalendricalRule<?>) null);
    }

    public void test_get_unsupported() {
        assertEquals(TEST_2007_07_15.get(MockRuleNoValue.INSTANCE), null);
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

    //-----------------------------------------------------------------------
    // get*()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_get(int y, int m, int d) {
        LocalDate a = LocalDate.of(y, m, d);
        assertEquals(a.getYear(), y);
        assertEquals(a.getMonthOfYear(), MonthOfYear.of(m));
        assertEquals(a.getDayOfMonth(), d);
    }

    @Test(dataProvider="sampleDates")
    public void test_getDOY(int y, int m, int d) {
        LocalDate a = LocalDate.of(y, m, d);
        int total = 0;
        for (int i = 1; i < m; i++) {
            total += MonthOfYear.of(i).lengthInDays(ISOChronology.isLeapYear(y));
        }
        int doy = total + d;
        assertEquals(a.getDayOfYear(), doy);
    }

    public void test_getDayOfWeek() {
        DayOfWeek dow = DayOfWeek.MONDAY;
        for (MonthOfYear month : MonthOfYear.values()) {
            int length = month.lengthInDays(false);
            for (int i = 1; i <= length; i++) {
                LocalDate d = LocalDate.of(2007, month, i);
                assertSame(d.getDayOfWeek(), dow);
                dow = dow.next();
            }
        }
    }

    //-----------------------------------------------------------------------
    // isLeapYear()
    //-----------------------------------------------------------------------
    public void test_isLeapYear() {
        assertEquals(LocalDate.of(1999, 1, 1).isLeapYear(), false);
        assertEquals(LocalDate.of(2000, 1, 1).isLeapYear(), true);
        assertEquals(LocalDate.of(2001, 1, 1).isLeapYear(), false);
        assertEquals(LocalDate.of(2002, 1, 1).isLeapYear(), false);
        assertEquals(LocalDate.of(2003, 1, 1).isLeapYear(), false);
        assertEquals(LocalDate.of(2004, 1, 1).isLeapYear(), true);
        assertEquals(LocalDate.of(2005, 1, 1).isLeapYear(), false);
        
        assertEquals(LocalDate.of(1500, 1, 1).isLeapYear(), false);
        assertEquals(LocalDate.of(1600, 1, 1).isLeapYear(), true);
        assertEquals(LocalDate.of(1700, 1, 1).isLeapYear(), false);
        assertEquals(LocalDate.of(1800, 1, 1).isLeapYear(), false);
        assertEquals(LocalDate.of(1900, 1, 1).isLeapYear(), false);
    }

    //-----------------------------------------------------------------------
    // with()
    //-----------------------------------------------------------------------
    public void test_with() {
        DateAdjuster dateAdjuster = DateAdjusters.lastDayOfMonth();
        assertEquals(TEST_2007_07_15.with(dateAdjuster), dateAdjuster.adjustDate(TEST_2007_07_15));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_with_null() {
        TEST_2007_07_15.with((DateAdjuster) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_with_null_adjustDate() {
        TEST_2007_07_15.with(new MockDateAdjusterReturnsNull());
    }

    //-----------------------------------------------------------------------
    // withYear()
    //-----------------------------------------------------------------------
    public void test_withYear_int_normal() {
        LocalDate t = TEST_2007_07_15.withYear(2008);
        assertEquals(t, LocalDate.of(2008, 7, 15));
    }

    public void test_withYear_int_noChange() {
        LocalDate t = TEST_2007_07_15.withYear(2007);
        assertSame(t, TEST_2007_07_15);
    }
    
    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withYear_int_invalid() {
        TEST_2007_07_15.withYear(Year.MIN_YEAR - 1);
    }

    public void test_withYear_int_adjustDay() {
        LocalDate t = LocalDate.of(2008, 2, 29).withYear(2007);
        LocalDate expected = LocalDate.of(2007, 2, 28);
        assertEquals(t, expected);
    }

    public void test_withYear_int_DateResolver_normal() {
        LocalDate t = TEST_2007_07_15.withYear(2008, DateResolvers.strict());
        assertEquals(t, LocalDate.of(2008, 7, 15));
    }

    public void test_withYear_int_DateResolver_noChange() {
        LocalDate t = TEST_2007_07_15.withYear(2007, DateResolvers.strict());
        assertSame(t, TEST_2007_07_15);
    }
    
    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withYear_int_DateResolver_invalid() {
        TEST_2007_07_15.withYear(Year.MIN_YEAR - 1, DateResolvers.nextValid());
    }

    public void test_withYear_int_DateResolver_adjustDay() {
        LocalDate t = LocalDate.of(2008, 2, 29).withYear(2007, DateResolvers.nextValid());
        LocalDate expected = LocalDate.of(2007, 3, 1);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_withYear_int_DateResolver_null_adjustDay() {
        TEST_2007_07_15.withYear(2008, new MockDateResolverReturnsNull());
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_withYear_int_DateResolver_adjustDay_invalid() {
        LocalDate.of(2008, 2, 29).withYear(2007, DateResolvers.strict());
    }

    //-----------------------------------------------------------------------
    // withMonthOfYear()
    //-----------------------------------------------------------------------
    public void test_withMonthOfYear_int_normal() {
        LocalDate t = TEST_2007_07_15.withMonthOfYear(1);
        assertEquals(t, LocalDate.of(2007, 1, 15));
    }

    public void test_withMonthOfYear_int_noChange() {
        LocalDate t = TEST_2007_07_15.withMonthOfYear(7);
        assertSame(t, TEST_2007_07_15);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withMonthOfYear_int_invalid() {
        TEST_2007_07_15.withMonthOfYear(13);
    }

    public void test_withMonthOfYear_int_adjustDay() {
        LocalDate t = LocalDate.of(2007, 12, 31).withMonthOfYear(11);
        LocalDate expected = LocalDate.of(2007, 11, 30);
        assertEquals(t, expected);
    }

    public void test_withMonthOfYear_int_DateResolver_normal() {
        LocalDate t = TEST_2007_07_15.withMonthOfYear(1, DateResolvers.strict());
        assertEquals(t, LocalDate.of(2007, 1, 15));
    }

    public void test_withMonthOfYear_int_DateResolver_noChange() {
        LocalDate t = TEST_2007_07_15.withMonthOfYear(7, DateResolvers.strict());
        assertSame(t, TEST_2007_07_15);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withMonthOfYear_int_DateResolver_invalid() {
        TEST_2007_07_15.withMonthOfYear(13, DateResolvers.nextValid());
    }

    public void test_withMonthOfYear_int_DateResolver_adjustDay() {
        LocalDate t = LocalDate.of(2007, 12, 31).withMonthOfYear(11, DateResolvers.nextValid());
        LocalDate expected = LocalDate.of(2007, 12, 1);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_withMonthOfYear_int_DateResolver_null_adjustDay() {
        TEST_2007_07_15.withMonthOfYear(1, new MockDateResolverReturnsNull());
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_withMonthOfYear_int_DateResolver_adjustDay_invalid() {
        LocalDate.of(2007, 12, 31).withMonthOfYear(11, DateResolvers.strict());
    }

    //-----------------------------------------------------------------------
    // withDayOfMonth()
    //-----------------------------------------------------------------------
    public void test_withDayOfMonth_normal() {
        LocalDate t = TEST_2007_07_15.withDayOfMonth(1);
        assertEquals(t, LocalDate.of(2007, 7, 1));
    }

    public void test_withDayOfMonth_noChange() {
        LocalDate t = TEST_2007_07_15.withDayOfMonth(15);
        assertSame(t, TEST_2007_07_15);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withDayOfMonth_illegal() {
        TEST_2007_07_15.withDayOfMonth(32);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_withDayOfMonth_invalid() {
        LocalDate.of(2007, 11, 30).withDayOfMonth(31);
    }

    public void test_withDayOfMonth_Resolver_normal() {
        LocalDate t = TEST_2007_07_15.withDayOfMonth(1, DateResolvers.strict());
        assertEquals(t, LocalDate.of(2007, 7, 1));
    }

    public void test_withDayOfMonth_Resolver_noChange() {
        LocalDate t = TEST_2007_07_15.withDayOfMonth(15, DateResolvers.strict());
        assertSame(t, TEST_2007_07_15);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withDayOfMonth_int_DateResolver_invalid() {
        TEST_2007_07_15.withDayOfMonth(32, DateResolvers.nextValid());
    }

    public void test_withDayOfMonth_int_DateResolver_adjustDay() {
        LocalDate t = LocalDate.of(2007, 6, 3).withDayOfMonth(31, DateResolvers.nextValid());
        LocalDate expected = LocalDate.of(2007, 7, 1);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_withDayOfMonth_int_DateResolver_null_adjustDay() {
        LocalDate.of(2007, 6, 3).withDayOfMonth(31, new MockDateResolverReturnsNull());
    }

    //-----------------------------------------------------------------------
    // withDayOfYear(int)
    //-----------------------------------------------------------------------
    public void test_withDayOfYear_normal() {
        LocalDate t = TEST_2007_07_15.withDayOfYear(33);
        assertEquals(t, LocalDate.of(2007, 2, 2));
    }

    public void test_withDayOfYear_noChange() {
        LocalDate t = TEST_2007_07_15.withDayOfYear(31 + 28 + 31 + 30 + 31 + 30 + 15);
        assertSame(t, TEST_2007_07_15);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withDayOfYear_illegal() {
        TEST_2007_07_15.withDayOfYear(367);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_withDayOfYear_invalid() {
        TEST_2007_07_15.withDayOfYear(366);
    }

    //-----------------------------------------------------------------------
    // plus(PeriodProvider)
    //-----------------------------------------------------------------------
    public void test_plus_PeriodProvider() {
        PeriodProvider provider = Period.of(1, 2, 3, 0, 0, 0, 0);
        LocalDate t = TEST_2007_07_15.plus(provider);
        assertEquals(t, LocalDate.of(2008, 9, 18));
    }

    public void test_plus_PeriodProvider_timeIgnored() {
        PeriodProvider provider = Period.of(1, 2, 3, 4, 5, 6, 7);
        LocalDate t = TEST_2007_07_15.plus(provider);
        assertEquals(t, LocalDate.of(2008, 9, 18));
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plus_PeriodProvider_notISOPeriod() {
        TEST_2007_07_15.plus(PeriodFields.of(2, MockOtherChronology.OTHER_MONTHS));
    }

    public void test_plus_PeriodProvider_zero() {
        LocalDate t = TEST_2007_07_15.plus(Period.ZERO);
        assertSame(t, TEST_2007_07_15);
    }

    @DataProvider(name="PlusPeriodProvider")
    Object[][] data_plus_PeriodProvider() {
        return new Object[][] {
            // plus(P1MnD) - push forward
            {LocalDate.of(2008, 1, 30), 0, 1, 1, LocalDate.of(2008, 3, 1)},
            {LocalDate.of(2008, 1, 30), 0, 1, 2, LocalDate.of(2008, 3, 2)},
            {LocalDate.of(2008, 1, 30), 0, 1, 31, LocalDate.of(2008, 3, 31)},
            {LocalDate.of(2008, 1, 30), 0, 1, 32, LocalDate.of(2008, 4, 1)},
            {LocalDate.of(2008, 1, 30), 0, 1, 33, LocalDate.of(2008, 4, 2)},
            
            // plus(P1M1D) - push forward
            {LocalDate.of(2008, 1, 28), 0, 1, 1, LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 1, 29), 0, 1, 1, LocalDate.of(2008, 3, 1)},
            {LocalDate.of(2008, 1, 30), 0, 1, 1, LocalDate.of(2008, 3, 1)},
            {LocalDate.of(2008, 1, 31), 0, 1, 1, LocalDate.of(2008, 3, 1)},
            {LocalDate.of(2008, 2, 1),  0, 1, 1, LocalDate.of(2008, 3, 2)},
            
            // plus(P1M2D) - push forward
            {LocalDate.of(2008, 1, 27), 0, 1, 2, LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 1, 28), 0, 1, 2, LocalDate.of(2008, 3, 1)},
            {LocalDate.of(2008, 1, 29), 0, 1, 2, LocalDate.of(2008, 3, 2)},
            {LocalDate.of(2008, 1, 30), 0, 1, 2, LocalDate.of(2008, 3, 2)},
            {LocalDate.of(2008, 1, 31), 0, 1, 2, LocalDate.of(2008, 3, 2)},
            {LocalDate.of(2008, 2, 1),  0, 1, 2, LocalDate.of(2008, 3, 3)},
            
            // plus(P1M-nD) - push back
            {LocalDate.of(2008, 1, 31), 0, 1, -1, LocalDate.of(2008, 2, 29)},  // within invalid Feb dates
            {LocalDate.of(2008, 1, 31), 0, 1, -2, LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 1, 31), 0, 1, -3, LocalDate.of(2008, 2, 28)},
            {LocalDate.of(2008, 1, 31), 0, 1, -4, LocalDate.of(2008, 2, 27)},
            {LocalDate.of(2008, 1, 31), 0, 1, -31, LocalDate.of(2008, 1, 31)},
            
            {LocalDate.of(2009, 1, 31), 0, 1, -1, LocalDate.of(2009, 2, 28)},  // within invalid Feb dates
            {LocalDate.of(2009, 1, 31), 0, 1, -2, LocalDate.of(2009, 2, 28)},  // within invalid Feb dates
            {LocalDate.of(2009, 1, 31), 0, 1, -3, LocalDate.of(2009, 2, 28)},
            {LocalDate.of(2009, 1, 31), 0, 1, -4, LocalDate.of(2009, 2, 27)},
            {LocalDate.of(2009, 1, 24), 0, 1, -8, LocalDate.of(2009, 2, 16)},
            {LocalDate.of(2009, 1, 16), 0, 1, -8, LocalDate.of(2009, 2, 8)},
            {LocalDate.of(2009, 1, 31), 0, 1, -31, LocalDate.of(2009, 1, 31)},
            
            // plus(P1M-1D) - push back
            {LocalDate.of(2008, 1, 29), 0, 1, -1, LocalDate.of(2008, 2, 28)},
            {LocalDate.of(2008, 1, 30), 0, 1, -1, LocalDate.of(2008, 2, 29)},  // within invalid Feb dates
            {LocalDate.of(2008, 1, 31), 0, 1, -1, LocalDate.of(2008, 2, 29)},  // within invalid Feb dates
            {LocalDate.of(2008, 2, 1),  0, 1, -1, LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 2, 2),  0, 1, -1, LocalDate.of(2008, 3, 1)},
            
            // plus(P1M-2D) - push back
            {LocalDate.of(2008, 1, 30), 0, 1, -2, LocalDate.of(2008, 2, 28)},
            {LocalDate.of(2008, 1, 31), 0, 1, -2, LocalDate.of(2008, 2, 29)},  // within invalid Feb dates
            {LocalDate.of(2008, 2, 1),  0, 1, -2, LocalDate.of(2008, 2, 28)},  // to last of Feb, then day before
            {LocalDate.of(2008, 2, 2),  0, 1, -2, LocalDate.of(2008, 2, 29)},  // to first of Mar, then day before
            {LocalDate.of(2008, 2, 3),  0, 1, -2, LocalDate.of(2008, 3, 1)},
            {LocalDate.of(2008, 1, 20),  0, 1, -2, LocalDate.of(2008, 2, 18)},
            
            // plus(P-1M1D) - push forward
            {LocalDate.of(2008, 3, 28), 0, -1, 1, LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 3, 29), 0, -1, 1, LocalDate.of(2008, 3, 1)},
            {LocalDate.of(2008, 3, 30), 0, -1, 1, LocalDate.of(2008, 3, 1)},
            {LocalDate.of(2008, 3, 31), 0, -1, 1, LocalDate.of(2008, 3, 1)},
            {LocalDate.of(2008, 4, 1),  0, -1, 1, LocalDate.of(2008, 3, 2)},
            
            // plus(P-1M-1D) - push back
            {LocalDate.of(2008, 3, 29), 0, -1, -1, LocalDate.of(2008, 2, 28)},
            {LocalDate.of(2008, 3, 30), 0, -1, -1, LocalDate.of(2008, 2, 29)},  // within invalid Feb dates
            {LocalDate.of(2008, 3, 31), 0, -1, -1, LocalDate.of(2008, 2, 29)},  // within invalid Feb dates
            {LocalDate.of(2008, 4, 1),  0, -1, -1, LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 4, 2),  0, -1, -1, LocalDate.of(2008, 3, 1)},
            
            // plus(P1Y1M) - simple month adjust
            {LocalDate.of(2008, 1, 27), 1, 1, 0, LocalDate.of(2009, 2, 27)},
            {LocalDate.of(2008, 1, 28), 1, 1, 0, LocalDate.of(2009, 2, 28)},
            {LocalDate.of(2008, 1, 29), 1, 1, 0, LocalDate.of(2009, 2, 28)},
            {LocalDate.of(2008, 1, 30), 1, 1, 0, LocalDate.of(2009, 2, 28)},
            {LocalDate.of(2008, 1, 31), 1, 1, 0, LocalDate.of(2009, 2, 28)},
            {LocalDate.of(2008, 2, 1),  1, 1, 0, LocalDate.of(2009, 3, 1)},
            
            // plus(P1Y1M1D) - push forward
            {LocalDate.of(2008, 1, 27), 1, 1, 1, LocalDate.of(2009, 2, 28)},
            {LocalDate.of(2008, 1, 28), 1, 1, 1, LocalDate.of(2009, 3, 1)},
            {LocalDate.of(2008, 1, 29), 1, 1, 1, LocalDate.of(2009, 3, 1)},
            {LocalDate.of(2008, 1, 30), 1, 1, 1, LocalDate.of(2009, 3, 1)},
            {LocalDate.of(2008, 1, 31), 1, 1, 1, LocalDate.of(2009, 3, 1)},
            {LocalDate.of(2008, 2, 1),  1, 1, 1, LocalDate.of(2009, 3, 2)},
            
            // plus(P1M) - as per plusMonths() - simple month adjust
            {LocalDate.of(2008, 1, 28), 0, 1, 0, LocalDate.of(2008, 2, 28)},
            {LocalDate.of(2008, 1, 29), 0, 1, 0, LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 1, 30), 0, 1, 0, LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 1, 31), 0, 1, 0, LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 2, 1),  0, 1, 0, LocalDate.of(2008, 3, 1)},
            
            // plus(PnD) from Jan - as per plusDays()
            {LocalDate.of(2008, 1, 30), 0, 0, 1, LocalDate.of(2008, 1, 31)},
            {LocalDate.of(2008, 1, 30), 0, 0, 2, LocalDate.of(2008, 2, 1)},
            {LocalDate.of(2008, 1, 30), 0, 0, 29, LocalDate.of(2008, 2, 28)},
            {LocalDate.of(2008, 1, 30), 0, 0, 30, LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 1, 30), 0, 0, 31, LocalDate.of(2008, 3, 1)},
            
            // plus(PnD) from Feb - as per plusDays()
            {LocalDate.of(2008, 2, 27), 0, 0, 1, LocalDate.of(2008, 2, 28)},
            {LocalDate.of(2008, 2, 27), 0, 0, 2, LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 2, 27), 0, 0, 3, LocalDate.of(2008, 3, 1)},
            
            // plus(PnD) from Mar - as per plusDays()
            {LocalDate.of(2008, 3, 2), 0, 0, -1, LocalDate.of(2008, 3, 1)},
            {LocalDate.of(2008, 3, 2), 0, 0, -2, LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 3, 2), 0, 0, -3, LocalDate.of(2008, 2, 28)},
            
            // plus(P1YnD) from 29 Feb
            {LocalDate.of(2008, 2, 29), 1, 0, 0, LocalDate.of(2009, 2, 28)},
            {LocalDate.of(2008, 2, 29), 1, 0, -1, LocalDate.of(2009, 2, 28)},
            {LocalDate.of(2008, 2, 29), 1, 0, -2, LocalDate.of(2009, 2, 27)},
            {LocalDate.of(2008, 2, 29), 1, 0, 1, LocalDate.of(2009, 3, 1)},
            {LocalDate.of(2008, 2, 29), 1, 0, 2, LocalDate.of(2009, 3, 2)},
        };
    }

    @Test(dataProvider="PlusPeriodProvider")
    public void test_plus_PeriodProvider(LocalDate base, int years, int months, int days, LocalDate expected) {
        PeriodProvider provider = Period.ofDateFields(years, months, days);
        LocalDate t = base.plus(provider);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_plus_PeriodProvider_null() {
        TEST_2007_07_15.plus((PeriodProvider) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_plus_PeriodProvider_badProvider() {
        TEST_2007_07_15.plus(new MockPeriodProviderReturnsNull());
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plus_PeriodProvider_invalidTooLarge() {
        PeriodProvider provider = Period.ofYears(1);
        LocalDate.of(Year.MAX_YEAR, 1, 1).plus(provider);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plus_PeriodProvider_invalidTooSmall() {
        PeriodProvider provider = Period.ofYears(-1);
        LocalDate.of(Year.MIN_YEAR, 1, 1).plus(provider);
    }

    //-----------------------------------------------------------------------
    // plusYears()
    //-----------------------------------------------------------------------
    public void test_plusYears_long_normal() {
        LocalDate t = TEST_2007_07_15.plusYears(1);
        assertEquals(t, LocalDate.of(2008, 7, 15));
    }

    public void test_plusYears_long_noChange() {
        LocalDate t = TEST_2007_07_15.plusYears(0);
        assertSame(t, TEST_2007_07_15);
    }

    public void test_plusYears_long_negative() {
        LocalDate t = TEST_2007_07_15.plusYears(-1);
        assertEquals(t, LocalDate.of(2006, 7, 15));
    }

    public void test_plusYears_long_adjustDay() {
        LocalDate t = LocalDate.of(2008, 2, 29).plusYears(1);
        LocalDate expected = LocalDate.of(2009, 2, 28);
        assertEquals(t, expected);
    }

    public void test_plusYears_long_big() {
        long years = 20L + Year.MAX_YEAR;
        LocalDate test = LocalDate.of(-40, 6, 1).plusYears(years);
        assertEquals(test, LocalDate.of((int) (-40L + years), 6, 1));
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusYears_long_invalidTooLarge() {
        LocalDate test = LocalDate.of(Year.MAX_YEAR, 6, 1);
        test.plusYears(1);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusYears_long_invalidTooLargeMaxAddMax() {
        LocalDate test = LocalDate.of(Year.MAX_YEAR, 12, 1);
        test.plusYears(Long.MAX_VALUE);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusYears_long_invalidTooLargeMaxAddMin() {
        LocalDate test = LocalDate.of(Year.MAX_YEAR, 12, 1);
        test.plusYears(Long.MIN_VALUE);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusYears_long_invalidTooSmall_validInt() {
        LocalDate.of(Year.MIN_YEAR, 1, 1).plusYears(-1);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusYears_long_invalidTooSmall_invalidInt() {
        LocalDate.of(Year.MIN_YEAR, 1, 1).plusYears(-10);
    }

    //-----------------------------------------------------------------------
    public void test_plusYears_long_DateResolver_normal() {
        LocalDate t = TEST_2007_07_15.plusYears(1, DateResolvers.nextValid());
        assertEquals(t, LocalDate.of(2008, 7, 15));
    }

    public void test_plusYears_long_DateResolver_noChange() {
        LocalDate t = TEST_2007_07_15.plusYears(0, DateResolvers.nextValid());
        assertSame(t, TEST_2007_07_15);
    }

    public void test_plusYears_long_DateResolver_negative() {
        LocalDate t = TEST_2007_07_15.plusYears(-1, DateResolvers.nextValid());
        assertEquals(t, LocalDate.of(2006, 7, 15));
    }

    public void test_plusYears_long_DateResolver_adjustDay() {
        LocalDate t = LocalDate.of(2008, 2, 29).plusYears(1, DateResolvers.nextValid());
        LocalDate expected = LocalDate.of(2009, 3, 1);
        assertEquals(t, expected);
    }

    public void test_plusYears_long_DateResolver_big() {
        long years = 20L + Year.MAX_YEAR;
        LocalDate test = LocalDate.of(-40, 6, 1).plusYears(years, DateResolvers.nextValid());
        assertEquals(test, LocalDate.of((int) (-40L + years), 6, 1));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_plusYears_long_DateResolver_null_adjustDay() {
        TEST_2007_07_15.plusYears(1, new MockDateResolverReturnsNull());
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusYears_long_DateResolver_invalidTooLarge() {
        LocalDate.of(Year.MAX_YEAR, 1, 1).plusYears(1, DateResolvers.nextValid());
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusYears_long_DateResolver_invalidTooSmall_validInt() {
        LocalDate.of(Year.MIN_YEAR, 1, 1).plusYears(-1, DateResolvers.nextValid());
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusYears_long_DateResolver_invalidTooSmall_invalidInt() {
        LocalDate.of(Year.MIN_YEAR, 1, 1).plusYears(-10, DateResolvers.nextValid());
    }

    //-----------------------------------------------------------------------
    // plusMonths()
    //-----------------------------------------------------------------------
    public void test_plusMonths_long_normal() {
        LocalDate t = TEST_2007_07_15.plusMonths(1);
        assertEquals(t, LocalDate.of(2007, 8, 15));
    }

    public void test_plusMonths_long_noChange() {
        LocalDate t = TEST_2007_07_15.plusMonths(0);
        assertSame(t, TEST_2007_07_15);
    }

    public void test_plusMonths_long_overYears() {
        LocalDate t = TEST_2007_07_15.plusMonths(25);
        assertEquals(t, LocalDate.of(2009, 8, 15));
    }

    public void test_plusMonths_long_negative() {
        LocalDate t = TEST_2007_07_15.plusMonths(-1);
        assertEquals(t, LocalDate.of(2007, 6, 15));
    }

    public void test_plusMonths_long_negativeAcrossYear() {
        LocalDate t = TEST_2007_07_15.plusMonths(-7);
        assertEquals(t, LocalDate.of(2006, 12, 15));
    }

    public void test_plusMonths_long_negativeOverYears() {
        LocalDate t = TEST_2007_07_15.plusMonths(-31);
        assertEquals(t, LocalDate.of(2004, 12, 15));
    }

    public void test_plusMonths_long_adjustDayFromLeapYear() {
        LocalDate t = LocalDate.of(2008, 2, 29).plusMonths(12);
        LocalDate expected = LocalDate.of(2009, 2, 28);
        assertEquals(t, expected);
    }

    public void test_plusMonths_long_adjustDayFromMonthLength() {
        LocalDate t = LocalDate.of(2007, 3, 31).plusMonths(1);
        LocalDate expected = LocalDate.of(2007, 4, 30);
        assertEquals(t, expected);
    }

    public void test_plusMonths_long_big() {
        long months = 20L + Integer.MAX_VALUE;
        LocalDate test = LocalDate.of(-40, 6, 1).plusMonths(months);
        assertEquals(test, LocalDate.of((int) (-40L + months / 12), 6 + (int) (months % 12), 1));
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_plusMonths_long_invalidTooLarge() {
        LocalDate.of(Year.MAX_YEAR, 12, 1).plusMonths(1);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusMonths_long_invalidTooLargeMaxAddMax() {
        LocalDate test = LocalDate.of(Year.MAX_YEAR, 12, 1);
        test.plusMonths(Long.MAX_VALUE);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusMonths_long_invalidTooLargeMaxAddMin() {
        LocalDate test = LocalDate.of(Year.MAX_YEAR, 12, 1);
        test.plusMonths(Long.MIN_VALUE);
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_plusMonths_long_invalidTooSmall() {
        LocalDate.of(Year.MIN_YEAR, 1, 1).plusMonths(-1);
    }

    //-----------------------------------------------------------------------
    public void test_plusMonths_long_DateResolver_normal() {
        LocalDate t = TEST_2007_07_15.plusMonths(1, DateResolvers.nextValid());
        assertEquals(t, LocalDate.of(2007, 8, 15));
    }

    public void test_plusMonths_long_DateResolver_noChange() {
        LocalDate t = TEST_2007_07_15.plusMonths(0, DateResolvers.nextValid());
        assertSame(t, TEST_2007_07_15);
    }

    public void test_plusMonths_long_DateResolver_overYears() {
        LocalDate t = TEST_2007_07_15.plusMonths(25, DateResolvers.nextValid());
        assertEquals(t, LocalDate.of(2009, 8, 15));
    }

    public void test_plusMonths_long_DateResolver_negative() {
        LocalDate t = TEST_2007_07_15.plusMonths(-1, DateResolvers.nextValid());
        assertEquals(t, LocalDate.of(2007, 6, 15));
    }

    public void test_plusMonths_long_DateResolver_negativeAcrossYear() {
        LocalDate t = TEST_2007_07_15.plusMonths(-7, DateResolvers.nextValid());
        assertEquals(t, LocalDate.of(2006, 12, 15));
    }

    public void test_plusMonths_long_DateResolver_negativeOverYears() {
        LocalDate t = TEST_2007_07_15.plusMonths(-31, DateResolvers.nextValid());
        assertEquals(t, LocalDate.of(2004, 12, 15));
    }

    public void test_plusMonths_long_DateResolver_adjustDayFromLeapYear() {
        LocalDate t = LocalDate.of(2008, 2, 29).plusMonths(12, DateResolvers.nextValid());
        LocalDate expected = LocalDate.of(2009, 3, 1);
        assertEquals(t, expected);
    }

    public void test_plusMonths_long_DateResolver_adjustDayFromMonthLength() {
        LocalDate t = LocalDate.of(2007, 3, 31).plusMonths(1, DateResolvers.nextValid());
        LocalDate expected = LocalDate.of(2007, 5, 1);
        assertEquals(t, expected);
    }

    public void test_plusMonths_long_DateResolver_big() {
        long months = 20L + Integer.MAX_VALUE;
        LocalDate test = LocalDate.of(-40, 6, 1).plusMonths(months, DateResolvers.nextValid());
        assertEquals(test, LocalDate.of((int) (-40L + months / 12), 6 + (int) (months % 12), 1));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_plusMonths_long_DateResolver_null_adjustDay() {
        TEST_2007_07_15.plusMonths(1, new MockDateResolverReturnsNull());
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_plusMonths_long_DateResolver_invalidTooLarge() {
        LocalDate.of(Year.MAX_YEAR, 12, 1).plusMonths(1, DateResolvers.nextValid());
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusMonths_long_DateResolver_invalidTooLargeMaxAddMax() {
        LocalDate test = LocalDate.of(Year.MAX_YEAR, 12, 1);
        test.plusMonths(Long.MAX_VALUE, DateResolvers.nextValid());
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusMonths_long_DateResolver_invalidTooLargeMaxAddMin() {
        LocalDate test = LocalDate.of(Year.MAX_YEAR, 12, 1);
        test.plusMonths(Long.MIN_VALUE, DateResolvers.nextValid());
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_plusMonths_long_DateResolver_invalidTooSmall() {
        LocalDate.of(Year.MIN_YEAR, 1, 1).plusMonths(-1, DateResolvers.nextValid());
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
    
    @Test(dataProvider="samplePlusWeeksSymmetry")
    public void test_plusWeeks_symmetry(LocalDate reference) {
        for (int weeks = 0; weeks < 365 * 8; weeks++) {
            LocalDate t = reference.plusWeeks(weeks).plusWeeks(-weeks);
            assertEquals(t, reference);

            t = reference.plusWeeks(-weeks).plusWeeks(weeks);
            assertEquals(t, reference);
        }
    }

    public void test_plusWeeks_normal() {
        LocalDate t = TEST_2007_07_15.plusWeeks(1);
        assertEquals(t, LocalDate.of(2007, 7, 22));
    }

    public void test_plusWeeks_noChange() {
        LocalDate t = TEST_2007_07_15.plusWeeks(0);
        assertSame(t, TEST_2007_07_15);
    }

    public void test_plusWeeks_overMonths() {
        LocalDate t = TEST_2007_07_15.plusWeeks(9);
        assertEquals(t, LocalDate.of(2007, 9, 16));
    }

    public void test_plusWeeks_overYears() {
        LocalDate t = LocalDate.of(2006, 7, 16).plusWeeks(52);
        assertEquals(t, TEST_2007_07_15);
    }

    public void test_plusWeeks_overLeapYears() {
        LocalDate t = TEST_2007_07_15.plusYears(-1).plusWeeks(104);
        assertEquals(t, LocalDate.of(2008, 7, 12));
    }

    public void test_plusWeeks_negative() {
        LocalDate t = TEST_2007_07_15.plusWeeks(-1);
        assertEquals(t, LocalDate.of(2007, 7, 8));
    }

    public void test_plusWeeks_negativeAcrossYear() {
        LocalDate t = TEST_2007_07_15.plusWeeks(-28);
        assertEquals(t, LocalDate.of(2006, 12, 31));
    }

    public void test_plusWeeks_negativeOverYears() {
        LocalDate t = TEST_2007_07_15.plusWeeks(-104);
        assertEquals(t, LocalDate.of(2005, 7, 17));
    }

    public void test_plusWeeks_maximum() {
        LocalDate t = LocalDate.of(Year.MAX_YEAR, 12, 24).plusWeeks(1);
        LocalDate expected = LocalDate.of(Year.MAX_YEAR, 12, 31);
        assertEquals(t, expected);
    }

    public void test_plusWeeks_minimum() {
        LocalDate t = LocalDate.of(Year.MIN_YEAR, 1, 8).plusWeeks(-1);
        LocalDate expected = LocalDate.of(Year.MIN_YEAR, 1, 1);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_plusWeeks_invalidTooLarge() {
        LocalDate.of(Year.MAX_YEAR, 12, 25).plusWeeks(1);
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_plusWeeks_invalidTooSmall() {
        LocalDate.of(Year.MIN_YEAR, 1, 7).plusWeeks(-1);
    }

    @Test(expectedExceptions={ArithmeticException.class})
    public void test_plusWeeks_invalidMaxMinusMax() {
        LocalDate.of(Year.MAX_YEAR, 12, 25).plusWeeks(Long.MAX_VALUE);
    }

    @Test(expectedExceptions={ArithmeticException.class})
    public void test_plusWeeks_invalidMaxMinusMin() {
        LocalDate.of(Year.MAX_YEAR, 12, 25).plusWeeks(Long.MIN_VALUE);
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
    
    @Test(dataProvider="samplePlusDaysSymmetry")
    public void test_plusDays_symmetry(LocalDate reference) {
        for (int days = 0; days < 365 * 8; days++) {
            LocalDate t = reference.plusDays(days).plusDays(-days);
            assertEquals(t, reference);

            t = reference.plusDays(-days).plusDays(days);
            assertEquals(t, reference);
        }
    }

    public void test_plusDays_normal() {
        LocalDate t = TEST_2007_07_15.plusDays(1);
        assertEquals(t, LocalDate.of(2007, 7, 16));
    }

    public void test_plusDays_noChange() {
        LocalDate t = TEST_2007_07_15.plusDays(0);
        assertSame(t, TEST_2007_07_15);
    }

    public void test_plusDays_overMonths() {
        LocalDate t = TEST_2007_07_15.plusDays(62);
        assertEquals(t, LocalDate.of(2007, 9, 15));
    }

    public void test_plusDays_overYears() {
        LocalDate t = LocalDate.of(2006, 7, 14).plusDays(366);
        assertEquals(t, TEST_2007_07_15);
    }

    public void test_plusDays_overLeapYears() {
        LocalDate t = TEST_2007_07_15.plusYears(-1).plusDays(365 + 366);
        assertEquals(t, LocalDate.of(2008, 7, 15));
    }

    public void test_plusDays_negative() {
        LocalDate t = TEST_2007_07_15.plusDays(-1);
        assertEquals(t, LocalDate.of(2007, 7, 14));
    }

    public void test_plusDays_negativeAcrossYear() {
        LocalDate t = TEST_2007_07_15.plusDays(-196);
        assertEquals(t, LocalDate.of(2006, 12, 31));
    }

    public void test_plusDays_negativeOverYears() {
        LocalDate t = TEST_2007_07_15.plusDays(-730);
        assertEquals(t, LocalDate.of(2005, 7, 15));
    }

    public void test_plusDays_maximum() {
        LocalDate t = LocalDate.of(Year.MAX_YEAR, 12, 30).plusDays(1);
        LocalDate expected = LocalDate.of(Year.MAX_YEAR, 12, 31);
        assertEquals(t, expected);
    }

    public void test_plusDays_minimum() {
        LocalDate t = LocalDate.of(Year.MIN_YEAR, 1, 2).plusDays(-1);
        LocalDate expected = LocalDate.of(Year.MIN_YEAR, 1, 1);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_plusDays_invalidTooLarge() {
        LocalDate.of(Year.MAX_YEAR, 12, 31).plusDays(1);
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_plusDays_invalidTooSmall() {
        LocalDate.of(Year.MIN_YEAR, 1, 1).plusDays(-1);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusDays_overflowTooLarge() {
        LocalDate.of(Year.MAX_YEAR, 12, 31).plusDays(Long.MAX_VALUE);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusDays_overflowTooSmall() {
        LocalDate.of(Year.MIN_YEAR, 1, 1).plusDays(Long.MIN_VALUE);
    }

    //-----------------------------------------------------------------------
    // minus(PeriodProvider)
    //-----------------------------------------------------------------------
    public void test_minus_PeriodProvider() {
        PeriodProvider provider = Period.of(1, 2, 3, 0, 0, 0, 0);
        LocalDate t = TEST_2007_07_15.minus(provider);
        assertEquals(t, LocalDate.of(2006, 5, 12));
    }

    public void test_minus_PeriodProvider_timeIgnored() {
        PeriodProvider provider = Period.of(1, 2, 3, 4, 5, 6, 7);
        LocalDate t = TEST_2007_07_15.minus(provider);
        assertEquals(t, LocalDate.of(2006, 5, 12));
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minus_PeriodProvider_notISOPeriod() {
        TEST_2007_07_15.minus(PeriodFields.of(2, MockOtherChronology.OTHER_MONTHS));
    }

    public void test_minus_PeriodProvider_zero() {
        LocalDate t = TEST_2007_07_15.minus(Period.ZERO);
        assertSame(t, TEST_2007_07_15);
    }

    @DataProvider(name="MinusPeriodProvider")
    Object[][] data_minus_PeriodProvider() {
        return new Object[][] {
            // minus(P1MnD)
            {LocalDate.of(2008, 4, 1), 0, 1, -2, LocalDate.of(2008, 3, 3)},
            {LocalDate.of(2008, 4, 1), 0, 1, -1, LocalDate.of(2008, 3, 2)},
            {LocalDate.of(2008, 4, 1), 0, 1, 0,  LocalDate.of(2008, 3, 1)},
            {LocalDate.of(2008, 4, 1), 0, 1, 1,  LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 4, 1), 0, 1, 2,  LocalDate.of(2008, 2, 28)},
            {LocalDate.of(2008, 4, 1), 0, 1, 29, LocalDate.of(2008, 2, 1)},
            {LocalDate.of(2008, 4, 1), 0, 1, 30, LocalDate.of(2008, 1, 31)},
            
            {LocalDate.of(2008, 3, 31), 0, 1, -2, LocalDate.of(2008, 3, 2)},
            {LocalDate.of(2008, 3, 31), 0, 1, -1, LocalDate.of(2008, 3, 1)},
            {LocalDate.of(2008, 3, 31), 0, 1, 0,  LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 3, 31), 0, 1, 1,  LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 3, 31), 0, 1, 2,  LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 3, 31), 0, 1, 3,  LocalDate.of(2008, 2, 28)},
            {LocalDate.of(2008, 3, 31), 0, 1, 30, LocalDate.of(2008, 2, 1)},
            {LocalDate.of(2008, 3, 31), 0, 1, 31, LocalDate.of(2008, 1, 31)},
            
            {LocalDate.of(2008, 3, 30), 0, 1, -2, LocalDate.of(2008, 3, 2)},
            {LocalDate.of(2008, 3, 30), 0, 1, -1, LocalDate.of(2008, 3, 1)},
            {LocalDate.of(2008, 3, 30), 0, 1, 0,  LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 3, 30), 0, 1, 1,  LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 3, 30), 0, 1, 2,  LocalDate.of(2008, 2, 28)},
            {LocalDate.of(2008, 3, 30), 0, 1, 29, LocalDate.of(2008, 2, 1)},
            {LocalDate.of(2008, 3, 30), 0, 1, 30, LocalDate.of(2008, 1, 31)},
            
            {LocalDate.of(2008, 3, 29), 0, 1, -2, LocalDate.of(2008, 3, 2)},
            {LocalDate.of(2008, 3, 29), 0, 1, -1, LocalDate.of(2008, 3, 1)},
            {LocalDate.of(2008, 3, 29), 0, 1, 0,  LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 3, 29), 0, 1, 1,  LocalDate.of(2008, 2, 28)},
            {LocalDate.of(2008, 3, 29), 0, 1, 2,  LocalDate.of(2008, 2, 27)},
            {LocalDate.of(2008, 3, 29), 0, 1, 28, LocalDate.of(2008, 2, 1)},
            {LocalDate.of(2008, 3, 29), 0, 1, 29, LocalDate.of(2008, 1, 31)},
            
            {LocalDate.of(2008, 3, 28), 0, 1, -2, LocalDate.of(2008, 3, 1)},
            {LocalDate.of(2008, 3, 28), 0, 1, -1, LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 3, 28), 0, 1, 0,  LocalDate.of(2008, 2, 28)},
            {LocalDate.of(2008, 3, 28), 0, 1, 1,  LocalDate.of(2008, 2, 27)},
            {LocalDate.of(2008, 3, 28), 0, 1, 2,  LocalDate.of(2008, 2, 26)},
            {LocalDate.of(2008, 3, 28), 0, 1, 27, LocalDate.of(2008, 2, 1)},
            {LocalDate.of(2008, 3, 28), 0, 1, 28, LocalDate.of(2008, 1, 31)},
            
            // minus(P-1MnD)
            {LocalDate.of(2008, 2, 1), 0, -1, -1, LocalDate.of(2008, 3, 2)},
            {LocalDate.of(2008, 2, 1), 0, -1, 0,  LocalDate.of(2008, 3, 1)},
            {LocalDate.of(2008, 2, 1), 0, -1, 1,  LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 2, 1), 0, -1, 2,  LocalDate.of(2008, 2, 28)},
            
            {LocalDate.of(2008, 1, 31), 0, -1, 3,  LocalDate.of(2008, 2, 28)},
            {LocalDate.of(2008, 1, 31), 0, -1, 2,  LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 1, 31), 0, -1, 1,  LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 1, 31), 0, -1, 0,  LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 1, 31), 0, -1, -1, LocalDate.of(2008, 3, 1)},
            {LocalDate.of(2008, 1, 31), 0, -1, -2, LocalDate.of(2008, 3, 2)},
            
            {LocalDate.of(2008, 1, 30), 0, -1, 2,  LocalDate.of(2008, 2, 28)},
            {LocalDate.of(2008, 1, 30), 0, -1, 1,  LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 1, 30), 0, -1, 0,  LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 1, 30), 0, -1, -1, LocalDate.of(2008, 3, 1)},
            {LocalDate.of(2008, 1, 30), 0, -1, -2, LocalDate.of(2008, 3, 2)},
            
            {LocalDate.of(2008, 1, 29), 0, -1, 1,  LocalDate.of(2008, 2, 28)},
            {LocalDate.of(2008, 1, 29), 0, -1, 0,  LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 1, 29), 0, -1, -1, LocalDate.of(2008, 3, 1)},
            {LocalDate.of(2008, 1, 29), 0, -1, -2, LocalDate.of(2008, 3, 2)},
            
            // minus(P1Y1M)
            {LocalDate.of(2008, 3, 27), 1, 1, 0, LocalDate.of(2007, 2, 27)},
            {LocalDate.of(2008, 3, 28), 1, 1, 0, LocalDate.of(2007, 2, 28)},
            {LocalDate.of(2008, 3, 29), 1, 1, 0, LocalDate.of(2007, 2, 28)},
            {LocalDate.of(2008, 3, 30), 1, 1, 0, LocalDate.of(2007, 2, 28)},
            {LocalDate.of(2008, 3, 31), 1, 1, 0, LocalDate.of(2007, 2, 28)},
            {LocalDate.of(2008, 4, 1),  1, 1, 0, LocalDate.of(2007, 3, 1)},
            
            // minus(P1Y1M1D)
            {LocalDate.of(2008, 3, 31), 1, 1, -2, LocalDate.of(2007, 3, 2)},
            {LocalDate.of(2008, 3, 31), 1, 1, -1, LocalDate.of(2007, 3, 1)},
            {LocalDate.of(2008, 3, 31), 1, 1, 0,  LocalDate.of(2007, 2, 28)},
            {LocalDate.of(2008, 3, 31), 1, 1, 1,  LocalDate.of(2007, 2, 28)},
            {LocalDate.of(2008, 3, 31), 1, 1, 2,  LocalDate.of(2007, 2, 28)},
            {LocalDate.of(2008, 3, 31), 1, 1, 3,  LocalDate.of(2007, 2, 28)},
            {LocalDate.of(2008, 3, 31), 1, 1, 4,  LocalDate.of(2007, 2, 27)},
            
            // minus(PnD) from Jan - as per minusDays()
            {LocalDate.of(2008, 1, 30), 0, 0, 1, LocalDate.of(2008, 1, 29)},
            {LocalDate.of(2008, 1, 30), 0, 0, 2, LocalDate.of(2008, 1, 28)},
            {LocalDate.of(2008, 1, 30), 0, 0, 30, LocalDate.of(2007, 12, 31)},
            
            // minus(PnD) from Mar - as per minusDays()
            {LocalDate.of(2008, 3, 2), 0, 0, 1, LocalDate.of(2008, 3, 1)},
            {LocalDate.of(2008, 3, 2), 0, 0, 2, LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 3, 2), 0, 0, 3, LocalDate.of(2008, 2, 28)},
        };
    }

    @Test(dataProvider="MinusPeriodProvider")
    public void test_minus_PeriodProvider(LocalDate base, int years, int months, int days, LocalDate expected) {
        PeriodProvider provider = Period.ofDateFields(years, months, days);
        LocalDate t = base.minus(provider);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_minus_PeriodProvider_null() {
        TEST_2007_07_15.minus((PeriodProvider) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_minus_PeriodProvider_badProvider() {
        TEST_2007_07_15.minus(new MockPeriodProviderReturnsNull());
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minus_PeriodProvider_invalidTooLarge() {
        PeriodProvider provider = Period.ofYears(-1);
        LocalDate.of(Year.MAX_YEAR, 1, 1).minus(provider);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minus_PeriodProvider_invalidTooSmall() {
        PeriodProvider provider = Period.ofYears(1);
        LocalDate.of(Year.MIN_YEAR, 1, 1).minus(provider);
    }

    //-----------------------------------------------------------------------
    // minusYears()
    //-----------------------------------------------------------------------
    public void test_minusYears_long_normal() {
        LocalDate t = TEST_2007_07_15.minusYears(1);
        assertEquals(t, LocalDate.of(2006, 7, 15));
    }

    public void test_minusYears_long_noChange() {
        LocalDate t = TEST_2007_07_15.minusYears(0);
        assertSame(t, TEST_2007_07_15);
    }

    public void test_minusYears_long_negative() {
        LocalDate t = TEST_2007_07_15.minusYears(-1);
        assertEquals(t, LocalDate.of(2008, 7, 15));
    }

    public void test_minusYears_long_adjustDay() {
        LocalDate t = LocalDate.of(2008, 2, 29).minusYears(1);
        LocalDate expected = LocalDate.of(2007, 2, 28);
        assertEquals(t, expected);
    }

    public void test_minusYears_long_big() {
        long years = 20L + Year.MAX_YEAR;
        LocalDate test = LocalDate.of(40, 6, 1).minusYears(years);
        assertEquals(test, LocalDate.of((int) (40L - years), 6, 1));
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusYears_long_invalidTooLarge() {
        LocalDate test = LocalDate.of(Year.MAX_YEAR, 6, 1);
        test.minusYears(-1);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusYears_long_invalidTooLargeMaxAddMax() {
        LocalDate test = LocalDate.of(Year.MAX_YEAR, 12, 1);
        test.minusYears(Long.MAX_VALUE);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusYears_long_invalidTooLargeMaxAddMin() {
        LocalDate test = LocalDate.of(Year.MAX_YEAR, 12, 1);
        test.minusYears(Long.MIN_VALUE);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusYears_long_invalidTooSmall() {
        LocalDate.of(Year.MIN_YEAR, 1, 1).minusYears(1);
    }

    //-----------------------------------------------------------------------
    public void test_minusYears_long_DateResolver_normal() {
        LocalDate t = TEST_2007_07_15.minusYears(1, DateResolvers.nextValid());
        assertEquals(t, LocalDate.of(2006, 7, 15));
    }

    public void test_minusYears_long_DateResolver_noChange() {
        LocalDate t = TEST_2007_07_15.minusYears(0, DateResolvers.nextValid());
        assertSame(t, TEST_2007_07_15);
    }

    public void test_minusYears_long_DateResolver_negative() {
        LocalDate t = TEST_2007_07_15.minusYears(-1, DateResolvers.nextValid());
        assertEquals(t, LocalDate.of(2008, 7, 15));
    }

    public void test_minusYears_long_DateResolver_adjustDay() {
        LocalDate t = LocalDate.of(2008, 2, 29).minusYears(1, DateResolvers.nextValid());
        LocalDate expected = LocalDate.of(2007, 3, 1);
        assertEquals(t, expected);
    }

    public void test_minusYears_long_DateResolver_big() {
        long years = 20L + Year.MAX_YEAR;
        LocalDate test = LocalDate.of(40, 6, 1).minusYears(years, DateResolvers.nextValid());
        assertEquals(test, LocalDate.of((int) (40L - years), 6, 1));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_minusYears_long_DateResolver_null_adjustDay() {
        TEST_2007_07_15.minusYears(1, new MockDateResolverReturnsNull());
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusYears_long_DateResolver_invalidTooLarge() {
        LocalDate.of(Year.MAX_YEAR, 1, 1).minusYears(-1, DateResolvers.nextValid());
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusYears_long_DateResolver_invalidTooSmall_validInt() {
        LocalDate.of(Year.MIN_YEAR, 1, 1).minusYears(1, DateResolvers.nextValid());
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusYears_long_DateResolver_invalidTooSmall_invalidInt() {
        LocalDate.of(Year.MIN_YEAR, 1, 1).minusYears(10, DateResolvers.nextValid());
    }

    //-----------------------------------------------------------------------
    // minusMonths()
    //-----------------------------------------------------------------------
    public void test_minusMonths_long_normal() {
        LocalDate t = TEST_2007_07_15.minusMonths(1);
        assertEquals(t, LocalDate.of(2007, 6, 15));
    }

    public void test_minusMonths_long_noChange() {
        LocalDate t = TEST_2007_07_15.minusMonths(0);
        assertSame(t, TEST_2007_07_15);
    }

    public void test_minusMonths_long_overYears() {
        LocalDate t = TEST_2007_07_15.minusMonths(25);
        assertEquals(t, LocalDate.of(2005, 6, 15));
    }

    public void test_minusMonths_long_negative() {
        LocalDate t = TEST_2007_07_15.minusMonths(-1);
        assertEquals(t, LocalDate.of(2007, 8, 15));
    }

    public void test_minusMonths_long_negativeAcrossYear() {
        LocalDate t = TEST_2007_07_15.minusMonths(-7);
        assertEquals(t, LocalDate.of(2008, 2, 15));
    }

    public void test_minusMonths_long_negativeOverYears() {
        LocalDate t = TEST_2007_07_15.minusMonths(-31);
        assertEquals(t, LocalDate.of(2010, 2, 15));
    }

    public void test_minusMonths_long_adjustDayFromLeapYear() {
        LocalDate t = LocalDate.of(2008, 2, 29).minusMonths(12);
        LocalDate expected = LocalDate.of(2007, 2, 28);
        assertEquals(t, expected);
    }

    public void test_minusMonths_long_adjustDayFromMonthLength() {
        LocalDate t = LocalDate.of(2007, 3, 31).minusMonths(1);
        LocalDate expected = LocalDate.of(2007, 2, 28);
        assertEquals(t, expected);
    }

    public void test_minusMonths_long_big() {
        long months = 20L + Integer.MAX_VALUE;
        LocalDate test = LocalDate.of(40, 6, 1).minusMonths(months);
        assertEquals(test, LocalDate.of((int) (40L - months / 12), 6 - (int) (months % 12), 1));
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_minusMonths_long_invalidTooLarge() {
        LocalDate.of(Year.MAX_YEAR, 12, 1).minusMonths(-1);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusMonths_long_invalidTooLargeMaxAddMax() {
        LocalDate test = LocalDate.of(Year.MAX_YEAR, 12, 1);
        test.minusMonths(Long.MAX_VALUE);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusMonths_long_invalidTooLargeMaxAddMin() {
        LocalDate test = LocalDate.of(Year.MAX_YEAR, 12, 1);
        test.minusMonths(Long.MIN_VALUE);
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_minusMonths_long_invalidTooSmall() {
        LocalDate.of(Year.MIN_YEAR, 1, 1).minusMonths(1);
    }

    //-----------------------------------------------------------------------
    public void test_minusMonths_long_DateResolver_normal() {
        LocalDate t = TEST_2007_07_15.minusMonths(1, DateResolvers.nextValid());
        assertEquals(t, LocalDate.of(2007, 6, 15));
    }

    public void test_minusMonths_long_DateResolver_noChange() {
        LocalDate t = TEST_2007_07_15.minusMonths(0, DateResolvers.nextValid());
        assertSame(t, TEST_2007_07_15);
    }

    public void test_minusMonths_long_DateResolver_overYears() {
        LocalDate t = TEST_2007_07_15.minusMonths(25, DateResolvers.nextValid());
        assertEquals(t, LocalDate.of(2005, 6, 15));
    }

    public void test_minusMonths_long_DateResolver_negative() {
        LocalDate t = TEST_2007_07_15.minusMonths(-1, DateResolvers.nextValid());
        assertEquals(t, LocalDate.of(2007, 8, 15));
    }

    public void test_minusMonths_long_DateResolver_negativeAcrossYear() {
        LocalDate t = TEST_2007_07_15.minusMonths(-7, DateResolvers.nextValid());
        assertEquals(t, LocalDate.of(2008, 2, 15));
    }

    public void test_minusMonths_long_DateResolver_negativeOverYears() {
        LocalDate t = TEST_2007_07_15.minusMonths(-31, DateResolvers.nextValid());
        assertEquals(t, LocalDate.of(2010, 2, 15));
    }

    public void test_minusMonths_long_DateResolver_adjustDayFromLeapYear() {
        LocalDate t = LocalDate.of(2008, 2, 29).minusMonths(12, DateResolvers.nextValid());
        LocalDate expected = LocalDate.of(2007, 3, 1);
        assertEquals(t, expected);
    }

    public void test_minusMonths_long_DateResolver_adjustDayFromMonthLength() {
        LocalDate t = LocalDate.of(2007, 3, 31).minusMonths(1, DateResolvers.nextValid());
        LocalDate expected = LocalDate.of(2007, 3, 1);
        assertEquals(t, expected);
    }

    public void test_minusMonths_long_DateResolver_big() {
        long months = 20L + Integer.MAX_VALUE;
        LocalDate test = LocalDate.of(40, 6, 1).minusMonths(months, DateResolvers.nextValid());
        assertEquals(test, LocalDate.of((int) (40L - months / 12), 6 - (int) (months % 12), 1));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_minusMonths_long_DateResolver_null_adjustDay() {
        TEST_2007_07_15.minusMonths(1, new MockDateResolverReturnsNull());
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_minusMonths_long_DateResolver_invalidTooLarge() {
        LocalDate.of(Year.MAX_YEAR, 12, 1).minusMonths(-1, DateResolvers.nextValid());
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusMonths_long_DateResolver_invalidTooLargeMaxAddMax() {
        LocalDate test = LocalDate.of(Year.MAX_YEAR, 12, 1);
        test.minusMonths(Long.MAX_VALUE, DateResolvers.nextValid());
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusMonths_long_DateResolver_invalidTooLargeMaxAddMin() {
        LocalDate test = LocalDate.of(Year.MAX_YEAR, 12, 1);
        test.minusMonths(Long.MIN_VALUE, DateResolvers.nextValid());
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_minusMonths_long_DateResolver_invalidTooSmall() {
        LocalDate.of(Year.MIN_YEAR, 1, 1).minusMonths(1, DateResolvers.nextValid());
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
    
    @Test(dataProvider="sampleMinusWeeksSymmetry")
    public void test_minusWeeks_symmetry(LocalDate reference) {
        for (int weeks = 0; weeks < 365 * 8; weeks++) {
            LocalDate t = reference.minusWeeks(weeks).minusWeeks(-weeks);
            assertEquals(t, reference);

            t = reference.minusWeeks(-weeks).minusWeeks(weeks);
            assertEquals(t, reference);
        }
    }

    public void test_minusWeeks_normal() {
        LocalDate t = TEST_2007_07_15.minusWeeks(1);
        assertEquals(t, LocalDate.of(2007, 7, 8));
    }

    public void test_minusWeeks_noChange() {
        LocalDate t = TEST_2007_07_15.minusWeeks(0);
        assertSame(t, TEST_2007_07_15);
    }

    public void test_minusWeeks_overMonths() {
        LocalDate t = TEST_2007_07_15.minusWeeks(9);
        assertEquals(t, LocalDate.of(2007, 5, 13));
    }

    public void test_minusWeeks_overYears() {
        LocalDate t = LocalDate.of(2008, 7, 13).minusWeeks(52);
        assertEquals(t, TEST_2007_07_15);
    }

    public void test_minusWeeks_overLeapYears() {
        LocalDate t = TEST_2007_07_15.minusYears(-1).minusWeeks(104);
        assertEquals(t, LocalDate.of(2006, 7, 18));
    }

    public void test_minusWeeks_negative() {
        LocalDate t = TEST_2007_07_15.minusWeeks(-1);
        assertEquals(t, LocalDate.of(2007, 7, 22));
    }

    public void test_minusWeeks_negativeAcrossYear() {
        LocalDate t = TEST_2007_07_15.minusWeeks(-28);
        assertEquals(t, LocalDate.of(2008, 1, 27));
    }

    public void test_minusWeeks_negativeOverYears() {
        LocalDate t = TEST_2007_07_15.minusWeeks(-104);
        assertEquals(t, LocalDate.of(2009, 7, 12));
    }

    public void test_minusWeeks_maximum() {
        LocalDate t = LocalDate.of(Year.MAX_YEAR, 12, 24).minusWeeks(-1);
        LocalDate expected = LocalDate.of(Year.MAX_YEAR, 12, 31);
        assertEquals(t, expected);
    }

    public void test_minusWeeks_minimum() {
        LocalDate t = LocalDate.of(Year.MIN_YEAR, 1, 8).minusWeeks(1);
        LocalDate expected = LocalDate.of(Year.MIN_YEAR, 1, 1);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_minusWeeks_invalidTooLarge() {
        LocalDate.of(Year.MAX_YEAR, 12, 25).minusWeeks(-1);
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_minusWeeks_invalidTooSmall() {
        LocalDate.of(Year.MIN_YEAR, 1, 7).minusWeeks(1);
    }

    @Test(expectedExceptions={ArithmeticException.class})
    public void test_minusWeeks_invalidMaxMinusMax() {
        LocalDate.of(Year.MAX_YEAR, 12, 25).minusWeeks(Long.MAX_VALUE);
    }

    @Test(expectedExceptions={ArithmeticException.class})
    public void test_minusWeeks_invalidMaxMinusMin() {
        LocalDate.of(Year.MAX_YEAR, 12, 25).minusWeeks(Long.MIN_VALUE);
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
    
    @Test(dataProvider="sampleMinusDaysSymmetry")
    public void test_minusDays_symmetry(LocalDate reference) {
        for (int days = 0; days < 365 * 8; days++) {
            LocalDate t = reference.minusDays(days).minusDays(-days);
            assertEquals(t, reference);

            t = reference.minusDays(-days).minusDays(days);
            assertEquals(t, reference);
        }
    }

    public void test_minusDays_normal() {
        LocalDate t = TEST_2007_07_15.minusDays(1);
        assertEquals(t, LocalDate.of(2007, 7, 14));
    }

    public void test_minusDays_noChange() {
        LocalDate t = TEST_2007_07_15.minusDays(0);
        assertSame(t, TEST_2007_07_15);
    }

    public void test_minusDays_overMonths() {
        LocalDate t = TEST_2007_07_15.minusDays(62);
        assertEquals(t, LocalDate.of(2007, 5, 14));
    }

    public void test_minusDays_overYears() {
        LocalDate t = LocalDate.of(2008, 7, 16).minusDays(367);
        assertEquals(t, TEST_2007_07_15);
    }

    public void test_minusDays_overLeapYears() {
        LocalDate t = TEST_2007_07_15.plusYears(2).minusDays(365 + 366);
        assertEquals(t, TEST_2007_07_15);
    }

    public void test_minusDays_negative() {
        LocalDate t = TEST_2007_07_15.minusDays(-1);
        assertEquals(t, LocalDate.of(2007, 7, 16));
    }

    public void test_minusDays_negativeAcrossYear() {
        LocalDate t = TEST_2007_07_15.minusDays(-169);
        assertEquals(t, LocalDate.of(2007, 12, 31));
    }

    public void test_minusDays_negativeOverYears() {
        LocalDate t = TEST_2007_07_15.minusDays(-731);
        assertEquals(t, LocalDate.of(2009, 7, 15));
    }

    public void test_minusDays_maximum() {
        LocalDate t = LocalDate.of(Year.MAX_YEAR, 12, 30).minusDays(-1);
        LocalDate expected = LocalDate.of(Year.MAX_YEAR, 12, 31);
        assertEquals(t, expected);
    }

    public void test_minusDays_minimum() {
        LocalDate t = LocalDate.of(Year.MIN_YEAR, 1, 2).minusDays(1);
        LocalDate expected = LocalDate.of(Year.MIN_YEAR, 1, 1);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_minusDays_invalidTooLarge() {
        LocalDate.of(Year.MAX_YEAR, 12, 31).minusDays(-1);
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_minusDays_invalidTooSmall() {
        LocalDate.of(Year.MIN_YEAR, 1, 1).minusDays(1);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusDays_overflowTooLarge() {
        LocalDate.of(Year.MAX_YEAR, 12, 31).minusDays(Long.MIN_VALUE);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusDays_overflowTooSmall() {
        LocalDate.of(Year.MIN_YEAR, 1, 1).minusDays(Long.MAX_VALUE);
    }

    //-----------------------------------------------------------------------
    // matches()
    //-----------------------------------------------------------------------
    public void test_matches() {
        assertTrue(TEST_2007_07_15.matches(new CalendricalMatcher() {
            public boolean matchesCalendrical(Calendrical calendrical) {
                return true;
            }
        }));
        assertFalse(TEST_2007_07_15.matches(new CalendricalMatcher() {
            public boolean matchesCalendrical(Calendrical calendrical) {
                return false;
            }
        }));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_matches_null() {
        TEST_2007_07_15.matches(null);
    }

    //-----------------------------------------------------------------------
    // atTime()
    //-----------------------------------------------------------------------
    public void test_atTime_OffsetTime() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        assertEquals(t.atTime(OffsetTime.of(11, 30, OFFSET_PONE)), OffsetDateTime.of(2008, 6, 30, 11, 30, OFFSET_PONE));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_atTime_OffsetTime_null() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime((OffsetTime) null);
    }

    public void test_atTime_LocalTime() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        assertEquals(t.atTime(LocalTime.of(11, 30)), LocalDateTime.of(2008, 6, 30, 11, 30));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_atTime_LocalTime_null() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime((LocalTime) null);
    }

    //-------------------------------------------------------------------------
    public void test_atTime_int_int() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        assertEquals(t.atTime(11, 30), LocalDateTime.of(2008, 6, 30, 11, 30));
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_hourTooSmall() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(-1, 30);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_hourTooBig() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(24, 30);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_minuteTooSmall() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(11, -1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_minuteTooBig() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(11, 60);
    }

    public void test_atTime_int_int_int() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        assertEquals(t.atTime(11, 30, 40), LocalDateTime.of(2008, 6, 30, 11, 30, 40));
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_int_hourTooSmall() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(-1, 30, 40);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_int_hourTooBig() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(24, 30, 40);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_int_minuteTooSmall() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(11, -1, 40);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_int_minuteTooBig() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(11, 60, 40);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_int_secondTooSmall() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(11, 30, -1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_int_secondTooBig() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(11, 30, 60);
    }

    public void test_atTime_int_int_int_int() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        assertEquals(t.atTime(11, 30, 40, 50), LocalDateTime.of(2008, 6, 30, 11, 30, 40, 50));
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_int_int_hourTooSmall() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(-1, 30, 40, 50);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_int_int_hourTooBig() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(24, 30, 40, 50);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_int_int_minuteTooSmall() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(11, -1, 40, 50);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_int_int_minuteTooBig() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(11, 60, 40, 50);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_int_int_secondTooSmall() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(11, 30, -1, 50);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_int_int_secondTooBig() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(11, 30, 60, 50);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_int_int_nanoTooSmall() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(11, 30, 40, -1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atTime_int_int_int_int_nanoTooBig() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(11, 30, 40, 1000000000);
    }

    //-----------------------------------------------------------------------
    // atMidnight()
    //-----------------------------------------------------------------------
    public void test_atMidnight() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        assertEquals(t.atMidnight(), LocalDateTime.of(2008, 6, 30, 0, 0));
    }

    //-----------------------------------------------------------------------
    // atOffset()
    //-----------------------------------------------------------------------
    public void test_atOffset() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        assertEquals(t.atOffset(OFFSET_PTWO), OffsetDate.of(2008, 6, 30, OFFSET_PTWO));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_atOffset_nullZoneOffset() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atOffset((ZoneOffset) null);
    }

    //-----------------------------------------------------------------------
    // atStartOfDayInZone()
    //-----------------------------------------------------------------------
    public void test_atStartOfDayInZone() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        assertEquals(t.atStartOfDayInZone(ZONE_PARIS),
                ZonedDateTime.of(LocalDateTime.of(2008, 6, 30, 0, 0), ZONE_PARIS));
    }

    public void test_atStartOfDayInZone_dstGap() {
        LocalDate t = LocalDate.of(2007, 4, 1);
        assertEquals(t.atStartOfDayInZone(ZONE_GAZA),
                ZonedDateTime.of(LocalDateTime.of(2007, 4, 1, 1, 0), ZONE_GAZA));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_atStartOfDayInZone_nullTimeZone() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atStartOfDayInZone((ZoneId) null);
    }

    //-----------------------------------------------------------------------
    // toLocalDate()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_toLocalDate(int year, int month, int day) {
        LocalDate t = LocalDate.of(year, month, day);
        assertSame(t.toLocalDate(), t);
    }

    //-----------------------------------------------------------------------
    // toEpochDay()
    //-----------------------------------------------------------------------
    public void test_toEpochDay() {
        long date_0000_01_01 = -678941 - 40587;
        
        LocalDate test = LocalDate.of(0, 1, 1);
        for (long i = date_0000_01_01; i < 700000; i++) {
            assertEquals(test.toEpochDay(), i);
            test = next(test);
        }
        test = LocalDate.of(0, 1, 1);
        for (long i = date_0000_01_01; i > -2000000; i--) {
            assertEquals(test.toEpochDay(), i);
            test = previous(test);
        }
        
        assertEquals(LocalDate.of(1858, 11, 17).toEpochDay(), -40587);
        assertEquals(LocalDate.of(1, 1, 1).toEpochDay(), -678575 - 40587);
        assertEquals(LocalDate.of(1995, 9, 27).toEpochDay(), 49987 - 40587);
        assertEquals(LocalDate.of(1970, 1, 1).toEpochDay(), 0);
        assertEquals(LocalDate.of(-1, 12, 31).toEpochDay(), -678942 - 40587);
    }

    public void test_toEpochDay_fromMJDays_symmetry() {
        long date_0000_01_01 = -678941 - 40587;
        
        LocalDate test = LocalDate.of(0, 1, 1);
        for (long i = date_0000_01_01; i < 700000; i++) {
            assertEquals(LocalDate.ofEpochDay(test.toEpochDay()), test);
            test = next(test);
        }
        test = LocalDate.of(0, 1, 1);
        for (long i = date_0000_01_01; i > -2000000; i--) {
            assertEquals(LocalDate.ofEpochDay(test.toEpochDay()), test);
            test = previous(test);
        }
    }

    //-----------------------------------------------------------------------
    // toModifiedJulianDay()
    //-----------------------------------------------------------------------
    public void test_toModifiedJulianDay() {
        LocalDate test = LocalDate.of(0, 1, 1);
        for (int i = -678941; i < 700000; i++) {
            assertEquals(test.toModifiedJulianDay(), i);
            test = next(test);
        }
        
        test = LocalDate.of(0, 1, 1);
        for (int i = -678941; i > -2000000; i--) {
            assertEquals(test.toModifiedJulianDay(), i);
            test = previous(test);
        }
        
        assertEquals(LocalDate.of(1858, 11, 17).toModifiedJulianDay(), 0);
        assertEquals(LocalDate.of(1, 1, 1).toModifiedJulianDay(), -678575);
        assertEquals(LocalDate.of(1995, 9, 27).toModifiedJulianDay(), 49987);
        assertEquals(LocalDate.of(1970, 1, 1).toModifiedJulianDay(), 40587);
        assertEquals(LocalDate.of(-1, 12, 31).toModifiedJulianDay(), -678942);
    }

    public void test_toModifiedJulianDay_fromMJDays_symmetry() {
        LocalDate test = LocalDate.of(0, 1, 1);
        for (int i = -678941; i < 700000; i++) {
            assertEquals(LocalDate.ofModifiedJulianDay(test.toModifiedJulianDay()), test);
            test = next(test);
        }

        test = LocalDate.of(0, 1, 1);
        for (int i = -678941; i > -2000000; i--) {
            assertEquals(LocalDate.ofModifiedJulianDay(test.toModifiedJulianDay()), test);
            test = previous(test);
        }
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_comparisons() {
        doTest_comparisons_LocalDate(
            LocalDate.of(Year.MIN_YEAR, 1, 1),
            LocalDate.of(Year.MIN_YEAR, 12, 31),
            LocalDate.of(-1, 1, 1),
            LocalDate.of(-1, 12, 31),
            LocalDate.of(0, 1, 1),
            LocalDate.of(0, 12, 31),
            LocalDate.of(1, 1, 1),
            LocalDate.of(1, 12, 31),
            LocalDate.of(2006, 1, 1),
            LocalDate.of(2006, 12, 31),
            LocalDate.of(2007, 1, 1),
            LocalDate.of(2007, 12, 31),
            LocalDate.of(2008, 1, 1),
            LocalDate.of(2008, 2, 29),
            LocalDate.of(2008, 12, 31),
            LocalDate.of(Year.MAX_YEAR, 1, 1),
            LocalDate.of(Year.MAX_YEAR, 12, 31)
        );
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

    @Test(expectedExceptions=NullPointerException.class)
    public void test_compareTo_ObjectNull() {
        TEST_2007_07_15.compareTo(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isBefore_ObjectNull() {
        TEST_2007_07_15.isBefore(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isAfter_ObjectNull() {
        TEST_2007_07_15.isAfter(null);
    }

    @Test(expectedExceptions=ClassCastException.class)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void compareToNonLocalDate() {
       Comparable c = TEST_2007_07_15;
       c.compareTo(new Object());
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_equals_true(int y, int m, int d) {
        LocalDate a = LocalDate.of(y, m, d);
        LocalDate b = LocalDate.of(y, m, d);
        assertEquals(a.equals(b), true);
    }
    @Test(dataProvider="sampleDates")
    public void test_equals_false_year_differs(int y, int m, int d) {
        LocalDate a = LocalDate.of(y, m, d);
        LocalDate b = LocalDate.of(y + 1, m, d);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleDates")
    public void test_equals_false_month_differs(int y, int m, int d) {
        LocalDate a = LocalDate.of(y, m, d);
        LocalDate b = LocalDate.of(y, m + 1, d);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleDates")
    public void test_equals_false_day_differs(int y, int m, int d) {
        LocalDate a = LocalDate.of(y, m, d);
        LocalDate b = LocalDate.of(y, m, d + 1);
        assertEquals(a.equals(b), false);
    }

    public void test_equals_itself_true() {
        assertEquals(TEST_2007_07_15.equals(TEST_2007_07_15), true);
    }

    public void test_equals_string_false() {
        assertEquals(TEST_2007_07_15.equals("2007-07-15"), false);
    }

    public void test_equals_null_false() {
        assertEquals(TEST_2007_07_15.equals(null), false);
    }

    //-----------------------------------------------------------------------
    // hashCode()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_hashCode(int y, int m, int d) {
        LocalDate a = LocalDate.of(y, m, d);
        assertEquals(a.hashCode(), a.hashCode());
        LocalDate b = LocalDate.of(y, m, d);
        assertEquals(a.hashCode(), b.hashCode());
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleToString")
    Object[][] provider_sampleToString() {
        return new Object[][] {
            {2008, 7, 5, "2008-07-05"},
            {2007, 12, 31, "2007-12-31"},
            {999, 12, 31, "0999-12-31"},
            {-1, 1, 2, "-0001-01-02"},
            {9999, 12, 31, "9999-12-31"},
            {-9999, 12, 31, "-9999-12-31"},
            {10000, 1, 1, "+10000-01-01"},
            {-10000, 1, 1, "-10000-01-01"},
            {12345678, 1, 1, "+12345678-01-01"},
            {-12345678, 1, 1, "-12345678-01-01"},
        };
    }

    @Test(dataProvider="sampleToString")
    public void test_toString(int y, int m, int d, String expected) {
        LocalDate t = LocalDate.of(y, m, d);
        String str = t.toString();
        assertEquals(str, expected);
    }

    //-----------------------------------------------------------------------
    // toString(DateTimeFormatter)
    //-----------------------------------------------------------------------
    public void test_toString_formatter() {
        String t = LocalDate.of(2010, 12, 3).toString(DateTimeFormatters.basicIsoDate());
        assertEquals(t, "20101203");
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_toString_formatter_null() {
        LocalDate.of(2010, 12, 3).toString(null);
    }

    //-----------------------------------------------------------------------
    // matchesCalendrical() - parameter is larger calendrical
    //-----------------------------------------------------------------------
    public void test_matchesCalendrical_true_date() {
        LocalDate test = TEST_2007_07_15;
        OffsetDate cal = TEST_2007_07_15.atOffset(ZoneOffset.UTC);
        assertEquals(test.matchesCalendrical(cal), true);
    }

    public void test_matchesCalendrical_false_date() {
        LocalDate test = TEST_2007_07_15;
        OffsetDate cal = TEST_2007_07_15.plusYears(1).atOffset(ZoneOffset.UTC);
        assertEquals(test.matchesCalendrical(cal), false);
    }

    public void test_matchesCalendrical_itself_true() {
        assertEquals(TEST_2007_07_15.matchesCalendrical(TEST_2007_07_15), true);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_matchesCalendrical_null() {
        TEST_2007_07_15.matchesCalendrical(null);
    }

    //-----------------------------------------------------------------------
    // adjustDate()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_adjustDate(int y, int m, int d) {
        LocalDate a = LocalDate.of(y, m, d);
        assertSame(a.adjustDate(TEST_2007_07_15), a);
        assertSame(TEST_2007_07_15.adjustDate(a), TEST_2007_07_15);
    }

    public void test_adjustDate_same() {
        assertSame(LocalDate.of(2007, 7, 15).adjustDate(TEST_2007_07_15), TEST_2007_07_15);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustDate_null() {
        TEST_2007_07_15.adjustDate(null);
    }
}
