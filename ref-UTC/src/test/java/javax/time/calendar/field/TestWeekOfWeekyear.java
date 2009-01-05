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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalProvider;
import javax.time.calendar.DateMatcher;
import javax.time.calendar.DateProvider;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.LocalDate;
import javax.time.calendar.MockDateProviderReturnsNull;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test WeekOfWeekyear.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestWeekOfWeekyear {

    private static final DateTimeFieldRule RULE = ISOChronology.weekOfWeekyearRule();
    private static final int MAX_LENGTH = 53;

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(CalendricalProvider.class.isAssignableFrom(WeekOfWeekyear.class));
        assertTrue(Serializable.class.isAssignableFrom(WeekOfWeekyear.class));
        assertTrue(Comparable.class.isAssignableFrom(WeekOfWeekyear.class));
        assertTrue(DateMatcher.class.isAssignableFrom(WeekOfWeekyear.class));
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        WeekOfWeekyear test = WeekOfWeekyear.weekOfWeekyear(1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertSame(ois.readObject(), test);
    }

    public void test_immutable() {
        Class<WeekOfWeekyear> cls = WeekOfWeekyear.class;
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
        assertEquals(WeekOfWeekyear.rule(), RULE);
    }

    //-----------------------------------------------------------------------
    public void test_factory_int_singleton() {
        for (int i = 1; i <= MAX_LENGTH; i++) {
            WeekOfWeekyear test = WeekOfWeekyear.weekOfWeekyear(i);
            assertEquals(test.getValue(), i);
            assertEquals(WeekOfWeekyear.weekOfWeekyear(i), test);
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_weekOfWeekyearTooLow() {
        WeekOfWeekyear.weekOfWeekyear(0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_weekOfWeekyearTooHigh() {
        WeekOfWeekyear.weekOfWeekyear(54);
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
    public void test_factory_DateProvider(int startYear, int startMonth, int startDay, int endYear, int endMonth, int endDay) {
        LocalDate date = LocalDate.date(startYear, startMonth, startDay);
        long offset = LocalDate.date(endYear, endMonth, endDay).toModifiedJulianDays() - date.toModifiedJulianDays();
        int week = 0;

        for (long l = 0; l < offset; l++) {
            if (l % 7 == 0) {
                week++;
            }

            assertEquals(WeekOfWeekyear.weekOfWeekyear(date).getValue(), week);
            date = date.plusDays(1);
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_nullDateProvider() {
        WeekOfWeekyear.weekOfWeekyear((DateProvider) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_badDateProvider() {
        WeekOfWeekyear.weekOfWeekyear(new MockDateProviderReturnsNull());
    }

    //-----------------------------------------------------------------------
    // matchesDate(LocalDate)
    //-----------------------------------------------------------------------

    @Test(dataProvider="dateProvider")
    public void test_matchesDate(int startYear, int startMonth, int startDay, int endYear, int endMonth, int endDay) {
        LocalDate date = LocalDate.date(startYear, startMonth, startDay);
        long offset = LocalDate.date(endYear, endMonth, endDay).toModifiedJulianDays() - date.toModifiedJulianDays();
        int week = 0;

        for (long l = 0; l < offset; l++) {
            if (l % 7 == 0) {
                week++;
            }

            assertTrue(WeekOfWeekyear.weekOfWeekyear(week).matchesDate(date));
            assertFalse(WeekOfWeekyear.weekOfWeekyear(week).matchesDate(date.plusDays(7)));
            date = date.plusDays(1);
        }
    }

    //-----------------------------------------------------------------------
    // isValid()
    //-----------------------------------------------------------------------
    public void test_isValid_52weeks() {
        for (int i = 2000; i < 2004; i++) {
            Weekyear weekyear = Weekyear.weekyear(i);
            for (int w = 1; w < 53; w++) {
                assertTrue(WeekOfWeekyear.weekOfWeekyear(w).isValid(weekyear));
            }

            assertFalse(WeekOfWeekyear.weekOfWeekyear(53).isValid(weekyear));
        }
    }

    public void test_isValid_53weeks() {
        Weekyear weekyear = Weekyear.weekyear(2004);
        for (int w = 1; w < 54; w++) {
            assertTrue(WeekOfWeekyear.weekOfWeekyear(w).isValid(weekyear));
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isValid_null() {
        WeekOfWeekyear test = WeekOfWeekyear.weekOfWeekyear(1);
        test.isValid(null);
    }

    //-----------------------------------------------------------------------
    // toCalendrical()
    //-----------------------------------------------------------------------
    public void test_toCalendrical() {
        for (int i = 1; i <= MAX_LENGTH; i++) {
            WeekOfWeekyear test = WeekOfWeekyear.weekOfWeekyear(i);
            assertEquals(test.toCalendrical(), new Calendrical(RULE, i));
        }
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo() {
        for (int i = 1; i <= MAX_LENGTH; i++) {
            WeekOfWeekyear a = WeekOfWeekyear.weekOfWeekyear(i);
            for (int j = 1; j <= MAX_LENGTH; j++) {
                WeekOfWeekyear b = WeekOfWeekyear.weekOfWeekyear(j);
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
        WeekOfWeekyear doy = null;
        WeekOfWeekyear test = WeekOfWeekyear.weekOfWeekyear(1);
        test.compareTo(doy);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    public void test_equals() {
        for (int i = 1; i <= MAX_LENGTH; i++) {
            WeekOfWeekyear a = WeekOfWeekyear.weekOfWeekyear(i);
            for (int j = 1; j <= MAX_LENGTH; j++) {
                WeekOfWeekyear b = WeekOfWeekyear.weekOfWeekyear(j);
                assertTrue(a.equals(a));
                assertEquals(a.equals(b), i == j);
                assertEquals(a.hashCode() == b.hashCode(), i == j);
            }
        }
    }

    public void test_equals_nullWeekOfWeekyear() {
        WeekOfWeekyear doy = null;
        WeekOfWeekyear test = WeekOfWeekyear.weekOfWeekyear(1);
        assertEquals(test.equals(doy), false);
    }

    public void test_equals_incorrectType() {
        WeekOfWeekyear test = WeekOfWeekyear.weekOfWeekyear(1);
        assertEquals(test.equals("Incorrect type"), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        for (int i = 1; i <= MAX_LENGTH; i++) {
            WeekOfWeekyear a = WeekOfWeekyear.weekOfWeekyear(i);
            assertEquals(a.toString(), "WeekOfWeekyear=" + i);
        }
    }
}
