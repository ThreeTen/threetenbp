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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.time.calendar.field.DayOfMonth;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test DayOfMonth.RULE.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestISODayOfMonthRule {

    private static final DateTimeFieldRule<Integer> RULE = DayOfMonth.rule();

    @BeforeMethod
    public void setUp() {
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(DateTimeFieldRule.class.isAssignableFrom(RULE.getClass()));
    }

    public void test_immutable() {
        Class<DayOfMonth> cls = DayOfMonth.class;
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                assertTrue(Modifier.isFinal(field.getModifiers()), "Field:" + field.getName());
            } else {
                assertTrue(Modifier.isPrivate(field.getModifiers()), "Field:" + field.getName());
                assertTrue(Modifier.isFinal(field.getModifiers()), "Field:" + field.getName());
            }
        }
    }

    //-----------------------------------------------------------------------
    public void test_singleton() {
        assertSame(DayOfMonth.rule(), DayOfMonth.rule());
    }

    //-----------------------------------------------------------------------
    // deriveValue(Calendrical)
    //-----------------------------------------------------------------------
    public void test_deriveValue_Calendrical_date() {
        Calendrical cal = LocalDate.date(2007, 6, 20);
        assertEquals(RULE.deriveValue(cal), (Integer) 20);
    }

    public void test_deriveValue_Calendrical_dateTime() {
        Calendrical cal = LocalDateTime.dateTime(2007, 6, 20, 12, 30);
        assertEquals(RULE.deriveValue(cal), (Integer) 20);
    }

    public void test_deriveValue_Calendrical_monthDay() {
        Calendrical cal = MonthDay.monthDay(6, 20);
        assertEquals(RULE.deriveValue(cal), null);  // derivation is in Calendrical
        assertEquals(cal.get(RULE), (Integer) 20);  // derivation is in Calendrical
    }

    public void test_deriveValue_Calendrical_dateTimeFields() {
        Calendrical cal = DateTimeFields.fields(RULE, 20);
        assertEquals(RULE.deriveValue(cal), null);  // derivation is in Calendrical
        assertEquals(cal.get(RULE), (Integer) 20);  // derivation is in Calendrical
    }

