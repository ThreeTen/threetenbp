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
package javax.time.calendrical;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.time.Duration;

import org.testng.annotations.Test;

/**
 * Test ISOChronology.
 *
 * @author Michael Nascimento Santos
 */
@Test
public class TestISOChronology {

    @SuppressWarnings("rawtypes")
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
    public void test_minuteOfHourRule() throws Exception {
        DateTimeRule rule = ISODateTimeRule.MINUTE_OF_HOUR;
        assertEquals(rule.getType(), DateTimeField.class);
        assertEquals(rule.getName(), "MinuteOfHour");
        assertEquals(rule.getValueRange(), DateTimeRuleRange.of(0, 59));
        assertEquals(rule.getPeriodUnit(), ISOPeriodUnit.MINUTES);
        assertEquals(rule.getPeriodRange(), ISOPeriodUnit.HOURS);
        serialize(rule);
    }

    public void test_secondOfMinuteRule() throws Exception {
        DateTimeRule rule = ISODateTimeRule.SECOND_OF_MINUTE;
        assertEquals(rule.getType(), DateTimeField.class);
        assertEquals(rule.getName(), "SecondOfMinute");
        assertEquals(rule.getValueRange(), DateTimeRuleRange.of(0, 59));
        assertEquals(rule.getPeriodUnit(), ISOPeriodUnit.SECONDS);
        assertEquals(rule.getPeriodRange(), ISOPeriodUnit.MINUTES);
        serialize(rule);
    }

    public void test_nanoOfSecondRule() throws Exception {
        DateTimeRule rule = ISODateTimeRule.NANO_OF_SECOND;
        assertEquals(rule.getType(), DateTimeField.class);
        assertEquals(rule.getName(), "NanoOfSecond");
        assertEquals(rule.getValueRange(), DateTimeRuleRange.of(0, 999999999));
        assertEquals(rule.getPeriodUnit(), ISOPeriodUnit.NANOS);
        assertEquals(rule.getPeriodRange(), ISOPeriodUnit.SECONDS);
        serialize(rule);
    }

    //-----------------------------------------------------------------------
    public void test_secondOfDayRule() throws Exception {
        DateTimeRule rule = ISODateTimeRule.SECOND_OF_DAY;
        assertEquals(rule.getType(), DateTimeField.class);
        assertEquals(rule.getName(), "SecondOfDay");
        assertEquals(rule.getValueRange(), DateTimeRuleRange.of(0, 24 * 60 * 60 - 1));
        assertEquals(rule.getPeriodUnit(), ISOPeriodUnit.SECONDS);
        assertEquals(rule.getPeriodRange(), ISOPeriodUnit.DAYS);
        serialize(rule);
    }

    public void test_milliOfDayRule() throws Exception {
        DateTimeRule rule = ISODateTimeRule.MILLI_OF_DAY;
        assertEquals(rule.getType(), DateTimeField.class);
        assertEquals(rule.getName(), "MilliOfDay");
        assertEquals(rule.getValueRange(), DateTimeRuleRange.of(0, 24 * 60 * 60 * 1000 - 1));
        assertEquals(rule.getPeriodUnit(), ISOPeriodUnit.MILLIS);
        assertEquals(rule.getPeriodRange(), ISOPeriodUnit.DAYS);
        serialize(rule);
    }

    public void test_milliOfSecondRule() throws Exception {
        DateTimeRule rule = ISODateTimeRule.MILLI_OF_SECOND;
        assertEquals(rule.getType(), DateTimeField.class);
        assertEquals(rule.getName(), "MilliOfSecond");
        assertEquals(rule.getValueRange(), DateTimeRuleRange.of(0, 999));
        assertEquals(rule.getPeriodUnit(), ISOPeriodUnit.MILLIS);
        assertEquals(rule.getPeriodRange(), ISOPeriodUnit.SECONDS);
        serialize(rule);
    }

    //-----------------------------------------------------------------------
    // eras
    //-----------------------------------------------------------------------
    public void test_eras() throws Exception {
        PeriodUnit rule = ISOPeriodUnit.ERAS;
        assertSame(ISOPeriodUnit.ERAS, rule);
        assertEquals(rule.getName(), "Eras");
        assertEquals(rule.getBaseEquivalent(), rule.field(1));
        assertEquals(rule.getDurationEstimate(), Duration.ofSeconds((long) (365.2425 * (2000000000L * 24 * 60 * 60L))));
        serialize(rule);
    }

