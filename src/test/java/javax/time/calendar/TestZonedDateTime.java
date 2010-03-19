/*
 * Copyright (c) 2008-2010, Stephen Colebourne & Michael Nascimento Santos
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

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.time.Instant;
import javax.time.InstantProvider;
import javax.time.TimeSource;
import javax.time.period.Period;
import javax.time.period.PeriodProvider;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test ZonedDateTime.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestZonedDateTime {

    private static final ZoneOffset OFFSET_0100 = ZoneOffset.hours(1);
    private static final ZoneOffset OFFSET_0200 = ZoneOffset.hours(2);
    private static final ZoneOffset OFFSET_0130 = ZoneOffset.of("+01:30");
    private static final TimeZone ZONE_0100 = TimeZone.of(OFFSET_0100);
    private static final TimeZone ZONE_0200 = TimeZone.of(OFFSET_0200);
    private static final TimeZone ZONE_PARIS = TimeZone.of("Europe/Paris");
    private ZonedDateTime TEST_DATE_TIME;

    @BeforeMethod
    public void setUp() {
        LocalDateTime dt = LocalDateTime.of(2008, 6, 30, 11, 30, 59, 500);
        TEST_DATE_TIME = ZonedDateTime.from(dt, ZONE_0100);
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Calendrical.class.isAssignableFrom(ZonedDateTime.class));
        assertTrue(Comparable.class.isAssignableFrom(ZonedDateTime.class));
        assertTrue(Serializable.class.isAssignableFrom(ZonedDateTime.class));
    }

    public void test_immutable() {
        Class<ZonedDateTime> cls = ZonedDateTime.class;
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            assertTrue(Modifier.isPrivate(field.getModifiers()));
            assertTrue(Modifier.isFinal(field.getModifiers()));
        }
    }

    //-----------------------------------------------------------------------
    // nowClock()
    //-----------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class)
    public void now_Clock_nullClock() {
        ZonedDateTime.now(null);
    }

    public void now_Clock_allSecsInDay_utc() {
        for (int i = 0; i < (2 * 24 * 60 * 60); i++) {
            Instant instant = Instant.seconds(i).plusNanos(123456789L);
            Clock clock = Clock.clock(TimeSource.fixed(instant), TimeZone.UTC);
            ZonedDateTime test = ZonedDateTime.now(clock);
            assertEquals(test.getYear(), 1970);
            assertEquals(test.getMonthOfYear(), MonthOfYear.JANUARY);
            assertEquals(test.getDayOfMonth(), (i < 24 * 60 * 60 ? 1 : 2));
            assertEquals(test.getHourOfDay(), (i / (60 * 60)) % 24);
            assertEquals(test.getMinuteOfHour(), (i / 60) % 60);
            assertEquals(test.getSecondOfMinute(), i % 60);
            assertEquals(test.getNanoOfSecond(), 123456789);
            assertEquals(test.getOffset(), ZoneOffset.UTC);
            assertEquals(test.getZone(), TimeZone.UTC);
        }
    }

    public void now_Clock_allSecsInDay_zone() {
        TimeZone zone = TimeZone.of("Europe/London");
        for (int i = 0; i < (2 * 24 * 60 * 60); i++) {
            Instant instant = Instant.seconds(i).plusNanos(123456789L);
            ZonedDateTime expected = ZonedDateTime.fromInstant(instant, zone);
            Clock clock = Clock.clock(TimeSource.fixed(expected.toInstant()), zone);
            ZonedDateTime test = ZonedDateTime.now(clock);
            assertEquals(test, expected);
        }
    }

    public void now_Clock_allSecsInDay_beforeEpoch() {
        LocalTime expected = LocalTime.MIDNIGHT.plusNanos(123456789L);
        for (int i =-1; i >= -(24 * 60 * 60); i--) {
            Instant instant = Instant.seconds(i).plusNanos(123456789L);
            Clock clock = Clock.clock(TimeSource.fixed(instant), TimeZone.UTC);
            ZonedDateTime test = ZonedDateTime.now(clock);
            assertEquals(test.getYear(), 1969);
            assertEquals(test.getMonthOfYear(), MonthOfYear.DECEMBER);
            assertEquals(test.getDayOfMonth(), 31);
            expected = expected.minusSeconds(1);
            assertEquals(test.toLocalTime(), expected);
            assertEquals(test.getOffset(), ZoneOffset.UTC);
            assertEquals(test.getZone(), TimeZone.UTC);
        }
    }

    public void now_Clock_offsets() {
        ZonedDateTime base = ZonedDateTime.of(OffsetDateTime.of(1970, 1, 1, 12, 0, ZoneOffset.UTC), TimeZone.UTC);
        for (int i = -9; i < 15; i++) {
            ZoneOffset offset = ZoneOffset.hours(i);
            Clock clock = Clock.clock(TimeSource.fixed(base.toInstant()), TimeZone.of(offset));
            ZonedDateTime test = ZonedDateTime.now(clock);
            assertEquals(test.getHourOfDay(), (12 + i) % 24);
            assertEquals(test.getMinuteOfHour(), 0);
            assertEquals(test.getSecondOfMinute(), 0);
            assertEquals(test.getNanoOfSecond(), 0);
            assertEquals(test.getOffset(), offset);
            assertEquals(test.getZone(), TimeZone.of(offset));
        }
    }

    //-----------------------------------------------------------------------
    // nowSystemClock()
    //-----------------------------------------------------------------------
    public void nowSystemClock() {
        ZonedDateTime expected = ZonedDateTime.now(Clock.systemDefaultZone());
        ZonedDateTime test = ZonedDateTime.nowSystemClock();
        long diff = Math.abs(test.toLocalTime().toNanoOfDay() - expected.toLocalTime().toNanoOfDay());
        if (diff >= 100000000) {
            // may be date change
            expected = ZonedDateTime.now(Clock.systemDefaultZone());
            test = ZonedDateTime.nowSystemClock();
            diff = Math.abs(test.toLocalTime().toNanoOfDay() - expected.toLocalTime().toNanoOfDay());
        }
        assertTrue(diff < 100000000);  // less than 0.1 secs
    }

//    //-----------------------------------------------------------------------
//    // dateMidnight factories
//    //-----------------------------------------------------------------------
//    public void factory_dateMidnight_YMD() {
//        ZonedDateTime test = ZonedDateTime.dateMidnight(Year.isoYear(2008), MonthOfYear.JUNE, DayOfMonth.dayOfMonth(30), ZONE_0100);
//        assertEquals(test.getYear(), Year.isoYear(2008));
//        assertEquals(test.getMonthOfYear(), MonthOfYear.JUNE);
//        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
//    }
//
//    //-----------------------------------------------------------------------
//    public void factory_dateMidnight_intMonthInt() {
//        ZonedDateTime test = ZonedDateTime.dateMidnight(2008, MonthOfYear.JUNE, 30, ZONE_0100);
//        assertEquals(test.getYear(), Year.isoYear(2008));
//        assertEquals(test.getMonthOfYear(), MonthOfYear.JUNE);
//        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
//    }
//
//    //-----------------------------------------------------------------------
//    public void factory_dateMidnight_ints() {
//        ZonedDateTime test = ZonedDateTime.dateMidnight(2008, 6, 30, ZONE_0100);
//        assertEquals(test.getYear(), Year.isoYear(2008));
//        assertEquals(test.getMonthOfYear(), MonthOfYear.JUNE);
//        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
//    }
//
//    //-----------------------------------------------------------------------
//    public void factory_dateMidnight_DateProvider() {
//        DateProvider provider = LocalDate.date(2008, 6, 30);
//        ZonedDateTime test = ZonedDateTime.dateMidnight(provider, ZONE_0100);
//        assertEquals(test.getYear(), Year.isoYear(2008));
//        assertEquals(test.getMonthOfYear(), MonthOfYear.JUNE);
//        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
//    }
//
    //-----------------------------------------------------------------------
    // dateTime factories
    //-----------------------------------------------------------------------
    void check(ZonedDateTime test, int y, int m, int d, int h, int min, int s, int n, ZoneOffset offset, TimeZone zone) {
        assertEquals(test.getYear(), y);
        assertEquals(test.getMonthOfYear().getValue(), m);
        assertEquals(test.getDayOfMonth(), d);
        assertEquals(test.getHourOfDay(), h);
        assertEquals(test.getMinuteOfHour(), min);
        assertEquals(test.getSecondOfMinute(), s);
        assertEquals(test.getNanoOfSecond(), n);
        assertEquals(test.getOffset(), offset);
        assertEquals(test.getZone(), zone);
    }

//    public void factory_dateTime_objectsHM() {
//        ZonedDateTime test = ZonedDateTime.dateTime(
//                Year.isoYear(2008), MonthOfYear.monthOfYear(6), DayOfMonth.dayOfMonth(30),
//                HourOfDay.hourOfDay(11), MinuteOfHour.minuteOfHour(30), ZONE_0100);
//        assertEquals(test.getYear(), Year.isoYear(2008));
//        assertEquals(test.getMonthOfYear(), MonthOfYear.monthOfYear(6));
//        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
//        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
//        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
//        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(0));
//        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(0));
//    }
//
//    //-----------------------------------------------------------------------
//    public void factory_dateTime_objectsHMS() {
//        ZonedDateTime test = ZonedDateTime.dateTime(
//                Year.isoYear(2008), MonthOfYear.monthOfYear(6), DayOfMonth.dayOfMonth(30),
//                HourOfDay.hourOfDay(11), MinuteOfHour.minuteOfHour(30), SecondOfMinute.secondOfMinute(10), ZONE_0100);
//        assertEquals(test.getYear(), Year.isoYear(2008));
//        assertEquals(test.getMonthOfYear(), MonthOfYear.monthOfYear(6));
//        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
//        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
//        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
//        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(10));
//        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(0));
//    }
//
//    //-----------------------------------------------------------------------
//    public void factory_dateTime_objectsHMSN() {
//        ZonedDateTime test = ZonedDateTime.dateTime(
//                Year.isoYear(2008), MonthOfYear.monthOfYear(6), DayOfMonth.dayOfMonth(30),
//                HourOfDay.hourOfDay(11), MinuteOfHour.minuteOfHour(30),
//                SecondOfMinute.secondOfMinute(10), NanoOfSecond.nanoOfSecond(500), ZONE_0100);
//        assertEquals(test.getYear(), Year.isoYear(2008));
//        assertEquals(test.getMonthOfYear(), MonthOfYear.monthOfYear(6));
//        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
//        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
//        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
//        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(10));
//        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(500));
//    }
//
//    //-----------------------------------------------------------------------
//    public void factory_dateTime_intMonthIntHM() {
//        ZonedDateTime test = ZonedDateTime.dateTime(2008, MonthOfYear.JUNE, 30, 11, 30, ZONE_0100);
//        assertEquals(test.getYear(), Year.isoYear(2008));
//        assertEquals(test.getMonthOfYear(), MonthOfYear.monthOfYear(6));
//        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
//        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
//        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
//        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(0));
//        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(0));
//    }
//
//    //-----------------------------------------------------------------------
//    public void factory_dateTime_intMonthIntHMS() {
//        ZonedDateTime test = ZonedDateTime.dateTime(2008, MonthOfYear.JUNE, 30, 11, 30, 10, ZONE_0100);
//        assertEquals(test.getYear(), Year.isoYear(2008));
//        assertEquals(test.getMonthOfYear(), MonthOfYear.monthOfYear(6));
//        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
//        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
//        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
//        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(10));
//        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(0));
//    }
//
//    //-----------------------------------------------------------------------
//    public void factory_dateTime_intMonthIntHMSN() {
//        ZonedDateTime test = ZonedDateTime.dateTime(2008, MonthOfYear.JUNE, 30, 11, 30, 10, 500, ZONE_0100);
//        assertEquals(test.getYear(), Year.isoYear(2008));
//        assertEquals(test.getMonthOfYear(), MonthOfYear.monthOfYear(6));
//        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
//        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
//        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
//        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(10));
//        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(500));
//    }
//
//    //-----------------------------------------------------------------------
//    public void factory_dateTime_intsHM() {
//        ZonedDateTime test = ZonedDateTime.dateTime(2008, 6, 30, 11, 30, ZONE_0100);
//        assertEquals(test.getYear(), Year.isoYear(2008));
//        assertEquals(test.getMonthOfYear(), MonthOfYear.monthOfYear(6));
//        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
//        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
//        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
//        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(0));
//        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(0));
//    }
//
//    //-----------------------------------------------------------------------
//    public void factory_dateTime_intsHMS() {
//        ZonedDateTime test = ZonedDateTime.dateTime(2008, 6, 30, 11, 30, 10, ZONE_0100);
//        assertEquals(test.getYear(), Year.isoYear(2008));
//        assertEquals(test.getMonthOfYear(), MonthOfYear.monthOfYear(6));
//        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
//        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
//        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
//        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(10));
//        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(0));
//    }
//
//    //-----------------------------------------------------------------------
//    public void factory_dateTime_intsHMSN() {
//        ZonedDateTime test = ZonedDateTime.dateTime(2008, 6, 30, 11, 30, 10, 500, ZONE_0100);
//        assertEquals(test.getYear(), Year.isoYear(2008));
//        assertEquals(test.getMonthOfYear(), MonthOfYear.monthOfYear(6));
//        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
//        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
//        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
//        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(10));
//        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(500));
//    }

    //-----------------------------------------------------------------------
    // from(DateProvider, TimeProvider, TimeZone)
    //-----------------------------------------------------------------------
    public void factory_from_DateProviderTimeProvider() {
        DateProvider dateProvider = LocalDate.of(2008, 6, 30);
        TimeProvider timeProvider = LocalTime.of(11, 30, 10, 500);
        ZonedDateTime test = ZonedDateTime.from(dateProvider, timeProvider, ZONE_PARIS);
        check(test, 2008, 6, 30, 11, 30, 10, 500, OFFSET_0200, ZONE_PARIS);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_from_DateProviderTimeProvider_nullDate() {
        TimeProvider timeProvider = LocalTime.of(11, 30, 10, 500);
        ZonedDateTime.from((DateProvider) null, timeProvider, ZONE_PARIS);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_from_DateProviderTimeProvider_nullTime() {
        DateProvider dateProvider = LocalDate.of(2008, 6, 30);
        ZonedDateTime.from(dateProvider, (TimeProvider) null, ZONE_PARIS);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_from_DateProviderTimeProvider_nullZone() {
        DateProvider dateProvider = LocalDate.of(2008, 6, 30);
        TimeProvider timeProvider = LocalTime.of(11, 30, 10, 500);
        ZonedDateTime.from(dateProvider, timeProvider, null);
    }

    //-----------------------------------------------------------------------
    // from(DateProvider, TimeProvider, TimeZone, ZoneResolver)
    //-----------------------------------------------------------------------
    public void factory_from_DateProviderTimeProviderResolver() {
        DateProvider dateProvider = LocalDate.of(2008, 6, 30);
        TimeProvider timeProvider = LocalTime.of(11, 30, 10, 500);
        ZonedDateTime test = ZonedDateTime.from(dateProvider, timeProvider, ZONE_PARIS, ZoneResolvers.postTransition());
        check(test, 2008, 6, 30, 11, 30, 10, 500, OFFSET_0200, ZONE_PARIS);
    }

    public void factory_from_DateProviderTimeProviderResolver_resolverUsed() {
        DateProvider dateProvider = LocalDate.of(2008, 3, 30);
        TimeProvider timeProvider = LocalTime.of(2, 30, 10, 500);
        ZonedDateTime test = ZonedDateTime.from(dateProvider, timeProvider, ZONE_PARIS, ZoneResolvers.postTransition());
        check(test, 2008, 3, 30, 3, 0, 0, 0, OFFSET_0200, ZONE_PARIS);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_from_DateProviderTimeProviderResolver_nullDate() {
        TimeProvider timeProvider = LocalTime.of(11, 30, 10, 500);
        ZonedDateTime.from((DateProvider) null, timeProvider, ZONE_PARIS, ZoneResolvers.strict());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_from_DateProviderTimeProviderResolver_nullTime() {
        DateProvider dateProvider = LocalDate.of(2008, 6, 30);
        ZonedDateTime.from(dateProvider, (TimeProvider) null, ZONE_PARIS, ZoneResolvers.strict());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_from_DateProviderTimeProviderResolver_nullZone() {
        DateProvider dateProvider = LocalDate.of(2008, 6, 30);
        TimeProvider timeProvider = LocalTime.of(11, 30, 10, 500);
        ZonedDateTime.from(dateProvider, timeProvider, null, ZoneResolvers.strict());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_from_DateProviderTimeProviderResolver_nullResolver() {
        DateProvider dateProvider = LocalDate.of(2008, 6, 30);
        TimeProvider timeProvider = LocalTime.of(11, 30, 10, 500);
        ZonedDateTime.from(dateProvider, timeProvider, ZONE_PARIS, (ZoneResolver) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_from_DateTimeProviderResolver_badDateProvider() {
        TimeProvider timeProvider = LocalTime.of(11, 30, 10, 500);
        ZonedDateTime.from(new MockDateProviderReturnsNull(), timeProvider, ZONE_PARIS, ZoneResolvers.strict());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_from_DateTimeProviderResolver_badTimeProvider() {
        DateProvider dateProvider = LocalDate.of(2008, 6, 30);
        ZonedDateTime.from(dateProvider, new MockTimeProviderReturnsNull(), ZONE_PARIS, ZoneResolvers.strict());
    }

    //-----------------------------------------------------------------------
    // from(DateTimeProvider, TimeZone)
    //-----------------------------------------------------------------------
    public void factory_from_DateTimeProvider() {
        DateTimeProvider provider = LocalDateTime.of(2008, 6, 30, 11, 30, 10, 500);
        ZonedDateTime test = ZonedDateTime.from(provider, ZONE_PARIS);
        check(test, 2008, 6, 30, 11, 30, 10, 500, OFFSET_0200, ZONE_PARIS);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_from_DateTimeProvider_nullDateTime() {
        ZonedDateTime.from((DateTimeProvider) null, ZONE_PARIS);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_from_DateTimeProvider_nullZone() {
        DateTimeProvider provider = LocalDateTime.of(2008, 6, 30, 11, 30, 10, 500);
        ZonedDateTime.from(provider, null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_from_DateTimeProvider_badProvider() {
        ZonedDateTime.from(new MockDateTimeProviderReturnsNull(), ZONE_PARIS);
    }

    //-----------------------------------------------------------------------
    // from(DateTimeProvider, TimeZone, ZoneResolver)
    //-----------------------------------------------------------------------
    public void factory_from_DateTimeProviderResolver() {
        DateTimeProvider provider = LocalDateTime.of(2008, 6, 30, 11, 30, 10, 500);
        ZonedDateTime test = ZonedDateTime.from(provider, ZONE_PARIS, ZoneResolvers.postTransition());
        check(test, 2008, 6, 30, 11, 30, 10, 500, OFFSET_0200, ZONE_PARIS);
    }

    public void factory_from_DateTimeProviderResolver_resolverUsed() {
        DateTimeProvider provider = LocalDateTime.of(2008, 3, 30, 2, 30, 10, 500);
        ZonedDateTime test = ZonedDateTime.from(provider, ZONE_PARIS, ZoneResolvers.postTransition());
        check(test, 2008, 3, 30, 3, 0, 0, 0, OFFSET_0200, ZONE_PARIS);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_from_DateTimeProviderResolver_nullDateTime() {
        ZonedDateTime.from((DateTimeProvider) null, ZONE_PARIS, ZoneResolvers.strict());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_from_DateTimeProviderResolver_nullZone() {
        DateTimeProvider provider = LocalDateTime.of(2008, 6, 30, 11, 30, 10, 500);
        ZonedDateTime.from(provider, null, ZoneResolvers.strict());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_from_DateTimeProviderResolver_nullResolver() {
        DateTimeProvider provider = LocalDateTime.of(2008, 6, 30, 11, 30, 10, 500);
        ZonedDateTime.from(provider, null, (ZoneResolver) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_from_DateTimeProviderResolver_badProvider() {
        ZonedDateTime.from(new MockDateTimeProviderReturnsNull(), ZONE_PARIS, ZoneResolvers.strict());
    }

    //-----------------------------------------------------------------------
    // of(OffsetDateTime, TimeZone)
    //-----------------------------------------------------------------------
    public void factory_of_OffsetDateTime() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 6, 30, 11, 30, 10, 500, OFFSET_0200);
        ZonedDateTime test = ZonedDateTime.of(odt, ZONE_PARIS);
        check(test, 2008, 6, 30, 11, 30, 10, 500, OFFSET_0200, ZONE_PARIS);
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void factory_of_OffsetDateTime_inGap() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 3, 30, 2, 30, OFFSET_0100);
        try {
            ZonedDateTime.of(odt, ZONE_PARIS);
        } catch (CalendarConversionException ex) {
            assertEquals(ex.getMessage().contains("daylight savings gap"), true);
            throw ex;
        }
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void factory_of_OffsetDateTime_inOverlap_invalidOfset() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 10, 26, 2, 30, OFFSET_0130);
        try {
            ZonedDateTime.of(odt, ZONE_PARIS);
        } catch (CalendarConversionException ex) {
            assertEquals(ex.getMessage().contains("invalid for time-zone"), true);
            throw ex;
        }
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void factory_of_OffsetDateTime_invalidOffset() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 6, 30, 11, 30, 10, 500, OFFSET_0130);
        try {
            ZonedDateTime.of(odt, ZONE_PARIS);
        } catch (CalendarConversionException ex) {
            assertEquals(ex.getMessage().contains("invalid for time-zone"), true);
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_of_OffsetDateTime_nullDateTime() {
        ZonedDateTime.of((OffsetDateTime) null, ZONE_0100);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_of_OffsetDateTime_nullZone() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 6, 30, 11, 30, 10, 500, OFFSET_0100);
        ZonedDateTime.of(odt, null);
    }

    //-----------------------------------------------------------------------
    public void factory_from_multiProvider_checkAmbiguous() {
        MockMultiProvider mmp = new MockMultiProvider(2008, 6, 30, 11, 30, 10, 500, OFFSET_0200);
        ZonedDateTime test = ZonedDateTime.from(mmp, ZONE_PARIS);
        check(test, 2008, 6, 30, 11, 30, 10, 500, OFFSET_0200, ZONE_PARIS);
    }

    //-----------------------------------------------------------------------
    public void factory_fromInstant_InstantProvider() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 6, 30, 11, 30, 10, 500, OFFSET_0200);
        InstantProvider provider = odt.toInstant();
        ZonedDateTime test = ZonedDateTime.fromInstant(provider, ZONE_PARIS);
        check(test, 2008, 6, 30, 11, 30, 10, 500, OFFSET_0200, ZONE_PARIS);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_fromInstant_InstantProvider_nullProvider() {
        ZonedDateTime.fromInstant((InstantProvider) null, ZONE_0100);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_fromInstant_InstantProvider_nullZone() {
        InstantProvider provider = Instant.seconds(0L);
        ZonedDateTime.fromInstant(provider, null);
    }

    //-----------------------------------------------------------------------
    public void factory_fromInstant_OffsetDateTime() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 6, 30, 11, 30, 10, 500, OFFSET_0200);
        ZonedDateTime test = ZonedDateTime.fromInstant(odt, ZONE_PARIS);
        check(test, 2008, 6, 30, 11, 30, 10, 500, OFFSET_0200, ZONE_PARIS);
    }

    public void factory_fromInstant_OffsetDateTime_inGap() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 3, 30, 2, 30, OFFSET_0100);  // DST gap
        ZonedDateTime test = ZonedDateTime.fromInstant(odt, ZONE_PARIS);
        check(test, 2008, 3, 30, 3, 30, 0, 0, OFFSET_0200, ZONE_PARIS);  // one hour later in summer offset
    }

    public void factory_fromInstant_OffsetDateTime_inOverlap_earlier() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 10, 26, 2, 30, OFFSET_0200);  // DST overlap
        ZonedDateTime test = ZonedDateTime.fromInstant(odt, ZONE_PARIS);
        check(test, 2008, 10, 26, 2, 30, 0, 0, OFFSET_0200, ZONE_PARIS);  // same time and offset
    }

    public void factory_fromInstant_OffsetDateTime_inOverlap_later() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 10, 26, 2, 30, OFFSET_0100);  // DST overlap
        ZonedDateTime test = ZonedDateTime.fromInstant(odt, ZONE_PARIS);
        check(test, 2008, 10, 26, 2, 30, 0, 0, OFFSET_0100, ZONE_PARIS);  // same time and offset
    }

    public void factory_fromInstant_OffsetDateTime_invalidOffset() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 6, 30, 11, 30, 10, 500, OFFSET_0130);
        ZonedDateTime test = ZonedDateTime.fromInstant(odt, ZONE_PARIS);
        check(test, 2008, 6, 30, 12, 0, 10, 500, OFFSET_0200, ZONE_PARIS);  // corrected offset, thus altered time
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_fromInstant_OffsetDateTime_nullDateTime() {
        ZonedDateTime.fromInstant((OffsetDateTime) null, ZONE_0100);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_fromInstant_OffsetDateTime_nullZone() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 6, 30, 11, 30, 10, 500, OFFSET_0100);
        ZonedDateTime.fromInstant(odt, null);
    }

    //-----------------------------------------------------------------------
    public void factory_fromInstant_multiProvider_checkAmbiguous() {
        MockMultiProvider mmp = new MockMultiProvider(2008, 6, 30, 11, 30, 10, 500, OFFSET_0200);
        ZonedDateTime test = ZonedDateTime.fromInstant(mmp, ZONE_PARIS);
        check(test, 2008, 6, 30, 11, 30, 10, 500, OFFSET_0200, ZONE_PARIS);
    }

    //-----------------------------------------------------------------------
    // parse()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleToString")
    public void test_parse(int y, int month, int d, int h, int m, int s, int n, String zoneId, String text) {
        ZonedDateTime t = ZonedDateTime.parse(text);
        assertEquals(t.getYear(), y);
        assertEquals(t.getMonthOfYear().getValue(), month);
        assertEquals(t.getDayOfMonth(), d);
        assertEquals(t.getHourOfDay(), h);
        assertEquals(t.getMinuteOfHour(), m);
        assertEquals(t.getSecondOfMinute(), s);
        assertEquals(t.getNanoOfSecond(), n);
        assertEquals(t.getZone().getID(), zoneId);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_parse_illegalValue() {
        ZonedDateTime.parse("2008-06-32T11:15+01:00[Europe/Paris]");
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void factory_parse_invalidValue() {
        ZonedDateTime.parse("2008-06-31T11:15+01:00[Europe/Paris]");
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_parse_nullText() {
        ZonedDateTime.parse((String) null);
    }

    //-----------------------------------------------------------------------
    // basics
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleTimes")
    Object[][] provider_sampleTimes() {
        return new Object[][] {
            {2008, 6, 30, 11, 30, 20, 500, ZONE_0100},
            {2008, 6, 30, 11, 0, 0, 0, ZONE_0100},
            {2008, 6, 30, 23, 59, 59, 999999999, ZONE_0100},
            {-1, 1, 1, 0, 0, 0, 0, ZONE_0100},
        };
    }

    @Test(dataProvider="sampleTimes")
    public void test_get(int y, int o, int d, int h, int m, int s, int n, TimeZone zone) {
        LocalDate localDate = LocalDate.of(y, o, d);
        LocalTime localTime = LocalTime.of(h, m, s, n);
        LocalDateTime localDateTime = LocalDateTime.from(localDate, localTime);
        ZoneOffset offset = zone.getRules().getOffsetInfo(localDateTime).getEstimatedOffset();
        ZonedDateTime a = ZonedDateTime.from(localDateTime, zone);
        assertSame(a.getOffset(), offset);
        assertSame(a.getZone(), zone);
        assertEquals(a.getChronology(), ISOChronology.INSTANCE);
        
        assertEquals(a.getYear(), localDate.getYear());
        assertEquals(a.getMonthOfYear(), localDate.getMonthOfYear());
        assertEquals(a.getDayOfMonth(), localDate.getDayOfMonth());
        assertEquals(a.getDayOfYear(), localDate.getDayOfYear());
        assertEquals(a.getDayOfWeek(), localDate.getDayOfWeek());
        
        assertEquals(a.getHourOfDay(), localDateTime.getHourOfDay());
        assertEquals(a.getMinuteOfHour(), localDateTime.getMinuteOfHour());
        assertEquals(a.getSecondOfMinute(), localDateTime.getSecondOfMinute());
        assertEquals(a.getNanoOfSecond(), localDateTime.getNanoOfSecond());
        
        assertSame(a.toLocalDate(), localDate);
        assertSame(a.toLocalTime(), localTime);
        assertSame(a.toLocalDateTime(), localDateTime);
        assertEquals(a.toOffsetDate(), OffsetDate.from(localDate, offset));
        assertEquals(a.toOffsetTime(), OffsetTime.from(localTime, offset));
        assertEquals(a.toOffsetDateTime(), OffsetDateTime.from(localDateTime, offset));
        assertEquals(a.toString(), a.toOffsetDateTime().toString() + "[" + zone.toString() + "]");
    }

    //-----------------------------------------------------------------------
    // get(CalendricalRule)
    //-----------------------------------------------------------------------
    public void test_get_CalendricalRule() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 12, 30, 40, 987654321);
        ZonedDateTime test = ZonedDateTime.from(ldt, ZONE_0100);
        assertEquals(test.get(ISOChronology.yearRule()), (Integer) 2008);
        assertEquals(test.get(ISOChronology.quarterOfYearRule()), QuarterOfYear.Q2);
        assertEquals(test.get(ISOChronology.monthOfYearRule()), MonthOfYear.JUNE);
        assertEquals(test.get(ISOChronology.monthOfQuarterRule()), (Integer) 3);
        assertEquals(test.get(ISOChronology.dayOfMonthRule()),  (Integer) 30);
        assertEquals(test.get(ISOChronology.dayOfWeekRule()), DayOfWeek.MONDAY);
        assertEquals(test.get(ISOChronology.dayOfYearRule()),  (Integer) 182);
        assertEquals(test.get(ISOChronology.weekOfWeekBasedYearRule()), (Integer) 27);
        assertEquals(test.get(ISOChronology.weekBasedYearRule()), (Integer) 2008);
        
        assertEquals(test.get(ISOChronology.hourOfDayRule()), (Integer) 12);
        assertEquals(test.get(ISOChronology.minuteOfHourRule()), (Integer) 30);
        assertEquals(test.get(ISOChronology.secondOfMinuteRule()), (Integer) 40);
        assertEquals(test.get(ISOChronology.nanoOfSecondRule()), (Integer) 987654321);
        assertEquals(test.get(ISOChronology.hourOfAmPmRule()), (Integer) 0);
        assertEquals(test.get(ISOChronology.amPmOfDayRule()), AmPmOfDay.PM);
        
        assertEquals(test.get(LocalDate.rule()), test.toLocalDate());
        assertEquals(test.get(LocalTime.rule()), test.toLocalTime());
        assertEquals(test.get(LocalDateTime.rule()), test.toLocalDateTime());
        assertEquals(test.get(OffsetDate.rule()), test.toOffsetDate());
        assertEquals(test.get(OffsetTime.rule()), test.toOffsetTime());
        assertEquals(test.get(OffsetDateTime.rule()), test.toOffsetDateTime());
        assertEquals(test.get(ZonedDateTime.rule()), test);
        assertEquals(test.get(ZoneOffset.rule()), test.getOffset());
        assertEquals(test.get(TimeZone.rule()), test.getZone());
        assertEquals(test.get(YearMonth.rule()), YearMonth.of(2008, 6));
        assertEquals(test.get(MonthDay.rule()), MonthDay.of(6, 30));
    }

    public void test_get_CalendricalRule_unsupported() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime test = ZonedDateTime.from(ldt, ZONE_0100);
        assertEquals(test.get(MockRuleNoValue.INSTANCE), null);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_get_CalendricalRule_null() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime test = ZonedDateTime.from(ldt, ZONE_0100);
        test.get((CalendricalRule<?>) null);
    }

    //-----------------------------------------------------------------------
    // withDateTime()
    //-----------------------------------------------------------------------
    public void test_withDateTime() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        LocalDateTime dt = LocalDateTime.of(2008, 6, 30, 11, 31, 0);
        ZonedDateTime test = base.withDateTime(dt);
        assertEquals(test.toLocalDateTime(), dt);
        assertSame(test.getZone(), base.getZone());
    }

    public void test_withDateTime_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        LocalDateTime dt = LocalDateTime.of(2008, 6, 30, 23, 30, 59);
        ZonedDateTime test = base.withDateTime(dt);
        assertSame(test, base);
    }

    public void test_withDateTime_retainOffsetResolver1() {
        TimeZone newYork = TimeZone.of("America/New_York");
        LocalDateTime ldt = LocalDateTime.of(2008, 11, 2, 1, 30);
        ZonedDateTime base = ZonedDateTime.from(ldt, newYork, ZoneResolvers.preTransition());
        assertEquals(base.getOffset(), ZoneOffset.hours(-4));
        ZonedDateTime test = base.withDateTime(LocalDateTime.of(2008, 11, 2, 1, 25));
        assertEquals(test.getOffset(), ZoneOffset.hours(-4));
    }

    public void test_withDateTime_retainOffsetResolver2() {
        TimeZone newYork = TimeZone.of("America/New_York");
        LocalDateTime ldt = LocalDateTime.of(2008, 11, 2, 1, 30);
        ZonedDateTime base = ZonedDateTime.from(ldt, newYork, ZoneResolvers.postTransition());
        assertEquals(base.getOffset(), ZoneOffset.hours(-5));
        ZonedDateTime test = base.withDateTime(LocalDateTime.of(2008, 11, 2, 1, 25));
        assertEquals(test.getOffset(), ZoneOffset.hours(-5));
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_withDateTime_null() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        base.withDateTime(null);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_withDateTime_badProvider() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        base.withDateTime(new MockDateTimeProviderReturnsNull());
    }

    //-----------------------------------------------------------------------
    // withEarlierOffsetAtOverlap()
    //-----------------------------------------------------------------------
    public void test_withEarlierOffsetAtOverlap() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 10, 26, 2, 30, OFFSET_0100);
        ZonedDateTime base = ZonedDateTime.of(odt, ZONE_PARIS);
        ZonedDateTime test = base.withEarlierOffsetAtOverlap();
        assertEquals(test.getOffset(), OFFSET_0200);  // offset changed to earlier
        assertSame(test.toLocalDateTime(), base.toLocalDateTime());
        assertSame(test.getZone(), base.getZone());
    }

    public void test_withEarlierOffsetAtOverlap_alreadyEarlier() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 10, 26, 2, 30, OFFSET_0200);
        ZonedDateTime base = ZonedDateTime.of(odt, ZONE_PARIS);
        ZonedDateTime test = base.withEarlierOffsetAtOverlap();
        assertSame(test, base);
    }

    public void test_withEarlierOffsetAtOverlap_notAtOverlap() {
        LocalDateTime ldt = LocalDateTime.of(2008, 10, 26, 1, 59);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_PARIS);
        ZonedDateTime test = base.withEarlierOffsetAtOverlap();
        assertSame(test, base);
    }

    public void test_withEarlierOffsetAtOverlap_fixedZone() {
        LocalDateTime ldt = LocalDateTime.of(2008, 10, 26, 2, 30);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.withEarlierOffsetAtOverlap();
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withLaterOffsetAtOverlap()
    //-----------------------------------------------------------------------
    public void test_withLaterOffsetAtOverlap() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 10, 26, 2, 30, OFFSET_0200);
        ZonedDateTime base = ZonedDateTime.of(odt, ZONE_PARIS);
        ZonedDateTime test = base.withLaterOffsetAtOverlap();
        assertEquals(test.getOffset(), OFFSET_0100);  // offset changed to later
        assertSame(test.toLocalDateTime(), base.toLocalDateTime());
        assertSame(test.getZone(), base.getZone());
    }

    public void test_withLaterOffsetAtOverlap_alreadyEarlier() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 10, 26, 2, 30, OFFSET_0100);
        ZonedDateTime base = ZonedDateTime.of(odt, ZONE_PARIS);
        ZonedDateTime test = base.withLaterOffsetAtOverlap();
        assertSame(test, base);
    }

    public void test_withLaterOffsetAtOverlap_notAtOverlap() {
        LocalDateTime ldt = LocalDateTime.of(2008, 10, 26, 1, 59);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_PARIS);
        ZonedDateTime test = base.withLaterOffsetAtOverlap();
        assertSame(test, base);
    }

    public void test_withLaterOffsetAtOverlap_fixedZone() {
        LocalDateTime ldt = LocalDateTime.of(2008, 10, 26, 2, 30);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.withLaterOffsetAtOverlap();
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withZoneSameLocal()
    //-----------------------------------------------------------------------
    public void test_withZoneSameLocal() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.withZoneSameLocal(ZONE_0200);
        assertEquals(test.toLocalTime(), base.toLocalTime());
        assertSame(test.getZone(), ZONE_0200);
    }

    public void test_withZoneSameLocal_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.withZoneSameLocal(ZONE_0100);
        assertSame(test, base);
    }

    public void test_withZoneSameLocal_retainOffsetResolver1() {
        LocalDateTime ldt = LocalDateTime.of(2008, 11, 2, 1, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, TimeZone.of("UTC-04:00") );
        ZonedDateTime test = base.withZoneSameLocal(TimeZone.of("America/New_York"));
        assertEquals(base.getOffset(), ZoneOffset.hours(-4));
        assertEquals(test.getOffset(), ZoneOffset.hours(-4));
    }

    public void test_withZoneSameLocal_retainOffsetResolver2() {
        LocalDateTime ldt = LocalDateTime.of(2008, 11, 2, 1, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, TimeZone.of("UTC-05:00") );
        ZonedDateTime test = base.withZoneSameLocal(TimeZone.of("America/New_York"));
        assertEquals(base.getOffset(), ZoneOffset.hours(-5));
        assertEquals(test.getOffset(), ZoneOffset.hours(-5));
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_withZoneSameLocal_null() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        base.withZoneSameLocal(null);
    }

    //-----------------------------------------------------------------------
    // withZoneSameLocal(TimeZone,ZoneResolver)
    //-----------------------------------------------------------------------
    public void test_withZoneSameLocal_ZoneResolver() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.withZoneSameLocal(ZONE_0200, ZoneResolvers.retainOffset());
        assertEquals(test.toLocalTime(), base.toLocalTime());
        assertSame(test.getZone(), ZONE_0200);
    }

    public void test_withZoneSameLocal_ZoneResolver_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.withZoneSameLocal(ZONE_0100, ZoneResolvers.retainOffset());
        assertSame(test, base);
    }

    public void test_withZoneSameLocal_ZoneResolver_retainOffsetResolver1() {
        LocalDateTime ldt = LocalDateTime.of(2008, 11, 2, 1, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, TimeZone.of("UTC-04:00") );
        ZonedDateTime test = base.withZoneSameLocal(TimeZone.of("America/New_York"), ZoneResolvers.retainOffset());
        assertEquals(base.getOffset(), ZoneOffset.hours(-4));
        assertEquals(test.getOffset(), ZoneOffset.hours(-4));
    }

    public void test_withZoneSameLocal_ZoneResolver_retainOffsetResolver2() {
        LocalDateTime ldt = LocalDateTime.of(2008, 11, 2, 1, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, TimeZone.of("UTC-05:00") );
        ZonedDateTime test = base.withZoneSameLocal(TimeZone.of("America/New_York"), ZoneResolvers.retainOffset());
        assertEquals(base.getOffset(), ZoneOffset.hours(-5));
        assertEquals(test.getOffset(), ZoneOffset.hours(-5));
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_withZoneSameLocal_ZoneResolver_nullZone() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        base.withZoneSameLocal(null, ZoneResolvers.retainOffset());
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_withZoneSameLocal_ZoneResolver_nullResolver() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        base.withZoneSameLocal(ZONE_0100, (ZoneResolver) null);
    }

    //-----------------------------------------------------------------------
    // withZoneSameInstant()
    //-----------------------------------------------------------------------
    public void test_withZoneSameInstant() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.withZoneSameInstant(ZONE_0200);
        ZonedDateTime expected = ZonedDateTime.from(ldt.plusHours(1), ZONE_0200);
        assertEquals(test, expected);
    }

    public void test_withZoneSameInstant_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.withZoneSameInstant(ZONE_0100);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_withZoneSameInstant_null() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        base.withZoneSameInstant(null);
    }

    //-----------------------------------------------------------------------
    // with(DateAdjuster)
    //-----------------------------------------------------------------------
    public void test_with_DateAdjuster() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.with(Year.of(2007));
        assertEquals(test, ZonedDateTime.from(ldt.withYear(2007), ZONE_0100));
    }

    public void test_with_DateAdjuster_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        DateAdjuster adjuster = LocalDate.of(2008, 6, 30);
        ZonedDateTime test = base.with(adjuster);
        assertSame(test, base);
    }

    public void test_with_DateAdjuster_retainOffsetResolver1() {
        TimeZone newYork = TimeZone.of("America/New_York");
        LocalDateTime ldt = LocalDateTime.of(2008, 11, 1, 1, 30);
        ZonedDateTime base = ZonedDateTime.from(ldt, newYork);
        assertEquals(base.getOffset(), ZoneOffset.hours(-4));
        ZonedDateTime test = base.with(LocalDate.of(2008, 11, 2));
        assertEquals(test.getOffset(), ZoneOffset.hours(-4));
    }

    public void test_with_DateAdjuster_retainOffsetResolver2() {
        TimeZone newYork = TimeZone.of("America/New_York");
        LocalDateTime ldt = LocalDateTime.of(2008, 11, 3, 1, 30);
        ZonedDateTime base = ZonedDateTime.from(ldt, newYork);
        assertEquals(base.getOffset(), ZoneOffset.hours(-5));
        ZonedDateTime test = base.with(LocalDate.of(2008, 11, 2));
        assertEquals(test.getOffset(), ZoneOffset.hours(-5));
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_with_DateAdjuster_null() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        base.with((DateAdjuster) null);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_with_DateAdjuster_badAdjuster() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        base.with(new MockDateAdjusterReturnsNull());
    }

    //-----------------------------------------------------------------------
    // with(TimeAdjuster)
    //-----------------------------------------------------------------------
    public void test_with_TimeAdjuster() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.with(new TimeAdjuster() {
            public LocalTime adjustTime(LocalTime time) {
                return time.withHourOfDay(1);
            }
        });
        assertEquals(test, ZonedDateTime.from(ldt.withHourOfDay(1), ZONE_0100));
    }

    public void test_with_TimeAdjuster_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        TimeAdjuster adjuster = LocalTime.of(23, 30, 59, 0);
        ZonedDateTime test = base.with(adjuster);
        assertSame(test, base);
    }

    public void test_with_TimeAdjuster_retainOffsetResolver1() {
        TimeZone newYork = TimeZone.of("America/New_York");
        LocalDateTime ldt = LocalDateTime.of(2008, 11, 2, 1, 30);
        ZonedDateTime base = ZonedDateTime.from(ldt, newYork, ZoneResolvers.preTransition());
        assertEquals(base.getOffset(), ZoneOffset.hours(-4));
        ZonedDateTime test = base.with(LocalTime.of(1, 25));
        assertEquals(test.getOffset(), ZoneOffset.hours(-4));
    }

    public void test_with_TimeAdjuster_retainOffsetResolver2() {
        TimeZone newYork = TimeZone.of("America/New_York");
        LocalDateTime ldt = LocalDateTime.of(2008, 11, 2, 1, 30);
        ZonedDateTime base = ZonedDateTime.from(ldt, newYork, ZoneResolvers.postTransition());
        assertEquals(base.getOffset(), ZoneOffset.hours(-5));
        ZonedDateTime test = base.with(LocalTime.of(1, 25));
        assertEquals(test.getOffset(), ZoneOffset.hours(-5));
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_with_TimeAdjuster_null() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        base.with((TimeAdjuster) null);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_with_TimeAdjuster_badAdjuster() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        base.with(new MockTimeAdjusterReturnsNull());
    }

    //-----------------------------------------------------------------------
    // withYear()
    //-----------------------------------------------------------------------
    public void test_withYear_normal() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.withYear(2007);
        assertEquals(test, ZonedDateTime.from(ldt.withYear(2007), ZONE_0100));
    }

    public void test_withYear_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.withYear(2008);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withMonthOfYear()
    //-----------------------------------------------------------------------
    public void test_withMonthOfYearr_normal() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.withMonthOfYear(1);
        assertEquals(test, ZonedDateTime.from(ldt.withMonthOfYear(1), ZONE_0100));
    }

    public void test_withMonthOfYear_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.withMonthOfYear(6);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withDayOfMonth()
    //-----------------------------------------------------------------------
    public void test_withDayOfMonth_normal() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.withDayOfMonth(15);
        assertEquals(test, ZonedDateTime.from(ldt.withDayOfMonth(15), ZONE_0100));
    }

    public void test_withDayOfMonth_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.withDayOfMonth(30);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withDayOfYear()
    //-----------------------------------------------------------------------
    public void test_withDayOfYear_normal() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.withDayOfYear(33);
        assertEquals(test, ZonedDateTime.from(ldt.withDayOfYear(33), ZONE_0100));
    }

    public void test_withDayOfYear_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.withDayOfYear(31 + 29 + 31 + 30 + 31 + 30);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withDate()
    //-----------------------------------------------------------------------
    public void test_withDate() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.withDate(2007, 1, 1);
        ZonedDateTime expected = ZonedDateTime.from(ldt.withDate(2007, 1, 1), ZONE_0100);
        assertEquals(test, expected);
    }

    public void test_withDate_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.withDate(2008, 6, 30);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withHourOfDay()
    //-----------------------------------------------------------------------
    public void test_withHourOfDayr_normal() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.withHourOfDay(15);
        assertEquals(test, ZonedDateTime.from(ldt.withHourOfDay(15), ZONE_0100));
    }

    public void test_withHourOfDay_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.withHourOfDay(23);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withMinuteOfHour()
    //-----------------------------------------------------------------------
    public void test_withMinuteOfHour_normal() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.withMinuteOfHour(15);
        assertEquals(test, ZonedDateTime.from(ldt.withMinuteOfHour(15), ZONE_0100));
    }

    public void test_withMinuteOfHour_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.withMinuteOfHour(30);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withSecondOfMinute()
    //-----------------------------------------------------------------------
    public void test_withSecondOfMinute_normal() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.withSecondOfMinute(15);
        assertEquals(test, ZonedDateTime.from(ldt.withSecondOfMinute(15), ZONE_0100));
    }

    public void test_withSecondOfMinute_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.withSecondOfMinute(59);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withNanoOfSecond()
    //-----------------------------------------------------------------------
    public void test_withNanoOfSecond_normal() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 1);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.withNanoOfSecond(15);
        assertEquals(test, ZonedDateTime.from(ldt.withNanoOfSecond(15), ZONE_0100));
    }

    public void test_withNanoOfSecond_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 1);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.withNanoOfSecond(1);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withTime()
    //-----------------------------------------------------------------------
    public void test_withTime_HM() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 0, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.withTime(12, 10);
        ZonedDateTime expected = ZonedDateTime.from(ldt.withTime(12, 10), ZONE_0100);
        assertEquals(test, expected);
    }

    public void test_withTime_HM_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 0, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.withTime(23, 30);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    public void test_withTime_HMS() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.withTime(12, 10, 9);
        ZonedDateTime expected = ZonedDateTime.from(ldt.withTime(12, 10, 9), ZONE_0100);
        assertEquals(test, expected);
    }

    public void test_withTime_HMS_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.withTime(23, 30, 59);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    public void test_withTime_HMSN() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.withTime(12, 10, 9, 8);
        ZonedDateTime expected = ZonedDateTime.from(ldt.withTime(12, 10, 9, 8), ZONE_0100);
        assertEquals(test, expected);
    }

    public void test_withTime_HMSN_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 500);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.withTime(23, 30, 59, 500);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plus(PeriodProvider)
    //-----------------------------------------------------------------------
    public void test_plus_PeriodProvider() {
        PeriodProvider provider = Period.of(1, 2, 3, 4, 5, 6, 7);
        ZonedDateTime t = ZonedDateTime.from(LocalDateTime.of(2008, 6, 1, 12, 30, 59, 500), ZONE_0100);
        ZonedDateTime expected = ZonedDateTime.from(LocalDateTime.of(2009, 8, 4, 16, 36, 5, 507), ZONE_0100);
        assertEquals(t.plus(provider), expected);
    }

    public void test_plus_PeriodProvider_zero() {
        ZonedDateTime t = TEST_DATE_TIME.plus(Period.ZERO);
        assertSame(t, TEST_DATE_TIME);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_plus_PeriodProvider_null() {
        TEST_DATE_TIME.plus((PeriodProvider) null);
    }

    //-----------------------------------------------------------------------
    // plusYears()
    //-----------------------------------------------------------------------
    public void test_plusYears() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.plusYears(1);
        assertEquals(test, ZonedDateTime.from(ldt.plusYears(1), ZONE_0100));
    }

    public void test_plusYears_zero() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.plusYears(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusMonths()
    //-----------------------------------------------------------------------
    public void test_plusMonths() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.plusMonths(1);
        assertEquals(test, ZonedDateTime.from(ldt.plusMonths(1), ZONE_0100));
    }

    public void test_plusMonths_zero() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.plusMonths(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusWeeks()
    //-----------------------------------------------------------------------
    public void test_plusWeeks() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.plusWeeks(1);
        assertEquals(test, ZonedDateTime.from(ldt.plusWeeks(1), ZONE_0100));
    }

    public void test_plusWeeks_zero() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.plusWeeks(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusDays()
    //-----------------------------------------------------------------------
    public void test_plusDays() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.plusDays(1);
        assertEquals(test, ZonedDateTime.from(ldt.plusDays(1), ZONE_0100));
    }

    public void test_plusDays_zero() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.plusDays(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusHours()
    //-----------------------------------------------------------------------
    public void test_plusHours() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.plusHours(13);
        assertEquals(test, ZonedDateTime.from(ldt.plusHours(13), ZONE_0100));
    }

    public void test_plusHours_zero() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.plusHours(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusMinutes()
    //-----------------------------------------------------------------------
    public void test_plusMinutes() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.plusMinutes(30);
        assertEquals(test, ZonedDateTime.from(ldt.plusMinutes(30), ZONE_0100));
    }

    public void test_plusMinutes_zero() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.plusMinutes(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusSeconds()
    //-----------------------------------------------------------------------
    public void test_plusSeconds() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.plusSeconds(1);
        assertEquals(test, ZonedDateTime.from(ldt.plusSeconds(1), ZONE_0100));
    }

    public void test_plusSeconds_zero() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.plusSeconds(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusNanos()
    //-----------------------------------------------------------------------
    public void test_plusNanos() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.plusNanos(1);
        assertEquals(test, ZonedDateTime.from(ldt.plusNanos(1), ZONE_0100));
    }

    public void test_plusNanos_zero() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.plusNanos(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusDuration(PeriodProvider)
    //-----------------------------------------------------------------------
    public void test_plusDuration_PeriodProvider() {
        PeriodProvider provider = Period.hoursMinutesSeconds(4, 5, 6);
        ZonedDateTime t = ZonedDateTime.from(LocalDateTime.of(2008, 6, 1, 12, 30, 59, 500), ZONE_0100);
        ZonedDateTime expected = ZonedDateTime.from(LocalDateTime.of(2008, 6, 1, 16, 36, 5, 500), ZONE_0100);
        assertEquals(t.plusDuration(provider), expected);
    }

    public void test_plusDuration_PeriodProvider_zero() {
        ZonedDateTime t = TEST_DATE_TIME.plusDuration(Period.ZERO);
        assertSame(t, TEST_DATE_TIME);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_plusDuration_PeriodProvider_null() {
        TEST_DATE_TIME.plusDuration((PeriodProvider) null);
    }

    //-----------------------------------------------------------------------
    // plusDuration(int,int,int,long)
    //-----------------------------------------------------------------------
    public void test_plusDuration_intintintlong() {
        ZonedDateTime t = ZonedDateTime.from(LocalDateTime.of(2008, 6, 1, 12, 30, 59, 500), ZONE_0100);
        ZonedDateTime expected = ZonedDateTime.from(LocalDateTime.of(2008, 6, 1, 16, 36, 5, 507), ZONE_0100);
        assertEquals(t.plusDuration(4, 5, 6, 7), expected);
    }

    public void test_plusDuration_intintintlong_zero() {
        ZonedDateTime t = TEST_DATE_TIME.plusDuration(0, 0, 0, 0);
        assertSame(t, TEST_DATE_TIME);
    }

    //-----------------------------------------------------------------------
    // minus(PeriodProvider)
    //-----------------------------------------------------------------------
    public void test_minus_PeriodProvider() {
        PeriodProvider provider = Period.of(1, 2, 3, 4, 5, 6, 7);
        ZonedDateTime t = ZonedDateTime.from(LocalDateTime.of(2008, 6, 1, 12, 30, 59, 500), ZONE_0100);
        ZonedDateTime expected = ZonedDateTime.from(LocalDateTime.of(2007, 3, 29, 8, 25, 53, 493), ZONE_0100);
        assertEquals(t.minus(provider), expected);
    }

    public void test_minus_PeriodProvider_zero() {
        ZonedDateTime t = TEST_DATE_TIME.minus(Period.ZERO);
        assertSame(t, TEST_DATE_TIME);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_minus_PeriodProvider_null() {
        TEST_DATE_TIME.minus((PeriodProvider) null);
    }

    //-----------------------------------------------------------------------
    // minusYears()
    //-----------------------------------------------------------------------
    public void test_minusYears() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.minusYears(1);
        assertEquals(test, ZonedDateTime.from(ldt.minusYears(1), ZONE_0100));
    }

    public void test_minusYears_zero() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.minusYears(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusMonths()
    //-----------------------------------------------------------------------
    public void test_minusMonths() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.minusMonths(1);
        assertEquals(test, ZonedDateTime.from(ldt.minusMonths(1), ZONE_0100));
    }

    public void test_minusMonths_zero() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.minusMonths(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusWeeks()
    //-----------------------------------------------------------------------
    public void test_minusWeeks() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.minusWeeks(1);
        assertEquals(test, ZonedDateTime.from(ldt.minusWeeks(1), ZONE_0100));
    }

    public void test_minusWeeks_zero() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.minusWeeks(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusDays()
    //-----------------------------------------------------------------------
    public void test_minusDays() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.minusDays(1);
        assertEquals(test, ZonedDateTime.from(ldt.minusDays(1), ZONE_0100));
    }

    public void test_minusDays_zero() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.minusDays(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusHours()
    //-----------------------------------------------------------------------
    public void test_minusHours() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.minusHours(13);
        assertEquals(test, ZonedDateTime.from(ldt.minusHours(13), ZONE_0100));
    }

    public void test_minusHours_zero() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.minusHours(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusMinutes()
    //-----------------------------------------------------------------------
    public void test_minusMinutes() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.minusMinutes(30);
        assertEquals(test, ZonedDateTime.from(ldt.minusMinutes(30), ZONE_0100));
    }

    public void test_minusMinutes_zero() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.minusMinutes(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusSeconds()
    //-----------------------------------------------------------------------
    public void test_minusSeconds() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.minusSeconds(1);
        assertEquals(test, ZonedDateTime.from(ldt.minusSeconds(1), ZONE_0100));
    }

    public void test_minusSeconds_zero() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.minusSeconds(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusNanos()
    //-----------------------------------------------------------------------
    public void test_minusNanos() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.minusNanos(1);
        assertEquals(test, ZonedDateTime.from(ldt.minusNanos(1), ZONE_0100));
    }

    public void test_minusNanos_zero() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        ZonedDateTime test = base.minusNanos(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusDuration(PeriodProvider)
    //-----------------------------------------------------------------------
    public void test_minusDuration_PeriodProvider() {
        PeriodProvider provider = Period.hoursMinutesSeconds(4, 5, 6);
        ZonedDateTime t = ZonedDateTime.from(LocalDateTime.of(2008, 6, 1, 12, 30, 59, 500), ZONE_0100);
        ZonedDateTime expected = ZonedDateTime.from(LocalDateTime.of(2008, 6, 1, 8, 25, 53, 500), ZONE_0100);
        assertEquals(t.minusDuration(provider), expected);
    }

    public void test_minusDuration_PeriodProvider_zero() {
        ZonedDateTime t = TEST_DATE_TIME.minusDuration(Period.ZERO);
        assertSame(t, TEST_DATE_TIME);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_minusDuration_PeriodProvider_null() {
        TEST_DATE_TIME.minusDuration((PeriodProvider) null);
    }

    //-----------------------------------------------------------------------
    // minusDuration(int,int,int,long)
    //-----------------------------------------------------------------------
    public void test_minusDuration_intintintlong() {
        ZonedDateTime t = ZonedDateTime.from(LocalDateTime.of(2008, 6, 1, 12, 30, 59, 500), ZONE_0100);
        ZonedDateTime expected = ZonedDateTime.from(LocalDateTime.of(2008, 6, 1, 8, 25, 53, 493), ZONE_0100);
        assertEquals(t.minusDuration(4, 5, 6, 7), expected);
    }

    public void test_minusDuration_intintintlong_zero() {
        ZonedDateTime t = TEST_DATE_TIME.minusDuration(0, 0, 0, 0);
        assertSame(t, TEST_DATE_TIME);
    }

    //-----------------------------------------------------------------------
    // matches()
    //-----------------------------------------------------------------------
    public void test_matches() {
        assertTrue(TEST_DATE_TIME.matches(new CalendricalMatcher() {
            public boolean matchesCalendrical(Calendrical calendrical) {
                return true;
            }
        }));
        assertFalse(TEST_DATE_TIME.matches(new CalendricalMatcher() {
            public boolean matchesCalendrical(Calendrical calendrical) {
                return false;
            }
        }));
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_matches_null() {
        TEST_DATE_TIME.matches(null);
    }

    //-----------------------------------------------------------------------
    // matchesCalendrical()
    //-----------------------------------------------------------------------
    public void test_matchesCalendrical_true() {
        ZonedDateTime test = ZonedDateTime.from(LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0), ZONE_0100);
        ZonedDateTime cal = ZonedDateTime.from(test, ZONE_0100);
        assertEquals(test.matchesCalendrical(cal), true);
    }

    public void test_matchesCalendrical_false() {
        ZonedDateTime test = ZonedDateTime.from(LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0), ZONE_0100);
        ZonedDateTime cal = ZonedDateTime.from(test.plusHours(1), ZONE_0100);
        assertEquals(test.matchesCalendrical(cal), false);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_matchesCalendrical_null() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.from(ldt, ZONE_0100);
        base.matchesCalendrical((Calendrical) null);
    }

    //-----------------------------------------------------------------------
    // toEpochSeconds()
    //-----------------------------------------------------------------------
    public void test_toEpochSeconds_afterEpoch() {
        for (int i = 0; i < 100000; i++) {
            OffsetDateTime a = OffsetDateTime.of(1970, 1, 1, 0, 0, ZoneOffset.UTC).plusSeconds(i);
            assertEquals(a.toEpochSeconds(), i);
        }
    }

    public void test_toEpochSeconds_beforeEpoch() {
        for (int i = 0; i < 100000; i++) {
            OffsetDateTime a = OffsetDateTime.of(1970, 1, 1, 0, 0, ZoneOffset.UTC).minusSeconds(i);
            assertEquals(a.toEpochSeconds(), -i);
        }
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo_time() {
        ZonedDateTime a = ZonedDateTime.from(LocalDateTime.of(2008, 6, 30, 11, 29), ZONE_0100);
        ZonedDateTime b = ZonedDateTime.from(LocalDateTime.of(2008, 6, 30, 11, 30), ZONE_0100);  // a is before b due to time
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    public void test_compareTo_offset() {
        ZonedDateTime a = ZonedDateTime.from(LocalDateTime.of(2008, 6, 30, 11, 30), ZONE_0200);
        ZonedDateTime b = ZonedDateTime.from(LocalDateTime.of(2008, 6, 30, 11, 30), ZONE_0100);  // a is before b due to offset
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    public void test_compareTo_both() {
        ZonedDateTime a = ZonedDateTime.from(LocalDateTime.of(2008, 6, 30, 11, 50), ZONE_0200);
        ZonedDateTime b = ZonedDateTime.from(LocalDateTime.of(2008, 6, 30, 11, 20), ZONE_0100);  // a is before b on instant scale
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    public void test_compareTo_hourDifference() {
        ZonedDateTime a = ZonedDateTime.from(LocalDateTime.of(2008, 6, 30, 10, 0), ZONE_0100);
        ZonedDateTime b = ZonedDateTime.from(LocalDateTime.of(2008, 6, 30, 11, 0), ZONE_0200);  // a is before b despite being same time-line time
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_compareTo_null() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime a = ZonedDateTime.from(ldt, ZONE_0100);
        a.compareTo(null);
    }

    //-----------------------------------------------------------------------
    // isAfter() / isBefore()
    //-----------------------------------------------------------------------
    public void test_isBeforeIsAfter() {
        ZonedDateTime a = ZonedDateTime.from(LocalDateTime.of(2008, 6, 30, 11, 30, 58), ZONE_0100);
        ZonedDateTime b = ZonedDateTime.from(LocalDateTime.of(2008, 6, 30, 11, 30, 59), ZONE_0100);  // a is before b due to time
        assertEquals(a.isBefore(b), true);
        assertEquals(a.isAfter(b), false);
        assertEquals(b.isBefore(a), false);
        assertEquals(b.isAfter(a), true);
        assertEquals(a.isBefore(a), false);
        assertEquals(b.isBefore(b), false);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isBefore_null() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime a = ZonedDateTime.from(ldt, ZONE_0100);
        a.isBefore(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isAfter_null() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime a = ZonedDateTime.from(ldt, ZONE_0100);
        a.isAfter(null);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes")
    public void test_equals_true(int y, int o, int d, int h, int m, int s, int n, TimeZone ignored) {
        ZonedDateTime a = ZonedDateTime.from(LocalDateTime.of(y, o, d, h, m, s, n), ZONE_0100);
        ZonedDateTime b = ZonedDateTime.from(LocalDateTime.of(y, o, d, h, m, s, n), ZONE_0100);
        assertEquals(a.equals(b), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_year_differs(int y, int o, int d, int h, int m, int s, int n, TimeZone ignored) {
        ZonedDateTime a = ZonedDateTime.from(LocalDateTime.of(y, o, d, h, m, s, n), ZONE_0100);
        ZonedDateTime b = ZonedDateTime.from(LocalDateTime.of(y + 1, o, d, h, m, s, n), ZONE_0100);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_hour_differs(int y, int o, int d, int h, int m, int s, int n, TimeZone ignored) {
        h = (h == 23 ? 22 : h);
        ZonedDateTime a = ZonedDateTime.from(LocalDateTime.of(y, o, d, h, m, s, n), ZONE_0100);
        ZonedDateTime b = ZonedDateTime.from(LocalDateTime.of(y, o, d, h + 1, m, s, n), ZONE_0100);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_minute_differs(int y, int o, int d, int h, int m, int s, int n, TimeZone ignored) {
        m = (m == 59 ? 58 : m);
        ZonedDateTime a = ZonedDateTime.from(LocalDateTime.of(y, o, d, h, m, s, n), ZONE_0100);
        ZonedDateTime b = ZonedDateTime.from(LocalDateTime.of(y, o, d, h, m + 1, s, n), ZONE_0100);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_second_differs(int y, int o, int d, int h, int m, int s, int n, TimeZone ignored) {
        s = (s == 59 ? 58 : s);
        ZonedDateTime a = ZonedDateTime.from(LocalDateTime.of(y, o, d, h, m, s, n), ZONE_0100);
        ZonedDateTime b = ZonedDateTime.from(LocalDateTime.of(y, o, d, h, m, s + 1, n), ZONE_0100);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_nano_differs(int y, int o, int d, int h, int m, int s, int n, TimeZone ignored) {
        n = (n == 999999999 ? 999999998 : n);
        ZonedDateTime a = ZonedDateTime.from(LocalDateTime.of(y, o, d, h, m, s, n), ZONE_0100);
        ZonedDateTime b = ZonedDateTime.from(LocalDateTime.of(y, o, d, h, m, s, n + 1), ZONE_0100);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_offset_differs(int y, int o, int d, int h, int m, int s, int n, TimeZone ignored) {
        ZonedDateTime a = ZonedDateTime.from(LocalDateTime.of(y, o, d, h, m, s, n), ZONE_0100);
        ZonedDateTime b = ZonedDateTime.from(LocalDateTime.of(y, o, d, h, m, s, n), ZONE_0200);
        assertEquals(a.equals(b), false);
    }

    public void test_equals_itself_true() {
        assertEquals(TEST_DATE_TIME.equals(TEST_DATE_TIME), true);
    }

    public void test_equals_string_false() {
        assertEquals(TEST_DATE_TIME.equals("2007-07-15"), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleToString")
    Object[][] provider_sampleToString() {
        return new Object[][] {
            {2008, 6, 30, 11, 30, 59, 0, "UTC", "2008-06-30T11:30:59Z[UTC]"},
            {2008, 6, 30, 11, 30, 59, 0, "UTC+01:00", "2008-06-30T11:30:59+01:00[UTC+01:00]"},
            {2008, 6, 30, 11, 30, 59, 999000000, "UTC", "2008-06-30T11:30:59.999Z[UTC]"},
            {2008, 6, 30, 11, 30, 59, 999000000, "UTC+01:00", "2008-06-30T11:30:59.999+01:00[UTC+01:00]"},
            {2008, 6, 30, 11, 30, 59, 999000, "UTC", "2008-06-30T11:30:59.000999Z[UTC]"},
            {2008, 6, 30, 11, 30, 59, 999000, "UTC+01:00", "2008-06-30T11:30:59.000999+01:00[UTC+01:00]"},
            {2008, 6, 30, 11, 30, 59, 999, "UTC", "2008-06-30T11:30:59.000000999Z[UTC]"},
            {2008, 6, 30, 11, 30, 59, 999, "UTC+01:00", "2008-06-30T11:30:59.000000999+01:00[UTC+01:00]"},
            
            {2008, 6, 30, 11, 30, 59, 999, "Europe/London", "2008-06-30T11:30:59.000000999+01:00[Europe/London]"},
            {2008, 6, 30, 11, 30, 59, 999, "Europe/London#2008i", "2008-06-30T11:30:59.000000999+01:00[Europe/London#2008i]"},
        };
    }

    @Test(dataProvider="sampleToString")
    public void test_toString(int y, int o, int d, int h, int m, int s, int n, String zoneId, String expected) {
        ZonedDateTime t = ZonedDateTime.from(LocalDateTime.of(y, o, d, h, m, s, n), TimeZone.of(zoneId));
        String str = t.toString();
        assertEquals(str, expected);
    }

}