//    // TODO: This is testing more than we need to
//    //-----------------------------------------------------------------------
//    // getValueQuiet(Calendrical)
//    //-----------------------------------------------------------------------
//    public void test_getValueQuiet_Calendrical_date() {
//        Calendrical cal = LocalDate.date(2007, 6, 20);
//        assertEquals(RULE.getValueQuiet(cal), (Integer) 20);
//    }
//
//    public void test_getValueQuiet_Calendrical_dateTime() {
//        Calendrical cal = LocalDateTime.dateTime(2007, 6, 20, 12, 30);
//        assertEquals(RULE.getValueQuiet(cal), (Integer) 20);
//    }
//
//    public void test_getValueQuiet_Calendrical_monthDay() {
//        Calendrical cal = MonthDay.monthDay(6, 20);
//        assertEquals(RULE.getValueQuiet(cal), (Integer) 20);
//    }
//
//    public void test_getValueQuiet_Calendrical_dateTimeFields() {
//        Calendrical cal = DateTimeFields.fields(RULE, 20);
//        assertEquals(RULE.getValueQuiet(cal), (Integer) 20);
//    }
//
//    public void test_getValueQuiet_Calendrical_noInfo() {
//        Calendrical cal = DateTimeFields.fields();
//        assertEquals(RULE.getValueQuiet(cal), null);
//    }
//
//    @Test(expectedExceptions=NullPointerException.class)
//    public void test_getValueQuiet_Calendrical_null() {
//        Calendrical calendrical = null;
//        RULE.getValueQuiet(calendrical);
//    }
//
//    //-----------------------------------------------------------------------
//    // getValue(Calendrical)
//    //-----------------------------------------------------------------------
//    public void test_getValue_Calendrical_date() {
//        Calendrical cal = LocalDate.date(2007, 6, 20);
//        assertEquals(RULE.getValue(cal), (Integer) 20);
//    }
//
//    public void test_getValue_Calendrical_dateTime() {
//        Calendrical cal = LocalDateTime.dateTime(2007, 6, 20, 12, 30);
//        assertEquals(RULE.getValue(cal), (Integer) 20);
//    }
//
//    public void test_getValue_Calendrical_monthDay() {
//        Calendrical cal = MonthDay.monthDay(6, 20);
//        assertEquals(RULE.getValue(cal), (Integer) 20);
//    }
//
//    public void test_getValue_Calendrical_dateTimeFields() {
//        Calendrical cal = DateTimeFields.fields(RULE, 20);
//        assertEquals(RULE.getValue(cal), (Integer) 20);
//    }
//
//    @Test(expectedExceptions=UnsupportedRuleException.class)
//    public void test_getValue_Calendrical_noInfo() {
//        Calendrical cal = DateTimeFields.fields();
//        RULE.getValue(cal);
//    }
//
//    @Test(expectedExceptions=NullPointerException.class)
//    public void test_getValue_Calendrical_null() {
//        Calendrical calendrical = null;
//        RULE.getValue(calendrical);
//    }
//
//    //-----------------------------------------------------------------------
//    // getValueQuiet(Calendrical)
//    //-----------------------------------------------------------------------
//    public void test_getIntQuiet_Calendrical_date() {
//        Calendrical cal = LocalDate.date(2007, 6, 20);
//        assertEquals(RULE.getIntQuiet(cal), (Integer) 20);
//    }
//
//    public void test_getIntQuiet_Calendrical_dateTime() {
//        Calendrical cal = LocalDateTime.dateTime(2007, 6, 20, 12, 30);
//        assertEquals(RULE.getIntQuiet(cal), (Integer) 20);
//    }
//
//    public void test_getIntQuiet_Calendrical_monthDay() {
//        Calendrical cal = MonthDay.monthDay(6, 20);
//        assertEquals(RULE.getIntQuiet(cal), (Integer) 20);
//    }
//
//    public void test_getIntQuiet_Calendrical_dateTimeFields() {
//        Calendrical cal = DateTimeFields.fields(RULE, 20);
//        assertEquals(RULE.getIntQuiet(cal), (Integer) 20);
//    }
//
//    public void test_getIntQuiet_Calendrical_noInfo() {
//        Calendrical cal = DateTimeFields.fields();
//        assertEquals(RULE.getIntQuiet(cal), null);
//    }
//
//    @Test(expectedExceptions=NullPointerException.class)
//    public void test_getIntQuiet_Calendrical_null() {
//        Calendrical calendrical = null;
//        RULE.getIntQuiet(calendrical);
//    }
//
//    //-----------------------------------------------------------------------
//    // getInt(Calendrical)
//    //-----------------------------------------------------------------------
//    public void test_getInt_Calendrical_date() {
//        Calendrical cal = LocalDate.date(2007, 6, 20);
//        assertEquals(RULE.getInt(cal), 20);
//    }
//
//    public void test_getInt_Calendrical_dateTime() {
//        Calendrical cal = LocalDateTime.dateTime(2007, 6, 20, 12, 30);
//        assertEquals(RULE.getInt(cal), 20);
//    }
//
//    public void test_getInt_Calendrical_monthDay() {
//        Calendrical cal = MonthDay.monthDay(6, 20);
//        assertEquals(RULE.getInt(cal), 20);
//    }
//
//    public void test_getInt_Calendrical_dateTimeFields() {
//        Calendrical cal = DateTimeFields.fields(RULE, 20);
//        assertEquals(RULE.getInt(cal), 20);
//    }
//
//    @Test(expectedExceptions=UnsupportedRuleException.class)
//    public void test_getInt_Calendrical_noInfo() {
//        Calendrical cal = DateTimeFields.fields();
//        RULE.getInt(cal);
//    }
//
//    @Test(expectedExceptions=NullPointerException.class)
//    public void test_getInt_Calendrical_null() {
//        Calendrical calendrical = null;
//        RULE.getInt(calendrical);
//    }

}
