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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.time.calendar.field.WeekBasedYear;
import javax.time.calendar.field.Year;
import javax.time.period.PeriodUnits;

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
        ISOChronology chronology = ISOChronology.INSTANCE;
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
    public void test_instance() throws Exception {
        assertSame(ISOChronology.INSTANCE.year(), ISOChronology.yearRule());
        assertSame(ISOChronology.INSTANCE.monthOfYear(), ISOChronology.monthOfYearRule());
        assertSame(ISOChronology.INSTANCE.dayOfMonth(), ISOChronology.dayOfMonthRule());
        assertSame(ISOChronology.INSTANCE.dayOfYear(), ISOChronology.dayOfYearRule());
        assertSame(ISOChronology.INSTANCE.dayOfWeek(), ISOChronology.dayOfWeekRule());
        assertSame(ISOChronology.INSTANCE.hourOfDay(), ISOChronology.hourOfDayRule());
        assertSame(ISOChronology.INSTANCE.minuteOfHour(), ISOChronology.minuteOfHourRule());
        assertSame(ISOChronology.INSTANCE.secondOfMinute(), ISOChronology.secondOfMinuteRule());
        assertSame(ISOChronology.INSTANCE.nanoOfSecond(), ISOChronology.nanoOfSecondRule());
    }

    //-----------------------------------------------------------------------
    public void test_yearRule() throws Exception {
        DateTimeFieldRule rule = ISOChronology.yearRule();
        assertEquals(rule.getID(), "ISO.Year");
        assertEquals(rule.getName(), "Year");
        assertEquals(rule.getMinimumValue(), Year.MIN_YEAR);
        assertEquals(rule.getLargestMinimumValue(), Year.MIN_YEAR);
        assertEquals(rule.getMaximumValue(), Year.MAX_YEAR);
        assertEquals(rule.getSmallestMaximumValue(), Year.MAX_YEAR);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.YEARS);
        assertEquals(rule.getPeriodRange(), null);
        serialize(rule);
    }

    public void test_monthOfYearRule() throws Exception {
        DateTimeFieldRule rule = ISOChronology.monthOfYearRule();
        assertEquals(rule.getID(), "ISO.MonthOfYear");
        assertEquals(rule.getName(), "MonthOfYear");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 12);
        assertEquals(rule.getSmallestMaximumValue(), 12);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.MONTHS);
        assertEquals(rule.getPeriodRange(), PeriodUnits.YEARS);
        serialize(rule);
    }

    public void test_dayOfMonthRule() throws Exception {
        DateTimeFieldRule rule = ISOChronology.dayOfMonthRule();
        assertEquals(rule.getID(), "ISO.DayOfMonth");
        assertEquals(rule.getName(), "DayOfMonth");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 31);
        assertEquals(rule.getSmallestMaximumValue(), 28);
        assertEquals(rule.isFixedValueSet(), false);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.DAYS);
        assertEquals(rule.getPeriodRange(), PeriodUnits.MONTHS);
        serialize(rule);
    }

    public void test_dayOfYearRule() throws Exception {
        DateTimeFieldRule rule = ISOChronology.dayOfYearRule();
        assertEquals(rule.getID(), "ISO.DayOfYear");
        assertEquals(rule.getName(), "DayOfYear");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 366);
        assertEquals(rule.getSmallestMaximumValue(), 365);
        assertEquals(rule.isFixedValueSet(), false);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.DAYS);
        assertEquals(rule.getPeriodRange(), PeriodUnits.YEARS);
        serialize(rule);
    }

    public void test_weekyearRule() throws Exception {
        DateTimeFieldRule rule = ISOChronology.weekBasedYearRule();
        assertEquals(rule.getID(), "ISO.WeekBasedYear");
        assertEquals(rule.getName(), "WeekBasedYear");
        assertEquals(rule.getMinimumValue(), WeekBasedYear.MIN_YEAR);
        assertEquals(rule.getLargestMinimumValue(), WeekBasedYear.MIN_YEAR);
        assertEquals(rule.getMaximumValue(), WeekBasedYear.MAX_YEAR);
        assertEquals(rule.getSmallestMaximumValue(), WeekBasedYear.MAX_YEAR);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.WEEKYEARS);
        assertEquals(rule.getPeriodRange(), null);
        serialize(rule);
    }

    public void test_weekOfWeekyearRule() throws Exception {
        DateTimeFieldRule rule = ISOChronology.weekOfWeekBasedYearRule();
        assertEquals(rule.getID(), "ISO.WeekOfWeekBasedYear");
        assertEquals(rule.getName(), "WeekOfWeekBasedYear");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 53);
        assertEquals(rule.getSmallestMaximumValue(), 52);
        assertEquals(rule.isFixedValueSet(), false);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.WEEKS);
        assertEquals(rule.getPeriodRange(), PeriodUnits.WEEKYEARS);
        serialize(rule);
    }

    public void test_dayOfWeekRule() throws Exception {
        DateTimeFieldRule rule = ISOChronology.dayOfWeekRule();
        assertEquals(rule.getID(), "ISO.DayOfWeek");
        assertEquals(rule.getName(), "DayOfWeek");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 7);
        assertEquals(rule.getSmallestMaximumValue(), 7);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.DAYS);
        assertEquals(rule.getPeriodRange(), PeriodUnits.WEEKS);
        serialize(rule);
    }

    public void test_weekOfYearRule() throws Exception {
        DateTimeFieldRule rule = ISOChronology.weekOfYearRule();
        assertEquals(rule.getID(), "ISO.WeekOfYear");
        assertEquals(rule.getName(), "WeekOfYear");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 53);
        assertEquals(rule.getSmallestMaximumValue(), 53);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.WEEKS);
        assertEquals(rule.getPeriodRange(), PeriodUnits.YEARS);
        serialize(rule);
    }

    public void test_quarterOfYearRule() throws Exception {
        DateTimeFieldRule rule = ISOChronology.quarterOfYearRule();
        assertEquals(rule.getID(), "ISO.QuarterOfYear");
        assertEquals(rule.getName(), "QuarterOfYear");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 4);
        assertEquals(rule.getSmallestMaximumValue(), 4);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.QUARTERS);
        assertEquals(rule.getPeriodRange(), PeriodUnits.YEARS);
        serialize(rule);
    }

    public void test_monthOfQuarterRule() throws Exception {
        DateTimeFieldRule rule = ISOChronology.monthOfQuarterRule();
        assertEquals(rule.getID(), "ISO.MonthOfQuarter");
        assertEquals(rule.getName(), "MonthOfQuarter");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 3);
        assertEquals(rule.getSmallestMaximumValue(), 3);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.MONTHS);
        assertEquals(rule.getPeriodRange(), PeriodUnits.QUARTERS);
        serialize(rule);
    }

    public void test_weekOfMonthRule() throws Exception {
        DateTimeFieldRule rule = ISOChronology.weekOfMonthRule();
        assertEquals(rule.getID(), "ISO.WeekOfMonth");
        assertEquals(rule.getName(), "WeekOfMonth");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 5);
        assertEquals(rule.getSmallestMaximumValue(), 4);
        assertEquals(rule.isFixedValueSet(), false);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.WEEKS);
        assertEquals(rule.getPeriodRange(), PeriodUnits.MONTHS);
        serialize(rule);
    }

    //-----------------------------------------------------------------------
    public void test_hourOfDayRule() throws Exception {
        DateTimeFieldRule rule = ISOChronology.hourOfDayRule();
        assertEquals(rule.getID(), "ISO.HourOfDay");
        assertEquals(rule.getName(), "HourOfDay");
        assertEquals(rule.getMinimumValue(), 0);
        assertEquals(rule.getLargestMinimumValue(), 0);
        assertEquals(rule.getMaximumValue(), 23);
        assertEquals(rule.getSmallestMaximumValue(), 23);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.HOURS);
        assertEquals(rule.getPeriodRange(), PeriodUnits.DAYS);
        serialize(rule);
    }

    public void test_minuteOfHourRule() throws Exception {
        DateTimeFieldRule rule = ISOChronology.minuteOfHourRule();
        assertEquals(rule.getID(), "ISO.MinuteOfHour");
        assertEquals(rule.getName(), "MinuteOfHour");
        assertEquals(rule.getMinimumValue(), 0);
        assertEquals(rule.getLargestMinimumValue(), 0);
        assertEquals(rule.getMaximumValue(), 59);
        assertEquals(rule.getSmallestMaximumValue(), 59);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.MINUTES);
        assertEquals(rule.getPeriodRange(), PeriodUnits.HOURS);
        serialize(rule);
    }

    public void test_secondOfMinuteRule() throws Exception {
        DateTimeFieldRule rule = ISOChronology.secondOfMinuteRule();
        assertEquals(rule.getID(), "ISO.SecondOfMinute");
        assertEquals(rule.getName(), "SecondOfMinute");
        assertEquals(rule.getMinimumValue(), 0);
        assertEquals(rule.getLargestMinimumValue(), 0);
        assertEquals(rule.getMaximumValue(), 59);
        assertEquals(rule.getSmallestMaximumValue(), 59);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.SECONDS);
        assertEquals(rule.getPeriodRange(), PeriodUnits.MINUTES);
        serialize(rule);
    }

    public void test_nanoOfSecondRule() throws Exception {
        DateTimeFieldRule rule = ISOChronology.nanoOfSecondRule();
        assertEquals(rule.getID(), "ISO.NanoOfSecond");
        assertEquals(rule.getName(), "NanoOfSecond");
        assertEquals(rule.getMinimumValue(), 0);
        assertEquals(rule.getLargestMinimumValue(), 0);
        assertEquals(rule.getMaximumValue(), 999999999);
        assertEquals(rule.getSmallestMaximumValue(), 999999999);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.NANOS);
        assertEquals(rule.getPeriodRange(), PeriodUnits.SECONDS);
        serialize(rule);
    }

    //-----------------------------------------------------------------------
    public void test_secondOfDayRule() throws Exception {
        DateTimeFieldRule rule = ISOChronology.secondOfDayRule();
        assertEquals(rule.getID(), "ISO.SecondOfDay");
        assertEquals(rule.getName(), "SecondOfDay");
        assertEquals(rule.getMinimumValue(), 0);
        assertEquals(rule.getLargestMinimumValue(), 0);
        assertEquals(rule.getMaximumValue(), 24 * 60 * 60 - 1);
        assertEquals(rule.getSmallestMaximumValue(), 24 * 60 * 60 - 1);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.SECONDS);
        assertEquals(rule.getPeriodRange(), PeriodUnits.DAYS);
        serialize(rule);
    }

    public void test_milliOfDayRule() throws Exception {
        DateTimeFieldRule rule = ISOChronology.milliOfDayRule();
        assertEquals(rule.getID(), "ISO.MilliOfDay");
        assertEquals(rule.getName(), "MilliOfDay");
        assertEquals(rule.getMinimumValue(), 0);
        assertEquals(rule.getLargestMinimumValue(), 0);
        assertEquals(rule.getMaximumValue(), 24 * 60 * 60 * 1000 - 1);
        assertEquals(rule.getSmallestMaximumValue(), 24 * 60 * 60 * 1000 - 1);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.MILLIS);
        assertEquals(rule.getPeriodRange(), PeriodUnits.DAYS);
        serialize(rule);
    }

    public void test_milliOfSecondRule() throws Exception {
        DateTimeFieldRule rule = ISOChronology.milliOfSecondRule();
        assertEquals(rule.getID(), "ISO.MilliOfSecond");
        assertEquals(rule.getName(), "MilliOfSecond");
        assertEquals(rule.getMinimumValue(), 0);
        assertEquals(rule.getLargestMinimumValue(), 0);
        assertEquals(rule.getMaximumValue(), 999);
        assertEquals(rule.getSmallestMaximumValue(), 999);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.MILLIS);
        assertEquals(rule.getPeriodRange(), PeriodUnits.SECONDS);
        serialize(rule);
    }

    public void test_amPmOfDayRule() throws Exception {
        DateTimeFieldRule rule = ISOChronology.amPmOfDayRule();
        assertEquals(rule.getID(), "ISO.AmPmOfDay");
        assertEquals(rule.getName(), "AmPmOfDay");
        assertEquals(rule.getMinimumValue(), 0);
        assertEquals(rule.getLargestMinimumValue(), 0);
        assertEquals(rule.getMaximumValue(), 1);
        assertEquals(rule.getSmallestMaximumValue(), 1);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.TWELVE_HOURS);
        assertEquals(rule.getPeriodRange(), PeriodUnits.DAYS);
        serialize(rule);
    }

    public void test_hourOfAmPmRule() throws Exception {
        DateTimeFieldRule rule = ISOChronology.hourOfAmPmRule();
        assertEquals(rule.getID(), "ISO.HourOfAmPm");
        assertEquals(rule.getName(), "HourOfAmPm");
        assertEquals(rule.getMinimumValue(), 0);
        assertEquals(rule.getLargestMinimumValue(), 0);
        assertEquals(rule.getMaximumValue(), 11);
        assertEquals(rule.getSmallestMaximumValue(), 11);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.HOURS);
        assertEquals(rule.getPeriodRange(), PeriodUnits.TWELVE_HOURS);
        serialize(rule);
    }

    //-----------------------------------------------------------------------
    public void test_toString() throws Exception {
        assertEquals(ISOChronology.INSTANCE.toString(), "ISO");
    }

    //-----------------------------------------------------------------------
    private void serialize(DateTimeFieldRule rule) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(rule);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertSame(ois.readObject(), rule);
    }

}
