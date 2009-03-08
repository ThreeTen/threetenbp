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

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import javax.time.calendar.field.DayOfMonth;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.Year;
import javax.time.calendar.format.CalendricalParseException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test MonthDay.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestMonthDay {

    private static final DateTimeFieldRule RULE_YEAR = ISOChronology.yearRule();
    private static final DateTimeFieldRule RULE_MONTH = ISOChronology.monthOfYearRule();
    private static final DateTimeFieldRule RULE_DOM = ISOChronology.dayOfMonthRule();
    private MonthDay TEST_07_15;

    @BeforeMethod
    public void setUp() {
        TEST_07_15 = MonthDay.monthDay(7, 15);
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(TEST_07_15 instanceof CalendricalProvider);
        assertTrue(TEST_07_15 instanceof Serializable);
        assertTrue(TEST_07_15 instanceof Comparable);
        assertTrue(TEST_07_15 instanceof DateAdjuster);
        assertTrue(TEST_07_15 instanceof DateMatcher);
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(TEST_07_15);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), TEST_07_15);
    }

    public void test_immutable() {
        Class<MonthDay> cls = MonthDay.class;
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            assertTrue(Modifier.isPrivate(field.getModifiers()));
            assertTrue(Modifier.isFinal(field.getModifiers()));
        }
    }

    //-----------------------------------------------------------------------
    void check(MonthDay test, int m, int d) {
        assertEquals(test.getMonthOfYear().getValue(), m);
        assertEquals(test.getDayOfMonth(), d);
    }
    
    public void factory_objects() {
        assertEquals(TEST_07_15, MonthDay.monthDay(MonthOfYear.JULY, DayOfMonth.dayOfMonth(15)));
    }

    public void factory_objects_leapDay() {
        MonthDay test = MonthDay.monthDay(MonthOfYear.FEBRUARY, DayOfMonth.dayOfMonth(29));
        check(test, 2, 29);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_objects_nullMonth() {
        MonthDay.monthDay(null, DayOfMonth.dayOfMonth(15));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_objects_nullDay() {
        MonthDay.monthDay(MonthOfYear.JULY, null);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void factory_objects_invalidDayOfMonth() {
        try {
            MonthDay.monthDay(MonthOfYear.APRIL, DayOfMonth.dayOfMonth(31));
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getFieldRule(), RULE_DOM);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    public void factory_intsMonth() {
        assertEquals(TEST_07_15, MonthDay.monthDay(MonthOfYear.JULY, 15));
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_intsMonth_dayTooLow() {
        try {
            MonthDay.monthDay(MonthOfYear.JANUARY, 0);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getFieldRule(), RULE_DOM);
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_intsMonth_dayTooHigh() {
        try {
            MonthDay.monthDay(MonthOfYear.JANUARY, 32);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getFieldRule(), RULE_DOM);
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_intsMonth_nullMonth() {
        MonthDay.monthDay(null, 15);
    }

    //-----------------------------------------------------------------------
    public void factory_ints() {
        check(TEST_07_15, 7, 15);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_ints_dayTooLow() {
        try {
            MonthDay.monthDay(1, 0);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getFieldRule(), RULE_DOM);
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_ints_dayTooHigh() {
        try {
            MonthDay.monthDay(1, 32);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getFieldRule(), RULE_DOM);
            throw ex;
        }
    }


    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_ints_monthTooLow() {
        try {
            MonthDay.monthDay(0, 1);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getFieldRule(), RULE_MONTH);
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_ints_monthTooHigh() {
        try {
            MonthDay.monthDay(13, 1);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getFieldRule(), RULE_MONTH);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    public void factory_DateProvider() {
        DateProvider provider = LocalDate.date(2007, 7, 15);
        assertEquals(MonthDay.monthDay(provider), TEST_07_15);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_DateProvider_null() {
        MonthDay.monthDay(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_DateProvider_badProvider() {
        MonthDay.monthDay(new MockDateProviderReturnsNull());
    }

    //-----------------------------------------------------------------------
    public void factory_CalendricalProvider() {
        CalendricalProvider provider = new Calendrical(RULE_MONTH, 7, RULE_DOM, 15);
        assertEquals(MonthDay.monthDay(provider), TEST_07_15);
    }

    public void factory_CalendricalProvider_otherFieldsIgnored() {
        Calendrical provider = LocalDate.date(2007, 7, 15).toCalendrical();
        assertEquals(MonthDay.monthDay(provider), TEST_07_15);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_CalendricalProvider_null() {
        MonthDay.monthDay(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_CalendricalProvider_badProvider() {
        MonthDay.monthDay(new MockCalendricalProviderReturnsNull());
    }

    //-----------------------------------------------------------------------
    public void test_getChronology() {
        assertEquals(ISOChronology.INSTANCE, TEST_07_15.getChronology());
    }

    //-----------------------------------------------------------------------
    // get*()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleDates")
    Object[][] provider_sampleDates() {
        return new Object[][] {
            {1, 1},
            {1, 31},
            {2, 1},
            {2, 28},
            {2, 29},
            {7, 4},
            {7, 5},
        };
    }

    @Test(dataProvider="sampleDates")
    public void test_get(int m, int d) {
        MonthDay a = MonthDay.monthDay(m, d);
        assertEquals(a.getMonthOfYear(), MonthOfYear.monthOfYear(m));
        assertEquals(a.getDayOfMonth(), d);
    }

    //-----------------------------------------------------------------------
    // with(MonthOfYear)
    //-----------------------------------------------------------------------
    public void test_with_MonthOfYear() {
        assertEquals(MonthDay.monthDay(6, 30).with(MonthOfYear.JANUARY), MonthDay.monthDay(1, 30));
    }

    public void test_with_MonthOfYear_adjustToValid() {
        assertEquals(MonthDay.monthDay(7, 31).with(MonthOfYear.JUNE), MonthDay.monthDay(6, 30));
    }

    public void test_with_MonthOfYear_adjustToValidFeb() {
        assertEquals(MonthDay.monthDay(7, 31).with(MonthOfYear.FEBRUARY), MonthDay.monthDay(2, 29));
    }

    public void test_with_MonthOfYear_noChange() {
        MonthDay test = MonthDay.monthDay(6, 30);
        assertSame(test.with(MonthOfYear.JUNE), test);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_with_MonthOfYear_null() {
        MonthDay.monthDay(6, 30).with((MonthOfYear) null);
    }

    //-----------------------------------------------------------------------
    // with(DayOfMonth)
    //-----------------------------------------------------------------------
    public void test_with_DayOfMonth() {
        assertEquals(MonthDay.monthDay(6, 30).with(DayOfMonth.dayOfMonth(1)), MonthDay.monthDay(6, 1));
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_with_DayOfMonth_invalid() {
        try {
            MonthDay.monthDay(6, 30).with(DayOfMonth.dayOfMonth(31));
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getFieldRule(), RULE_DOM);
            throw ex;
        }
    }

    public void test_with_DayOfMonth_adjustToValidFeb() {
        assertEquals(MonthDay.monthDay(2, 1).with(DayOfMonth.dayOfMonth(29)), MonthDay.monthDay(2, 29));
    }

    public void test_with_DayOfMonth_noChange() {
        MonthDay test = MonthDay.monthDay(6, 30);
        assertSame(test.with(DayOfMonth.dayOfMonth(30)), test);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_with_DayOfMonth_null() {
        MonthDay.monthDay(6, 30).with((DayOfMonth) null);
    }

    //-----------------------------------------------------------------------
    // withMonthOfYear()
    //-----------------------------------------------------------------------
    public void test_withMonthOfYear() {
        assertEquals(MonthDay.monthDay(6, 30).withMonthOfYear(1), MonthDay.monthDay(1, 30));
    }

    public void test_withMonthOfYear_adjustToValid() {
        assertEquals(MonthDay.monthDay(7, 31).withMonthOfYear(6), MonthDay.monthDay(6, 30));
    }

    public void test_withMonthOfYear_adjustToValidFeb() {
        assertEquals(MonthDay.monthDay(7, 31).withMonthOfYear(2), MonthDay.monthDay(2, 29));
    }

    public void test_withMonthOfYear_int_noChange() {
        MonthDay test = MonthDay.monthDay(6, 30);
        assertSame(test.withMonthOfYear(6), test);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withMonthOfYear_tooLow() {
        try {
            MonthDay.monthDay(6, 30).withMonthOfYear(0);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getFieldRule(), RULE_MONTH);
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withMonthOfYear_tooHigh() {
        try {
            MonthDay.monthDay(6, 30).withMonthOfYear(13);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getFieldRule(), RULE_MONTH);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // withDayOfMonth()
    //-----------------------------------------------------------------------
    public void test_withDayOfMonth() {
        assertEquals(MonthDay.monthDay(6, 30).withDayOfMonth(1), MonthDay.monthDay(6, 1));
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_withDayOfMonth_invalid() {
        try {
            MonthDay.monthDay(6, 30).withDayOfMonth(31);
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getFieldRule(), RULE_DOM);
            throw ex;
        }
    }

    public void test_withDayOfMonth_adjustToValidFeb() {
        assertEquals(MonthDay.monthDay(2, 1).withDayOfMonth(29), MonthDay.monthDay(2, 29));
    }

    public void test_withDayOfMonth_noChange() {
        MonthDay test = MonthDay.monthDay(6, 30);
        assertSame(test.withDayOfMonth(30), test);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withDayOfMonth_tooLow() {
        try {
            MonthDay.monthDay(6, 30).withDayOfMonth(0);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getFieldRule(), RULE_DOM);
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withDayOfMonth_tooHigh() {
        try {
            MonthDay.monthDay(6, 30).withDayOfMonth(32);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getFieldRule(), RULE_DOM);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // rollMonthOfYear()
    //-----------------------------------------------------------------------
    public void test_rollMonthOfYear() {
        MonthDay base = MonthDay.monthDay(7, 31);
        assertSame(base.rollMonthOfYear(0), base);
        assertEquals(base.rollMonthOfYear(1), MonthDay.monthDay(8, 31));
        assertEquals(base.rollMonthOfYear(2), MonthDay.monthDay(9, 30));
        assertEquals(base.rollMonthOfYear(3), MonthDay.monthDay(10, 31));
        assertEquals(base.rollMonthOfYear(4), MonthDay.monthDay(11, 30));
        assertEquals(base.rollMonthOfYear(5), MonthDay.monthDay(12, 31));
        assertEquals(base.rollMonthOfYear(6), MonthDay.monthDay(1, 31));
        assertEquals(base.rollMonthOfYear(7), MonthDay.monthDay(2, 29));
        assertEquals(base.rollMonthOfYear(8), MonthDay.monthDay(3, 31));
        assertEquals(base.rollMonthOfYear(9), MonthDay.monthDay(4, 30));
        assertEquals(base.rollMonthOfYear(10), MonthDay.monthDay(5, 31));
        assertEquals(base.rollMonthOfYear(11), MonthDay.monthDay(6, 30));
        assertEquals(base.rollMonthOfYear(12), MonthDay.monthDay(7, 31));
        
        assertEquals(base.rollMonthOfYear(-1), MonthDay.monthDay(6, 30));
        assertEquals(base.rollMonthOfYear(-2), MonthDay.monthDay(5, 31));
        assertEquals(base.rollMonthOfYear(-3), MonthDay.monthDay(4, 30));
        assertEquals(base.rollMonthOfYear(-4), MonthDay.monthDay(3, 31));
        assertEquals(base.rollMonthOfYear(-5), MonthDay.monthDay(2, 29));
        assertEquals(base.rollMonthOfYear(-6), MonthDay.monthDay(1, 31));
        assertEquals(base.rollMonthOfYear(-7), MonthDay.monthDay(12, 31));
        assertEquals(base.rollMonthOfYear(-8), MonthDay.monthDay(11, 30));
        assertEquals(base.rollMonthOfYear(-9), MonthDay.monthDay(10, 31));
        assertEquals(base.rollMonthOfYear(-10), MonthDay.monthDay(9, 30));
        assertEquals(base.rollMonthOfYear(-11), MonthDay.monthDay(8, 31));
        assertEquals(base.rollMonthOfYear(-12), MonthDay.monthDay(7, 31));
    }

    //-----------------------------------------------------------------------
    // rollDayOfMonth()
    //-----------------------------------------------------------------------
    public void test_rollDayOfMonth_july() {
        MonthDay base = MonthDay.monthDay(7, 31);
        assertEquals(base.rollDayOfMonth(-2), MonthDay.monthDay(7, 29));
        assertEquals(base.rollDayOfMonth(-1), MonthDay.monthDay(7, 30));
        assertSame(base.rollDayOfMonth(0), base);
        assertEquals(base.rollDayOfMonth(1), MonthDay.monthDay(7, 1));
        assertEquals(base.rollDayOfMonth(2), MonthDay.monthDay(7, 2));
    }

    public void test_rollDayOfMonth_feb() {
        MonthDay base = MonthDay.monthDay(2, 29);
        assertEquals(base.rollDayOfMonth(-2), MonthDay.monthDay(2, 27));
        assertEquals(base.rollDayOfMonth(-1), MonthDay.monthDay(2, 28));
        assertSame(base.rollDayOfMonth(0), base);
        assertEquals(base.rollDayOfMonth(1), MonthDay.monthDay(2, 1));
        assertEquals(base.rollDayOfMonth(2), MonthDay.monthDay(2, 2));
    }

    //-----------------------------------------------------------------------
    // adjustDate()
    //-----------------------------------------------------------------------
    public void test_adjustDate() {
        MonthDay test = MonthDay.monthDay(6, 30);
        LocalDate date = LocalDate.date(2007, 1, 1);
        assertEquals(test.adjustDate(date), LocalDate.date(2007, 6, 30));
    }

    public void test_adjustDate_same() {
        MonthDay test = MonthDay.monthDay(6, 30);
        LocalDate date = LocalDate.date(2007, 6, 30);
        assertSame(test.adjustDate(date), date);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_adjustDate_invalid() {
        MonthDay test = MonthDay.monthDay(2, 29);
        LocalDate date = LocalDate.date(2007, 6, 30);
        try {
            test.adjustDate(date);
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getFieldRule(), RULE_DOM);
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustDate_null() {
        TEST_07_15.adjustDate((LocalDate) null);
    }

    //-----------------------------------------------------------------------
    public void test_adjustDate_DateResolver() {
        MonthDay test = MonthDay.monthDay(6, 30);
        LocalDate date = LocalDate.date(2007, 1, 1);
        assertEquals(test.adjustDate(date, DateResolvers.nextValid()), LocalDate.date(2007, 6, 30));
    }

    public void test_adjustDate_DateResolver_same() {
        MonthDay test = MonthDay.monthDay(6, 30);
        LocalDate date = LocalDate.date(2007, 6, 30);
        assertSame(test.adjustDate(date, DateResolvers.nextValid()), date);
    }

    public void test_adjustDate_DateResolver_resolve() {
        MonthDay test = MonthDay.monthDay(2, 29);
        LocalDate date = LocalDate.date(2007, 1, 1);
        assertEquals(test.adjustDate(date, DateResolvers.nextValid()), LocalDate.date(2007, 3, 1));
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_adjustDate_DateResolver_invalid() {
        MonthDay test = MonthDay.monthDay(2, 29);
        LocalDate date = LocalDate.date(2007, 6, 30);
        try {
            test.adjustDate(date, DateResolvers.strict());
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getFieldRule(), RULE_DOM);
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustDate_DateResolver_nullDate() {
        TEST_07_15.adjustDate((LocalDate) null, DateResolvers.nextValid());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustDate_DateResolver_nullResolver() {
        MonthDay test = MonthDay.monthDay(2, 29);
        LocalDate date = LocalDate.date(2008, 2, 29);  // same date, but resolver should still NPE
        test.adjustDate(date, (DateResolver) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustDate_DateResolver_badResolver() {
        MonthDay test = MonthDay.monthDay(2, 29);
        LocalDate date = LocalDate.date(2007, 6, 30);
        test.adjustDate(date, new MockDateResolverReturnsNull());
    }

    //-----------------------------------------------------------------------
    // matchesDate()
    //-----------------------------------------------------------------------
    public void test_matchesDate() {
        assertEquals(MonthDay.monthDay(1, 1).matchesDate(LocalDate.date(2007, 1, 1)), true);
        assertEquals(MonthDay.monthDay(1, 1).matchesDate(LocalDate.date(2008, 1, 1)), true);
        
        assertEquals(MonthDay.monthDay(2, 1).matchesDate(LocalDate.date(2007, 1, 1)), false);
        assertEquals(MonthDay.monthDay(1, 2).matchesDate(LocalDate.date(2008, 1, 1)), false);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_matchesDate_null() {
        TEST_07_15.matchesDate(null);
    }

    //-----------------------------------------------------------------------
    // atYear(Year)
    //-----------------------------------------------------------------------
    public void test_atYear() {
        MonthDay test = MonthDay.monthDay(6, 30);
        assertEquals(test.atYear(Year.isoYear(2008)), LocalDate.date(2008, 6, 30));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_atYear_nullYear() {
        MonthDay test = MonthDay.monthDay(6, 30);
        test.atYear((Year) null);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_atYear_notLeapYear() {
        MonthDay test = MonthDay.monthDay(2, 29);
        try {
            test.atYear(Year.isoYear(2005));
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getFieldRule(), ISOChronology.dayOfMonthRule());
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // atYear(int)
    //-----------------------------------------------------------------------
    public void test_atYear_int() {
        MonthDay test = MonthDay.monthDay(6, 30);
        assertEquals(test.atYear(2008), LocalDate.date(2008, 6, 30));
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atYear_int_invalidYear() {
        MonthDay test = MonthDay.monthDay(6, 30);
        test.atYear(Integer.MIN_VALUE);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_atYear_int_notLeapYear() {
        MonthDay test = MonthDay.monthDay(2, 29);
        try {
            test.atYear(2005);
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getFieldRule(), ISOChronology.dayOfMonthRule());
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // toCalendrical()
    //-----------------------------------------------------------------------
    public void test_toCalendrical() {
        MonthDay test = MonthDay.monthDay(6, 30);
        assertEquals(test.toCalendrical(), new Calendrical(RULE_MONTH, 6, RULE_DOM, 30));
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_comparisons() {
        doTest_comparisons_MonthDay(
            MonthDay.monthDay(1, 1),
            MonthDay.monthDay(1, 31),
            MonthDay.monthDay(2, 1),
            MonthDay.monthDay(2, 29),
            MonthDay.monthDay(3, 1),
            MonthDay.monthDay(12, 31)
        );
    }

    void doTest_comparisons_MonthDay(MonthDay... localDates) {
        for (int i = 0; i < localDates.length; i++) {
            MonthDay a = localDates[i];
            for (int j = 0; j < localDates.length; j++) {
                MonthDay b = localDates[j];
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
    public void test_compareTo_ObjectNull() {
        TEST_07_15.compareTo(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isBefore_ObjectNull() {
        TEST_07_15.isBefore(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isAfter_ObjectNull() {
        TEST_07_15.isAfter(null);
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    public void test_equals() {
        MonthDay a = MonthDay.monthDay(1, 1);
        MonthDay b = MonthDay.monthDay(1, 1);
        MonthDay c = MonthDay.monthDay(2, 1);
        MonthDay d = MonthDay.monthDay(1, 2);
        
        assertEquals(a.equals(a), true);
        assertEquals(a.equals(b), true);
        assertEquals(a.equals(c), false);
        assertEquals(a.equals(d), false);
        
        assertEquals(b.equals(a), true);
        assertEquals(b.equals(b), true);
        assertEquals(b.equals(c), false);
        assertEquals(b.equals(d), false);
        
        assertEquals(c.equals(a), false);
        assertEquals(c.equals(b), false);
        assertEquals(c.equals(c), true);
        assertEquals(c.equals(d), false);
        
        assertEquals(d.equals(a), false);
        assertEquals(d.equals(b), false);
        assertEquals(d.equals(c), false);
        assertEquals(d.equals(d), true);
    }

    public void test_equals_itself_true() {
        assertEquals(TEST_07_15.equals(TEST_07_15), true);
    }

    public void test_equals_string_false() {
        assertEquals(TEST_07_15.equals("2007-07-15"), false);
    }

    public void test_equals_null_false() {
        assertEquals(TEST_07_15.equals(null), false);
    }

    //-----------------------------------------------------------------------
    // hashCode()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_hashCode(int m, int d) {
        MonthDay a = MonthDay.monthDay(m, d);
        assertEquals(a.hashCode(), a.hashCode());
        MonthDay b = MonthDay.monthDay(m, d);
        assertEquals(a.hashCode(), b.hashCode());
    }

    public void test_hashCode_unique() {
        Year leapYear = Year.isoYear(2008);
        Set<Integer> uniques = new HashSet<Integer>(366);
        for (int i = 1; i <= 12; i++) {
            for (int j = 1; j <= 31; j++) {
                if (DayOfMonth.dayOfMonth(j).isValid(leapYear, MonthOfYear.monthOfYear(i))) {
                    assertTrue(uniques.add(MonthDay.monthDay(i, j).hashCode()));
                }
            }
        }
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleToString")
    Object[][] provider_sampleToString() {
        return new Object[][] {
            {7, 5, "--07-05"},
            {12, 31, "--12-31"},
            {1, 2, "--01-02"},
        };
    }

    @Test(dataProvider="sampleToString")
    public void test_toString(int m, int d, String expected) {
        MonthDay test = MonthDay.monthDay(m, d);
        String str = test.toString();
        assertEquals(str, expected);
    }
    
    //-----------------------------------------------------------------------
    // parse()
    //-----------------------------------------------------------------------
    @DataProvider(name="goodParseData")
    Object[][] provider_goodParseData() {
    	return new Object[][] {
    			{"--01-01", MonthDay.monthDay(1, 1)},
    			{"--01-31", MonthDay.monthDay(1, 31)},
    			{"--02-01", MonthDay.monthDay(2, 1)},
    			{"--02-29", MonthDay.monthDay(2, 29)},
    			{"--03-01", MonthDay.monthDay(3, 1)},
    			{"--03-31", MonthDay.monthDay(3, 31)},
    			{"--04-01", MonthDay.monthDay(4, 1)},
    			{"--04-30", MonthDay.monthDay(4, 30)},
    			{"--05-01", MonthDay.monthDay(5, 1)},
    			{"--05-31", MonthDay.monthDay(5, 31)},
    			{"--06-01", MonthDay.monthDay(6, 1)},
    			{"--06-30", MonthDay.monthDay(6, 30)},
    			{"--07-01", MonthDay.monthDay(7, 1)},
    			{"--07-31", MonthDay.monthDay(7, 31)},
    			{"--08-01", MonthDay.monthDay(8, 1)},
    			{"--08-31", MonthDay.monthDay(8, 31)},
    			{"--09-01", MonthDay.monthDay(9, 1)},
    			{"--09-30", MonthDay.monthDay(9, 30)},
    			{"--10-01", MonthDay.monthDay(10, 1)},
    			{"--10-31", MonthDay.monthDay(10, 31)},
    			{"--11-01", MonthDay.monthDay(11, 1)},
    			{"--11-30", MonthDay.monthDay(11, 30)},
    			{"--12-01", MonthDay.monthDay(12, 1)},
    			{"--12-31", MonthDay.monthDay(12, 31)},
    	};
    }

    @Test(dataProvider="goodParseData")
    public void factory_parse_success(String text, MonthDay expected) {
    	MonthDay monthDay = MonthDay.parse(text);
    	assertEquals(monthDay, expected);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="badParseData")
    Object[][] provider_badParseData() {
    	return new Object[][] {
    			{"", 0},
    			{"-00", 0},
    			{"--FEB-23", 2},
    			{"--01-0", 5},
    			{"--01-3A", 5},
    	};
    }

    @Test(dataProvider="badParseData", expectedExceptions=CalendricalParseException.class)
    public void factory_parse_fail(String text, int pos) {
    	try {
    		MonthDay.parse(text);
    		fail(String.format("Parse should have failed for %s at position %d", text, pos));
    	}
    	catch (CalendricalParseException ex) {
    		assertEquals(ex.getParsedString(), text);
    		assertEquals(ex.getErrorIndex(), pos);
    		throw ex;
    	}
    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_parse_illegalValue() {
        MonthDay.parse("--06-32");
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void factory_parse_invalidValue() {
        MonthDay.parse("--06-31");
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_parse_nullText() {
    	MonthDay.parse(null);
    }

}
