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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.time.Duration;
import javax.time.period.PeriodField;

import org.testng.annotations.Test;

/**
 * Test ISOChronology.
 *
 * @author Michael Nascimento Santos
 */
@Test
public class TestISOChronology {

    @SuppressWarnings("unchecked")
    public void test_constructor() throws Exception {
        for (Constructor constructor : ISOChronology.class.getDeclaredConstructors()) {
            assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        }
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        Object chronology = ISOChronology.INSTANCE;
        assertTrue(chronology instanceof Serializable);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(chronology);
        oos.close();
        
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertSame(ois.readObject(), chronology);
    }

    public void test_immutable() throws Exception {
        Class<ISOChronology> cls = ISOChronology.class;
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) == false) {
                assertTrue(Modifier.isPrivate(field.getModifiers()));
                assertTrue(Modifier.isFinal(field.getModifiers()));
            }
        }
    }

    //-----------------------------------------------------------------------
//    public void test_twoDigitYearRule() throws Exception {
//        DateTimeFieldRule rule = ISOChronology.twoDigitYearRule();
//        assertEquals(rule.getID(), "ISO.TwoDigitYear");
//        assertEquals(rule.getName(), "TwoDigitYear");
//        assertEquals(rule.getMinimumValue(), 0);
//        assertEquals(rule.getLargestMinimumValue(), 0);
//        assertEquals(rule.getMaximumValue(), 99);
//        assertEquals(rule.getSmallestMaximumValue(), 99);
//        assertEquals(rule.isFixedValueSet(), true);
//        assertEquals(rule.getPeriodUnit(), ISOChronology.YEARS);
//        assertEquals(rule.getPeriodRange(), ISOChronology.CENTURIES);
//        serialize(rule);
//    }

    //-----------------------------------------------------------------------
    public void test_hourOfDayRule() throws Exception {
        DateTimeFieldRule<Integer> rule = ISOChronology.hourOfDayRule();
        assertEquals(rule.getReifiedType(), Integer.class);
        assertEquals(rule.getID(), "ISO.HourOfDay");
        assertEquals(rule.getName(), "HourOfDay");
        assertEquals(rule.getMinimumValue(), 0);
        assertEquals(rule.getLargestMinimumValue(), 0);
        assertEquals(rule.getMaximumValue(), 23);
        assertEquals(rule.getSmallestMaximumValue(), 23);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), ISOChronology.periodHours());
        assertEquals(rule.getPeriodRange(), ISOChronology.periodDays());
        serialize(rule);
    }

    public void test_minuteOfHourRule() throws Exception {
        DateTimeFieldRule<Integer> rule = ISOChronology.minuteOfHourRule();
        assertEquals(rule.getReifiedType(), Integer.class);
        assertEquals(rule.getID(), "ISO.MinuteOfHour");
        assertEquals(rule.getName(), "MinuteOfHour");
        assertEquals(rule.getMinimumValue(), 0);
        assertEquals(rule.getLargestMinimumValue(), 0);
        assertEquals(rule.getMaximumValue(), 59);
        assertEquals(rule.getSmallestMaximumValue(), 59);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), ISOChronology.periodMinutes());
        assertEquals(rule.getPeriodRange(), ISOChronology.periodHours());
        serialize(rule);
    }

    public void test_secondOfMinuteRule() throws Exception {
        DateTimeFieldRule<Integer> rule = ISOChronology.secondOfMinuteRule();
        assertEquals(rule.getReifiedType(), Integer.class);
        assertEquals(rule.getID(), "ISO.SecondOfMinute");
        assertEquals(rule.getName(), "SecondOfMinute");
        assertEquals(rule.getMinimumValue(), 0);
        assertEquals(rule.getLargestMinimumValue(), 0);
        assertEquals(rule.getMaximumValue(), 59);
        assertEquals(rule.getSmallestMaximumValue(), 59);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), ISOChronology.periodSeconds());
        assertEquals(rule.getPeriodRange(), ISOChronology.periodMinutes());
        serialize(rule);
    }

    public void test_nanoOfSecondRule() throws Exception {
        DateTimeFieldRule<Integer> rule = ISOChronology.nanoOfSecondRule();
        assertEquals(rule.getReifiedType(), Integer.class);
        assertEquals(rule.getID(), "ISO.NanoOfSecond");
        assertEquals(rule.getName(), "NanoOfSecond");
        assertEquals(rule.getMinimumValue(), 0);
        assertEquals(rule.getLargestMinimumValue(), 0);
        assertEquals(rule.getMaximumValue(), 999999999);
        assertEquals(rule.getSmallestMaximumValue(), 999999999);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), ISOChronology.periodNanos());
        assertEquals(rule.getPeriodRange(), ISOChronology.periodSeconds());
        serialize(rule);
    }

    //-----------------------------------------------------------------------
    public void test_secondOfDayRule() throws Exception {
        DateTimeFieldRule<Integer> rule = ISOChronology.secondOfDayRule();
        assertEquals(rule.getReifiedType(), Integer.class);
        assertEquals(rule.getID(), "ISO.SecondOfDay");
        assertEquals(rule.getName(), "SecondOfDay");
        assertEquals(rule.getMinimumValue(), 0);
        assertEquals(rule.getLargestMinimumValue(), 0);
        assertEquals(rule.getMaximumValue(), 24 * 60 * 60 - 1);
        assertEquals(rule.getSmallestMaximumValue(), 24 * 60 * 60 - 1);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), ISOChronology.periodSeconds());
        assertEquals(rule.getPeriodRange(), ISOChronology.periodDays());
        serialize(rule);
    }

    public void test_milliOfDayRule() throws Exception {
        DateTimeFieldRule<Integer> rule = ISOChronology.milliOfDayRule();
        assertEquals(rule.getReifiedType(), Integer.class);
        assertEquals(rule.getID(), "ISO.MilliOfDay");
        assertEquals(rule.getName(), "MilliOfDay");
        assertEquals(rule.getMinimumValue(), 0);
        assertEquals(rule.getLargestMinimumValue(), 0);
        assertEquals(rule.getMaximumValue(), 24 * 60 * 60 * 1000 - 1);
        assertEquals(rule.getSmallestMaximumValue(), 24 * 60 * 60 * 1000 - 1);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), ISOChronology.periodMillis());
        assertEquals(rule.getPeriodRange(), ISOChronology.periodDays());
        serialize(rule);
    }

    public void test_milliOfSecondRule() throws Exception {
        DateTimeFieldRule<Integer> rule = ISOChronology.milliOfSecondRule();
        assertEquals(rule.getReifiedType(), Integer.class);
        assertEquals(rule.getID(), "ISO.MilliOfSecond");
        assertEquals(rule.getName(), "MilliOfSecond");
        assertEquals(rule.getMinimumValue(), 0);
        assertEquals(rule.getLargestMinimumValue(), 0);
        assertEquals(rule.getMaximumValue(), 999);
        assertEquals(rule.getSmallestMaximumValue(), 999);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), ISOChronology.periodMillis());
        assertEquals(rule.getPeriodRange(), ISOChronology.periodSeconds());
        serialize(rule);
    }

    public void test_amPmOfDayRule() throws Exception {
        DateTimeFieldRule<AmPmOfDay> rule = ISOChronology.amPmOfDayRule();
        assertEquals(rule.getReifiedType(), AmPmOfDay.class);
        assertEquals(rule.getID(), "ISO.AmPmOfDay");
        assertEquals(rule.getName(), "AmPmOfDay");
        assertEquals(rule.getMinimumValue(), 0);
        assertEquals(rule.getLargestMinimumValue(), 0);
        assertEquals(rule.getMaximumValue(), 1);
        assertEquals(rule.getSmallestMaximumValue(), 1);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), ISOChronology.periodTwelveHours());
        assertEquals(rule.getPeriodRange(), ISOChronology.periodDays());
        serialize(rule);
    }

    public void test_hourOfAmPmRule() throws Exception {
        DateTimeFieldRule<Integer> rule = ISOChronology.hourOfAmPmRule();
        assertEquals(rule.getReifiedType(), Integer.class);
        assertEquals(rule.getID(), "ISO.HourOfAmPm");
        assertEquals(rule.getName(), "HourOfAmPm");
        assertEquals(rule.getMinimumValue(), 0);
        assertEquals(rule.getLargestMinimumValue(), 0);
        assertEquals(rule.getMaximumValue(), 11);
        assertEquals(rule.getSmallestMaximumValue(), 11);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), ISOChronology.periodHours());
        assertEquals(rule.getPeriodRange(), ISOChronology.periodTwelveHours());
        serialize(rule);
    }

    public void test_clockHourOfAmPmRule() throws Exception {
        DateTimeFieldRule<Integer> rule = ISOChronology.clockHourOfAmPmRule();
        assertEquals(rule.getReifiedType(), Integer.class);
        assertEquals(rule.getID(), "ISO.ClockHourOfAmPm");
        assertEquals(rule.getName(), "ClockHourOfAmPm");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 12);
        assertEquals(rule.getSmallestMaximumValue(), 12);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), ISOChronology.periodHours());
        assertEquals(rule.getPeriodRange(), ISOChronology.periodTwelveHours());
        serialize(rule);
    }

    //-----------------------------------------------------------------------
    // eras
    //-----------------------------------------------------------------------
    public void test_eras() {
        PeriodUnit rule = ISOChronology.periodEras();
        assertSame(ISOChronology.periodEras(), rule);
        assertEquals(rule.getName(), "Eras");
        assertEquals(rule.getEquivalentPeriods().size(), 0);
        assertEquals(rule.getEstimatedDuration(), Duration.seconds((long) (365.2425 * (2000000000L * 24 * 60 * 60L))));
    }

    //-----------------------------------------------------------------------
    // millenia
    //-----------------------------------------------------------------------
    public void test_millenia() {
        PeriodUnit rule = ISOChronology.periodMillenia();
        assertSame(ISOChronology.periodMillenia(), rule);
        assertEquals(rule.getName(), "Millenia");
        assertEquals(rule.getEquivalentPeriods().size(), 5);
        assertEquals(rule.getEquivalentPeriods().get(0), PeriodField.of(10, ISOChronology.periodCenturies()));
        assertEquals(rule.getEquivalentPeriods().get(1), PeriodField.of(100, ISOChronology.periodDecades()));
        assertEquals(rule.getEquivalentPeriods().get(2), PeriodField.of(1000, ISOChronology.periodYears()));
        assertEquals(rule.getEquivalentPeriods().get(3), PeriodField.of(4000, ISOChronology.periodQuarters()));
        assertEquals(rule.getEquivalentPeriods().get(4), PeriodField.of(12000, ISOChronology.periodMonths()));
        assertEquals(rule.getEstimatedDuration(), Duration.seconds((long) (365.2425 * (1000L * 24 * 60 * 60L))));
    }

    //-----------------------------------------------------------------------
    // centuries
    //-----------------------------------------------------------------------
    public void test_centuries() {
        PeriodUnit rule = ISOChronology.periodCenturies();
        assertSame(ISOChronology.periodCenturies(), rule);
        assertEquals(rule.getName(), "Centuries");
        assertEquals(rule.getEquivalentPeriods().size(), 4);
        assertEquals(rule.getEquivalentPeriods().get(0), PeriodField.of(10, ISOChronology.periodDecades()));
        assertEquals(rule.getEquivalentPeriods().get(1), PeriodField.of(100, ISOChronology.periodYears()));
        assertEquals(rule.getEquivalentPeriods().get(2), PeriodField.of(400, ISOChronology.periodQuarters()));
        assertEquals(rule.getEquivalentPeriods().get(3), PeriodField.of(1200, ISOChronology.periodMonths()));
        assertEquals(rule.getEstimatedDuration(), Duration.seconds((long) (365.2425 * (100L * 24 * 60 * 60L))));
    }

    //-----------------------------------------------------------------------
    // decades
    //-----------------------------------------------------------------------
    public void test_decades() {
        PeriodUnit rule = ISOChronology.periodDecades();
        assertSame(ISOChronology.periodDecades(), rule);
        assertEquals(rule.getName(), "Decades");
        assertEquals(rule.getEquivalentPeriods().size(), 3);
        assertEquals(rule.getEquivalentPeriods().get(0), PeriodField.of(10, ISOChronology.periodYears()));
        assertEquals(rule.getEquivalentPeriods().get(1), PeriodField.of(40, ISOChronology.periodQuarters()));
        assertEquals(rule.getEquivalentPeriods().get(2), PeriodField.of(120, ISOChronology.periodMonths()));
        assertEquals(rule.getEstimatedDuration(), Duration.seconds((long) (365.2425 * (10L * 24 * 60 * 60L))));
    }

    //-----------------------------------------------------------------------
    // years
    //-----------------------------------------------------------------------
    public void test_years() {
        PeriodUnit rule = ISOChronology.periodYears();
        assertSame(ISOChronology.periodYears(), rule);
        assertEquals(rule.getName(), "Years");
        assertEquals(rule.getEquivalentPeriods().size(), 2);
        assertEquals(rule.getEquivalentPeriods().get(0), PeriodField.of(4, ISOChronology.periodQuarters()));
        assertEquals(rule.getEquivalentPeriods().get(1), PeriodField.of(12, ISOChronology.periodMonths()));
        assertEquals(rule.getEstimatedDuration(), Duration.seconds((long) (365.2425 * (24 * 60 * 60L))));
    }

    //-----------------------------------------------------------------------
    // quarters
    //-----------------------------------------------------------------------
    public void test_quarters() {
        PeriodUnit rule = ISOChronology.periodQuarters();
        assertSame(ISOChronology.periodQuarters(), rule);
        assertEquals(rule.getName(), "Quarters");
        assertEquals(rule.getEquivalentPeriods().size(), 1);
        assertEquals(rule.getEquivalentPeriods().get(0), PeriodField.of(3, ISOChronology.periodMonths()));
        assertEquals(rule.getEstimatedDuration(), Duration.seconds(((long) (365.2425 * (24 * 60 * 60L)) / 4)));
    }

    //-----------------------------------------------------------------------
    // months
    //-----------------------------------------------------------------------
    public void test_months() {
        PeriodUnit rule = ISOChronology.periodMonths();
        assertSame(ISOChronology.periodMonths(), rule);
        assertEquals(rule.getName(), "Months");
        assertEquals(rule.getEquivalentPeriods().size(), 0);
        assertEquals(rule.getEstimatedDuration(), Duration.seconds(((long) (365.2425 * (24 * 60 * 60L)) / 12)));
    }

    //-----------------------------------------------------------------------
    // weekBasedYears
    //-----------------------------------------------------------------------
    public void test_weekyears() {
        PeriodUnit rule = ISOChronology.periodWeekBasedYears();
        assertSame(ISOChronology.periodWeekBasedYears(), rule);
        assertEquals(rule.getName(), "WeekBasedYears");
        assertEquals(rule.getEquivalentPeriods().size(), 0);
        assertEquals(rule.getEstimatedDuration(), Duration.seconds((long) ((52 * 7 + 0.5) * (24 * 60 * 60L))));
    }

    //-----------------------------------------------------------------------
    // weeks
    //-----------------------------------------------------------------------
    public void test_weeks() {
        PeriodUnit rule = ISOChronology.periodWeeks();
        assertSame(ISOChronology.periodWeeks(), rule);
        assertEquals(rule.getName(), "Weeks");
        assertEquals(rule.getEquivalentPeriods().size(), 1);
        assertEquals(rule.getEquivalentPeriods().get(0), PeriodField.of(7, ISOChronology.periodDays()));
        assertEquals(rule.getEstimatedDuration(), Duration.seconds(7 * 24 * 60 * 60L));
    }

    //-----------------------------------------------------------------------
    // days
    //-----------------------------------------------------------------------
    public void test_days() {
        PeriodUnit rule = ISOChronology.periodDays();
        assertSame(ISOChronology.periodDays(), rule);
        assertEquals(rule.getName(), "Days");
        assertEquals(rule.getEquivalentPeriods().size(), 0);
        assertEquals(rule.getEstimatedDuration(), Duration.seconds(24 * 60 * 60L));
    }

    //-----------------------------------------------------------------------
    // twelveHours
    //-----------------------------------------------------------------------
    public void test_twelveHours() {
        PeriodUnit rule = ISOChronology.periodTwelveHours();
        assertSame(ISOChronology.periodTwelveHours(), rule);
        assertEquals(rule.getName(), "TwelveHours");
        assertEquals(rule.getEquivalentPeriods().size(), 6);
        assertEquals(rule.getEquivalentPeriods().get(0), PeriodField.of(12, ISOChronology.periodHours()));
        assertEquals(rule.getEquivalentPeriods().get(1), PeriodField.of(12 * 60, ISOChronology.periodMinutes()));
        assertEquals(rule.getEquivalentPeriods().get(2), PeriodField.of(12 * 60 * 60L, ISOChronology.periodSeconds()));
        assertEquals(rule.getEquivalentPeriods().get(3), PeriodField.of(12 * 60 * 60 * 1000L, ISOChronology.periodMillis()));
        assertEquals(rule.getEquivalentPeriods().get(4), PeriodField.of(12 * 60 * 60 * 1000000L, ISOChronology.periodMicros()));
        assertEquals(rule.getEquivalentPeriods().get(5), PeriodField.of(12 * 60 * 60 * 1000000000L, ISOChronology.periodNanos()));
        assertEquals(rule.getEstimatedDuration(), Duration.seconds(12 * 60 * 60L));
    }

    //-----------------------------------------------------------------------
    // hours
    //-----------------------------------------------------------------------
    public void test_hours() {
        PeriodUnit rule = ISOChronology.periodHours();
        assertSame(ISOChronology.periodHours(), rule);
        assertEquals(rule.getName(), "Hours");
        assertEquals(rule.getEquivalentPeriods().size(), 5);
        assertEquals(rule.getEquivalentPeriods().get(0), PeriodField.of(60, ISOChronology.periodMinutes()));
        assertEquals(rule.getEquivalentPeriods().get(1), PeriodField.of(60 * 60L, ISOChronology.periodSeconds()));
        assertEquals(rule.getEquivalentPeriods().get(2), PeriodField.of(60 * 60 * 1000L, ISOChronology.periodMillis()));
        assertEquals(rule.getEquivalentPeriods().get(3), PeriodField.of(60 * 60 * 1000000L, ISOChronology.periodMicros()));
        assertEquals(rule.getEquivalentPeriods().get(4), PeriodField.of(60 * 60 * 1000000000L, ISOChronology.periodNanos()));
        assertEquals(rule.getEstimatedDuration(), Duration.seconds(60 * 60L));
    }

    //-----------------------------------------------------------------------
    // minutes
    //-----------------------------------------------------------------------
    public void test_minutes() {
        PeriodUnit rule = ISOChronology.periodMinutes();
        assertSame(ISOChronology.periodMinutes(), rule);
        assertEquals(rule.getName(), "Minutes");
        assertEquals(rule.getEquivalentPeriods().size(), 4);
        assertEquals(rule.getEquivalentPeriods().get(0), PeriodField.of(60L, ISOChronology.periodSeconds()));
        assertEquals(rule.getEquivalentPeriods().get(1), PeriodField.of(60 * 1000L, ISOChronology.periodMillis()));
        assertEquals(rule.getEquivalentPeriods().get(2), PeriodField.of(60 * 1000000L, ISOChronology.periodMicros()));
        assertEquals(rule.getEquivalentPeriods().get(3), PeriodField.of(60 * 1000000000L, ISOChronology.periodNanos()));
        assertEquals(rule.getEstimatedDuration(), Duration.seconds(60L));
    }

    //-----------------------------------------------------------------------
    // seconds
    //-----------------------------------------------------------------------
    public void test_seconds() {
        PeriodUnit rule = ISOChronology.periodSeconds();
        assertSame(ISOChronology.periodSeconds(), rule);
        assertEquals(rule.getName(), "Seconds");
        assertEquals(rule.getEquivalentPeriods().size(), 3);
        assertEquals(rule.getEquivalentPeriods().get(0), PeriodField.of(1000L, ISOChronology.periodMillis()));
        assertEquals(rule.getEquivalentPeriods().get(1), PeriodField.of(1000000L, ISOChronology.periodMicros()));
        assertEquals(rule.getEquivalentPeriods().get(2), PeriodField.of(1000000000L, ISOChronology.periodNanos()));
        assertEquals(rule.getEstimatedDuration(), Duration.nanos(1000000000L));
    }

    //-----------------------------------------------------------------------
    // millis
    //-----------------------------------------------------------------------
    public void test_millis() {
        PeriodUnit rule = ISOChronology.periodMillis();
        assertSame(ISOChronology.periodMillis(), rule);
        assertEquals(rule.getName(), "Millis");
        assertEquals(rule.getEquivalentPeriods().size(), 2);
        assertEquals(rule.getEquivalentPeriods().get(0), PeriodField.of(1000L, ISOChronology.periodMicros()));
        assertEquals(rule.getEquivalentPeriods().get(1), PeriodField.of(1000000L, ISOChronology.periodNanos()));
        assertEquals(rule.getEstimatedDuration(), Duration.nanos(1000000L));
    }

    //-----------------------------------------------------------------------
    // micros
    //-----------------------------------------------------------------------
    public void test_micros() {
        PeriodUnit rule = ISOChronology.periodMicros();
        assertSame(ISOChronology.periodMicros(), rule);
        assertEquals(rule.getName(), "Micros");
        assertEquals(rule.getEquivalentPeriods().size(), 1);
        assertEquals(rule.getEquivalentPeriods().get(0), PeriodField.of(1000L, ISOChronology.periodNanos()));
        assertEquals(rule.getEstimatedDuration(), Duration.nanos(1000L));
    }

    //-----------------------------------------------------------------------
    // nanos
    //-----------------------------------------------------------------------
    public void test_nanos() {
        PeriodUnit rule = ISOChronology.periodNanos();
        assertSame(ISOChronology.periodNanos(), rule);
        assertEquals(rule.getName(), "Nanos");
        assertEquals(rule.getEquivalentPeriods().size(), 0);
        assertEquals(rule.getEstimatedDuration(), Duration.nanos(1L));
    }

    //-----------------------------------------------------------------------
    // compareTo
    //-----------------------------------------------------------------------
    public void test_compareTo() {
        List<PeriodUnit> list = new ArrayList<PeriodUnit>();
        list.add(ISOChronology.periodNanos());
        list.add(ISOChronology.periodMicros());
        list.add(ISOChronology.periodMillis());
        list.add(ISOChronology.periodSeconds());
        list.add(ISOChronology.periodMinutes());
        list.add(ISOChronology.periodHours());
        list.add(ISOChronology.periodTwelveHours());
        list.add(ISOChronology.periodDays());
        list.add(ISOChronology.periodWeeks());
        list.add(ISOChronology.periodMonths());
        list.add(ISOChronology.periodQuarters());
        list.add(ISOChronology.periodWeekBasedYears());
        list.add(ISOChronology.periodYears());
        list.add(ISOChronology.periodDecades());
        list.add(ISOChronology.periodCenturies());
        list.add(ISOChronology.periodMillenia());
        list.add(ISOChronology.periodEras());
        
        List<PeriodUnit> test = new ArrayList<PeriodUnit>(list);
        Collections.shuffle(test);
        Collections.sort(test);
        assertEquals(test, list);
        // repeat for more randomness
        Collections.shuffle(test);
        Collections.sort(test);
        assertEquals(test, list);
    }

    //-----------------------------------------------------------------------
    public void test_toString() throws Exception {
        assertEquals(ISOChronology.INSTANCE.toString(), "ISO");
    }

    //-----------------------------------------------------------------------
    private void serialize(DateTimeFieldRule<?> rule) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(rule);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertSame(ois.readObject(), rule);
    }

}
