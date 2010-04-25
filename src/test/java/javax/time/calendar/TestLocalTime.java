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

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;

import javax.time.CalendricalException;
import javax.time.Instant;
import javax.time.TimeSource;
import javax.time.calendar.LocalTime.Overflow;
import javax.time.calendar.format.CalendricalParseException;
import javax.time.period.MockPeriodProviderReturnsNull;
import javax.time.period.Period;
import javax.time.period.PeriodProvider;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test LocalTime.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test(timeOut=5000)
public class TestLocalTime {

    private static final ZoneOffset OFFSET_PTWO = ZoneOffset.hours(2);

    private LocalTime TEST_12_30_40_987654321;

    @BeforeMethod
    public void setUp() {
        TEST_12_30_40_987654321 = LocalTime.of(12, 30, 40, 987654321);
    }

    //-----------------------------------------------------------------------
    private void check(LocalTime time, int h, int m, int s, int n) {
        assertEquals(time.getHourOfDay(), h);
        assertEquals(time.getMinuteOfHour(), m);
        assertEquals(time.getSecondOfMinute(), s);
        assertEquals(time.getNanoOfSecond(), n);
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        Object obj = TEST_12_30_40_987654321;
        assertTrue(obj instanceof Calendrical);
        assertTrue(obj instanceof Serializable);
        assertTrue(obj instanceof Comparable<?>);
        assertTrue(obj instanceof TimeAdjuster);
        assertTrue(obj instanceof CalendricalMatcher);
        assertTrue(obj instanceof TimeProvider);
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(TEST_12_30_40_987654321);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), TEST_12_30_40_987654321);
    }

    public void test_immutable() {
        Class<LocalTime> cls = LocalTime.class;
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
    public void constant_MIDNIGHT() {
        check(LocalTime.MIDNIGHT, 0, 0, 0, 0);
        assertSame(LocalTime.MIDNIGHT, LocalTime.MIDNIGHT);
        assertSame(LocalTime.MIDNIGHT, LocalTime.of(0, 0));
    }

    public void constant_MIDDAY() {
        check(LocalTime.MIDDAY, 12, 0, 0, 0);
        assertSame(LocalTime.MIDDAY, LocalTime.MIDDAY);
        assertSame(LocalTime.MIDDAY, LocalTime.of(12, 0));
    }

    //-----------------------------------------------------------------------
    // now(Clock)
    //-----------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class)
    public void now_Clock_nullClock() {
        LocalTime.now(null);
    }

    public void now_Clock_allSecsInDay() {
        for (int i = 0; i < (2 * 24 * 60 * 60); i++) {
            Instant instant = Instant.ofSeconds(i, 8);
            Clock clock = Clock.clock(TimeSource.fixed(instant), TimeZone.UTC);
            LocalTime test = LocalTime.now(clock);
            assertEquals(test.getHourOfDay(), (i / (60 * 60)) % 24);
            assertEquals(test.getMinuteOfHour(), (i / 60) % 60);
            assertEquals(test.getSecondOfMinute(), i % 60);
            assertEquals(test.getNanoOfSecond(), 8);
        }
    }

    public void now_Clock_beforeEpoch() {
        for (int i =-1; i >= -(24 * 60 * 60); i--) {
            Instant instant = Instant.ofSeconds(i, 8);
            Clock clock = Clock.clock(TimeSource.fixed(instant), TimeZone.UTC);
            LocalTime test = LocalTime.now(clock);
            assertEquals(test.getHourOfDay(), ((i + 24 * 60 * 60) / (60 * 60)) % 24);
            assertEquals(test.getMinuteOfHour(), ((i + 24 * 60 * 60) / 60) % 60);
            assertEquals(test.getSecondOfMinute(), (i + 24 * 60 * 60) % 60);
            assertEquals(test.getNanoOfSecond(), 8);
        }
    }

    //-----------------------------------------------------------------------
    public void now_Clock_maxYear() {
        Clock clock = Clock.clock(TimeSource.fixed(Instant.ofSeconds(Long.MAX_VALUE)), TimeZone.UTC);
        LocalTime test = LocalTime.now(clock);
        int hour = (int) ((Long.MAX_VALUE / (60 * 60)) % 24);
        int min = (int) ((Long.MAX_VALUE / 60) % 60);
        int sec = (int) (Long.MAX_VALUE % 60);
        assertEquals(test.getHourOfDay(), hour);
        assertEquals(test.getMinuteOfHour(), min);
        assertEquals(test.getSecondOfMinute(), sec);
        assertEquals(test.getNanoOfSecond(), 0);
    }

    public void now_Clock_minYear() {
        long oneDay = 24 * 60 * 60;
        long addition = ((Long.MAX_VALUE / oneDay) + 2) * oneDay;
        
        Clock clock = Clock.clock(TimeSource.fixed(Instant.ofSeconds(Long.MIN_VALUE)), TimeZone.UTC);
        LocalTime test = LocalTime.now(clock);
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
    // nowSystemClock()
    //-----------------------------------------------------------------------
    @Test(timeOut=30000)  // TODO: remove when time zone loading is faster
    public void nowSystemClock() {
        LocalTime expected = LocalTime.now(Clock.systemDefaultZone());
        LocalTime test = LocalTime.nowSystemClock();
        long diff = Math.abs(test.toNanoOfDay() - expected.toNanoOfDay());
        assertTrue(diff < 100000000);  // less than 0.1 secs
    }

    //-----------------------------------------------------------------------
    // of() factories
    //-----------------------------------------------------------------------
    public void factory_time_2ints() {
        LocalTime test = LocalTime.of(12, 30);
        check(test, 12, 30, 0, 0);
    }

    public void factory_time_2ints_singletons() {
        for (int i = 0; i < 24; i++) {
            LocalTime test1 = LocalTime.of(i, 0);
            LocalTime test2 = LocalTime.of(i, 0);
            assertSame(test1, test2);
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_time_2ints_hourTooLow() {
        LocalTime.of(-1, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_time_2ints_hourTooHigh() {
        LocalTime.of(24, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_time_2ints_minuteTooLow() {
        LocalTime.of(0, -1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_time_2ints_minuteTooHigh() {
        LocalTime.of(0, 60);
    }

    //-----------------------------------------------------------------------
    public void factory_time_3ints() {
        LocalTime test = LocalTime.of(12, 30, 40);
        check(test, 12, 30, 40, 0);
    }

    public void factory_time_3ints_singletons() {
        for (int i = 0; i < 24; i++) {
            LocalTime test1 = LocalTime.of(i, 0, 0);
            LocalTime test2 = LocalTime.of(i, 0, 0);
            assertSame(test1, test2);
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_time_3ints_hourTooLow() {
        LocalTime.of(-1, 0, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_time_3ints_hourTooHigh() {
        LocalTime.of(24, 0, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_time_3ints_minuteTooLow() {
        LocalTime.of(0, -1, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_time_3ints_minuteTooHigh() {
        LocalTime.of(0, 60, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_time_3ints_secondTooLow() {
        LocalTime.of(0, 0, -1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_time_3ints_secondTooHigh() {
        LocalTime.of(0, 0, 60);
    }

    //-----------------------------------------------------------------------
    public void factory_time_4ints() {
        LocalTime test = LocalTime.of(12, 30, 40, 987654321);
        check(test, 12, 30, 40, 987654321);
        test = LocalTime.of(12, 0, 40, 987654321);
        check(test, 12, 0, 40, 987654321);
    }

    public void factory_time_4ints_singletons() {
        for (int i = 0; i < 24; i++) {
            LocalTime test1 = LocalTime.of(i, 0, 0, 0);
            LocalTime test2 = LocalTime.of(i, 0, 0, 0);
            assertSame(test1, test2);
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_time_4ints_hourTooLow() {
        LocalTime.of(-1, 0, 0, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_time_4ints_hourTooHigh() {
        LocalTime.of(24, 0, 0, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_time_4ints_minuteTooLow() {
        LocalTime.of(0, -1, 0, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_time_4ints_minuteTooHigh() {
        LocalTime.of(0, 60, 0, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_time_4ints_secondTooLow() {
        LocalTime.of(0, 0, -1, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_time_4ints_secondTooHigh() {
        LocalTime.of(0, 0, 60, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_time_4ints_nanoTooLow() {
        LocalTime.of(0, 0, 0, -1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_time_4ints_nanoTooHigh() {
        LocalTime.of(0, 0, 0, 1000000000);
    }

    //-----------------------------------------------------------------------
    public void factory_time_TimeProvider() {
        LocalTime localTime = LocalTime.of(TEST_12_30_40_987654321);
        check(localTime, 12, 30, 40, 987654321);
    }

    public void factory_time_TimeProvider_singletons() {
        for (int i = 0; i < 24; i++) {
            LocalTime test1 = LocalTime.of(LocalTime.of(i, 0));
            LocalTime test2 = LocalTime.of(i, 0);
            assertSame(test1, test2);
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_time_TimeProvider_null() {
        LocalTime.of(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_time_TimeProvider_null_toLocalTime() {
        LocalTime.of(new TimeProvider() {
            public LocalTime toLocalTime() {
                return null;
            }
        });
    }

    //-----------------------------------------------------------------------
    public void factory_time_multiProvider_checkAmbiguous() {
        MockMultiProvider mmp = new MockMultiProvider(2008, 6, 30, 11, 30, 10, 500);
        LocalTime test = LocalTime.of(mmp);
        check(test, 11, 30, 10, 500);
    }

    //-----------------------------------------------------------------------
    // fromSecondOfDay(long)
    //-----------------------------------------------------------------------
    public void factory_fromSecondOfDay() {
        LocalTime localTime = LocalTime.ofSecondOfDay(2 * 60 * 60 + 17 * 60 + 23);
        check(localTime, 2, 17, 23, 0);
    }

    public void factory_fromSecondOfDay_singletons() {
        for (int i = 0; i < 24; i++) {
            LocalTime test1 = LocalTime.ofSecondOfDay(i * 60L * 60L);
            LocalTime test2 = LocalTime.of(i, 0);
            assertSame(test1, test2);
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_fromSecondOfDay_tooLow() {
        try {
            LocalTime.ofSecondOfDay(-1);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), ISOChronology.secondOfDayRule());
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_fromSecondOfDay_tooHigh() {
        try {
            LocalTime.ofSecondOfDay(24 * 60 * 60);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), ISOChronology.secondOfDayRule());
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // fromSecondOfDay(long, int)
    //-----------------------------------------------------------------------
    public void factory_fromSecondOfDay_long_int() {
        LocalTime localTime = LocalTime.ofSecondOfDay(2 * 60 * 60 + 17 * 60 + 23, 987);
        check(localTime, 2, 17, 23, 987);
    }

    public void factory_fromSecondOfDay7_long_int_singletons() {
        for (int i = 0; i < 24; i++) {
            LocalTime test1 = LocalTime.ofSecondOfDay(i * 60L * 60L, 0);
            LocalTime test2 = LocalTime.of(i, 0);
            assertSame(test1, test2);
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_fromSecondOfDay_long_int_tooLowSecs() {
        try {
            LocalTime.ofSecondOfDay(-1, 0);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), ISOChronology.secondOfDayRule());
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_fromSecondOfDay_long_int_tooHighSecs() {
        try {
            LocalTime.ofSecondOfDay(24 * 60 * 60, 0);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), ISOChronology.secondOfDayRule());
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_fromSecondOfDay_long_int_tooLowNanos() {
        try {
            LocalTime.ofSecondOfDay(0, -1);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), ISOChronology.nanoOfSecondRule());
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_fromSecondOfDay_long_int_tooHighNanos() {
        try {
            LocalTime.ofSecondOfDay(0, 1000000000);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), ISOChronology.nanoOfSecondRule());
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // fromNanoOfDay(long)
    //-----------------------------------------------------------------------
    public void factory_fromNanoOfDay() {
        LocalTime localTime = LocalTime.ofNanoOfDay(60 * 60 * 1000000000L + 17);
        check(localTime, 1, 0, 0, 17);
    }

    public void factory_fromNanoOfDay_singletons() {
        for (int i = 0; i < 24; i++) {
            LocalTime test1 = LocalTime.ofNanoOfDay(i * 1000000000L * 60L * 60L);
            LocalTime test2 = LocalTime.of(i, 0);
            assertSame(test1, test2);
        }
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void factory_fromNanoOfDay_tooLow() {
        LocalTime.ofNanoOfDay(-1);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void factory_fromNanoOfDay_tooHigh() {
        LocalTime.ofNanoOfDay(24 * 60 * 60 * 1000000000L);
    }

    //-----------------------------------------------------------------------
    // parse()
    //-----------------------------------------------------------------------
    @Test(dataProvider = "sampleToString")
    public void factory_parse_validText(int h, int m, int s, int n, String parsable) {
        LocalTime t = LocalTime.parse(parsable);
        assertNotNull(t, parsable);
        assertEquals(t.getHourOfDay(), h);
        assertEquals(t.getMinuteOfHour(), m);
        assertEquals(t.getSecondOfMinute(), s);
        assertEquals(t.getNanoOfSecond(), n);
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
                {"11:30+01:00"},
                {"11:30+01:00[Europe/Paris]"},
        };
    }

    @Test(dataProvider = "sampleBadParse", expectedExceptions={CalendricalParseException.class})
    public void factory_parse_invalidText(String unparsable) {
        LocalTime.parse(unparsable);
    }

    //-----------------------------------------------------------------------s
    @Test(expectedExceptions={IllegalCalendarFieldValueException.class})
    public void factory_parse_illegalHour() {
        LocalTime.parse("25:00");
    }

    @Test(expectedExceptions={IllegalCalendarFieldValueException.class})
    public void factory_parse_illegalMinute() {
        LocalTime.parse("12:60");
    }

    @Test(expectedExceptions={IllegalCalendarFieldValueException.class})
    public void factory_parse_illegalSecond() {
        LocalTime.parse("12:12:60");
    }

    //-----------------------------------------------------------------------s
    @Test(expectedExceptions = {NullPointerException.class})
    public void factory_parse_nullTest() {
        LocalTime.parse((String) null);
    }

    //-----------------------------------------------------------------------
    // getChronology()
    //-----------------------------------------------------------------------
    public void test_getChronology() {
        assertSame(ISOChronology.INSTANCE, TEST_12_30_40_987654321.getChronology());
    }

    //-----------------------------------------------------------------------
    // get(CalendricalRule)
    //-----------------------------------------------------------------------
    public void test_get_CalendricalRule() {
        LocalTime test = TEST_12_30_40_987654321;
        assertEquals(test.get(ISOChronology.yearRule()), null);
        assertEquals(test.get(ISOChronology.quarterOfYearRule()), null);
        assertEquals(test.get(ISOChronology.monthOfYearRule()), null);
        assertEquals(test.get(ISOChronology.monthOfQuarterRule()), null);
        assertEquals(test.get(ISOChronology.dayOfMonthRule()), null);
        assertEquals(test.get(ISOChronology.dayOfWeekRule()), null);
        assertEquals(test.get(ISOChronology.dayOfYearRule()), null);
        assertEquals(test.get(ISOChronology.weekOfWeekBasedYearRule()), null);
        assertEquals(test.get(ISOChronology.weekBasedYearRule()), null);
        
        assertEquals(test.get(ISOChronology.hourOfDayRule()), (Integer) 12);
        assertEquals(test.get(ISOChronology.minuteOfHourRule()), (Integer) 30);
        assertEquals(test.get(ISOChronology.secondOfMinuteRule()), (Integer) 40);
        assertEquals(test.get(ISOChronology.nanoOfSecondRule()), (Integer) 987654321);
        assertEquals(test.get(ISOChronology.hourOfAmPmRule()), (Integer) 0);
        assertEquals(test.get(ISOChronology.amPmOfDayRule()), AmPmOfDay.PM);
        
        assertEquals(test.get(LocalDate.rule()), null);
        assertEquals(test.get(LocalTime.rule()), test);
        assertEquals(test.get(LocalDateTime.rule()), null);
        assertEquals(test.get(OffsetDate.rule()), null);
        assertEquals(test.get(OffsetTime.rule()), null);
        assertEquals(test.get(OffsetDateTime.rule()), null);
        assertEquals(test.get(ZonedDateTime.rule()), null);
        assertEquals(test.get(ZoneOffset.rule()), null);
        assertEquals(test.get(TimeZone.rule()), null);
        assertEquals(test.get(YearMonth.rule()), null);
        assertEquals(test.get(MonthDay.rule()), null);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_get_CalendricalRule_null() {
        TEST_12_30_40_987654321.get((CalendricalRule<?>) null);
    }

    public void test_get_unsupported() {
        assertEquals(TEST_12_30_40_987654321.get(MockRuleNoValue.INSTANCE), null);
    }

    //-----------------------------------------------------------------------
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

    //-----------------------------------------------------------------------
    // get*()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes")
    public void test_get(int h, int m, int s, int ns) {
        LocalTime a = LocalTime.of(h, m, s, ns);
        assertEquals(a.getHourOfDay(), h);
        assertEquals(a.getMinuteOfHour(), m);
        assertEquals(a.getSecondOfMinute(), s);
        assertEquals(a.getNanoOfSecond(), ns);
    }

    //-----------------------------------------------------------------------
    // with()
    //-----------------------------------------------------------------------
    public void test_with() {
        TimeAdjuster timeAdjuster = new TimeAdjuster() {
            public LocalTime adjustTime(LocalTime time) {
                return LocalTime.of(23, 5);
            }
        };
        assertEquals(TEST_12_30_40_987654321.with(timeAdjuster), LocalTime.of(23, 5));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_with_null_adjustTime() {
        TEST_12_30_40_987654321.with(new MockTimeAdjusterReturnsNull());
    }

    //-----------------------------------------------------------------------
    // withHourOfDay()
    //-----------------------------------------------------------------------
    public void test_withHourOfDay_normal() {
        LocalTime t = TEST_12_30_40_987654321;
        for (int i = 0; i < 24; i++) {
            t = t.withHourOfDay(i);
            assertEquals(t.getHourOfDay(), i);
        }
    }

    public void test_withHourOfDay_noChange() {
        LocalTime t = TEST_12_30_40_987654321.withHourOfDay(12);
        assertSame(t, TEST_12_30_40_987654321);
    }

    public void test_withHourOfDay_toMidnight() {
        LocalTime t = LocalTime.of(1, 0).withHourOfDay(0);
        assertSame(t, LocalTime.MIDNIGHT);
    }

    public void test_withHourOfDay_toMidday() {
        LocalTime t = LocalTime.of(1, 0).withHourOfDay(12);
        assertSame(t, LocalTime.MIDDAY);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withHourOfDay_hourTooLow() {
        TEST_12_30_40_987654321.withHourOfDay(-1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withHourOfDay_hourTooHigh() {
        TEST_12_30_40_987654321.withHourOfDay(24);
    }

    //-----------------------------------------------------------------------
    // withMinuteOfHour()
    //-----------------------------------------------------------------------
    public void test_withMinuteOfHour_normal() {
        LocalTime t = TEST_12_30_40_987654321;
        for (int i = 0; i < 60; i++) {
            t = t.withMinuteOfHour(i);
            assertEquals(t.getMinuteOfHour(), i);
        }
    }

    public void test_withMinuteOfHour_noChange() {
        LocalTime t = TEST_12_30_40_987654321.withMinuteOfHour(30);
        assertSame(t, TEST_12_30_40_987654321);
    }

    public void test_withMinuteOfHour_toMidnight() {
        LocalTime t = LocalTime.of(0, 1).withMinuteOfHour(0);
        assertSame(t, LocalTime.MIDNIGHT);
    }

    public void test_withMinuteOfHour_toMidday() {
        LocalTime t = LocalTime.of(12, 1).withMinuteOfHour(0);
        assertSame(t, LocalTime.MIDDAY);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withMinuteOfHour_minuteTooLow() {
        TEST_12_30_40_987654321.withMinuteOfHour(-1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withMinuteOfHour_minuteTooHigh() {
        TEST_12_30_40_987654321.withMinuteOfHour(60);
    }

    //-----------------------------------------------------------------------
    // withSecondOfMinute()
    //-----------------------------------------------------------------------
    public void test_withSecondOfMinute_normal() {
        LocalTime t = TEST_12_30_40_987654321;
        for (int i = 0; i < 60; i++) {
            t = t.withSecondOfMinute(i);
            assertEquals(t.getSecondOfMinute(), i);
        }
    }

    public void test_withSecondOfMinute_noChange() {
        LocalTime t = TEST_12_30_40_987654321.withSecondOfMinute(40);
        assertSame(t, TEST_12_30_40_987654321);
    }

    public void test_withSecondOfMinute_toMidnight() {
        LocalTime t = LocalTime.of(0, 0, 1).withSecondOfMinute(0);
        assertSame(t, LocalTime.MIDNIGHT);
    }

    public void test_withSecondOfMinute_toMidday() {
        LocalTime t = LocalTime.of(12, 0, 1).withSecondOfMinute(0);
        assertSame(t, LocalTime.MIDDAY);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withSecondOfMinute_secondTooLow() {
        TEST_12_30_40_987654321.withSecondOfMinute(-1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withSecondOfMinute_secondTooHigh() {
        TEST_12_30_40_987654321.withSecondOfMinute(60);
    }

    //-----------------------------------------------------------------------
    // withNanoOfSecond()
    //-----------------------------------------------------------------------
    public void test_withNanoOfSecond_normal() {
        LocalTime t = TEST_12_30_40_987654321;
        t = t.withNanoOfSecond(1);
        assertEquals(t.getNanoOfSecond(), 1);
        t = t.withNanoOfSecond(10);
        assertEquals(t.getNanoOfSecond(), 10);
        t = t.withNanoOfSecond(100);
        assertEquals(t.getNanoOfSecond(), 100);
        t = t.withNanoOfSecond(999999999);
        assertEquals(t.getNanoOfSecond(), 999999999);
    }

    public void test_withNanoOfSecond_noChange() {
        LocalTime t = TEST_12_30_40_987654321.withNanoOfSecond(987654321);
        assertSame(t, TEST_12_30_40_987654321);
    }

    public void test_withNanoOfSecond_toMidnight() {
        LocalTime t = LocalTime.of(0, 0, 0, 1).withNanoOfSecond(0);
        assertSame(t, LocalTime.MIDNIGHT);
    }

    public void test_withNanoOfSecond_toMidday() {
        LocalTime t = LocalTime.of(12, 0, 0, 1).withNanoOfSecond(0);
        assertSame(t, LocalTime.MIDDAY);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withNanoOfSecond_nanoTooLow() {
        TEST_12_30_40_987654321.withNanoOfSecond(-1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withNanoOfSecond_nanoTooHigh() {
        TEST_12_30_40_987654321.withNanoOfSecond(1000000000);
    }

    //-----------------------------------------------------------------------
    // plus(PeriodProvider)
    //-----------------------------------------------------------------------
    public void test_plus_PeriodProvider() {
        PeriodProvider provider = Period.of(0, 0, 0, 4, 5, 6, 7);
        LocalTime t = TEST_12_30_40_987654321.plus(provider);
        assertEquals(t, LocalTime.of(16, 35, 46, 987654328));
    }

    public void test_plus_PeriodProvider_max() {
        PeriodProvider provider = Period.of(0, 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE);
        LocalTime t = TEST_12_30_40_987654321.plus(provider);
        assertEquals(t, TEST_12_30_40_987654321.plusHours(Integer.MAX_VALUE).plusMinutes(Integer.MAX_VALUE).plusSeconds(Integer.MAX_VALUE).plusNanos(Long.MAX_VALUE));
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plus_PeriodProvider_dateIgnored() {
        PeriodProvider provider = Period.of(1, 2, Integer.MAX_VALUE, 4, 5, 6, 7);
        TEST_12_30_40_987654321.plus(provider);
    }

    public void test_plus_PeriodProvider_zero() {
        LocalTime t = TEST_12_30_40_987654321.plus(Period.ZERO);
        assertSame(t, TEST_12_30_40_987654321);
    }

    public void test_plus_PeriodProvider_overflowIgnored() {
        PeriodProvider provider = Period.hours(1);
        LocalTime t = LocalTime.of(23, 30).plus(provider);
        assertEquals(t, LocalTime.of(0, 30));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_plus_PeriodProvider_null() {
        TEST_12_30_40_987654321.plus((PeriodProvider) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_plus_PeriodProvider_badProvider() {
        TEST_12_30_40_987654321.plus(new MockPeriodProviderReturnsNull());
    }

    //-----------------------------------------------------------------------
    // plusHours()
    //-----------------------------------------------------------------------
    public void test_plusHours_one() {
        LocalTime t = LocalTime.MIDNIGHT;
        for (int i = 0; i < 50; i++) {
            t = t.plusHours(1);
            assertEquals(t.getHourOfDay(), (i + 1) % 24);
        }
    }

    public void test_plusHours_fromZero() {
        LocalTime base = LocalTime.MIDNIGHT;
        for (int i = -50; i < 50; i++) {
            LocalTime t = base.plusHours(i);
            assertEquals(t.getHourOfDay(), (i + 72) % 24);
        }
    }

    public void test_plusHours_fromOne() {
        LocalTime base = LocalTime.of(1, 0);
        for (int i = -50; i < 50; i++) {
            LocalTime t = base.plusHours(i);
            assertEquals(t.getHourOfDay(), (1 + i + 72) % 24);
        }
    }

    public void test_plusHours_noChange() {
        LocalTime t = TEST_12_30_40_987654321.plusHours(0);
        assertSame(t, TEST_12_30_40_987654321);
    }

    public void test_plusHours_toMidnight() {
        LocalTime t = LocalTime.of(23, 0).plusHours(1);
        assertSame(t, LocalTime.MIDNIGHT);
    }

    public void test_plusHours_toMidday() {
        LocalTime t = LocalTime.of(11, 0).plusHours(1);
        assertSame(t, LocalTime.MIDDAY);
    }

    //-----------------------------------------------------------------------
    // plusMinutes()
    //-----------------------------------------------------------------------
    public void test_plusMinutes_one() {
        LocalTime t = LocalTime.MIDNIGHT;
        int hour = 0;
        int min = 0;
        for (int i = 0; i < 70; i++) {
            t = t.plusMinutes(1);
            min++;
            if (min == 60) {
                hour++;
                min = 0;
            }
            assertEquals(t.getHourOfDay(), hour);
            assertEquals(t.getMinuteOfHour(), min);
        }
    }

    public void test_plusMinutes_fromZero() {
        LocalTime base = LocalTime.MIDNIGHT;
        int hour;
        int min;
        for (int i = -70; i < 70; i++) {
            LocalTime t = base.plusMinutes(i);
            if (i < -60) {
                hour = 22;
                min = i + 120;
            } else if (i < 0) {
                hour = 23;
                min = i + 60;
            } else if (i >= 60) {
                hour = 1;
                min = i - 60;
            } else {
                hour = 0;
                min = i;
            }
            assertEquals(t.getHourOfDay(), hour);
            assertEquals(t.getMinuteOfHour(), min);
        }
    }

    public void test_plusMinutes_noChange() {
        LocalTime t = TEST_12_30_40_987654321.plusMinutes(0);
        assertSame(t, TEST_12_30_40_987654321);
    }

    public void test_plusMinutes_noChange_oneDay() {
        LocalTime t = TEST_12_30_40_987654321.plusMinutes(24 * 60);
        assertSame(t, TEST_12_30_40_987654321);
    }

    public void test_plusMinutes_toMidnight() {
        LocalTime t = LocalTime.of(23, 59).plusMinutes(1);
        assertSame(t, LocalTime.MIDNIGHT);
    }

    public void test_plusMinutes_toMidday() {
        LocalTime t = LocalTime.of(11, 59).plusMinutes(1);
        assertSame(t, LocalTime.MIDDAY);
    }

    //-----------------------------------------------------------------------
    // plusSeconds()
    //-----------------------------------------------------------------------
    public void test_plusSeconds_one() {
        LocalTime t = LocalTime.MIDNIGHT;
        int hour = 0;
        int min = 0;
        int sec = 0;
        for (int i = 0; i < 3700; i++) {
            t = t.plusSeconds(1);
            sec++;
            if (sec == 60) {
                min++;
                sec = 0;
            }
            if (min == 60) {
                hour++;
                min = 0;
            }
            assertEquals(t.getHourOfDay(), hour);
            assertEquals(t.getMinuteOfHour(), min);
            assertEquals(t.getSecondOfMinute(), sec);
        }
    }

    @DataProvider(name="plusSeconds_fromZero")
    Iterator<Object[]> plusSeconds_fromZero() {
        return new Iterator<Object[]>() {
            int delta = 30;
            int i = -3660;
            int hour = 22;
            int min = 59;
            int sec = 0;

            public boolean hasNext() {
                return i <= 3660;
            }

            public Object[] next() {
                final Object[] ret = new Object[] {i, hour, min, sec};
                i += delta;
                sec += delta;

                if (sec >= 60) {
                    min++;
                    sec -= 60;

                    if (min == 60) {
                        hour++;
                        min = 0;
                        
                        if (hour == 24) {
                            hour = 0;
                        }
                    }
                }

                return ret;
            }

            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @Test(dataProvider="plusSeconds_fromZero")
    public void test_plusSeconds_fromZero(int seconds, int hour, int min, int sec) {
        LocalTime base = LocalTime.MIDNIGHT;
        LocalTime t = base.plusSeconds(seconds);

        assertEquals(hour, t.getHourOfDay());
        assertEquals(min, t.getMinuteOfHour());
        assertEquals(sec, t.getSecondOfMinute());
    }

    public void test_plusSeconds_noChange() {
        LocalTime t = TEST_12_30_40_987654321.plusSeconds(0);
        assertSame(t, TEST_12_30_40_987654321);
    }

    public void test_plusSeconds_noChange_oneDay() {
        LocalTime t = TEST_12_30_40_987654321.plusSeconds(24 * 60 * 60);
        assertSame(t, TEST_12_30_40_987654321);
    }

    public void test_plusSeconds_toMidnight() {
        LocalTime t = LocalTime.of(23, 59, 59).plusSeconds(1);
        assertSame(t, LocalTime.MIDNIGHT);
    }

    public void test_plusSeconds_toMidday() {
        LocalTime t = LocalTime.of(11, 59, 59).plusSeconds(1);
        assertSame(t, LocalTime.MIDDAY);
    }

    //-----------------------------------------------------------------------
    // plusNanos()
    //-----------------------------------------------------------------------
    public void test_plusNanos_halfABillion() {
        LocalTime t = LocalTime.MIDNIGHT;
        int hour = 0;
        int min = 0;
        int sec = 0;
        int nanos = 0;
        for (long i = 0; i < 3700 * 1000000000L; i+= 500000000) {
            t = t.plusNanos(500000000);
            nanos += 500000000;
            if (nanos == 1000000000) {
                sec++;
                nanos = 0;
            }
            if (sec == 60) {
                min++;
                sec = 0;
            }
            if (min == 60) {
                hour++;
                min = 0;
            }
            assertEquals(t.getHourOfDay(), hour);
            assertEquals(t.getMinuteOfHour(), min);
            assertEquals(t.getSecondOfMinute(), sec);
            assertEquals(t.getNanoOfSecond(), nanos);
        }
    }

    @DataProvider(name="plusNanos_fromZero")
    Iterator<Object[]> plusNanos_fromZero() {
        return new Iterator<Object[]>() {
            long delta = 7500000000L;
            long i = -3660 * 1000000000L;
            int hour = 22;
            int min = 59;
            int sec = 0;
            long nanos = 0;

            public boolean hasNext() {
                return i <= 3660 * 1000000000L;
            }

            public Object[] next() {
                final Object[] ret = new Object[] {i, hour, min, sec, (int)nanos};
                i += delta;
                nanos += delta;

                if (nanos >= 1000000000L) {
                    sec += nanos / 1000000000L;
                    nanos %= 1000000000L;

                    if (sec >= 60) {
                        min++;
                        sec %= 60;

                        if (min == 60) {
                            hour++;
                            min = 0;

                            if (hour == 24) {
                                hour = 0;
                            }
                        }
                    }
                }

                return ret;
            }

            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @Test(dataProvider="plusNanos_fromZero")
    public void test_plusNanos_fromZero(long nanoseconds, int hour, int min, int sec, int nanos) {
        LocalTime base = LocalTime.MIDNIGHT;
        LocalTime t = base.plusNanos(nanoseconds);

        assertEquals(hour, t.getHourOfDay());
        assertEquals(min, t.getMinuteOfHour());
        assertEquals(sec, t.getSecondOfMinute());
        assertEquals(nanos, t.getNanoOfSecond());
    }

    public void test_plusNanos_noChange() {
        LocalTime t = TEST_12_30_40_987654321.plusNanos(0);
        assertSame(t, TEST_12_30_40_987654321);
    }

    public void test_plusNanos_noChange_oneDay() {
        LocalTime t = TEST_12_30_40_987654321.plusNanos(24 * 60 * 60 * 1000000000L);
        assertSame(t, TEST_12_30_40_987654321);
    }

    public void test_plusNanos_toMidnight() {
        LocalTime t = LocalTime.of(23, 59, 59, 999999999).plusNanos(1);
        assertSame(t, LocalTime.MIDNIGHT);
    }

    public void test_plusNanos_toMidday() {
        LocalTime t = LocalTime.of(11, 59, 59, 999999999).plusNanos(1);
        assertSame(t, LocalTime.MIDDAY);
    }

    //-----------------------------------------------------------------------
    // plusWithOverflow()
    //-----------------------------------------------------------------------
    public void test_plusWithOverflow_zero() {
        Overflow t = TEST_12_30_40_987654321.plusWithOverflow(0, 0, 0, 0);
        assertEquals(t.getResultTime(), TEST_12_30_40_987654321);
        assertEquals(t.getOverflowDays(), 0);
    }

    public void test_plusWithOverflow_midday_nano() {
        Overflow t = LocalTime.of(11, 59, 59, 999999999).plusWithOverflow(0, 0, 0, 1);
        assertEquals(t.getResultTime(), LocalTime.MIDDAY);
        assertEquals(t.getOverflowDays(), 0);
    }

    public void test_plusWithOverflow_midnight_nano() {
        Overflow t = LocalTime.of(23, 59, 59, 999999999).plusWithOverflow(0, 0, 0, 1);
        assertEquals(t.getResultTime(), LocalTime.MIDNIGHT);
        assertEquals(t.getOverflowDays(), 1);
    }

    public void test_plusWithOverflow_midnight_second() {
        Overflow t = LocalTime.of(23, 59, 59, 0).plusWithOverflow(0, 0, 1, 0);
        assertEquals(t.getResultTime(), LocalTime.MIDNIGHT);
        assertEquals(t.getOverflowDays(), 1);
    }

    public void test_plusWithOverflow_midnight_minute() {
        Overflow t = LocalTime.of(23, 59, 0, 0).plusWithOverflow(0, 1, 0, 0);
        assertEquals(t.getResultTime(), LocalTime.MIDNIGHT);
        assertEquals(t.getOverflowDays(), 1);
    }

    public void test_plusWithOverflow_midnight_hour() {
        Overflow t = LocalTime.of(23, 0, 0, 0).plusWithOverflow(1, 0, 0, 0);
        assertEquals(t.getResultTime(), LocalTime.MIDNIGHT);
        assertEquals(t.getOverflowDays(), 1);
    }

    public void test_plusWithOverflow_midnight_minusNano() {
        Overflow t = LocalTime.MIDNIGHT.plusWithOverflow(0, 0, 0, -1);
        assertEquals(t.getResultTime(), LocalTime.of(23, 59, 59, 999999999));
        assertEquals(t.getOverflowDays(), -1);
    }

    public void test_plusWithOverflow_midnight_minusSecond() {
        Overflow t = LocalTime.MIDNIGHT.plusWithOverflow(0, 0, -1, 0);
        assertEquals(t.getResultTime(), LocalTime.of(23, 59, 59));
        assertEquals(t.getOverflowDays(), -1);
    }

    public void test_plusWithOverflow_midnight_minusMinute() {
        Overflow t = LocalTime.MIDNIGHT.plusWithOverflow(0, -1, 0, 0);
        assertEquals(t.getResultTime(), LocalTime.of(23, 59));
        assertEquals(t.getOverflowDays(), -1);
    }

    public void test_plusWithOverflow_midnight_minusHour() {
        Overflow t = LocalTime.MIDNIGHT.plusWithOverflow(-1, 0, 0, 0);
        assertEquals(t.getResultTime(), LocalTime.of(23, 0));
        assertEquals(t.getOverflowDays(), -1);
    }

    public void test_plusWithOverflow_hourMinuteAddToZero() {
        Overflow t = TEST_12_30_40_987654321.plusWithOverflow(1, -60, 0, 0);
        assertSame(t.getResultTime(), TEST_12_30_40_987654321);
        assertEquals(t.getOverflowDays(), 0);
    }

    public void test_plusWithOverflow_hourMinuteOneDayAddToZero() {
        Overflow t = TEST_12_30_40_987654321.plusWithOverflow(24, -(24 * 60), 0, 0);
        assertSame(t.getResultTime(), TEST_12_30_40_987654321);
        assertEquals(t.getOverflowDays(), 0);
    }

    public void test_plusWithOverflow_secNanoAddToZero() {
        Overflow t = TEST_12_30_40_987654321.plusWithOverflow(0, 0, 1, -1000000000);
        assertSame(t.getResultTime(), TEST_12_30_40_987654321);
        assertEquals(t.getOverflowDays(), 0);
    }

    public void test_plusWithOverflow_secNanoOneDayAddToZero() {
        Overflow t = TEST_12_30_40_987654321.plusWithOverflow(0, 0, 24 * 60 * 60, -(24 * 60 * 60 * 1000000000L));
        assertSame(t.getResultTime(), TEST_12_30_40_987654321);
        assertEquals(t.getOverflowDays(), 0);
    }

    public void test_plusWithOverflow_allNanosAddToZero() {
        Overflow t = LocalTime.of(1, 0).plusWithOverflow(-1, 0, 0, 0);
        assertSame(t.getResultTime(), LocalTime.MIDNIGHT);
        assertEquals(t.getOverflowDays(), 0);
    }

    public void test_plusWithOveflow_oneDay_nanos() {
        for (int days = -10; days < 10; days++) {
            Overflow t = TEST_12_30_40_987654321.plusWithOverflow(0, 0, 0, days * 24 * 60 * 60 * 1000000000L);
            assertSame(t.getResultTime(), TEST_12_30_40_987654321);
            assertEquals(t.getOverflowDays(), days);
        }
    }

    public void test_plusWithOveflow_oneDay_seconds() {
        for (int days = -10; days < 10; days++) {
            Overflow t = TEST_12_30_40_987654321.plusWithOverflow(0, 0, days * 24 * 60 * 60, 0);
            assertSame(t.getResultTime(), TEST_12_30_40_987654321);
            assertEquals(t.getOverflowDays(), days);
        }
    }

    public void test_plusWithOveflow_oneDay_minutes() {
        for (int days = -10; days < 10; days++) {
            Overflow t = TEST_12_30_40_987654321.plusWithOverflow(0, days * 24 * 60, 0, 0);
            assertSame(t.getResultTime(), TEST_12_30_40_987654321);
            assertEquals(t.getOverflowDays(), days);
        }
    }

    public void test_plusWithOveflow_oneDay_days() {
        for (int days = -10; days < 10; days++) {
            Overflow t = TEST_12_30_40_987654321.plusWithOverflow(days * 24, 0, 0, 0);
            assertSame(t.getResultTime(), TEST_12_30_40_987654321);
            assertEquals(t.getOverflowDays(), days);
        }
    }

    public void test_plusWithOveflow_max() {
        Overflow t = TEST_12_30_40_987654321.plusWithOverflow(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE);
        long nanos = Long.MAX_VALUE % (24 * 60 * 60 * 1000000000L);
        nanos += (Integer.MAX_VALUE % (24 * 60 * 60)) * 1000000000L;
        nanos += (Integer.MAX_VALUE % (24 * 60)) * 60 * 1000000000L;
        nanos += (Integer.MAX_VALUE % (24)) * 60 * 60 * 1000000000L;
        Overflow justNanos = TEST_12_30_40_987654321.plusWithOverflow(0, 0, 0, nanos);
        long days = Long.MAX_VALUE / (24 * 60 * 60 * 1000000000L);
        days += Integer.MAX_VALUE / (24 * 60 * 60);
        days += Integer.MAX_VALUE / (24 * 60);
        days += Integer.MAX_VALUE / (24);
        days += justNanos.getOverflowDays();
        assertEquals(t.getOverflowDays(), days);
        assertEquals(t.getResultTime(), justNanos.getResultTime());
    }

    //-----------------------------------------------------------------------
    // minus(PeriodProvider)
    //-----------------------------------------------------------------------
    public void test_minus_PeriodProvider() {
        PeriodProvider provider = Period.of(0, 0, 0, 4, 5, 6, 7);
        LocalTime t = TEST_12_30_40_987654321.minus(provider);
        assertEquals(t, LocalTime.of(8, 25, 34, 987654314));
    }

    public void test_minus_PeriodProvider_max() {
        PeriodProvider provider = Period.of(0, 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE);
        LocalTime t = TEST_12_30_40_987654321.minus(provider);
        assertEquals(t, TEST_12_30_40_987654321.minusHours(Integer.MAX_VALUE).minusMinutes(Integer.MAX_VALUE).minusSeconds(Integer.MAX_VALUE).minusNanos(Long.MAX_VALUE));
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minus_PeriodProvider_dateIgnored() {
        PeriodProvider provider = Period.of(1, 2, Integer.MAX_VALUE, 4, 5, 6, 7);
        TEST_12_30_40_987654321.minus(provider);
    }

    public void test_minus_PeriodProvider_zero() {
        LocalTime t = TEST_12_30_40_987654321.minus(Period.ZERO);
        assertSame(t, TEST_12_30_40_987654321);
    }

    public void test_minus_PeriodProvider_overflowIgnored() {
        PeriodProvider provider = Period.hours(1);
        LocalTime t = LocalTime.of(0, 30).minus(provider);
        assertEquals(t, LocalTime.of(23, 30));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_minus_PeriodProvider_null() {
        TEST_12_30_40_987654321.minus((PeriodProvider) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_minus_PeriodProvider_badProvider() {
        TEST_12_30_40_987654321.minus(new MockPeriodProviderReturnsNull());
    }

    //-----------------------------------------------------------------------
    // minusHours()
    //-----------------------------------------------------------------------
    public void test_minusHours_one() {
        LocalTime t = LocalTime.MIDNIGHT;
        for (int i = 0; i < 50; i++) {
            t = t.minusHours(1);
            assertEquals(t.getHourOfDay(), (((-i + 23) % 24) + 24) % 24, String.valueOf(i));
        }
    }

    public void test_minusHours_fromZero() {
        LocalTime base = LocalTime.MIDNIGHT;
        for (int i = -50; i < 50; i++) {
            LocalTime t = base.minusHours(i);
            assertEquals(t.getHourOfDay(), ((-i % 24) + 24) % 24);
        }
    }

    public void test_minusHours_fromOne() {
        LocalTime base = LocalTime.of(1, 0);
        for (int i = -50; i < 50; i++) {
            LocalTime t = base.minusHours(i);
            assertEquals(t.getHourOfDay(), (1 + (-i % 24) + 24) % 24);
        }
    }

    public void test_minusHours_noChange() {
        LocalTime t = TEST_12_30_40_987654321.minusHours(0);
        assertSame(t, TEST_12_30_40_987654321);
    }

    public void test_minusHours_toMidnight() {
        LocalTime t = LocalTime.of(1, 0).minusHours(1);
        assertSame(t, LocalTime.MIDNIGHT);
    }

    public void test_minusHours_toMidday() {
        LocalTime t = LocalTime.of(13, 0).minusHours(1);
        assertSame(t, LocalTime.MIDDAY);
    }

    //-----------------------------------------------------------------------
    // minusMinutes()
    //-----------------------------------------------------------------------
    public void test_minusMinutes_one() {
        LocalTime t = LocalTime.MIDNIGHT;
        int hour = 0;
        int min = 0;
        for (int i = 0; i < 70; i++) {
            t = t.minusMinutes(1);
            min--;
            if (min == -1) {
                hour--;
                min = 59;
                
                if (hour == -1) {
                    hour = 23;
                }
            }
            assertEquals(t.getHourOfDay(), hour);
            assertEquals(t.getMinuteOfHour(), min);
        }
    }

    public void test_minusMinutes_fromZero() {
        LocalTime base = LocalTime.MIDNIGHT;
        int hour = 22;
        int min = 49;
        for (int i = 70; i > -70; i--) {
            LocalTime t = base.minusMinutes(i);
            min++;
            
            if (min == 60) {
                hour++;
                min = 0;
                
                if (hour == 24) {
                    hour = 0;
                }
            }

            assertEquals(t.getHourOfDay(), hour);
            assertEquals(t.getMinuteOfHour(), min);
        }
    }

    public void test_minusMinutes_noChange() {
        LocalTime t = TEST_12_30_40_987654321.minusMinutes(0);
        assertSame(t, TEST_12_30_40_987654321);
    }

    public void test_minusMinutes_noChange_oneDay() {
        LocalTime t = TEST_12_30_40_987654321.minusMinutes(24 * 60);
        assertSame(t, TEST_12_30_40_987654321);
    }

    public void test_minusMinutes_toMidnight() {
        LocalTime t = LocalTime.of(0, 1).minusMinutes(1);
        assertSame(t, LocalTime.MIDNIGHT);
    }

    public void test_minusMinutes_toMidday() {
        LocalTime t = LocalTime.of(12, 1).minusMinutes(1);
        assertSame(t, LocalTime.MIDDAY);
    }

    //-----------------------------------------------------------------------
    // minusSeconds()
    //-----------------------------------------------------------------------
    public void test_minusSeconds_one() {
        LocalTime t = LocalTime.MIDNIGHT;
        int hour = 0;
        int min = 0;
        int sec = 0;
        for (int i = 0; i < 3700; i++) {
            t = t.minusSeconds(1);
            sec--;
            if (sec == -1) {
                min--;
                sec = 59;

                if (min == -1) {
                    hour--;
                    min = 59;
                    
                    if (hour == -1) {
                        hour = 23;
                    }
                }
            }
            assertEquals(t.getHourOfDay(), hour);
            assertEquals(t.getMinuteOfHour(), min);
            assertEquals(t.getSecondOfMinute(), sec);
        }
    }

    @DataProvider(name="minusSeconds_fromZero")
    Iterator<Object[]> minusSeconds_fromZero() {
        return new Iterator<Object[]>() {
            int delta = 30;
            int i = 3660;
            int hour = 22;
            int min = 59;
            int sec = 0;

            public boolean hasNext() {
                return i >= -3660;
            }

            public Object[] next() {
                final Object[] ret = new Object[] {i, hour, min, sec};
                i -= delta;
                sec += delta;

                if (sec >= 60) {
                    min++;
                    sec -= 60;

                    if (min == 60) {
                        hour++;
                        min = 0;
                        
                        if (hour == 24) {
                            hour = 0;
                        }
                    }
                }

                return ret;
            }

            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @Test(dataProvider="minusSeconds_fromZero")
    public void test_minusSeconds_fromZero(int seconds, int hour, int min, int sec) {
        LocalTime base = LocalTime.MIDNIGHT;
        LocalTime t = base.minusSeconds(seconds);

        assertEquals(t.getHourOfDay(), hour);
        assertEquals(t.getMinuteOfHour(), min);
        assertEquals(t.getSecondOfMinute(), sec);
    }

    public void test_minusSeconds_noChange() {
        LocalTime t = TEST_12_30_40_987654321.minusSeconds(0);
        assertSame(t, TEST_12_30_40_987654321);
    }

    public void test_minusSeconds_noChange_oneDay() {
        LocalTime t = TEST_12_30_40_987654321.minusSeconds(24 * 60 * 60);
        assertSame(t, TEST_12_30_40_987654321);
    }

    public void test_minusSeconds_toMidnight() {
        LocalTime t = LocalTime.of(0, 0, 1).minusSeconds(1);
        assertSame(t, LocalTime.MIDNIGHT);
    }

    public void test_minusSeconds_toMidday() {
        LocalTime t = LocalTime.of(12, 0, 1).minusSeconds(1);
        assertSame(t, LocalTime.MIDDAY);
    }

    //-----------------------------------------------------------------------
    // minusNanos()
    //-----------------------------------------------------------------------
    public void test_minusNanos_halfABillion() {
        LocalTime t = LocalTime.MIDNIGHT;
        int hour = 0;
        int min = 0;
        int sec = 0;
        int nanos = 0;
        for (long i = 0; i < 3700 * 1000000000L; i+= 500000000) {
            t = t.minusNanos(500000000);
            nanos -= 500000000;

            if (nanos < 0) {
                sec--;
                nanos += 1000000000;

                if (sec == -1) {
                    min--;
                    sec += 60;
                    
                    if (min == -1) {
                        hour--;
                        min += 60;

                        if (hour == -1) {
                            hour += 24;
                        }
                    }
                }
            }

            assertEquals(t.getHourOfDay(), hour);
            assertEquals(t.getMinuteOfHour(), min);
            assertEquals(t.getSecondOfMinute(), sec);
            assertEquals(t.getNanoOfSecond(), nanos);
        }
    }

    @DataProvider(name="minusNanos_fromZero")
    Iterator<Object[]> minusNanos_fromZero() {
        return new Iterator<Object[]>() {
            long delta = 7500000000L;
            long i = 3660 * 1000000000L;
            int hour = 22;
            int min = 59;
            int sec = 0;
            long nanos = 0;

            public boolean hasNext() {
                return i >= -3660 * 1000000000L;
            }

            public Object[] next() {
                final Object[] ret = new Object[] {i, hour, min, sec, (int)nanos};
                i -= delta;
                nanos += delta;

                if (nanos >= 1000000000L) {
                    sec += nanos / 1000000000L;
                    nanos %= 1000000000L;

                    if (sec >= 60) {
                        min++;
                        sec %= 60;

                        if (min == 60) {
                            hour++;
                            min = 0;

                            if (hour == 24) {
                                hour = 0;
                            }
                        }
                    }
                }

                return ret;
            }

            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @Test(dataProvider="minusNanos_fromZero")
    public void test_minusNanos_fromZero(long nanoseconds, int hour, int min, int sec, int nanos) {
        LocalTime base = LocalTime.MIDNIGHT;
        LocalTime t = base.minusNanos(nanoseconds);

        assertEquals(hour, t.getHourOfDay());
        assertEquals(min, t.getMinuteOfHour());
        assertEquals(sec, t.getSecondOfMinute());
        assertEquals(nanos, t.getNanoOfSecond());
    }

    public void test_minusNanos_noChange() {
        LocalTime t = TEST_12_30_40_987654321.minusNanos(0);
        assertSame(t, TEST_12_30_40_987654321);
    }

    public void test_minusNanos_noChange_oneDay() {
        LocalTime t = TEST_12_30_40_987654321.minusNanos(24 * 60 * 60 * 1000000000L);
        assertSame(t, TEST_12_30_40_987654321);
    }

    public void test_minusNanos_toMidnight() {
        LocalTime t = LocalTime.of(0, 0, 0, 1).minusNanos(1);
        assertSame(t, LocalTime.MIDNIGHT);
    }

    public void test_minusNanos_toMidday() {
        LocalTime t = LocalTime.of(12, 0, 0, 1).minusNanos(1);
        assertSame(t, LocalTime.MIDDAY);
    }

    //-----------------------------------------------------------------------
    // minusWithOverflow()
    //-----------------------------------------------------------------------
    public void test_minusWithOverflow_zero() {
        Overflow t = TEST_12_30_40_987654321.minusWithOverflow(0, 0, 0, 0);
        assertSame(t.getResultTime(), TEST_12_30_40_987654321);
        assertEquals(t.getOverflowDays(), 0);
    }

    public void test_minusWithOverflow_midday_nano() {
        Overflow t = LocalTime.of(12, 0, 0, 1).minusWithOverflow(0, 0, 0, 1);
        assertEquals(t.getResultTime(), LocalTime.MIDDAY);
        assertEquals(t.getOverflowDays(), 0);
    }

    public void test_minusWithOverflow_midnight_nano() {
        Overflow t = LocalTime.MIDNIGHT.minusWithOverflow(0, 0, 0, 1);
        assertEquals(t.getResultTime(), LocalTime.of(23, 59, 59, 999999999));
        assertEquals(t.getOverflowDays(), -1);
    }

    public void test_minusWithOverflow_midnight_second() {
        Overflow t = LocalTime.MIDNIGHT.minusWithOverflow(0, 0, 1, 0);
        assertEquals(t.getResultTime(), LocalTime.of(23, 59, 59));
        assertEquals(t.getOverflowDays(), -1);
    }

    public void test_minusWithOverflow_midnight_minute() {
        Overflow t = LocalTime.MIDNIGHT.minusWithOverflow(0, 1, 0, 0);
        assertEquals(t.getResultTime(), LocalTime.of(23, 59));
        assertEquals(t.getOverflowDays(), -1);
    }

    public void test_minusWithOverflow_midnight_hour() {
        Overflow t = LocalTime.MIDNIGHT.minusWithOverflow(1, 0, 0, 0);
        assertEquals(t.getResultTime(), LocalTime.of(23, 0));
        assertEquals(t.getOverflowDays(), -1);
    }

    public void test_minusWithOverflow_midnight_minusNano() {
        Overflow t = LocalTime.of(23, 59, 59, 999999999).minusWithOverflow(0, 0, 0, -1);
        assertEquals(t.getResultTime(), LocalTime.MIDNIGHT);
        assertEquals(t.getOverflowDays(), 1);
    }

    public void test_minusWithOverflow_midnight_minusSecond() {
        Overflow t = LocalTime.of(23, 59, 59).minusWithOverflow(0, 0, -1, 0);
        assertEquals(t.getResultTime(), LocalTime.MIDNIGHT);
        assertEquals(t.getOverflowDays(), 1);
    }

    public void test_minusWithOverflow_midnight_minusMinute() {
        Overflow t = LocalTime.of(23, 59).minusWithOverflow(0, -1, 0, 0);
        assertEquals(t.getResultTime(), LocalTime.MIDNIGHT);
        assertEquals(t.getOverflowDays(), 1);
    }

    public void test_minusWithOverflow_midnight_minusHour() {
        Overflow t = LocalTime.of(23, 0).minusWithOverflow(-1, 0, 0, 0);
        assertEquals(t.getResultTime(), LocalTime.MIDNIGHT);
        assertEquals(t.getOverflowDays(), 1);
    }

    public void test_minusWithOverflow_hourMinuteAddToZero() {
        Overflow t = TEST_12_30_40_987654321.minusWithOverflow(1, -60, 0, 0);
        assertSame(t.getResultTime(), TEST_12_30_40_987654321);
        assertEquals(t.getOverflowDays(), 0);
    }

    public void test_minusWithOverflow_hourMinuteOneDayAddToZero() {
        Overflow t = TEST_12_30_40_987654321.minusWithOverflow(24, -(24 * 60), 0, 0);
        assertSame(t.getResultTime(), TEST_12_30_40_987654321);
        assertEquals(t.getOverflowDays(), 0);
    }

    public void test_minusWithOverflow_secNanoAddToZero() {
        Overflow t = TEST_12_30_40_987654321.minusWithOverflow(0, 0, 1, -1000000000);
        assertSame(t.getResultTime(), TEST_12_30_40_987654321);
        assertEquals(t.getOverflowDays(), 0);
    }

    public void test_minusWithOverflow_secNanoOneDayAddToZero() {
        Overflow t = TEST_12_30_40_987654321.minusWithOverflow(0, 0, 24 * 60 * 60, -(24 * 60 * 60 * 1000000000L));
        assertSame(t.getResultTime(), TEST_12_30_40_987654321);
        assertEquals(t.getOverflowDays(), 0);
    }

    public void test_minusWithOverflow_allNanosAddToZero() {
        Overflow t = LocalTime.of(1, 0).minusWithOverflow(1, 0, 0, 0);
        assertSame(t.getResultTime(), LocalTime.MIDNIGHT);
        assertEquals(t.getOverflowDays(), 0);
    }

    public void test_minusWithOveflow_oneDay_nanos() {
        for (int days = -10; days < 10; days++) {
            Overflow t = TEST_12_30_40_987654321.minusWithOverflow(0, 0, 0, days * 24 * 60 * 60 * 1000000000L);
            assertSame(t.getResultTime(), TEST_12_30_40_987654321);
            assertEquals(t.getOverflowDays(), -days);
        }
    }

    public void test_minusWithOveflow_oneDay_seconds() {
        for (int days = -10; days < 10; days++) {
            Overflow t = TEST_12_30_40_987654321.minusWithOverflow(0, 0, days * 24 * 60 * 60, 0);
            assertSame(t.getResultTime(), TEST_12_30_40_987654321);
            assertEquals(t.getOverflowDays(), -days);
        }
    }

    public void test_minusWithOveflow_oneDay_minutes() {
        for (int days = -10; days < 10; days++) {
            Overflow t = TEST_12_30_40_987654321.minusWithOverflow(0, days * 24 * 60, 0, 0);
            assertSame(t.getResultTime(), TEST_12_30_40_987654321);
            assertEquals(t.getOverflowDays(), -days);
        }
    }

    public void test_minusWithOveflow_oneDay_days() {
        for (int days = -10; days < 10; days++) {
            Overflow t = TEST_12_30_40_987654321.minusWithOverflow(days * 24, 0, 0, 0);
            assertSame(t.getResultTime(), TEST_12_30_40_987654321);
            assertEquals(t.getOverflowDays(), -days);
        }
    }

    public void test_minusWithOveflow_max() {
        Overflow t = TEST_12_30_40_987654321.minusWithOverflow(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE);
        long nanos = Long.MAX_VALUE % (24 * 60 * 60 * 1000000000L);
        nanos += (Integer.MAX_VALUE % (24 * 60 * 60)) * 1000000000L;
        nanos += (Integer.MAX_VALUE % (24 * 60)) * 60 * 1000000000L;
        nanos += (Integer.MAX_VALUE % (24)) * 60 * 60 * 1000000000L;
        Overflow justNanos = TEST_12_30_40_987654321.minusWithOverflow(0, 0, 0, nanos);
        long days = Long.MAX_VALUE / (24 * 60 * 60 * 1000000000L);
        days += Integer.MAX_VALUE / (24 * 60 * 60);
        days += Integer.MAX_VALUE / (24 * 60);
        days += Integer.MAX_VALUE / (24);
        days -= justNanos.getOverflowDays();
        assertEquals(t.getOverflowDays(), -days);
        assertEquals(t.getResultTime(), justNanos.getResultTime());
    }

    //-----------------------------------------------------------------------
    // matches()
    //-----------------------------------------------------------------------
    public void test_matches() {
        assertTrue(TEST_12_30_40_987654321.matches(new CalendricalMatcher() {
            public boolean matchesCalendrical(Calendrical calendrical) {
                return true;
            }
        }));
        assertFalse(TEST_12_30_40_987654321.matches(new CalendricalMatcher() {
            public boolean matchesCalendrical(Calendrical calendrical) {
                return false;
            }
        }));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_matches_null() {
        TEST_12_30_40_987654321.matches(null);
    }

    //-----------------------------------------------------------------------
    // atOffset()
    //-----------------------------------------------------------------------
    public void test_atOffset() {
        LocalTime t = LocalTime.of(11, 30);
        assertEquals(t.atOffset(OFFSET_PTWO), OffsetTime.of(11, 30, OFFSET_PTWO));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_atOffset_nullZoneOffset() {
        LocalTime t = LocalTime.of(11, 30);
        t.atOffset((ZoneOffset) null);
    }

    //-----------------------------------------------------------------------
    // toLocalTime()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes")
    public void test_toLocalTime(int h, int m, int s, int ns) {
        LocalTime t = LocalTime.of(h, m, s, ns);
        assertSame(t.toLocalTime(), t);
    }

    //-----------------------------------------------------------------------
    // toSecondOfDay()
    //-----------------------------------------------------------------------
    public void test_toSecondOfDay() {
        LocalTime t = LocalTime.of(0, 0);
        for (int i = 0; i < 24 * 60 * 60; i++) {
            assertEquals(t.toSecondOfDay(), i);
            t = t.plusSeconds(1);
        }
    }

    public void test_toSecondOfDay_fromNanoOfDay_symmetry() {
        LocalTime t = LocalTime.of(0, 0);
        for (int i = 0; i < 24 * 60 * 60; i++) {
            assertEquals(LocalTime.ofSecondOfDay(t.toSecondOfDay()), t);
            t = t.plusSeconds(1);
        }
    }

    //-----------------------------------------------------------------------
    // toNanoOfDay()
    //-----------------------------------------------------------------------
    public void test_toNanoOfDay() {
        LocalTime t = LocalTime.of(0, 0);
        for (int i = 0; i < 1000000; i++) {
            assertEquals(t.toNanoOfDay(), i);
            t = t.plusNanos(1);
        }
        t = LocalTime.of(0, 0);
        for (int i = 1; i <= 1000000; i++) {
            t = t.minusNanos(1);
            assertEquals(t.toNanoOfDay(), 24 * 60 * 60 * 1000000000L - i);
        }
    }

    public void test_toNanoOfDay_fromNanoOfDay_symmetry() {
        LocalTime t = LocalTime.of(0, 0);
        for (int i = 0; i < 1000000; i++) {
            assertEquals(LocalTime.ofNanoOfDay(t.toNanoOfDay()), t);
            t = t.plusNanos(1);
        }
        t = LocalTime.of(0, 0);
        for (int i = 1; i <= 1000000; i++) {
            t = t.minusNanos(1);
            assertEquals(LocalTime.ofNanoOfDay(t.toNanoOfDay()), t);
        }
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_comparisons() {
        doTest_comparisons_LocalTime(
            LocalTime.MIDNIGHT,
            LocalTime.of(0, 0, 0, 999999999),
            LocalTime.of(0, 0, 59, 0),
            LocalTime.of(0, 0, 59, 999999999),
            LocalTime.of(0, 59, 0, 0),
            LocalTime.of(0, 59, 0, 999999999),
            LocalTime.of(0, 59, 59, 0),
            LocalTime.of(0, 59, 59, 999999999),
            LocalTime.MIDDAY,
            LocalTime.of(12, 0, 0, 999999999),
            LocalTime.of(12, 0, 59, 0),
            LocalTime.of(12, 0, 59, 999999999),
            LocalTime.of(12, 59, 0, 0),
            LocalTime.of(12, 59, 0, 999999999),
            LocalTime.of(12, 59, 59, 0),
            LocalTime.of(12, 59, 59, 999999999),
            LocalTime.of(23, 0, 0, 0),
            LocalTime.of(23, 0, 0, 999999999),
            LocalTime.of(23, 0, 59, 0),
            LocalTime.of(23, 0, 59, 999999999),
            LocalTime.of(23, 59, 0, 0),
            LocalTime.of(23, 59, 0, 999999999),
            LocalTime.of(23, 59, 59, 0),
            LocalTime.of(23, 59, 59, 999999999)
        );
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

    @Test(expectedExceptions=NullPointerException.class)
    public void test_compareTo_ObjectNull() {
        TEST_12_30_40_987654321.compareTo(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isBefore_ObjectNull() {
        TEST_12_30_40_987654321.isBefore(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isAfter_ObjectNull() {
        TEST_12_30_40_987654321.isAfter(null);
    }

    @Test(expectedExceptions=ClassCastException.class)
    @SuppressWarnings("unchecked")
    public void compareToNonLocalTime() {
       Comparable c = TEST_12_30_40_987654321;
       c.compareTo(new Object());
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes")
    public void test_equals_true(int h, int m, int s, int n) {
        LocalTime a = LocalTime.of(h, m, s, n);
        LocalTime b = LocalTime.of(h, m, s, n);
        assertEquals(a.equals(b), true);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_hour_differs(int h, int m, int s, int n) {
        LocalTime a = LocalTime.of(h, m, s, n);
        LocalTime b = LocalTime.of(h + 1, m, s, n);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_minute_differs(int h, int m, int s, int n) {
        LocalTime a = LocalTime.of(h, m, s, n);
        LocalTime b = LocalTime.of(h, m + 1, s, n);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_second_differs(int h, int m, int s, int n) {
        LocalTime a = LocalTime.of(h, m, s, n);
        LocalTime b = LocalTime.of(h, m, s + 1, n);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_nano_differs(int h, int m, int s, int n) {
        LocalTime a = LocalTime.of(h, m, s, n);
        LocalTime b = LocalTime.of(h, m, s, n + 1);
        assertEquals(a.equals(b), false);
    }

    public void test_equals_itself_true() {
        assertEquals(TEST_12_30_40_987654321.equals(TEST_12_30_40_987654321), true);
    }

    public void test_equals_string_false() {
        assertEquals(TEST_12_30_40_987654321.equals("2007-07-15"), false);
    }

    public void test_equals_null_false() {
        assertEquals(TEST_12_30_40_987654321.equals(null), false);
    }

    //-----------------------------------------------------------------------
    // hashCode()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes")
    public void test_hashCode_same(int h, int m, int s, int n) {
        LocalTime a = LocalTime.of(h, m, s, n);
        LocalTime b = LocalTime.of(h, m, s, n);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test(dataProvider="sampleTimes")
    public void test_hashCode_hour_differs(int h, int m, int s, int n) {
        LocalTime a = LocalTime.of(h, m, s, n);
        LocalTime b = LocalTime.of(h + 1, m, s, n);
        assertEquals(a.hashCode() == b.hashCode(), false);
    }

    @Test(dataProvider="sampleTimes")
    public void test_hashCode_minute_differs(int h, int m, int s, int n) {
        LocalTime a = LocalTime.of(h, m, s, n);
        LocalTime b = LocalTime.of(h, m + 1, s, n);
        assertEquals(a.hashCode() == b.hashCode(), false);
    }

    @Test(dataProvider="sampleTimes")
    public void test_hashCode_second_differs(int h, int m, int s, int n) {
        LocalTime a = LocalTime.of(h, m, s, n);
        LocalTime b = LocalTime.of(h, m, s + 1, n);
        assertEquals(a.hashCode() == b.hashCode(), false);
    }

    @Test(dataProvider="sampleTimes")
    public void test_hashCode_nano_differs(int h, int m, int s, int n) {
        LocalTime a = LocalTime.of(h, m, s, n);
        LocalTime b = LocalTime.of(h, m, s, n + 1);
        assertEquals(a.hashCode() == b.hashCode(), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleToString")
    Object[][] provider_sampleToString() {
        return new Object[][] {
            {0, 0, 0, 0, "00:00"},
            {1, 0, 0, 0, "01:00"},
            {23, 0, 0, 0, "23:00"},
            {0, 1, 0, 0, "00:01"},
            {12, 30, 0, 0, "12:30"},
            {23, 59, 0, 0, "23:59"},
            {0, 0, 1, 0, "00:00:01"},
            {0, 0, 59, 0, "00:00:59"},
            {0, 0, 0, 100000000, "00:00:00.100"},
            {0, 0, 0, 10000000, "00:00:00.010"},
            {0, 0, 0, 1000000, "00:00:00.001"},
            {0, 0, 0, 100000, "00:00:00.000100"},
            {0, 0, 0, 10000, "00:00:00.000010"},
            {0, 0, 0, 1000, "00:00:00.000001"},
            {0, 0, 0, 100, "00:00:00.000000100"},
            {0, 0, 0, 10, "00:00:00.000000010"},
            {0, 0, 0, 1, "00:00:00.000000001"},
            {0, 0, 0, 999999999, "00:00:00.999999999"},
            {0, 0, 0, 99999999, "00:00:00.099999999"},
            {0, 0, 0, 9999999, "00:00:00.009999999"},
            {0, 0, 0, 999999, "00:00:00.000999999"},
            {0, 0, 0, 99999, "00:00:00.000099999"},
            {0, 0, 0, 9999, "00:00:00.000009999"},
            {0, 0, 0, 999, "00:00:00.000000999"},
            {0, 0, 0, 99, "00:00:00.000000099"},
            {0, 0, 0, 9, "00:00:00.000000009"},
        };
    }

    @Test(dataProvider="sampleToString")
    public void test_toString(int h, int m, int s, int n, String expected) {
        LocalTime t = LocalTime.of(h, m, s, n);
        String str = t.toString();
        assertEquals(str, expected);
    }

    //-----------------------------------------------------------------------
    // matchesCalendrical() - parameter is larger calendrical
    //-----------------------------------------------------------------------
    public void test_matchesCalendrical_true_date() {
        LocalTime test = TEST_12_30_40_987654321;
        OffsetTime cal = TEST_12_30_40_987654321.atOffset(ZoneOffset.UTC);
        assertEquals(test.matchesCalendrical(cal), true);
    }

    public void test_matchesCalendrical_false_date() {
        LocalTime test = TEST_12_30_40_987654321;
        OffsetTime cal = TEST_12_30_40_987654321.plusHours(1).atOffset(ZoneOffset.UTC);
        assertEquals(test.matchesCalendrical(cal), false);
    }

    public void test_matchesCalendrical_itself_true() {
        assertEquals(TEST_12_30_40_987654321.matchesCalendrical(TEST_12_30_40_987654321), true);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_matchesCalendrical_null() {
        TEST_12_30_40_987654321.matchesCalendrical(null);
    }

    //-----------------------------------------------------------------------
    // adjustTime()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes")
    public void test_adjustTime(int h, int m, int s, int n) {
        LocalTime a = LocalTime.of(h, m, s, n);
        assertSame(a.adjustTime(TEST_12_30_40_987654321), a);
        assertSame(TEST_12_30_40_987654321.adjustTime(a), TEST_12_30_40_987654321);
    }

    public void test_adjustTime_same() {
        assertSame(LocalTime.of(12, 30, 40, 987654321).adjustTime(TEST_12_30_40_987654321), TEST_12_30_40_987654321);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustTime_null() {
        TEST_12_30_40_987654321.adjustTime(null);
    }

    //-----------------------------------------------------------------------
    // Overflow.equals()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes")
    public void test_Overflow_equals_true(int h, int m, int s, int n) {
        Overflow a = LocalTime.of(h, m, s, n).plusWithOverflow(0, 0, 0, 999999999);
        Overflow b = LocalTime.of(h, m, s, n).plusWithOverflow(0, 0, 0, 999999999);
        assertEquals(a.equals(b), true);
        assertEquals(a.getResultTime().plusWithOverflow(0, 0, 0, 0).equals(a), true);
    }
    @Test(dataProvider="sampleTimes")
    public void test_Overflow_equals_false_days_differs(int h, int m, int s, int n) {
        Overflow a = LocalTime.of(h, m, s, n).plusWithOverflow(0, 0, 0, 0);
        Overflow b = LocalTime.of(h, m, s, n).plusWithOverflow(0, 0, 0, 1);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_Overflow_equals_false_hour_differs(int h, int m, int s, int n) {
        Overflow a = LocalTime.of(h, m, s, n).plusWithOverflow(0, 0, 0, 0);
        Overflow b = LocalTime.of(h + 1, m, s, n).plusWithOverflow(0, 0, 0, 0);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_Overflow_equals_false_minute_differs(int h, int m, int s, int n) {
        Overflow a = LocalTime.of(h, m, s, n).plusWithOverflow(0, 0, 0, 0);
        Overflow b = LocalTime.of(h, m + 1, s, n).plusWithOverflow(0, 0, 0, 0);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_Overflow_equals_false_second_differs(int h, int m, int s, int n) {
        Overflow a = LocalTime.of(h, m, s, n).plusWithOverflow(0, 0, 0, 0);
        Overflow b = LocalTime.of(h, m, s + 1, n).plusWithOverflow(0, 0, 0, 0);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_Overflow_equals_false_nano_differs(int h, int m, int s, int n) {
        Overflow a = LocalTime.of(h, m, s, n).plusWithOverflow(0, 0, 0, 0);
        Overflow b = LocalTime.of(h, m, s, n + 1).plusWithOverflow(0, 0, 0, 0);
        assertEquals(a.equals(b), false);
    }

    public void test_Overflow_equals_itself_true() {
        Overflow a = TEST_12_30_40_987654321.plusWithOverflow(0, 0, 0, 0);
        assertEquals(a.equals(a), true);
    }

    public void test_Overflow_equals_string_false() {
        assertEquals(TEST_12_30_40_987654321.plusWithOverflow(0, 0, 0, 0).equals("2007-07-15"), false);
    }

    public void test_Overflow_equals_null_false() {
        assertEquals(TEST_12_30_40_987654321.plusWithOverflow(0, 0, 0, 0).equals(null), false);
    }
    
    //-----------------------------------------------------------------------
    // hashCode()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes")
    public void test_Overflow_hashCode_same(int h, int m, int s, int n) {
        long days = (h + m + s + n) * 24 * 60 * 60 * 1000000000L;
        Overflow a = LocalTime.of(h, m, s, n).plusWithOverflow(0, 0, 0, days);
        Overflow b = LocalTime.of(h, m, s, n).plusWithOverflow(0, 0, 0, days);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test(dataProvider="sampleTimes")
    public void test_Overflow_hashCode_hour_differs(int h, int m, int s, int n) {
        long days = (h + m + s + n) * 24 * 60 * 60 * 1000000000L;
        Overflow a = LocalTime.of(h, m, s, n).plusWithOverflow(0, 0, 0, days);
        Overflow b = LocalTime.of(h + 1, m, s, n).plusWithOverflow(0, 0, 0, days);
        assertEquals(a.hashCode() == b.hashCode(), false);
    }

    @Test(dataProvider="sampleTimes")
    public void test_Overflow_hashCode_minute_differs(int h, int m, int s, int n) {
        long days = (h + m + s + n) * 24 * 60 * 60 * 1000000000L;
        Overflow a = LocalTime.of(h, m, s, n).plusWithOverflow(0, 0, 0, days);
        Overflow b = LocalTime.of(h, m + 1, s, n).plusWithOverflow(0, 0, 0, days);
        assertEquals(a.hashCode() == b.hashCode(), false);
    }

    @Test(dataProvider="sampleTimes")
    public void test_Overflow_hashCode_second_differs(int h, int m, int s, int n) {
        long days = (h + m + s + n) * 24 * 60 * 60 * 1000000000L;
        Overflow a = LocalTime.of(h, m, s, n).plusWithOverflow(0, 0, 0, days);
        Overflow b = LocalTime.of(h, m, s + 1, n).plusWithOverflow(0, 0, 0, days);
        assertEquals(a.hashCode() == b.hashCode(), false);
    }

    @Test(dataProvider="sampleTimes")
    public void test_Overflow_hashCode_nano_differs(int h, int m, int s, int n) {
        long days = (h + m + s + n) * 24 * 60 * 60 * 1000000000L;
        Overflow a = LocalTime.of(h, m, s, n).plusWithOverflow(0, 0, 0, days);
        Overflow b = LocalTime.of(h, m, s, n + 1).plusWithOverflow(0, 0, 0, days);
        assertEquals(a.hashCode() == b.hashCode(), false);
    }
}
