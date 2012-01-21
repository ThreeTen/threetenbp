/*
 * Copyright (c) 2008-2011, Stephen Colebourne & Michael Nascimento Santos
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
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import javax.time.AmPmOfDay;
import javax.time.CalendricalException;
import javax.time.Chronology;
import javax.time.Clock;
import javax.time.Duration;
import javax.time.Instant;
import javax.time.LocalDate;
import javax.time.LocalDateTime;
import javax.time.LocalTime;
import javax.time.MonthDay;
import javax.time.OffsetDate;
import javax.time.OffsetDateTime;
import javax.time.OffsetTime;
import javax.time.Period;
import javax.time.TimeSource;
import javax.time.YearMonth;
import javax.time.ZoneId;
import javax.time.ZoneOffset;
import javax.time.ZonedDateTime;
import javax.time.calendar.format.CalendricalParseException;
import javax.time.calendar.format.DateTimeFormatters;
import javax.time.calendrical.Calendrical;
import javax.time.calendrical.CalendricalMatcher;
import javax.time.calendrical.CalendricalRule;
import javax.time.calendrical.ISOChronology;
import javax.time.calendrical.MockMultiProvider;
import javax.time.calendrical.MockPeriodProviderReturnsNull;
import javax.time.calendrical.MockRuleNoValue;
import javax.time.calendrical.MockTimeAdjusterReturnsNull;
import javax.time.calendrical.PeriodProvider;
import javax.time.calendrical.TimeAdjuster;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test OffsetTime.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestOffsetTime {

    private static final ZoneOffset OFFSET_PONE = ZoneOffset.ofHours(1);
    private static final ZoneOffset OFFSET_PTWO = ZoneOffset.ofHours(2);
    private static final LocalDate DATE = LocalDate.of(2008, 12, 3);
    private OffsetTime TEST_11_30_59_500_PONE;

    @BeforeMethod
    public void setUp() {
        TEST_11_30_59_500_PONE = OffsetTime.of(11, 30, 59, 500, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        Object obj = TEST_11_30_59_500_PONE;
        assertTrue(obj instanceof Calendrical);
        assertTrue(obj instanceof Serializable);
        assertTrue(obj instanceof Comparable<?>);
        assertTrue(obj instanceof CalendricalMatcher);
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(TEST_11_30_59_500_PONE);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), TEST_11_30_59_500_PONE);
    }

    public void test_immutable() {
        Class<OffsetTime> cls = OffsetTime.class;
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
    @Test
    public void now() {
        OffsetDateTime nowDT = OffsetDateTime.now();
        
        OffsetTime expected = OffsetTime.now(Clock.systemDefaultZone());
        OffsetTime test = OffsetTime.now();
        long diff = Math.abs(test.toLocalTime().toNanoOfDay() - expected.toLocalTime().toNanoOfDay());
        assertTrue(diff < 100000000);  // less than 0.1 secs
        assertEquals(test.getOffset(), nowDT.getOffset());
    }

    //-----------------------------------------------------------------------
    // now(Clock)
    //-----------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class)
    public void now_Clock_nullClock() {
        OffsetTime.now(null);
    }

    public void now_Clock_allSecsInDay() {
        for (int i = 0; i < (2 * 24 * 60 * 60); i++) {
            Instant instant = Instant.ofEpochSecond(i, 8);
            Clock clock = Clock.clock(TimeSource.fixed(instant), ZoneId.UTC);
            OffsetTime test = OffsetTime.now(clock);
            assertEquals(test.getHourOfDay(), (i / (60 * 60)) % 24);
            assertEquals(test.getMinuteOfHour(), (i / 60) % 60);
            assertEquals(test.getSecondOfMinute(), i % 60);
            assertEquals(test.getNanoOfSecond(), 8);
            assertEquals(test.getOffset(), ZoneOffset.UTC);
        }
    }

    public void now_Clock_beforeEpoch() {
        for (int i =-1; i >= -(24 * 60 * 60); i--) {
            Instant instant = Instant.ofEpochSecond(i, 8);
            Clock clock = Clock.clock(TimeSource.fixed(instant), ZoneId.UTC);
            OffsetTime test = OffsetTime.now(clock);
            assertEquals(test.getHourOfDay(), ((i + 24 * 60 * 60) / (60 * 60)) % 24);
            assertEquals(test.getMinuteOfHour(), ((i + 24 * 60 * 60) / 60) % 60);
            assertEquals(test.getSecondOfMinute(), (i + 24 * 60 * 60) % 60);
            assertEquals(test.getNanoOfSecond(), 8);
            assertEquals(test.getOffset(), ZoneOffset.UTC);
        }
    }

    public void now_Clock_offsets() {
        OffsetDateTime base = OffsetDateTime.of(1970, 1, 1, 12, 0, ZoneOffset.UTC);
        for (int i = -9; i < 15; i++) {
            ZoneOffset offset = ZoneOffset.ofHours(i);
            Clock clock = Clock.clock(TimeSource.fixed(base.toInstant()), ZoneId.of(offset));
            OffsetTime test = OffsetTime.now(clock);
            assertEquals(test.getHourOfDay(), (12 + i) % 24);
            assertEquals(test.getMinuteOfHour(), 0);
            assertEquals(test.getSecondOfMinute(), 0);
            assertEquals(test.getNanoOfSecond(), 0);
            assertEquals(test.getOffset(), offset);
        }
    }

    //-----------------------------------------------------------------------
    // factories
    //-----------------------------------------------------------------------
    void check(OffsetTime test, int h, int m, int s, int n, ZoneOffset offset) {
        assertEquals(test.getHourOfDay(), h);
        assertEquals(test.getMinuteOfHour(), m);
        assertEquals(test.getSecondOfMinute(), s);
        assertEquals(test.getNanoOfSecond(), n);
        assertEquals(test.getOffset(), offset);
    }

    //-----------------------------------------------------------------------
    public void factory_intsHM() {
        OffsetTime test = OffsetTime.of(11, 30, OFFSET_PONE);
        check(test, 11, 30, 0, 0, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    public void factory_intsHMS() {
        OffsetTime test = OffsetTime.of(11, 30, 10, OFFSET_PONE);
        check(test, 11, 30, 10, 0, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    public void factory_intsHMSN() {
        OffsetTime test = OffsetTime.of(11, 30, 10, 500, OFFSET_PONE);
        check(test, 11, 30, 10, 500, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    public void factory_LocalTimeZoneOffset() {
        LocalTime localTime = LocalTime.of(11, 30, 10, 500);
        OffsetTime test = OffsetTime.of(localTime, OFFSET_PONE);
        check(test, 11, 30, 10, 500, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_LocalTimeZoneOffset_nullTime() {
        OffsetTime.of((LocalTime) null, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_LocalTimeZoneOffset_nullOffset() {
        LocalTime localTime = LocalTime.of(11, 30, 10, 500);
        OffsetTime.of(localTime, (ZoneOffset) null);
    }

    //-----------------------------------------------------------------------
    // ofInstant()
    //-----------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class)
    public void factoryUTC_ofInstant_InstantProvider_nullInstant() {
        OffsetTime.ofInstantUTC((Instant) null);
    }

    public void factoryUTC_ofInstant_InstantProvider() {
        Instant instant = Instant.ofEpochSecond(86400 + 5 * 3600 + 10 * 60 + 20);
        OffsetTime test = OffsetTime.ofInstantUTC(instant);
        check(test, 5, 10, 20, 0, ZoneOffset.UTC);
    }

    //-----------------------------------------------------------------------
    public void factory_ofInstant_multiProvider_checkAmbiguous() {
        MockMultiProvider mmp = new MockMultiProvider(2008, 6, 30, 11, 30, 10, 500);
        OffsetTime test = OffsetTime.ofInstant(mmp, ZoneOffset.UTC);
        check(test, 11, 30, 10, 500, ZoneOffset.UTC);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_ofInstant_InstantProvider_nullInstant() {
        OffsetTime.ofInstant((Instant) null, ZoneOffset.UTC);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_ofInstant_InstantProvider_nullOffset() {
        Instant instant = Instant.ofEpochSecond(0L);
        OffsetTime.ofInstant(instant, (ZoneOffset) null);
    }

    public void factory_ofInstant_InstantProvider_allSecsInDay() {
        for (int i = 0; i < (2 * 24 * 60 * 60); i++) {
            Instant instant = Instant.ofEpochSecond(i, 8);
            OffsetTime test = OffsetTime.ofInstant(instant, ZoneOffset.UTC);
            assertEquals(test.getHourOfDay(), (i / (60 * 60)) % 24);
            assertEquals(test.getMinuteOfHour(), (i / 60) % 60);
            assertEquals(test.getSecondOfMinute(), i % 60);
            assertEquals(test.getNanoOfSecond(), 8);
        }
    }

    public void factory_ofInstant_InstantProvider_beforeEpoch() {
        for (int i =-1; i >= -(24 * 60 * 60); i--) {
            Instant instant = Instant.ofEpochSecond(i, 8);
            OffsetTime test = OffsetTime.ofInstant(instant, ZoneOffset.UTC);
            assertEquals(test.getHourOfDay(), ((i + 24 * 60 * 60) / (60 * 60)) % 24);
            assertEquals(test.getMinuteOfHour(), ((i + 24 * 60 * 60) / 60) % 60);
            assertEquals(test.getSecondOfMinute(), (i + 24 * 60 * 60) % 60);
            assertEquals(test.getNanoOfSecond(), 8);
        }
    }

    //-----------------------------------------------------------------------
    public void factory_ofInstant_InstantProvider_maxYear() {
        OffsetTime test = OffsetTime.ofInstant(Instant.ofEpochSecond(Long.MAX_VALUE), ZoneOffset.UTC);
        int hour = (int) ((Long.MAX_VALUE / (60 * 60)) % 24);
        int min = (int) ((Long.MAX_VALUE / 60) % 60);
        int sec = (int) (Long.MAX_VALUE % 60);
        assertEquals(test.getHourOfDay(), hour);
        assertEquals(test.getMinuteOfHour(), min);
        assertEquals(test.getSecondOfMinute(), sec);
        assertEquals(test.getNanoOfSecond(), 0);
    }

    public void factory_ofInstant_InstantProvider_minYear() {
        long oneDay = 24 * 60 * 60;
        long addition = ((Long.MAX_VALUE / oneDay) + 2) * oneDay;
        
        OffsetTime test = OffsetTime.ofInstant(Instant.ofEpochSecond(Long.MIN_VALUE), ZoneOffset.UTC);
        long added = Long.MIN_VALUE + addition;
        int hour = (int) ((added / (60 * 60)) % 24);
        int min = (int) ((added / 60) % 60);
        int sec = (int) (added % 60);
        assertEquals(test.getHourOfDay(), hour);
        assertEquals(test.getMinuteOfHour(), min);
        assertEquals(test.getSecondOfMinute(), sec);
        assertEquals(test.getNanoOfSecond(), 0);
    }

    //-----------------------------------------------------------------------
    // from()
    //-----------------------------------------------------------------------
    public void test_factory_Calendricals() {
        assertEquals(OffsetTime.from(OFFSET_PONE, AmPmOfDay.PM, HOUR_OF_AMPM.field(5), MINUTE_OF_HOUR.field(30)), OffsetTime.of(17, 30, OFFSET_PONE));
        assertEquals(OffsetTime.from(LocalTime.of(17, 30), OFFSET_PONE), OffsetTime.of(17, 30, OFFSET_PONE));
        assertEquals(OffsetTime.from(OffsetDateTime.of(2007, 7, 15, 17, 30, OFFSET_PONE)), OffsetTime.of(17, 30, OFFSET_PONE));
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_factory_Calendricals_invalid_clash() {
        OffsetTime.from(AmPmOfDay.PM, HOUR_OF_AMPM.field(5), HOUR_OF_DAY.field(20));
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_factory_Calendricals_invalid_noDerive() {
        OffsetTime.from(LocalDate.of(2007, 7, 15));
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_factory_Calendricals_invalid_empty() {
        OffsetTime.from();
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_Calendricals_nullArray() {
        OffsetTime.from((Calendrical[]) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_Calendricals_null() {
        OffsetTime.from((Calendrical) null);
    }

    //-----------------------------------------------------------------------
    // parse()
    //-----------------------------------------------------------------------
    @Test(dataProvider = "sampleToString")
    public void factory_parse_validText(int h, int m, int s, int n, String offsetId, String parsable) {
        OffsetTime t = OffsetTime.parse(parsable);
        assertNotNull(t, parsable);
        assertEquals(t.getHourOfDay(), h);
        assertEquals(t.getMinuteOfHour(), m);
        assertEquals(t.getSecondOfMinute(), s);
        assertEquals(t.getNanoOfSecond(), n);
        assertEquals(t.getOffset(), ZoneOffset.of(offsetId));
    }

    @DataProvider(name="sampleBadParse")
    Object[][] provider_sampleBadParse() {
        return new Object[][]{
                {"00;00"},
                {"12-00"},
                {"-01:00"},
                {"00:00:00-09"},
                {"00:00:00,09"},
                {"00:00:abs"},
                {"11"},
                {"11:30"},
                {"11:30+01:00[Europe/Paris]"},
        };
    }

    @Test(dataProvider = "sampleBadParse", expectedExceptions={CalendricalParseException.class})
    public void factory_parse_invalidText(String unparsable) {
        OffsetTime.parse(unparsable);
    }

    //-----------------------------------------------------------------------s
    @Test(expectedExceptions={CalendricalParseException.class})
    public void factory_parse_illegalHour() {
        OffsetTime.parse("25:00+01:00");
    }

    @Test(expectedExceptions={CalendricalParseException.class})
    public void factory_parse_illegalMinute() {
        OffsetTime.parse("12:60+01:00");
    }

    @Test(expectedExceptions={CalendricalParseException.class})
    public void factory_parse_illegalSecond() {
        OffsetTime.parse("12:12:60+01:00");
    }

    //-----------------------------------------------------------------------
    // parse(DateTimeFormatter)
    //-----------------------------------------------------------------------
    public void factory_parse_formatter() {
        OffsetTime t = OffsetTime.parse("11 30+0100", DateTimeFormatters.pattern("HH mmXX"));
        assertEquals(t, OffsetTime.of(11, 30, ZoneOffset.ofHours(1)));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_parse_formatter_nullText() {
        OffsetTime.parse((String) null, DateTimeFormatters.pattern("HM mmXX"));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_parse_formatter_nullFormatter() {
        OffsetTime.parse("", null);
    }

    //-----------------------------------------------------------------------
    // constructor
    //-----------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class)
    public void constructor_nullTime() throws Throwable  {
        Constructor<OffsetTime> con = OffsetTime.class.getDeclaredConstructor(LocalTime.class, ZoneOffset.class);
        con.setAccessible(true);
        try {
            con.newInstance(null, OFFSET_PONE);
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void constructor_nullOffset() throws Throwable  {
        Constructor<OffsetTime> con = OffsetTime.class.getDeclaredConstructor(LocalTime.class, ZoneOffset.class);
        con.setAccessible(true);
        try {
            con.newInstance(LocalTime.of(11, 30), null);
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }

    //-----------------------------------------------------------------------
    // basics
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleTimes")
    Object[][] provider_sampleTimes() {
        return new Object[][] {
            {11, 30, 20, 500, OFFSET_PONE},
            {11, 0, 0, 0, OFFSET_PONE},
            {23, 59, 59, 999999999, OFFSET_PONE},
        };
    }

    @Test(dataProvider="sampleTimes")
    public void test_get(int h, int m, int s, int n, ZoneOffset offset) {
        LocalTime localTime = LocalTime.of(h, m, s, n);
        OffsetTime a = OffsetTime.of(localTime, offset);
        assertSame(a.getOffset(), offset);
        
        assertEquals(a.getHourOfDay(), localTime.getHourOfDay());
        assertEquals(a.getMinuteOfHour(), localTime.getMinuteOfHour());
        assertEquals(a.getSecondOfMinute(), localTime.getSecondOfMinute());
        assertEquals(a.getNanoOfSecond(), localTime.getNanoOfSecond());
        
        assertSame(a.toLocalTime(), localTime);
        assertEquals(a.toString(), localTime.toString() + offset.toString());
    }

    //-----------------------------------------------------------------------
    // get(CalendricalRule)
    //-----------------------------------------------------------------------
    public void test_get_CalendricalRule() {
        OffsetTime test = OffsetTime.of(12, 30, 40, 987654321, OFFSET_PONE);
        assertEquals(test.get(Chronology.rule()), ISOChronology.INSTANCE);
        assertEquals(test.get(YEAR), null);
        assertEquals(test.get(QUARTER_OF_YEAR), null);
        assertEquals(test.get(MONTH_OF_YEAR), null);
        assertEquals(test.get(MONTH_OF_QUARTER), null);
        assertEquals(test.get(DAY_OF_MONTH), null);
        assertEquals(test.get(DAY_OF_WEEK), null);
        assertEquals(test.get(DAY_OF_YEAR), null);
        assertEquals(test.get(WEEK_OF_WEEK_BASED_YEAR), null);
        assertEquals(test.get(WEEK_BASED_YEAR), null);
        
        assertEquals(test.get(HOUR_OF_DAY).getValue(), 12);
        assertEquals(test.get(MINUTE_OF_HOUR).getValue(), 30);
        assertEquals(test.get(SECOND_OF_MINUTE).getValue(), 40);
        assertEquals(test.get(NANO_OF_SECOND).getValue(), 987654321);
        assertEquals(test.get(HOUR_OF_AMPM).getValue(), 0);
        assertEquals(test.get(AMPM_OF_DAY).getValue(), AmPmOfDay.PM.getValue());
        
        assertEquals(test.get(LocalDate.rule()), null);
        assertEquals(test.get(LocalTime.rule()), test.toLocalTime());
        assertEquals(test.get(LocalDateTime.rule()), null);
        assertEquals(test.get(OffsetDate.rule()), null);
        assertEquals(test.get(OffsetTime.rule()), test);
        assertEquals(test.get(OffsetDateTime.rule()), null);
        assertEquals(test.get(ZonedDateTime.rule()), null);
        assertEquals(test.get(ZoneOffset.rule()), test.getOffset());
        assertEquals(test.get(ZoneId.rule()), null);
        assertEquals(test.get(YearMonth.rule()), null);
        assertEquals(test.get(MonthDay.rule()), null);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_get_CalendricalRule_null() {
        OffsetTime test = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        test.get((CalendricalRule<?>) null);
    }

    public void test_get_unsupported() {
        OffsetTime test = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        assertEquals(test.get(MockRuleNoValue.INSTANCE), null);
    }

    //-----------------------------------------------------------------------
    // withOffsetSameLocal()
    //-----------------------------------------------------------------------
    public void test_withOffsetSameLocal() {
        OffsetTime base = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.withOffsetSameLocal(OFFSET_PTWO);
        assertSame(test.toLocalTime(), base.toLocalTime());
        assertSame(test.getOffset(), OFFSET_PTWO);
    }

    public void test_withOffsetSameLocal_noChange() {
        OffsetTime base = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.withOffsetSameLocal(OFFSET_PONE);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_withOffsetSameLocal_null() {
        OffsetTime base = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        base.withOffsetSameLocal(null);
    }

    //-----------------------------------------------------------------------
    // withOffsetSameInstant()
    //-----------------------------------------------------------------------
    public void test_withOffsetSameInstant() {
        OffsetTime base = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.withOffsetSameInstant(OFFSET_PTWO);
        OffsetTime expected = OffsetTime.of(12, 30, 59, OFFSET_PTWO);
        assertEquals(test, expected);
    }

    public void test_withOffsetSameInstant_noChange() {
        OffsetTime base = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.withOffsetSameInstant(OFFSET_PONE);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_withOffsetSameInstant_null() {
        OffsetTime base = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        base.withOffsetSameInstant(null);
    }

    //-----------------------------------------------------------------------
    // with()
    //-----------------------------------------------------------------------
    public void test_with() {
        OffsetTime base = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.with(new TimeAdjuster() {
            public LocalTime adjustTime(LocalTime time) {
                return time.withHourOfDay(1);
            }
        });
        assertEquals(test.toLocalTime(), LocalTime.of(1, 30, 59));
        assertSame(test.getOffset(), base.getOffset());
    }

    public void test_with_noChange() {
        LocalTime time = LocalTime.of(11, 30, 59);
        OffsetTime base = OffsetTime.of(time, OFFSET_PONE);
        OffsetTime test = base.with(time);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_with_null() {
        OffsetTime base = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        base.with(null);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_with_badAdjuster() {
        OffsetTime base = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        base.with(new MockTimeAdjusterReturnsNull());
    }

    //-----------------------------------------------------------------------
    // withHourOfDay()
    //-----------------------------------------------------------------------
    public void test_withHourOfDay_normal() {
        OffsetTime base = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.withHourOfDay(15);
        assertEquals(test, OffsetTime.of(15, 30, 59, OFFSET_PONE));
    }

    public void test_withHourOfDay_noChange() {
        OffsetTime base = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.withHourOfDay(11);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withMinuteOfHour()
    //-----------------------------------------------------------------------
    public void test_withMinuteOfHour_normal() {
        OffsetTime base = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.withMinuteOfHour(15);
        assertEquals(test, OffsetTime.of(11, 15, 59, OFFSET_PONE));
    }

    public void test_withMinuteOfHour_noChange() {
        OffsetTime base = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.withMinuteOfHour(30);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withSecondOfMinute()
    //-----------------------------------------------------------------------
    public void test_withSecondOfMinute_normal() {
        OffsetTime base = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.withSecondOfMinute(15);
        assertEquals(test, OffsetTime.of(11, 30, 15, OFFSET_PONE));
    }

    public void test_withSecondOfMinute_noChange() {
        OffsetTime base = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.withSecondOfMinute(59);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withNanoOfSecond()
    //-----------------------------------------------------------------------
    public void test_withNanoOfSecond_normal() {
        OffsetTime base = OffsetTime.of(11, 30, 59, 1, OFFSET_PONE);
        OffsetTime test = base.withNanoOfSecond(15);
        assertEquals(test, OffsetTime.of(11, 30, 59, 15, OFFSET_PONE));
    }

    public void test_withNanoOfSecond_noChange() {
        OffsetTime base = OffsetTime.of(11, 30, 59, 1, OFFSET_PONE);
        OffsetTime test = base.withNanoOfSecond(1);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plus(PeriodProvider)
    //-----------------------------------------------------------------------
    public void test_plus_PeriodProvider() {
        PeriodProvider provider = Period.ofTimeFields(1, 2, 3);
        OffsetTime t = TEST_11_30_59_500_PONE.plus(provider);
        assertEquals(t, OffsetTime.of(12, 33, 2, 500, OFFSET_PONE));
    }

    public void test_plus_PeriodProvider_zero() {
        OffsetTime t = TEST_11_30_59_500_PONE.plus(Period.ZERO);
        assertSame(t, TEST_11_30_59_500_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_plus_PeriodProvider_null() {
        TEST_11_30_59_500_PONE.plus((PeriodProvider) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_plus_PeriodProvider_badProvider() {
        TEST_11_30_59_500_PONE.plus(new MockPeriodProviderReturnsNull());
    }

    //-----------------------------------------------------------------------
    // plus(Duration)
    //-----------------------------------------------------------------------
    public void test_plus_Duration() {
        Duration dur = Duration.ofSeconds(62, 3);
        OffsetTime t = TEST_11_30_59_500_PONE.plus(dur);
        assertEquals(t, OffsetTime.of(11, 32, 1, 503, OFFSET_PONE));
    }

    public void test_plus_Duration_big1() {
        Duration dur = Duration.ofSeconds(Long.MAX_VALUE, 999999999);
        OffsetTime t = TEST_11_30_59_500_PONE.plus(dur);
        assertEquals(t, TEST_11_30_59_500_PONE.plusSeconds(Long.MAX_VALUE).plusNanos(999999999));
    }

    public void test_plus_Duration_big2() {
        Duration dur = Duration.ofSeconds(999, Long.MAX_VALUE);
        OffsetTime t = TEST_11_30_59_500_PONE.plus(dur);
        assertEquals(t, TEST_11_30_59_500_PONE.plusSeconds(999).plusNanos(Long.MAX_VALUE));
    }

    public void test_plus_Duration_zero() {
        OffsetTime t = TEST_11_30_59_500_PONE.plus(Duration.ZERO);
        assertSame(t, TEST_11_30_59_500_PONE);
    }

    public void test_plus_Duration_wrap() {
        Duration dur = Duration.ofHours(1);
        OffsetTime t = OffsetTime.of(23, 30, OFFSET_PONE).plus(dur);
        assertEquals(t, OffsetTime.of(0, 30, OFFSET_PONE));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_plus_Duration_null() {
        TEST_11_30_59_500_PONE.plus((Duration) null);
    }

    //-----------------------------------------------------------------------
    // plusHours()
    //-----------------------------------------------------------------------
    public void test_plusHours() {
        OffsetTime base = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.plusHours(13);
        assertEquals(test, OffsetTime.of(0, 30, 59, OFFSET_PONE));
    }

    public void test_plusHours_zero() {
        OffsetTime base = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.plusHours(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusMinutes()
    //-----------------------------------------------------------------------
    public void test_plusMinutes() {
        OffsetTime base = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.plusMinutes(30);
        assertEquals(test, OffsetTime.of(12, 0, 59, OFFSET_PONE));
    }

    public void test_plusMinutes_zero() {
        OffsetTime base = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.plusMinutes(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusSeconds()
    //-----------------------------------------------------------------------
    public void test_plusSeconds() {
        OffsetTime base = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.plusSeconds(1);
        assertEquals(test, OffsetTime.of(11, 31, 0, OFFSET_PONE));
    }

    public void test_plusSeconds_zero() {
        OffsetTime base = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.plusSeconds(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusNanos()
    //-----------------------------------------------------------------------
    public void test_plusNanos() {
        OffsetTime base = OffsetTime.of(11, 30, 59, 0, OFFSET_PONE);
        OffsetTime test = base.plusNanos(1);
        assertEquals(test, OffsetTime.of(11, 30, 59, 1, OFFSET_PONE));
    }

    public void test_plusNanos_zero() {
        OffsetTime base = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.plusNanos(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minus(PeriodProvider)
    //-----------------------------------------------------------------------
    public void test_minus_PeriodProvider() {
        PeriodProvider provider = Period.ofTimeFields(1, 2, 3);
        OffsetTime t = TEST_11_30_59_500_PONE.minus(provider);
        assertEquals(t, OffsetTime.of(10, 28, 56, 500, OFFSET_PONE));
    }

    public void test_minus_PeriodProvider_zero() {
        OffsetTime t = TEST_11_30_59_500_PONE.minus(Period.ZERO);
        assertSame(t, TEST_11_30_59_500_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_minus_PeriodProvider_null() {
        TEST_11_30_59_500_PONE.minus((PeriodProvider) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_minus_PeriodProvider_badProvider() {
        TEST_11_30_59_500_PONE.minus(new MockPeriodProviderReturnsNull());
    }

    //-----------------------------------------------------------------------
    // minus(Duration)
    //-----------------------------------------------------------------------
    public void test_minus_Duration() {
        Duration dur = Duration.ofSeconds(62, 3);
        OffsetTime t = TEST_11_30_59_500_PONE.minus(dur);
        assertEquals(t, OffsetTime.of(11, 29, 57, 497, OFFSET_PONE));
    }

    public void test_minus_Duration_big1() {
        Duration dur = Duration.ofSeconds(Long.MAX_VALUE, 999999999);
        OffsetTime t = TEST_11_30_59_500_PONE.minus(dur);
        assertEquals(t, TEST_11_30_59_500_PONE.minusSeconds(Long.MAX_VALUE).minusNanos(999999999));
    }

    public void test_minus_Duration_big2() {
        Duration dur = Duration.ofSeconds(999, Long.MAX_VALUE);
        OffsetTime t = TEST_11_30_59_500_PONE.minus(dur);
        assertEquals(t, TEST_11_30_59_500_PONE.minusSeconds(999).minusNanos(Long.MAX_VALUE));
    }

    public void test_minus_Duration_zero() {
        OffsetTime t = TEST_11_30_59_500_PONE.minus(Duration.ZERO);
        assertSame(t, TEST_11_30_59_500_PONE);
    }

    public void test_minus_Duration_wrap() {
        Duration dur = Duration.ofHours(1);
        OffsetTime t = OffsetTime.of(0, 30, OFFSET_PONE).minus(dur);
        assertEquals(t, OffsetTime.of(23, 30, OFFSET_PONE));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_minus_Duration_null() {
        TEST_11_30_59_500_PONE.minus((Duration) null);
    }

    //-----------------------------------------------------------------------
    // minusHours()
    //-----------------------------------------------------------------------
    public void test_minusHours() {
        OffsetTime base = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.minusHours(-13);
        assertEquals(test, OffsetTime.of(0, 30, 59, OFFSET_PONE));
    }

    public void test_minusHours_zero() {
        OffsetTime base = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.minusHours(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusMinutes()
    //-----------------------------------------------------------------------
    public void test_minusMinutes() {
        OffsetTime base = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.minusMinutes(50);
        assertEquals(test, OffsetTime.of(10, 40, 59, OFFSET_PONE));
    }

    public void test_minusMinutes_zero() {
        OffsetTime base = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.minusMinutes(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusSeconds()
    //-----------------------------------------------------------------------
    public void test_minusSeconds() {
        OffsetTime base = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.minusSeconds(60);
        assertEquals(test, OffsetTime.of(11, 29, 59, OFFSET_PONE));
    }

    public void test_minusSeconds_zero() {
        OffsetTime base = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.minusSeconds(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusNanos()
    //-----------------------------------------------------------------------
    public void test_minusNanos() {
        OffsetTime base = OffsetTime.of(11, 30, 59, 0, OFFSET_PONE);
        OffsetTime test = base.minusNanos(1);
        assertEquals(test, OffsetTime.of(11, 30, 58, 999999999, OFFSET_PONE));
    }

    public void test_minusNanos_zero() {
        OffsetTime base = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.minusNanos(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // matches()
    //-----------------------------------------------------------------------
    public void test_matches() {
        assertTrue(TEST_11_30_59_500_PONE.matches(new CalendricalMatcher() {
            public boolean matchesCalendrical(Calendrical calendrical) {
                return true;
            }
        }));
        assertFalse(TEST_11_30_59_500_PONE.matches(new CalendricalMatcher() {
            public boolean matchesCalendrical(Calendrical calendrical) {
                return false;
            }
        }));
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_matches_null() {
        TEST_11_30_59_500_PONE.matches(null);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo_time() {
        OffsetTime a = OffsetTime.of(11, 29, OFFSET_PONE);
        OffsetTime b = OffsetTime.of(11, 30, OFFSET_PONE);  // a is before b due to time
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
        assertEquals(DATE.atTime(a).toInstant().compareTo(DATE.atTime(b).toInstant()) < 0, true);
    }

    public void test_compareTo_offset() {
        OffsetTime a = OffsetTime.of(11, 30, OFFSET_PTWO);
        OffsetTime b = OffsetTime.of(11, 30, OFFSET_PONE);  // a is before b due to offset
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
        assertEquals(DATE.atTime(a).toInstant().compareTo(DATE.atTime(b).toInstant()) < 0, true);
    }

    public void test_compareTo_both() {
        OffsetTime a = OffsetTime.of(11, 50, OFFSET_PTWO);
        OffsetTime b = OffsetTime.of(11, 20, OFFSET_PONE);  // a is before b on instant scale
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
        assertEquals(DATE.atTime(a).toInstant().compareTo(DATE.atTime(b).toInstant()) < 0, true);
    }

    public void test_compareTo_bothNearStartOfDay() {
        OffsetTime a = OffsetTime.of(0, 10, OFFSET_PONE);
        OffsetTime b = OffsetTime.of(2, 30, OFFSET_PTWO);  // a is before b on instant scale
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
        assertEquals(DATE.atTime(a).toInstant().compareTo(DATE.atTime(b).toInstant()) < 0, true);
    }

    public void test_compareTo_hourDifference() {
        OffsetTime a = OffsetTime.of(10, 0, OFFSET_PONE);
        OffsetTime b = OffsetTime.of(11, 0, OFFSET_PTWO);  // a is before b despite being same time-line time
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
        assertEquals(DATE.atTime(a).toInstant().compareTo(DATE.atTime(b).toInstant()) == 0, true);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_compareTo_null() {
        OffsetTime a = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        a.compareTo(null);
    }

    @Test(expectedExceptions=ClassCastException.class)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void compareToNonOffsetTime() {
       Comparable c = TEST_11_30_59_500_PONE;
       c.compareTo(new Object());
    }

    //-----------------------------------------------------------------------
    // isAfter() / isBefore() / equalInstant()
    //-----------------------------------------------------------------------
    public void test_isBeforeIsAfterIsEqual1() {
        OffsetTime a = OffsetTime.of(11, 30, 58, OFFSET_PONE);
        OffsetTime b = OffsetTime.of(11, 30, 59, OFFSET_PONE);  // a is before b due to time
        assertEquals(a.isBefore(b), true);
        assertEquals(a.equalInstant(b), false);
        assertEquals(a.isAfter(b), false);
        
        assertEquals(b.isBefore(a), false);
        assertEquals(b.equalInstant(a), false);
        assertEquals(b.isAfter(a), true);
        
        assertEquals(a.isBefore(a), false);
        assertEquals(b.isBefore(b), false);
        
        assertEquals(a.equalInstant(a), true);
        assertEquals(b.equalInstant(b), true);
        
        assertEquals(a.isAfter(a), false);
        assertEquals(b.isAfter(b), false);
    }

    public void test_isBeforeIsAfterIsEqual1nanos() {
        OffsetTime a = OffsetTime.of(11, 30, 59, 3, OFFSET_PONE);
        OffsetTime b = OffsetTime.of(11, 30, 59, 4, OFFSET_PONE);  // a is before b due to time
        assertEquals(a.isBefore(b), true);
        assertEquals(a.equalInstant(b), false);
        assertEquals(a.isAfter(b), false);
        
        assertEquals(b.isBefore(a), false);
        assertEquals(b.equalInstant(a), false);
        assertEquals(b.isAfter(a), true);
        
        assertEquals(a.isBefore(a), false);
        assertEquals(b.isBefore(b), false);
        
        assertEquals(a.equalInstant(a), true);
        assertEquals(b.equalInstant(b), true);
        
        assertEquals(a.isAfter(a), false);
        assertEquals(b.isAfter(b), false);
    }

    public void test_isBeforeIsAfterIsEqual2() {
        OffsetTime a = OffsetTime.of(11, 30, 59, OFFSET_PTWO);
        OffsetTime b = OffsetTime.of(11, 30, 58, OFFSET_PONE);  // a is before b due to offset
        assertEquals(a.isBefore(b), true);
        assertEquals(a.equalInstant(b), false);
        assertEquals(a.isAfter(b), false);
        
        assertEquals(b.isBefore(a), false);
        assertEquals(b.equalInstant(a), false);
        assertEquals(b.isAfter(a), true);
        
        assertEquals(a.isBefore(a), false);
        assertEquals(b.isBefore(b), false);
        
        assertEquals(a.equalInstant(a), true);
        assertEquals(b.equalInstant(b), true);
        
        assertEquals(a.isAfter(a), false);
        assertEquals(b.isAfter(b), false);
    }

    public void test_isBeforeIsAfterIsEqual2nanos() {
        OffsetTime a = OffsetTime.of(11, 30, 59, 4, OFFSET_PONE.plus(Period.ofSeconds(1)));
        OffsetTime b = OffsetTime.of(11, 30, 59, 3, OFFSET_PONE);  // a is before b due to offset
        assertEquals(a.isBefore(b), true);
        assertEquals(a.equalInstant(b), false);
        assertEquals(a.isAfter(b), false);
        
        assertEquals(b.isBefore(a), false);
        assertEquals(b.equalInstant(a), false);
        assertEquals(b.isAfter(a), true);
        
        assertEquals(a.isBefore(a), false);
        assertEquals(b.isBefore(b), false);
        
        assertEquals(a.equalInstant(a), true);
        assertEquals(b.equalInstant(b), true);
        
        assertEquals(a.isAfter(a), false);
        assertEquals(b.isAfter(b), false);
    }

    public void test_isBeforeIsAfterIsEqual_instantComparison() {
        OffsetTime a = OffsetTime.of(11, 30, 59, OFFSET_PTWO);
        OffsetTime b = OffsetTime.of(10, 30, 59, OFFSET_PONE);  // a is same instant as b
        assertEquals(a.isBefore(b), false);
        assertEquals(a.equalInstant(b), true);
        assertEquals(a.isAfter(b), false);
        
        assertEquals(b.isBefore(a), false);
        assertEquals(b.equalInstant(a), true);
        assertEquals(b.isAfter(a), false);
        
        assertEquals(a.isBefore(a), false);
        assertEquals(b.isBefore(b), false);
        
        assertEquals(a.equalInstant(a), true);
        assertEquals(b.equalInstant(b), true);
        
        assertEquals(a.isAfter(a), false);
        assertEquals(b.isAfter(b), false);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isBefore_null() {
        OffsetTime a = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        a.isBefore(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isAfter_null() {
        OffsetTime a = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        a.isAfter(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_equalInstant_null() {
        OffsetTime a = OffsetTime.of(11, 30, 59, OFFSET_PONE);
        a.equalInstant(null);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes")
    public void test_equals_true(int h, int m, int s, int n, ZoneOffset ignored) {
        OffsetTime a = OffsetTime.of(h, m, s, n, OFFSET_PONE);
        OffsetTime b = OffsetTime.of(h, m, s, n, OFFSET_PONE);
        assertEquals(a.equals(b), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_hour_differs(int h, int m, int s, int n, ZoneOffset ignored) {
        h = (h == 23 ? 22 : h);
        OffsetTime a = OffsetTime.of(h, m, s, n, OFFSET_PONE);
        OffsetTime b = OffsetTime.of(h + 1, m, s, n, OFFSET_PONE);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_minute_differs(int h, int m, int s, int n, ZoneOffset ignored) {
        m = (m == 59 ? 58 : m);
        OffsetTime a = OffsetTime.of(h, m, s, n, OFFSET_PONE);
        OffsetTime b = OffsetTime.of(h, m + 1, s, n, OFFSET_PONE);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_second_differs(int h, int m, int s, int n, ZoneOffset ignored) {
        s = (s == 59 ? 58 : s);
        OffsetTime a = OffsetTime.of(h, m, s, n, OFFSET_PONE);
        OffsetTime b = OffsetTime.of(h, m, s + 1, n, OFFSET_PONE);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_nano_differs(int h, int m, int s, int n, ZoneOffset ignored) {
        n = (n == 999999999 ? 999999998 : n);
        OffsetTime a = OffsetTime.of(h, m, s, n, OFFSET_PONE);
        OffsetTime b = OffsetTime.of(h, m, s, n + 1, OFFSET_PONE);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_offset_differs(int h, int m, int s, int n, ZoneOffset ignored) {
        OffsetTime a = OffsetTime.of(h, m, s, n, OFFSET_PONE);
        OffsetTime b = OffsetTime.of(h, m, s, n, OFFSET_PTWO);
        assertEquals(a.equals(b), false);
    }

    public void test_equals_itself_true() {
        assertEquals(TEST_11_30_59_500_PONE.equals(TEST_11_30_59_500_PONE), true);
    }

    public void test_equals_string_false() {
        assertEquals(TEST_11_30_59_500_PONE.equals("2007-07-15"), false);
    }

    public void test_equals_null_false() {
        assertEquals(TEST_11_30_59_500_PONE.equals(null), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleToString")
    Object[][] provider_sampleToString() {
        return new Object[][] {
            {11, 30, 59, 0, "Z", "11:30:59Z"},
            {11, 30, 59, 0, "+01:00", "11:30:59+01:00"},
            {11, 30, 59, 999000000, "Z", "11:30:59.999Z"},
            {11, 30, 59, 999000000, "+01:00", "11:30:59.999+01:00"},
            {11, 30, 59, 999000, "Z", "11:30:59.000999Z"},
            {11, 30, 59, 999000, "+01:00", "11:30:59.000999+01:00"},
            {11, 30, 59, 999, "Z", "11:30:59.000000999Z"},
            {11, 30, 59, 999, "+01:00", "11:30:59.000000999+01:00"},
        };
    }

    @Test(dataProvider="sampleToString")
    public void test_toString(int h, int m, int s, int n, String offsetId, String expected) {
        OffsetTime t = OffsetTime.of(h, m, s, n, ZoneOffset.of(offsetId));
        String str = t.toString();
        assertEquals(str, expected);
    }

    //-----------------------------------------------------------------------
    // toString(DateTimeFormatter)
    //-----------------------------------------------------------------------
    public void test_toString_formatter() {
        String t = OffsetTime.of(11, 30, OFFSET_PONE).toString(DateTimeFormatters.pattern("HH mm"));
        assertEquals(t, "11 30");
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_toString_formatter_null() {
        OffsetTime.of(11, 30, OFFSET_PONE).toString(null);
    }

    //-----------------------------------------------------------------------
    // matchesCalendrical() - parameter is larger calendrical
    //-----------------------------------------------------------------------
    public void test_matchesCalendrical_true_date() {
        OffsetTime test = TEST_11_30_59_500_PONE;
        OffsetDateTime cal = OffsetDateTime.of(2008, 6, 30, 12, 30, test.getOffset()).with(TEST_11_30_59_500_PONE.toLocalTime());
        assertEquals(test.matchesCalendrical(cal), true);
    }

    public void test_matchesCalendrical_false_date() {
        OffsetTime test = TEST_11_30_59_500_PONE;
        OffsetDateTime cal = OffsetDateTime.of(2008, 6, 30, 12, 30, test.getOffset()).with(TEST_11_30_59_500_PONE.toLocalTime().plusHours(1));
        assertEquals(test.matchesCalendrical(cal), false);
    }

    public void test_matchesCalendrical_itself_true() {
        assertEquals(TEST_11_30_59_500_PONE.matchesCalendrical(TEST_11_30_59_500_PONE), true);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_matchesCalendrical_null() {
        TEST_11_30_59_500_PONE.matchesCalendrical(null);
    }

}
