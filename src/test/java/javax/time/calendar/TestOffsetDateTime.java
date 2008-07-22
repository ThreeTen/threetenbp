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
 * Test OffsetDateTime.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestOffsetDateTime {

    private static final ZoneOffset OFFSET_PONE = ZoneOffset.zoneOffset(1);
    private static final ZoneOffset OFFSET_PTWO = ZoneOffset.zoneOffset(2);
    private OffsetDateTime TEST_DATE_TIME;

    @BeforeMethod
    public void setUp() {
        TEST_DATE_TIME = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, 500, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(TEST_DATE_TIME instanceof CalendricalProvider);
        assertTrue(TEST_DATE_TIME instanceof Serializable);
        assertTrue(TEST_DATE_TIME instanceof Comparable);
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
    public void factory_dateMidnight_YMD() {
        OffsetDateTime test = OffsetDateTime.dateMidnight(Year.isoYear(2008), MonthOfYear.JUNE, DayOfMonth.dayOfMonth(30), OFFSET_PONE);
        assertEquals(test.getYear(), Year.isoYear(2008));
        assertEquals(test.getMonthOfYear(), MonthOfYear.JUNE);
        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
    }

    //-----------------------------------------------------------------------
    public void factory_dateMidnight_intMonthInt() {
        OffsetDateTime test = OffsetDateTime.dateMidnight(2008, MonthOfYear.JUNE, 30, OFFSET_PONE);
        assertEquals(test.getYear(), Year.isoYear(2008));
        assertEquals(test.getMonthOfYear(), MonthOfYear.JUNE);
        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
    }

    //-----------------------------------------------------------------------
    public void factory_dateMidnight_ints() {
        OffsetDateTime test = OffsetDateTime.dateMidnight(2008, 6, 30, OFFSET_PONE);
        assertEquals(test.getYear(), Year.isoYear(2008));
        assertEquals(test.getMonthOfYear(), MonthOfYear.JUNE);
        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
    }

    //-----------------------------------------------------------------------
    public void factory_dateMidnight_DateProvider() {
        DateProvider provider = LocalDate.date(2008, 6, 30);
        OffsetDateTime test = OffsetDateTime.dateMidnight(provider, OFFSET_PONE);
        assertEquals(test.getYear(), Year.isoYear(2008));
        assertEquals(test.getMonthOfYear(), MonthOfYear.JUNE);
        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
    }

    //-----------------------------------------------------------------------
    // dateTime factories
    //-----------------------------------------------------------------------
    public void factory_dateTime_objectsHM() {
        OffsetDateTime test = OffsetDateTime.dateTime(
                Year.isoYear(2008), MonthOfYear.monthOfYear(6), DayOfMonth.dayOfMonth(30),
                HourOfDay.hourOfDay(11), MinuteOfHour.minuteOfHour(30), OFFSET_PONE);
        assertEquals(test.getYear(), Year.isoYear(2008));
        assertEquals(test.getMonthOfYear(), MonthOfYear.monthOfYear(6));
        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(0));
        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(0));
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_objectsHMS() {
        OffsetDateTime test = OffsetDateTime.dateTime(
                Year.isoYear(2008), MonthOfYear.monthOfYear(6), DayOfMonth.dayOfMonth(30),
                HourOfDay.hourOfDay(11), MinuteOfHour.minuteOfHour(30), SecondOfMinute.secondOfMinute(10), OFFSET_PONE);
        assertEquals(test.getYear(), Year.isoYear(2008));
        assertEquals(test.getMonthOfYear(), MonthOfYear.monthOfYear(6));
        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(10));
        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(0));
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_objectsHMSN() {
        OffsetDateTime test = OffsetDateTime.dateTime(
                Year.isoYear(2008), MonthOfYear.monthOfYear(6), DayOfMonth.dayOfMonth(30),
                HourOfDay.hourOfDay(11), MinuteOfHour.minuteOfHour(30),
                SecondOfMinute.secondOfMinute(10), NanoOfSecond.nanoOfSecond(500), OFFSET_PONE);
        assertEquals(test.getYear(), Year.isoYear(2008));
        assertEquals(test.getMonthOfYear(), MonthOfYear.monthOfYear(6));
        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(10));
        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(500));
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_intMonthIntHM() {
        OffsetDateTime test = OffsetDateTime.dateTime(2008, MonthOfYear.JUNE, 30, 11, 30, OFFSET_PONE);
        assertEquals(test.getYear(), Year.isoYear(2008));
        assertEquals(test.getMonthOfYear(), MonthOfYear.monthOfYear(6));
        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(0));
        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(0));
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_intMonthIntHMS() {
        OffsetDateTime test = OffsetDateTime.dateTime(2008, MonthOfYear.JUNE, 30, 11, 30, 10, OFFSET_PONE);
        assertEquals(test.getYear(), Year.isoYear(2008));
        assertEquals(test.getMonthOfYear(), MonthOfYear.monthOfYear(6));
        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(10));
        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(0));
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_intMonthIntHMSN() {
        OffsetDateTime test = OffsetDateTime.dateTime(2008, MonthOfYear.JUNE, 30, 11, 30, 10, 500, OFFSET_PONE);
        assertEquals(test.getYear(), Year.isoYear(2008));
        assertEquals(test.getMonthOfYear(), MonthOfYear.monthOfYear(6));
        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(10));
        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(500));
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_intsHM() {
        OffsetDateTime test = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, OFFSET_PONE);
        assertEquals(test.getYear(), Year.isoYear(2008));
        assertEquals(test.getMonthOfYear(), MonthOfYear.monthOfYear(6));
        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(0));
        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(0));
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_intsHMS() {
        OffsetDateTime test = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 10, OFFSET_PONE);
        assertEquals(test.getYear(), Year.isoYear(2008));
        assertEquals(test.getMonthOfYear(), MonthOfYear.monthOfYear(6));
        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(10));
        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(0));
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_intsHMSN() {
        OffsetDateTime test = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 10, 500, OFFSET_PONE);
        assertEquals(test.getYear(), Year.isoYear(2008));
        assertEquals(test.getMonthOfYear(), MonthOfYear.monthOfYear(6));
        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(10));
        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(500));
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_DateProviderTimeProvider() {
        DateProvider dateProvider = LocalDate.date(2008, 6, 30);
        TimeProvider timeProvider = LocalTime.time(11, 30, 10, 500);
        OffsetDateTime test = OffsetDateTime.dateTime(dateProvider, timeProvider, OFFSET_PONE);
        assertEquals(test.getYear(), Year.isoYear(2008));
        assertEquals(test.getMonthOfYear(), MonthOfYear.monthOfYear(6));
        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(10));
        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(500));
    }

    //-----------------------------------------------------------------------
    public void factory_dateTime_DateTimeProvider() {
        DateTimeProvider provider = LocalDateTime.dateTime(2008, 6, 30, 11, 30, 10, 500);
        OffsetDateTime test = OffsetDateTime.dateTime(provider, OFFSET_PONE);
        assertEquals(test.getYear(), Year.isoYear(2008));
        assertEquals(test.getMonthOfYear(), MonthOfYear.monthOfYear(6));
        assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(30));
        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(10));
        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(500));
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
        
        assertEquals(a.getHourOfDay(), localDateTime.getHourOfDay());
        assertEquals(a.getMinuteOfHour(), localDateTime.getMinuteOfHour());
        assertEquals(a.getSecondOfMinute(), localDateTime.getSecondOfMinute());
        assertEquals(a.getNanoOfSecond(), localDateTime.getNanoOfSecond());
        
        assertSame(a.toLocalDate(), localDate);
        assertSame(a.toLocalTime(), localTime);
        assertSame(a.toLocalDateTime(), localDateTime);
        assertEquals(a.toOffsetDate(), OffsetDate.date(localDate, offset));
        assertEquals(a.toOffsetTime(), OffsetTime.time(localTime, offset));
        assertEquals(a.toCalendrical(), Calendrical.calendrical(localDateTime.toLocalDate(), localDateTime.toLocalTime(), offset, null));
        assertEquals(a.toString(), localDateTime.toString() + offset.toString());
    }

    //-----------------------------------------------------------------------
    // isSupported(DateTimeFieldRule)
    //-----------------------------------------------------------------------
    public void test_isSupported() {
        assertEquals(TEST_DATE_TIME.isSupported(ISOChronology.INSTANCE.year()), true);
        assertEquals(TEST_DATE_TIME.isSupported(ISOChronology.INSTANCE.monthOfYear()), true);
        assertEquals(TEST_DATE_TIME.isSupported(ISOChronology.INSTANCE.dayOfMonth()), true);
        assertEquals(TEST_DATE_TIME.isSupported(ISOChronology.INSTANCE.dayOfWeek()), true);
        assertEquals(TEST_DATE_TIME.isSupported(ISOChronology.INSTANCE.dayOfYear()), true);
        
        assertEquals(TEST_DATE_TIME.isSupported(ISOChronology.INSTANCE.hourOfDay()), true);
        assertEquals(TEST_DATE_TIME.isSupported(ISOChronology.INSTANCE.minuteOfHour()), true);
        assertEquals(TEST_DATE_TIME.isSupported(ISOChronology.INSTANCE.secondOfMinute()), true);
        assertEquals(TEST_DATE_TIME.isSupported(ISOChronology.INSTANCE.nanoOfSecond()), true);
        assertEquals(TEST_DATE_TIME.isSupported(ISOChronology.INSTANCE.hourOfAmPm()), true);
    }

    //-----------------------------------------------------------------------
    // get(DateTimeFieldRule)
    //-----------------------------------------------------------------------
    public void test_get_DateTimeFieldRule() {
        OffsetDateTime test = OffsetDateTime.dateTime(2008, 6, 30, 23, 30, 59, OFFSET_PONE);
        assertEquals(test.get(ISOChronology.INSTANCE.hourOfDay()), 23);
        assertEquals(test.get(ISOChronology.INSTANCE.minuteOfHour()), 30);
        assertEquals(test.get(ISOChronology.INSTANCE.secondOfMinute()), 59);
        assertEquals(test.get(ISOChronology.INSTANCE.hourOfAmPm()), 11);
        assertEquals(test.get(ISOChronology.INSTANCE.amPmOfDay()), 1);
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

    //-----------------------------------------------------------------------
    // withOffset()
    //-----------------------------------------------------------------------
    public void test_withOffset() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withOffset(OFFSET_PTWO);
        assertSame(test.toLocalDateTime(), base.toLocalDateTime());
        assertSame(test.getOffset(), OFFSET_PTWO);
    }

    public void test_withOffset_noChange() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withOffset(OFFSET_PONE);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_withOffset_null() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        base.withOffset(null);
    }

    //-----------------------------------------------------------------------
    // adjustLocalDateTime()
    //-----------------------------------------------------------------------
    public void test_adjustLocalDateTime() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.adjustLocalDateTime(OFFSET_PTWO);
        OffsetDateTime expected = OffsetDateTime.dateTime(2008, 6, 30, 12, 30, 59, OFFSET_PTWO);
        assertEquals(test, expected);
    }

    public void test_adjustLocalDateTime_noChange() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.adjustLocalDateTime(OFFSET_PONE);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_adjustLocalDateTime_null() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        base.adjustLocalDateTime(null);
    }

    //-----------------------------------------------------------------------
    // with(DateAdjustor)
    //-----------------------------------------------------------------------
    public void test_with_DateAdjustor() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.with(Year.isoYear(2007));
        assertEquals(test, OffsetDateTime.dateTime(2007, 6, 30, 11, 30, 59, OFFSET_PONE));
    }

    public void test_with_DateAdjustor_noChange() {
        DateAdjustor adjustor = LocalDate.date(2008, 6, 30);
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 0, 0, OFFSET_PONE);
        OffsetDateTime test = base.with(adjustor);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_with_DateAdjustor_null() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        base.with((DateAdjustor) null);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_with_DateAdjustor_badAdjustor() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        base.with(new MockDateAdjusterReturnsNull());
    }

    //-----------------------------------------------------------------------
    // with(TimeAdjustor)
    //-----------------------------------------------------------------------
    public void test_with_TimeAdjustor() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.with(HourOfDay.hourOfDay(1));
        assertEquals(test, OffsetDateTime.dateTime(2008, 6, 30, 1, 30, 59, OFFSET_PONE));
    }

    public void test_with_TimeAdjustor_noChange() {
        TimeAdjustor adjustor = LocalTime.time(11, 30, 59, 0);
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, 0, OFFSET_PONE);
        OffsetDateTime test = base.with(adjustor);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_with_TimeAdjustor_null() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        base.with((TimeAdjustor) null);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_with_TimeAdjustor_badAdjustor() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        base.with(new MockTimeAdjusterReturnsNull());
    }

    //-----------------------------------------------------------------------
    // withYear()
    //-----------------------------------------------------------------------
    public void test_withYearr_normal() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withYear(2007);
        assertEquals(test, OffsetDateTime.dateTime(2007, 6, 30, 11, 30, 59, OFFSET_PONE));
    }

    public void test_withYear_noChange() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withYear(2008);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withMonthOfYear()
    //-----------------------------------------------------------------------
    public void test_withMonthOfYearr_normal() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withMonthOfYear(1);
        assertEquals(test, OffsetDateTime.dateTime(2008, 1, 30, 11, 30, 59, OFFSET_PONE));
    }

    public void test_withMonthOfYear_noChange() {
        OffsetDateTime base = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withMonthOfYear(6);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withDayOfMonth()
    //-----------------------------------------------------------------------
    public void test_withDayOfMonthr_normal() {
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
    public void test_withHourOfDayr_normal() {
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
    public void test_compareTo_time() {
        OffsetDateTime a = OffsetDateTime.dateTime(2008, 6, 30, 11, 29, OFFSET_PONE);
        OffsetDateTime b = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, OFFSET_PONE);  // a is before b due to time
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

    @Test(expectedExceptions=NullPointerException.class)
    public void test_compareTo_null() {
        OffsetDateTime a = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        a.compareTo(null);
    }

    //-----------------------------------------------------------------------
    // isAfter() / isBefore()
    //-----------------------------------------------------------------------
    public void test_isBeforeIsAfter() {
        OffsetDateTime a = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 58, OFFSET_PONE);
        OffsetDateTime b = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);  // a is before b due to time
        assertEquals(a.isBefore(b), true);
        assertEquals(a.isAfter(b), false);
        assertEquals(b.isBefore(a), false);
        assertEquals(b.isAfter(a), true);
        assertEquals(a.isBefore(a), false);
        assertEquals(b.isBefore(b), false);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isBefore_null() {
        OffsetDateTime a = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        a.isBefore(null);
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

}
