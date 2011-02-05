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
package javax.time.calendar.i18n;

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

import javax.time.calendar.DateTimeField;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.i18n.CopticChronology;
import javax.time.i18n.CopticDate;

import org.testng.annotations.Test;

/**
 * Test CopticChronology.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestCopticChronology {

    @SuppressWarnings("rawtypes")
    public void test_constructor() throws Exception {
        for (Constructor constructor : CopticChronology.class.getDeclaredConstructors()) {
            assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        }
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        Object chronology = CopticChronology.INSTANCE;
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
        Class<CopticChronology> cls = CopticChronology.class;
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
    public void test_isLeapYear() throws Exception {
        for (int i = CopticDate.MIN_YEAR; i < CopticDate.MAX_YEAR; i++) {
            assertEquals(CopticChronology.isLeapYear(i), (i % 4) == 3);
        }
    }

    //-----------------------------------------------------------------------
    public void test_yearRule() throws Exception {
        DateTimeFieldRule rule = CopticChronology.yearRule();
        assertEquals(rule.getReifiedType(), DateTimeField.class);
        assertEquals(rule.getID(), "Coptic.Year");
        assertEquals(rule.getName(), "Year");
        assertEquals(rule.getMinimumValue(), CopticDate.MIN_YEAR);
        assertEquals(rule.getLargestMinimumValue(), CopticDate.MIN_YEAR);
        assertEquals(rule.getMaximumValue(), CopticDate.MAX_YEAR);
        assertEquals(rule.getSmallestMaximumValue(), CopticDate.MAX_YEAR);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), CopticChronology.periodYears());
        assertEquals(rule.getPeriodRange(), null);
        serialize(rule);
    }

    public void test_monthOfYearRule() throws Exception {
        DateTimeFieldRule rule = CopticChronology.monthOfYearRule();
        assertEquals(rule.getReifiedType(), DateTimeField.class);
        assertEquals(rule.getID(), "Coptic.MonthOfYear");
        assertEquals(rule.getName(), "MonthOfYear");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 13);
        assertEquals(rule.getSmallestMaximumValue(), 13);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), CopticChronology.periodMonths());
        assertEquals(rule.getPeriodRange(), CopticChronology.periodYears());
        serialize(rule);
    }

    public void test_dayOfMonthRule() throws Exception {
        DateTimeFieldRule rule = CopticChronology.dayOfMonthRule();
        assertEquals(rule.getReifiedType(), DateTimeField.class);
        assertEquals(rule.getID(), "Coptic.DayOfMonth");
        assertEquals(rule.getName(), "DayOfMonth");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 30);
        assertEquals(rule.getSmallestMaximumValue(), 5);
        assertEquals(rule.isFixedValueSet(), false);
        assertEquals(rule.getPeriodUnit(), CopticChronology.periodDays());
        assertEquals(rule.getPeriodRange(), CopticChronology.periodMonths());
        serialize(rule);
    }

    public void test_dayOfYearRule() throws Exception {
        DateTimeFieldRule rule = CopticChronology.dayOfYearRule();
        assertEquals(rule.getReifiedType(), DateTimeField.class);
        assertEquals(rule.getID(), "Coptic.DayOfYear");
        assertEquals(rule.getName(), "DayOfYear");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 366);
        assertEquals(rule.getSmallestMaximumValue(), 365);
        assertEquals(rule.isFixedValueSet(), false);
        assertEquals(rule.getPeriodUnit(), CopticChronology.periodDays());
        assertEquals(rule.getPeriodRange(), CopticChronology.periodYears());
        serialize(rule);
    }

    public void test_dayOfWeekRule() throws Exception {
        DateTimeFieldRule rule = CopticChronology.dayOfWeekRule();
        assertEquals(rule.getReifiedType(), DateTimeField.class);
        assertEquals(rule.getID(), "Coptic.DayOfWeek");
        assertEquals(rule.getName(), "DayOfWeek");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 7);
        assertEquals(rule.getSmallestMaximumValue(), 7);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), CopticChronology.periodDays());
        assertEquals(rule.getPeriodRange(), CopticChronology.periodWeeks());
        serialize(rule);
    }

    //-----------------------------------------------------------------------
    public void test_toString() throws Exception {
        assertEquals(CopticChronology.INSTANCE.toString(), "Coptic");
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
