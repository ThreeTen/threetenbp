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

import static org.testng.Assert.assertEquals;
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
import java.lang.reflect.Modifier;

import javax.time.calendar.field.AmPmOfDay;

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
