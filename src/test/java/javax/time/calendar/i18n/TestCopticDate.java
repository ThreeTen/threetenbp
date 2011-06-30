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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.time.CalendricalException;
import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalRule;
import javax.time.calendar.CalendricalRuleException;
import javax.time.calendar.Chronology;
import javax.time.calendar.DateProvider;
import javax.time.calendar.DateTimeFields;
import javax.time.calendar.DayOfWeek;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.InvalidCalendarFieldException;
import javax.time.calendar.LocalDate;
import javax.time.calendar.LocalTime;
import javax.time.i18n.CopticChronology;
import javax.time.i18n.CopticDate;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test CopticDate.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestCopticDate {

    private CopticDate TEST_1234_7_15;

    @BeforeMethod
    public void setUp() {
        TEST_1234_7_15 = CopticDate.of(1234, 7, 15);
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        Object obj = TEST_1234_7_15;
        assertTrue(obj instanceof Calendrical);
        assertTrue(obj instanceof DateProvider);
        assertTrue(obj instanceof Serializable);
        assertTrue(obj instanceof Comparable<?>);
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(TEST_1234_7_15);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), TEST_1234_7_15);
    }

    public void test_immutable() {
        Class<CopticDate> cls = CopticDate.class;
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
    public void factory_of_ints() throws Exception {
        for (int y = 1; y <= 9999; y++) {
            for (int m = 1; m <= 12; m++) {
                for (int d = 1; d <= 30; d++) {
                    assertCopticDate(CopticDate.of(y, m, d), y, m, d);
                }
            }
            int m = 13;
            for (int d = 1; d < 30; d++) {
                if (d <= 5 || (d == 6 && isLeapYear(y))) {
                    assertCopticDate(CopticDate.of(y, m, d), y, m, d);
                } else {
                    assertInvalidCopticDate(y, m, d);
                }
            }
        }
    }

    public void factory_of_ints_invalidYear() throws Exception {
        assertInvalidCopticDate(CopticDate.MIN_YEAR - 1, 1, 1);
        assertInvalidCopticDate(CopticDate.MIN_YEAR - 1, 12, 30);
        assertInvalidCopticDate(CopticDate.MIN_YEAR - 1, 13, 5);
        assertInvalidCopticDate(CopticDate.MAX_YEAR + 1, 1, 1);
        assertInvalidCopticDate(CopticDate.MAX_YEAR + 1, 12, 30);
        assertInvalidCopticDate(CopticDate.MAX_YEAR + 1, 13, 5);
    }

    public void factory_of_ints_invalidMonth() throws Exception {
        for (int y = 1; y <= 9999; y++) {
            assertInvalidCopticDate(y, 0, 1);
            assertInvalidCopticDate(y, 14, 1);
        }
    }

    public void factory_of_ints_invalidDay() throws Exception {
        for (int y = 1; y <= 9999; y++) {
            for (int m = 1; m <= 12; m++) {
                assertInvalidCopticDate(y, m, 0);
                assertInvalidCopticDate(y, m, 31);
            }
        }
    }

    //-----------------------------------------------------------------------
    public void factory_of_Calendrical() throws Exception {
        assertEquals(CopticDate.of(TEST_1234_7_15), TEST_1234_7_15);
        assertCopticDate(CopticDate.of(TEST_1234_7_15), 1234, 7, 15);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void factory_of_Calendrical_noData() throws Exception {
        CopticDate.of(DateTimeFields.EMPTY);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_of_Calendrical_null() throws Exception {
        CopticDate.of((Calendrical) null);
    }

    //-----------------------------------------------------------------------
    public void test_get() throws Exception {
        assertEquals(TEST_1234_7_15.get(CopticChronology.YEAR).getValidIntValue(), TEST_1234_7_15.getYear());
        assertEquals(TEST_1234_7_15.get(CopticChronology.MONTH_OF_YEAR).getValidIntValue(), TEST_1234_7_15.getMonthOfYear());
        assertEquals(TEST_1234_7_15.get(CopticChronology.DAY_OF_MONTH).getValidIntValue(), TEST_1234_7_15.getDayOfMonth());
        assertEquals(TEST_1234_7_15.get(CopticChronology.DAY_OF_YEAR).getValidIntValue(), TEST_1234_7_15.getDayOfYear());
        assertEquals(TEST_1234_7_15.get(CopticChronology.DAY_OF_WEEK).getValidIntValue(), TEST_1234_7_15.getDayOfWeek().getValue());
        assertEquals(TEST_1234_7_15.get(Chronology.rule()), CopticChronology.INSTANCE);
    }

    public void test_get_unsupported() throws Exception {
        assertEquals(TEST_1234_7_15.get(LocalTime.rule()), null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_get_null() throws Exception {
        TEST_1234_7_15.get((CalendricalRule<?>) null);
    }

    //-----------------------------------------------------------------------
    // getDayOfWeek()
    //-----------------------------------------------------------------------
    public void test_getDayOfWeek() throws Exception {
        assertEquals(CopticDate.of(1662, 3, 3).getDayOfWeek(), DayOfWeek.MONDAY);
        assertEquals(CopticDate.of(1662, 3, 4).getDayOfWeek(), DayOfWeek.TUESDAY);
        assertEquals(CopticDate.of(1662, 3, 5).getDayOfWeek(), DayOfWeek.WEDNESDAY);
        assertEquals(CopticDate.of(1662, 3, 6).getDayOfWeek(), DayOfWeek.THURSDAY);
        assertEquals(CopticDate.of(1662, 3, 7).getDayOfWeek(), DayOfWeek.FRIDAY);
        assertEquals(CopticDate.of(1662, 3, 8).getDayOfWeek(), DayOfWeek.SATURDAY);
        assertEquals(CopticDate.of(1662, 3, 9).getDayOfWeek(), DayOfWeek.SUNDAY);
        assertEquals(CopticDate.of(1662, 3, 10).getDayOfWeek(), DayOfWeek.MONDAY);
    }

    public void test_getDayOfWeek_crossCheck() throws Exception {
        CopticDate test = CopticDate.of(1662, 3, 3);
        assertEquals(test.getDayOfWeek(), test.toLocalDate().getDayOfWeek());
    }

    //-----------------------------------------------------------------------
    // withYear()
    //-----------------------------------------------------------------------
    public void test_withYear_int() throws Exception {
        CopticDate test = TEST_1234_7_15.withYear(2008);
        assertEquals(test, CopticDate.of(2008, 7, 15));
    }

    public void test_withYear_int_invalid_tooSmall() throws Exception {
        try {
            TEST_1234_7_15.withYear(CopticDate.MIN_YEAR - 1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), CopticChronology.YEAR);
        }
    }

    public void test_withYear_int_invalid_tooBig() throws Exception {
        try {
            TEST_1234_7_15.withYear(CopticDate.MAX_YEAR + 1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), CopticChronology.YEAR);
        }
    }

    public void test_withYear_int_adjustDay_nonLeap() throws Exception {
        CopticDate test = CopticDate.of(7, 13, 6).withYear(8);
        CopticDate expected = CopticDate.of(8, 13, 5);
        assertEquals(test, expected);
    }

    public void test_withYear_int_adjustDay_leap() throws Exception {
        CopticDate test = CopticDate.of(11, 13, 6).withYear(7);
        CopticDate expected = CopticDate.of(7, 13, 6);
        assertEquals(test, expected);
    }

    //-----------------------------------------------------------------------
    // withMonthOfYear()
    //-----------------------------------------------------------------------
    public void test_withMonthOfYear_int() throws Exception {
        CopticDate test = TEST_1234_7_15.withMonthOfYear(1);
        assertEquals(test, CopticDate.of(1234, 1, 15));
    }

    public void test_withMonthOfYear_int_invalid_tooSmall() throws Exception {
        try {
            TEST_1234_7_15.withMonthOfYear(0);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), CopticChronology.MONTH_OF_YEAR);
        }
    }

    public void test_withMonthOfYear_int_invalid_tooBig() throws Exception {
        try {
            TEST_1234_7_15.withMonthOfYear(14);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), CopticChronology.MONTH_OF_YEAR);
        }
    }

    public void test_withMonthOfYear_int_adjustDay_nonLeap() throws Exception {
        CopticDate test = CopticDate.of(8, 9, 30).withMonthOfYear(13);
        CopticDate expected = CopticDate.of(8, 13, 5);
        assertEquals(test, expected);
    }

    public void test_withMonthOfYear_int_adjustDay_leap() throws Exception {
        CopticDate test = CopticDate.of(7, 9, 30).withMonthOfYear(13);
        CopticDate expected = CopticDate.of(7, 13, 6);
        assertEquals(test, expected);
    }

    //-----------------------------------------------------------------------
    // withDayOfMonth()
    //-----------------------------------------------------------------------
    public void test_withDayOfMonth() throws Exception {
        CopticDate test = TEST_1234_7_15.withDayOfMonth(1);
        assertEquals(test, CopticDate.of(1234, 7, 1));
    }

    public void test_withDayOfMonth_13Leap() throws Exception {
        CopticDate test = CopticDate.of(7, 13, 1).withDayOfMonth(6);
        assertEquals(test, CopticDate.of(7, 13, 6));
    }

    public void test_withDayOfMonth_invalid_tooSmall() throws Exception {
        CopticDate test = CopticDate.of(1234, 11, 30);
        try {
            test.withDayOfMonth(0);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), CopticChronology.DAY_OF_MONTH);
        }
    }

    public void test_withDayOfMonth_invalid_tooBig_normal() throws Exception {
        CopticDate test = CopticDate.of(1234, 11, 30);
        try {
            test.withDayOfMonth(31);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), CopticChronology.DAY_OF_MONTH);
        }
    }

    public void test_withDayOfMonth_invalid_tooBig_13NonLeap() throws Exception {
        CopticDate test = CopticDate.of(8, 13, 1);
        try {
            test.withDayOfMonth(6);
            fail();
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getRule(), CopticChronology.DAY_OF_MONTH);
        }
    }

    public void test_withDayOfMonth_invalid_tooBig_13Leap() throws Exception {
        CopticDate test = CopticDate.of(7, 13, 1);
        try {
            test.withDayOfMonth(7);
            fail();
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getRule(), CopticChronology.DAY_OF_MONTH);
        }
    }

    //-----------------------------------------------------------------------
    // withDayOfYear()
    //-----------------------------------------------------------------------
    public void test_withDayOfYear() throws Exception {
        CopticDate test = TEST_1234_7_15.withDayOfYear(1);
        assertEquals(test, CopticDate.of(1234, 1, 1));
    }

    public void test_withDayOfYear_31() throws Exception {
        CopticDate test = TEST_1234_7_15.withDayOfYear(31);
        assertEquals(test, CopticDate.of(1234, 2, 1));
    }

    public void test_withDayOfYear_leapDay() throws Exception {
        CopticDate test = CopticDate.of(7, 2, 3).withDayOfYear(366);
        assertEquals(test, CopticDate.of(7, 13, 6));
    }

    public void test_withDayOfYear_invalid_tooSmall() throws Exception {
        CopticDate test = CopticDate.of(1234, 11, 30);
        try {
            test.withDayOfYear(0);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), CopticChronology.DAY_OF_MONTH);
        }
    }

    public void test_withDayOfYear_invalid_tooBig_nonLeap() throws Exception {
        CopticDate test = CopticDate.of(8, 13, 1);
        try {
            test.withDayOfYear(366);
            fail();
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getRule(), CopticChronology.DAY_OF_MONTH);
        }
    }

    public void test_withDayOfYear_invalid_tooBig_leap() throws Exception {
        CopticDate test = CopticDate.of(7, 13, 1);
        try {
            test.withDayOfYear(367);
            fail();
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getRule(), CopticChronology.DAY_OF_MONTH);
        }
    }

    //-----------------------------------------------------------------------
    // plusYears()
    //-----------------------------------------------------------------------
    public void test_plusYears_int() throws Exception {
        doTest_plusYears(1234, 7, 15, 1, 1235, 7, 15);  // simple
        doTest_plusYears(1234, 7, 15, -1, 1233, 7, 15);  // simple negative
        doTest_plusYears(7, 13, 6, 4, 11, 13, 6);  // no round day leap to leap
        doTest_plusYears(7, 13, 6, 1, 8, 13, 5);  // round day leap to non-leap
    }

    private void doTest_plusYears(int y, int m, int d, int plus, int ey, int em, int ed) throws Exception {
        CopticDate test = CopticDate.of(y, m, d);
        CopticDate expected = CopticDate.of(ey, em, ed);
        assertEquals(test.plusYears(plus), expected);
    }

    public void test_plusYearsOverflow() throws Exception {
        test_plusYearsOverflow(CopticDate.MAX_YEAR, 1, 1, 1);  // max + 1
        test_plusYearsOverflow(CopticDate.MAX_YEAR, 1, 1, Integer.MAX_VALUE);  // max + max
        test_plusYearsOverflow(CopticDate.MAX_YEAR, 1, 1, Integer.MIN_VALUE);  // max + min
        
        test_plusYearsOverflow(CopticDate.MIN_YEAR, 1, 1, -1);  // min - 1
        test_plusYearsOverflow(CopticDate.MIN_YEAR, 1, 1, Integer.MAX_VALUE);  // min + max
        test_plusYearsOverflow(CopticDate.MIN_YEAR, 1, 1, Integer.MIN_VALUE);  // min + min
    }

    private void test_plusYearsOverflow(int y, int m, int d, int plus) throws Exception {
        CopticDate test = CopticDate.of(y, m, d);
        try {
            test.plusYears(plus);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), CopticChronology.YEAR);
        }
    }

    //-----------------------------------------------------------------------
    // plusMonths()
    //-----------------------------------------------------------------------
    public void test_plusMonths() throws Exception {
        doTest_plusMonths(1234, 7, 15, 1, 1234, 8, 15);  // simple
        doTest_plusMonths(1234, 7, 15, 7, 1235, 1, 15);  // across year
        doTest_plusMonths(1234, 7, 15, -1, 1234, 6, 15);  // simple negative
        doTest_plusMonths(1234, 7, 15, -8, 1233, 12, 15);  // negative across year
        
        doTest_plusMonths(8, 12, 15, 1, 8, 13, 5);  // round day, from month 12 non leap
        doTest_plusMonths(7, 12, 15, 1, 7, 13, 6);  // round day, from month 12 leap
        doTest_plusMonths(7, 13, 6, 13, 8, 13, 5);  // round day, from month 13 plus one year
    }

    private void doTest_plusMonths(int y, int m, int d, int plus, int ey, int em, int ed) throws Exception {
        CopticDate test = CopticDate.of(y, m, d);
        CopticDate expected = CopticDate.of(ey, em, ed);
        assertEquals(test.plusMonths(plus), expected);
    }

    public void test_plusMonthsOverflow() throws Exception {
        doTest_plusMonthsOverflow(CopticDate.MAX_YEAR, 13, 1, 1);  // max + 1
        doTest_plusMonthsOverflow(CopticDate.MAX_YEAR, 13, 1, Integer.MAX_VALUE);  // max + max
        doTest_plusMonthsOverflow(CopticDate.MAX_YEAR, 13, 1, Integer.MIN_VALUE);  // max + min
        
        doTest_plusMonthsOverflow(CopticDate.MIN_YEAR, 1, 1, -1);  // min - 1
        doTest_plusMonthsOverflow(CopticDate.MIN_YEAR, 1, 1, Integer.MAX_VALUE);  // min + max
        doTest_plusMonthsOverflow(CopticDate.MIN_YEAR, 1, 1, Integer.MIN_VALUE);  // min + min
    }

    private void doTest_plusMonthsOverflow(int y, int m, int d, int plus) throws Exception {
        CopticDate test = CopticDate.of(y, m, d);
        try {
            test.plusMonths(plus);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), CopticChronology.YEAR);
        }
    }

    //-----------------------------------------------------------------------
    // plusWeeks()
    //-----------------------------------------------------------------------
    public void test_plusWeeks() throws Exception {
        doTest_plusWeeks(1234, 7, 15, 1, 1234, 7, 22);  // simple
        doTest_plusWeeks(1234, 13, 1, 1, 1235, 1, 3);  // across year
        doTest_plusWeeks(1234, 7, 15, -1, 1234, 7, 8);  // simple negative
        doTest_plusWeeks(1234, 1, 1, -1, 1233, 12, 29);  // negative across year
    }

    private void doTest_plusWeeks(int y, int m, int d, int plus, int ey, int em, int ed) throws Exception {
        CopticDate test = CopticDate.of(y, m, d);
        CopticDate expected = CopticDate.of(ey, em, ed);
        assertEquals(test.plusWeeks(plus), expected);
    }

    //-----------------------------------------------------------------------
    // plusDays()
    //-----------------------------------------------------------------------
    public void test_plusDays() throws Exception {
        doTest_plusDays(1234, 7, 15, 1, 1234, 7, 16);  // simple
        doTest_plusDays(1234, 13, 1, 5, 1235, 1, 1);  // across year
        doTest_plusDays(1234, 7, 15, -1, 1234, 7, 14);  // simple negative
        doTest_plusDays(1234, 1, 1, -1, 1233, 13, 5);  // negative across year
    }

    private void doTest_plusDays(int y, int m, int d, int plus, int ey, int em, int ed) throws Exception {
        CopticDate test = CopticDate.of(y, m, d);
        CopticDate expected = CopticDate.of(ey, em, ed);
        assertEquals(test.plusDays(plus), expected);
    }

    public void test_plusDaysOverflow() throws Exception {
        doTest_plusDaysOverflow(CopticDate.MAX_YEAR, 13, 6, 1);  // max + 1
        doTest_plusDaysOverflow(CopticDate.MAX_YEAR, 13, 6, Integer.MAX_VALUE);  // max + max
        doTest_plusDaysOverflow(CopticDate.MAX_YEAR, 13, 6, Integer.MIN_VALUE);  // max + min
        
        doTest_plusDaysOverflow(CopticDate.MIN_YEAR, 1, 1, -1);  // min - 1
        doTest_plusDaysOverflow(CopticDate.MIN_YEAR, 1, 1, Integer.MAX_VALUE);  // min + max
        doTest_plusDaysOverflow(CopticDate.MIN_YEAR, 1, 1, Integer.MIN_VALUE);  // min + min
    }

    private void doTest_plusDaysOverflow(int y, int m, int d, int plus) throws Exception {
        CopticDate test = CopticDate.of(y, m, d);
        try {
            test.plusDays(plus);
            fail();
        } catch (CalendricalRuleException ex) {
            assertEquals(ex.getRule(), CopticChronology.YEAR);
        }
    }

    //-----------------------------------------------------------------------
    // toLocalDate()
    //-----------------------------------------------------------------------
    public void test_toLocalDate() throws Exception {
        assertEquals(CopticDate.of(1,1,1).toLocalDate(), LocalDate.of(284, 8, 29));
        assertEquals(CopticDate.of(1662, 3,3).toLocalDate(), LocalDate.of(1945, 11, 12));
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_comparisons() throws Exception {
        doTest_comparisons_CopticDate(
            CopticDate.of(1, 1, 1),
            CopticDate.of(1, 1, 2),
            CopticDate.of(1, 1, 30),
            CopticDate.of(1, 2, 1),
            CopticDate.of(1, 2, 30),
            CopticDate.of(1, 12, 30),
            CopticDate.of(1, 13, 1),
            CopticDate.of(1, 13, 5),
            CopticDate.of(2, 1, 1),
            CopticDate.of(2, 12, 30),
            CopticDate.of(2, 13, 5),
            CopticDate.of(3, 1, 1),
            CopticDate.of(3, 12, 30),
            CopticDate.of(3, 13, 6),
            CopticDate.of(CopticDate.MAX_YEAR, 1, 1),
            CopticDate.of(CopticDate.MAX_YEAR, 12, 30),
            CopticDate.of(CopticDate.MAX_YEAR, 13, 5)
        );
    }

    void doTest_comparisons_CopticDate(CopticDate... CopticDates) {
        for (int i = 0; i < CopticDates.length; i++) {
            CopticDate a = CopticDates[i];
            for (int j = 0; j < CopticDates.length; j++) {
                CopticDate b = CopticDates[j];
                if (i < j) {
                    assertTrue(a.compareTo(b) < 0, a + " <=> " + b);
                    assertEquals(a.isBefore(b), true, a + " <=> " + b);
                    assertEquals(a.isAfter(b), false, a + " <=> " + b);
                    assertEquals(a.equals(b), false, a + " <=> " + b);
                } else if (i > j) {
                    assertTrue(a.compareTo(b) > 0, a + " <=> " + b);
                    assertEquals(a.isBefore(b), false, a + " <=> " + b);
                    assertEquals(a.isAfter(b), true, a + " <=> " + b);
                    assertEquals(a.equals(b), false, a + " <=> " + b);
                } else {
                    assertEquals(a.compareTo(b), 0, a + " <=> " + b);
                    assertEquals(a.isBefore(b), false, a + " <=> " + b);
                    assertEquals(a.isAfter(b), false, a + " <=> " + b);
                    assertEquals(a.equals(b), true, a + " <=> " + b);
                }
            }
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_compareTo_ObjectNull() throws Exception {
        TEST_1234_7_15.compareTo(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isBefore_ObjectNull() throws Exception {
        TEST_1234_7_15.isBefore(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isAfter_ObjectNull() throws Exception {
        TEST_1234_7_15.isAfter(null);
    }

    @Test(expectedExceptions=ClassCastException.class)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void test_compareToNonCopticDate() throws Exception {
       Comparable c = TEST_1234_7_15;
       c.compareTo(new Object());
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    public void test_equals_equal() throws Exception {
        CopticDate a = CopticDate.of(1, 1, 1);
        CopticDate b = CopticDate.of(1, 1, 1);
        assertEquals(a.equals(b), true);
        assertEquals(b.equals(a), true);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals_notEqualDay() throws Exception {
        CopticDate a = CopticDate.of(1, 1, 1);
        CopticDate b = CopticDate.of(1, 1, 2);
        assertEquals(a.equals(b), false);
        assertEquals(b.equals(a), false);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals_notEqualMonth() throws Exception {
        CopticDate a = CopticDate.of(1, 1, 1);
        CopticDate b = CopticDate.of(1, 2, 1);
        assertEquals(a.equals(b), false);
        assertEquals(b.equals(a), false);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals_notEqualYear() throws Exception {
        CopticDate a = CopticDate.of(1, 1, 1);
        CopticDate b = CopticDate.of(2, 1, 1);
        assertEquals(a.equals(b), false);
        assertEquals(b.equals(a), false);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals_itself_true() throws Exception {
        assertEquals(TEST_1234_7_15.equals(TEST_1234_7_15), true);
    }

    public void test_equals_string_false() throws Exception {
        assertEquals(TEST_1234_7_15.equals("1234-07-15"), false);
    }

    public void test_hashCode() throws Exception {
        CopticDate a = CopticDate.of(1, 1, 1);
        CopticDate b = CopticDate.of(1, 1, 1);
        assertEquals(a.hashCode(), a.hashCode());
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(b.hashCode(), b.hashCode());
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleToString")
    Object[][] provider_sampleToString() {
        return new Object[][] {
            {1, 1, 1, "0001-01-01 (Coptic)"},
            {12, 1, 1, "0012-01-01 (Coptic)"},
            {123, 1, 1, "0123-01-01 (Coptic)"},
            {1234, 1, 1, "1234-01-01 (Coptic)"},
            {1, 1, 2, "0001-01-02 (Coptic)"},
            {1, 2, 1, "0001-02-01 (Coptic)"},
            {3, 13, 6, "0003-13-06 (Coptic)"},
            {9999, 13, 5, "9999-13-05 (Coptic)"},
        };
    }

    @Test(dataProvider="sampleToString")
    public void test_toString(int y, int m, int d, String expected) {
        CopticDate test = CopticDate.of(y, m, d);
        String str = test.toString();
        assertEquals(str, expected);
    }

    private void assertCopticDate(CopticDate test, int year, int month, int day) throws Exception {
        assertEquals(test.getYear(), year);
        assertEquals(test.getMonthOfYear(), month);
        assertEquals(test.getDayOfMonth(), day);
        assertEquals(test.getDayOfYear(), (month - 1) * 30 + day);
        assertEquals(test.isLeapYear(), isLeapYear(year));
    }

    private void assertInvalidCopticDate(int year, int month, int day) throws Exception {
        try {
            CopticDate.of(year, month, day);
            fail();
        } catch (InvalidCalendarFieldException ex) {
            if (year < 1 || year > 9999 || month < 1 || month > 13 || day < 1 || day > 30) {
                throw ex;  // should be IllegalCalendarFieldValueException
            }
            if (month == 13 && (day > 6 || (day == 6 && isLeapYear(year) == false))) {
                assertEquals(ex.getRule(), CopticChronology.DAY_OF_MONTH);
            } else {
                throw ex;  // valid date
            }
        } catch (IllegalCalendarFieldValueException ex) {
            if (year < 1 || year > 9999) {
                assertEquals(ex.getRule(), CopticChronology.YEAR);
            } else if (month < 1 || month > 13) {
                assertEquals(ex.getRule(), CopticChronology.MONTH_OF_YEAR);
            } else if (day < 1 || day > 30) {
                assertEquals(ex.getRule(), CopticChronology.DAY_OF_MONTH);
            }
        }
    }

    private boolean isLeapYear(int year) {
        return (year % 4) == 3;
    }

}