    //-----------------------------------------------------------------------
    // millennia
    //-----------------------------------------------------------------------
    public void test_millennia() throws Exception {
        PeriodUnit rule = ISOPeriodUnit.MILLENNIA;
        assertSame(ISOPeriodUnit.MILLENNIA, rule);
        assertEquals(rule.getName(), "Millennia");
        assertEquals(rule.getBaseEquivalent(), ISOPeriodUnit.MONTHS.field(12000));
        assertEquals(rule.getDurationEstimate(), Duration.ofSeconds((long) (365.2425 * (1000L * 24 * 60 * 60L))));
        serialize(rule);
    }

    //-----------------------------------------------------------------------
    // centuries
    //-----------------------------------------------------------------------
    public void test_centuries() throws Exception {
        PeriodUnit rule = ISOPeriodUnit.CENTURIES;
        assertSame(ISOPeriodUnit.CENTURIES, rule);
        assertEquals(rule.getName(), "Centuries");
        assertEquals(rule.getBaseEquivalent(), ISOPeriodUnit.MONTHS.field(1200));
        assertEquals(rule.getDurationEstimate(), Duration.ofSeconds((long) (365.2425 * (100L * 24 * 60 * 60L))));
        serialize(rule);
    }

    //-----------------------------------------------------------------------
    // decades
    //-----------------------------------------------------------------------
    public void test_decades() throws Exception {
        PeriodUnit rule = ISOPeriodUnit.DECADES;
        assertSame(ISOPeriodUnit.DECADES, rule);
        assertEquals(rule.getName(), "Decades");
        assertEquals(rule.getBaseEquivalent(), ISOPeriodUnit.MONTHS.field(120));
        assertEquals(rule.getDurationEstimate(), Duration.ofSeconds((long) (365.2425 * (10L * 24 * 60 * 60L))));
        serialize(rule);
    }

    //-----------------------------------------------------------------------
    // years
    //-----------------------------------------------------------------------
    public void test_years() throws Exception {
        PeriodUnit rule = ISOPeriodUnit.YEARS;
        assertSame(ISOPeriodUnit.YEARS, rule);
        assertEquals(rule.getName(), "Years");
        assertEquals(rule.getBaseEquivalent(), ISOPeriodUnit.MONTHS.field(12));
        assertEquals(rule.getDurationEstimate(), Duration.ofSeconds((long) (365.2425 * (24 * 60 * 60L))));
        serialize(rule);
    }

    //-----------------------------------------------------------------------
    // quarters
    //-----------------------------------------------------------------------
    public void test_quarters() throws Exception {
        PeriodUnit rule = ISOPeriodUnit.QUARTERS;
        assertSame(ISOPeriodUnit.QUARTERS, rule);
        assertEquals(rule.getName(), "Quarters");
        assertEquals(rule.getBaseEquivalent(), ISOPeriodUnit.MONTHS.field(3));
        assertEquals(rule.getDurationEstimate(), Duration.ofSeconds(((long) (365.2425 * (24 * 60 * 60L)) / 4)));
        serialize(rule);
    }

    //-----------------------------------------------------------------------
    // months
    //-----------------------------------------------------------------------
    public void test_months() throws Exception {
        PeriodUnit rule = ISOPeriodUnit.MONTHS;
        assertSame(ISOPeriodUnit.MONTHS, rule);
        assertEquals(rule.getName(), "Months");
        assertEquals(rule.getBaseEquivalent(), rule.field(1));
        assertEquals(rule.getDurationEstimate(), Duration.ofSeconds(((long) (365.2425 * (24 * 60 * 60L)) / 12)));
        serialize(rule);
    }

    //-----------------------------------------------------------------------
    // weekBasedYears
    //-----------------------------------------------------------------------
    public void test_weekyears() throws Exception {
        PeriodUnit rule = ISOPeriodUnit.WEEK_BASED_YEARS;
        assertSame(ISOPeriodUnit.WEEK_BASED_YEARS, rule);
        assertEquals(rule.getName(), "WeekBasedYears");
        assertEquals(rule.getBaseEquivalent(), rule.field(1));
        assertEquals(rule.getDurationEstimate(), Duration.ofSeconds((long) ((52 * 7 + 0.5) * (24 * 60 * 60L))));
        serialize(rule);
    }

    //-----------------------------------------------------------------------
    // weeks
    //-----------------------------------------------------------------------
    public void test_weeks() throws Exception {
        PeriodUnit rule = ISOPeriodUnit.WEEKS;
        assertSame(ISOPeriodUnit.WEEKS, rule);
        assertEquals(rule.getName(), "Weeks");
        assertEquals(rule.getBaseEquivalent(), ISOPeriodUnit.DAYS.field(7));
        assertEquals(rule.getDurationEstimate(), Duration.ofSeconds(7 * 24 * 60 * 60L));
        serialize(rule);
    }

