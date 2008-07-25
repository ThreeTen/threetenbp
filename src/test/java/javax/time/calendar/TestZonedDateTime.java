/*
 * Copyright (c) 2008, Stephen Colebourne & Michael Nascimento Santos
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
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import javax.time.Instant;
import javax.time.InstantProvider;
import javax.time.calendar.field.DayOfMonth;
import javax.time.calendar.field.HourOfDay;
import javax.time.calendar.field.MinuteOfHour;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.NanoOfSecond;
import javax.time.calendar.field.SecondOfMinute;
import javax.time.calendar.field.Year;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test ZonedDateTime.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestZonedDateTime {

    private static final ZoneOffset OFFSET_0100 = ZoneOffset.zoneOffset(1);
    private static final ZoneOffset OFFSET_0200 = ZoneOffset.zoneOffset(2);
    private static final TimeZone ZONE_0100 = TimeZone.timeZone(OFFSET_0100);
    private static final TimeZone ZONE_0200 = TimeZone.timeZone(OFFSET_0200);
    private ZonedDateTime TEST_DATE_TIME;

    @BeforeMethod
    public void setUp() {
        LocalDateTime dt = LocalDateTime.dateTime(2008, 6, 30, 11, 30, 59, 500);
        TEST_DATE_TIME = ZonedDateTime.dateTime(dt, ZONE_0100);
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(TEST_DATE_TIME instanceof CalendricalProvider);
        assertTrue(TEST_DATE_TIME instanceof Serializable);
        assertTrue(TEST_DATE_TIME instanceof Comparable);
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
//    //-----------------------------------------------------------------------
//    // dateTime factories
//    //-----------------------------------------------------------------------
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
    public void factory_dateTime_DateProviderTimeProvider() {
        DateProvider dateProvider = LocalDate.date(2008, 6, 30);
        TimeProvider timeProvider = LocalTime.time(11, 30, 10, 500);
        ZonedDateTime test = ZonedDateTime.dateTime(dateProvider, timeProvider, ZONE_0100);
        assertEquals(test.getYear(), Year.isoYear(2008));
        assertEquals(test.getMonthOfYear(), MonthOfYear.monthOfYear(6));
        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(10));
        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(500));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_dateTime_DateProviderTimeProvider_nullDate() {
        TimeProvider timeProvider = LocalTime.time(11, 30, 10, 500);
        ZonedDateTime.dateTime((DateProvider) null, timeProvider, ZONE_0100);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_dateTime_DateProviderTimeProvider_nullTime() {
        DateProvider dateProvider = LocalDate.date(2008, 6, 30);
        ZonedDateTime.dateTime(dateProvider, (TimeProvider) null, ZONE_0100);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_dateTime_DateProviderTimeProvider_nullZone() {
        DateProvider dateProvider = LocalDate.date(2008, 6, 30);
        TimeProvider timeProvider = LocalTime.time(11, 30, 10, 500);
        ZonedDateTime.dateTime(dateProvider, timeProvider, null);
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_DateProviderTimeProviderResolver() {
        DateProvider dateProvider = LocalDate.date(2008, 6, 30);
        TimeProvider timeProvider = LocalTime.time(11, 30, 10, 500);
        ZonedDateTime test = ZonedDateTime.dateTime(dateProvider, timeProvider, ZONE_0100, ZoneResolvers.strict());
        assertEquals(test.getYear(), Year.isoYear(2008));
        assertEquals(test.getMonthOfYear(), MonthOfYear.monthOfYear(6));
        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(10));
        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(500));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_dateTime_DateProviderTimeProviderResolver_nullDate() {
        TimeProvider timeProvider = LocalTime.time(11, 30, 10, 500);
        ZonedDateTime.dateTime((DateProvider) null, timeProvider, ZONE_0100, ZoneResolvers.strict());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_dateTime_DateProviderTimeProviderResolver_nullTime() {
        DateProvider dateProvider = LocalDate.date(2008, 6, 30);
        ZonedDateTime.dateTime(dateProvider, (TimeProvider) null, ZONE_0100, ZoneResolvers.strict());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_dateTime_DateProviderTimeProviderResolver_nullZone() {
        DateProvider dateProvider = LocalDate.date(2008, 6, 30);
        TimeProvider timeProvider = LocalTime.time(11, 30, 10, 500);
        ZonedDateTime.dateTime(dateProvider, timeProvider, null, ZoneResolvers.strict());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_dateTime_DateProviderTimeProviderResolver_nullResolver() {
        DateProvider dateProvider = LocalDate.date(2008, 6, 30);
        TimeProvider timeProvider = LocalTime.time(11, 30, 10, 500);
        ZonedDateTime.dateTime(dateProvider, timeProvider, ZONE_0100, (ZoneResolver) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_dateTime_DateTimeProviderResolver_badDateProvider() {
        TimeProvider timeProvider = LocalTime.time(11, 30, 10, 500);
        ZonedDateTime.dateTime(new MockDateProviderReturnsNull(), timeProvider, ZONE_0100, ZoneResolvers.strict());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_dateTime_DateTimeProviderResolver_badTimeProvider() {
        DateProvider dateProvider = LocalDate.date(2008, 6, 30);
        ZonedDateTime.dateTime(dateProvider, new MockTimeProviderReturnsNull(), ZONE_0100, ZoneResolvers.strict());
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_DateTimeProvider() {
        DateTimeProvider provider = LocalDateTime.dateTime(2008, 6, 30, 11, 30, 10, 500);
        ZonedDateTime test = ZonedDateTime.dateTime(provider, ZONE_0100);
        assertEquals(test.getYear(), Year.isoYear(2008));
        assertEquals(test.getMonthOfYear(), MonthOfYear.monthOfYear(6));
        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(10));
        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(500));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_dateTime_DateTimeProvider_nullDateTime() {
        ZonedDateTime.dateTime((DateTimeProvider) null, ZONE_0100);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_dateTime_DateTimeProvider_nullZone() {
        DateTimeProvider provider = LocalDateTime.dateTime(2008, 6, 30, 11, 30, 10, 500);
        ZonedDateTime.dateTime(provider, null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_dateTime_DateTimeProvider_badProvider() {
        ZonedDateTime.dateTime(new MockDateTimeProviderReturnsNull(), ZONE_0100);
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_DateTimeProviderResolver() {
        DateTimeProvider provider = LocalDateTime.dateTime(2008, 6, 30, 11, 30, 10, 500);
        ZonedDateTime test = ZonedDateTime.dateTime(provider, ZONE_0100, ZoneResolvers.strict());
        assertEquals(test.getYear(), Year.isoYear(2008));
        assertEquals(test.getMonthOfYear(), MonthOfYear.monthOfYear(6));
        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(10));
        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(500));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_dateTime_DateTimeProviderResolver_nullDateTime() {
        ZonedDateTime.dateTime((DateTimeProvider) null, ZONE_0100, ZoneResolvers.strict());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_dateTime_DateTimeProviderResolver_nullZone() {
        DateTimeProvider provider = LocalDateTime.dateTime(2008, 6, 30, 11, 30, 10, 500);
        ZonedDateTime.dateTime(provider, null, ZoneResolvers.strict());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_dateTime_DateTimeProviderResolver_nullResolver() {
        DateTimeProvider provider = LocalDateTime.dateTime(2008, 6, 30, 11, 30, 10, 500);
        ZonedDateTime.dateTime(provider, null, (ZoneResolver) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_dateTime_DateTimeProviderResolver_badProvider() {
        ZonedDateTime.dateTime(new MockDateTimeProviderReturnsNull(), ZONE_0100, ZoneResolvers.strict());
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_OffsetDateTimeProvider() {
        OffsetDateTime odt = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 10, 500, OFFSET_0100);
        ZonedDateTime test = ZonedDateTime.dateTime(odt, ZONE_0100);
        assertEquals(test.getYear(), Year.isoYear(2008));
        assertEquals(test.getMonthOfYear(), MonthOfYear.monthOfYear(6));
        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(10));
        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(500));
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void factory_dateTime_OffsetDateTimeProvider_invalidOffset() {
        OffsetDateTime odt = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 10, 500, OFFSET_0200);
        ZonedDateTime.dateTime(odt, ZONE_0100);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_dateTime_OffsetDateTimeProvider_nullDateTime() {
        ZonedDateTime.dateTime((OffsetDateTime) null, ZONE_0100);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_dateTime_OffsetDateTimeProvider_nullZone() {
        OffsetDateTime odt = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 10, 500, OFFSET_0100);
        ZonedDateTime.dateTime(odt, null);
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_InstantProvider() {
        OffsetDateTime odt = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 10, 500, OFFSET_0100);
        InstantProvider provider = odt.toInstant();
        ZonedDateTime test = ZonedDateTime.dateTime(provider, ZONE_0100);
        assertEquals(test.getYear(), Year.isoYear(2008));
        assertEquals(test.getMonthOfYear(), MonthOfYear.monthOfYear(6));
        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(10));
        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(500));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_dateTime_InstantProvider_nullProvider() {
        ZonedDateTime.dateTime((InstantProvider) null, ZONE_0100);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_dateTime_InstantProvider_nullZone() {
        InstantProvider provider = Instant.instant(0L);
        ZonedDateTime.dateTime(provider, null);
    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class)
    public void constructor_nullTime() throws Throwable  {
        Constructor<ZonedDateTime> con = ZonedDateTime.class.getDeclaredConstructor(OffsetDateTime.class, TimeZone.class);
        con.setAccessible(true);
        try {
            con.newInstance(null, ZONE_0100);
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void constructor_nullZoned() throws Throwable  {
        Constructor<ZonedDateTime> con = ZonedDateTime.class.getDeclaredConstructor(OffsetDateTime.class, TimeZone.class);
        con.setAccessible(true);
        try {
            con.newInstance(OffsetDateTime.dateTime(2008, 6, 30, 11, 30, OFFSET_0100), null);
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
            {2008, 6, 30, 11, 30, 20, 500, ZONE_0100},
            {2008, 6, 30, 11, 0, 0, 0, ZONE_0100},
            {2008, 6, 30, 23, 59, 59, 999999999, ZONE_0100},
            {-1, 1, 1, 0, 0, 0, 0, ZONE_0100},
        };
    }

    @Test(dataProvider="sampleTimes")
    public void test_get(int y, int o, int d, int h, int m, int s, int n, TimeZone zone) {
        LocalDate localDate = LocalDate.date(y, o, d);
        LocalTime localTime = LocalTime.time(h, m, s, n);
        LocalDateTime localDateTime = LocalDateTime.dateTime(localDate, localTime);
        ZoneOffset offset = zone.getOffset(Instant.instant(0L));
        ZonedDateTime a = ZonedDateTime.dateTime(localDateTime, zone);
        assertSame(a.getDateTime(), localDateTime);
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
        assertEquals(a.toOffsetDate(), OffsetDate.date(localDate, offset));
        assertEquals(a.toOffsetTime(), OffsetTime.time(localTime, offset));
        assertEquals(a.toOffsetDateTime(), OffsetDateTime.dateTime(localDateTime, offset));
        assertEquals(a.toCalendrical(), Calendrical.calendrical(localDateTime.toLocalDate(), localDateTime.toLocalTime(), offset, zone));
        assertEquals(a.toString(), a.toOffsetDateTime().toString() + " " + zone.toString());
    }

    //-----------------------------------------------------------------------
    // isSupported(DateTimeFieldRule)
    //-----------------------------------------------------------------------
    public void test_isSupported() {
        assertEquals(TEST_DATE_TIME.isSupported(ISOChronology.yearRule()), true);
        assertEquals(TEST_DATE_TIME.isSupported(ISOChronology.monthOfYearRule()), true);
        assertEquals(TEST_DATE_TIME.isSupported(ISOChronology.dayOfMonthRule()), true);
        assertEquals(TEST_DATE_TIME.isSupported(ISOChronology.dayOfWeekRule()), true);
        assertEquals(TEST_DATE_TIME.isSupported(ISOChronology.dayOfYearRule()), true);
        
        assertEquals(TEST_DATE_TIME.isSupported(ISOChronology.hourOfDayRule()), true);
        assertEquals(TEST_DATE_TIME.isSupported(ISOChronology.minuteOfHourRule()), true);
        assertEquals(TEST_DATE_TIME.isSupported(ISOChronology.secondOfMinuteRule()), true);
        assertEquals(TEST_DATE_TIME.isSupported(ISOChronology.nanoOfSecondRule()), true);
        assertEquals(TEST_DATE_TIME.isSupported(ISOChronology.hourOfAmPmRule()), true);
    }

    //-----------------------------------------------------------------------
    // get(DateTimeFieldRule)
    //-----------------------------------------------------------------------
    public void test_get_DateTimeFieldRule() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime test = ZonedDateTime.dateTime(ldt, ZONE_0100);
        assertEquals(test.get(ISOChronology.hourOfDayRule()), 23);
        assertEquals(test.get(ISOChronology.minuteOfHourRule()), 30);
        assertEquals(test.get(ISOChronology.secondOfMinuteRule()), 59);
        assertEquals(test.get(ISOChronology.hourOfAmPmRule()), 11);
        assertEquals(test.get(ISOChronology.amPmOfDayRule()), 1);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_get_DateTimeFieldRule_null() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime test = ZonedDateTime.dateTime(ldt, ZONE_0100);
        test.get((DateTimeFieldRule) null);
    }

    @Test(expectedExceptions=UnsupportedCalendarFieldException.class )
    public void test_get_DateTimeFieldRule_unsupported() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime test = ZonedDateTime.dateTime(ldt, ZONE_0100);
        try {
            test.get(MockRuleNoValue.INSTANCE);
        } catch (UnsupportedCalendarFieldException ex) {
            assertEquals(ex.getFieldRule(), MockRuleNoValue.INSTANCE);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // withDateTime()
    //-----------------------------------------------------------------------
    public void test_withDateTime() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        LocalDateTime dt = LocalDateTime.dateTime(2008, 6, 30, 11, 31, 0);
        ZonedDateTime test = base.withDateTime(dt);
        assertEquals(test.toLocalDateTime(), dt);
        assertSame(test.getZone(), base.getZone());
    }

    public void test_withDateTime_noChange() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        LocalDateTime dt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59);
        ZonedDateTime test = base.withDateTime(dt);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_withDateTime_null() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        base.withDateTime(null);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_withDateTime_badProvider() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        base.withDateTime(new MockDateTimeProviderReturnsNull());
    }

    //-----------------------------------------------------------------------
    // withZoneSameLocal()
    //-----------------------------------------------------------------------
    public void test_withZoneSameLocal() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.withZoneSameLocal(ZONE_0200);
        assertEquals(test.toLocalTime(), base.toLocalTime());
        assertSame(test.getZone(), ZONE_0200);
    }

    public void test_withZoneSameLocal_noChange() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.withZoneSameLocal(ZONE_0100);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_withZoneSameLocal_null() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        base.withZoneSameLocal(null);
    }

    //-----------------------------------------------------------------------
    // withZoneSameInstant()
    //-----------------------------------------------------------------------
    public void test_withZoneSameInstant() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.withZoneSameInstant(ZONE_0200);
        ZonedDateTime expected = ZonedDateTime.dateTime(ldt.plusHours(1), ZONE_0200);
        assertEquals(test, expected);
    }

    public void test_withZoneSameInstant_noChange() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.withZoneSameInstant(ZONE_0100);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_withZoneSameInstant_null() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        base.withZoneSameInstant(null);
    }

    //-----------------------------------------------------------------------
    // with(DateAdjuster)
    //-----------------------------------------------------------------------
    public void test_with_DateAdjuster() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.with(Year.isoYear(2007));
        assertEquals(test, ZonedDateTime.dateTime(ldt.withYear(2007), ZONE_0100));
    }

    public void test_with_DateAdjuster_noChange() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        DateAdjuster adjuster = LocalDate.date(2008, 6, 30);
        ZonedDateTime test = base.with(adjuster);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_with_DateAdjuster_null() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        base.with((DateAdjuster) null);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_with_DateAdjuster_badAdjuster() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        base.with(new MockDateAdjusterReturnsNull());
    }

    //-----------------------------------------------------------------------
    // with(TimeAdjuster)
    //-----------------------------------------------------------------------
    public void test_with_TimeAdjuster() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.with(HourOfDay.hourOfDay(1));
        assertEquals(test, ZonedDateTime.dateTime(ldt.withHourOfDay(1), ZONE_0100));
    }

    public void test_with_TimeAdjuster_noChange() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        TimeAdjuster adjuster = LocalTime.time(23, 30, 59, 0);
        ZonedDateTime test = base.with(adjuster);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_with_TimeAdjuster_null() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        base.with((TimeAdjuster) null);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_with_TimeAdjuster_badAdjuster() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        base.with(new MockTimeAdjusterReturnsNull());
    }

    //-----------------------------------------------------------------------
    // withYear()
    //-----------------------------------------------------------------------
    public void test_withYear_normal() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.withYear(2007);
        assertEquals(test, ZonedDateTime.dateTime(ldt.withYear(2007), ZONE_0100));
    }

    public void test_withYear_noChange() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.withYear(2008);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withMonthOfYear()
    //-----------------------------------------------------------------------
    public void test_withMonthOfYearr_normal() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.withMonthOfYear(1);
        assertEquals(test, ZonedDateTime.dateTime(ldt.withMonthOfYear(1), ZONE_0100));
    }

    public void test_withMonthOfYear_noChange() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.withMonthOfYear(6);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withDayOfMonth()
    //-----------------------------------------------------------------------
    public void test_withDayOfMonthr_normal() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.withDayOfMonth(15);
        assertEquals(test, ZonedDateTime.dateTime(ldt.withDayOfMonth(15), ZONE_0100));
    }

    public void test_withDayOfMonth_noChange() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.withDayOfMonth(30);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withDate()
    //-----------------------------------------------------------------------
    public void test_withDate() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.withDate(2007, 1, 1);
        ZonedDateTime expected = ZonedDateTime.dateTime(ldt.withDate(2007, 1, 1), ZONE_0100);
        assertEquals(test, expected);
    }

    public void test_withDate_noChange() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.withDate(2008, 6, 30);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withHourOfDay()
    //-----------------------------------------------------------------------
    public void test_withHourOfDayr_normal() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.withHourOfDay(15);
        assertEquals(test, ZonedDateTime.dateTime(ldt.withHourOfDay(15), ZONE_0100));
    }

    public void test_withHourOfDay_noChange() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.withHourOfDay(23);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withMinuteOfHour()
    //-----------------------------------------------------------------------
    public void test_withMinuteOfHour_normal() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.withMinuteOfHour(15);
        assertEquals(test, ZonedDateTime.dateTime(ldt.withMinuteOfHour(15), ZONE_0100));
    }

    public void test_withMinuteOfHour_noChange() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.withMinuteOfHour(30);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withSecondOfMinute()
    //-----------------------------------------------------------------------
    public void test_withSecondOfMinute_normal() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.withSecondOfMinute(15);
        assertEquals(test, ZonedDateTime.dateTime(ldt.withSecondOfMinute(15), ZONE_0100));
    }

    public void test_withSecondOfMinute_noChange() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.withSecondOfMinute(59);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withNanoOfSecond()
    //-----------------------------------------------------------------------
    public void test_withNanoOfSecond_normal() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 1);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.withNanoOfSecond(15);
        assertEquals(test, ZonedDateTime.dateTime(ldt.withNanoOfSecond(15), ZONE_0100));
    }

    public void test_withNanoOfSecond_noChange() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 1);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.withNanoOfSecond(1);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withTime()
    //-----------------------------------------------------------------------
    public void test_withTime_HM() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 0, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.withTime(12, 10);
        ZonedDateTime expected = ZonedDateTime.dateTime(ldt.withTime(12, 10), ZONE_0100);
        assertEquals(test, expected);
    }

    public void test_withTime_HM_noChange() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 0, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.withTime(23, 30);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    public void test_withTime_HMS() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.withTime(12, 10, 9);
        ZonedDateTime expected = ZonedDateTime.dateTime(ldt.withTime(12, 10, 9), ZONE_0100);
        assertEquals(test, expected);
    }

    public void test_withTime_HMS_noChange() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.withTime(23, 30, 59);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    public void test_withTime_HMSN() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.withTime(12, 10, 9, 8);
        ZonedDateTime expected = ZonedDateTime.dateTime(ldt.withTime(12, 10, 9, 8), ZONE_0100);
        assertEquals(test, expected);
    }

    public void test_withTime_HMSN_noChange() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 500);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.withTime(23, 30, 59, 500);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusYears()
    //-----------------------------------------------------------------------
    public void test_plusYears() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.plusYears(1);
        assertEquals(test, ZonedDateTime.dateTime(ldt.plusYears(1), ZONE_0100));
    }

    public void test_plusYears_zero() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.plusYears(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusMonths()
    //-----------------------------------------------------------------------
    public void test_plusMonths() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.plusMonths(1);
        assertEquals(test, ZonedDateTime.dateTime(ldt.plusMonths(1), ZONE_0100));
    }

    public void test_plusMonths_zero() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.plusMonths(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusWeeks()
    //-----------------------------------------------------------------------
    public void test_plusWeeks() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.plusWeeks(1);
        assertEquals(test, ZonedDateTime.dateTime(ldt.plusWeeks(1), ZONE_0100));
    }

    public void test_plusWeeks_zero() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.plusWeeks(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusDays()
    //-----------------------------------------------------------------------
    public void test_plusDays() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.plusDays(1);
        assertEquals(test, ZonedDateTime.dateTime(ldt.plusDays(1), ZONE_0100));
    }

    public void test_plusDays_zero() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.plusDays(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusHours()
    //-----------------------------------------------------------------------
    public void test_plusHours() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.plusHours(13);
        assertEquals(test, ZonedDateTime.dateTime(ldt.plusHours(13), ZONE_0100));
    }

    public void test_plusHours_zero() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.plusHours(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusMinutes()
    //-----------------------------------------------------------------------
    public void test_plusMinutes() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.plusMinutes(30);
        assertEquals(test, ZonedDateTime.dateTime(ldt.plusMinutes(30), ZONE_0100));
    }

    public void test_plusMinutes_zero() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.plusMinutes(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusSeconds()
    //-----------------------------------------------------------------------
    public void test_plusSeconds() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.plusSeconds(1);
        assertEquals(test, ZonedDateTime.dateTime(ldt.plusSeconds(1), ZONE_0100));
    }

    public void test_plusSeconds_zero() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.plusSeconds(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusNanos()
    //-----------------------------------------------------------------------
    public void test_plusNanos() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.plusNanos(1);
        assertEquals(test, ZonedDateTime.dateTime(ldt.plusNanos(1), ZONE_0100));
    }

    public void test_plusNanos_zero() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        ZonedDateTime test = base.plusNanos(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // matches(DateMatcher)
    //-----------------------------------------------------------------------
    public void test_matches_DateMatcher() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime test = ZonedDateTime.dateTime(ldt, ZONE_0100);
        assertEquals(test.matches(Year.isoYear(2008)), true);
        assertEquals(test.matches(Year.isoYear(2007)), false);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_matches_DateMatcher_null() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        base.matches((DateMatcher) null);
    }

    //-----------------------------------------------------------------------
    // matches(TimeMatcher)
    //-----------------------------------------------------------------------
    public void test_matches_TimeMatcher() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime test = ZonedDateTime.dateTime(ldt, ZONE_0100);
        assertEquals(test.matches(HourOfDay.hourOfDay(23)), true);
        assertEquals(test.matches(HourOfDay.hourOfDay(10)), false);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_matches_TimeMatcher_null() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.dateTime(ldt, ZONE_0100);
        base.matches((TimeMatcher) null);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo_time() {
        ZonedDateTime a = ZonedDateTime.dateTime(LocalDateTime.dateTime(2008, 6, 30, 11, 29), ZONE_0100);
        ZonedDateTime b = ZonedDateTime.dateTime(LocalDateTime.dateTime(2008, 6, 30, 11, 30), ZONE_0100);  // a is before b due to time
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    public void test_compareTo_offset() {
        ZonedDateTime a = ZonedDateTime.dateTime(LocalDateTime.dateTime(2008, 6, 30, 11, 30), ZONE_0200);
        ZonedDateTime b = ZonedDateTime.dateTime(LocalDateTime.dateTime(2008, 6, 30, 11, 30), ZONE_0100);  // a is before b due to offset
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    public void test_compareTo_both() {
        ZonedDateTime a = ZonedDateTime.dateTime(LocalDateTime.dateTime(2008, 6, 30, 11, 50), ZONE_0200);
        ZonedDateTime b = ZonedDateTime.dateTime(LocalDateTime.dateTime(2008, 6, 30, 11, 20), ZONE_0100);  // a is before b on instant scale
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    public void test_compareTo_hourDifference() {
        ZonedDateTime a = ZonedDateTime.dateTime(LocalDateTime.dateTime(2008, 6, 30, 10, 0), ZONE_0100);
        ZonedDateTime b = ZonedDateTime.dateTime(LocalDateTime.dateTime(2008, 6, 30, 11, 0), ZONE_0200);  // a is before b despite being same time-line time
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_compareTo_null() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime a = ZonedDateTime.dateTime(ldt, ZONE_0100);
        a.compareTo(null);
    }

    //-----------------------------------------------------------------------
    // isAfter() / isBefore()
    //-----------------------------------------------------------------------
    public void test_isBeforeIsAfter() {
        ZonedDateTime a = ZonedDateTime.dateTime(LocalDateTime.dateTime(2008, 6, 30, 11, 30, 58), ZONE_0100);
        ZonedDateTime b = ZonedDateTime.dateTime(LocalDateTime.dateTime(2008, 6, 30, 11, 30, 59), ZONE_0100);  // a is before b due to time
        assertEquals(a.isBefore(b), true);
        assertEquals(a.isAfter(b), false);
        assertEquals(b.isBefore(a), false);
        assertEquals(b.isAfter(a), true);
        assertEquals(a.isBefore(a), false);
        assertEquals(b.isBefore(b), false);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isBefore_null() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime a = ZonedDateTime.dateTime(ldt, ZONE_0100);
        a.isBefore(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isAfter_null() {
        LocalDateTime ldt = LocalDateTime.dateTime(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime a = ZonedDateTime.dateTime(ldt, ZONE_0100);
        a.isAfter(null);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes")
    public void test_equals_true(int y, int o, int d, int h, int m, int s, int n, TimeZone ignored) {
        ZonedDateTime a = ZonedDateTime.dateTime(LocalDateTime.dateTime(y, o, d, h, m, s, n), ZONE_0100);
        ZonedDateTime b = ZonedDateTime.dateTime(LocalDateTime.dateTime(y, o, d, h, m, s, n), ZONE_0100);
        assertEquals(a.equals(b), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_year_differs(int y, int o, int d, int h, int m, int s, int n, TimeZone ignored) {
        ZonedDateTime a = ZonedDateTime.dateTime(LocalDateTime.dateTime(y, o, d, h, m, s, n), ZONE_0100);
        ZonedDateTime b = ZonedDateTime.dateTime(LocalDateTime.dateTime(y + 1, o, d, h, m, s, n), ZONE_0100);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_hour_differs(int y, int o, int d, int h, int m, int s, int n, TimeZone ignored) {
        h = (h == 23 ? 22 : h);
        ZonedDateTime a = ZonedDateTime.dateTime(LocalDateTime.dateTime(y, o, d, h, m, s, n), ZONE_0100);
        ZonedDateTime b = ZonedDateTime.dateTime(LocalDateTime.dateTime(y, o, d, h + 1, m, s, n), ZONE_0100);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_minute_differs(int y, int o, int d, int h, int m, int s, int n, TimeZone ignored) {
        m = (m == 59 ? 58 : m);
        ZonedDateTime a = ZonedDateTime.dateTime(LocalDateTime.dateTime(y, o, d, h, m, s, n), ZONE_0100);
        ZonedDateTime b = ZonedDateTime.dateTime(LocalDateTime.dateTime(y, o, d, h, m + 1, s, n), ZONE_0100);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_second_differs(int y, int o, int d, int h, int m, int s, int n, TimeZone ignored) {
        s = (s == 59 ? 58 : s);
        ZonedDateTime a = ZonedDateTime.dateTime(LocalDateTime.dateTime(y, o, d, h, m, s, n), ZONE_0100);
        ZonedDateTime b = ZonedDateTime.dateTime(LocalDateTime.dateTime(y, o, d, h, m, s + 1, n), ZONE_0100);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_nano_differs(int y, int o, int d, int h, int m, int s, int n, TimeZone ignored) {
        n = (n == 999999999 ? 999999998 : n);
        ZonedDateTime a = ZonedDateTime.dateTime(LocalDateTime.dateTime(y, o, d, h, m, s, n), ZONE_0100);
        ZonedDateTime b = ZonedDateTime.dateTime(LocalDateTime.dateTime(y, o, d, h, m, s, n + 1), ZONE_0100);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_offset_differs(int y, int o, int d, int h, int m, int s, int n, TimeZone ignored) {
        ZonedDateTime a = ZonedDateTime.dateTime(LocalDateTime.dateTime(y, o, d, h, m, s, n), ZONE_0100);
        ZonedDateTime b = ZonedDateTime.dateTime(LocalDateTime.dateTime(y, o, d, h, m, s, n), ZONE_0200);
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
            {2008, 6, 30, 11, 30, 59, 0, "UTC", "2008-06-30T11:30:59Z UTC"},
            {2008, 6, 30, 11, 30, 59, 0, "UTC+01:00", "2008-06-30T11:30:59+01:00 UTC+01:00"},
            {2008, 6, 30, 11, 30, 59, 999000000, "UTC", "2008-06-30T11:30:59.999Z UTC"},
            {2008, 6, 30, 11, 30, 59, 999000000, "UTC+01:00", "2008-06-30T11:30:59.999+01:00 UTC+01:00"},
            {2008, 6, 30, 11, 30, 59, 999000, "UTC", "2008-06-30T11:30:59.000999Z UTC"},
            {2008, 6, 30, 11, 30, 59, 999000, "UTC+01:00", "2008-06-30T11:30:59.000999+01:00 UTC+01:00"},
            {2008, 6, 30, 11, 30, 59, 999, "UTC", "2008-06-30T11:30:59.000000999Z UTC"},
            {2008, 6, 30, 11, 30, 59, 999, "UTC+01:00", "2008-06-30T11:30:59.000000999+01:00 UTC+01:00"},
        };
    }

    @Test(dataProvider="sampleToString")
    public void test_toString(int y, int o, int d, int h, int m, int s, int n, String offsetId, String expected) {
        ZonedDateTime t = ZonedDateTime.dateTime(LocalDateTime.dateTime(y, o, d, h, m, s, n), TimeZone.timeZone(offsetId));
        String str = t.toString();
        assertEquals(str, expected);
    }

}
