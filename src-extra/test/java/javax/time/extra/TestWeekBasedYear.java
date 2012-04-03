/*
 * Copyright (c) 2008-2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.extra;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;

import javax.time.CalendricalException;
import javax.time.LocalDate;
import javax.time.calendrical.Calendrical;
import javax.time.calendrical.DateTimeFields;
import javax.time.calendrical.DateTimeRule;
import javax.time.calendrical.ISODateTimeRule;
import javax.time.calendrical.IllegalCalendarFieldValueException;
import javax.time.extended.Year;
import javax.time.extra.WeekBasedYear;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test WeekBasedYear.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestWeekBasedYear {
    private static final DateTimeRule RULE = ISODateTimeRule.WEEK_BASED_YEAR;

    @BeforeMethod
    public void setUp() {
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Calendrical.class.isAssignableFrom(WeekBasedYear.class));
        assertTrue(Serializable.class.isAssignableFrom(WeekBasedYear.class));
        assertTrue(Comparable.class.isAssignableFrom(WeekBasedYear.class));
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        WeekBasedYear test = WeekBasedYear.weekBasedYear(1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), test);
    }

    public void test_immutable() {
        Class<WeekBasedYear> cls = WeekBasedYear.class;
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().contains("$") == false) {
                if (Modifier.isStatic(field.getModifiers())) {
                    assertTrue(Modifier.isFinal(field.getModifiers()), "Field:" + field.getName());
                } else {
                    assertTrue(Modifier.isPrivate(field.getModifiers()), "Field:" + field.getName());
                    assertTrue(Modifier.isFinal(field.getModifiers()), "Field:" + field.getName());
                }
            }
        }
    }

    //-----------------------------------------------------------------------
    public void test_rule() {
        assertEquals(WeekBasedYear.rule(), RULE);
    }

    //-----------------------------------------------------------------------
    public void test_factory_int() {
        for (int i = Year.MIN_YEAR; i <= Year.MIN_YEAR + 100; i++) {
            WeekBasedYear test = WeekBasedYear.weekBasedYear(i);
            assertEquals(test.getValue(), i);
            assertEquals(WeekBasedYear.weekBasedYear(i), test);
        }

        for (int i = Year.MAX_YEAR; i >= Year.MAX_YEAR - 100; i--) {
            WeekBasedYear test = WeekBasedYear.weekBasedYear(i);
            assertEquals(test.getValue(), i);
            assertEquals(WeekBasedYear.weekBasedYear(i), test);
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_yearTooLow() {
        WeekBasedYear.weekBasedYear(Year.MIN_YEAR - 1);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="weekyearLocalDate")
    Iterator<Object[]> weekyearLocalDate() {
        return new Iterator<Object[]>() {
            private final Integer[][] data = new Integer[][] {
                {2000, 2000, 1, 3, 2000, 12, 31},
                {2001, 2001, 1, 1, 2001, 12, 30},
                {2002, 2001, 12, 31, 2002, 12, 29},
                {2003, 2002, 12, 30, 2003, 12, 28},
                {2004, 2003, 12, 29, 2005, 1, 2},
                {2005, 2005, 1, 3, 2006, 1, 1},
                {2006, 2006, 1, 2, 2006, 12, 31},
                {2007, 2007, 1, 1, 2007, 12, 30},
                {2008, 2007, 12, 31, 2008, 12, 28},
                {2009, 2008, 12, 29, 2010, 1, 3},
                {2010, 2010, 1, 4, 2011, 1, 2},
            };

            private final int offset = 7;
            private int i = -1;
            private int weekyear;
            private LocalDate start;
            private LocalDate end;
            private LocalDate current;
            private boolean hasNext = true;

            public boolean hasNext() {
                return hasNext;
            }

            public Object[] next() {
                if (i == -1 || lastDate()) {
                    i++;
                    final Integer[] line = data[i];
                    weekyear = line[0];
                    start = LocalDate.of(line[1], line[2], line[3]);
                    end = LocalDate.of(line[4], line[5], line[6]);
                    current = start.minusDays(offset + 1);
                }

                if (current.equals(start.plusDays(offset))) {
                    current = end.minusDays(offset);
                }

                current = current.plusDays(1);

                if (i == data.length - 1 && lastDate()) {
                    hasNext = false;
                }

                return new Object[] {current, current.isBefore(start) ? weekyear - 1 : current.isAfter(end) ? weekyear + 1 : weekyear};
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

            private boolean lastDate() {
                return end.plusDays(offset).equals(current);
            }
        };
    }

    @Test(dataProvider="weekyearLocalDate")
    public void test_factory_Calendrical(LocalDate date, int expectedWeekyear) {
        WeekBasedYear test = WeekBasedYear.weekBasedYear(date);
        assertEquals(test.getValue(), expectedWeekyear);
    }

    public void test_factory_Calendrical_simple_weekyear() {
        LocalDate date = LocalDate.of(2008, 1, 4);
        LocalDate end = LocalDate.of(2008, 12, 28);

        while (!date.isAfter(end)) {
            assertEquals(WeekBasedYear.weekBasedYear(date).getValue(), 2008);
            date = date.plusDays(1);
        }
    }


    @Test(expectedExceptions=CalendricalException.class)
    public void test_factory_Calendrical_noData() {
        WeekBasedYear.weekBasedYear(DateTimeFields.EMPTY);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_nullCalendrical() {
        WeekBasedYear.weekBasedYear((Calendrical) null);
    }

    //-----------------------------------------------------------------------
    // lengthInWeeks()/getLastWeekOfWeekyear()
    //-----------------------------------------------------------------------
    @DataProvider(name="yearWeek")
    Object[][] yearWeek() {
        return new Object[][] {
            {2000, 52},
            {2001, 52},
            {2002, 52},
            {2003, 52},
            {2004, 53},
        };
    }

    @Test(dataProvider="yearWeek")
    public void test_lengthInWeeks(int weekyear, int lengthInWeeks) {
        WeekBasedYear test = WeekBasedYear.weekBasedYear(weekyear);
        assertEquals(test.lengthInWeeks(), lengthInWeeks);
    }

    public void test_lengthInWeeks_minYear() {
        WeekBasedYear test = WeekBasedYear.weekBasedYear(WeekBasedYear.MIN_YEAR);
        //TODO check result
        test.lengthInWeeks();
    }

    public void test_lengthInWeeks_maxYear() {
        WeekBasedYear test = WeekBasedYear.weekBasedYear(WeekBasedYear.MAX_YEAR);
        //TODO check result
        test.lengthInWeeks();
    }

    @Test(dataProvider="yearWeek")
    public void test_getLastWeekOfWeekyear(int weekyear, int lastWeekNumber) {
        WeekBasedYear test = WeekBasedYear.weekBasedYear(weekyear);
        assertEquals(test.getLastWeekOfWeekyear().getValue(), lastWeekNumber);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo() {
        for (int i = -100; i <= 100; i++) {
            WeekBasedYear a = WeekBasedYear.weekBasedYear(i);
            for (int j = -100; j <= 100; j++) {
                WeekBasedYear b = WeekBasedYear.weekBasedYear(j);
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
    public void test_compareTo_nullWeekyear() {
        WeekBasedYear doy = null;
        WeekBasedYear test = WeekBasedYear.weekBasedYear(1);
        test.compareTo(doy);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    public void test_equals() {
        for (int i = -100; i <= 100; i++) {
            WeekBasedYear a = WeekBasedYear.weekBasedYear(i);
            assertTrue(a.equals(a));
            for (int j = -100; j <= 100; j++) {
                WeekBasedYear b = WeekBasedYear.weekBasedYear(j);
                assertEquals(a.equals(b), i == j);
                assertEquals(a.hashCode() == b.hashCode(), i == j);
            }
        }
    }

    public void test_equals_nullWeekyear() {
        WeekBasedYear doy = null;
        WeekBasedYear test = WeekBasedYear.weekBasedYear(1);
        assertEquals(test.equals(doy), false);
    }

    public void test_equals_incorrectType() {
        WeekBasedYear test = WeekBasedYear.weekBasedYear(1);
        assertEquals(test.equals("Incorrect type"), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        for (int i = -100; i <= 100; i++) {
            WeekBasedYear a = WeekBasedYear.weekBasedYear(i);
            assertEquals(a.toString(), "WeekBasedYear=" + i);
        }
    }

}
