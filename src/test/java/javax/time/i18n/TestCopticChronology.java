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
package javax.time.i18n;

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

import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.DateTimeRule;
import javax.time.calendrical.DateTimeRuleRange;
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
        DateTimeRule rule = CopticChronology.YEAR;
        assertEquals(rule.getType(), DateTimeField.class);
        assertEquals(rule.getName(), "CopticYear");
        assertEquals(rule.getValueRange(), DateTimeRuleRange.of(CopticDate.MIN_YEAR, CopticDate.MAX_YEAR));
        assertEquals(rule.getPeriodUnit(), CopticChronology.YEARS);
        assertEquals(rule.getPeriodRange(), null);
        serialize(rule);
    }

    public void test_monthOfYearRule() throws Exception {
        DateTimeRule rule = CopticChronology.MONTH_OF_YEAR;
        assertEquals(rule.getType(), DateTimeField.class);
        assertEquals(rule.getName(), "CopticMonthOfYear");
        assertEquals(rule.getValueRange(), DateTimeRuleRange.of(1, 13));
        assertEquals(rule.getPeriodUnit(), CopticChronology.MONTHS);
        assertEquals(rule.getPeriodRange(), CopticChronology.YEARS);
        serialize(rule);
    }

    public void test_dayOfMonthRule() throws Exception {
        DateTimeRule rule = CopticChronology.DAY_OF_MONTH;
        assertEquals(rule.getType(), DateTimeField.class);
        assertEquals(rule.getName(), "CopticDayOfMonth");
        assertEquals(rule.getValueRange(), DateTimeRuleRange.of(1, 5, 30));
        assertEquals(rule.getPeriodUnit(), CopticChronology.DAYS);
        assertEquals(rule.getPeriodRange(), CopticChronology.MONTHS);
        serialize(rule);
    }

    public void test_dayOfYearRule() throws Exception {
        DateTimeRule rule = CopticChronology.DAY_OF_YEAR;
        assertEquals(rule.getType(), DateTimeField.class);
        assertEquals(rule.getName(), "CopticDayOfYear");
        assertEquals(rule.getValueRange(), DateTimeRuleRange.of(1, 365, 366));
        assertEquals(rule.getPeriodUnit(), CopticChronology.DAYS);
        assertEquals(rule.getPeriodRange(), CopticChronology.YEARS);
        serialize(rule);
    }

    public void test_dayOfWeekRule() throws Exception {
        DateTimeRule rule = CopticChronology.DAY_OF_WEEK;
        assertEquals(rule.getType(), DateTimeField.class);
        assertEquals(rule.getName(), "CopticDayOfWeek");
        assertEquals(rule.getValueRange(), DateTimeRuleRange.of(1, 7));
        assertEquals(rule.getPeriodUnit(), CopticChronology.DAYS);
        assertEquals(rule.getPeriodRange(), CopticChronology.WEEKS);
        serialize(rule);
    }

    //-----------------------------------------------------------------------
    public void test_toString() throws Exception {
        assertEquals(CopticChronology.INSTANCE.toString(), "Coptic");
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

}
