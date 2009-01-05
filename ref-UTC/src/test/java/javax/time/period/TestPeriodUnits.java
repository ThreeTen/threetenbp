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
package javax.time.period;

import static javax.time.period.PeriodUnits.*;
import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

/**
 * Test PeriodUnits.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestPeriodUnits {

    //-----------------------------------------------------------------------
    // basics
    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void test_constructor() throws Exception {
        Constructor[] cons = PeriodUnits.class.getDeclaredConstructors();
        assertEquals(cons.length, 1);
        assertTrue(Modifier.isPrivate(cons[0].getModifiers()));
        cons[0].setAccessible(true);
        cons[0].newInstance(Collections.nCopies(cons[0].getParameterTypes().length, null).toArray());
    }

    public void test_immutable() {
        Class<PeriodUnits> cls = PeriodUnits.class;
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            assertTrue(Modifier.isStatic(field.getModifiers()));
            assertTrue(Modifier.isFinal(field.getModifiers()));
        }
    }

    //-----------------------------------------------------------------------
    // centuries
    //-----------------------------------------------------------------------
    public void centuries() {
        assertNotNull(CENTURIES);
        assertSame(CENTURIES, CENTURIES);
        assertEquals(CENTURIES.isStandard(), true);
        assertEquals(CENTURIES.isStandardDerived(), true);
        assertEquals(CENTURIES.getName(), "Centuries");
        assertEquals(CENTURIES.getAlternatePeriod(), Period.years(100));
        assertEquals(CENTURIES.equals(CENTURIES), true);
        assertEquals(CENTURIES.equals(NANOS), false);
        assertEquals(CENTURIES.equals(null), false);
        assertEquals(CENTURIES.equals(""), false);
        assertEquals(CENTURIES.hashCode() == CENTURIES.hashCode(), true);
    }

    //-----------------------------------------------------------------------
    // decades
    //-----------------------------------------------------------------------
    public void decades() {
        assertNotNull(DECADES);
        assertSame(DECADES, DECADES);
        assertEquals(DECADES.isStandard(), true);
        assertEquals(DECADES.isStandardDerived(), true);
        assertEquals(DECADES.getName(), "Decades");
        assertEquals(DECADES.getAlternatePeriod(), Period.years(10));
        assertEquals(DECADES.equals(DECADES), true);
        assertEquals(DECADES.equals(NANOS), false);
        assertEquals(DECADES.equals(null), false);
        assertEquals(DECADES.equals(""), false);
        assertEquals(DECADES.hashCode() == DECADES.hashCode(), true);
    }

    //-----------------------------------------------------------------------
    // years
    //-----------------------------------------------------------------------
    public void years() {
        assertNotNull(YEARS);
        assertSame(YEARS, YEARS);
        assertEquals(YEARS.isStandard(), true);
        assertEquals(YEARS.isStandardDerived(), false);
        assertEquals(YEARS.getName(), "Years");
        assertEquals(YEARS.getAlternatePeriod(), Period.months(12));
        assertEquals(YEARS.equals(YEARS), true);
        assertEquals(YEARS.equals(NANOS), false);
        assertEquals(YEARS.equals(null), false);
        assertEquals(YEARS.equals(""), false);
        assertEquals(NANOS.hashCode() == NANOS.hashCode(), true);
    }

    //-----------------------------------------------------------------------
    // weekyears
    //-----------------------------------------------------------------------
    public void weekyears() {
        assertNotNull(WEEKYEARS);
        assertSame(WEEKYEARS, WEEKYEARS);
        assertEquals(WEEKYEARS.isStandard(), true);
        assertEquals(WEEKYEARS.isStandardDerived(), false);
        assertEquals(WEEKYEARS.getName(), "Weekyears");
        assertEquals(WEEKYEARS.getAlternatePeriod(), null);
        assertEquals(WEEKYEARS.equals(WEEKYEARS), true);
        assertEquals(WEEKYEARS.equals(NANOS), false);
        assertEquals(WEEKYEARS.equals(null), false);
        assertEquals(WEEKYEARS.equals(""), false);
        assertEquals(NANOS.hashCode() == NANOS.hashCode(), true);
    }

    //-----------------------------------------------------------------------
    // quarters
    //-----------------------------------------------------------------------
    public void quarters() {
        assertNotNull(QUARTERS);
        assertSame(QUARTERS, QUARTERS);
        assertEquals(QUARTERS.isStandard(), true);
        assertEquals(QUARTERS.isStandardDerived(), true);
        assertEquals(QUARTERS.getName(), "Quarters");
        assertEquals(QUARTERS.getAlternatePeriod(), Period.months(3));
        assertEquals(QUARTERS.equals(QUARTERS), true);
        assertEquals(QUARTERS.equals(NANOS), false);
        assertEquals(QUARTERS.equals(null), false);
        assertEquals(QUARTERS.equals(""), false);
        assertEquals(NANOS.hashCode() == NANOS.hashCode(), true);
    }

    //-----------------------------------------------------------------------
    // months
    //-----------------------------------------------------------------------
    public void months() {
        assertNotNull(MONTHS);
        assertSame(MONTHS, MONTHS);
        assertEquals(MONTHS.isStandard(), true);
        assertEquals(MONTHS.isStandardDerived(), false);
        assertEquals(MONTHS.getName(), "Months");
        assertEquals(MONTHS.getAlternatePeriod(), null);
        assertEquals(MONTHS.equals(MONTHS), true);
        assertEquals(MONTHS.equals(NANOS), false);
        assertEquals(MONTHS.equals(null), false);
        assertEquals(MONTHS.equals(""), false);
        assertEquals(MONTHS.hashCode() == MONTHS.hashCode(), true);
    }

    //-----------------------------------------------------------------------
    // weeks
    //-----------------------------------------------------------------------
    public void weeks() {
        assertNotNull(WEEKS);
        assertSame(WEEKS, WEEKS);
        assertEquals(WEEKS.isStandard(), true);
        assertEquals(WEEKS.isStandardDerived(), true);
        assertEquals(WEEKS.getName(), "Weeks");
        assertEquals(WEEKS.getAlternatePeriod(), Period.days(7));
        assertEquals(WEEKS.equals(WEEKS), true);
        assertEquals(WEEKS.equals(NANOS), false);
        assertEquals(WEEKS.equals(null), false);
        assertEquals(WEEKS.equals(""), false);
        assertEquals(WEEKS.hashCode() == WEEKS.hashCode(), true);
    }

    //-----------------------------------------------------------------------
    // days
    //-----------------------------------------------------------------------
    public void days() {
        assertNotNull(DAYS);
        assertSame(DAYS, DAYS);
        assertEquals(DAYS.isStandard(), true);
        assertEquals(DAYS.isStandardDerived(), false);
        assertEquals(DAYS.getName(), "Days");
        assertEquals(DAYS.getAlternatePeriod(), null);
        assertEquals(DAYS.equals(DAYS), true);
        assertEquals(DAYS.equals(NANOS), false);
        assertEquals(DAYS.equals(null), false);
        assertEquals(DAYS.equals(""), false);
        assertEquals(DAYS.hashCode() == DAYS.hashCode(), true);
    }

    //-----------------------------------------------------------------------
    // twelveHours
    //-----------------------------------------------------------------------
    public void twelveHours() {
        assertNotNull(TWELVE_HOURS);
        assertSame(TWELVE_HOURS, TWELVE_HOURS);
        assertEquals(TWELVE_HOURS.isStandard(), true);
        assertEquals(TWELVE_HOURS.isStandardDerived(), true);
        assertEquals(TWELVE_HOURS.getName(), "TwelveHours");
        assertEquals(TWELVE_HOURS.getAlternatePeriod(), Period.hours(12));
        assertEquals(TWELVE_HOURS.equals(TWELVE_HOURS), true);
        assertEquals(TWELVE_HOURS.equals(NANOS), false);
        assertEquals(TWELVE_HOURS.equals(null), false);
        assertEquals(TWELVE_HOURS.equals(""), false);
        assertEquals(TWELVE_HOURS.hashCode() == TWELVE_HOURS.hashCode(), true);
    }

    //-----------------------------------------------------------------------
    // hours
    //-----------------------------------------------------------------------
    public void hours() {
        assertNotNull(HOURS);
        assertSame(HOURS, HOURS);
        assertEquals(HOURS.isStandard(), true);
        assertEquals(HOURS.isStandardDerived(), false);
        assertEquals(HOURS.getName(), "Hours");
        assertEquals(HOURS.getAlternatePeriod(), Period.minutes(60));
        assertEquals(HOURS.equals(HOURS), true);
        assertEquals(HOURS.equals(NANOS), false);
        assertEquals(HOURS.equals(null), false);
        assertEquals(HOURS.equals(""), false);
        assertEquals(HOURS.hashCode() == HOURS.hashCode(), true);
    }

    //-----------------------------------------------------------------------
    // minutes
    //-----------------------------------------------------------------------
    public void minutes() {
        assertNotNull(MINUTES);
        assertSame(MINUTES, MINUTES);
        assertEquals(MINUTES.isStandard(), true);
        assertEquals(MINUTES.isStandardDerived(), false);
        assertEquals(MINUTES.getName(), "Minutes");
        assertEquals(MINUTES.getAlternatePeriod(), Period.seconds(60));
        assertEquals(MINUTES.equals(MINUTES), true);
        assertEquals(MINUTES.equals(NANOS), false);
        assertEquals(MINUTES.equals(null), false);
        assertEquals(MINUTES.equals(""), false);
        assertEquals(MINUTES.hashCode() == MINUTES.hashCode(), true);
    }

    //-----------------------------------------------------------------------
    // seconds
    //-----------------------------------------------------------------------
    public void seconds() {
        assertNotNull(SECONDS);
        assertSame(SECONDS, SECONDS);
        assertEquals(SECONDS.isStandard(), true);
        assertEquals(SECONDS.isStandardDerived(), false);
        assertEquals(SECONDS.getName(), "Seconds");
        assertEquals(SECONDS.getAlternatePeriod(), Period.nanos(1000000000));
        assertEquals(SECONDS.equals(SECONDS), true);
        assertEquals(SECONDS.equals(NANOS), false);
        assertEquals(SECONDS.equals(null), false);
        assertEquals(SECONDS.equals(""), false);
        assertEquals(SECONDS.hashCode() == SECONDS.hashCode(), true);
    }

    //-----------------------------------------------------------------------
    // millis
    //-----------------------------------------------------------------------
    public void millis() {
        assertNotNull(MILLIS);
        assertSame(MILLIS, MILLIS);
        assertEquals(MILLIS.isStandard(), true);
        assertEquals(MILLIS.isStandardDerived(), false);
        assertEquals(MILLIS.getName(), "Millis");
        assertEquals(MILLIS.getAlternatePeriod(), Period.nanos(1000000));
        assertEquals(MILLIS.equals(MILLIS), true);
        assertEquals(MILLIS.equals(YEARS), false);
        assertEquals(MILLIS.equals(null), false);
        assertEquals(MILLIS.equals(""), false);
        assertEquals(MILLIS.hashCode() == MILLIS.hashCode(), true);
    }

    //-----------------------------------------------------------------------
    // micros
    //-----------------------------------------------------------------------
    public void micros() {
        assertNotNull(MICROS);
        assertSame(MICROS, MICROS);
        assertEquals(MICROS.isStandard(), true);
        assertEquals(MICROS.isStandardDerived(), false);
        assertEquals(MICROS.getName(), "Micros");
        assertEquals(MICROS.getAlternatePeriod(), Period.nanos(1000));
        assertEquals(MICROS.equals(MICROS), true);
        assertEquals(MICROS.equals(YEARS), false);
        assertEquals(MICROS.equals(null), false);
        assertEquals(MICROS.equals(""), false);
        assertEquals(MICROS.hashCode() == MICROS.hashCode(), true);
    }

    //-----------------------------------------------------------------------
    // nanos
    //-----------------------------------------------------------------------
    public void nanos() {
        assertNotNull(NANOS);
        assertSame(NANOS, NANOS);
        assertEquals(NANOS.isStandard(), true);
        assertEquals(NANOS.isStandardDerived(), false);
        assertEquals(NANOS.getName(), "Nanos");
        assertEquals(NANOS.getAlternatePeriod(), null);
        assertEquals(NANOS.equals(NANOS), true);
        assertEquals(NANOS.equals(YEARS), false);
        assertEquals(NANOS.equals(null), false);
        assertEquals(NANOS.equals(""), false);
        assertEquals(NANOS.hashCode() == NANOS.hashCode(), true);
    }

    //-----------------------------------------------------------------------
    // compareTo
    //-----------------------------------------------------------------------
    public void compareTo() {
        List<PeriodUnit> list = new ArrayList<PeriodUnit>();
        list.add(NANOS);
        list.add(MICROS);
        list.add(MILLIS);
        list.add(SECONDS);
        list.add(MINUTES);
        list.add(HOURS);
        list.add(TWELVE_HOURS);
        list.add(DAYS);
        list.add(WEEKS);
        list.add(MONTHS);
        list.add(QUARTERS);
        list.add(WEEKYEARS);
        list.add(YEARS);
        list.add(DECADES);
        list.add(CENTURIES);
        
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
    // nonStandardUnit
    //-----------------------------------------------------------------------
    public void nonStandardUnit() {
        PeriodUnit nonStandardUnit = new PeriodUnit() {
            @Override
            public Period getAlternatePeriod() {
                return Period.seconds(10);
            }
            @Override
            protected BigDecimal getEstimatedDurationForComparison() {
                return BigDecimal.TEN;
            }
            @Override
            public String getName() {
                return "Hello";
            }
            @Override
            public boolean isStandardDerived() {
                return true;
            }
        };
        assertEquals(nonStandardUnit.isStandard(), false);
        assertEquals(nonStandardUnit.isStandardDerived(), true);
        assertEquals(nonStandardUnit.getName(), "Hello");
        assertEquals(nonStandardUnit.getAlternatePeriod(), Period.seconds(10));
        assertEquals(nonStandardUnit.getEstimatedDurationForComparison(), BigDecimal.TEN);
    }

    //-----------------------------------------------------------------------
    // serialization
    //-----------------------------------------------------------------------
    public void serialization() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(CENTURIES);
        oos.close();
        
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertSame(ois.readObject(), CENTURIES);
    }

    @Test(expectedExceptions=InvalidObjectException.class)
    @SuppressWarnings("unchecked")
    public void serialization_bad() throws Exception {
        // simulate unknown unit by removing a known one from the map
        Class cls = PeriodUnit.Standard.class;
        Field field = cls.getDeclaredField("RESOLVE_MAP");
        field.setAccessible(true);
        Map map = (Map) field.get(null);
        map.remove("Centuries");
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(CENTURIES);
            oos.close();
            
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            ois.readObject();
        } finally {
            map.put("Centuries", CENTURIES);
        }
    }

}