    //-----------------------------------------------------------------------
    // days
    //-----------------------------------------------------------------------
    public void test_days() throws Exception {
        PeriodUnit rule = ISOPeriodUnit.DAYS;
        assertSame(ISOPeriodUnit.DAYS, rule);
        assertEquals(rule.getName(), "Days");
        assertEquals(rule.getBaseEquivalent(), rule.field(1));
        assertEquals(rule.getDurationEstimate(), Duration.ofSeconds(24 * 60 * 60L));
        serialize(rule);
    }

    //-----------------------------------------------------------------------
    // 24Hours
    //-----------------------------------------------------------------------
    public void test_24hours() throws Exception {
        PeriodUnit rule = ISOPeriodUnit._24_HOURS;
        assertSame(ISOPeriodUnit._24_HOURS, rule);
        assertEquals(rule.getName(), "24Hours");
        assertEquals(rule.getBaseEquivalent(), ISOPeriodUnit.NANOS.field(24 * 60 * 60 * 1000000000L));
        assertEquals(rule.getDurationEstimate(), Duration.ofSeconds(24 * 60 * 60L));
        serialize(rule);
    }

    //-----------------------------------------------------------------------
    // 12Hours
    //-----------------------------------------------------------------------
    public void test_12hours() throws Exception {
        PeriodUnit rule = ISOPeriodUnit._12_HOURS;
        assertSame(ISOPeriodUnit._12_HOURS, rule);
        assertEquals(rule.getName(), "12Hours");
        assertEquals(rule.getBaseEquivalent(), ISOPeriodUnit.NANOS.field(12 * 60 * 60 * 1000000000L));
        assertEquals(rule.getDurationEstimate(), Duration.ofSeconds(12 * 60 * 60L));
        serialize(rule);
    }

    //-----------------------------------------------------------------------
    // hours
    //-----------------------------------------------------------------------
    public void test_hours() throws Exception {
        PeriodUnit rule = ISOPeriodUnit.HOURS;
        assertSame(ISOPeriodUnit.HOURS, rule);
        assertEquals(rule.getName(), "Hours");
        assertEquals(rule.getBaseEquivalent(), ISOPeriodUnit.NANOS.field(60 * 60 * 1000000000L));
        assertEquals(rule.getDurationEstimate(), Duration.ofSeconds(60 * 60L));
        serialize(rule);
    }

    //-----------------------------------------------------------------------
    // minutes
    //-----------------------------------------------------------------------
    public void test_minutes() throws Exception {
        PeriodUnit rule = ISOPeriodUnit.MINUTES;
        assertSame(ISOPeriodUnit.MINUTES, rule);
        assertEquals(rule.getName(), "Minutes");
        assertEquals(rule.getBaseEquivalent(), ISOPeriodUnit.NANOS.field(60 * 1000000000L));
        assertEquals(rule.getDurationEstimate(), Duration.ofSeconds(60L));
        serialize(rule);
    }

    //-----------------------------------------------------------------------
    // seconds
    //-----------------------------------------------------------------------
    public void test_seconds() throws Exception {
        PeriodUnit rule = ISOPeriodUnit.SECONDS;
        assertSame(ISOPeriodUnit.SECONDS, rule);
        assertEquals(rule.getName(), "Seconds");
        assertEquals(rule.getBaseEquivalent(), ISOPeriodUnit.NANOS.field(1000000000L));
        assertEquals(rule.getDurationEstimate(), Duration.ofNanos(1000000000L));
        serialize(rule);
    }

    //-----------------------------------------------------------------------
    // millis
    //-----------------------------------------------------------------------
    public void test_millis() throws Exception {
        PeriodUnit rule = ISOPeriodUnit.MILLIS;
        assertSame(ISOPeriodUnit.MILLIS, rule);
        assertEquals(rule.getName(), "Millis");
        assertEquals(rule.getBaseEquivalent(), ISOPeriodUnit.NANOS.field(1000000L));
        assertEquals(rule.getDurationEstimate(), Duration.ofNanos(1000000L));
        serialize(rule);
    }

    //-----------------------------------------------------------------------
    // micros
    //-----------------------------------------------------------------------
    public void test_micros() throws Exception {
        PeriodUnit rule = ISOPeriodUnit.MICROS;
        assertSame(ISOPeriodUnit.MICROS, rule);
        assertEquals(rule.getName(), "Micros");
        assertEquals(rule.getBaseEquivalent(), ISOPeriodUnit.NANOS.field(1000L));
        assertEquals(rule.getDurationEstimate(), Duration.ofNanos(1000L));
        serialize(rule);
    }

