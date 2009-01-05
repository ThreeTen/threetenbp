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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static org.testng.Assert.*;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import javax.time.calendar.field.HourOfDay;
import javax.time.calendar.field.MinuteOfHour;
import javax.time.calendar.field.NanoOfSecond;
import javax.time.calendar.field.SecondOfMinute;
import javax.time.period.MockPeriodProviderReturnsNull;
import javax.time.period.Period;
import javax.time.period.PeriodProvider;

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

    private static final ZoneOffset OFFSET_PONE = ZoneOffset.zoneOffset(1);
    private static final ZoneOffset OFFSET_PTWO = ZoneOffset.zoneOffset(2);
    private OffsetTime TEST_TIME;

    @BeforeMethod
    public void setUp() {
        TEST_TIME = OffsetTime.time(11, 30, 59, 500, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(TEST_TIME instanceof CalendricalProvider);
        assertTrue(TEST_TIME instanceof Serializable);
        assertTrue(TEST_TIME instanceof Comparable);
        assertTrue(TEST_TIME instanceof TimeMatcher);
        assertTrue(TEST_TIME instanceof TimeAdjuster);
        assertTrue(TEST_TIME instanceof TimeProvider);
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(TEST_TIME);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), TEST_TIME);
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
    // factories
    //-----------------------------------------------------------------------
    public void factory_objectsHM() {
        OffsetTime test = OffsetTime.time(HourOfDay.hourOfDay(11), MinuteOfHour.minuteOfHour(30), OFFSET_PONE);
        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(0));
        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(0));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_objectsHM_nullHour() {
        OffsetTime.time(null, MinuteOfHour.minuteOfHour(30), OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_objectsHM_nullMinute() {
        OffsetTime.time(HourOfDay.hourOfDay(11), null, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_objectsHM_nullOffset() {
        OffsetTime.time(HourOfDay.hourOfDay(11), MinuteOfHour.minuteOfHour(30), null);
    }

    //-----------------------------------------------------------------------
    public void factory_objectsHMS() {
        OffsetTime test = OffsetTime.time(HourOfDay.hourOfDay(11), MinuteOfHour.minuteOfHour(30), SecondOfMinute.secondOfMinute(10), OFFSET_PONE);
        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(10));
        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(0));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_objectsHMS_nullHour() {
        OffsetTime.time(null, MinuteOfHour.minuteOfHour(30), SecondOfMinute.secondOfMinute(10), OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_objectsHMS_nullMinute() {
        OffsetTime.time(HourOfDay.hourOfDay(11), null, SecondOfMinute.secondOfMinute(10), OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_objectsHMS_nullSecond() {
        OffsetTime.time(HourOfDay.hourOfDay(11), MinuteOfHour.minuteOfHour(30), null, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_objectsHMS_nullOffset() {
        OffsetTime.time(HourOfDay.hourOfDay(11), MinuteOfHour.minuteOfHour(30), SecondOfMinute.secondOfMinute(10), null);
    }

    //-----------------------------------------------------------------------
    public void factory_objectsHMSN() {
        OffsetTime test = OffsetTime.time(HourOfDay.hourOfDay(11), MinuteOfHour.minuteOfHour(30),
                SecondOfMinute.secondOfMinute(10), NanoOfSecond.nanoOfSecond(500), OFFSET_PONE);
        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(10));
        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(500));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_objectsHMSN_nullHour() {
        OffsetTime.time(null, MinuteOfHour.minuteOfHour(30), SecondOfMinute.secondOfMinute(10), NanoOfSecond.nanoOfSecond(500), 
                OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_objectsHMSN_nullMinute() {
        OffsetTime.time(HourOfDay.hourOfDay(11), null, SecondOfMinute.secondOfMinute(10), NanoOfSecond.nanoOfSecond(500), 
                OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_objectsHMSN_nullSecond() {
        OffsetTime.time(HourOfDay.hourOfDay(11), MinuteOfHour.minuteOfHour(30), null, NanoOfSecond.nanoOfSecond(500), OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_objectsHMSN_nullNano() {
        OffsetTime.time(HourOfDay.hourOfDay(11), MinuteOfHour.minuteOfHour(30), SecondOfMinute.secondOfMinute(10), null, 
                OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_objectsHMSN_nullOffset() {
        OffsetTime.time(HourOfDay.hourOfDay(11), MinuteOfHour.minuteOfHour(30), SecondOfMinute.secondOfMinute(10), 
                NanoOfSecond.nanoOfSecond(500), null);
    }

    //-----------------------------------------------------------------------
    public void factory_intsHM() {
        OffsetTime test = OffsetTime.time(11, 30, OFFSET_PONE);
        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(0));
        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(0));
    }

    //-----------------------------------------------------------------------
    public void factory_intsHMS() {
        OffsetTime test = OffsetTime.time(11, 30, 10, OFFSET_PONE);
        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(10));
        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(0));
    }

    //-----------------------------------------------------------------------
    public void factory_intsHMSN() {
        OffsetTime test = OffsetTime.time(11, 30, 10, 500, OFFSET_PONE);
        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(10));
        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(500));
    }

    //-----------------------------------------------------------------------
    public void factory_TimeProvider() {
        TimeProvider localTime = LocalTime.time(11, 30, 10, 500);
        OffsetTime test = OffsetTime.time(localTime, OFFSET_PONE);
        assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(11));
        assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour(30));
        assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(10));
        assertEquals(test.getNanoOfSecond(), NanoOfSecond.nanoOfSecond(500));
    }

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
            con.newInstance(LocalTime.time(11, 30), null);
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
        LocalTime localTime = LocalTime.time(h, m, s, n);
        OffsetTime a = OffsetTime.time(localTime, offset);
        assertSame(a.getTime(), localTime);
        assertSame(a.getOffset(), offset);
        assertEquals(a.getChronology(), ISOChronology.INSTANCE);
        
        assertEquals(a.getHourOfDay(), localTime.getHourOfDay());
        assertEquals(a.getMinuteOfHour(), localTime.getMinuteOfHour());
        assertEquals(a.getSecondOfMinute(), localTime.getSecondOfMinute());
        assertEquals(a.getNanoOfSecond(), localTime.getNanoOfSecond());
        
        assertSame(a.toLocalTime(), localTime);
        assertEquals(a.toCalendrical(), new Calendrical(null, localTime, offset, null));
        assertEquals(a.toString(), localTime.toString() + offset.toString());
    }

    //-----------------------------------------------------------------------
    // isSupported(TimeTimeFieldRule)
    //-----------------------------------------------------------------------
    public void test_isSupported() {
        assertEquals(TEST_TIME.isSupported(ISOChronology.yearRule()), false);
        assertEquals(TEST_TIME.isSupported(ISOChronology.monthOfYearRule()), false);
        assertEquals(TEST_TIME.isSupported(ISOChronology.dayOfMonthRule()), false);
        assertEquals(TEST_TIME.isSupported(ISOChronology.dayOfWeekRule()), false);
        assertEquals(TEST_TIME.isSupported(ISOChronology.dayOfYearRule()), false);
        
        assertEquals(TEST_TIME.isSupported(ISOChronology.hourOfDayRule()), true);
        assertEquals(TEST_TIME.isSupported(ISOChronology.minuteOfHourRule()), true);
        assertEquals(TEST_TIME.isSupported(ISOChronology.secondOfMinuteRule()), true);
        assertEquals(TEST_TIME.isSupported(ISOChronology.nanoOfSecondRule()), true);
        assertEquals(TEST_TIME.isSupported(ISOChronology.hourOfAmPmRule()), true);
    }

    //-----------------------------------------------------------------------
    // get(DateTimeFieldRule)
    //-----------------------------------------------------------------------
    public void test_get_DateTimeFieldRule() {
        OffsetTime test = OffsetTime.time(23, 30, 59, OFFSET_PONE);
        assertEquals(test.get(ISOChronology.hourOfDayRule()), 23);
        assertEquals(test.get(ISOChronology.minuteOfHourRule()), 30);
        assertEquals(test.get(ISOChronology.secondOfMinuteRule()), 59);
        assertEquals(test.get(ISOChronology.hourOfAmPmRule()), 11);
        assertEquals(test.get(ISOChronology.amPmOfDayRule()), 1);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_get_DateTimeFieldRule_null() {
        OffsetTime test = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        test.get((DateTimeFieldRule) null);
    }

    @Test(expectedExceptions=UnsupportedCalendarFieldException.class )
    public void test_get_DateTimeFieldRule_unsupported() {
        OffsetTime test = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        try {
            test.get(MockRuleNoValue.INSTANCE);
        } catch (UnsupportedCalendarFieldException ex) {
            assertEquals(ex.getFieldRule(), MockRuleNoValue.INSTANCE);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // withTime()
    //-----------------------------------------------------------------------
    public void test_withTime() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        LocalTime time = LocalTime.time(11, 31, 0);
        OffsetTime test = base.withTime(time);
        assertSame(test.toLocalTime(), time);
        assertSame(test.getOffset(), base.getOffset());
    }

    public void test_withTime_noChange() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        LocalTime time = LocalTime.time(11, 30, 59);
        OffsetTime test = base.withTime(time);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_withTime_null() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        base.withTime(null);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_withTime_badProvider() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        base.withTime(new MockTimeProviderReturnsNull());
    }

    //-----------------------------------------------------------------------
    // withOffset()
    //-----------------------------------------------------------------------
    public void test_withOffset() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.withOffset(OFFSET_PTWO);
        assertSame(test.toLocalTime(), base.toLocalTime());
        assertSame(test.getOffset(), OFFSET_PTWO);
    }

    public void test_withOffset_noChange() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.withOffset(OFFSET_PONE);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_withOffset_null() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        base.withOffset(null);
    }

    //-----------------------------------------------------------------------
    // adjustLocalTime()
    //-----------------------------------------------------------------------
    public void test_adjustLocalTime() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.adjustLocalTime(OFFSET_PTWO);
        OffsetTime expected = OffsetTime.time(12, 30, 59, OFFSET_PTWO);
        assertEquals(test, expected);
    }

    public void test_adjustLocalTime_noChange() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.adjustLocalTime(OFFSET_PONE);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_adjustLocalTime_null() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        base.adjustLocalTime(null);
    }

    //-----------------------------------------------------------------------
    // with()
    //-----------------------------------------------------------------------
    public void test_with() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.with(HourOfDay.hourOfDay(1));
        assertEquals(test.toLocalTime(), LocalTime.time(1, 30, 59));
        assertSame(test.getOffset(), base.getOffset());
    }

    public void test_with_noChange() {
        LocalTime time = LocalTime.time(11, 30, 59);
        OffsetTime base = OffsetTime.time(time, OFFSET_PONE);
        OffsetTime test = base.with(time);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_with_null() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        base.with(null);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_with_badAdjuster() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        base.with(new MockTimeAdjusterReturnsNull());
    }

    //-----------------------------------------------------------------------
    // withHourOfDay()
    //-----------------------------------------------------------------------
    public void test_withHourOfDay_normal() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.withHourOfDay(15);
        assertEquals(test, OffsetTime.time(15, 30, 59, OFFSET_PONE));
    }

    public void test_withHourOfDay_noChange() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.withHourOfDay(11);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withMinuteOfHour()
    //-----------------------------------------------------------------------
    public void test_withMinuteOfHour_normal() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.withMinuteOfHour(15);
        assertEquals(test, OffsetTime.time(11, 15, 59, OFFSET_PONE));
    }

    public void test_withMinuteOfHour_noChange() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.withMinuteOfHour(30);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withSecondOfMinute()
    //-----------------------------------------------------------------------
    public void test_withSecondOfMinute_normal() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.withSecondOfMinute(15);
        assertEquals(test, OffsetTime.time(11, 30, 15, OFFSET_PONE));
    }

    public void test_withSecondOfMinute_noChange() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.withSecondOfMinute(59);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withNanoOfSecond()
    //-----------------------------------------------------------------------
    public void test_withNanoOfSecond_normal() {
        OffsetTime base = OffsetTime.time(11, 30, 59, 1, OFFSET_PONE);
        OffsetTime test = base.withNanoOfSecond(15);
        assertEquals(test, OffsetTime.time(11, 30, 59, 15, OFFSET_PONE));
    }

    public void test_withNanoOfSecond_noChange() {
        OffsetTime base = OffsetTime.time(11, 30, 59, 1, OFFSET_PONE);
        OffsetTime test = base.withNanoOfSecond(1);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plus(PeriodProvider)
    //-----------------------------------------------------------------------
    public void test_plus_PeriodProvider() {
        PeriodProvider provider = Period.hoursMinutesSeconds(1, 2, 3);
        OffsetTime t = TEST_TIME.plus(provider);
        assertEquals(t, OffsetTime.time(12, 33, 2, 500, OFFSET_PONE));
    }

    public void test_plus_PeriodProvider_zero() {
        OffsetTime t = TEST_TIME.plus(Period.ZERO);
        assertSame(t, TEST_TIME);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_plus_PeriodProvider_null() {
        TEST_TIME.plus((PeriodProvider) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_plus_PeriodProvider_badProvider() {
        TEST_TIME.plus(new MockPeriodProviderReturnsNull());
    }

    //-----------------------------------------------------------------------
    // plusHours()
    //-----------------------------------------------------------------------
    public void test_plusHours() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.plusHours(13);
        assertEquals(test, OffsetTime.time(0, 30, 59, OFFSET_PONE));
    }

    public void test_plusHours_zero() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.plusHours(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusMinutes()
    //-----------------------------------------------------------------------
    public void test_plusMinutes() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.plusMinutes(30);
        assertEquals(test, OffsetTime.time(12, 0, 59, OFFSET_PONE));
    }

    public void test_plusMinutes_zero() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.plusMinutes(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusSeconds()
    //-----------------------------------------------------------------------
    public void test_plusSeconds() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.plusSeconds(1);
        assertEquals(test, OffsetTime.time(11, 31, 0, OFFSET_PONE));
    }

    public void test_plusSeconds_zero() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.plusSeconds(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusNanos()
    //-----------------------------------------------------------------------
    public void test_plusNanos() {
        OffsetTime base = OffsetTime.time(11, 30, 59, 0, OFFSET_PONE);
        OffsetTime test = base.plusNanos(1);
        assertEquals(test, OffsetTime.time(11, 30, 59, 1, OFFSET_PONE));
    }

    public void test_plusNanos_zero() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.plusNanos(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minus(PeriodProvider)
    //-----------------------------------------------------------------------
    public void test_minus_PeriodProvider() {
        PeriodProvider provider = Period.hoursMinutesSeconds(1, 2, 3);
        OffsetTime t = TEST_TIME.minus(provider);
        assertEquals(t, OffsetTime.time(10, 28, 56, 500, OFFSET_PONE));
    }

    public void test_minus_PeriodProvider_zero() {
        OffsetTime t = TEST_TIME.minus(Period.ZERO);
        assertSame(t, TEST_TIME);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_minus_PeriodProvider_null() {
        TEST_TIME.minus((PeriodProvider) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_minus_PeriodProvider_badProvider() {
        TEST_TIME.minus(new MockPeriodProviderReturnsNull());
    }

    //-----------------------------------------------------------------------
    // minusHours()
    //-----------------------------------------------------------------------
    public void test_minusHours() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.minusHours(-13);
        assertEquals(test, OffsetTime.time(0, 30, 59, OFFSET_PONE));
    }

    public void test_minusHours_zero() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.minusHours(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusMinutes()
    //-----------------------------------------------------------------------
    public void test_minusMinutes() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.minusMinutes(50);
        assertEquals(test, OffsetTime.time(10, 40, 59, OFFSET_PONE));
    }

    public void test_minusMinutes_zero() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.minusMinutes(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusSeconds()
    //-----------------------------------------------------------------------
    public void test_minusSeconds() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.minusSeconds(60);
        assertEquals(test, OffsetTime.time(11, 29, 59, OFFSET_PONE));
    }

    public void test_minusSeconds_zero() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.minusSeconds(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusNanos()
    //-----------------------------------------------------------------------
    public void test_minusNanos() {
        OffsetTime base = OffsetTime.time(11, 30, 59, 0, OFFSET_PONE);
        OffsetTime test = base.minusNanos(1);
        assertEquals(test, OffsetTime.time(11, 30, 58, 999999999, OFFSET_PONE));
    }

    public void test_minusNanos_zero() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.minusNanos(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // matches()
    //-----------------------------------------------------------------------
    public void test_matches() {
        OffsetTime test = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        assertTrue(test.matches(HourOfDay.hourOfDay(11)));
        assertFalse(test.matches(HourOfDay.hourOfDay(10)));
        assertTrue(test.matches(MinuteOfHour.minuteOfHour(30)));
        assertFalse(test.matches(MinuteOfHour.minuteOfHour(0)));
        assertTrue(test.matches(SecondOfMinute.secondOfMinute(59)));
        assertFalse(test.matches(SecondOfMinute.secondOfMinute(50)));
        assertTrue(test.matches(NanoOfSecond.nanoOfSecond(0)));
        assertFalse(test.matches(NanoOfSecond.nanoOfSecond(1)));
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_matches_null() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        base.matches(null);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo_time() {
        OffsetTime a = OffsetTime.time(11, 29, OFFSET_PONE);
        OffsetTime b = OffsetTime.time(11, 30, OFFSET_PONE);  // a is before b due to time
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    public void test_compareTo_offset() {
        OffsetTime a = OffsetTime.time(11, 30, OFFSET_PTWO);
        OffsetTime b = OffsetTime.time(11, 30, OFFSET_PONE);  // a is before b due to offset
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    public void test_compareTo_both() {
        OffsetTime a = OffsetTime.time(11, 50, OFFSET_PTWO);
        OffsetTime b = OffsetTime.time(11, 20, OFFSET_PONE);  // a is before b on instant scale
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    public void test_compareTo_hourDifference() {
        OffsetTime a = OffsetTime.time(10, 0, OFFSET_PONE);
        OffsetTime b = OffsetTime.time(11, 0, OFFSET_PTWO);  // a is before b despite being same time-line time
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_compareTo_null() {
        OffsetTime a = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        a.compareTo(null);
    }

    @Test(expectedExceptions=ClassCastException.class)
    @SuppressWarnings("unchecked")
    public void compareToNonOffsetTime() {
       Comparable c = TEST_TIME;
       c.compareTo(new Object());
    }

    //-----------------------------------------------------------------------
    // isAfter() / isBefore()
    //-----------------------------------------------------------------------
    public void test_isBeforeIsAfter() {
        OffsetTime a = OffsetTime.time(11, 30, 58, OFFSET_PONE);
        OffsetTime b = OffsetTime.time(11, 30, 59, OFFSET_PONE);  // a is before b due to time
        assertEquals(a.isBefore(b), true);
        assertEquals(a.isAfter(b), false);
        assertEquals(b.isBefore(a), false);
        assertEquals(b.isAfter(a), true);
        assertEquals(a.isBefore(a), false);
        assertEquals(b.isBefore(b), false);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isBefore_null() {
        OffsetTime a = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        a.isBefore(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isAfter_null() {
        OffsetTime a = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        a.isAfter(null);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes")
    public void test_equals_true(int h, int m, int s, int n, ZoneOffset ignored) {
        OffsetTime a = OffsetTime.time(h, m, s, n, OFFSET_PONE);
        OffsetTime b = OffsetTime.time(h, m, s, n, OFFSET_PONE);
        assertEquals(a.equals(b), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_hour_differs(int h, int m, int s, int n, ZoneOffset ignored) {
        h = (h == 23 ? 22 : h);
        OffsetTime a = OffsetTime.time(h, m, s, n, OFFSET_PONE);
        OffsetTime b = OffsetTime.time(h + 1, m, s, n, OFFSET_PONE);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_minute_differs(int h, int m, int s, int n, ZoneOffset ignored) {
        m = (m == 59 ? 58 : m);
        OffsetTime a = OffsetTime.time(h, m, s, n, OFFSET_PONE);
        OffsetTime b = OffsetTime.time(h, m + 1, s, n, OFFSET_PONE);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_second_differs(int h, int m, int s, int n, ZoneOffset ignored) {
        s = (s == 59 ? 58 : s);
        OffsetTime a = OffsetTime.time(h, m, s, n, OFFSET_PONE);
        OffsetTime b = OffsetTime.time(h, m, s + 1, n, OFFSET_PONE);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_nano_differs(int h, int m, int s, int n, ZoneOffset ignored) {
        n = (n == 999999999 ? 999999998 : n);
        OffsetTime a = OffsetTime.time(h, m, s, n, OFFSET_PONE);
        OffsetTime b = OffsetTime.time(h, m, s, n + 1, OFFSET_PONE);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_offset_differs(int h, int m, int s, int n, ZoneOffset ignored) {
        OffsetTime a = OffsetTime.time(h, m, s, n, OFFSET_PONE);
        OffsetTime b = OffsetTime.time(h, m, s, n, OFFSET_PTWO);
        assertEquals(a.equals(b), false);
    }

    public void test_equals_itself_true() {
        assertEquals(TEST_TIME.equals(TEST_TIME), true);
    }

    public void test_equals_string_false() {
        assertEquals(TEST_TIME.equals("2007-07-15"), false);
    }

    public void test_equals_null_false() {
        assertEquals(TEST_TIME.equals(null), false);
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
        OffsetTime t = OffsetTime.time(h, m, s, n, ZoneOffset.zoneOffset(offsetId));
        String str = t.toString();
        assertEquals(str, expected);
    }

    //-----------------------------------------------------------------------
    // matchesTime()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes")
    public void test_matchesTime_true(int h, int m, int s, int n, ZoneOffset offset) {
        OffsetTime a = OffsetTime.time(h, m, s, n, offset);
        LocalTime b = LocalTime.time(h, m, s, n);
        assertEquals(a.matchesTime(b), true);
    }
    @Test(dataProvider="sampleTimes")
    public void test_matchesTime_false_hour_differs(int h, int m, int s, int n, ZoneOffset offset) {
        OffsetTime a = OffsetTime.time(h, m, s, n, offset);
        LocalTime b = LocalTime.time(h, m, s, n).plusHours(1);
        assertEquals(a.matchesTime(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_matchesTime_false_minute_differs(int h, int m, int s, int n, ZoneOffset offset) {
        OffsetTime a = OffsetTime.time(h, m, s, n, offset);
        LocalTime b = LocalTime.time(h, m, s, n).plusMinutes(1);
        assertEquals(a.matchesTime(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_matchesTime_false_second_differs(int h, int m, int s, int n, ZoneOffset offset) {
        OffsetTime a = OffsetTime.time(h, m, s, n, offset);
        LocalTime b = LocalTime.time(h, m, s, n).plusSeconds(1);
        assertEquals(a.matchesTime(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_matchesTime_false_nano_differs(int h, int m, int s, int n, ZoneOffset offset) {
        OffsetTime a = OffsetTime.time(h, m, s, n, offset);
        LocalTime b = LocalTime.time(h, m, s, n).plusNanos(1);
        assertEquals(a.matchesTime(b), false);
    }

    public void test_matchesTime_itself_true() {
        assertEquals(TEST_TIME.matchesTime(TEST_TIME.getTime()), true);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_matchesTime_null() {
        TEST_TIME.matchesTime(null);
    }
    
    //-----------------------------------------------------------------------
    // adjustTime()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes")
    public void test_adjustTime(int h, int m, int s, int n, ZoneOffset ignored) {
        LocalTime a = LocalTime.time(h, m, s, n);
        assertSame(a.adjustTime(TEST_TIME.getTime()), a);
        assertSame(TEST_TIME.adjustTime(a), TEST_TIME.getTime());
    }

    public void test_adjustTime_same() {
        assertSame(OffsetTime.time(11, 30, 59, 500, OFFSET_PTWO).adjustTime(TEST_TIME.getTime()), TEST_TIME.getTime());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustTime_null() {
        TEST_TIME.adjustTime(null);
    }
}
