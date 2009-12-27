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
package javax.time.calendar.field;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalMatcher;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.LocalDate;
import javax.time.calendar.LocalTime;
import javax.time.calendar.UnsupportedRuleException;
import javax.time.calendar.format.MockSimpleCalendrical;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test WeekOfWeekBasedYear.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestWeekOfWeekBasedYear {

    private static final DateTimeFieldRule<Integer> RULE = ISOChronology.weekOfWeekBasedYearRule();
    private static final int MAX_LENGTH = 53;

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Calendrical.class.isAssignableFrom(WeekOfWeekBasedYear.class));
        assertTrue(Serializable.class.isAssignableFrom(WeekOfWeekBasedYear.class));
        assertTrue(Comparable.class.isAssignableFrom(WeekOfWeekBasedYear.class));
        assertTrue(CalendricalMatcher.class.isAssignableFrom(WeekOfWeekBasedYear.class));
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        WeekOfWeekBasedYear test = WeekOfWeekBasedYear.weekOfWeekBasedYear(1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertSame(ois.readObject(), test);
    }

    public void test_immutable() {
        Class<WeekOfWeekBasedYear> cls = WeekOfWeekBasedYear.class;
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
    public void test_rule() {
        assertEquals(WeekOfWeekBasedYear.rule(), RULE);
    }

    //-----------------------------------------------------------------------
    public void test_factory_int_singleton() {
        for (int i = 1; i <= MAX_LENGTH; i++) {
            WeekOfWeekBasedYear test = WeekOfWeekBasedYear.weekOfWeekBasedYear(i);
            assertEquals(test.getValue(), i);
            assertEquals(WeekOfWeekBasedYear.weekOfWeekBasedYear(i), test);
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_weekOfWeekyearTooLow() {
        WeekOfWeekBasedYear.weekOfWeekBasedYear(0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_weekOfWeekyearTooHigh() {
        WeekOfWeekBasedYear.weekOfWeekBasedYear(54);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="dateProvider")
    Object[][] dateProvider() {
        return new Object[][] {
            {2002, 12, 30, 2003, 12, 28},
            {2003, 12, 29, 2005, 1, 2},
        };
    }

    @Test(dataProvider="dateProvider")
    public void test_factory_Calendrical(int startYear, int startMonth, int startDay, int endYear, int endMonth, int endDay) {
        LocalDate date = LocalDate.date(startYear, startMonth, startDay);
        long offset = LocalDate.date(endYear, endMonth, endDay).toModifiedJulianDays() - date.toModifiedJulianDays();
        int week = 0;

        for (long l = 0; l < offset; l++) {
            if (l % 7 == 0) {
                week++;
            }

            assertEquals(WeekOfWeekBasedYear.weekOfWeekBasedYear(date).getValue(), week);
            date = date.plusDays(1);
        }
    }

    @Test(expectedExceptions=UnsupportedRuleException.class)
    public void test_factory_Calendrical_noData() {
        WeekOfWeekBasedYear.weekOfWeekBasedYear(new MockSimpleCalendrical());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_nullCalendrical() {
        WeekOfWeekBasedYear.weekOfWeekBasedYear((Calendrical) null);
    }

    //-----------------------------------------------------------------------
    // matchesCalendrical(Calendrical)
    //-----------------------------------------------------------------------
    @Test(dataProvider="dateProvider")
    public void test_matchesCalendrical(int startYear, int startMonth, int startDay, int endYear, int endMonth, int endDay) {
        LocalDate date = LocalDate.date(startYear, startMonth, startDay);
        long offset = LocalDate.date(endYear, endMonth, endDay).toModifiedJulianDays() - date.toModifiedJulianDays();
        int week = 0;

        for (long l = 0; l < offset; l++) {
            if (l % 7 == 0) {
                week++;
            }

            assertTrue(WeekOfWeekBasedYear.weekOfWeekBasedYear(week).matchesCalendrical(date));
            assertFalse(WeekOfWeekBasedYear.weekOfWeekBasedYear(week).matchesCalendrical(date.plusDays(7)));
            date = date.plusDays(1);
        }
    }

    public void test_matchesCalendrical_noData() {
        assertEquals(WeekOfWeekBasedYear.weekOfWeekBasedYear(12).matchesCalendrical(LocalTime.time(12, 30)), false);
    }

    //-----------------------------------------------------------------------
    // isValid()
    //-----------------------------------------------------------------------
    public void test_isValid_52weeks() {
        for (int i = 2000; i < 2004; i++) {
            WeekBasedYear weekyear = WeekBasedYear.weekBasedYear(i);
            for (int w = 1; w < 53; w++) {
                assertTrue(WeekOfWeekBasedYear.weekOfWeekBasedYear(w).isValid(weekyear));
            }

            assertFalse(WeekOfWeekBasedYear.weekOfWeekBasedYear(53).isValid(weekyear));
        }
    }

    public void test_isValid_53weeks() {
        WeekBasedYear weekyear = WeekBasedYear.weekBasedYear(2004);
        for (int w = 1; w < 54; w++) {
            assertTrue(WeekOfWeekBasedYear.weekOfWeekBasedYear(w).isValid(weekyear));
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isValid_null() {
        WeekOfWeekBasedYear test = WeekOfWeekBasedYear.weekOfWeekBasedYear(1);
        test.isValid(null);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo() {
        for (int i = 1; i <= MAX_LENGTH; i++) {
            WeekOfWeekBasedYear a = WeekOfWeekBasedYear.weekOfWeekBasedYear(i);
            for (int j = 1; j <= MAX_LENGTH; j++) {
                WeekOfWeekBasedYear b = WeekOfWeekBasedYear.weekOfWeekBasedYear(j);
                if (i < j) {
                    assertEquals(a.compareTo(b), -1);
                    assertEquals(b.compareTo(a), 1);
                    assertTrue(a.isBefore(b));
                    assertFalse(a.isAfter(b));
                } else if (i > j) {
                    assertEquals(a.compareTo(b), 1);
                    assertEquals(b.compareTo(a), -1);
                    assertTrue(a.isAfter(b));
                    assertFalse(a.isBefore(b));
                } else {
                    assertEquals(a.compareTo(b), 0);
                    assertEquals(b.compareTo(a), 0);
                    assertFalse(a.isAfter(b));
                    assertFalse(a.isBefore(b));
                }
            }
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_compareTo_nullWeekOfWeekyear() {
        WeekOfWeekBasedYear doy = null;
        WeekOfWeekBasedYear test = WeekOfWeekBasedYear.weekOfWeekBasedYear(1);
        test.compareTo(doy);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    public void test_equals() {
        for (int i = 1; i <= MAX_LENGTH; i++) {
            WeekOfWeekBasedYear a = WeekOfWeekBasedYear.weekOfWeekBasedYear(i);
            for (int j = 1; j <= MAX_LENGTH; j++) {
                WeekOfWeekBasedYear b = WeekOfWeekBasedYear.weekOfWeekBasedYear(j);
                assertTrue(a.equals(a));
                assertEquals(a.equals(b), i == j);
                assertEquals(a.hashCode() == b.hashCode(), i == j);
            }
        }
    }

    public void test_equals_nullWeekOfWeekyear() {
        WeekOfWeekBasedYear doy = null;
        WeekOfWeekBasedYear test = WeekOfWeekBasedYear.weekOfWeekBasedYear(1);
        assertEquals(test.equals(doy), false);
    }

    public void test_equals_incorrectType() {
        WeekOfWeekBasedYear test = WeekOfWeekBasedYear.weekOfWeekBasedYear(1);
        assertEquals(test.equals("Incorrect type"), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        for (int i = 1; i <= MAX_LENGTH; i++) {
            WeekOfWeekBasedYear a = WeekOfWeekBasedYear.weekOfWeekBasedYear(i);
            assertEquals(a.toString(), "WeekOfWeekBasedYear=" + i);
        }
    }
}