    //-----------------------------------------------------------------------
    // nanos
    //-----------------------------------------------------------------------
    public void test_nanos() throws Exception {
        PeriodUnit rule = ISOPeriodUnit.NANOS;
        assertSame(ISOPeriodUnit.NANOS, rule);
        assertEquals(rule.getName(), "Nanos");
        assertEquals(rule.getBaseEquivalent(), rule.field(1));
        assertEquals(rule.getDurationEstimate(), Duration.ofNanos(1L));
        serialize(rule);
    }

    //-----------------------------------------------------------------------
    // compareTo
    //-----------------------------------------------------------------------
    public void test_compareTo_periodUnits() {
        List<PeriodUnit> list = new ArrayList<PeriodUnit>();
        list.add(ISOPeriodUnit.NANOS);
        list.add(ISOPeriodUnit.MICROS);
        list.add(ISOPeriodUnit.MILLIS);
        list.add(ISOPeriodUnit.SECONDS);
        list.add(ISOPeriodUnit.MINUTES);
        list.add(ISOPeriodUnit.HOURS);
        list.add(ISOPeriodUnit._12_HOURS);
        list.add(ISOPeriodUnit.DAYS);
        list.add(ISOPeriodUnit.WEEKS);
        list.add(ISOPeriodUnit.MONTHS);
        list.add(ISOPeriodUnit.QUARTERS);
        list.add(ISOPeriodUnit.WEEK_BASED_YEARS);
        list.add(ISOPeriodUnit.YEARS);
        list.add(ISOPeriodUnit.DECADES);
        list.add(ISOPeriodUnit.CENTURIES);
        list.add(ISOPeriodUnit.MILLENNIA);
        list.add(ISOPeriodUnit.ERAS);
        
        List<PeriodUnit> test = new ArrayList<PeriodUnit>(list);
        Collections.shuffle(test);
        Collections.sort(test);
        assertEquals(test, list);
        // repeat for more randomness
        Collections.shuffle(test);
        Collections.sort(test);
        assertEquals(test, list);
    }

    public void test_compareTo_fields() {
        List<DateTimeRule> list = new ArrayList<DateTimeRule>();
        list.add(ISODateTimeRule.NANO_OF_SECOND);
        list.add(ISODateTimeRule.NANO_OF_DAY);
        list.add(ISODateTimeRule.MILLI_OF_SECOND);
        list.add(ISODateTimeRule.MILLI_OF_DAY);
        list.add(ISODateTimeRule.SECOND_OF_MINUTE);
        list.add(ISODateTimeRule.SECOND_OF_DAY);
        list.add(ISODateTimeRule.EPOCH_SECOND);
        list.add(ISODateTimeRule.MINUTE_OF_HOUR);
        list.add(ISODateTimeRule.MINUTE_OF_DAY);
        list.add(ISODateTimeRule.CLOCK_HOUR_OF_AMPM);
        list.add(ISODateTimeRule.HOUR_OF_AMPM);
        list.add(ISODateTimeRule.CLOCK_HOUR_OF_DAY);
        list.add(ISODateTimeRule.HOUR_OF_DAY);
        list.add(ISODateTimeRule.AM_PM_OF_DAY);
        list.add(ISODateTimeRule.DAY_OF_WEEK);
        list.add(ISODateTimeRule.DAY_OF_MONTH);
        list.add(ISODateTimeRule.DAY_OF_YEAR);
        list.add(ISODateTimeRule.EPOCH_DAY);
        list.add(ISODateTimeRule.ALIGNED_WEEK_OF_MONTH);
        list.add(ISODateTimeRule.WEEK_OF_WEEK_BASED_YEAR);
        list.add(ISODateTimeRule.ALIGNED_WEEK_OF_YEAR);
        list.add(ISODateTimeRule.MONTH_OF_QUARTER);
        list.add(ISODateTimeRule.MONTH_OF_YEAR);
        list.add(ISODateTimeRule.ZERO_EPOCH_MONTH);
        list.add(ISODateTimeRule.QUARTER_OF_YEAR);
        list.add(ISODateTimeRule.WEEK_BASED_YEAR);
        list.add(ISODateTimeRule.YEAR);
        
        List<DateTimeRule> test = new ArrayList<DateTimeRule>(list);
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
    private void serialize(DateTimeRule rule) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(rule);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertSame(ois.readObject(), rule);
    }

    //-----------------------------------------------------------------------
    private void serialize(PeriodUnit unit) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(unit);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertSame(ois.readObject(), unit);
    }

}
