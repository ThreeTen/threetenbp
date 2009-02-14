/*
 * Copyright (c) 2008-2009, Stephen Colebourne & Michael Nascimento Santos
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
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import javax.time.InstantProvider;
import javax.time.calendar.field.DayOfMonth;
import javax.time.calendar.field.HourOfDay;
import javax.time.calendar.field.MinuteOfHour;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.NanoOfSecond;
import javax.time.calendar.field.SecondOfMinute;
import javax.time.calendar.field.Year;
import javax.time.period.Period;
import javax.time.period.PeriodProvider;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test OffsetDateTime.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestOffsetDateTime {

    private static final ZoneOffset OFFSET_PONE = ZoneOffset.zoneOffset(1);
    private static final ZoneOffset OFFSET_PTWO = ZoneOffset.zoneOffset(2);
    private static final ZoneOffset OFFSET_MONE = ZoneOffset.zoneOffset(-1);
    private static final ZoneOffset OFFSET_MTWO = ZoneOffset.zoneOffset(-2);
    private OffsetDateTime TEST_DATE_TIME;

    @BeforeMethod
    public void setUp() {
        TEST_DATE_TIME = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, 500, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(TEST_DATE_TIME instanceof InstantProvider);
        assertTrue(TEST_DATE_TIME instanceof CalendricalProvider);
        assertTrue(TEST_DATE_TIME instanceof Serializable);
        assertTrue(TEST_DATE_TIME instanceof Comparable);
        assertTrue(TEST_DATE_TIME instanceof DateTimeProvider);
        assertTrue(TEST_DATE_TIME instanceof DateMatcher);
        assertTrue(TEST_DATE_TIME instanceof TimeMatcher);
        assertTrue(TEST_DATE_TIME instanceof DateAdjuster);
        assertTrue(TEST_DATE_TIME instanceof TimeAdjuster);
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(TEST_DATE_TIME);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), TEST_DATE_TIME);
    }


    public void test_immutable() {
        Class<OffsetDateTime> cls = OffsetDateTime.class;
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            assertTrue(Modifier.isPrivate(field.getModifiers()));
            assertTrue(Modifier.isFinal(field.getModifiers()));
        }
    }

    //-----------------------------------------------------------------------
    // dateMidnight factories
    //-----------------------------------------------------------------------
    private void check(OffsetDateTime test, int y, int mo, int d, int h, int m, int s, int n, ZoneOffset offset) {
        assertEquals(test.getYear(), y);
        assertEquals(test.getMonthOfYear().getValue(), mo);
        assertEquals(test.getDayOfMonth(), d);
        assertEquals(test.getHourOfDay(), h);
        assertEquals(test.getMinuteOfHour(), m);
        assertEquals(test.getSecondOfMinute(), s);
        assertEquals(test.getNanoOfSecond(), n);
        assertEquals(test.getOffset(), offset);
    }

    //-----------------------------------------------------------------------
    public void factory_dateMidnight_YMD() {
        OffsetDateTime test = OffsetDateTime.dateMidnight(Year.isoYear(2008), MonthOfYear.JUNE, DayOfMonth.dayOfMonth(30), OFFSET_PONE);
        check(test, 2008, 6, 30, 0, 0, 0, 0, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_dateMidnight_YMD_nullYear() {
        OffsetDateTime.dateMidnight(null, MonthOfYear.JUNE, DayOfMonth.dayOfMonth(30), OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_dateMidnight_YMD_nullMonth() {
        OffsetDateTime.dateMidnight(Year.isoYear(2008), null, DayOfMonth.dayOfMonth(30), OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_dateMidnight_YMD_nullDay() {
        OffsetDateTime.dateMidnight(Year.isoYear(2008), MonthOfYear.JUNE, null, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_dateMidnight_YMD_nullOffset() {
        OffsetDateTime.dateMidnight(Year.isoYear(2008), MonthOfYear.JUNE, DayOfMonth.dayOfMonth(30), null);
    }

    //-----------------------------------------------------------------------
    public void factory_dateMidnight_intMonthInt() {
        OffsetDateTime test = OffsetDateTime.dateMidnight(2008, MonthOfYear.JUNE, 30, OFFSET_PONE);
        check(test, 2008, 6, 30, 0, 0, 0, 0, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    public void factory_dateMidnight_ints() {
        OffsetDateTime test = OffsetDateTime.dateMidnight(2008, 6, 30, OFFSET_PONE);
        check(test, 2008, 6, 30, 0, 0, 0, 0, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    public void factory_dateMidnight_DateProvider() {
        DateProvider provider = LocalDate.date(2008, 6, 30);
        OffsetDateTime test = OffsetDateTime.dateMidnight(provider, OFFSET_PONE);
        check(test, 2008, 6, 30, 0, 0, 0, 0, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    public void factory_dateMidnight_multiProvider_checkAmbiguous() {
        MockMultiProvider mmp = new MockMultiProvider(2008, 6, 30, 11, 30, 10, 500);
        OffsetDateTime test = OffsetDateTime.dateMidnight(mmp, OFFSET_PTWO);
        check(test, 2008, 6, 30, 0, 0, 0, 0, OFFSET_PTWO);
    }

    //-----------------------------------------------------------------------
    // dateTime factories
    //-----------------------------------------------------------------------
    public void factory_dateTime_objectsHM() {
        OffsetDateTime test = OffsetDateTime.dateTime(
                Year.isoYear(2008), MonthOfYear.monthOfYear(6), DayOfMonth.dayOfMonth(30),
                HourOfDay.hourOfDay(11), MinuteOfHour.minuteOfHour(30), OFFSET_PONE);
        check(test, 2008, 6, 30, 11, 30, 0, 0, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_objectsHMS() {
        OffsetDateTime test = OffsetDateTime.dateTime(
                Year.isoYear(2008), MonthOfYear.monthOfYear(6), DayOfMonth.dayOfMonth(30),
                HourOfDay.hourOfDay(11), MinuteOfHour.minuteOfHour(30), SecondOfMinute.secondOfMinute(10), OFFSET_PONE);
        check(test, 2008, 6, 30, 11, 30, 10, 0, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_objectsHMSN() {
        OffsetDateTime test = OffsetDateTime.dateTime(
                Year.isoYear(2008), MonthOfYear.monthOfYear(6), DayOfMonth.dayOfMonth(30),
                HourOfDay.hourOfDay(11), MinuteOfHour.minuteOfHour(30),
                SecondOfMinute.secondOfMinute(10), NanoOfSecond.nanoOfSecond(500), OFFSET_PONE);
        check(test, 2008, 6, 30, 11, 30, 10, 500, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_intMonthIntHM() {
        OffsetDateTime test = OffsetDateTime.dateTime(2008, MonthOfYear.JUNE, 30, 11, 30, OFFSET_PONE);
        check(test, 2008, 6, 30, 11, 30, 0, 0, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_intMonthIntHMS() {
        OffsetDateTime test = OffsetDateTime.dateTime(2008, MonthOfYear.JUNE, 30, 11, 30, 10, OFFSET_PONE);
        check(test, 2008, 6, 30, 11, 30, 10, 0, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_intMonthIntHMSN() {
        OffsetDateTime test = OffsetDateTime.dateTime(2008, MonthOfYear.JUNE, 30, 11, 30, 10, 500, OFFSET_PONE);
        check(test, 2008, 6, 30, 11, 30, 10, 500, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_intsHM() {
        OffsetDateTime test = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, OFFSET_PONE);
        check(test, 2008, 6, 30, 11, 30, 0, 0, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_intsHMS() {
        OffsetDateTime test = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 10, OFFSET_PONE);
        check(test, 2008, 6, 30, 11, 30, 10, 0, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_intsHMSN() {
        OffsetDateTime test = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 10, 500, OFFSET_PONE);
        check(test, 2008, 6, 30, 11, 30, 10, 500, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_DateProviderTimeProvider() {
        DateProvider dateProvider = LocalDate.date(2008, 6, 30);
        TimeProvider timeProvider = LocalTime.time(11, 30, 10, 500);
        OffsetDateTime test = OffsetDateTime.dateTime(dateProvider, timeProvider, OFFSET_PONE);
        check(test, 2008, 6, 30, 11, 30, 10, 500, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_dateTime_DateProviderTimeProvider_nullDateProvider() {
        TimeProvider timeProvider = LocalTime.time(11, 30, 10, 500);
        OffsetDateTime.dateTime((DateProvider) null, timeProvider, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_dateTime_DateProviderTimeProvider_nullTimeProvider() {
        DateProvider dateProvider = LocalDate.date(2008, 6, 30);
        OffsetDateTime.dateTime(dateProvider, (TimeProvider) null, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_dateTime_DateProviderTimeProvider_nullOffset() {
        DateTimeProvider provider = LocalDateTime.dateTime(2008, 6, 30, 11, 30, 10, 500);
        OffsetDateTime.dateTime(provider, (ZoneOffset) null);
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_DateTimeProvider() {
        DateTimeProvider provider = LocalDateTime.dateTime(2008, 6, 30, 11, 30, 10, 500);
        OffsetDateTime test = OffsetDateTime.dateTime(provider, OFFSET_PONE);
        check(test, 2008, 6, 30, 11, 30, 10, 500, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_dateTime_DateTimeProvider_nullProvider() {
        OffsetDateTime.dateTime((DateTimeProvider) null, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_dateTime_DateTimeProvider_nullOffset() {
        DateTimeProvider provider = LocalDateTime.dateTime(2008, 6, 30, 11, 30, 10, 500);
        OffsetDateTime.dateTime(provider, (ZoneOffset) null);
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_multiProvider_checkAmbiguous() {
        MockMultiProvider mmp = new MockMultiProvider(2008, 6, 30, 11, 30, 10, 500);
        OffsetDateTime test = OffsetDateTime.dateTime(mmp, OFFSET_PTWO);
        check(test, 2008, 6, 30, 11, 30, 10, 500, OFFSET_PTWO);
    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class)
    public void constructor_nullTime() throws Throwable  {
        Constructor<OffsetDateTime> con = OffsetDateTime.class.getDeclaredConstructor(LocalDateTime.class, ZoneOffset.class);
        con.setAccessible(true);
        try {
            con.newInstance(null, OFFSET_PONE);
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void constructor_nullOffset() throws Throwable  {
        Constructor<OffsetDateTime> con = OffsetDateTime.class.getDeclaredConstructor(LocalDateTime.class, ZoneOffset.class);
        con.setAccessible(true);
        try {
            con.newInstance(LocalDateTime.dateTime(2008, 6, 30, 11, 30), null);
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
            {2008, 6, 30, 11, 30, 20, 500, OFFSET_PONE},
            {2008, 6, 30, 11, 0, 0, 0, OFFSET_PONE},
            {2008, 6, 30, 23, 59, 59, 999999999, OFFSET_PONE},
            {-1, 1, 1, 0, 0, 0, 0, OFFSET_PONE},
        };
    }

    @Test(dataProvider="sampleTimes")
    public void test_get(int y, int o, int d, int h, int m, int s, int n, ZoneOffset offset) {
        LocalDate localDate = LocalDate.date(y, o, d);
        LocalTime localTime = LocalTime.time(h, m, s, n);
        LocalDateTime localDateTime = LocalDateTime.dateTime(localDate, localTime);
        OffsetDateTime a = OffsetDateTime.dateTime(localDateTime, offset);
        assertSame(a.getDateTime(), localDateTime);
        assertSame(a.getOffset(), offset);
        assertEquals(a.getChronology(), ISOChronology.INSTANCE);
        
        assertEquals(a.getYear(), localDate.getYear());
        assertEquals(a.getMonthOfYear(), localDate.getMonthOfYear());
        assertEquals(a.getDayOfMonth(), localDate.getDayOfMonth());
        assertEquals(a.getDayOfYear(), localDate.getDayOfYear());
        assertEquals(a.getDayOfWeek(), localDate.getDayOfWeek());
        
        assertEquals(a.toYear(), localDate.toYear());
        assertEquals(a.toMonthOfYear(), localDate.toMonthOfYear());
        assertEquals(a.toDayOfMonth(), localDate.toDayOfMonth());
        assertEquals(a.toDayOfYear(), localDate.toDayOfYear());
        assertEquals(a.toDayOfWeek(), localDate.toDayOfWeek());
        
        assertEquals(a.getHourOfDay(), localDateTime.getHourOfDay());
        assertEquals(a.getMinuteOfHour(), localDateTime.getMinuteOfHour());
        assertEquals(a.getSecondOfMinute(), localDateTime.getSecondOfMinute());
        assertEquals(a.getNanoOfSecond(), localDateTime.getNanoOfSecond());
        
        assertEquals(a.toHourOfDay(), localDateTime.toHourOfDay());
        assertEquals(a.toMinuteOfHour(), localDateTime.toMinuteOfHour());
        assertEquals(a.toSecondOfMinute(), localDateTime.toSecondOfMinute());
        assertEquals(a.toNanoOfSecond(), localDateTime.toNanoOfSecond());
        
        assertSame(a.toLocalDate(), localDate);
        assertSame(a.toLocalTime(), localTime);
        assertSame(a.toLocalDateTime(), localDateTime);
        assertEquals(a.toOffsetDate(), OffsetDate.date(localDate, offset));
        assertEquals(a.toOffsetTime(), OffsetTime.time(localTime, offset));
        assertEquals(a.toCalendrical(), new Calendrical(localDateTime.toLocalDate(), localDateTime.toLocalTime(), offset, null));
        assertEquals(a.toString(), localDateTime.toString() + offset.toString());
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
        OffsetDateTime test = OffsetDateTime.dateTime(2008, 6, 30, 23, 30, 59, OFFSET_PONE);
        assertEquals(test.get(ISOChronology.hourOfDayRule()), 23);
        assertEquals(test.get(ISOChronology.minuteOfHourRule()), 30);
        assertEquals(test.get(ISOChronology.secondOfMinuteRule()), 59);
        assertEquals(test.get(ISOChronology.hourOfAmPmRule()), 11);
        assertEquals(test.get(ISOChronology.amPmOfDayRule()), 1);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_get_DateTimeFieldRule_null() {
        OffsetDateTime test = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        test.get((DateTimeFieldRule) null);
    }

    @Test(expectedExceptions=UnsupportedCalendarFieldException.class )
    public void test_get_DateTimeFieldRule_unsupported() {
        OffsetDateTime test = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
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
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        LocalDateTime dt = LocalDateTime.dateTime(2008, 6, 30, 11, 31, 0);
        OffsetDateTime test = base.withDateTime(dt);
        assertSame(test.toLocalDateTime(), dt);
        assertSame(test.getOffset(), base.getOffset());
    }

    public void test_withDateTime_noChange() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        LocalDateTime dt = LocalDateTime.dateTime(2008, 6, 30, 11, 30, 59);
        OffsetDateTime test = base.withDateTime(dt);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_withDateTime_null() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        base.withDateTime(null);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_withDateTime_badProvider() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        base.withDateTime(new MockDateTimeProviderReturnsNull());
    }

    //-----------------------------------------------------------------------
    // withOffsetSameLocal()
    //-----------------------------------------------------------------------
    public void test_withOffsetSameLocal() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withOffsetSameLocal(OFFSET_PTWO);
        assertSame(test.toLocalDateTime(), base.toLocalDateTime());
        assertSame(test.getOffset(), OFFSET_PTWO);
    }

    public void test_withOffsetSameLocal_noChange() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withOffsetSameLocal(OFFSET_PONE);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_withOffsetSameLocal_null() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        base.withOffsetSameLocal(null);
    }

    //-----------------------------------------------------------------------
    // withOffsetSameInstant()
    //-----------------------------------------------------------------------
    public void test_withOffsetSameInstant() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withOffsetSameInstant(OFFSET_PTWO);
        OffsetDateTime expected = OffsetDateTime.dateTime(2008, 6, 30, 12, 30, 59, OFFSET_PTWO);
        assertEquals(test, expected);
    }

    public void test_withOffsetSameInstant_noChange() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withOffsetSameInstant(OFFSET_PONE);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_withOffsetSameInstant_null() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        base.withOffsetSameInstant(null);
    }

    //-----------------------------------------------------------------------
    // with(DateAdjuster)
    //-----------------------------------------------------------------------
    public void test_with_DateAdjuster() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.with(Year.isoYear(2007));
        assertEquals(test, OffsetDateTime.dateTime(2007, 6, 30, 11, 30, 59, OFFSET_PONE));
    }

    public void test_with_DateAdjuster_noChange() {
        DateAdjuster adjuster = LocalDate.date(2008, 6, 30);
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 0, 0, OFFSET_PONE);
        OffsetDateTime test = base.with(adjuster);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_with_DateAdjuster_null() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        base.with((DateAdjuster) null);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_with_DateAdjuster_badAdjuster() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        base.with(new MockDateAdjusterReturnsNull());
    }

    //-----------------------------------------------------------------------
    // with(TimeAdjuster)
    //-----------------------------------------------------------------------
    public void test_with_TimeAdjuster() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.with(HourOfDay.hourOfDay(1));
        assertEquals(test, OffsetDateTime.dateTime(2008, 6, 30, 1, 30, 59, OFFSET_PONE));
    }

    public void test_with_TimeAdjuster_noChange() {
        TimeAdjuster adjuster = LocalTime.time(11, 30, 59, 0);
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, 0, OFFSET_PONE);
        OffsetDateTime test = base.with(adjuster);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_with_TimeAdjuster_null() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        base.with((TimeAdjuster) null);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_with_TimeAdjuster_badAdjuster() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        base.with(new MockTimeAdjusterReturnsNull());
    }

    //-----------------------------------------------------------------------
    // withYear()
    //-----------------------------------------------------------------------
    public void test_withYear_normal() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withYear(2007);
        assertEquals(test, OffsetDateTime.dateTime(2007, 6, 30, 11, 30, 59, OFFSET_PONE));
    }

    public void test_withYear_noChange() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withYear(2008);
        assertSame(test, base);
    }

    public void test_withYear_DateResolver_normal() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 2, 29, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withYear(2007, DateResolvers.previousValid());
        assertEquals(test, OffsetDateTime.dateTime(2007, 2, 28, 11, 30, 59, OFFSET_PONE));
    }

    public void test_withYear_DateResolver_noChange() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 2, 29, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withYear(2008, DateResolvers.previousValid());
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withMonthOfYear()
    //-----------------------------------------------------------------------
    public void test_withMonthOfYear_normal() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withMonthOfYear(1);
        assertEquals(test, OffsetDateTime.dateTime(2008, 1, 30, 11, 30, 59, OFFSET_PONE));
    }

    public void test_withMonthOfYear_noChange() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withMonthOfYear(6);
        assertSame(test, base);
    }

    public void test_withMonthOfYear_DateResolver_normal() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withMonthOfYear(2, DateResolvers.previousValid());
        assertEquals(test, OffsetDateTime.dateTime(2008, 2, 29, 11, 30, 59, OFFSET_PONE));
    }

    public void test_withMonthOfYear_DateResolver_noChange() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withMonthOfYear(6, DateResolvers.previousValid());
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withDayOfMonth()
    //-----------------------------------------------------------------------
    public void test_withDayOfMonth_normal() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withDayOfMonth(15);
        assertEquals(test, OffsetDateTime.dateTime(2008, 6, 15, 11, 30, 59, OFFSET_PONE));
    }

    public void test_withDayOfMonth_noChange() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withDayOfMonth(30);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withDate()
    //-----------------------------------------------------------------------
    public void test_withDate() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withDate(2007, 1, 1);
        OffsetDateTime expected = OffsetDateTime.dateTime(2007, 1, 1, 11, 30, 59, OFFSET_PONE);
        assertEquals(test, expected);
    }

    public void test_withDate_noChange() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, OFFSET_PONE);
        OffsetDateTime test = base.withDate(2008, 6, 30);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withHourOfDay()
    //-----------------------------------------------------------------------
    public void test_withHourOfDay_normal() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withHourOfDay(15);
        assertEquals(test, OffsetDateTime.dateTime(2008, 6, 30, 15, 30, 59, OFFSET_PONE));
    }

    public void test_withHourOfDay_noChange() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withHourOfDay(11);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withMinuteOfHour()
    //-----------------------------------------------------------------------
    public void test_withMinuteOfHour_normal() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withMinuteOfHour(15);
        assertEquals(test, OffsetDateTime.dateTime(2008, 6, 30, 11, 15, 59, OFFSET_PONE));
    }

    public void test_withMinuteOfHour_noChange() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withMinuteOfHour(30);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withSecondOfMinute()
    //-----------------------------------------------------------------------
    public void test_withSecondOfMinute_normal() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withSecondOfMinute(15);
        assertEquals(test, OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 15, OFFSET_PONE));
    }

    public void test_withSecondOfMinute_noChange() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withSecondOfMinute(59);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withNanoOfSecond()
    //-----------------------------------------------------------------------
    public void test_withNanoOfSecond_normal() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, 1, OFFSET_PONE);
        OffsetDateTime test = base.withNanoOfSecond(15);
        assertEquals(test, OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, 15, OFFSET_PONE));
    }

    public void test_withNanoOfSecond_noChange() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, 1, OFFSET_PONE);
        OffsetDateTime test = base.withNanoOfSecond(1);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withTime()
    //-----------------------------------------------------------------------
    public void test_withTime_HM() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, 0, OFFSET_PONE);
        OffsetDateTime test = base.withTime(12, 10);
        OffsetDateTime expected = OffsetDateTime.dateTime(2008, 6, 30, 12, 10, 0, 0, OFFSET_PONE);
        assertEquals(test, expected);
    }

    public void test_withTime_HM_noChange() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, OFFSET_PONE);
        OffsetDateTime test = base.withTime(11, 30);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    public void test_withTime_HMS() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, 0, OFFSET_PONE);
        OffsetDateTime test = base.withTime(12, 10, 9);
        OffsetDateTime expected = OffsetDateTime.dateTime(2008, 6, 30, 12, 10, 9, 0, OFFSET_PONE);
        assertEquals(test, expected);
    }

    public void test_withTime_HMS_noChange() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withTime(11, 30, 59);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    public void test_withTime_HMSN() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withTime(12, 10, 9, 8);
        OffsetDateTime expected = OffsetDateTime.dateTime(2008, 6, 30, 12, 10, 9, 8, OFFSET_PONE);
        assertEquals(test, expected);
    }

    public void test_withTime_HMSN_noChange() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, 500, OFFSET_PONE);
        OffsetDateTime test = base.withTime(11, 30, 59, 500);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plus(PeriodProvider)
    //-----------------------------------------------------------------------
    public void test_plus_PeriodProvider() {
        PeriodProvider provider = Period.period(1, 2, 3, 4, 5, 6, 7);
        OffsetDateTime t = TEST_DATE_TIME.plus(provider);
        assertEquals(t, OffsetDateTime.dateTime(2009, 9, 2, 15, 36, 5, 507, OFFSET_PONE));
    }

    public void test_plus_PeriodProvider_zero() {
        OffsetDateTime t = TEST_DATE_TIME.plus(Period.ZERO);
        assertSame(t, TEST_DATE_TIME);
    }

    //-----------------------------------------------------------------------
    // plusYears()
    //-----------------------------------------------------------------------
    public void test_plusYears() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusYears(1);
        assertEquals(test, OffsetDateTime.dateTime(2009, 6, 30, 11, 30, 59, OFFSET_PONE));
    }

    public void test_plusYears_zero() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusYears(0);
        assertSame(test, base);
    }

    public void test_plusYears_DateResolver() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 2, 29, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusYears(1, DateResolvers.previousValid());
        assertEquals(test, OffsetDateTime.dateTime(2009, 2, 28, 11, 30, 59, OFFSET_PONE));
    }

    public void test_plusYears_DateResolver_zero() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusYears(0, DateResolvers.previousValid());
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusMonths()
    //-----------------------------------------------------------------------
    public void test_plusMonths() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusMonths(1);
        assertEquals(test, OffsetDateTime.dateTime(2008, 7, 30, 11, 30, 59, OFFSET_PONE));
    }

    public void test_plusMonths_zero() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusMonths(0);
        assertSame(test, base);
    }

    public void test_plusMonths_DateResolver() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 1, 31, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusMonths(1, DateResolvers.previousValid());
        assertEquals(test, OffsetDateTime.dateTime(2008, 2, 29, 11, 30, 59, OFFSET_PONE));
    }

    public void test_plusMonths_DateResolver_zero() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusMonths(0, DateResolvers.previousValid());
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusWeeks()
    //-----------------------------------------------------------------------
    public void test_plusWeeks() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusWeeks(1);
        assertEquals(test, OffsetDateTime.dateTime(2008, 7, 7, 11, 30, 59, OFFSET_PONE));
    }

    public void test_plusWeeks_zero() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusWeeks(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusDays()
    //-----------------------------------------------------------------------
    public void test_plusDays() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusDays(1);
        assertEquals(test, OffsetDateTime.dateTime(2008, 7, 1, 11, 30, 59, OFFSET_PONE));
    }

    public void test_plusDays_zero() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusDays(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusHours()
    //-----------------------------------------------------------------------
    public void test_plusHours() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusHours(13);
        assertEquals(test, OffsetDateTime.dateTime(2008, 7, 1, 0, 30, 59, OFFSET_PONE));
    }

    public void test_plusHours_zero() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusHours(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusMinutes()
    //-----------------------------------------------------------------------
    public void test_plusMinutes() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusMinutes(30);
        assertEquals(test, OffsetDateTime.dateTime(2008, 6, 30, 12, 0, 59, OFFSET_PONE));
    }

    public void test_plusMinutes_zero() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusMinutes(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusSeconds()
    //-----------------------------------------------------------------------
    public void test_plusSeconds() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusSeconds(1);
        assertEquals(test, OffsetDateTime.dateTime(2008, 6, 30, 11, 31, 0, OFFSET_PONE));
    }

    public void test_plusSeconds_zero() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusSeconds(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusNanos()
    //-----------------------------------------------------------------------
    public void test_plusNanos() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, 0, OFFSET_PONE);
        OffsetDateTime test = base.plusNanos(1);
        assertEquals(test, OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, 1, OFFSET_PONE));
    }

    public void test_plusNanos_zero() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusNanos(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minus(PeriodProvider)
    //-----------------------------------------------------------------------
    public void test_minus_PeriodProvider() {
        PeriodProvider provider = Period.period(1, 2, 3, 4, 5, 6, 7);
        OffsetDateTime t = TEST_DATE_TIME.minus(provider);
        assertEquals(t, OffsetDateTime.dateTime(2007, 4, 27, 7, 25, 53, 493, OFFSET_PONE));
    }

    public void test_minus_PeriodProvider_zero() {
        OffsetDateTime t = TEST_DATE_TIME.minus(Period.ZERO);
        assertSame(t, TEST_DATE_TIME);
    }

    //-----------------------------------------------------------------------
    // minusYears()
    //-----------------------------------------------------------------------
    public void test_minusYears() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusYears(1);
        assertEquals(test, OffsetDateTime.dateTime(2007, 6, 30, 11, 30, 59, OFFSET_PONE));
    }

    public void test_minusYears_zero() {
        OffsetDateTime base = OffsetDateTime.dateTime(2007, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusYears(0);
        assertSame(test, base);
    }

    public void test_minusYears_DateResolver() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 2, 29, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusYears(1, DateResolvers.previousValid());
        assertEquals(test, OffsetDateTime.dateTime(2007, 2, 28, 11, 30, 59, OFFSET_PONE));
    }

    public void test_minusYears_DateResolver_zero() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusYears(0, DateResolvers.previousValid());
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusMonths()
    //-----------------------------------------------------------------------
    public void test_minusMonths() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusMonths(1);
        assertEquals(test, OffsetDateTime.dateTime(2008, 5, 30, 11, 30, 59, OFFSET_PONE));
    }

    public void test_minusMonths_zero() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusMonths(0);
        assertSame(test, base);
    }

    public void test_minusMonths_DateResolver() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 3, 31, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusMonths(1, DateResolvers.previousValid());
        assertEquals(test, OffsetDateTime.dateTime(2008, 2, 29, 11, 30, 59, OFFSET_PONE));
    }

    public void test_minusMonths_DateResolver_zero() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusMonths(0, DateResolvers.previousValid());
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusWeeks()
    //-----------------------------------------------------------------------
    public void test_minusWeeks() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusWeeks(1);
        assertEquals(test, OffsetDateTime.dateTime(2008, 6, 23, 11, 30, 59, OFFSET_PONE));
    }

    public void test_minusWeeks_zero() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusWeeks(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusDays()
    //-----------------------------------------------------------------------
    public void test_minusDays() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusDays(1);
        assertEquals(test, OffsetDateTime.dateTime(2008, 6, 29, 11, 30, 59, OFFSET_PONE));
    }

    public void test_minusDays_zero() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusDays(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusHours()
    //-----------------------------------------------------------------------
    public void test_minusHours() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusHours(13);
        assertEquals(test, OffsetDateTime.dateTime(2008, 6, 29, 22, 30, 59, OFFSET_PONE));
    }

    public void test_minusHours_zero() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusHours(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusMinutes()
    //-----------------------------------------------------------------------
    public void test_minusMinutes() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusMinutes(30);
        assertEquals(test, OffsetDateTime.dateTime(2008, 6, 30, 11, 0, 59, OFFSET_PONE));
    }

    public void test_minusMinutes_zero() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusMinutes(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusSeconds()
    //-----------------------------------------------------------------------
    public void test_minusSeconds() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusSeconds(1);
        assertEquals(test, OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 58, OFFSET_PONE));
    }

    public void test_minusSeconds_zero() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusSeconds(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusNanos()
    //-----------------------------------------------------------------------
    public void test_minusNanos() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, 0, OFFSET_PONE);
        OffsetDateTime test = base.minusNanos(1);
        assertEquals(test, OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 58, 999999999, OFFSET_PONE));
    }

    public void test_minusNanos_zero() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusNanos(0);
        assertSame(test, base);
    }
    //-----------------------------------------------------------------------
    // matches(DateMatcher)
    //-----------------------------------------------------------------------
    public void test_matches_DateMatcher() {
        OffsetDateTime test = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        assertEquals(test.matches(Year.isoYear(2008)), true);
        assertEquals(test.matches(Year.isoYear(2007)), false);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_matches_DateMatcher_null() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        base.matches((DateMatcher) null);
    }

    //-----------------------------------------------------------------------
    // matches(TimeMatcher)
    //-----------------------------------------------------------------------
    public void test_matches_TimeMatcher() {
        OffsetDateTime test = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        assertEquals(test.matches(HourOfDay.hourOfDay(11)), true);
        assertEquals(test.matches(HourOfDay.hourOfDay(10)), false);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_matches_TimeMatcher_null() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        base.matches((TimeMatcher) null);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo_time1() {
        OffsetDateTime a = OffsetDateTime.dateTime(2008, 6, 30, 11, 29, 3, OFFSET_PONE);
        OffsetDateTime b = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 2, OFFSET_PONE);  // a is before b due to time
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    public void test_compareTo_time2() {
        OffsetDateTime a = OffsetDateTime.dateTime(2008, 6, 30, 11, 29, 2, OFFSET_PONE);
        OffsetDateTime b = OffsetDateTime.dateTime(2008, 6, 30, 11, 29, 3, OFFSET_PONE);  // a is before b due to time
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    public void test_compareTo_offset() {
        OffsetDateTime a = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, OFFSET_PTWO);
        OffsetDateTime b = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, OFFSET_PONE);  // a is before b due to offset
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    public void test_compareTo_both() {
        OffsetDateTime a = OffsetDateTime.dateTime(2008, 6, 30, 11, 50, OFFSET_PTWO);
        OffsetDateTime b = OffsetDateTime.dateTime(2008, 6, 30, 11, 20, OFFSET_PONE);  // a is before b on instant scale
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    public void test_compareTo_hourDifference() {
        OffsetDateTime a = OffsetDateTime.dateTime(2008, 6, 30, 10, 0, OFFSET_PONE);
        OffsetDateTime b = OffsetDateTime.dateTime(2008, 6, 30, 11, 0, OFFSET_PTWO);  // a is before b despite being same time-line time
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    public void test_compareTo_max() {
        OffsetDateTime a = OffsetDateTime.dateTime(Year.MAX_YEAR, 12, 31, 23, 59, OFFSET_MONE);
        OffsetDateTime b = OffsetDateTime.dateTime(Year.MAX_YEAR, 12, 31, 23, 59, OFFSET_MTWO);  // a is before b due to offset
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    public void test_compareTo_min() {
        OffsetDateTime a = OffsetDateTime.dateTime(Year.MIN_YEAR, 1, 1, 0, 0, OFFSET_PTWO);
        OffsetDateTime b = OffsetDateTime.dateTime(Year.MIN_YEAR, 1, 1, 0, 0, OFFSET_PONE);  // a is before b due to offset
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_compareTo_null() {
        OffsetDateTime a = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        a.compareTo(null);
    }

    @Test(expectedExceptions=ClassCastException.class)
    @SuppressWarnings("unchecked")
    public void compareToNonOffsetDateTime() {
       Comparable c = TEST_DATE_TIME;
       c.compareTo(new Object());
    }

    //-----------------------------------------------------------------------
    // isAfter() / isBefore() / equalInstant()
    //-----------------------------------------------------------------------
    public void test_isBeforeIsAfterIsEqual1() {
        OffsetDateTime a = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 58, 3, OFFSET_PONE);
        OffsetDateTime b = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, 2, OFFSET_PONE);  // a is before b due to time
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
        OffsetDateTime a = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, 2, OFFSET_PONE);
        OffsetDateTime b = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, 3, OFFSET_PONE);  // a is before b due to time
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
        OffsetDateTime a = OffsetDateTime.dateTime(2008, 6, 30, 10, 0, OFFSET_PONE);
        OffsetDateTime b = OffsetDateTime.dateTime(2008, 6, 30, 11, 0, OFFSET_PTWO);  // a is same instant as b
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
        OffsetDateTime a = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        a.isBefore(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isEqual_null() {
        OffsetDateTime a = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        a.equalInstant(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isAfter_null() {
        OffsetDateTime a = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        a.isAfter(null);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes")
    public void test_equals_true(int y, int o, int d, int h, int m, int s, int n, ZoneOffset ignored) {
        OffsetDateTime a = OffsetDateTime.dateTime(y, o, d, h, m, s, n, OFFSET_PONE);
        OffsetDateTime b = OffsetDateTime.dateTime(y, o, d, h, m, s, n, OFFSET_PONE);
        assertEquals(a.equals(b), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_year_differs(int y, int o, int d, int h, int m, int s, int n, ZoneOffset ignored) {
        OffsetDateTime a = OffsetDateTime.dateTime(y, o, d, h, m, s, n, OFFSET_PONE);
        OffsetDateTime b = OffsetDateTime.dateTime(y + 1, o, d, h, m, s, n, OFFSET_PONE);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_hour_differs(int y, int o, int d, int h, int m, int s, int n, ZoneOffset ignored) {
        h = (h == 23 ? 22 : h);
        OffsetDateTime a = OffsetDateTime.dateTime(y, o, d, h, m, s, n, OFFSET_PONE);
        OffsetDateTime b = OffsetDateTime.dateTime(y, o, d, h + 1, m, s, n, OFFSET_PONE);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_minute_differs(int y, int o, int d, int h, int m, int s, int n, ZoneOffset ignored) {
        m = (m == 59 ? 58 : m);
        OffsetDateTime a = OffsetDateTime.dateTime(y, o, d, h, m, s, n, OFFSET_PONE);
        OffsetDateTime b = OffsetDateTime.dateTime(y, o, d, h, m + 1, s, n, OFFSET_PONE);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_second_differs(int y, int o, int d, int h, int m, int s, int n, ZoneOffset ignored) {
        s = (s == 59 ? 58 : s);
        OffsetDateTime a = OffsetDateTime.dateTime(y, o, d, h, m, s, n, OFFSET_PONE);
        OffsetDateTime b = OffsetDateTime.dateTime(y, o, d, h, m, s + 1, n, OFFSET_PONE);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_nano_differs(int y, int o, int d, int h, int m, int s, int n, ZoneOffset ignored) {
        n = (n == 999999999 ? 999999998 : n);
        OffsetDateTime a = OffsetDateTime.dateTime(y, o, d, h, m, s, n, OFFSET_PONE);
        OffsetDateTime b = OffsetDateTime.dateTime(y, o, d, h, m, s, n + 1, OFFSET_PONE);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_offset_differs(int y, int o, int d, int h, int m, int s, int n, ZoneOffset ignored) {
        OffsetDateTime a = OffsetDateTime.dateTime(y, o, d, h, m, s, n, OFFSET_PONE);
        OffsetDateTime b = OffsetDateTime.dateTime(y, o, d, h, m, s, n, OFFSET_PTWO);
        assertEquals(a.equals(b), false);
    }

    public void test_equals_itself_true() {
        assertEquals(TEST_DATE_TIME.equals(TEST_DATE_TIME), true);
    }

    public void test_equals_string_false() {
        assertEquals(TEST_DATE_TIME.equals("2007-07-15"), false);
    }

    public void test_equals_null_false() {
        assertEquals(TEST_DATE_TIME.equals(null), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleToString")
    Object[][] provider_sampleToString() {
        return new Object[][] {
            {2008, 6, 30, 11, 30, 59, 0, "Z", "2008-06-30T11:30:59Z"},
            {2008, 6, 30, 11, 30, 59, 0, "+01:00", "2008-06-30T11:30:59+01:00"},
            {2008, 6, 30, 11, 30, 59, 999000000, "Z", "2008-06-30T11:30:59.999Z"},
            {2008, 6, 30, 11, 30, 59, 999000000, "+01:00", "2008-06-30T11:30:59.999+01:00"},
            {2008, 6, 30, 11, 30, 59, 999000, "Z", "2008-06-30T11:30:59.000999Z"},
            {2008, 6, 30, 11, 30, 59, 999000, "+01:00", "2008-06-30T11:30:59.000999+01:00"},
            {2008, 6, 30, 11, 30, 59, 999, "Z", "2008-06-30T11:30:59.000000999Z"},
            {2008, 6, 30, 11, 30, 59, 999, "+01:00", "2008-06-30T11:30:59.000000999+01:00"},
        };
    }

    @Test(dataProvider="sampleToString")
    public void test_toString(int y, int o, int d, int h, int m, int s, int n, String offsetId, String expected) {
        OffsetDateTime t = OffsetDateTime.dateTime(y, o, d, h, m, s, n, ZoneOffset.zoneOffset(offsetId));
        String str = t.toString();
        assertEquals(str, expected);
    }

    //-----------------------------------------------------------------------
    // matchesDate()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes")
    public void test_matchesDate_true(int y, int m, int d, int h, int mi, int s, int n, ZoneOffset offset) {
        OffsetDateTime a = TEST_DATE_TIME.withDate(y, m, d);
        LocalDate b = LocalDate.date(y, m, d);
        assertEquals(a.matchesDate(b), true);
    }
    @Test(dataProvider="sampleTimes")
    public void test_matchesDate_false_year_differs(int y, int m, int d, int h, int mi, int s, int n, ZoneOffset offset) {
        OffsetDateTime a = TEST_DATE_TIME.withDate(y, m, d);
        LocalDate b = LocalDate.date(y + 1, m, d);
        assertEquals(a.matchesDate(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_matchesDate_false_month_differs(int y, int m, int d, int h, int mi, int s, int n, ZoneOffset offset) {
        OffsetDateTime a = TEST_DATE_TIME.withDate(y, m, d);
        LocalDate b = LocalDate.date(y, m + 1, d);
        assertEquals(a.matchesDate(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_matchesDate_false_day_differs(int y, int m, int d, int h, int mi, int s, int n, ZoneOffset offset) {
        OffsetDateTime a = TEST_DATE_TIME.withDate(y, m, d);
        LocalDate b = LocalDate.date(y, m, d).plusDays(1);
        assertEquals(a.matchesDate(b), false);
    }

    public void test_matchesDate_itself_true() {
        assertTrue(TEST_DATE_TIME.matchesDate(TEST_DATE_TIME.toLocalDate()));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_matchesDate_null() {
        TEST_DATE_TIME.matchesDate(null);
    }
    
    //-----------------------------------------------------------------------
    // matchesTime()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes")
    public void test_matchesTime_true(int y, int m, int d, int h, int mi, int s, int n, ZoneOffset offset) {
        OffsetDateTime a = TEST_DATE_TIME.withTime(h, mi, s, n);
        LocalTime b = LocalTime.time(h, mi, s, n);
        assertEquals(a.matchesTime(b), true);
    }
    @Test(dataProvider="sampleTimes")
    public void test_matchesTime_false_hour_differs(int y, int m, int d, int h, int mi, int s, int n, ZoneOffset offset) {
        OffsetDateTime a = TEST_DATE_TIME.withTime(h, mi, s, n);
        LocalTime b = LocalTime.time(h, mi, s, n).plusHours(1);
        assertEquals(a.matchesTime(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_matchesTime_false_minute_differs(int y, int m, int d, int h, int mi, int s, int n, ZoneOffset offset) {
        OffsetDateTime a = TEST_DATE_TIME.withTime(h, mi, s, n);
        LocalTime b = LocalTime.time(h, mi, s, n).plusMinutes(1);
        assertEquals(a.matchesTime(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_matchesTime_false_second_differs(int y, int m, int d, int h, int mi, int s, int n, ZoneOffset offset) {
        OffsetDateTime a = TEST_DATE_TIME.withTime(h, mi, s, n);
        LocalTime b = LocalTime.time(h, mi, s, n).plusSeconds(1);
        assertEquals(a.matchesTime(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_matchesTime_false_nano_differs(int y, int m, int d, int h, int mi, int s, int n, ZoneOffset offset) {
        OffsetDateTime a = TEST_DATE_TIME.withTime(h, mi, s, n);
        LocalTime b = LocalTime.time(h, mi, s, n).plusNanos(1);
        assertEquals(a.matchesTime(b), false);
    }

    public void test_matchesTime_itself_true() {
        assertEquals(TEST_DATE_TIME.matchesTime(TEST_DATE_TIME.toLocalTime()), true);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_matchesTime_null() {
        TEST_DATE_TIME.matchesTime(null);
    }
    
    //-----------------------------------------------------------------------
    // adjustDate()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes")
    public void test_adjustDate(int y, int m, int d, int h, int mi, int s, int n, ZoneOffset offset) {
        OffsetDateTime a = TEST_DATE_TIME.withDate(y, m, d);
        assertSame(a.adjustDate(TEST_DATE_TIME.toLocalDate()), a.toLocalDate());
        assertSame(TEST_DATE_TIME.adjustDate(a.toLocalDate()), TEST_DATE_TIME.toLocalDate());
    }

    public void test_adjustDate_same() {
        assertSame(OffsetDateTime.dateMidnight(2008, 6, 30, OFFSET_PONE).adjustDate(TEST_DATE_TIME.toLocalDate()), 
                TEST_DATE_TIME.toLocalDate());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustDate_null() {
        TEST_DATE_TIME.adjustDate(null);
    }
    
    //-----------------------------------------------------------------------
    // adjustTime()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes")
    public void test_adjustTime(int y, int m, int d, int h, int mi, int s, int n, ZoneOffset offset) {
        OffsetDateTime a = TEST_DATE_TIME.withTime(h, mi, s, n);
        assertSame(a.adjustTime(TEST_DATE_TIME.toLocalTime()), a.toLocalTime());
        assertSame(TEST_DATE_TIME.adjustTime(a.toLocalTime()), TEST_DATE_TIME.toLocalTime());
    }

    public void test_adjustTime_same() {
        assertSame(OffsetDateTime.dateTime(1, 1, 1, 11, 30, 59, 500, OFFSET_PONE).adjustTime(TEST_DATE_TIME.toLocalTime()), 
                TEST_DATE_TIME.toLocalTime());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustTime_null() {
        TEST_DATE_TIME.adjustTime(null);
    }
}
