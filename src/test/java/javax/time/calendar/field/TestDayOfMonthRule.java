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
package javax.time.calendar.field;

import static org.testng.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.time.calendar.Calendrical;
import javax.time.calendar.LocalDate;
import javax.time.calendar.MonthDay;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.UnsupportedCalendarFieldException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test DayOfMonth.RULE.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestDayOfMonthRule {
    // TODO: Refactor to test ISOChronology.monthOfYear()

    @BeforeMethod
    public void setUp() {
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(DateTimeFieldRule.class.isAssignableFrom(DayOfMonth.rule().getClass()));
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
    // getValue(Calendrical)
    //-----------------------------------------------------------------------
    public void test_getValue_Calendrical_date() {
        LocalDate date = LocalDate.date(2007, 6, 20);
        Calendrical calendrical = date.toCalendrical();
        
        assertEquals(DayOfMonth.rule().getValue(calendrical), 20);
    }

    @Test(expectedExceptions=UnsupportedCalendarFieldException.class)
    public void test_getValue_Calendrical_noDate() {
        Calendrical calendrical = Calendrical.calendrical(null, null, null, null);
        DayOfMonth.rule().getValue(calendrical);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_getValue_Calendrical_null() {
        Calendrical calendrical = null;
        DayOfMonth.rule().getValue(calendrical);
    }

    public void test_getValue_Calendrical_monthDay() {
        MonthDay date = MonthDay.monthDay(6, 20);
        Calendrical calendrical = date.toCalendrical();
        
        assertEquals(DayOfMonth.rule().getValue(calendrical), 20);
    }

    public void test_getValue_Calendrical_day() {
        Calendrical calendrical = Calendrical.calendrical(DayOfMonth.rule(), 20);
        
        assertEquals(DayOfMonth.rule().getValue(calendrical), 20);
    }

//    //-----------------------------------------------------------------------
//    // extractValue(Calendrical)
//    //-----------------------------------------------------------------------
//    public void test_extractValue_Calendrical_date() {
//        LocalDate date = LocalDate.date(2007, 6, 20);
//        Calendrical dt = date.toCalendrical();
//        
//        assertEquals(DayOfMonth.rule().extractValue(dt), 20);
//    }
//
//    public void test_extractValue_Calendrical_noDate() {
//        Calendrical dt = Calendrical.calendrical(null, null, null, null);
//        
//        assertEquals(DayOfMonth.rule().extractValue(dt), null);
//    }
//
//    @Test(expectedExceptions=NullPointerException.class)
//    public void test_extractValue_Calendrical_null() {
//        Calendrical dt = null;
//        DayOfMonth.rule().extractValue(dt);
//    }
//
//    public void test_extractValue_Calendrical_monthDay() {
//        MonthDay date = MonthDay.monthDay(6, 20);
//        Calendrical dt = date.toCalendrical();
//        
//        assertEquals(DayOfMonth.rule().extractValue(dt), 20);
//    }
//
//    public void test_extractValue_Calendrical_day() {
//        Calendrical dt = Calendrical.calendrical(DayOfMonth.rule(), 20);
//        
//        assertEquals(DayOfMonth.rule().extractValue(dt), 20);
//    }

}
